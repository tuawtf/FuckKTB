package com.example

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Kultur ve Turizm Bakanligi (KTB) kutuphane Wi-Fi captive portali ile
 * haberlesen ve ag trafigini zorunlu Wi-Fi arayuzune baglayan OkHttp istemcisi.
 */
class CaptivePortalClient(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
) {

    companion object {
        private const val TAG = "CaptivePortalClient"
        private const val BASE_URL = "http://captive.kultur.gov.tr"
        private const val CREATE_SMS_TOKEN_URL = "$BASE_URL/users/createSmsToken"
        private const val VALIDATE_SMS_TOKEN_URL = "$BASE_URL/users/validateSmsToken"
        private const val CONNECTIVITY_CHECK_URL = "http://connectivitycheck.gstatic.com/generate_204"

        // Nginx / Express sunucu beklentisi olan basliklar
        private const val HEADER_ORIGIN = "http://captive.kultur.gov.tr"
        private const val HEADER_REFERER = "http://captive.kultur.gov.tr/"
        private const val HEADER_USER_AGENT = "Mozilla/5.0 (Android 16; Mobile; rv:159.0) Gecko/159.0 Firefox/159.0"
        private const val HEADER_ACCEPT = "application/json, text/plain, */*"
        private const val HEADER_ACCEPT_LANGUAGE = "tr-TR"

        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    data class PortalResult(
        val isSuccess: Boolean,
        val message: String,
        val rawResponse: String? = null,
        val httpStatusCode: Int = 0
    )

    /**
     * Android'in internetsiz Wi-Fi baglantilarinda trafigi otomatik olarak
     * mobil veriye (LTE/5G) yonlendirmesini engelleyip, soketleri Wi-Fi agina kilitler.
     */
    private fun bindProcessToWifiNetwork(): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

            val wifiNetwork = cm.allNetworks.firstOrNull { network ->
                val caps = cm.getNetworkCapabilities(network)
                caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }

            if (wifiNetwork != null) {
                val bound = cm.bindProcessToNetwork(wifiNetwork)
                Log.d(TAG, "Ag Wi-Fi arayuzune baglandi (bindProcessToNetwork): $bound")
                bound
            } else {
                Log.w(TAG, "Aktif bir Wi-Fi agi bulunamadi.")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Wi-Fi agina baglanirken hata: ${e.localizedMessage}")
            false
        }
    }

    /**
     * 1. ADIM: SMS Dogrulama Kodu Istegi
     *
     * Method: POST
     * URL: http://captive.kultur.gov.tr/users/createSmsToken
     * Headers:
     *   - Content-Type: application/json
     *   - Accept: application/json, text/plain, * / *
     *   - Accept-Language: tr-TR
     *   - Origin: http://captive.kultur.gov.tr
     *   - Referer: http://captive.kultur.gov.tr/
     *   - User-Agent: Mozilla/5.0 (Android 16; Mobile; rv:159.0) Gecko/159.0 Firefox/159.0
     * Body: {"phone_number": "905xxxxxxxxx"}
     */
    suspend fun createSmsToken(phoneNumber: String): PortalResult = withContext(Dispatchers.IO) {
        // Trafigi Wi-Fi arayuzune kilitle
        bindProcessToWifiNetwork()

        val jsonBody = JSONObject().apply {
            put("phone_number", phoneNumber)
        }.toString()

        val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(CREATE_SMS_TOKEN_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", HEADER_ACCEPT)
            .addHeader("Accept-Language", HEADER_ACCEPT_LANGUAGE)
            .addHeader("Origin", HEADER_ORIGIN)
            .addHeader("Referer", HEADER_REFERER)
            .addHeader("User-Agent", HEADER_USER_AGENT)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                val statusCode = response.code
                parsePortalResponse(bodyString, statusCode, "SMS dogrulama kodu basariyla istendi.")
            }
        } catch (e: IOException) {
            PortalResult(
                isSuccess = false,
                message = "Baglanti hatasi: ${e.localizedMessage ?: "Captive portala ulasilamadi. Kutuphane Wi-Fi agina bagli oldugunuzdan emin olun."}",
                httpStatusCode = -1
            )
        } catch (e: Exception) {
            PortalResult(
                isSuccess = false,
                message = "Beklenmeyen hata: ${e.localizedMessage}",
                httpStatusCode = -1
            )
        }
    }

    /**
     * 2. ADIM: Dogrulama ve Giris Istegi
     *
     * Method: POST
     * URL: http://captive.kultur.gov.tr/users/validateSmsToken
     * Headers: 1. adim ile ayni eksiksiz basliklar
     * Body: {"phone_number": "905xxxxxxxxx", "verification_code": "XXXXXX"}
     */
    suspend fun validateSmsToken(phoneNumber: String, verificationCode: String): PortalResult =
        withContext(Dispatchers.IO) {
            // Trafigi Wi-Fi arayuzune kilitle
            bindProcessToWifiNetwork()

            val jsonBody = JSONObject().apply {
                put("phone_number", phoneNumber)
                put("verification_code", verificationCode.trim())
            }.toString()

            val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(VALIDATE_SMS_TOKEN_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", HEADER_ACCEPT)
                .addHeader("Accept-Language", HEADER_ACCEPT_LANGUAGE)
                .addHeader("Origin", HEADER_ORIGIN)
                .addHeader("Referer", HEADER_REFERER)
                .addHeader("User-Agent", HEADER_USER_AGENT)
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string().orEmpty()
                    val statusCode = response.code
                    parsePortalResponse(bodyString, statusCode, "Internet Baglantisi Basarili!")
                }
            } catch (e: IOException) {
                PortalResult(
                    isSuccess = false,
                    message = "Dogrulama baglanti hatasi: ${e.localizedMessage}",
                    httpStatusCode = -1
                )
            } catch (e: Exception) {
                PortalResult(
                    isSuccess = false,
                    message = "Dogrulama hatasi: ${e.localizedMessage}",
                    httpStatusCode = -1
                )
            }
        }

    /**
     * Captive portalin kalkip kuresel internetin acildigini test eder.
     */
    suspend fun checkInternetConnectivity(): Boolean = withContext(Dispatchers.IO) {
        bindProcessToWifiNetwork()
        val request = Request.Builder()
            .url(CONNECTIVITY_CHECK_URL)
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                response.code == 204 || response.isSuccessful
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Sunucu yanitini ayristirir.
     * Oncelikle JSON icindeki 'resultMessage' alanini okur, yoksa alternatif alanlara bakar.
     */
    private fun parsePortalResponse(
        rawBody: String,
        statusCode: Int,
        defaultSuccessMessage: String
    ): PortalResult {
        return try {
            val json = JSONObject(rawBody)
            val isSuccess = json.optBoolean("isSuccess", false)

            // Sunucunun dondurdugu hata veya basari mesajini ayikla
            val serverMessage = when {
                json.has("resultMessage") && !json.isNull("resultMessage") -> json.optString("resultMessage")
                json.has("message") && !json.isNull("message") -> json.optString("message")
                json.has("msg") && !json.isNull("msg") -> json.optString("msg")
                json.has("error") && !json.isNull("error") -> json.optString("error")
                else -> null
            }

            val finalMessage = when {
                !serverMessage.isNullOrBlank() -> serverMessage
                isSuccess -> defaultSuccessMessage
                statusCode != 200 -> "Sunucu yanit kodu: $statusCode"
                else -> "Islem captive portal tarafindan onaylanmadi."
            }

            PortalResult(
                isSuccess = isSuccess,
                message = finalMessage,
                rawResponse = rawBody,
                httpStatusCode = statusCode
            )
        } catch (_: Exception) {
            val lower = rawBody.lowercase()
            val seemsSuccess = lower.contains("\"issuccess\": true") || lower.contains("\"issuccess\":true")
            PortalResult(
                isSuccess = seemsSuccess,
                message = if (seemsSuccess) defaultSuccessMessage else if (statusCode != 200) "Sunucu hata kodu: $statusCode" else "Sunucudan beklenmeyen yanit alindi.",
                rawResponse = rawBody.take(500),
                httpStatusCode = statusCode
            )
        }
    }
}
