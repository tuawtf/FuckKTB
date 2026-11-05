package com.example.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainActivity
import com.example.ui.navigation.MainTab
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusInfo
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.LogLevel
import com.example.ui.viewmodel.PortalStep
import com.example.ui.viewmodel.PortalViewModel
import com.example.util.PhoneUtils

@Composable
fun MainScreen(
    viewModel: PortalViewModel,
    onResetOnboarding: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.CONNECT) }
    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        modifier = Modifier.testTag("tab_" + tab.name.lowercase())
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(250)).togetherWith(fadeOut(tween(200))) },
                label = "main_tab_content"
            ) { tab ->
                when (tab) {
                    MainTab.CONNECT -> ConnectTabContent(viewModel = viewModel)
                    MainTab.LOGS -> LogsTabContent(viewModel = viewModel)
                    MainTab.SETTINGS -> SettingsTabContent(
                        viewModel = viewModel,
                        registeredPhone = userPrefs.phoneNumber,
                        isAutoConnectEnabled = userPrefs.isAutoConnectEnabled,
                        onResetOnboarding = onResetOnboarding
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. SEKME: BAGLAN (HOME) - Ferah, modern, gereksiz kalabaliktan uzak
// -------------------------------------------------------------
@Composable
private fun ConnectTabContent(viewModel: PortalViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showManualCodeDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "beacon")
    val beaconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconAlpha"
    )

    val cardColor by animateColorAsState(
        targetValue = when (uiState.step) {
            PortalStep.SUCCESS -> StatusSuccess.copy(alpha = 0.12f)
            PortalStep.ERROR -> StatusError.copy(alpha = 0.12f)
            PortalStep.WAITING_SMS -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
            PortalStep.IDLE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        },
        label = "cardColor"
    )

    val statusIconColor = when (uiState.step) {
        PortalStep.SUCCESS -> StatusSuccess
        PortalStep.ERROR -> StatusError
        PortalStep.WAITING_SMS -> MaterialTheme.colorScheme.primary
        PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondary
        PortalStep.IDLE -> MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Ust Bilgi: Bagli Ag ve Telefon Numarasi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (uiState.isWifiConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = if (uiState.isWifiConnected) StatusSuccess else StatusWarning,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = uiState.wifiSsid ?: if (uiState.isWifiConnected) "Wi-Fi Aktif" else "Wi-Fi Bagli Degil",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            IconButton(
                onClick = { viewModel.checkWifiState() },
                modifier = Modifier.size(32.dp).testTag("refresh_wifi_state_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Yenile",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Merkez: Durum Karti ve Buyuk Eylem Butonu
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dinamik Durum Karti
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("connect_status_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = uiState.statusMessage,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )

                    if (uiState.step == PortalStep.WAITING_SMS) {
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
                                text = "Kalan Sure: " + uiState.countdownSeconds + " sn (KUL.TUR.BKN)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (uiState.countdownSeconds / 120f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = StatusError,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val context = LocalContext.current
            // BUYUK MERKEZI BAGLAN BUTONU (FAB Benzeri Dikkat Cekici Card)
            Surface(
                onClick = {
                    if (uiState.step == PortalStep.IDLE || uiState.step == PortalStep.ERROR || uiState.step == PortalStep.SUCCESS) {
                        val activity = context as? MainActivity
                        activity?.triggerSmsConsent()
                        viewModel.startConnectionFlow {
                            activity?.triggerSmsConsent()
                        }
                    }
                },
                enabled = uiState.step != PortalStep.REQUESTING_SMS && uiState.step != PortalStep.VALIDATING,
                shape = CircleShape,
                color = when (uiState.step) {
                    PortalStep.SUCCESS -> StatusSuccess
                    PortalStep.WAITING_SMS -> MaterialTheme.colorScheme.primary
                    PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.primary
                },
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .size(160.dp)
                    .testTag("big_connect_button")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (uiState.step) {
                            PortalStep.REQUESTING_SMS, PortalStep.VALIDATING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(44.dp),
                                    color = Color.White,
                                    strokeWidth = 3.5.dp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (uiState.step == PortalStep.REQUESTING_SMS) "SMS Isteniyor" else "Giris Yapiliyor",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            PortalStep.WAITING_SMS -> {
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .alpha(beaconAlpha)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "SMS Bekleniyor",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            PortalStep.SUCCESS -> {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Bagli",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            PortalStep.ERROR -> {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Yeniden Dene",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            PortalStep.IDLE -> {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Baglan",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Alt Yardimci Butonlar
            AnimatedVisibility(
                visible = uiState.step == PortalStep.WAITING_SMS || uiState.step == PortalStep.ERROR,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showManualCodeDialog = true },
                        modifier = Modifier.testTag("manual_code_dialog_button")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kodu Elle Gir", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetState() },
                        modifier = Modifier.testTag("cancel_connect_button")
                    ) {
                        Text("Iptal", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Alt Bilgi Notu
        Text(
            text = "Kayitli Numara: " + PhoneUtils.formatForDisplay(uiState.phoneNumber),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showManualCodeDialog) {
        ManualCodeDialog(
            currentCode = uiState.verificationCode,
            onDismiss = { showManualCodeDialog = false },
            onValidate = { code ->
                viewModel.validateSmsToken(code)
                showManualCodeDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// 2. SEKME: GUNLUK (LOGS)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogsTabContent(viewModel: PortalViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.logs.size) {
        if (uiState.logs.isNotEmpty()) {
            listState.animateScrollToItem(uiState.logs.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Aktivite Gunlugu",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            val text = uiState.logs.joinToString("\n") { "[" + it.timestamp + "] " + it.message }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("KTB Logs", text))
                            Toast.makeText(context, "Gunluk panoya kopyalandi", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("copy_logs_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala")
                    }

                    IconButton(
                        onClick = { viewModel.clearLogs() },
                        modifier = Modifier.testTag("clear_logs_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Temizle")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (uiState.logs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Henuz aktivite kaydi bulunmuyor.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.logs, key = { it.id }) { log ->
                        val icon = when (log.level) {
                            LogLevel.SUCCESS -> Icons.Default.CheckCircle
                            LogLevel.ERROR -> Icons.Default.Error
                            LogLevel.WARNING -> Icons.Default.Warning
                            LogLevel.INFO -> Icons.Default.Info
                        }
                        val tint = when (log.level) {
                            LogLevel.SUCCESS -> StatusSuccess
                            LogLevel.ERROR -> StatusError
                            LogLevel.WARNING -> StatusWarning
                            LogLevel.INFO -> MaterialTheme.colorScheme.primary
                        }

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = tint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.level.name,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = tint
                                        )
                                        Text(
                                            text = log.timestamp,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = log.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Test Simulasyon Butonu
            OutlinedButton(
                onClick = { viewModel.simulateSmsArrival() },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulate_sms_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ornek SMS Gonder (Test Simulasyonu)")
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SEKME: AYARLAR (SETTINGS)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTabContent(
    viewModel: PortalViewModel,
    registeredPhone: String,
    isAutoConnectEnabled: Boolean,
    onResetOnboarding: () -> Unit
) {
    var showEditPhoneDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ayarlar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Telefon Numarasi Ayari
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Kayitli Telefon Numarasi",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (registeredPhone.isNotBlank()) PhoneUtils.formatForDisplay(registeredPhone) else "Numara tanimlanmamis",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            FilledTonalButton(
                                onClick = { showEditPhoneDialog = true },
                                modifier = Modifier.testTag("edit_phone_button")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Duzenle", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // 2. Otomasyon Ayarlari
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Wi-Fi Algilandiginda Baglan",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Kutuphane agina baglanildiginda portali otomatik acar.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isAutoConnectEnabled,
                            onCheckedChange = { viewModel.setAutoConnect(it) },
                            modifier = Modifier.testTag("auto_connect_switch")
                        )
                    }
                }
            }

            // 3. Kurulum Sihirbazini Yeniden Baslat
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kurulum Sihirbazi",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Ilk acilis ve tanitim ekranlarini yeniden baslatir.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.resetOnboarding()
                                onResetOnboarding()
                            },
                            modifier = Modifier.testTag("restart_onboarding_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sifirla", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // 4. Hakkinda Bolumu
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Hakkinda & Bilgi",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "KTB Kutuphane Wi-Fi Giris Yardimcisi - Surum 1.0.0",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "T.C. Kultur ve Turizm Bakanligi kutuphane aglarindaki captive portal SMS dogrulama surecini kolaylastirmak uzere gelistirilmis acik kaynakli bir aractir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "Guvenlik: Telefon numaraniz ve dogrulama kodlariniz yalnizca cihazinizdaki yerel DataStore uzerinde saklanir. Hicbir ucuncu taraf sunucuya veri aktarilmaz.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showEditPhoneDialog) {
        EditPhoneDialog(
            currentPhone = registeredPhone,
            onDismiss = { showEditPhoneDialog = false },
            onSave = { newPhone ->
                viewModel.updatePhoneNumber(newPhone)
                showEditPhoneDialog = false
            }
        )
    }
}

@Composable
private fun EditPhoneDialog(
    currentPhone: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var phoneInput by remember { mutableStateOf(currentPhone) }
    val isValid = PhoneUtils.isValidTurkishMobile(phoneInput)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Numarayi Guncelle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Giris yaparken kullanilacak yeni telefon numaranizi giriniz.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = PhoneUtils.normalizePhoneNumber(it) },
                    label = { Text("GSM No") },
                    placeholder = { Text("05xx xxx xx xx") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("edit_phone_input")
                )
                Text(
                    text = "Format: " + PhoneUtils.formatForDisplay(phoneInput),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isValid) StatusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(phoneInput) },
                enabled = isValid
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgec")
            }
        }
    )
}

@Composable
private fun ManualCodeDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onValidate: (String) -> Unit
) {
    var codeInput by remember { mutableStateOf(currentCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dogrulama Kodu Girisi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SMS ile gelen 6 haneli kodu elle girip baglantiyi tamamlayabilirsiniz.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) codeInput = it },
                    label = { Text("6 Haneli Kod") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("manual_code_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onValidate(codeInput) },
                enabled = codeInput.length == 6
            ) {
                Text("Dogrula ve Giris Yap")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgec")
            }
        }
    )
}
