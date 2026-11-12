package com.pulseforge.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.ThemePalette

val LocalExtraColors = staticCompositionLocalOf { PulseThemeColors.PowerExtraDark }

val PulseTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

@Composable
fun PulseForgeTheme(
    palette: ThemePalette = ThemePalette.POWER,
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val (colorScheme, extraColors) = when (palette) {
        ThemePalette.POWER -> PulseThemeColors.PowerDarkScheme to PulseThemeColors.PowerExtraDark
        ThemePalette.FIRE -> PulseThemeColors.FireDarkScheme to PulseThemeColors.FireExtraDark
        ThemePalette.CALM -> PulseThemeColors.CalmDarkScheme to PulseThemeColors.CalmExtraDark
        ThemePalette.PEACE -> PulseThemeColors.PeaceDarkScheme to PulseThemeColors.PeaceExtraDark
        ThemePalette.WARM -> PulseThemeColors.WarmDarkScheme to PulseThemeColors.WarmExtraDark
        ThemePalette.MOOD -> PulseThemeColors.MoodDarkScheme to PulseThemeColors.MoodExtraDark
        ThemePalette.LIGHT -> PulseThemeColors.LightScheme to PulseThemeColors.LightExtra
    }

    // If user explicitly requests Light mode on any dark theme, adapt surfaces
    val resolvedScheme = if (!isDarkMode && palette != ThemePalette.LIGHT) {
        lightColorScheme(
            primary = colorScheme.primary,
            secondary = colorScheme.secondary,
            background = PulseThemeColors.LightScheme.background,
            surface = PulseThemeColors.LightScheme.surface,
            onPrimary = colorScheme.onPrimary,
            onBackground = PulseThemeColors.LightScheme.onBackground,
            onSurface = PulseThemeColors.LightScheme.onSurface
        )
    } else colorScheme

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = resolvedScheme,
            typography = PulseTypography,
            content = content
        )
    }
}

