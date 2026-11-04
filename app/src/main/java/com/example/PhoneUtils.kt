package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

/**
 * Telefon numarası formatlama, doğrulama, cihazdan otomatik okuma
 * ve SharedPreferences üzerinde kalıcı saklama yardımcı sınıfı.
 */
object PhoneUtils {

    private const val PREFS_NAME = "ktb_wifi_portal_prefs"
    private const val KEY_SAVED_PHONE = "saved_phone_number"

    /**
     * Telefon numarasını Türkiye standart kütüphane portalı formatına (905xxxxxxxxx - 12 hane) dönüştürür.
     * Örnekler:
     * - "0532 123 45 67" -> "905321234567"
     * - "+905321234567"  -> "905321234567"
     * - "5321234567"     -> "905321234567"
     * - "905321234567"   -> "905321234567"
     */
    fun normalizePhoneNumber(rawInput: String): String {
        val digitsOnly = rawInput.filter { it.isDigit() }
        return when {
            // "00905xxxxxxxxx" formatı
            digitsOnly.startsWith("0090") && digitsOnly.length == 14 -> {
                digitsOnly.substring(2)
            }
            // "05xxxxxxxxx" (11 haneli standart Türkiye GSM)
            digitsOnly.startsWith("0") && digitsOnly.length == 11 -> {
                "9" + digitsOnly
            }
            // "5xxxxxxxxx" (10 haneli başında 0 olmayan)
            digitsOnly.startsWith("5") && digitsOnly.length == 10 -> {
                "90$digitsOnly"
            }
            // "905xxxxxxxxx" (12 haneli hedef format)
            digitsOnly.startsWith("90") && digitsOnly.length == 12 -> {
                digitsOnly
            }
            else -> {
                // Diğer durumlarda sadece rakamları korur
                digitsOnly
            }
        }
    }

    /**
     * Numaranın 12 haneli ve "905" ile başlayan geçerli bir GSM numarası olup olmadığını kontrol eder.
     */
    fun isValidTurkishMobile(normalizedPhone: String): Boolean {
        val regex = Regex("""^905\d{9}$""")
        return regex.matches(normalizedPhone)
    }

    /**
     * Kullanıcı arayüzünde okunabilir gösterim (örn: +90 (532) 123 45 67).
     */
    fun formatForDisplay(rawInput: String): String {
        val normalized = normalizePhoneNumber(rawInput)
        if (normalized.length == 12 && normalized.startsWith("905")) {
            val op = normalized.substring(2, 5)     // 532
            val p1 = normalized.substring(5, 8)     // 123
            val p2 = normalized.substring(8, 10)    // 45
            val p3 = normalized.substring(10, 12)   // 67
            return "+90 ($op) $p1 $p2 $p3"
        }
        return rawInput
    }

    /**
     * Cihazdaki SIM karttan telefon numarasını otomatik olarak çekmeyi dener.
     * READ_PHONE_NUMBERS veya READ_PHONE_STATE izni gerektirir.
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
            // Android 13+ (API 33) ve üstü için SubscriptionManager kullanımı
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

            // Geriye dönük uyumluluk: TelephonyManager line1Number
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
            // SIM kart veya operatör numara paylaşımını kısıtlamış olabilir
        }

        return null
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun savePhoneNumber(context: Context, phoneNumber: String) {
        val normalized = normalizePhoneNumber(phoneNumber)
        getPrefs(context).edit().putString(KEY_SAVED_PHONE, normalized).apply()
    }

    fun getSavedPhoneNumber(context: Context): String? {
        val saved = getPrefs(context).getString(KEY_SAVED_PHONE, null)
        return if (!saved.isNullOrBlank()) normalizePhoneNumber(saved) else null
    }
}
