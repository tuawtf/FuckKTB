package com.example.ui.theme

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CompositionLocal providing true if the active rendered theme is dark mode.
 * Disables any direct dependence on isSystemInDarkTheme() in UI composables.
 */
val LocalIsDarkTheme = staticCompositionLocalOf { true }

val isDarkTheme: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsDarkTheme.current

data class PaletteOption(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val tertiaryColor: Color,
    val secondaryColor: Color
)

/**
 * Available color palettes.
 * API 31+ includes "Dinamik" (Material You). API < 31 hides "Dinamik".
 */
@Composable
fun getAppPalettes(): List<PaletteOption> {
    val context = LocalContext.current
    val list = mutableListOf<PaletteOption>()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicDark = dynamicDarkColorScheme(context)
        list.add(
            PaletteOption(
                id = "DYNAMIC",
                name = "Dinamik",
                primaryColor = dynamicDark.primary,
                tertiaryColor = dynamicDark.tertiary,
                secondaryColor = dynamicDark.secondary
            )
        )
    }

    list.addAll(
        listOf(
            PaletteOption(
                id = "OCEAN",
                name = "Okyanus",
                primaryColor = OceanPrimary,
                tertiaryColor = Color(0xFF38BDF8),
                secondaryColor = OceanSecondary
            ),
            PaletteOption(
                id = "SAKURA",
                name = "Sakura",
                primaryColor = SakuraPrimary,
                tertiaryColor = Color(0xFFFB7185),
                secondaryColor = SakuraSecondary
            ),
            PaletteOption(
                id = "MACHA",
                name = "Maça",
                primaryColor = MachaPrimary,
                tertiaryColor = Color(0xFF34D399),
                secondaryColor = MachaSecondary
            ),
            PaletteOption(
                id = "ESPRESSO",
                name = "Espresso",
                primaryColor = Color(0xFFFBBF24),
                tertiaryColor = EspressoPrimary,
                secondaryColor = EspressoSecondary
            ),
            PaletteOption(
                id = "LAVENDER",
                name = "Lavanta",
                primaryColor = LavenderPrimary,
                tertiaryColor = Color(0xFFA855F7),
                secondaryColor = LavenderSecondary
            )
        )
    )

    return list
}

// ---------------------------------------------------------------------------
// COLOR SCHEMES FOR ALL PALETTES WITH COMPLETE M3 ROLES
// ---------------------------------------------------------------------------

// 1. OCEAN (Dark & Light)
val OceanDarkColorScheme = darkColorScheme(
    primary = OceanPrimary,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = OceanSecondary,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF0284C7),
    onTertiaryContainer = Color(0xFFE0F2FE),
    background = Color(0xFF090D16),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131B2E),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainerLowest = Color(0xFF070A12),
    surfaceContainerLow = Color(0xFF0E1422),
    surfaceContainer = Color(0xFF131B2E),
    surfaceContainerHigh = Color(0xFF1A243B),
    surfaceContainerHighest = Color(0xFF222F4C),
    surfaceBright = Color(0xFF2D3C5E),
    surfaceDim = Color(0xFF0C121F),
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = Color(0xFF1D4ED8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    scrim = Color(0xFF000000),
    surfaceTint = OceanPrimary,
    error = IconColors.ErrorRed,
    onError = Color.White
)

val OceanLightColorScheme = lightColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F2FE),
    onTertiaryContainer = Color(0xFF075985),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8FAFC),
    surfaceContainer = Color(0xFFF1F5F9),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFE2E8F0),
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF8FAFC),
    inversePrimary = OceanPrimary,
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF1D4ED8),
    error = IconColors.ErrorRed,
    onError = Color.White
)

