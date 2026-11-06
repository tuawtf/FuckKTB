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
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.BuildConfig
import com.example.data.DownloadState
import com.example.data.GitHubAsset
import com.example.data.UpdateCheckResult
import com.example.data.UpdateManager
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.UnboundedFontFamily
import com.example.ui.theme.AppPalettes
import com.example.ui.theme.getPaletteAccent
import com.example.ui.viewmodel.LogLevel
import com.example.ui.viewmodel.PortalStep
import com.example.ui.viewmodel.PortalViewModel
import com.example.util.PhoneUtils
import com.example.ui.update.UpdateScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PortalViewModel,
    onResetOnboarding: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    var activeSettingsSubPage by remember { mutableStateOf<String?>(null) }
    var showUpdateBottomSheet by remember { mutableStateOf(false) }
    var hasAutoOpenedSheetOnLaunch by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()

    // Açılışta güncelleme kontrolü ayarı aktifse ve yeni sürüm bulunduysa paneli kullanıcıya göster (kullanıcı 'Şimdi Güncelle' demeden indirme başlamaz)
    LaunchedEffect(updateCheckState, userPrefs.checkUpdatesOnLaunch) {
        if (userPrefs.checkUpdatesOnLaunch && !hasAutoOpenedSheetOnLaunch) {
            if (updateCheckState is UpdateCheckResult.UpdateAvailable) {
                hasAutoOpenedSheetOnLaunch = true
                showUpdateBottomSheet = true
            }
        }
    }

    BackHandler(enabled = showUpdateBottomSheet) {
        showUpdateBottomSheet = false
    }

    BackHandler(enabled = !showUpdateBottomSheet && selectedTab == MainTab.SETTINGS && activeSettingsSubPage != null) {
        activeSettingsSubPage = null
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBarSection(
                title = if (selectedTab == MainTab.SETTINGS && activeSettingsSubPage != null) {
                    activeSettingsSubPage ?: selectedTab.title
                } else {
                    selectedTab.title
                },
                showBackButton = selectedTab == MainTab.SETTINGS && activeSettingsSubPage != null,
                onBackClick = { activeSettingsSubPage = null }
            )
        },
        bottomBar = {
            CustomBottomBar(
                selectedTab = selectedTab,
                onTabSelected = {
                    selectedTab = it
                    if (it != MainTab.SETTINGS) {
                        activeSettingsSubPage = null
                    }
                }
            )
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
                    MainTab.HOME -> ConnectTabContent(viewModel = viewModel)
                    MainTab.CONNECTIONS -> ConnectionsTabContent()
                    MainTab.LOGS -> LogsTabContent(viewModel = viewModel)
                    MainTab.SETTINGS -> SettingsTabContent(
                        activeSubPage = activeSettingsSubPage,
                        viewModel = viewModel,
                        onSelectSubPage = { activeSettingsSubPage = it },
                        onStartDownload = { asset, ver ->
                            viewModel.startApkDownload(asset, ver)
                            showUpdateBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    if (showUpdateBottomSheet) {
        UpdateDownloadBottomSheet(
            checkState = updateCheckState,
            downloadState = downloadState,
            onInstallApk = { file -> viewModel.installApk(file) },
            onRetryDownload = { asset, ver -> viewModel.startApkDownload(asset, ver) },
            onDismiss = { showUpdateBottomSheet = false }
        )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Toolbar: Günlük başlığı ve hızlı eylem butonları
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kayıtlar (${uiState.logs.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF8E95A5)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = {
                        val text = uiState.logs.joinToString("\n") { "[" + it.timestamp + "] " + it.message }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("KTB Logs", text))
                        Toast.makeText(context, "Günlük panoya kopyalandı", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp).testTag("copy_logs_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Kopyala", modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = { viewModel.clearLogs() },
                    modifier = Modifier.size(36.dp).testTag("clear_logs_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Temizle", modifier = Modifier.size(18.dp))
                }
            }
        }
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

// -------------------------------------------------------------
// 3. SEKME: AYARLAR (SETTINGS)
// -------------------------------------------------------------
@Composable
private fun SettingsTabContent(
    activeSubPage: String?,
    viewModel: PortalViewModel,
    onSelectSubPage: (String) -> Unit,
    onStartDownload: (GitHubAsset, String) -> Unit
) {
    AnimatedContent(
        targetState = activeSubPage,
        transitionSpec = {
            fadeIn(tween(220)).togetherWith(fadeOut(tween(180)))
        },
        label = "settings_subpage_anim"
    ) { subPage ->
        if (subPage == "Güncellemeler") {
            UpdatesSubPageContent(
                viewModel = viewModel,
                onStartDownload = onStartDownload
            )
        } else if (subPage == "Görünüm") {
            AppearanceSubPageContent(
                viewModel = viewModel
            )
        } else if (subPage != null) {
            SettingsSubPagePlaceholder(title = subPage)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Grup 1 (Ağ Ayarları Kart Bloğu - RoundedCornerShape(20.dp))
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_group_network"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        )
                    ) {
                        Column {
                            // 1. Öğe: "Kablosuz"
                            SettingsMenuItem(
                                icon = Icons.Default.Wifi,
                                title = "Kablosuz",
                                subtitle = "Wi-Fi bağlantı ve ağ yapılandırması",
                                onClick = { onSelectSubPage("Kablosuz") },
                                modifier = Modifier.testTag("settings_item_wireless")
                            )

                            // İnce ayırıcı çizgi
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // 2. Öğe: "Bağlantılar"
                            SettingsMenuItem(
                                icon = Icons.Default.AccountTree,
                                title = "Bağlantılar",
                                subtitle = "Kütüphane ağ noktaları ve kayıtlı portallar",
                                onClick = { onSelectSubPage("Bağlantılar") },
                                modifier = Modifier.testTag("settings_item_connections")
                            )
                        }
                    }
                }

                // Araya Dikey Boşluk (16.dp)
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Grup 2 (Uygulama Tercihleri Bloğu - RoundedCornerShape(20.dp))
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_group_preferences"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        )
                    ) {
                        Column {
                            // 3. Öğe: "Görünüm"
                            SettingsMenuItem(
                                icon = Icons.Default.Palette,
                                title = "Görünüm",
                                subtitle = "Tema, renkler ve arayüz kişiselleştirmesi",
                                onClick = { onSelectSubPage("Görünüm") },
                                modifier = Modifier.testTag("settings_item_appearance")
                            )

                            // İnce ayırıcı çizgi
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            // 4. Öğe: "Güncellemeler"
                            SettingsMenuItem(
                                icon = Icons.Default.SystemUpdate,
                                title = "Güncellemeler",
                                subtitle = "Sürüm denetimi ve yeni güncellemeler",
                                onClick = { onSelectSubPage("Güncellemeler") },
                                modifier = Modifier.testTag("settings_item_updates")
                            )
                        }
                    }
                }

                // Araya Dikey Boşluk (24.dp)
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Grup 3: "Hakkında" Bölümü (Ayrı Kart)
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Hakkında",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_group_about"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                            )
                        ) {
                            SettingsMenuItem(
                                icon = Icons.Default.Info,
                                title = "FKTB Wi-Fi Giriş",
                                subtitle = "v${BuildConfig.VERSION_NAME} • Açık Kaynak",
                                onClick = { onSelectSubPage("Hakkında") },
                                modifier = Modifier.testTag("settings_item_about")
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

// -------------------------------------------------------------
// GÜNCELLEMELER ALT SAYFASI (3 ANA BLOK)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdatesSubPageContent(
    viewModel: PortalViewModel,
    onStartDownload: (GitHubAsset, String) -> Unit
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()
    val currentChannel = userPrefs.updateChannel
    val isAutoCheck = userPrefs.checkUpdatesOnLaunch
    val haptic = LocalHapticFeedback.current

    // Kullanıcı "Güncellemeleri Denetle" butonuna bastığında panel (ModalBottomSheet) açılması için state
    var manualCheckRequested by remember { mutableStateOf(false) }
    var showUpToDateSheet by remember { mutableStateOf<UpdateCheckResult.UpToDate?>(null) }
    var showErrorSheet by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(updateCheckState) {
        if (manualCheckRequested) {
            when (updateCheckState) {
                is UpdateCheckResult.UpToDate -> {
                    showUpToDateSheet = updateCheckState as UpdateCheckResult.UpToDate
                    manualCheckRequested = false
                }
                is UpdateCheckResult.Error -> {
                    showErrorSheet = (updateCheckState as UpdateCheckResult.Error).message
                    manualCheckRequested = false
                }
                is UpdateCheckResult.UpdateAvailable -> {
                    manualCheckRequested = false
                }
                else -> {}
            }
        }
    }

    // Uygulamanız Güncel Alt Paneli (ModalBottomSheet)
    if (showUpToDateSheet != null) {
        val upToDateData = showUpToDateSheet!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showUpToDateSheet = null },
            sheetState = sheetState,
            containerColor = Color(0xFF161A22),
            contentColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF374151)) },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF064E3B),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Uygulamanız Güncel",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = UnboundedFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "En son sürümü kullanıyorsunuz. Herhangi bir güncelleme gerekmiyor.",
                        color = Color(0xFF8E95A5),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Sürüm, Tarih ve Commit ID Bilgi Kartı
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1218),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262A34)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Uygulama Sürümü", color = Color(0xFF8E95A5), fontSize = 13.sp)
                            Text(
                                text = "v${upToDateData.currentVersion}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily
                            )
                        }

                        HorizontalDivider(color = Color(0xFF1E232E), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Sürüm Tarihi", color = Color(0xFF8E95A5), fontSize = 13.sp)
                            val formattedDate = if (upToDateData.publishedAt.isNotBlank()) {
                                upToDateData.publishedAt.take(10) // YYYY-MM-DD
                            } else {
                                "Güncel"
                            }
                            Text(
                                text = formattedDate,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider(color = Color(0xFF1E232E), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Commit ID / Tag", color = Color(0xFF8E95A5), fontSize = 13.sp)
                            val commitDisplay = if (upToDateData.commitHash.isNotBlank()) {
                                upToDateData.commitHash.take(7)
                            } else {
                                "HEAD"
                            }
                            Text(
                                text = commitDisplay,
                                color = Color(0xFF9EC5EA),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = { showUpToDateSheet = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937), contentColor = Color.White)
                ) {
                    Text("Tamam", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Hata Alt Paneli (ModalBottomSheet)
    if (showErrorSheet != null) {
        val errorMessage = showErrorSheet!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showErrorSheet = null },
            sheetState = sheetState,
            containerColor = Color(0xFF161A22),
            contentColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF374151)) },
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF7F1D1D),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Kontrol Başarısız",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = UnboundedFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "GitHub sunucusu ile iletişim kurulurken bir sorun meydana geldi.",
                        color = Color(0xFF8E95A5),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F1218),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFF87171),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { showErrorSheet = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937), contentColor = Color.White)
                ) {
                    Text("Kapat", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Blok: Güncelleme Kanalı (Segmented Pill Seçim)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("update_channel_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Güncelleme Kanalı",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Beta kanalı en yeni özellikleri, Kararlı kanalı test edilmiş sürümleri sunar.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    // Segmented Button Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val isStable = currentChannel == "STABLE"
                            val isBeta = currentChannel == "BETA"

                            // Kararlı (Stable) Butonu
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.setUpdateChannel("STABLE")
                                    }
                                    .testTag("channel_stable_option"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isStable) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isStable) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = "Kararlı",
                                        color = if (isStable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp,
                                        fontWeight = if (isStable) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }

                            // Beta Butonu
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.setUpdateChannel("BETA")
                                    }
                                    .testTag("channel_beta_option"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isBeta) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isBeta) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = "Beta",
                                        color = if (isBeta) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp,
                                        fontWeight = if (isBeta) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Blok: Manuel Güncelleme Kartı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_update_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sürüm Bilgileri
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Yüklü Sürüm",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "v${BuildConfig.VERSION_NAME}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily
                            )
                        }

                        val currentInstalled = BuildConfig.VERSION_NAME
                        val recordedVersion = if (currentChannel == "BETA") {
                            userPrefs.latestBetaVersion.ifBlank { userPrefs.latestStableVersion }
                        } else {
                            userPrefs.latestStableVersion
                        }

                        val latestVersionNumber: String
                        val latestVersionColor: Color

                        if (updateCheckState is UpdateCheckResult.Checking) {
                            latestVersionNumber = "Kontrol ediliyor..."
                            latestVersionColor = MaterialTheme.colorScheme.primary
                        } else if (recordedVersion.isNotBlank()) {
                            latestVersionNumber = "v$recordedVersion"
                            val isNewer = UpdateManager.isVersionNewer(recordedVersion, currentInstalled)
                            latestVersionColor = if (isNewer) {
                                if (currentChannel == "BETA") Color(0xFFFBBF24) else Color(0xFF34D399)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        } else {
                            latestVersionNumber = "v$currentInstalled"
                            latestVersionColor = MaterialTheme.colorScheme.onSurface
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "En Son Sürüm ($currentChannel)",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = latestVersionNumber,
                                color = latestVersionColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = if (latestVersionNumber.startsWith("v")) UnboundedFontFamily else FontFamily.Default
                            )
                        }
                    }

                    // Kontrol ediliyor göstergesi
                    if (updateCheckState is UpdateCheckResult.Checking) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "GitHub sunucusu üzerinden denetleniyor...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Aksiyon Butonu ("Güncellemeleri Denetle" veya "Şimdi Güncelle")
                    val isUpdateAvailable = updateCheckState is UpdateCheckResult.UpdateAvailable
                    val isChecking = updateCheckState is UpdateCheckResult.Checking

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            if (isUpdateAvailable) {
                                val available = updateCheckState as UpdateCheckResult.UpdateAvailable
                                onStartDownload(available.apkAsset, available.newVersion)
                            } else {
                                manualCheckRequested = true
                                viewModel.checkForUpdates(currentChannel)
                            }
                        },
                        enabled = !isChecking,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("action_update_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUpdateAvailable) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Denetleniyor...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        } else if (isUpdateAvailable) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Şimdi Güncelle", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Güncellemeleri Denetle", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 3. Blok: Açılışta Denetleme (Switch)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_check_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Açılışta Kontrol Et",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Uygulama her başladığında arka planda yeni sürüm olup olmadığını denetler.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Switch(
                        checked = isAutoCheck,
                        onCheckedChange = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.setCheckUpdatesOnLaunch(it)
                        },
                        modifier = Modifier.testTag("auto_check_switch")
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// GÖRÜNÜM ALT SAYFASI (TEMA & RENK PALETİ SEÇİMİ)
// -------------------------------------------------------------
@Composable
private fun AppearanceSubPageContent(
    viewModel: PortalViewModel
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val currentThemeMode = userPrefs.themeMode // "SYSTEM", "LIGHT", "DARK"
    val currentPalette = userPrefs.colorPalette // "DYNAMIC", "OCEAN", "SAKURA", "MACHA", "ESPRESSO", "LAVENDER"
    val haptic = LocalHapticFeedback.current

    // Aktif temanın vurgu rengi
    val activeAccentColor = getPaletteAccent(currentPalette)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Blok: "Tema" Kartı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sol Taraf: Dikey ortalanmış hilal / ay ikonu (~24.dp) ve başlık
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Tema",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Tema",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Sağ Taraf: 3 seçenekli yan yana Segmented Buton Grubu: "Sistem", "Aydınlık", "Karanlık"
                    val themeOptions = listOf(
                        "SYSTEM" to "Sistem",
                        "LIGHT" to "Aydınlık",
                        "DARK" to "Karanlık"
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            themeOptions.forEach { (modeKey, modeTitle) ->
                                val isSelected = currentThemeMode == modeKey

                                val pillColor = if (isSelected) activeAccentColor else Color.Transparent
                                val textColor = if (isSelected) {
                                    Color(0xFF0F172A)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            if (!isSelected) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                viewModel.setThemeMode(modeKey)
                                            }
                                        }
                                        .testTag("theme_option_${modeKey.lowercase()}"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = pillColor
                                ) {
                                    Text(
                                        text = modeTitle,
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Blok: "Renk paleti" Kartı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("color_palette_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Başlık ve Sol Taraf İkonu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Renk paleti",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 6 seçenek yan yana listelenir (LazyRow): [Dinamik] | [Okyanus] | [Sakura] | [Maça] | [Espresso] | [Lavanta]
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                    ) {
                        items(AppPalettes) { palette ->
                            val isSelected = currentPalette == palette.id

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) palette.primaryColor.copy(alpha = 0.16f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, palette.primaryColor.copy(alpha = 0.5f)) else null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (!isSelected) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.setColorPalette(palette.id)
                                        }
                                    }
                                    .testTag("palette_option_${palette.id.lowercase()}")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                                ) {
                                    // Çember Renk Önizlemesi (64.dp)
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        // 3 Renk Dilimine Bölünmüş Daire:
                                        // - Sol üst çeyrek dilim: Paletin 1. rengi (Primary)
                                        // - Sağ üst çeyrek dilim: Paletin 2. rengi (Tertiary / Container)
                                        // - Alt yarım dilim: Paletin 3. rengi (Secondary / Accent)
                                        Canvas(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                        ) {
                                            val canvasSize = size

                                            // Sol üst çeyrek dilim (Primary): 180°'den 90° süpürür
                                            drawArc(
                                                color = palette.primaryColor,
                                                startAngle = 180f,
                                                sweepAngle = 90f,
                                                useCenter = true,
                                                size = canvasSize
                                            )

                                            // Sağ üst çeyrek dilim (Tertiary): 270°'den 90° süpürür
                                            drawArc(
                                                color = palette.tertiaryColor,
                                                startAngle = 270f,
                                                sweepAngle = 90f,
                                                useCenter = true,
                                                size = canvasSize
                                            )

                                            // Alt yarım dilim (Secondary): 0°'den 180° süpürür
                                            drawArc(
                                                color = palette.secondaryColor,
                                                startAngle = 0f,
                                                sweepAngle = 180f,
                                                useCenter = true,
                                                size = canvasSize
                                            )
                                        }

                                        // Seçili olan paletin merkezinde beyaz onay işareti
                                        if (isSelected) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xB3000000),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Seçili",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Alt Etiket
                                    Text(
                                        text = palette.name,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// GÜNCELLEME İNDİRME BOTTOM SHEET (M3 ModalBottomSheet)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateDownloadBottomSheet(
    checkState: UpdateCheckResult,
    downloadState: DownloadState,
    onInstallApk: (java.io.File) -> Unit,
    onRetryDownload: (GitHubAsset, String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            if (downloadState !is DownloadState.Downloading) {
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = Color(0xFF161A22),
        contentColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF374151)) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (downloadState) {
                is DownloadState.Downloading -> {
                    // İndirme Sürüyor
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E232E),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Güncellemeler indiriliyor...",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = UnboundedFontFamily,
                            textAlign = TextAlign.Center
                        )

                        // Var olan sürümden yeni sürüme geçiş göstergesi:
                        // Sola mevcut sürüm (beyaz), araya uzun ok işareti, sağa yeni sürüm (beyaz)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1F2937).copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )

                                Text(
                                    text = "────────▶",
                                    color = Color(0xFF9EC5EA),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "v${downloadState.versionName}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )
                            }
                        }

                        Text(
                            text = "GitHub sunucularından doğrudan aktarılıyor",
                            color = Color(0xFF8E95A5),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // İlerleme Çubuğu ve Metin
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val downloadedMb = downloadState.downloadedBytes / (1024.0 * 1024.0)
                            val totalMb = downloadState.totalBytes / (1024.0 * 1024.0)
                            val pct = (downloadState.progress * 100).toInt()

                            Text(
                                text = String.format(java.util.Locale.US, "%.1f MB / %.1f MB", downloadedMb, totalMb),
                                color = Color(0xFF8E95A5),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "%$pct",
                                color = Color(0xFF9EC5EA),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily
                            )
                        }

                        LinearProgressIndicator(
                            progress = { downloadState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF2563EB),
                            trackColor = Color(0xFF1E232E)
                        )
                    }

                    Text(
                        text = "İndirme tamamlandığında sistem kurulum ekranı otomatik açılacaktır.",
                        color = Color(0xFF626978),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                is DownloadState.ReadyToInstall -> {
                    // Kuruluma Hazır
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF064E3B),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Kuruluma Hazır!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = UnboundedFontFamily,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1F2937).copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )

                                Text(
                                    text = "────────▶",
                                    color = Color(0xFF34D399),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "v${downloadState.versionName}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )
                            }
                        }

                        Text(
                            text = "v${downloadState.versionName} paketi başarıyla indirildi. Yükleme işlemine devam edebilirsiniz.",
                            color = Color(0xFF8E95A5),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { onInstallApk(downloadState.apkFile) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("install_apk_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Şimdi Kur",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                is DownloadState.Failed -> {
                    // Hata Durumu
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF7F1D1D),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "İndirme Başarısız",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = downloadState.message,
                            color = Color(0xFFF87171),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Kapat")
                        }

                        if (checkState is UpdateCheckResult.UpdateAvailable) {
                            Button(
                                onClick = { onRetryDownload(checkState.apkAsset, checkState.newVersion) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Tekrar Dene")
                            }
                        }
                    }
                }

                DownloadState.Idle -> {
                    if (checkState is UpdateCheckResult.UpdateAvailable) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1E232E),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = Color(0xFF60A5FA),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Yeni Güncelleme Mevcut!",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily,
                                textAlign = TextAlign.Center
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1F2937).copy(alpha = 0.7f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "v${BuildConfig.VERSION_NAME}",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = UnboundedFontFamily
                                    )

                                    Text(
                                        text = "────────▶",
                                        color = Color(0xFF60A5FA),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "v${checkState.newVersion}",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = UnboundedFontFamily
                                    )
                                }
                            }

                            Text(
                                text = "${checkState.apkAsset.name} (${String.format(java.util.Locale.US, "%.1f MB", checkState.apkAsset.size / (1024.0 * 1024.0))})",
                                color = Color(0xFF8E95A5),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = { onRetryDownload(checkState.apkAsset, checkState.newVersion) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("sheet_start_update_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10B981),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Şimdi Güncelle", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        CircularProgressIndicator(
                            color = Color(0xFF9EC5EA),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "İndirme başlatılıyor...",
                            color = Color(0xFF8E95A5),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSubPagePlaceholder(
    title: String
) {
    val icon = when (title) {
        "Kablosuz" -> Icons.Default.Wifi
        "Bağlantılar" -> Icons.Default.AccountTree
        "Görünüm" -> Icons.Default.Palette
        "Güncellemeler" -> Icons.Default.SystemUpdate
        "Hakkında" -> Icons.Default.Info
        else -> Icons.Default.Settings
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF161A22),
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF9EC5EA),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bu ayar sayfası yakında yapılandırılacaktır.",
                color = Color(0xFF8E95A5),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
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

// -------------------------------------------------------------
// 2. SEKME: BAGLANTILAR (CONNECTIONS) - Bos / Placeholder Ekran
// -------------------------------------------------------------
@Composable
private fun ConnectionsTabContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = "Bağlantılar",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Kayıtlı kütüphane ağları ve geçmiş bağlantı noktaları burada listelenecektir.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

// -------------------------------------------------------------
// EKRAN USTU BUYUK BASLIK (SOLDA, UNBOUNDED FONT ILE)
// -------------------------------------------------------------
@Composable
private fun TopBarSection(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (showBackButton) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBackClick()
                        }
                        .testTag("settings_back_button")
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
                Spacer(modifier = Modifier.width(12.dp))
            }

            AnimatedContent(
                targetState = title,
                transitionSpec = {
                    fadeIn(tween(220)).togetherWith(fadeOut(tween(180)))
                },
                label = "topbar_title_anim"
            ) { currentTitle ->
                Text(
                    text = currentTitle,
                    fontFamily = UnboundedFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("screen_top_title")
                )
            }
        }
    }
}

// -------------------------------------------------------------
// YENI MATERIAL 3 BOTTOM BAR (4 SEKME, PILL & OFFSET ANIMASYONU)
// -------------------------------------------------------------
@Composable
private fun CustomBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val iconOffsetY by animateDpAsState(
                    targetValue = if (isSelected) (-2).dp else 0.dp,
                    animationSpec = tween(durationMillis = 200),
                    label = "tab_icon_offset"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(tab)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("tab_" + tab.name.lowercase())
                ) {
                    val pillModifier = if (isSelected) {
                        Modifier
                            .offset(y = iconOffsetY)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 22.dp, vertical = 5.dp)
                    } else {
                        Modifier
                            .offset(y = iconOffsetY)
                            .padding(horizontal = 16.dp, vertical = 5.dp)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = pillModifier
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    if (isSelected) {
                        Text(
                            text = tab.title,
                            color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}
