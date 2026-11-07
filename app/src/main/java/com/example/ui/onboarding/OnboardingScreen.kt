package com.example.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.util.PhoneUtils
import kotlin.math.PI
import kotlin.math.sin

enum class OnboardingStepType {
    WELCOME,
    PHONE_PERMISSION,
    PHONE_NUMBER
}

/**
 * Modern Çok Adımlı Onboarding Akışı (Material 3 Dinamik Temalı)
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    initialPhoneNumber: String = "",
    onGetStartedClick: () -> Unit = {},
    onComplete: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    val activeSteps = remember {
        listOf(
            OnboardingStepType.WELCOME,
            OnboardingStepType.PHONE_PERMISSION,
            OnboardingStepType.PHONE_NUMBER
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }

    // 10 basamaklı telefon numarasını ayıkla (başta '0' hariç)
    var phoneDigits by remember {
        mutableStateOf(extractTenDigits(initialPhoneNumber))
    }

    // Geri tuşu kontrolü
    BackHandler(enabled = currentStepIndex > 0) {
        currentStepIndex--
    }

    // Renk Paleti (MaterialTheme.colorScheme Rollerinden Alınır)
    val backgroundColor = MaterialTheme.colorScheme.background
    val accentBlue = MaterialTheme.colorScheme.primary
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val buttonText = MaterialTheme.colorScheme.onPrimary
    val progressTrack = MaterialTheme.colorScheme.outlineVariant
    val darkButtonBg = MaterialTheme.colorScheme.surfaceContainerHigh

    // Dinamik Progress
    val targetProgress = (currentStepIndex + 1).toFloat() / activeSteps.size.toFloat()

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    // Telefon İzinleri Launcher'ı
    val phonePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val detected = PhoneUtils.detectDevicePhoneNumber(context)
        if (!detected.isNullOrBlank()) {
            phoneDigits = extractTenDigits(detected)
        }
        if (currentStepIndex < activeSteps.lastIndex) {
            currentStepIndex++
        }
    }

    // Numara Belirleme adımına geçildiğinde numara boş ise SIM'den okumayı dene
    LaunchedEffect(currentStepIndex) {
        if (activeSteps[currentStepIndex] == OnboardingStepType.PHONE_NUMBER && phoneDigits.isBlank()) {
            val detected = PhoneUtils.detectDevicePhoneNumber(context)
            if (!detected.isNullOrBlank()) {
                phoneDigits = extractTenDigits(detected)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Üst Bölüm: Dinamik Hareketli Sinüs Eğrisi Progress Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            UndulatingProgressBar(
                progress = animatedProgress,
                waveColor = accentBlue,
                trackColor = progressTrack,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 2. Çok Adımlı Geçiş Alanı (AnimatedContent)
        AnimatedContent(
            targetState = currentStepIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "step_transition",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { stepIndex ->
            when (activeSteps[stepIndex]) {
                OnboardingStepType.WELCOME -> {
                    StepWelcome(
                        accentBlue = accentBlue,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        buttonText = buttonText,
                        onNext = {
                            onGetStartedClick()
                            if (currentStepIndex < activeSteps.lastIndex) {
                                currentStepIndex++
                            }
                        }
                    )
                }
                OnboardingStepType.PHONE_PERMISSION -> {
                    StepPhonePermission(
                        accentBlue = accentBlue,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        buttonText = buttonText,
                        darkButtonBg = darkButtonBg,
                        onBack = {
                            if (currentStepIndex > 0) currentStepIndex--
                        },
                        onSkip = {
                            if (currentStepIndex < activeSteps.lastIndex) {
                                currentStepIndex++
                            }
                        },
                        onGrantPermission = {
                            val perms = mutableListOf<String>()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                perms.add(Manifest.permission.READ_PHONE_NUMBERS)
                            }
                            perms.add(Manifest.permission.READ_PHONE_STATE)
                            phonePermissionLauncher.launch(perms.toTypedArray())
                        }
                    )
                }
                OnboardingStepType.PHONE_NUMBER -> {
                    StepPhoneNumberDetermination(
                        digits = phoneDigits,
                        accentBlue = accentBlue,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        buttonText = buttonText,
                        darkButtonBg = darkButtonBg,
                        onDigitsChanged = { phoneDigits = it },
                        onBack = {
                            if (currentStepIndex > 0) currentStepIndex--
                        },
                        onAutoFill = {
                            val detected = PhoneUtils.detectDevicePhoneNumber(context)
                            if (!detected.isNullOrBlank()) {
                                phoneDigits = extractTenDigits(detected)
                            }
                        },
                        onFinish = {
                            val normalized = PhoneUtils.normalizePhoneNumber("0$phoneDigits")
                            onComplete?.invoke(normalized)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepWelcome(
    accentBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    buttonText: Color,
    onNext: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.phone_mockup),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("onboarding_phone_mockup")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-44).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Fuck KTB",
                color = textPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kütüphane Wi-Fi ağına saniyeler içinde, SMS bekleme çilesi olmadan otomatik ve güvenli bir şekilde bağlanın.",
                color = textSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("onboarding_description")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("get_started_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentBlue,
                    contentColor = buttonText
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Başla",
                        color = buttonText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = buttonText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPhonePermission(
    accentBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    buttonText: Color,
    darkButtonBg: Color,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onGrantPermission: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.phone_mockup3),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("onboarding_phone_mockup3")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-40).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Telefona Erişim",
                color = textPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step3_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "FKTB, Telefon numaranızı siz daha girmeden doldurur. Sadece ilk açılışta bu izni ister.",
                color = textSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("step3_description")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSkip()
                },
                shape = CircleShape,
                color = darkButtonBg,
                modifier = Modifier.testTag("step3_skip_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Şimdilik atla",
                        color = textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack()
                    },
                    shape = CircleShape,
                    color = darkButtonBg,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("step3_back_button")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onGrantPermission()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("step3_grant_permission_button"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentBlue,
                        contentColor = buttonText
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "İzin Veriyorum",
                            color = buttonText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = buttonText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepPhoneNumberDetermination(
    digits: String,
    accentBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    buttonText: Color,
    darkButtonBg: Color,
    onDigitsChanged: (String) -> Unit,
    onBack: () -> Unit,
    onAutoFill: () -> Unit,
    onFinish: () -> Unit
) {
    val isValid = digits.length == 10
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(darkButtonBg, CircleShape)
                    .testTag("step4_phone_icon"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = accentBlue,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Telefon Numaranı Ekle",
                color = textPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("step4_title")
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Başlarken telefon numaranızı gözden geçirmeniz gerekiyor.",
                color = textSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("step4_description")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(66.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp)
                    .testTag("step4_phone_input_box"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(darkButtonBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SimCard,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Telefon Numarası",
                            color = textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = buildMaskedPhoneAnnotatedString(
                                digits = digits,
                                activeColor = MaterialTheme.colorScheme.onSurface,
                                placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            ),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.5.sp,
                            letterSpacing = 0.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier.testTag("step4_phone_input")
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = !isValid,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAutoFill()
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .size(66.dp)
                        .testTag("step4_auto_fill_button")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.SimCard,
                            contentDescription = "Numarayı Otomatik Çek",
                            tint = textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = accentBlue,
                            modifier = Modifier
                                .size(13.dp)
                                .offset(x = 8.dp, y = 7.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onBack()
                },
                shape = CircleShape,
                color = darkButtonBg,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("step4_back_button")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onFinish()
                },
                enabled = isValid,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("step4_finish_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentBlue,
                    contentColor = buttonText,
                    disabledContainerColor = accentBlue.copy(alpha = 0.35f),
                    disabledContentColor = buttonText.copy(alpha = 0.45f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Bitir",
                        color = if (isValid) buttonText else buttonText.copy(alpha = 0.5f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (isValid) buttonText else buttonText.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        NumericKeypad(
            darkButtonBg = darkButtonBg,
            textSecondary = textSecondary,
            onDigitClick = { digit ->
                if (digits.length < 10) {
                    onDigitsChanged(digits + digit)
                }
            },
            onBackspaceClick = {
                if (digits.isNotEmpty()) {
                    onDigitsChanged(digits.dropLast(1))
                }
            },
            onBackspaceLongClick = {
                onDigitsChanged("")
            },
            modifier = Modifier
                .padding(bottom = 6.dp)
                .testTag("step4_numeric_keypad")
        )
    }
}

private data class KeypadItem(
    val digit: String = "",
    val letters: String = "",
    val isBackspace: Boolean = false,
    val isEmpty: Boolean = false
)

private val keypadRows = listOf(
    listOf(
        KeypadItem(digit = "1", letters = ""),
        KeypadItem(digit = "2", letters = "ABC"),
        KeypadItem(digit = "3", letters = "DEF")
    ),
    listOf(
        KeypadItem(digit = "4", letters = "GHI"),
        KeypadItem(digit = "5", letters = "JKL"),
        KeypadItem(digit = "6", letters = "MNO")
    ),
    listOf(
        KeypadItem(digit = "7", letters = "PQRS"),
        KeypadItem(digit = "8", letters = "TUV"),
        KeypadItem(digit = "9", letters = "WXYZ")
    ),
    listOf(
        KeypadItem(isEmpty = true),
        KeypadItem(digit = "0", letters = "+"),
        KeypadItem(isBackspace = true)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NumericKeypad(
    darkButtonBg: Color,
    textSecondary: Color,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onBackspaceLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keypadRows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    when {
                        item.isEmpty -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            )
                        }
                        item.isBackspace -> {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = darkButtonBg,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .combinedClickable(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onBackspaceClick()
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onBackspaceLongClick()
                                        }
                                    )
                                    .testTag("keypad_backspace")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Sil",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = darkButtonBg,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDigitClick(item.digit)
                                    }
                                    .testTag("keypad_key_${item.digit}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = item.digit,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (item.letters.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = item.letters,
                                            color = textSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun buildMaskedPhoneAnnotatedString(
    digits: String,
    activeColor: Color,
    placeholderColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = activeColor,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append("0 ")
        }

        for (i in 0 until 10) {
            if (i == 3 || i == 6 || i == 8) {
                val spaceColor = if (i <= digits.length) activeColor else placeholderColor
                withStyle(
                    SpanStyle(
                        color = spaceColor,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append(" ")
                }
            }

            if (i < digits.length) {
                withStyle(
                    SpanStyle(
                        color = activeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(digits[i])
                }
            } else {
                withStyle(
                    SpanStyle(
                        color = placeholderColor,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("0")
                }
            }
        }
    }
}

private fun extractTenDigits(rawInput: String): String {
    val digitsOnly = rawInput.filter { it.isDigit() }
    return when {
        digitsOnly.startsWith("905") && digitsOnly.length == 12 -> digitsOnly.substring(2)
        digitsOnly.startsWith("05") && digitsOnly.length == 11 -> digitsOnly.substring(1)
        digitsOnly.startsWith("5") && digitsOnly.length == 10 -> digitsOnly
        digitsOnly.length >= 10 -> digitsOnly.takeLast(10)
        else -> digitsOnly
    }
}

@Composable
fun UndulatingProgressBar(
    progress: Float,
    waveColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_progress_transition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp)
            .testTag("undulating_progress_bar")
    ) {
        val totalWidth = size.width
        val midY = size.height / 2f
        val trackStrokeWidth = 2.dp.toPx()
        val dotRadius = 2.5.dp.toPx()

        drawLine(
            color = trackColor,
            start = Offset(x = 0f, y = midY),
            end = Offset(x = totalWidth - (dotRadius * 2.5f), y = midY),
            strokeWidth = trackStrokeWidth,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = trackColor,
            radius = dotRadius,
            center = Offset(x = totalWidth - dotRadius, y = midY)
        )

        val clampedProgress = progress.coerceIn(0f, 1f)
        val activeWidth = totalWidth * clampedProgress

        if (activeWidth > 0f) {
            val path = Path()
            path.moveTo(0f, midY)

            val amplitude = 3.2.dp.toPx()
            val wavelength = (totalWidth * 0.25f) / 2.2f
            val frequency = (2 * PI / wavelength).toFloat()

            var x = 0f
            val step = 1.5f
            while (x <= activeWidth) {
                val normalizedX = (x / activeWidth).coerceIn(0f, 1f)
                val window = sin(PI * normalizedX).toFloat()
                val y = midY + amplitude * window * sin(frequency * x - phase)
                path.lineTo(x, y)
                x += step
            }

            drawPath(
                path = path,
                color = waveColor,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun OnboardingScreenPreview() {
    OnboardingScreen()
}
