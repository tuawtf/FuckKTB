package com.example

import android.content.Context
import com.example.util.PhoneUtils as UtilPhoneUtils

/**
 * Geriye donuk uyumluluk karsilayici nesne (Zero Emoji).
 */
object PhoneUtils {
    fun normalizePhoneNumber(rawInput: String): String = UtilPhoneUtils.normalizePhoneNumber(rawInput)
    fun isValidTurkishMobile(normalizedPhone: String): Boolean = UtilPhoneUtils.isValidTurkishMobile(normalizedPhone)
    fun formatForDisplay(rawInput: String): String = UtilPhoneUtils.formatForDisplay(rawInput)
    fun detectDevicePhoneNumber(context: Context): String? = UtilPhoneUtils.detectDevicePhoneNumber(context)
}