// 2. SAKURA (Dark & Light)
val SakuraDarkColorScheme = darkColorScheme(
    primary = SakuraPrimary,
    onPrimary = Color(0xFF500724),
    primaryContainer = Color(0xFF831843),
    onPrimaryContainer = Color(0xFFFCE7F3),
    secondary = SakuraSecondary,
    onSecondary = Color(0xFF500724),
    secondaryContainer = Color(0xFF4A102E),
    onSecondaryContainer = Color(0xFFFBCFE8),
    tertiary = Color(0xFFFB7185),
    onTertiary = Color(0xFF4C0519),
    tertiaryContainer = Color(0xFF881337),
    onTertiaryContainer = Color(0xFFFFE4E6),
    background = Color(0xFF140B10),
    onBackground = Color(0xFFFDF2F8),
    surface = Color(0xFF1F121B),
    onSurface = Color(0xFFFDF2F8),
    surfaceVariant = Color(0xFF2C1926),
    onSurfaceVariant = Color(0xFFF472B6),
    surfaceContainerLowest = Color(0xFF0D060B),
    surfaceContainerLow = Color(0xFF170D14),
    surfaceContainer = Color(0xFF1F121B),
    surfaceContainerHigh = Color(0xFF2A1925),
    surfaceContainerHighest = Color(0xFF382132),
    surfaceBright = Color(0xFF482B40),
    surfaceDim = Color(0xFF160C13),
    inverseSurface = Color(0xFFFDF2F8),
    inverseOnSurface = Color(0xFF1F121B),
    inversePrimary = Color(0xFFDB2777),
    outline = Color(0xFF5C2C4A),
    outlineVariant = Color(0xFF381D2E),
    scrim = Color(0xFF000000),
    surfaceTint = SakuraPrimary,
    error = IconColors.ErrorRed,
    onError = Color.White
)

val SakuraLightColorScheme = lightColorScheme(
    primary = Color(0xFFBE185D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFCE7F3),
    onPrimaryContainer = Color(0xFF831843),
    secondary = Color(0xFF9D174D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBCFE8),
    onSecondaryContainer = Color(0xFF831843),
    tertiary = Color(0xFFE11D48),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE4E6),
    onTertiaryContainer = Color(0xFF881337),
    background = Color(0xFFFDF2F8),
    onBackground = Color(0xFF831843),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF831843),
    surfaceVariant = Color(0xFFFCE7F3),
    onSurfaceVariant = Color(0xFF9D174D),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFDF2F8),
    surfaceContainer = Color(0xFFFCE7F3),
    surfaceContainerHigh = Color(0xFFFBCFE8),
    surfaceContainerHighest = Color(0xFFF472B6),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFFBCFE8),
    inverseSurface = Color(0xFF382132),
    inverseOnSurface = Color(0xFFFDF2F8),
    inversePrimary = SakuraPrimary,
    outline = Color(0xFFF472B6),
    outlineVariant = Color(0xFFFBCFE8),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFBE185D),
    error = IconColors.ErrorRed,
    onError = Color.White
)

// 3. MACHA (Dark & Light)
val MachaDarkColorScheme = darkColorScheme(
    primary = MachaPrimary,
    onPrimary = Color(0xFF052E16),
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = MachaSecondary,
    onSecondary = Color(0xFF052E16),
    secondaryContainer = Color(0xFF163821),
    onSecondaryContainer = Color(0xFFBBF7D0),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF022C22),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = Color(0xFF09140D),
    onBackground = Color(0xFFF0FDF4),
    surface = Color(0xFF112217),
    onSurface = Color(0xFFF0FDF4),
    surfaceVariant = Color(0xFF1A3323),
    onSurfaceVariant = Color(0xFF86EFAC),
    surfaceContainerLowest = Color(0xFF050D08),
    surfaceContainerLow = Color(0xFF0D1B12),
    surfaceContainer = Color(0xFF112217),
    surfaceContainerHigh = Color(0xFF182F20),
    surfaceContainerHighest = Color(0xFF22422D),
    surfaceBright = Color(0xFF2F593E),
    surfaceDim = Color(0xFF0C1911),
    inverseSurface = Color(0xFFF0FDF4),
    inverseOnSurface = Color(0xFF112217),
    inversePrimary = Color(0xFF15803D),
    outline = Color(0xFF2C543A),
    outlineVariant = Color(0xFF1A3524),
    scrim = Color(0xFF000000),
    surfaceTint = MachaPrimary,
    error = IconColors.ErrorRed,
    onError = Color.White
)

