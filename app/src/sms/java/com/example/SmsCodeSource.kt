package com.example

import androidx.activity.ComponentActivity

/**
 * SMS izinli (sms flavor) surum icin SmsCodeSource nesnesi.
 * SMS yakalama islemleri dogrudan manifest'teki SmsReceiver uzerinden SmsCodeBus'a iletilir.
 * Bu yuzden dongu fonksiyonlari bostur.
 *
 * SIFIR EMOJI KURALI: Kod, log ve mesajlarda emoji kullanilmaz.
 */
object SmsCodeSource {

    fun init(activity: ComponentActivity) {
        // sms flavor manifest uzerinden SmsReceiver ile calisir
    }

    fun start(activity: ComponentActivity) {
        // sms flavor icin ayri bir baslatma islemi gerekmez
    }

    fun stop(activity: ComponentActivity) {
        // sms flavor icin ayri bir durdurma islemi gerekmez
    }

    fun startSmsUserConsent(activity: ComponentActivity) {
        // sms flavor'da SMS User Consent API kullanilmaz
    }
}
