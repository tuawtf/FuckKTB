package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * KESİN KURAL: İkon ve Durum Renkleri (IconColors)
 * Tema veya palet değişince durum ikonlarının ve rozetlerinin iç dolgu renkleri DEĞİŞMEZ.
 * Sabit tutulması gereken tüm durum/rozet renkleri bu nesne altında toplanmıştır.
 */
object IconColors {
    val SuccessGreen = Color(0xFF10B981)
    val SuccessGreenLight = Color(0xFF34D399)
    val SuccessGreenContainer = Color(0xFF064E3B)

    val ErrorRed = Color(0xFFEF4444)
    val ErrorRedLight = Color(0xFFF87171)
    val ErrorRedContainer = Color(0xFF7F1D1D)

    val WarningYellow = Color(0xFFF59E0B)
    val WarningYellowLight = Color(0xFFFBBF24)
    val WarningYellowContainer = Color(0xFF78350F)

    val InfoBlue = Color(0xFF60A5FA)
    val InfoBlueLight = Color(0xFF9EC5EA)
    val InfoBlueContainer = Color(0xFF1E3A8A)

    val NeutralBadgeBg = Color(0xFF1E232E)
    val NeutralIconTint = Color(0xFF9EC5EA)
}

// Eski sabit durum renkleri uyumluluğu
val StatusSuccess = IconColors.SuccessGreen
val StatusWarning = IconColors.WarningYellow
val StatusError = IconColors.ErrorRed
val StatusInfo = IconColors.InfoBlue

// Brand Colors
val KtbBluePrimary = Color(0xFF1D4ED8)
val KtbBlueSecondary = Color(0xFF0284C7)
val KtbCyan = Color(0xFF06B6D4)

// 1. Okyanus (Ocean) Paleti Renkleri
val OceanPrimary = Color(0xFF60A5FA)
val OceanSecondary = Color(0xFF9EC5EA)
val OceanTertiary = Color(0xFF1E3A8A)

// 2. Sakura Paleti Renkleri
val SakuraPrimary = Color(0xFFF472B6)
val SakuraSecondary = Color(0xFFFBCFE8)
val SakuraTertiary = Color(0xFF831843)

// 3. Maça (Matcha) Paleti Renkleri
val MachaPrimary = Color(0xFF86EFAC)
val MachaSecondary = Color(0xFFBBF7D0)
val MachaTertiary = Color(0xFF14532D)

// 4. Espresso Paleti Renkleri
val EspressoPrimary = Color(0xFFD97706)
val EspressoSecondary = Color(0xFFFDE68A)
val EspressoTertiary = Color(0xFF78350F)

// 5. Lavanta Paleti Renkleri
val LavenderPrimary = Color(0xFFC084FC)
val LavenderSecondary = Color(0xFFE9D5FF)
val LavenderTertiary = Color(0xFF581C87)

// 6. Amoled Paleti Renkleri
val AmoledPrimary = Color(0xFFE2E8F0)
val AmoledSecondary = Color(0xFF9CA3AF)
val AmoledTertiary = Color(0xFF6B7280)
