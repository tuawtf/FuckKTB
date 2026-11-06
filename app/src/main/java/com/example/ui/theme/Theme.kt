package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class PaletteOption(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val tertiaryColor: Color,
    val secondaryColor: Color,
    val accentTint: Color
)

val AppPalettes = listOf(
    PaletteOption(
        id = "DYNAMIC",
        name = "Dinamik",
        primaryColor = Color(0xFF3B82F6),
        tertiaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFF10B981),
        accentTint = Color(0xFF60A5FA)
    ),
    PaletteOption(
        id = "OCEAN",
        name = "Okyanus",
        primaryColor = OceanPrimary,
        tertiaryColor = OceanTertiary,
        secondaryColor = OceanSecondary,
        accentTint = OceanPrimary
    ),
    PaletteOption(
        id = "SAKURA",
        name = "Sakura",
        primaryColor = SakuraPrimary,
        tertiaryColor = SakuraTertiary,
        secondaryColor = SakuraSecondary,
        accentTint = SakuraPrimary
    ),
    PaletteOption(
        id = "MACHA",
        name = "Maça",
        primaryColor = MachaPrimary,
        tertiaryColor = MachaTertiary,
        secondaryColor = MachaSecondary,
        accentTint = MachaPrimary
    ),
    PaletteOption(
        id = "ESPRESSO",
        name = "Espresso",
        primaryColor = EspressoPrimary,
        tertiaryColor = EspressoTertiary,
        secondaryColor = EspressoSecondary,
        accentTint = EspressoPrimary
    ),
    PaletteOption(
        id = "LAVENDER",
        name = "Lavanta",
        primaryColor = LavenderPrimary,
        tertiaryColor = LavenderTertiary,
        secondaryColor = LavenderSecondary,
        accentTint = LavenderPrimary
    )
)

fun getPaletteAccent(paletteId: String): Color {
    return AppPalettes.firstOrNull { it.id == paletteId }?.primaryColor ?: OceanPrimary
}

// 1. Okyanus Paleti (Varsayılan)
val OceanDarkColorScheme = darkColorScheme(
    primary = OceanPrimary,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = OceanTertiary,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = OceanSecondary,
    onSecondary = Color(0xFF0F172A),
    tertiary = Color(0xFF38BDF8),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = StatusError,
    onError = Color.White
)

val OceanLightColorScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = OceanSecondary,
    onSecondary = Color.White,
    tertiary = Color(0xFF0284C7),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = StatusError,
    onError = Color.White
)

// 2. Sakura Paleti
val SakuraDarkColorScheme = darkColorScheme(
    primary = SakuraPrimary,
    onPrimary = Color(0xFF500724),
    primaryContainer = SakuraTertiary,
    onPrimaryContainer = Color(0xFFFCE7F3),
    secondary = SakuraSecondary,
    onSecondary = Color(0xFF500724),
    tertiary = Color(0xFFFB7185),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = Color(0xFF1B131A),
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF2A1C27),
    onSurfaceVariant = Color(0xFFE2B8D0),
    outline = Color(0xFF4A2A3E),
    error = StatusError,
    onError = Color.White
)

val SakuraLightColorScheme = lightColorScheme(
    primary = Color(0xFFDB2777),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE7F3),
    onPrimaryContainer = Color(0xFF831843),
    secondary = Color(0xFFBE185D),
    onSecondary = Color.White,
    tertiary = Color(0xFFE11D48),
    background = Color(0xFFFDF8FA),
    onBackground = Color(0xFF1F141B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F141B),
    surfaceVariant = Color(0xFFFCE7F3),
    onSurfaceVariant = Color(0xFF5D384D),
    outline = Color(0xFFE0BDCF),
    error = StatusError,
    onError = Color.White
)

// 3. Maça (Matcha) Paleti
val MachaDarkColorScheme = darkColorScheme(
    primary = MachaPrimary,
    onPrimary = Color(0xFF052E16),
    primaryContainer = MachaTertiary,
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = MachaSecondary,
    onSecondary = Color(0xFF052E16),
    tertiary = Color(0xFF34D399),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = Color(0xFF121B15),
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF1B2A20),
    onSurfaceVariant = Color(0xFFB4D2BE),
    outline = Color(0xFF284132),
    error = StatusError,
    onError = Color.White
)

val MachaLightColorScheme = lightColorScheme(
    primary = Color(0xFF16A34A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF14532D),
    secondary = Color(0xFF15803D),
    onSecondary = Color.White,
    tertiary = Color(0xFF059669),
    background = Color(0xFFF6FBF7),
    onBackground = Color(0xFF0F1E14),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F1E14),
    surfaceVariant = Color(0xFFE8F5EB),
    onSurfaceVariant = Color(0xFF375440),
    outline = Color(0xFFBCE3C7),
    error = StatusError,
    onError = Color.White
)

// 4. Espresso Paleti
val EspressoDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFBBF24),
    onPrimary = Color(0xFF451A03),
    primaryContainer = EspressoTertiary,
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = EspressoSecondary,
    onSecondary = Color(0xFF451A03),
    tertiary = EspressoPrimary,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = Color(0xFF1C1612),
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF2B221A),
    onSurfaceVariant = Color(0xFFD6C3B4),
    outline = Color(0xFF443327),
    error = StatusError,
    onError = Color.White
)

val EspressoLightColorScheme = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFFB45309),
    onSecondary = Color.White,
    tertiary = Color(0xFF92400E),
    background = Color(0xFFFCFAF7),
    onBackground = Color(0xFF241A12),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF241A12),
    surfaceVariant = Color(0xFFF6EFE6),
    onSurfaceVariant = Color(0xFF5A4434),
    outline = Color(0xFFDFD1C4),
    error = StatusError,
    onError = Color.White
)

// 5. Lavanta Paleti
val LavenderDarkColorScheme = darkColorScheme(
    primary = LavenderPrimary,
    onPrimary = Color(0xFF3B0764),
    primaryContainer = LavenderTertiary,
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = LavenderSecondary,
    onSecondary = Color(0xFF3B0764),
    tertiary = Color(0xFFA855F7),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = Color(0xFF171220),
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF241B31),
    onSurfaceVariant = Color(0xFFCEBFDF),
    outline = Color(0xFF3B2E4E),
    error = StatusError,
    onError = Color.White
)

val LavenderLightColorScheme = lightColorScheme(
    primary = Color(0xFF9333EA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF581C87),
    secondary = Color(0xFF7E22CE),
    onSecondary = Color.White,
    tertiary = Color(0xFF6B21A8),
    background = Color(0xFFFAF7FC),
    onBackground = Color(0xFF1C1226),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1226),
    surfaceVariant = Color(0xFFF2EAF9),
    onSurfaceVariant = Color(0xFF503A68),
    outline = Color(0xFFD8C7E7),
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: String = "DARK", // "SYSTEM", "LIGHT", "DARK"
    colorPalette: String = "OCEAN", // "DYNAMIC", "OCEAN", "SAKURA", "MACHA", "ESPRESSO", "LAVENDER"
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        colorPalette == "DYNAMIC" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        colorPalette == "SAKURA" -> if (darkTheme) SakuraDarkColorScheme else SakuraLightColorScheme
        colorPalette == "MACHA" -> if (darkTheme) MachaDarkColorScheme else MachaLightColorScheme
        colorPalette == "ESPRESSO" -> if (darkTheme) EspressoDarkColorScheme else EspressoLightColorScheme
        colorPalette == "LAVENDER" -> if (darkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
        else -> if (darkTheme) OceanDarkColorScheme else OceanLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
