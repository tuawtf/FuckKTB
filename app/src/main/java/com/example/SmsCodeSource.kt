package com.example

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Google Play Services SMS User Consent API kullanarak kullanicidan onay alir
 * ve dogrulama kodunu SmsCodeBus veri yoluna aktarir (TEK SÜRÜM / SIFIR SMS İZNİ).
 *
 * Guvenlik geregi SMS icerigi ve dogrulama kodu loglanmaz.
 * SIFIR EMOJI KURALI: Kod, log ve mesajlarda emoji kullanilmaz.
 */
object SmsCodeSource {

    private var consentLauncher: ActivityResultLauncher<Intent>? = null
    private var isReceiverRegistered = false

    private val consentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
                val extras = intent.extras ?: return
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    extras.getParcelable(SmsRetriever.EXTRA_STATUS) as? Status
                }

                when (status?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val consentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT) as? Intent
                        }
                        if (consentIntent != null) {
                            try {
                                consentLauncher?.launch(consentIntent)
                            } catch (e: ActivityNotFoundException) {
                                Log.e("SmsCodeSource", "SMS User Consent aktivitesi baslatilamadi: ${e.message}")
                            }
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.d("SmsCodeSource", "SMS User Consent zaman asimina ugradi.")
                    }
                }
            }
        }
    }

    fun init(activity: ComponentActivity) {
        consentLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                if (!message.isNullOrBlank()) {
                    val code = SmsCodeBus.extractVerificationCode(null, message)
                    if (code != null) {
                        Log.d("SmsCodeSource", "SMS User Consent dogrulama kodu basariyla yakalandi.")
                        SmsCodeBus.emitCode(SmsCodeBus.TARGET_SENDER, message, code)
                    }
                }
            }
        }
    }

    fun start(activity: ComponentActivity) {
        if (!isReceiverRegistered) {
            val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            ContextCompat.registerReceiver(
                activity,
                consentReceiver,
                intentFilter,
                SmsRetriever.SEND_PERMISSION,
                null,
                ContextCompat.RECEIVER_EXPORTED
            )
            isReceiverRegistered = true
        }
        startSmsUserConsent(activity)
    }

    fun stop(activity: ComponentActivity) {
        if (isReceiverRegistered) {
            try {
                activity.unregisterReceiver(consentReceiver)
            } catch (_: Exception) {
            }
            isReceiverRegistered = false
        }
    }

    fun startSmsUserConsent(activity: ComponentActivity) {
        try {
            SmsRetriever.getClient(activity).startSmsUserConsent(null)
                .addOnSuccessListener {
                    Log.d("SmsCodeSource", "startSmsUserConsent baslatildi.")
                }
                .addOnFailureListener { e ->
                    Log.e("SmsCodeSource", "startSmsUserConsent baslatilamadi: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("SmsCodeSource", "startSmsUserConsent cagri hatasi: ${e.message}")
        }
    }
}