val MachaLightColorScheme = lightColorScheme(
    primary = Color(0xFF15803D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF14532D),
    secondary = Color(0xFF166534),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBBF7D0),
    onSecondaryContainer = Color(0xFF14532D),
    tertiary = Color(0xFF047857),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA7F3D0),
    onTertiaryContainer = Color(0xFF064E3B),
    background = Color(0xFFF0FDF4),
    onBackground = Color(0xFF14532D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF14532D),
    surfaceVariant = Color(0xFFDCFCE7),
    onSurfaceVariant = Color(0xFF166534),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF0FDF4),
    surfaceContainer = Color(0xFFDCFCE7),
    surfaceContainerHigh = Color(0xFFBBF7D0),
    surfaceContainerHighest = Color(0xFF86EFAC),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFBBF7D0),
    inverseSurface = Color(0xFF22422D),
    inverseOnSurface = Color(0xFFF0FDF4),
    inversePrimary = MachaPrimary,
    outline = Color(0xFF86EFAC),
    outlineVariant = Color(0xFFBBF7D0),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF15803D),
    error = IconColors.ErrorRed,
    onError = Color.White
)

// 4. ESPRESSO (Dark & Light)
val EspressoDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFBBF24),
    onPrimary = Color(0xFF451A03),
    primaryContainer = Color(0xFF78350F),
    onPrimaryContainer = Color(0xFFFEF3C7),
    secondary = EspressoSecondary,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF44230D),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = EspressoPrimary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF92400E),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = Color(0xFF120E0A),
    onBackground = Color(0xFFFFFBEB),
    surface = Color(0xFF1F1812),
    onSurface = Color(0xFFFFFBEB),
    surfaceVariant = Color(0xFF2E241B),
    onSurfaceVariant = Color(0xFFFBBF24),
    surfaceContainerLowest = Color(0xFF0B0806),
    surfaceContainerLow = Color(0xFF17120D),
    surfaceContainer = Color(0xFF1F1812),
    surfaceContainerHigh = Color(0xFF2B2118),
    surfaceContainerHighest = Color(0xFF3B2E22),
    surfaceBright = Color(0xFF4C3C2C),
    surfaceDim = Color(0xFF15100B),
    inverseSurface = Color(0xFFFFFBEB),
    inverseOnSurface = Color(0xFF1F1812),
    inversePrimary = Color(0xFFB45309),
    outline = Color(0xFF5E4935),
    outlineVariant = Color(0xFF3B2E22),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFBBF24),
    error = IconColors.ErrorRed,
    onError = Color.White
)

val EspressoLightColorScheme = lightColorScheme(
    primary = Color(0xFFB45309),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF92400E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFDE68A),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF78350F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF451A03),
    background = Color(0xFFFFFBEB),
    onBackground = Color(0xFF78350F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF78350F),
    surfaceVariant = Color(0xFFFEF3C7),
    onSurfaceVariant = Color(0xFF92400E),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFBEB),
    surfaceContainer = Color(0xFFFEF3C7),
    surfaceContainerHigh = Color(0xFFFDE68A),
    surfaceContainerHighest = Color(0xFFFBBF24),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFFDE68A),
    inverseSurface = Color(0xFF3B2E22),
    inverseOnSurface = Color(0xFFFFFBEB),
    inversePrimary = Color(0xFFFBBF24),
    outline = Color(0xFFFBBF24),
    outlineVariant = Color(0xFFFDE68A),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFB45309),
    error = IconColors.ErrorRed,
    onError = Color.White
)

// 5. LAVENDER (Dark & Light)
val LavenderDarkColorScheme = darkColorScheme(
    primary = LavenderPrimary,
    onPrimary = Color(0xFF3B0764),
    primaryContainer = Color(0xFF581C87),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = LavenderSecondary,
    onSecondary = Color(0xFF3B0764),
    secondaryContainer = Color(0xFF341052),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = Color(0xFFA855F7),
    onTertiary = Color(0xFF3B0764),
    tertiaryContainer = Color(0xFF6B21A8),
    onTertiaryContainer = Color(0xFFF3E8FF),
    background = Color(0xFF100B17),
    onBackground = Color(0xFFFAF5FF),
    surface = Color(0xFF1A1226),
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF281C3B),
    onSurfaceVariant = Color(0xFFC084FC),
    surfaceContainerLowest = Color(0xFF0A070F),
    surfaceContainerLow = Color(0xFF130D1C),
    surfaceContainer = Color(0xFF1A1226),
    surfaceContainerHigh = Color(0xFF241A33),
    surfaceContainerHighest = Color(0xFF312347),
    surfaceBright = Color(0xFF42305E),
    surfaceDim = Color(0xFF120C1B),
    inverseSurface = Color(0xFFFAF5FF),
    inverseOnSurface = Color(0xFF1A1226),
    inversePrimary = Color(0xFF7E22CE),
    outline = Color(0xFF503870),
    outlineVariant = Color(0xFF312347),
    scrim = Color(0xFF000000),
    surfaceTint = LavenderPrimary,
    error = IconColors.ErrorRed,
    onError = Color.White
)

