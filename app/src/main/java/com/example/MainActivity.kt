package com.example

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KtbPortalApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KtbPortalApp(viewModel: PortalViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showSimulationDialog by remember { mutableStateOf(false) }

    // SMS ve Telefon İzinleri İsteme Launcher'ı
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.RECEIVE_SMS] == true
        val phoneNumbersGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions[Manifest.permission.READ_PHONE_NUMBERS] == true
        } else false
        val phoneStateGranted = permissions[Manifest.permission.READ_PHONE_STATE] == true

        if (smsGranted) {
            Toast.makeText(context, "SMS izni verildi, kodlar otomatik yakalanacak.", Toast.LENGTH_SHORT).show()
        }

        if (phoneNumbersGranted || phoneStateGranted) {
            viewModel.tryAutoDetectPhoneNumber(context)
        }
    }

    // İlk açılışta SMS iznini ve telefon iznini kontrol et
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECEIVE_SMS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_PHONE_NUMBERS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // İzinler zaten varsa ve numara boşsa numarayı otomatik çekmeyi dene
            if (uiState.phoneNumber.isBlank()) {
                viewModel.tryAutoDetectPhoneNumber(context)
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "KTB Kütüphane Wi-Fi",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Otomatik Captive Portal Girişi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSimulationDialog = true },
                        modifier = Modifier.testTag("simulation_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Test Simülasyonu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Ağ ve Wi-Fi Bilgisi Kartı
            item {
                WifiStatusCard(
                    isWifiConnected = uiState.isWifiConnected,
                    ssid = uiState.wifiSsid,
                    onRefresh = { viewModel.checkWifiState() }
                )
            }

            // 2. Ana Durum & Hero Kartı
            item {
                HeroStatusCard(
                    step = uiState.step,
                    statusMessage = uiState.statusMessage,
                    errorMessage = uiState.errorMessage,
                    countdownSeconds = uiState.countdownSeconds
                )
            }

            // 3. Adım İlerleme Göstergesi
            item {
                StepTimeline(currentStep = uiState.step)
            }

            // 4. Telefon Numarası Giriş Kartı
            item {
                PhoneNumberCard(
                    phoneNumber = uiState.phoneNumber,
                    onPhoneNumberChanged = { viewModel.onPhoneNumberChanged(it) },
                    onAutoDetect = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            viewModel.tryAutoDetectPhoneNumber(context)
                        } else {
                            val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                arrayOf(Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE)
                            } else {
                                arrayOf(Manifest.permission.READ_PHONE_STATE)
                            }
                            permissionLauncher.launch(perms)
                        }
                    },
                    isEnabled = uiState.step != PortalStep.REQUESTING_SMS && uiState.step != PortalStep.VALIDATING
                )
            }

            // 5. Doğrulama Kodu Kartı (SMS Kodu)
            item {
                VerificationCodeCard(
                    code = uiState.verificationCode,
                    onCodeChanged = { viewModel.onVerificationCodeChanged(it) },
                    onValidate = { viewModel.validateSmsToken() },
                    step = uiState.step,
                    isEnabled = uiState.step != PortalStep.REQUESTING_SMS && uiState.step != PortalStep.VALIDATING
                )
            }

            // 6. Ana Aksiyon Butonu
            item {
                ActionButtons(
                    step = uiState.step,
                    isValidPhone = PhoneUtils.isValidTurkishMobile(uiState.phoneNumber),
                    onConnect = { viewModel.startConnectionFlow() },
                    onReset = { viewModel.resetState() }
                )
            }

            // 7. Canlı Ağ ve Konsol Olay Günlüğü
            item {
                LogConsoleCard(
                    logs = uiState.logs,
                    onClear = { viewModel.clearLogs() },
                    onSimulateSms = { viewModel.simulateMockSms() }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showSimulationDialog) {
        SimulationDialog(
            onDismiss = { showSimulationDialog = false },
            onSend = { mockCode ->
                viewModel.simulateMockSms(mockCode)
                showSimulationDialog = false
            }
        )
    }
}

