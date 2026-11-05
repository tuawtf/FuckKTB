package com.example

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Kutuphane Wi-Fi captive portalindan (KUL.TUR.BKN) gelen dogrulama SMS'lerini
 * isleyen, regex ile 6 haneli kodu ayiklayan ve UI/ViewModel'e ileten ortak olay veri yolu (event bus).
 *
 * SIFIR EMOJI KURALI: Kod, log ve mesajlarda emoji kullanilmaz.
 */
object SmsCodeBus {

    const val TARGET_SENDER = "KUL.TUR.BKN"

    val PRIMARY_REGEX = Regex("""(?:Dogrulama kodu:\s*)(\d{6})""", RegexOption.IGNORE_CASE)
    val FALLBACK_REGEX = Regex("""\b(\d{6})\b""")

    data class SmsEvent(
        val sender: String,
        val fullBody: String,
        val verificationCode: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _smsEvents = MutableSharedFlow<SmsEvent>(extraBufferCapacity = 10)
    val smsEvents: SharedFlow<SmsEvent> = _smsEvents.asSharedFlow()

    fun emitCode(sender: String, body: String, code: String) {
        _smsEvents.tryEmit(
            SmsEvent(
                sender = sender,
                fullBody = body,
                verificationCode = code
            )
        )
    }

    fun simulateSmsArrival(sender: String = TARGET_SENDER, body: String, code: String) {
        emitCode(sender, body, code)
    }

    fun extractVerificationCode(sender: String?, body: String): String? {
        val primaryMatch = PRIMARY_REGEX.find(body)
        if (primaryMatch != null && primaryMatch.groupValues.size > 1) {
            return primaryMatch.groupValues[1]
        }

        val isKtbSender = sender?.contains("KUL", ignoreCase = true) == true ||
                sender?.contains("TUR", ignoreCase = true) == true ||
                sender?.contains("BKN", ignoreCase = true) == true

        val isKtbBody = body.contains("KTB", ignoreCase = true) ||
                body.contains("Wifi", ignoreCase = true) ||
                body.contains("kütüphane", ignoreCase = true) ||
                body.contains("kutuphane", ignoreCase = true)

        if (isKtbSender || isKtbBody) {
            val fallbackMatch = FALLBACK_REGEX.find(body)
            if (fallbackMatch != null) {
                return fallbackMatch.groupValues[1]
            }
        }

        return null
    }
}
