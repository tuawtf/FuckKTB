package com.example

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

data class LogEntry(
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
    val logs: List<LogEntry> = emptyList(),
    val isAutoValidateEnabled: Boolean = true
)

class PortalViewModel(application: Application) : AndroidViewModel(application) {

    private val client = CaptivePortalClient()
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val _uiState = MutableStateFlow(PortalUiState())
    val uiState: StateFlow<PortalUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        // Önceden kaydedilmiş telefon numarasını yükle
        loadSavedPhoneNumber()

        // Ağ durumunu kontrol et
        checkWifiState()

        // SMS Dinleyici akışını sürekli dinle
        observeSmsEvents()

        addLog(LogLevel.INFO, "KTB Wi-Fi Otomatik Giriş başlatıldı.")
    }

    private fun loadSavedPhoneNumber() {
        val saved = PhoneUtils.getSavedPhoneNumber(getApplication())
        if (!saved.isNullOrBlank()) {
            _uiState.update { it.copy(phoneNumber = saved) }
            addLog(LogLevel.INFO, "Kayıtlı telefon numarası yüklendi: ${PhoneUtils.formatForDisplay(saved)}")
        }
    }

    fun onPhoneNumberChanged(newPhone: String) {
        val normalized = PhoneUtils.normalizePhoneNumber(newPhone)
        _uiState.update { it.copy(phoneNumber = normalized, errorMessage = null) }
        PhoneUtils.savePhoneNumber(getApplication(), normalized)
    }

    fun onVerificationCodeChanged(code: String) {
        val digits = code.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(verificationCode = digits, errorMessage = null) }
    }

    fun tryAutoDetectPhoneNumber(context: Context) {
        val detected = PhoneUtils.detectDevicePhoneNumber(context)
        if (detected != null) {
            onPhoneNumberChanged(detected)
            addLog(LogLevel.SUCCESS, "SIM karttan numara otomatik algılandı: ${PhoneUtils.formatForDisplay(detected)}")
        } else {
            addLog(LogLevel.WARNING, "Cihazdan numara otomatik alınamadı. Lütfen elle giriniz.")
        }
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
                it.copy(
                    isWifiConnected = isWifi,
                    wifiSsid = ssid
                )
            }
        } catch (_: Exception) {}
    }

    /**
     * SmsReceiver'dan gelen kodları yakalar ve bekleme durumundaysa otomatik giriş yapar.
     */
    private fun observeSmsEvents() {
        viewModelScope.launch {
            SmsReceiver.smsEvents.collect { event ->
                addLog(LogLevel.SUCCESS, "SMS Yakalandı! Gönderici: ${event.sender}")
                addLog(LogLevel.INFO, "Doğrulama Kodu: ${event.verificationCode}")

                _uiState.update {
                    it.copy(
                        verificationCode = event.verificationCode
                    )
                }

                // Otomatik doğrulama açık ise ve SMS bekliyorsak veya hazır durumdaysak doğrudan doğrula
                if (_uiState.value.isAutoValidateEnabled) {
                    validateSmsToken(event.verificationCode)
                }
            }
        }
    }

    /**
     * 1. ADIM: "Bağlan" butonu tetiklendiğinde createSmsToken isteği atar.
     */
    fun startConnectionFlow() {
        val currentPhone = _uiState.value.phoneNumber
        if (!PhoneUtils.isValidTurkishMobile(currentPhone)) {
            val error = "Lütfen geçerli bir cep telefonu numarası girin (Örn: 05xx xxx xx xx)"
            _uiState.update { it.copy(errorMessage = error) }
            addLog(LogLevel.ERROR, error)
            return
        }

        // Numarayı kalıcı kaydet
        PhoneUtils.savePhoneNumber(getApplication(), currentPhone)

        _uiState.update {
            it.copy(
                step = PortalStep.REQUESTING_SMS,
                statusMessage = "SMS Kodu İsteniyor...",
                errorMessage = null,
                verificationCode = ""
            )
        }

        addLog(LogLevel.INFO, "POST http://captive.kultur.gov.tr/users/createSmsToken ($currentPhone)")

        viewModelScope.launch {
            val result = client.createSmsToken(currentPhone)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        step = PortalStep.WAITING_SMS,
                        statusMessage = "SMS Kodu Bekleniyor... (KUL.TUR.BKN)",
                        countdownSeconds = 120
                    )
                }
                addLog(LogLevel.SUCCESS, "SMS talebi başarılı! KUL.TUR.BKN tarafından SMS gönderildi.")
                startCountdown()
            } else {
                _uiState.update {
                    it.copy(
                        step = PortalStep.ERROR,
                        statusMessage = "SMS Talebi Başarısız",
                        errorMessage = result.message
                    )
                }
                addLog(LogLevel.ERROR, "SMS Hatası: ${result.message} (HTTP ${result.httpStatusCode})")
            }
        }
    }

    /**
     * 2. ADIM: Doğrulama kodunu portal API'sine gönderir ve bağlantıyı tamamlar.
     */
    fun validateSmsToken(code: String = _uiState.value.verificationCode) {
        val currentPhone = _uiState.value.phoneNumber
        val cleanCode = code.trim()

        if (cleanCode.length != 6) {
            val error = "Doğrulama kodu 6 haneli olmalıdır."
            _uiState.update { it.copy(errorMessage = error) }
            addLog(LogLevel.ERROR, error)
            return
        }

        countdownJob?.cancel()

        _uiState.update {
            it.copy(
                step = PortalStep.VALIDATING,
                statusMessage = "Doğrulanıyor ve Giriş Yapılıyor...",
                verificationCode = cleanCode,
                errorMessage = null
            )
        }

        addLog(LogLevel.INFO, "POST http://captive.kultur.gov.tr/users/validateSmsToken ($cleanCode)")

        viewModelScope.launch {
            val result = client.validateSmsToken(currentPhone, cleanCode)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        step = PortalStep.SUCCESS,
                        statusMessage = "İnternet Bağlantısı Başarılı!",
                        errorMessage = null
                    )
                }
                addLog(LogLevel.SUCCESS, "TEBRİKLER: İnternet Bağlantısı Başarılı! KTB WiFi aktif.")

                // İnternet erişimini doğrulamak için test pingi at
                checkInternetAfterLogin()
            } else {
                _uiState.update {
                    it.copy(
                        step = PortalStep.ERROR,
                        statusMessage = "Giriş Başarısız",
                        errorMessage = result.message
                    )
                }
                addLog(LogLevel.ERROR, "Doğrulama Hatası: ${result.message} (HTTP ${result.httpStatusCode})")
            }
        }
    }

    private fun checkInternetAfterLogin() {
        viewModelScope.launch {
            delay(1500)
            val isOnline = client.checkInternetConnectivity()
            if (isOnline) {
                addLog(LogLevel.SUCCESS, "Ağ Kontrolü: Küresel internet bağlantısı teyit edildi (HTTP 204 OK).")
            } else {
                addLog(LogLevel.INFO, "Ağ Kontrolü: Captive portal yanıt verdi, tarayıcıda gezinmeye başlayabilirsiniz.")
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
                addLog(LogLevel.WARNING, "SMS bekleme süresi doldu. Tekrar 'SMS İste' diyebilirsiniz.")
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
        addLog(LogLevel.INFO, "Durum sıfırlandı.")
    }

    /**
     * Test / Simülasyon: Kullanıcı emülatörde veya gerçek SMS olmadan testi deneyebilsin.
     */
    fun simulateMockSms(mockCode: String = "594586") {
        addLog(LogLevel.INFO, "--- TEST SİMÜLASYONU TETİKLENDİ ---")
        val sender = "KUL.TUR.BKN"
        val body = "Dogrulama kodu: $mockCode Bu kodu kullanarak KTB Wifi hizmetinden yararlanabilirsiniz. B001"
        SmsReceiver.simulateSmsArrival(sender, body, mockCode)
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    private fun addLog(level: LogLevel, message: String) {
        val entry = LogEntry(
            timestamp = timeFormat.format(Date()),
            level = level,
            message = message
        )
        _uiState.update {
            val updated = it.logs + entry
            // Son 100 logu tut
            it.copy(logs = updated.takeLast(100))
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}
