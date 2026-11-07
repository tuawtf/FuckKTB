package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WifiTethering
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen {
    data object Onboarding : Screen()
    data object Main : Screen()
}

enum class MainTab(
    val title: String,
    val icon: ImageVector,
    val outlinedIcon: ImageVector = icon
) {
    HOME("Bağlan", Icons.Default.WifiTethering, Icons.Outlined.WifiTethering),
    CONNECTIONS("Bağlantılar", Icons.Default.AccountTree, Icons.Outlined.AccountTree),
    LOGS("Günlük", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
    SETTINGS("Ayarlar", Icons.Default.Settings, Icons.Outlined.Settings);

    val filledIcon: ImageVector get() = icon
    val testTag: String get() = "tab_" + name.lowercase()
}
