package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

/**
 * SMS izinli (sms flavor) surumde KUL.TUR.BKN dogrulama SMS'lerini yakalayan BroadcastReceiver.
 * Yakalanan dogrulama kodu guvenli bir sekilde SmsCodeBus uzerinden UI ve ViewModel'e aktarilir.
 *
 * Guvenlik geregi SMS icerigi ve dogrulama kodu loglanmaz.
 * SIFIR EMOJI KURALI: Kod, log ve mesajlarda emoji kullanilmaz.
 */
class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) return

            val fullBodyBuilder = StringBuilder()
            var sender: String? = null

            for (msg in messages) {
                if (sender == null) {
                    sender = msg.displayOriginatingAddress
                }
                fullBodyBuilder.append(msg.displayMessageBody ?: "")
            }

            val fullBody = fullBodyBuilder.toString()
            val code = SmsCodeBus.extractVerificationCode(sender, fullBody)

            if (code != null) {
                Log.d("SmsReceiver", "Dogrulama kodu basariyla ayiklandi ve veri yoluna iletildi.")
                SmsCodeBus.emitCode(
                    sender = sender ?: SmsCodeBus.TARGET_SENDER,
                    body = fullBody,
                    code = code
                )
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "SMS isleme hatasi: ${e.message}")
        }
    }
}
