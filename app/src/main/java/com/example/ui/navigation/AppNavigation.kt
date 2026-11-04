package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen {
    data object Onboarding : Screen()
    data object Main : Screen()
}

enum class MainTab(
    val title: String,
    val icon: ImageVector
) {
    CONNECT("Bağlan", Icons.Default.Wifi),
    LOGS("Günlük", Icons.Default.ReceiptLong),
    SETTINGS("Ayarlar", Icons.Default.Settings)
}