val LavenderLightColorScheme = lightColorScheme(
    primary = Color(0xFF7E22CE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF581C87),
    secondary = Color(0xFF6B21A8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9D5FF),
    onSecondaryContainer = Color(0xFF581C87),
    tertiary = Color(0xFF581C87),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3E8FF),
    onTertiaryContainer = Color(0xFF3B0764),
    background = Color(0xFFFAF5FF),
    onBackground = Color(0xFF581C87),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF581C87),
    surfaceVariant = Color(0xFFF3E8FF),
    onSurfaceVariant = Color(0xFF6B21A8),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF5FF),
    surfaceContainer = Color(0xFFF3E8FF),
    surfaceContainerHigh = Color(0xFFE9D5FF),
    surfaceContainerHighest = Color(0xFFC084FC),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFE9D5FF),
    inverseSurface = Color(0xFF312347),
    inverseOnSurface = Color(0xFFFAF5FF),
    inversePrimary = LavenderPrimary,
    outline = Color(0xFFC084FC),
    outlineVariant = Color(0xFFE9D5FF),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF7E22CE),
    error = IconColors.ErrorRed,
    onError = Color.White
)

/**
 * Animated ColorScheme generator to ensure smooth ~300ms transitions when theme/palette changes.
 */
@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    val duration = 300
    val animSpec = tween<Color>(duration)

    return ColorScheme(
        primary = animateColorAsState(target.primary, animSpec, label = "primary").value,
        onPrimary = animateColorAsState(target.onPrimary, animSpec, label = "onPrimary").value,
        primaryContainer = animateColorAsState(target.primaryContainer, animSpec, label = "primaryContainer").value,
        onPrimaryContainer = animateColorAsState(target.onPrimaryContainer, animSpec, label = "onPrimaryContainer").value,
        inversePrimary = animateColorAsState(target.inversePrimary, animSpec, label = "inversePrimary").value,
        secondary = animateColorAsState(target.secondary, animSpec, label = "secondary").value,
        onSecondary = animateColorAsState(target.onSecondary, animSpec, label = "onSecondary").value,
        secondaryContainer = animateColorAsState(target.secondaryContainer, animSpec, label = "secondaryContainer").value,
        onSecondaryContainer = animateColorAsState(target.onSecondaryContainer, animSpec, label = "onSecondaryContainer").value,
        tertiary = animateColorAsState(target.tertiary, animSpec, label = "tertiary").value,
        onTertiary = animateColorAsState(target.onTertiary, animSpec, label = "onTertiary").value,
        tertiaryContainer = animateColorAsState(target.tertiaryContainer, animSpec, label = "tertiaryContainer").value,
        onTertiaryContainer = animateColorAsState(target.onTertiaryContainer, animSpec, label = "onTertiaryContainer").value,
        background = animateColorAsState(target.background, animSpec, label = "background").value,
        onBackground = animateColorAsState(target.onBackground, animSpec, label = "onBackground").value,
        surface = animateColorAsState(target.surface, animSpec, label = "surface").value,
        onSurface = animateColorAsState(target.onSurface, animSpec, label = "onSurface").value,
        surfaceVariant = animateColorAsState(target.surfaceVariant, animSpec, label = "surfaceVariant").value,
        onSurfaceVariant = animateColorAsState(target.onSurfaceVariant, animSpec, label = "onSurfaceVariant").value,
        surfaceTint = animateColorAsState(target.surfaceTint, animSpec, label = "surfaceTint").value,
        inverseSurface = animateColorAsState(target.inverseSurface, animSpec, label = "inverseSurface").value,
        inverseOnSurface = animateColorAsState(target.inverseOnSurface, animSpec, label = "inverseOnSurface").value,
        error = animateColorAsState(target.error, animSpec, label = "error").value,
        onError = animateColorAsState(target.onError, animSpec, label = "onError").value,
        errorContainer = animateColorAsState(target.errorContainer, animSpec, label = "errorContainer").value,
        onErrorContainer = animateColorAsState(target.onErrorContainer, animSpec, label = "onErrorContainer").value,
        outline = animateColorAsState(target.outline, animSpec, label = "outline").value,
        outlineVariant = animateColorAsState(target.outlineVariant, animSpec, label = "outlineVariant").value,
        scrim = animateColorAsState(target.scrim, animSpec, label = "scrim").value,
        surfaceContainerLowest = animateColorAsState(target.surfaceContainerLowest, animSpec, label = "surfaceContainerLowest").value,
        surfaceContainerLow = animateColorAsState(target.surfaceContainerLow, animSpec, label = "surfaceContainerLow").value,
        surfaceContainer = animateColorAsState(target.surfaceContainer, animSpec, label = "surfaceContainer").value,
        surfaceContainerHigh = animateColorAsState(target.surfaceContainerHigh, animSpec, label = "surfaceContainerHigh").value,
        surfaceContainerHighest = animateColorAsState(target.surfaceContainerHighest, animSpec, label = "surfaceContainerHighest").value,
        surfaceBright = animateColorAsState(target.surfaceBright, animSpec, label = "surfaceBright").value,
        surfaceDim = animateColorAsState(target.surfaceDim, animSpec, label = "surfaceDim").value
    )
}

