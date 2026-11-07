package com.example.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import com.example.SmsCodeSource
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.MainActivity
import com.example.data.DownloadState
import com.example.data.GitHubAsset
import com.example.data.UpdateCheckResult
import com.example.data.UpdateManager
import com.example.ui.navigation.MainTab
import com.example.ui.theme.IconColors
import com.example.ui.theme.UnboundedFontFamily
import com.example.ui.theme.getAppPalettes
import com.example.ui.viewmodel.LogLevel
import com.example.ui.viewmodel.PortalStep
import com.example.ui.viewmodel.PortalViewModel
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.ui.components.frostedContainer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

val LocalBottomBarPadding = compositionLocalOf { 0.dp }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    viewModel: PortalViewModel,
    onResetOnboarding: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    var activeSubPage by remember { mutableStateOf<String?>(null) }
    var showUpdateBottomSheet by remember { mutableStateOf(false) }
    var hasAutoOpenedSheetOnLaunch by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val updateCheckState by viewModel.updateCheckState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()

    val hazeState = remember { HazeState() }
    val effectiveHazeState = if (userPrefs.expressiveBlur) hazeState else null

    val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomBarPadding = 56.dp + 16.dp + navBarsBottom + 16.dp

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

    BackHandler(enabled = !showUpdateBottomSheet && activeSubPage != null) {
        if (activeSubPage == "Profilim_Sihirbaz") {
            activeSubPage = "Profilim"
        } else {
            activeSubPage = null
        }
    }

    CompositionLocalProvider(LocalBottomBarPadding provides bottomBarPadding) {
        Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            containerColor = Color.Transparent,
            topBar = {
                TopBarSection(
                    title = if (activeSubPage == "Profilim_Sihirbaz") "Profil Sihirbazı" else (activeSubPage ?: selectedTab.title),
                    showBackButton = activeSubPage != null,
                    onBackClick = {
                        if (activeSubPage == "Profilim_Sihirbaz") {
                            activeSubPage = "Profilim"
                        } else {
                            activeSubPage = null
                        }
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AnimatedVisibility(
                        visible = activeSubPage == null,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        CustomBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = {
                                selectedTab = it
                                activeSubPage = null
                            },
                            hazeState = effectiveHazeState
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .then(if (userPrefs.expressiveBlur) Modifier.hazeSource(state = hazeState) else Modifier)
                    .background(MaterialTheme.colorScheme.background)
            ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn(tween(250)).togetherWith(fadeOut(tween(200))) },
                label = "main_tab_content"
            ) { tab ->
                when (tab) {
                    MainTab.HOME -> ConnectTabContent(
                        viewModel = viewModel,
                        onNavigateToProfileSetup = {
                            selectedTab = MainTab.CONNECTIONS
                            activeSubPage = "Profilim"
                        }
                    )
                    MainTab.CONNECTIONS -> ConnectionsTabContent(
                        activeSubPage = activeSubPage,
                        viewModel = viewModel,
                        onSelectSubPage = { activeSubPage = it }
                    )
                    MainTab.LOGS -> LogsTabContent(viewModel = viewModel)
                    MainTab.SETTINGS -> SettingsTabContent(
                        activeSubPage = activeSubPage,
                        viewModel = viewModel,
                        onSelectSubPage = { activeSubPage = it },
                        onStartDownload = { asset, ver ->
                            viewModel.startApkDownload(asset, ver)
                            showUpdateBottomSheet = true
                        }
                    )
                }
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
// 1. SEKME: BAĞLAN (HOME)
// -------------------------------------------------------------
@Composable
private fun ConnectTabContent(
    viewModel: PortalViewModel,
    onNavigateToProfileSetup: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    var showNoProfileDialog by remember { mutableStateOf(false) }

    val cardColor by animateColorAsState(
        targetValue = when (uiState.step) {
            PortalStep.SUCCESS -> IconColors.SuccessGreen.copy(alpha = 0.14f)
            PortalStep.ERROR -> IconColors.ErrorRed.copy(alpha = 0.14f)
            PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        },
        label = "cardColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = LocalBottomBarPadding.current),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                    imageVector = if (uiState.isKtbNetworkConfirmed) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = if (uiState.isKtbNetworkConfirmed) IconColors.SuccessGreen else IconColors.WarningYellow,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (uiState.isKtbNetworkConfirmed) "KTB-Misafir" else "Wi-Fi Bağlı Değil",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { viewModel.checkWifiState() },
                modifier = Modifier
                    .size(32.dp)
                    .testTag("refresh_wifi_state_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Yenile",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("connect_status_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
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
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = IconColors.ErrorRed,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            val buttonBgColor = when (uiState.step) {
                PortalStep.SUCCESS -> IconColors.SuccessGreen
                PortalStep.VALIDATING -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.primary
            }
            val buttonContentColor = when (uiState.step) {
                PortalStep.SUCCESS -> Color.White
                else -> MaterialTheme.colorScheme.onPrimary
            }

            Surface(
                onClick = {
                    if (!userPrefs.isProfileCreated || userPrefs.profileUsername.isBlank() || userPrefs.profilePassword.isBlank()) {
                        showNoProfileDialog = true
                    } else if (uiState.step != PortalStep.VALIDATING) {
                        viewModel.startConnectionFlow()
                    }
                },
                enabled = uiState.step != PortalStep.VALIDATING,
                shape = CircleShape,
                color = buttonBgColor,
                contentColor = buttonContentColor,
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
                            PortalStep.VALIDATING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(44.dp),
                                    color = buttonContentColor,
                                    strokeWidth = 3.5.dp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Giriş Yapılıyor",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = buttonContentColor
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
                                    text = "Bağlı",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            PortalStep.ERROR -> {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = buttonContentColor,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Yeniden Dene",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = buttonContentColor
                                )
                            }
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = buttonContentColor,
                                    modifier = Modifier.size(52.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Bağlan",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = buttonContentColor
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.step == PortalStep.ERROR,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetState() },
                    modifier = Modifier.testTag("cancel_connect_button")
                ) {
                    Text("Sıfırla", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Text(
            text = if (userPrefs.isProfileCreated) "Kayıtlı Profil: ${userPrefs.profileUsername}" else "Bağlantı Profili Oluşturulmadı",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showNoProfileDialog) {
        AlertDialog(
            onDismissRequest = { showNoProfileDialog = false },
            title = { Text("Uyarı") },
            text = { Text("Profilinizi oluşturmadınız.") },
            confirmButton = {
                Button(
                    onClick = {
                        showNoProfileDialog = false
                        onNavigateToProfileSetup()
                    }
                ) {
                    Text("Profilimi oluştur")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoProfileDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 2. SEKME: GÜNLÜK (LOGS)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = {
                        val text = uiState.logs.joinToString("\n") { "[" + it.timestamp + "] " + it.message }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("KTB Logs", text))
                        Toast.makeText(context, "Günlük panoya kopyalandı", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("copy_logs_button")
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Kopyala",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.clearLogs() },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("clear_logs_button")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Temizle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (uiState.logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = LocalBottomBarPadding.current),
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
                        text = "Henüz aktivite kaydı bulunmuyor.",
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
                contentPadding = PaddingValues(bottom = LocalBottomBarPadding.current),
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
                        LogLevel.SUCCESS -> IconColors.SuccessGreen
                        LogLevel.ERROR -> IconColors.ErrorRed
                        LogLevel.WARNING -> IconColors.WarningYellow
                        LogLevel.INFO -> IconColors.InfoBlue
                    }

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
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

        OutlinedButton(
            onClick = { viewModel.simulateSmsArrival() },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("simulate_sms_button")
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Örnek SMS Gönder (Test Simülasyonu)")
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = LocalBottomBarPadding.current),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_group_network"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        )
                    ) {
                        Column {
                            SettingsMenuItem(
                                icon = Icons.Default.Wifi,
                                title = "Kablosuz",
                                subtitle = "Wi-Fi bağlantı ve ağ yapılandırması",
                                onClick = { onSelectSubPage("Kablosuz") },
                                modifier = Modifier.testTag("settings_item_wireless")
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

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

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_group_preferences"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        )
                    ) {
                        Column {
                            SettingsMenuItem(
                                icon = Icons.Default.Palette,
                                title = "Görünüm",
                                subtitle = "Tema, renkler ve arayüz kişiselleştirmesi",
                                onClick = { onSelectSubPage("Görünüm") },
                                modifier = Modifier.testTag("settings_item_appearance")
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

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

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                        tint = MaterialTheme.colorScheme.onSurface,
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
// GÜNCELLEMELER ALT SAYFASI
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

    if (showUpToDateSheet != null) {
        val upToDateData = showUpToDateSheet!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showUpToDateSheet = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant) },
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
                    color = IconColors.SuccessGreen.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IconColors.SuccessGreen,
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = UnboundedFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "En son sürümü kullanıyorsunuz. Herhangi bir güncelleme gerekmiyor.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                            Text(text = "Uygulama Sürümü", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Text(
                                text = "v${upToDateData.currentVersion}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Sürüm Tarihi", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            val formattedDate = if (upToDateData.publishedAt.isNotBlank()) {
                                upToDateData.publishedAt.take(10)
                            } else {
                                "Güncel"
                            }
                            Text(
                                text = formattedDate,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Commit ID / Tag", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            val commitDisplay = if (upToDateData.commitHash.isNotBlank()) {
                                upToDateData.commitHash.take(7)
                            } else {
                                "HEAD"
                            }
                            Text(
                                text = commitDisplay,
                                color = MaterialTheme.colorScheme.primary,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Tamam", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (showErrorSheet != null) {
        val errorMessage = showErrorSheet!!
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showErrorSheet = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant) },
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
                    color = IconColors.ErrorRed.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = IconColors.ErrorRed,
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = UnboundedFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "GitHub sunucusu ile iletişim kurulurken bir sorun meydana geldi.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = IconColors.ErrorRed,
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Kapat", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = LocalBottomBarPadding.current),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("update_channel_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
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

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_update_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                                if (currentChannel == "BETA") IconColors.WarningYellow else IconColors.SuccessGreen
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
                            containerColor = if (isUpdateAvailable) IconColors.SuccessGreen else MaterialTheme.colorScheme.primary,
                            contentColor = if (isUpdateAvailable) Color.White else MaterialTheme.colorScheme.onPrimary
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

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auto_check_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
// GÖRÜNÜM ALT SAYFASI (CaffeineHealth REFERANS GÖRÜNÜM TASARIMI)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppearanceSubPageContent(
    viewModel: PortalViewModel
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val currentThemeMode = userPrefs.themeMode
    val currentPalette = userPrefs.colorPalette
    val isAmoledMode = userPrefs.isAmoledMode
    val isExpressiveBlur = userPrefs.expressiveBlur
    val haptic = LocalHapticFeedback.current

    val appPalettes = getAppPalettes()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = LocalBottomBarPadding.current),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Blok: Uygulama Teması Kartı (Caffeine ToggleButton 3'lü Grup)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Başlık: Ay İkonu ve "Uygulama Teması"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Uygulama Teması",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Alt Kısım: ToggleButton 3'lü grup [Sistem] [Açık] [Koyu]
                    val themeOptions = listOf(
                        Triple("SYSTEM", "Sistem", Icons.Default.BrightnessAuto),
                        Triple("LIGHT", "Açık", Icons.Default.LightMode),
                        Triple("DARK", "Koyu", Icons.Default.DarkMode)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        themeOptions.forEach { (modeKey, modeTitle, modeIcon) ->
                            val isSelected = currentThemeMode == modeKey

                            ToggleButton(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (!isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.setThemeMode(modeKey)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("theme_option_${modeKey.lowercase()}"),
                                shapes = ToggleButtonDefaults.shapes(
                                    shape = CircleShape,
                                    pressedShape = CircleShape,
                                    checkedShape = CircleShape
                                ),
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = modeIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = modeTitle,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Blok: Renk Paleti Kartı
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("color_palette_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Renk paleti",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val outlineBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                    ) {
                        items(appPalettes) { palette ->
                            val isSelected = currentPalette == palette.id

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) palette.primaryColor.copy(alpha = 0.16f) else Color.Transparent,
                                border = if (isSelected) {
                                    androidx.compose.foundation.BorderStroke(2.dp, palette.primaryColor)
                                } else {
                                    null
                                },
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
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Canvas(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                        ) {
                                            val canvasSize = size

                                            drawArc(
                                                color = palette.primaryColor,
                                                startAngle = 180f,
                                                sweepAngle = 90f,
                                                useCenter = true,
                                                size = canvasSize
                                            )

                                             drawArc(
                                                color = palette.tertiaryColor,
                                                startAngle = 270f,
                                                sweepAngle = 90f,
                                                useCenter = true,
                                                size = canvasSize
                                            )

                                            drawArc(
                                                color = palette.secondaryColor,
                                                startAngle = 0f,
                                                sweepAngle = 180f,
                                                useCenter = true,
                                                size = canvasSize
                                            )

                                            drawCircle(
                                                color = outlineBorderColor,
                                                radius = (canvasSize.minDimension / 2f) - 1f,
                                                style = Stroke(width = 1.dp.toPx())
                                            )
                                        }

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

        // 3. Blok: Görünüm Seçenekleri Kartı (Segmented Card: Amoled + Gelişmiş Bulanıklık)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("appearance_options_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Satır 1: Amoled Görünüm
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val next = !isAmoledMode
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setAmoledMode(next)
                            }
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Contrast,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Amoled görünüm",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Karanlık temada katran siyahı (#000000) arka plan kullanır.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Switch(
                            checked = isAmoledMode,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setAmoledMode(it)
                            },
                            modifier = Modifier.testTag("amoled_mode_switch")
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )

                    // Satır 2: Gelişmiş Bulanıklık (Expressive Blur)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val next = !isExpressiveBlur
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setExpressiveBlur(next)
                            }
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.BlurOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Gelişmiş bulanıklık",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Alt gezinme çubuğunda cam (frosted glass) bulanıklık efekti uygular.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Switch(
                            checked = isExpressiveBlur,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setExpressiveBlur(it)
                            },
                            modifier = Modifier.testTag("expressive_blur_switch")
                        )
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
// GÜNCELLEME İNDİRME BOTTOM SHEET
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
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant) },
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
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = UnboundedFontFamily,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )

                                Text(
                                    text = "────────▶",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "v${downloadState.versionName}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )
                            }
                        }

                        Text(
                            text = "GitHub sunucularından doğrudan aktarılıyor",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "%$pct",
                                color = MaterialTheme.colorScheme.primary,
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
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    Text(
                        text = "İndirme tamamlandığında sistem kurulum ekranı otomatik açılacaktır.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                is DownloadState.ReadyToInstall -> {
                    Surface(
                        shape = CircleShape,
                        color = IconColors.SuccessGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = IconColors.SuccessGreen,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = UnboundedFontFamily,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )

                                Text(
                                    text = "────────▶",
                                    color = IconColors.SuccessGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "v${downloadState.versionName}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily
                                )
                            }
                        }

                        Text(
                            text = "v${downloadState.versionName} paketi başarıyla indirildi. Yükleme işlemine devam edebilirsiniz.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            containerColor = IconColors.SuccessGreen,
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
                    Surface(
                        shape = CircleShape,
                        color = IconColors.ErrorRed.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = IconColors.ErrorRed,
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
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = downloadState.message,
                            color = IconColors.ErrorRed,
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily,
                                textAlign = TextAlign.Center
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "v${BuildConfig.VERSION_NAME}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = UnboundedFontFamily
                                    )

                                    Text(
                                        text = "────────▶",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "v${checkState.newVersion}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = UnboundedFontFamily
                                    )
                                }
                            }

                            Text(
                                text = "${checkState.apkAsset.name} (${String.format(java.util.Locale.US, "%.1f MB", checkState.apkAsset.size / (1024.0 * 1024.0))})",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                containerColor = IconColors.SuccessGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Şimdi Güncelle", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "İndirme başlatılıyor...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        else -> Icons.Default.Wifi
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bu ayar sayfası yakında yapılandırılacaktır.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
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
        title = { Text("Doğrulama Kodu Girişi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SMS ile gelen 6 haneli kodu elle girip bağlantıyı tamamlayabilirsiniz.",
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
                Text("Doğrula ve Giriş Yap")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç")
            }
        }
    )
}

// -------------------------------------------------------------
// 2. SEKME: BAĞLANTILAR (CONNECTIONS)
// -------------------------------------------------------------
@Composable
private fun ConnectionsTabContent(
    activeSubPage: String?,
    viewModel: PortalViewModel,
    onSelectSubPage: (String?) -> Unit
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val isProfileCreated = userPrefs.isProfileCreated
    val haptic = LocalHapticFeedback.current

    if (activeSubPage == "Profilim") {
        ProfileDetailScreen(
            viewModel = viewModel,
            onBackClick = { onSelectSubPage(null) },
            onReconfigure = { onSelectSubPage("Profilim_Sihirbaz") }
        )
    } else if (activeSubPage == "Profilim_Sihirbaz") {
        ProfileSubPageContent(
            viewModel = viewModel,
            onFinishSetup = { onSelectSubPage("Profilim") }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = LocalBottomBarPadding.current),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // "Bağlantı Profilim" Kartı
            item {
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSelectSubPage("Profilim")
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF161A22),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("connection_profile_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF9EC5EA).copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = Color(0xFF9EC5EA),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Bağlantı Profilim",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isProfileCreated) {
                            // Panel boyunca yatayda uzanan yeşil kapsül kutu
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF064E3B).copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF059669)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Profiliniz tamamlandı.",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            // Profiliniz oluşturulmadı Uyarısı (Tuşa dahil DEĞİL, bağımsız uyarı kutusu)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF332A15),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Profilinizi oluşturmadınız.",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // UYARIDAN AYRI VE NORMAL MATERIAL 3 PALET RENGİNDE TUŞ
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onSelectSubPage("Profilim_Sihirbaz")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("create_profile_card_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Profilimi oluştur",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// "PROFİLİM" DETAY SAYFASI (ProfileDetailScreen)
// -------------------------------------------------------------
@Composable
private fun ProfileDetailScreen(
    viewModel: PortalViewModel,
    onBackClick: () -> Unit,
    onReconfigure: () -> Unit
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val haptic = LocalHapticFeedback.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Ekran Ortası (Profil Bilgi Kartları)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Kullanıcı Adı Kutusu
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Kullanıcı Adı",
                        color = Color(0xFF8E95A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E232E),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp)
                        ) {
                            Text(
                                text = userPrefs.profileUsername.ifBlank { userPrefs.phoneNumber.ifBlank { "Henüz Belirlenmedi" } },
                                color = if (userPrefs.isProfileCreated || userPrefs.phoneNumber.isNotBlank()) Color.White else Color(0xFF8E95A5),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = UnboundedFontFamily
                            )
                        }
                    }
                }

                // 2. Erişim Şifresi / Token Kutusu
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Erişim Şifresi / Token",
                        color = Color(0xFF8E95A5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E232E),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp)
                        ) {
                            Text(
                                text = if (userPrefs.isProfileCreated) userPrefs.profilePassword.ifBlank { "f9642d0cd3" } else "Henüz Belirlenmedi",
                                color = if (userPrefs.isProfileCreated) Color(0xFF9EC5EA) else Color(0xFF8E95A5),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Ekranın En Altı (İşlem Butonları)
        if (userPrefs.isProfileCreated) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onReconfigure()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("reconfigure_profile_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Text("Yeniden Yapılandır", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showDeleteDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("delete_profile_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7F1D1D),
                        contentColor = Color(0xFFF87171)
                    )
                ) {
                    Text("Profili Sil", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onReconfigure()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 12.dp)
                    .testTag("create_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Profilimi Oluştur", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Profili Sil") },
            text = { Text("Kayıtlı otomatik bağlantı profilinizi silmek istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.clearConnectionProfile()
                        showDeleteDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IconColors.ErrorRed,
                        contentColor = Color.White
                    )
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// "PROFİLİM" KURULUM SİHİRBAZI (ProfileSubPageContent)
// -------------------------------------------------------------
private fun Context.findActivity(): ComponentActivity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is ComponentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
private fun ProfileSubPageContent(
    viewModel: PortalViewModel,
    onFinishSetup: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    var currentStep by remember { mutableIntStateOf(1) }

    var phoneDigits by remember {
        mutableStateOf(
            if (userPrefs.phoneNumber.isNotBlank()) {
                userPrefs.phoneNumber.filter { it.isDigit() }.takeLast(10)
            } else if (uiState.phoneNumber.isNotBlank()) {
                uiState.phoneNumber.filter { it.isDigit() }.takeLast(10)
            } else {
                "5551234567"
            }
        )
    }

    var showPhoneEditDialog by remember { mutableStateOf(false) }

    var codeInput by remember { mutableStateOf("") }
    var isValidating by remember { mutableStateOf(false) }
    var isErrorCode by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("HATALI KOD") }
    var resendCooldown by remember { mutableIntStateOf(30) }

    val focusRequester = remember { FocusRequester() }

    var extractedUsername by remember { mutableStateOf("") }
    var extractedPassword by remember { mutableStateOf("") }

    LaunchedEffect(uiState.verificationCode) {
        if (uiState.verificationCode.isNotBlank() && currentStep == 3) {
            codeInput = uiState.verificationCode
        }
    }

    LaunchedEffect(currentStep) {
        if (currentStep == 3) {
            resendCooldown = 30
            delay(250)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(currentStep, resendCooldown) {
        if (currentStep == 3 && resendCooldown > 0) {
            delay(1000)
            resendCooldown -= 1
        }
    }

    // Otomatik SMS Kod Doğrulama (6 hane girilince başlar)
    LaunchedEffect(codeInput) {
        if (codeInput.length == 6 && !isValidating && !isErrorCode && currentStep == 3) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            isValidating = true

            val phone = com.example.util.PhoneUtils.normalizePhoneNumber("0$phoneDigits")
            val res = viewModel.validateSmsTokenForProfile(phone, codeInput)
            isValidating = false
            if (res.isSuccess) {
                val (user, pass) = viewModel.parseResultSetCredentials(res.rawResponse, phone)
                extractedUsername = user
                extractedPassword = pass
                currentStep = 4
            } else {
                val msg = res.message ?: ""
                val raw = res.rawResponse ?: ""
                val isCodeErr = msg.contains("kod", ignoreCase = true) ||
                                msg.contains("token", ignoreCase = true) ||
                                msg.contains("geçersiz", ignoreCase = true) ||
                                msg.contains("hatalı", ignoreCase = true) ||
                                msg.contains("yanlış", ignoreCase = true) ||
                                raw.contains("invalid", ignoreCase = true) ||
                                raw.contains("fail", ignoreCase = true)

                errorText = if (isCodeErr || msg.isBlank()) "HATALI KOD" else "ONAYLANMADI"
                isErrorCode = true
                delay(1500)
                codeInput = ""
                isErrorCode = false
            }
        }
    }

    val isKtbConnected = uiState.isKtbNetworkConfirmed || uiState.wifiSsid?.contains("KTB", ignoreCase = true) == true || uiState.isWifiConnected

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Step gösterge çubuğu (1 - 4)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..4).forEach { step ->
                val isActive = step <= currentStep
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                    shape = CircleShape,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ) {}
            }
        }

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn(tween(220)).togetherWith(fadeOut(tween(180))) },
            label = "profile_step_anim",
            modifier = Modifier.weight(1f)
        ) { step ->
            when (step) {
                // ADIM 1: Wi-Fi Kontrolü (KTB-Misafir)
                1 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Wifi,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Wi-Fi Ağ Kontrolü",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Profil kurulumu için cihazınızın kütüphanedeki 'KTB-Misafir' Wi-Fi ağına bağlı olması gerekmektedir.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            // Wi-Fi Durum Kartı
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isKtbConnected) IconColors.SuccessGreen.copy(alpha = 0.15f) else Color(0xFF332A15),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isKtbConnected) IconColors.SuccessGreen else Color(0xFFF59E0B)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isKtbConnected) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (isKtbConnected) IconColors.SuccessGreen else Color(0xFFF59E0B),
                                        modifier = Modifier.size(28.dp)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = if (isKtbConnected) "KTB-Misafir Ağına Bağlı" else "KTB-Misafir Bağlı Değil",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isKtbConnected) "Aktif SSID: ${uiState.wifiSsid ?: "KTB-Misafir"}" else "Geçerli SSID: ${uiState.wifiSsid ?: "Yok"}",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            if (!isKtbConnected) {
                                OutlinedButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        try {
                                            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                        } catch (_: Exception) {}
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Wi-Fi Ayarlarını Aç")
                                }
                            }
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentStep = 2
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("step1_continue_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // ADIM 2: Numara Gözden Geçirme
                2 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Numaranızı Kontrol Edin",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "SMS doğrulama kodu aşağıdaki 10 haneli cep telefonu numarasına gönderilecektir.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Yan yana numara kutusu ve kare Düzenle ikonu kutusu
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val formattedPhone = if (phoneDigits.length == 10) {
                                            "0${phoneDigits.substring(0, 3)} ${phoneDigits.substring(3, 6)} ${phoneDigits.substring(6, 8)} ${phoneDigits.substring(8, 10)}"
                                        } else {
                                            phoneDigits
                                        }
                                        Text(
                                            text = formattedPhone,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = UnboundedFontFamily,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showPhoneEditDialog = true
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .testTag("edit_phone_number_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Düzenle",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Alt Buton Satırı (Bölünmüş Geri + Devam Et)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentStep = 1
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    val normalized = com.example.util.PhoneUtils.normalizePhoneNumber("0$phoneDigits")
                                    viewModel.updatePhoneNumber(normalized)
                                    viewModel.startConnectionFlow()
                                    context.findActivity()?.let { SmsCodeSource.startSmsUserConsent(it) }
                                    currentStep = 3
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("send_sms_token_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Devam Et", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // ADIM 3: Kod Girişi ve SMS Yakalama (Android Klavye + Otomatik Onay + Birleşik Hata Kutusu + Tekrar Gönder)
                3 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            },
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // 4-5 satır aşağı kaydırma
                            Spacer(modifier = Modifier.height(44.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "SMS Kodunuz",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "KUL.TUR.BKN tarafından gönderilen 6 haneli doğrulama kodunu giriniz.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }

                            // 6 haneli kod giriş alanı (Dokununca klavyeyi kesin açar)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clickable {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    }
                            ) {
                                BasicTextField(
                                    value = codeInput,
                                    onValueChange = { input ->
                                        if (input.length <= 6 && input.all { it.isDigit() } && !isErrorCode && !isValidating) {
                                            codeInput = input
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(0.01f)
                                        .focusRequester(focusRequester)
                                )

                                AnimatedContent(
                                    targetState = isErrorCode,
                                    transitionSpec = { fadeIn(tween(200)).togetherWith(fadeOut(tween(180))) },
                                    label = "code_box_merge_anim",
                                    modifier = Modifier.fillMaxSize()
                                ) { errorState ->
                                    if (errorState) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = IconColors.ErrorRed.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.5.dp, IconColors.ErrorRed),
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    focusRequester.requestFocus()
                                                    keyboardController?.show()
                                                }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.CenterStart,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 18.dp)
                                            ) {
                                                Text(
                                                    text = errorText.uppercase(java.util.Locale.getDefault()),
                                                    color = IconColors.ErrorRed,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = UnboundedFontFamily,
                                                    textAlign = TextAlign.Start
                                                )
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    focusRequester.requestFocus()
                                                    keyboardController?.show()
                                                },
                                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                                        ) {
                                            (0..5).forEach { index ->
                                                val digit = codeInput.getOrNull(index)?.toString() ?: ""
                                                val isCurrent = index == codeInput.length.coerceAtMost(5)

                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        if (isCurrent) 2.dp else 1.dp,
                                                        if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                                    ),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(54.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                            text = digit,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            fontSize = 22.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFamily = UnboundedFontFamily,
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Alt Bar (Sol Geri Butonu + Sağ Tekrar Gönder veya Doğrulanıyor)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentStep = 2
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            if (isValidating) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.5.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Doğrulanıyor...",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (resendCooldown == 0) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            val phone = com.example.util.PhoneUtils.normalizePhoneNumber("0$phoneDigits")
                                            viewModel.updatePhoneNumber(phone)
                                            viewModel.startConnectionFlow()
                                            context.findActivity()?.let { SmsCodeSource.startSmsUserConsent(it) }
                                            resendCooldown = 30
                                            codeInput = ""
                                            focusRequester.requestFocus()
                                            Toast.makeText(context, "SMS Kodu Tekrar Gönderildi", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    enabled = resendCooldown == 0,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .testTag("resend_sms_token_button"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (resendCooldown > 0) "Tekrar Gönder ($resendCooldown s)" else "Tekrar Gönder",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // ADIM 4: Profil Kaydı ve Tamamlama
                4 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = IconColors.SuccessGreen.copy(alpha = 0.2f),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = IconColors.SuccessGreen,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Profil Hazırlandı!",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = UnboundedFontFamily,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "KTB Wi-Fi otomatik bağlantı bilgileriniz sunucudan güvenle alındı.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Özet Kartı
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Kullanıcı Adı / Telefon:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                        Text(extractedUsername, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Erişim Şifresi / Token:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                                        Text(extractedPassword, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        // Alt Buton Satırı (Bölünmüş Geri + Bitir)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentStep = 3
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.saveConnectionProfile(extractedUsername, extractedPassword)
                                    onFinishSetup()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("finish_profile_setup_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IconColors.SuccessGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Bitir", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPhoneEditDialog) {
        var tempDigits by remember { mutableStateOf(phoneDigits) }
        AlertDialog(
            onDismissRequest = { showPhoneEditDialog = false },
            title = { Text("Numara Düzenle") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("10 haneli cep telefonu numaranızı başında 0 olmadan giriniz (5xx xxx xx xx).", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = tempDigits,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }.take(10)
                            tempDigits = digits
                        },
                        singleLine = true,
                        label = { Text("10 Haneli Cep No") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_phone_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempDigits.length == 10) {
                            phoneDigits = tempDigits
                            showPhoneEditDialog = false
                        }
                    },
                    enabled = tempDigits.length == 10
                ) {
                    Text("Kaydet")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhoneEditDialog = false }) {
                    Text("Vazgeç")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// ÖZEL NUMERİK TUŞ TAKIMI (NUMERIC KEYPAD)
// -------------------------------------------------------------
@Composable
private fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "DEL")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { key ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                when (key) {
                                    "DEL" -> onDeleteClick()
                                    "C" -> onClearClick()
                                    else -> onDigitClick(key)
                                }
                            }
                            .testTag("keypad_key_$key")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            when (key) {
                                "DEL" -> Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Sil",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(20.dp)
                                )
                                "C" -> Text(
                                    text = "C",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                else -> Text(
                                    text = key,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EKRAN ÜSTÜ BÜYÜK BAŞLIK
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
// CAFFEINE HEALTH STYLE FLOATING BOTTOM BAR (4 SEKME)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CustomBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    hazeState: HazeState?,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val tabs = MainTab.entries.toTypedArray()

    val tabBounds = remember { mutableStateMapOf<MainTab, Rect>() }

    val selectedBounds = tabBounds[selectedTab]
    val targetX = selectedBounds?.left ?: 0f
    val targetWidth = selectedBounds?.width ?: 0f

    val animatedIndicatorX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_indicator_x"
    )

    val animatedIndicatorWidth by animateFloatAsState(
        targetValue = targetWidth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "nav_indicator_width"
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val isWideScreen = maxWidth >= 600.dp
        // Etiket sığmıyorsa gizle: etiketGenişliği + 248.dp <= maxWidth
        val canShowSelectedLabel = isWideScreen || (maxWidth >= 330.dp)

        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            HorizontalFloatingToolbar(
                expanded = true,
                colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(
                    toolbarContainerColor = Color.Transparent,
                    toolbarContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .frostedContainer(
                        hazeState = hazeState,
                        shape = CircleShape,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        tintAlpha = 0.72f,
                        blurRadius = 24.dp,
                        noiseFactor = 0.08f
                    )
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == tab
                    val isFirstTab = index == 0

                    val buttonModifier = Modifier
                        .height(56.dp)
                        .testTag(tab.testTag)
                        .onGloballyPositioned { coordinates ->
                            tabBounds[tab] = coordinates.boundsInParent()
                        }
                        .then(
                            if (isFirstTab) {
                                Modifier.drawWithContent {
                                    val firstBounds = tabBounds[tabs[0]]
                                    val firstLeft = firstBounds?.left ?: 0f

                                    if (animatedIndicatorWidth > 0f) {
                                        drawRoundRect(
                                            color = primaryColor,
                                            topLeft = Offset(animatedIndicatorX - firstLeft, 0f),
                                            size = Size(animatedIndicatorWidth, size.height),
                                            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
                                        )
                                    }
                                    drawContent()
                                }
                            } else {
                                Modifier
                            }
                        )

                    ToggleButton(
                        checked = isSelected,
                        onCheckedChange = {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(tab)
                            }
                        },
                        shapes = ToggleButtonDefaults.shapes(shape = CircleShape),
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = Color.Transparent,
                            checkedContainerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = buttonModifier
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = if (isSelected && (isWideScreen || canShowSelectedLabel)) 14.dp else 10.dp)
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )

                            val showLabel = isWideScreen || (isSelected && canShowSelectedLabel)
                            AnimatedVisibility(
                                visible = showLabel,
                                enter = expandHorizontally(MaterialTheme.motionScheme.defaultSpatialSpec()) + fadeIn(),
                                exit = shrinkHorizontally(MaterialTheme.motionScheme.defaultSpatialSpec()) + fadeOut()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false
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
