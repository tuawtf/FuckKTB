package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPhoneNumberNormalization() {
        assertEquals("905321234567", PhoneUtils.normalizePhoneNumber("0532 123 45 67"))
        assertEquals("905321234567", PhoneUtils.normalizePhoneNumber("+90 532 123 45 67"))
        assertEquals("905321234567", PhoneUtils.normalizePhoneNumber("5321234567"))
        assertEquals("905321234567", PhoneUtils.normalizePhoneNumber("905321234567"))
        assertEquals("905321234567", PhoneUtils.normalizePhoneNumber("00905321234567"))

        assertTrue(PhoneUtils.isValidTurkishMobile("905321234567"))
        assertFalse(PhoneUtils.isValidTurkishMobile("90532123456"))  // 11 digits
        assertFalse(PhoneUtils.isValidTurkishMobile("906321234567")) // doesn't start with 905
    }

    @Test
    fun testSmsVerificationCodeExtraction() {
        val messageBody = "Dogrulama kodu: 594586 Bu kodu kullanarak KTB Wifi hizmetinden yararlanabilirsiniz. B001"
        val sender = "KUL.TUR.BKN"

        val code = SmsReceiver.extractVerificationCode(sender, messageBody)
        assertEquals("594586", code)

        // Case insensitivity & spaces
        val variations = listOf(
            "Dogrulama kodu: 123456 KTB Wifi",
            "dogrulama kodu:  789012  Bu kodu kullanarak",
            "Dogrulama Kodu: 345678"
        )
        assertEquals("123456", SmsReceiver.extractVerificationCode(sender, variations[0]))
        assertEquals("789012", SmsReceiver.extractVerificationCode(sender, variations[1]))
        assertEquals("345678", SmsReceiver.extractVerificationCode(sender, variations[2]))
    }
}
