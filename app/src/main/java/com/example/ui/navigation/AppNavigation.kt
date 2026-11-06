package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen {
    data object Onboarding : Screen()
    data object Main : Screen()
}

enum class MainTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("Bağlan", Icons.Default.WifiTethering),
    CONNECTIONS("Bağlantılar", Icons.Default.AccountTree),
    LOGS("Günlük", Icons.AutoMirrored.Filled.ReceiptLong),
    SETTINGS("Ayarlar", Icons.Default.Settings)
}