@Composable
fun WifiStatusCard(
    isWifiConnected: Boolean,
    ssid: String?,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isWifiConnected) StatusSuccess.copy(alpha = 0.15f)
                            else StatusWarning.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = "Wi-Fi Durumu",
                        tint = if (isWifiConnected) StatusSuccess else StatusWarning,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isWifiConnected) (ssid ?: "Wi-Fi Ağına Bağlı") else "Wi-Fi Bağlantısı Yok",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = if (isWifiConnected) "Captive portal: captive.kultur.gov.tr" else "Lütfen kütüphane Wi-Fi ağına bağlanın",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = onRefresh,
                modifier = Modifier.size(32.dp).testTag("refresh_wifi_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Yenile",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun HeroStatusCard(
    step: PortalStep,
    statusMessage: String,
    errorMessage: String?,
    countdownSeconds: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val cardColor by animateColorAsState(
        targetValue = when (step) {
            PortalStep.SUCCESS -> StatusSuccess.copy(alpha = 0.12f)
            PortalStep.ERROR -> StatusError.copy(alpha = 0.12f)
            PortalStep.WAITING_SMS -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
            PortalStep.IDLE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        },
        label = "cardColor"
    )

    val iconColor = when (step) {
        PortalStep.SUCCESS -> StatusSuccess
        PortalStep.ERROR -> StatusError
        PortalStep.WAITING_SMS -> MaterialTheme.colorScheme.primary
        PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondary
        PortalStep.IDLE -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_status_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(iconColor.copy(alpha = 0.3f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // İkon / Animasyon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = iconColor,
                            strokeWidth = 3.dp
                        )
                    }
                    PortalStep.WAITING_SMS -> {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS Bekleniyor",
                            tint = iconColor,
                            modifier = Modifier
                                .size(34.dp)
                                .alpha(alphaAnim)
                        )
                    }
                    PortalStep.SUCCESS -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Başarılı",
                            tint = iconColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    PortalStep.ERROR -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Hata",
                            tint = iconColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    PortalStep.IDLE -> {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Wi-Fi Giriş",
                            tint = iconColor,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }

            // Durum Başlığı
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            // Geri Sayım veya Hata Detayı
            if (step == PortalStep.WAITING_SMS) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "SMS bekleniyor: $countdownSeconds sn kaldı",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LinearProgressIndicator(
                    progress = { (countdownSeconds / 120f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusError,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun StepTimeline(currentStep: PortalStep) {
    val steps = listOf(
        "1. Numara",
        "2. SMS Talebi",
        "3. Kod Yakalama",
        "4. İnternet Aktif"
    )

    val activeIndex = when (currentStep) {
        PortalStep.IDLE -> 0
        PortalStep.REQUESTING_SMS -> 1
        PortalStep.WAITING_SMS -> 2
        PortalStep.VALIDATING -> 2
        PortalStep.SUCCESS -> 3
        PortalStep.ERROR -> 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val isPassed = index < activeIndex || currentStep == PortalStep.SUCCESS
            val isCurrent = index == activeIndex && currentStep != PortalStep.SUCCESS

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPassed -> StatusSuccess
                                isCurrent -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = if (isCurrent || isPassed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (index < activeIndex) StatusSuccess.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}

@Composable
fun PhoneNumberCard(
    phoneNumber: String,
    onPhoneNumberChanged: (String) -> Unit,
    onAutoDetect: () -> Unit,
    isEnabled: Boolean
) {
    val isValid = PhoneUtils.isValidTurkishMobile(phoneNumber)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Telefon Numarası",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                FilledTonalButton(
                    onClick = onAutoDetect,
                    enabled = isEnabled,
                    modifier = Modifier.height(32.dp).testTag("auto_detect_button"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                ) {
                    Text(text = "SIM'den Al", style = MaterialTheme.typography.labelSmall)
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChanged,
                enabled = isEnabled,
                label = { Text("GSM No (905xxxxxxxxx)") },
                placeholder = { Text("05xx xxx xx xx") },
                supportingText = {
                    Text(
                        text = if (phoneNumber.isNotBlank()) {
                            "Format: ${PhoneUtils.formatForDisplay(phoneNumber)} ${if (isValid) "✓" else "(12 haneli olmalı)"}"
                        } else {
                            "Numaranız cihazda kalıcı olarak saklanır."
                        },
                        color = if (isValid) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (phoneNumber.isNotBlank()) {
                        IconButton(onClick = { onPhoneNumberChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                leadingIcon = {
                    Text(
                        text = "+90",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                isError = phoneNumber.isNotBlank() && !isValid && phoneNumber.length >= 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_number_input")
            )
        }
    }
}

@Composable
fun VerificationCodeCard(
    code: String,
    onCodeChanged: (String) -> Unit,
    onValidate: () -> Unit,
    step: PortalStep,
    isEnabled: Boolean
) {
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Doğrulama Kodu (SMS)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            Text(
                text = "KUL.TUR.BKN tarafından gönderilen 6 haneli kod otomatik olarak yakalanır ve girilir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = onCodeChanged,
                    enabled = isEnabled,
                    placeholder = { Text("594586") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (code.length == 6) onValidate()
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("verification_code_input")
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onValidate()
                    },
                    enabled = isEnabled && code.length == 6,
                    modifier = Modifier.testTag("manual_validate_button")
                ) {
                    Text("Doğrula")
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    step: PortalStep,
    isValidPhone: Boolean,
    onConnect: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onConnect,
            enabled = isValidPhone && step != PortalStep.REQUESTING_SMS && step != PortalStep.VALIDATING,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("connect_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (step == PortalStep.SUCCESS) StatusSuccess else MaterialTheme.colorScheme.primary
            )
        ) {
            when (step) {
                PortalStep.REQUESTING_SMS -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("SMS İsteniyor...", fontWeight = FontWeight.Bold)
                }
                PortalStep.VALIDATING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Doğrulanıyor...", fontWeight = FontWeight.Bold)
                }
                PortalStep.WAITING_SMS -> {
                    Icon(Icons.Default.HourglassEmpty, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SMS Bekleniyor (Yeniden İste)", fontWeight = FontWeight.Bold)
                }
                PortalStep.SUCCESS -> {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bağlantı Aktif (Yeniden Bağlan)", fontWeight = FontWeight.Bold)
                }
                PortalStep.ERROR -> {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yeniden Dene", fontWeight = FontWeight.Bold)
                }
                PortalStep.IDLE -> {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SMS İste & Bağlan", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (step != PortalStep.IDLE) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("reset_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Akışı Sıfırla")
            }
        }
    }
}

@Composable
fun LogConsoleCard(
    logs: List<LogEntry>,
    onClear: () -> Unit,
    onSimulateSms: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Log eklendikçe en alta kaydır
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.lastIndex)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_console_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aktivite & Protokol Günlüğü",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val text = logs.joinToString("\n") { "[${it.timestamp}] ${it.message}" }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("KTB Logs", text))
                            Toast.makeText(context, "Loglar panoya kopyalandı", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp).testTag("copy_logs_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala", modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(32.dp).testTag("clear_logs_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Temizle", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Terminal Görünümü
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(8.dp)
            ) {
                if (logs.isEmpty()) {
                    Text(
                        text = "Henüz log kaydı bulunmuyor...",
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs, key = { it.id }) { log ->
                            val color = when (log.level) {
                                LogLevel.SUCCESS -> Color(0xFF34D399) // Emerald
                                LogLevel.ERROR -> Color(0xFFF87171)   // Red
                                LogLevel.WARNING -> Color(0xFFFBBF24) // Amber
                                LogLevel.INFO -> Color(0xFF93C5FD)    // Light Blue
                            }
                            Text(
                                text = "[${log.timestamp}] ${log.message}",
                                color = color,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Hızlı Test Butonu
            OutlinedButton(
                onClick = onSimulateSms,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_simulate_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Gelen KTB SMS'i Simüle Et (Kod: 594586)", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SimulationDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    var codeInput by remember { mutableStateOf("594586") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SMS Simülatörü") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Gerçek SMS ağ geçidi veya SIM kart olmadan test etmek için özel bir 6 haneli kod belirleyip simüle edebilirsiniz.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) codeInput = it },
                    label = { Text("6 Haneli Kod") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("simulation_code_input")
                )
                Text(
                    text = "Format: 'Dogrulama kodu: $codeInput Bu kodu kullanarak KTB Wifi hizmetinden yararlanabilirsiniz. B001'",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSend(codeInput.ifBlank { "594586" }) },
                enabled = codeInput.length == 6
            ) {
                Text("SMS'i Gönder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}
