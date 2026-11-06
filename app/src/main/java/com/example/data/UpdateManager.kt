package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

data class GitHubAsset(
    val id: Long,
    val name: String,
    val size: Long,
    val downloadUrl: String
)

data class GitHubRelease(
    val id: Long,
    val tagName: String,
    val name: String,
    val prerelease: Boolean,
    val draft: Boolean,
    val body: String,
    val publishedAt: String,
    val targetCommitish: String,
    val assets: List<GitHubAsset>
)

sealed interface UpdateCheckResult {
    data object Idle : UpdateCheckResult
    data object Checking : UpdateCheckResult
    data class UpToDate(
        val currentVersion: String = BuildConfig.VERSION_NAME,
        val publishedAt: String = "",
        val commitHash: String = ""
    ) : UpdateCheckResult
    data class UpdateAvailable(
        val currentVersion: String,
        val newVersion: String,
        val releaseName: String,
        val changelog: String,
        val publishedAt: String,
        val commitHash: String,
        val apkAsset: GitHubAsset,
        val isPrerelease: Boolean
    ) : UpdateCheckResult
    data class Error(val message: String) : UpdateCheckResult
}

sealed interface DownloadState {
    data object Idle : DownloadState
    data class Downloading(
        val downloadedBytes: Long,
        val totalBytes: Long,
        val progress: Float, // 0.0f .. 1.0f
        val formattedProgress: String, // "18.4 MB / 32.1 MB • %57"
        val versionName: String
    ) : DownloadState
    data class ReadyToInstall(
        val apkFile: File,
        val versionName: String
    ) : DownloadState
    data class Failed(val message: String) : DownloadState
}