@Composable
fun MyApplicationTheme(
    themeMode: String = "DARK", // "SYSTEM", "LIGHT", "DARK"
    colorPalette: String = "OCEAN", // "DYNAMIC", "OCEAN", "SAKURA", "MACHA", "ESPRESSO", "LAVENDER"
    isAmoledMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()

    val effectiveDarkTheme = when (themeMode) {
        "LIGHT" -> false
        "DARK" -> true
        else -> systemInDark
    }

    val context = LocalContext.current
    var rawTargetColorScheme: ColorScheme = when {
        colorPalette == "DYNAMIC" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (effectiveDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        colorPalette == "SAKURA" -> if (effectiveDarkTheme) SakuraDarkColorScheme else SakuraLightColorScheme
        colorPalette == "MACHA" -> if (effectiveDarkTheme) MachaDarkColorScheme else MachaLightColorScheme
        colorPalette == "ESPRESSO" -> if (effectiveDarkTheme) EspressoDarkColorScheme else EspressoLightColorScheme
        colorPalette == "LAVENDER" -> if (effectiveDarkTheme) LavenderDarkColorScheme else LavenderLightColorScheme
        else -> if (effectiveDarkTheme) OceanDarkColorScheme else OceanLightColorScheme
    }

    // Amoled görünüm aktifse ve karanlık temadaysak zeminleri katran siyahı (#000000) yap
    if (effectiveDarkTheme && isAmoledMode) {
        rawTargetColorScheme = rawTargetColorScheme.copy(
            background = Color(0xFF000000),
            surface = Color(0xFF000000),
            surfaceContainerLowest = Color(0xFF000000),
            surfaceContainerLow = Color(0xFF080808),
            surfaceContainer = Color(0xFF0A0A0A),
            surfaceContainerHigh = Color(0xFF121212),
            surfaceContainerHighest = Color(0xFF1A1A1A),
            surfaceDim = Color(0xFF080808)
        )
    }

    val animatedColorScheme = animateColorScheme(rawTargetColorScheme)
    val view = LocalView.current

    // Sistem Çubukları & Edge-To-Edge SideEffect
    SideEffect {
        val activity = view.context as? Activity ?: return@SideEffect
        val window = activity.window ?: return@SideEffect
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.isAppearanceLightStatusBars = !effectiveDarkTheme
        insetsController.isAppearanceLightNavigationBars = !effectiveDarkTheme

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        window.setBackgroundDrawable(ColorDrawable(animatedColorScheme.background.toArgb()))
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides effectiveDarkTheme
    ) {
        MaterialTheme(
            colorScheme = animatedColorScheme,
            typography = Typography,
            content = content
        )
    }
}
