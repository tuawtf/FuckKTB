package com.example

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.main.MainScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PortalViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class MainActivity : ComponentActivity() {

    private val viewModel: PortalViewModel by viewModels()

    /**
     * SMS User Consent API sonucunu dinleyen launcher.
     * Kullanıcı sistem onay diyaloğunda "İzin Ver" derse (RESULT_OK) mesaj metnini alır,
     * kodu regex ile ayıklar ve SmsReceiver reaktif akışına iletir.
     */
    private val consentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            if (!message.isNullOrBlank()) {
                val code = SmsReceiver.extractVerificationCode(null, message)
                if (code != null) {
                    SmsReceiver.simulateSmsArrival(SmsReceiver.TARGET_SENDER, message, code)
                }
            }
        }
    }

    /**
     * Google Play Services SMS_RETRIEVED_ACTION yayınını dinleyen receiver.
     */
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
                                consentLauncher.launch(consentIntent)
                            } catch (e: ActivityNotFoundException) {
                                Log.e("MainActivity", "Consent activity başlatılamadı: ${e.message}")
                            }
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.d("MainActivity", "SMS User Consent zaman aşımına uğradı")
                    }
                }
            }
        }
    }

    /**
     * SMS User Consent API dinleyicisini başlatır.
     * SMS talebi gönderilmeden hemen önce çağrılmalıdır.
     */
    fun startSmsConsent() {
        SmsRetriever.getClient(this).startSmsUserConsent(null)
            .addOnSuccessListener {
                Log.d("MainActivity", "SMS User Consent dinleyicisi başlatıldı")
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "SMS User Consent dinleyicisi başlatılamadı: ${e.message}")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(
            this,
            consentReceiver,
            intentFilter,
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(consentReceiver)
        } catch (_: Exception) {
        }
    }
}

@Composable
fun AppRoot(viewModel: PortalViewModel) {
    val userPrefs by viewModel.userPreferences.collectAsState()

    Crossfade(
        targetState = userPrefs.isOnboardingCompleted,
        animationSpec = tween(400),
        label = "root_screen_transition"
    ) { isCompleted ->
        if (!isCompleted) {
            OnboardingScreen(
                initialPhoneNumber = userPrefs.phoneNumber,
                onComplete = { phoneNumber ->
                    viewModel.completeOnboarding(phoneNumber)
                }
            )
        } else {
            MainScreen(
                viewModel = viewModel,
                onResetOnboarding = {
                    viewModel.resetOnboarding()
                }
            )
        }
    }
}
