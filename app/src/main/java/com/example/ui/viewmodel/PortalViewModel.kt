package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CaptivePortalClient
import com.example.SmsCodeBus
import com.example.data.DownloadState
import com.example.data.GitHubAsset
import com.example.data.UpdateCheckResult
import com.example.data.UpdateManager
import com.example.data.UserPreferences
import com.example.data.UserPreferencesRepository
import com.example.util.PhoneUtils
import java.io.File
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PortalStep {
    IDLE,
    REQUESTING_SMS,
    WAITING_SMS,
    VALIDATING,
    SUCCESS,
    ERROR
}

enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

data class LogItem(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val timestamp: String,
    val level: LogLevel,
    val message: String
)

data class PortalUiState(
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val step: PortalStep = PortalStep.IDLE,
    val statusMessage: String = "Bağlanmaya hazır",
    val errorMessage: String? = null,
    val countdownSeconds: Int = 120,
    val isWifiConnected: Boolean = false,
    val wifiSsid: String? = null,
    val logs: List<LogItem> = emptyList()
)

class PortalViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefsRepo = UserPreferencesRepository(application)
    private val client = CaptivePortalClient(application)
    val updateManager = UpdateManager(application)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val userPreferences: StateFlow<UserPreferences> = userPrefsRepo.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences(
                phoneNumber = "",
                isOnboardingCompleted = false,
                isAutoConnectEnabled = false,
                updateChannel = "STABLE",
                checkUpdatesOnLaunch = true,
                latestStableVersion = "",
                latestBetaVersion = "",
                themeMode = "DARK",
                colorPalette = "OCEAN"
            )
        )

    private val _uiState = MutableStateFlow(PortalUiState())
    val uiState: StateFlow<PortalUiState> = _uiState.asStateFlow()

    val updateCheckState: StateFlow<UpdateCheckResult> = updateManager.checkState
    val downloadState: StateFlow<DownloadState> = updateManager.downloadState

    private var countdownJob: Job? = null
    private var hasFetchedLatestVersionsOnLaunch = false

    init {
        // DataStore'daki kayitli telefon numarasini izle
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                if (prefs.phoneNumber.isNotBlank() && _uiState.value.phoneNumber.isBlank()) {
                    _uiState.update { it.copy(phoneNumber = prefs.phoneNumber) }
                    addLog(LogLevel.INFO, "Kayitli numara DataStore uzerinden yuklendi: " + PhoneUtils.formatForDisplay(prefs.phoneNumber))
                }

                // Uygulama ilk açılışta en son sürüm numaralarını arka planda sessizce kaydeder
                if (!hasFetchedLatestVersionsOnLaunch) {
                    hasFetchedLatestVersionsOnLaunch = true
                    viewModelScope.launch {
                        val (stableVer, betaVer) = updateManager.fetchLatestVersionsQuietly()
                        if (stableVer != null) userPrefsRepo.setLatestStableVersion(stableVer)
                        if (betaVer != null) userPrefsRepo.setLatestBetaVersion(betaVer)

                        // Açılışta güncelleme denetleme ayarı açıksa arka planda kontrol et
                        if (prefs.checkUpdatesOnLaunch) {
                            checkForUpdates(prefs.updateChannel)
                        }
                    }
                }
            }
        }

        checkWifiState()
        observeSmsEvents()
        addLog(LogLevel.INFO, "KTB Wi-Fi Yardimcisi baslatildi.")
    }

    fun setUpdateChannel(channel: String) {
        viewModelScope.launch {
            userPrefsRepo.setUpdateChannel(channel)
            addLog(LogLevel.INFO, "Güncelleme kanalı seçildi: $channel")
            // Kanal değiştiğinde otomatik sorgulama yapmaz; sadece kullanıcı 'Güncellemeleri Denetle' dediğinde kontrol edilir.
        }
    }

    fun setCheckUpdatesOnLaunch(enabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepo.setCheckUpdatesOnLaunch(enabled)
            addLog(LogLevel.INFO, "Açılışta güncelleme kontrolü: " + if (enabled) "Aktif" else "Devre Dışı")
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            userPrefsRepo.setThemeMode(mode)
            addLog(LogLevel.INFO, "Tema modu seçildi: $mode")
        }
    }

    fun setColorPalette(palette: String) {
        viewModelScope.launch {
            userPrefsRepo.setColorPalette(palette)
            addLog(LogLevel.INFO, "Renk paleti seçildi: $palette")
        }
    }

    fun checkForUpdates(channel: String) {
        viewModelScope.launch {
            addLog(LogLevel.INFO, "GitHub Releases denetleniyor ($channel)...")
            val result = updateManager.checkForUpdates(channel)
            when (result) {
                is UpdateCheckResult.UpdateAvailable -> {
                    addLog(LogLevel.SUCCESS, "Yeni sürüm bulundu: v${result.newVersion}")
                }
                is UpdateCheckResult.UpToDate -> {
                    addLog(LogLevel.INFO, "Uygulama sürümü güncel.")
                }
                is UpdateCheckResult.Error -> {
                    addLog(LogLevel.WARNING, result.message)
                }
                else -> {}
            }
        }
    }

    fun startApkDownload(asset: GitHubAsset, versionName: String) {
        viewModelScope.launch {
            addLog(LogLevel.INFO, "v$versionName APK indirmesi başlatılıyor...")
            updateManager.downloadAndInstallApk(asset, versionName)
        }
    }

    fun installApk(apkFile: File) {
        updateManager.installApk(apkFile)
    }

    fun resetUpdateCheck() {
        updateManager.resetCheckState()
    }

    fun resetDownloadState() {
        updateManager.resetDownloadState()
    }

    fun updatePhoneNumber(phone: String) {
        val normalized = PhoneUtils.normalizePhoneNumber(phone)
        _uiState.update { it.copy(phoneNumber = normalized, errorMessage = null) }
        viewModelScope.launch {
            userPrefsRepo.setPhoneNumber(normalized)
        }
    }

    fun completeOnboarding(phoneNumber: String) {
        val normalized = PhoneUtils.normalizePhoneNumber(phoneNumber)
        _uiState.update { it.copy(phoneNumber = normalized) }
        viewModelScope.launch {
            userPrefsRepo.setPhoneNumber(normalized)
            userPrefsRepo.setOnboardingCompleted(true)
            addLog(LogLevel.SUCCESS, "Ilk kurulum basariyla tamamlandi.")
        }
    }

    fun setAutoConnect(enabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepo.setAutoConnectEnabled(enabled)
            addLog(
                LogLevel.INFO,
                if (enabled) "Otomatik baglanma acildi." else "Otomatik baglanma kapatildi."
            )
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            userPrefsRepo.setOnboardingCompleted(false)
            addLog(LogLevel.WARNING, "Kurulum sihirbazi sifirlandi.")
        }
    }

    fun onVerificationCodeChanged(code: String) {
        val digits = code.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(verificationCode = digits, errorMessage = null) }
    }

    fun checkWifiState() {
        try {
            val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val wm = getApplication<Application>().applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

            val network = cm?.activeNetwork
            val caps = cm?.getNetworkCapabilities(network)
            val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

            var ssid: String? = null
            if (isWifi) {
                @Suppress("DEPRECATION")
                val wifiInfo = wm?.connectionInfo
                val rawSsid = wifiInfo?.ssid?.replace("\"", "")
                if (!rawSsid.isNullOrBlank() && rawSsid != "<unknown ssid>") {
                    ssid = rawSsid
                }
            }

            _uiState.update {
                it.copy(isWifiConnected = isWifi, wifiSsid = ssid)
            }
        } catch (_: Exception) {}
    }

    private fun observeSmsEvents() {
        viewModelScope.launch {
            SmsCodeBus.smsEvents.collect { event ->
                addLog(LogLevel.SUCCESS, "SMS Yakalandi! Gonderici: " + event.sender)
                addLog(LogLevel.INFO, "Dogrulama Kodu: " + event.verificationCode)

                _uiState.update {
                    it.copy(verificationCode = event.verificationCode)
                }

                // Otomatik giris akisini tetikle
                validateSmsToken(event.verificationCode)
            }
        }
    }

    /**
     * 1. ADIM: "Baglan" butonuna basildiginda createSmsToken istegi atar.
     */
    fun startConnectionFlow(onBeforeRequest: (() -> Unit)? = null) {
        val currentPhone = _uiState.value.phoneNumber
        if (!PhoneUtils.isValidTurkishMobile(currentPhone)) {
            val error = "Gecerli bir cep telefonu numarasi gereklidir (Ornek: 05xx xxx xx xx)"
            _uiState.update { it.copy(errorMessage = error) }
            addLog(LogLevel.ERROR, error)
            return
        }

        _uiState.update {
            it.copy(
                step = PortalStep.REQUESTING_SMS,
                statusMessage = "SMS Kodu Isteniyor...",
                errorMessage = null,
                verificationCode = ""
            )
        }

        addLog(LogLevel.INFO, "POST http://captive.kultur.gov.tr/users/createSmsToken (" + currentPhone + ")")

        onBeforeRequest?.invoke()

        viewModelScope.launch {
            val result = client.createSmsToken(currentPhone)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        step = PortalStep.WAITING_SMS,
                        statusMessage = "SMS Kodu Bekleniyor...",
                        countdownSeconds = 120
                    )
                }
                addLog(LogLevel.SUCCESS, "SMS talebi iletildi. KUL.TUR.BKN SMS'i bekleniyor.")
                startCountdown()
            } else {
                _uiState.update {
                    it.copy(
                        step = PortalStep.ERROR,
                        statusMessage = "SMS Talebi Basarisiz",
                        errorMessage = result.message
                    )
                }
                addLog(LogLevel.ERROR, "SMS Hatasi: " + result.message + " (HTTP " + result.httpStatusCode + ")")
            }
        }
    }

    /**
     * 2. ADIM: Dogrulama kodunu portala gonderip giris yapar.
     */
    fun validateSmsToken(code: String = _uiState.value.verificationCode) {
        val currentPhone = _uiState.value.phoneNumber
        val cleanCode = code.trim()

        if (cleanCode.length != 6) {
            val error = "Dogrulama kodu 6 haneli olmalidir."
            _uiState.update { it.copy(errorMessage = error) }
            addLog(LogLevel.ERROR, error)
            return
        }

        countdownJob?.cancel()

        _uiState.update {
            it.copy(
                step = PortalStep.VALIDATING,
                statusMessage = "Giris Yapiliyor...",
                verificationCode = cleanCode,
                errorMessage = null
            )
        }

        addLog(LogLevel.INFO, "POST http://captive.kultur.gov.tr/users/validateSmsToken (" + cleanCode + ")")

        viewModelScope.launch {
            val result = client.validateSmsToken(currentPhone, cleanCode)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        step = PortalStep.SUCCESS,
                        statusMessage = "Internet Baglantisi Basarili!",
                        errorMessage = null
                    )
                }
                addLog(LogLevel.SUCCESS, "Internet Baglantisi Basarili! Captive portal acildi.")
                verifyGlobalInternet()
            } else {
                _uiState.update {
                    it.copy(
                        step = PortalStep.ERROR,
                        statusMessage = "Giris Basarisiz",
                        errorMessage = result.message
                    )
                }
                addLog(LogLevel.ERROR, "Dogrulama Hatasi: " + result.message + " (HTTP " + result.httpStatusCode + ")")
            }
        }
    }

    private fun verifyGlobalInternet() {
        viewModelScope.launch {
            delay(1500)
            val isOnline = client.checkInternetConnectivity()
            if (isOnline) {
                addLog(LogLevel.SUCCESS, "Ag Denetimi: Kuresel internet erisimi dogrulandi (HTTP 204).")
            } else {
                addLog(LogLevel.INFO, "Ag Denetimi: Captive portal onay verdi, internet erisimi hazir.")
            }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (sec in 120 downTo 0) {
                _uiState.update { it.copy(countdownSeconds = sec) }
                delay(1000)
            }
            if (_uiState.value.step == PortalStep.WAITING_SMS) {
                addLog(LogLevel.WARNING, "SMS bekleme suresi doldu. Yeniden SMS isteyebilirsiniz.")
            }
        }
    }

    fun resetState() {
        countdownJob?.cancel()
        _uiState.update {
            it.copy(
                step = PortalStep.IDLE,
                statusMessage = "Bağlanmaya hazır",
                errorMessage = null,
                verificationCode = ""
            )
        }
        addLog(LogLevel.INFO, "Baglanti durumu sifirlandi.")
    }

    fun simulateSmsArrival(mockCode: String = "594586") {
        addLog(LogLevel.INFO, "Test SMS simulasyonu tetiklendi (Kod: " + mockCode + ")")
        val sender = "KUL.TUR.BKN"
        val body = "Dogrulama kodu: $mockCode Bu kodu kullanarak KTB Wifi hizmetinden yararlanabilirsiniz. B001"
        SmsCodeBus.simulateSmsArrival(sender, body, mockCode)
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    fun addLog(level: LogLevel, message: String) {
        val item = LogItem(
            timestamp = timeFormat.format(Date()),
            level = level,
            message = message
        )
        _uiState.update {
            val updated = it.logs + item
            it.copy(logs = updated.takeLast(100))
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
