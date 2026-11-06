package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.main.MainScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PortalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PortalViewModel by viewModels()

    fun triggerSmsConsent() {
        SmsCodeSource.startSmsUserConsent(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SmsCodeSource.init(this)
        enableEdgeToEdge()
        setContent {
            val userPrefs by viewModel.userPreferences.collectAsState()
            MyApplicationTheme(
                themeMode = userPrefs.themeMode,
                colorPalette = userPrefs.colorPalette,
                isAmoledMode = userPrefs.isAmoledMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        SmsCodeSource.start(this)
    }

    override fun onStop() {
        super.onStop()
        SmsCodeSource.stop(this)
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