class UpdateManager(
    private val context: Context,
    private val repoOwner: String = "tuawtf",
    private val repoName: String = "FuckKTB"
) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val _checkState = MutableStateFlow<UpdateCheckResult>(UpdateCheckResult.Idle)
    val checkState: StateFlow<UpdateCheckResult> = _checkState.asStateFlow()

    suspend fun checkForUpdates(channel: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        _checkState.value = UpdateCheckResult.Checking
        try {
            val apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/releases"
            val request = Request.Builder()
                .url(apiUrl)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "FKTB-Android-App")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val errorMsg = "GitHub API yanıt vermedi (HTTP ${response.code})"
                val result = UpdateCheckResult.Error(errorMsg)
                _checkState.value = result
                return@withContext result
            }

            val bodyString = response.body?.string().orEmpty()
            val jsonArray = JSONArray(bodyString)
            val releases = mutableListOf<GitHubRelease>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val isDraft = obj.optBoolean("draft", false)
                if (isDraft) continue

                val assetsArray = obj.optJSONArray("assets") ?: JSONArray()
                val assets = mutableListOf<GitHubAsset>()
                for (j in 0 until assetsArray.length()) {
                    val a = assetsArray.getJSONObject(j)
                    val name = a.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        assets.add(
                            GitHubAsset(
                                id = a.optLong("id", 0),
                                name = name,
                                size = a.optLong("size", 0),
                                downloadUrl = a.optString("browser_download_url", "")
                            )
                        )
                    }
                }

                val publishedAt = obj.optString("published_at", "")
                val targetCommitish = obj.optString("target_commitish", "")

                releases.add(
                    GitHubRelease(
                        id = obj.optLong("id", 0),
                        tagName = obj.optString("tag_name", ""),
                        name = obj.optString("name", obj.optString("tag_name", "")),
                        prerelease = obj.optBoolean("prerelease", false),
                        draft = isDraft,
                        body = obj.optString("body", ""),
                        publishedAt = publishedAt,
                        targetCommitish = targetCommitish,
                        assets = assets
                    )
                )
            }

            // Kanal Filtreleme:
            // STABLE: Yalnızca prerelease == false olan sürümler
            // BETA: Pre-release dahil tüm sürümler
            val eligibleReleases = if (channel.equals("BETA", ignoreCase = true)) {
                releases
            } else {
                releases.filter { !it.prerelease }
            }

            if (eligibleReleases.isEmpty()) {
                val result = UpdateCheckResult.UpToDate()
                _checkState.value = result
                return@withContext result
            }

            val latestRelease = eligibleReleases.first()
            val currentVersion = BuildConfig.VERSION_NAME

            val isNewer = isVersionNewer(latestRelease.tagName, currentVersion)
            if (isNewer && latestRelease.assets.isNotEmpty()) {
                val matchingApk = pickBestApkAsset(latestRelease.assets)
                if (matchingApk != null) {
                    val result = UpdateCheckResult.UpdateAvailable(
                        currentVersion = currentVersion,
                        newVersion = latestRelease.tagName.removePrefix("v").removePrefix("V"),
                        releaseName = latestRelease.name,
                        changelog = latestRelease.body,
                        publishedAt = latestRelease.publishedAt,
                        commitHash = latestRelease.targetCommitish,
                        apkAsset = matchingApk,
                        isPrerelease = latestRelease.prerelease
                    )
                    _checkState.value = result
                    return@withContext result
                }
            }

            val result = UpdateCheckResult.UpToDate(
                currentVersion = currentVersion,
                publishedAt = latestRelease.publishedAt,
                commitHash = latestRelease.targetCommitish
            )
            _checkState.value = result
            result
        } catch (e: Exception) {
            Log.e("UpdateManager", "Güncelleme denetleme hatası", e)
            val result = UpdateCheckResult.Error("Bağlantı hatası: ${e.localizedMessage ?: "Bilinmeyen hata"}")
            _checkState.value = result
            result
        }
    }

    private fun pickBestApkAsset(assets: List<GitHubAsset>): GitHubAsset? {
        val smsEnabled = BuildConfig.SMS_PERMISSION_ENABLED
        return if (smsEnabled) {
            assets.firstOrNull { it.name.endsWith(".apk") && !it.name.contains("nosms", ignoreCase = true) }
                ?: assets.firstOrNull { it.name.endsWith(".apk") }
        } else {
            assets.firstOrNull { it.name.contains("nosms", ignoreCase = true) && it.name.endsWith(".apk") }
                ?: assets.firstOrNull { it.name.endsWith(".apk") }
        }
    }

    suspend fun downloadAndInstallApk(apkAsset: GitHubAsset, versionName: String) = withContext(Dispatchers.IO) {
        try {
            _downloadState.value = DownloadState.Downloading(
                downloadedBytes = 0L,
                totalBytes = apkAsset.size,
                progress = 0f,
                formattedProgress = "0 MB / %.1f MB • %%0".format(apkAsset.size / (1024f * 1024f)),
                versionName = versionName
            )

            val request = Request.Builder()
                .url(apkAsset.downloadUrl)
                .header("User-Agent", "FKTB-Android-App")
                .header("Accept", "application/octet-stream")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                _downloadState.value = DownloadState.Failed("İndirme sunucu hatası (HTTP ${response.code})")
                return@withContext
            }

            val responseBody = response.body ?: throw IOException("Boş yanıt gövdesi")
            val totalBytes = if (responseBody.contentLength() > 0) responseBody.contentLength() else apkAsset.size

            val cacheDir = File(context.cacheDir, "updates")
            if (!cacheDir.exists()) cacheDir.mkdirs()
            val apkFile = File(cacheDir, "FKTB-v$versionName.apk")
            if (apkFile.exists()) apkFile.delete()

            responseBody.byteStream().use { input ->
                apkFile.outputStream().use { output ->
                    val buffer = ByteArray(16384)
                    var bytesRead: Int
                    var downloadedBytes = 0L
                    var lastUpdateMs = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        val now = System.currentTimeMillis()
                        if (now - lastUpdateMs > 120 || downloadedBytes == totalBytes) {
                            lastUpdateMs = now
                            val progress = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
                            val dlMB = downloadedBytes / (1024f * 1024f)
                            val totMB = totalBytes / (1024f * 1024f)
                            val percent = (progress * 100).toInt().coerceIn(0, 100)
                            val formatted = String.format(Locale.US, "%.1f MB / %.1f MB • %%%d", dlMB, totMB, percent)

                            _downloadState.value = DownloadState.Downloading(
                                downloadedBytes = downloadedBytes,
                                totalBytes = totalBytes,
                                progress = progress,
                                formattedProgress = formatted,
                                versionName = versionName
                            )
                        }
                    }
                }
            }

            _downloadState.value = DownloadState.ReadyToInstall(
                apkFile = apkFile,
                versionName = versionName
            )

            // Doğrudan sistem paket yükleyicisini başlat
            withContext(Dispatchers.Main) {
                installApk(apkFile)
            }
        } catch (e: Exception) {
            Log.e("UpdateManager", "APK indirme hatası", e)
            _downloadState.value = DownloadState.Failed("İndirme başarısız: ${e.localizedMessage ?: "Bilinmeyen hata"}")
        }
    }

    fun installApk(apkFile: File) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(settingsIntent)
                    return
                }
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, apkFile)

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e("UpdateManager", "Yükleyici başlatılamadı", e)
            _downloadState.value = DownloadState.Failed("Yükleyici başlatılamadı: ${e.localizedMessage}")
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }

    fun resetCheckState() {
        _checkState.value = UpdateCheckResult.Idle
    }

    companion object {
        fun isVersionNewer(newVerStr: String, currentVerStr: String): Boolean {
            val cleanNew = newVerStr.removePrefix("v").removePrefix("V").trim()
            val cleanCur = currentVerStr.removePrefix("v").removePrefix("V").trim()

            val newParts = cleanNew.split("-", "_", "+")[0].split(".").mapNotNull { it.toIntOrNull() }
            val curParts = cleanCur.split("-", "_", "+")[0].split(".").mapNotNull { it.toIntOrNull() }

            val maxLen = maxOf(newParts.size, curParts.size)
            for (i in 0 until maxLen) {
                val n = newParts.getOrElse(i) { 0 }
                val c = curParts.getOrElse(i) { 0 }
                if (n > c) return true
                if (n < c) return false
            }
            return false
        }
    }
}
