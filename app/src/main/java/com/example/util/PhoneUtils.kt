package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

/**
 * Telefon numarasi formatlama, dogrulama ve cihazdan otomatik okuma yardimci sinifi.
 * SIFIR EMOJI KURALI: Hicbir emoji veya ozel sembol kullanilmaz.
 */
object PhoneUtils {

    /**
     * Telefon numarasini Turkiye standart GSM formatina (905xxxxxxxxx - 12 hane) donusturur.
     */
    fun normalizePhoneNumber(rawInput: String): String {
        val digitsOnly = rawInput.filter { it.isDigit() }
        return when {
            digitsOnly.startsWith("0090") && digitsOnly.length == 14 -> {
                digitsOnly.substring(2)
            }
            digitsOnly.startsWith("0") && digitsOnly.length == 11 -> {
                "9" + digitsOnly
            }
            digitsOnly.startsWith("5") && digitsOnly.length == 10 -> {
                "90$digitsOnly"
            }
            digitsOnly.startsWith("90") && digitsOnly.length == 12 -> {
                digitsOnly
            }
            else -> {
                digitsOnly
            }
        }
    }

    /**
     * Numaranin 12 haneli ve 905 ile baslayan gecerli bir GSM numarasi olup olmadigini kontrol eder.
     */
    fun isValidTurkishMobile(normalizedPhone: String): Boolean {
        val regex = Regex("""^905\d{9}$""")
        return regex.matches(normalizedPhone)
    }

    /**
     * Okunabilir arayuz gosterimi (Ornek: +90 (532) 123 45 67).
     */
    fun formatForDisplay(rawInput: String): String {
        val normalized = normalizePhoneNumber(rawInput)
        if (normalized.length == 12 && normalized.startsWith("905")) {
            val op = normalized.substring(2, 5)
            val p1 = normalized.substring(5, 8)
            val p2 = normalized.substring(8, 10)
            val p3 = normalized.substring(10, 12)
            return "+90 ($op) $p1 $p2 $p3"
        }
        return rawInput
    }

    /**
     * SIM karttan telefon numarasini otomatik olarak okur.
     */
    @SuppressLint("HardwareIds", "MissingPermission")
    fun detectDevicePhoneNumber(context: Context): String? {
        val hasPhoneNumbersPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_NUMBERS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

        val hasPhoneStatePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPhoneNumbersPermission && !hasPhoneStatePermission) {
            return null
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val subscriptionManager =
                    context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                if (subscriptionManager != null) {
                    val defaultSubId = SubscriptionManager.getDefaultSubscriptionId()
                    if (defaultSubId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                        val number = subscriptionManager.getPhoneNumber(defaultSubId)
                        if (!number.isNullOrBlank()) {
                            val normalized = normalizePhoneNumber(number)
                            if (isValidTurkishMobile(normalized)) {
                                return normalized
                            }
                        }
                    }
                }
            }

            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val line1 = telephonyManager?.line1Number
            if (!line1.isNullOrBlank()) {
                val normalized = normalizePhoneNumber(line1)
                if (isValidTurkishMobile(normalized)) {
                    return normalized
                }
            }
        } catch (_: Exception) {
            // SIM kart veya operator kisiti
        }

        return null
    }
}
