package com.example

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Kütüphane Wi-Fi captive portalından (KUL.TUR.BKN) gelen doğrulama SMS'lerini
 * işleyen, regex ile 6 haneli kodu ayıklayan ve UI/ViewModel'e ileten yardımcı nesne.
 * (SMS User Consent API ile birlikte çalışır; doğrudan SMS dinleme izni gerektirmez).
 *
 * SIFIR EMOJI KURALI: Kod, log ve mesajlarda emoji kullanılmaz.
 */
object SmsReceiver {

    // Hedef gönderici başlığı
    const val TARGET_SENDER = "KUL.TUR.BKN"

    // Şartnamede belirtilen Regex: (?:Dogrulama kodu:\s*)(\d{6})
    val PRIMARY_REGEX = Regex("""(?:Dogrulama kodu:\s*)(\d{6})""", RegexOption.IGNORE_CASE)

    // Alternatif / esnek 6 haneli kod arama deseni
    val FALLBACK_REGEX = Regex("""\b(\d{6})\b""")

    data class SmsEvent(
        val sender: String,
        val fullBody: String,
        val verificationCode: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    // Uygulama içi reaktif haberleşme akışı
    private val _smsEvents = MutableSharedFlow<SmsEvent>(extraBufferCapacity = 10)
    val smsEvents: SharedFlow<SmsEvent> = _smsEvents.asSharedFlow()

    /**
     * SMS User Consent veya simülasyon sonucu yakalanan SMS olayını reaktif akışa iletir.
     * Güvenlik gereği SMS içeriği ve doğrulama kodu loglanmaz.
     */
    fun simulateSmsArrival(sender: String, body: String, code: String) {
        _smsEvents.tryEmit(
            SmsEvent(
                sender = sender,
                fullBody = body,
                verificationCode = code
            )
        )
    }

    /**
     * Verilen mesaj metninden 6 haneli doğrulama kodunu ayıklar.
     */
    fun extractVerificationCode(sender: String?, body: String): String? {
        // 1. Şartnamedeki birincil regex ile dene
        val primaryMatch = PRIMARY_REGEX.find(body)
        if (primaryMatch != null && primaryMatch.groupValues.size > 1) {
            return primaryMatch.groupValues[1]
        }

        // 2. Gönderici KTB/Kültür ile ilgiliyse veya mesajda "KTB"/"Wifi" geçiyorsa esnek 6 haneyi al
        val isKtbSender = sender?.contains("KUL", ignoreCase = true) == true ||
                sender?.contains("TUR", ignoreCase = true) == true ||
                sender?.contains("BKN", ignoreCase = true) == true

        val isKtbBody = body.contains("KTB", ignoreCase = true) ||
                body.contains("Wifi", ignoreCase = true) ||
                body.contains("kütüphane", ignoreCase = true)

        if (isKtbSender || isKtbBody) {
            val fallbackMatch = FALLBACK_REGEX.find(body)
            if (fallbackMatch != null) {
                return fallbackMatch.groupValues[1]
            }
        }

        return null
    }
}
