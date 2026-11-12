package com.pulseforge.app.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.pulseforge.app.domain.model.ThemePalette

data class ExtraThemeColors(
    val accentGlow: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val proteinColor: Color = Color(0xFFE85B37),
    val carbsColor: Color = Color(0xFFF59E0B),
    val fatColor: Color = Color(0xFF3B82F6),
    val calorieRing: Color,
    val floatingNavBackground: Color = Color(0xFF1E1F24),
    val floatingNavActivePill: Color = Color(0xFFFFFFFF),
    val floatingNavActiveText: Color = Color(0xFF16171B)
)

object PulseThemeColors {
    // 1. SIGNATURE PULSE MINIMAL (Warm Terracotta Coral & Soft Alabaster Neumorphic - Exact to uploaded UI)
    val PowerLightScheme = lightColorScheme(
        primary = Color(0xFFE85B37),       // Radiant Coral Orange
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF1E1F24),     // Matte Charcoal
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF1F2F5),    // Soft warm clay
        onBackground = Color(0xFF16171B),  // Crisp charcoal headline
        surface = Color(0xFFFFFFFF),       // Elevated white card
        onSurface = Color(0xFF16171B),
        surfaceVariant = Color(0xFFE6E7EC),
        onSurfaceVariant = Color(0xFF7A7E8D)
    )
    val PowerDarkScheme = darkColorScheme(
        primary = Color(0xFFE85B37),       // Radiant Coral Orange
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFF4F5F8),
        onSecondary = Color(0xFF16171B),
        background = Color(0xFF141518),    // Matte Carbon
        onBackground = Color(0xFFF4F5F8),
        surface = Color(0xFF202228),       // Elevated dark clay surface
        onSurface = Color(0xFFF4F5F8),
        surfaceVariant = Color(0xFF2A2D35),
        onSurfaceVariant = Color(0xFFA1A5B4)
    )
    val PowerExtraLight = ExtraThemeColors(
        accentGlow = Color(0xFFE85B37),
        cardBackground = Color(0xFFFFFFFF),
        cardBorder = Color(0xFFE2E4EB),
        calorieRing = Color(0xFFE85B37),
        floatingNavBackground = Color(0xFF1E1F24),
        floatingNavActivePill = Color(0xFFFFFFFF),
        floatingNavActiveText = Color(0xFF16171B)
    )
    val PowerExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFFE85B37),
        cardBackground = Color(0xFF202228),
        cardBorder = Color(0xFF2E313A),
        calorieRing = Color(0xFFE85B37),
        floatingNavBackground = Color(0xFF1E1F24),
        floatingNavActivePill = Color(0xFFFFFFFF),
        floatingNavActiveText = Color(0xFF16171B)
    )

    // 2. MACROFACTOR AMBER
    val FireDarkScheme = darkColorScheme(
        primary = Color(0xFFFF643D),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFF59E0B),
        onSecondary = Color(0xFF291200),
        background = Color(0xFF0E0E12),
        onBackground = Color(0xFFFBF8F5),
        surface = Color(0xFF17171E),
        onSurface = Color(0xFFFBF8F5),
        surfaceVariant = Color(0xFF242430),
        onSurfaceVariant = Color(0xFFB8B3AB)
    )
    val FireExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFFFF643D),
        cardBackground = Color(0xFF17171E),
        cardBorder = Color(0xFF332D28),
        calorieRing = Color(0xFFFF643D)
    )

    // 3. OURA EMERALD
    val CalmDarkScheme = darkColorScheme(
        primary = Color(0xFF10B981),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFFBBF24),
        onSecondary = Color(0xFF261900),
        background = Color(0xFF070E0B),
        onBackground = Color(0xFFEAFBF5),
        surface = Color(0xFF0E1A15),
        onSurface = Color(0xFFEAFBF5),
        surfaceVariant = Color(0xFF172922),
        onSurfaceVariant = Color(0xFFA6C3B9)
    )
    val CalmExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFF10B981),
        cardBackground = Color(0xFF0E1A15),
        cardBorder = Color(0xFF1E3831),
        calorieRing = Color(0xFF10B981)
    )

    // 4. HEVY COBALT
    val PeaceDarkScheme = darkColorScheme(
        primary = Color(0xFF3B82F6),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF38BDF8),
        onSecondary = Color(0xFF042033),
        background = Color(0xFF0A0E17),
        onBackground = Color(0xFFF0F6FF),
        surface = Color(0xFF131B2C),
        onSurface = Color(0xFFF0F6FF),
        surfaceVariant = Color(0xFF1D2840),
        onSurfaceVariant = Color(0xFFA3B6D4)
    )
    val PeaceExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFF3B82F6),
        cardBackground = Color(0xFF131B2C),
        cardBorder = Color(0xFF233252),
        calorieRing = Color(0xFF3B82F6)
    )

    // 5. APPLE ACTIVITY
    val WarmDarkScheme = darkColorScheme(
        primary = Color(0xFFFA114F),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFA1FF00),
        onSecondary = Color(0xFF132000),
        background = Color(0xFF050505),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF151518),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF222227),
        onSurfaceVariant = Color(0xFFA1A1AA)
    )
    val WarmExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFFFA114F),
        cardBackground = Color(0xFF151518),
        cardBorder = Color(0xFF2D2D33),
        calorieRing = Color(0xFFFA114F)
    )

    // 6. GYMSHARK ONYX
    val MoodDarkScheme = darkColorScheme(
        primary = Color(0xFFFFFFFF),
        onPrimary = Color(0xFF090D11),
        secondary = Color(0xFF94A3B8),
        onSecondary = Color(0xFF090D11),
        background = Color(0xFF0C0C0E),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF18181B),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF27272A),
        onSurfaceVariant = Color(0xFFA1A1AA)
    )
    val MoodExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFFE2E8F0),
        cardBackground = Color(0xFF18181B),
        cardBorder = Color(0xFF3F3F46),
        calorieRing = Color(0xFFFFFFFF)
    )

    // 7. STUDIO PORCELAIN
    val LightScheme = lightColorScheme(
        primary = Color(0xFF2563EB),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF0284C7),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF8FAFC),
        onBackground = Color(0xFF0F172A),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFF1F5F9),
        onSurfaceVariant = Color(0xFF475569)
    )
    val LightExtra = ExtraThemeColors(
        accentGlow = Color(0xFF2563EB),
        cardBackground = Color(0xFFFFFFFF),
        cardBorder = Color(0xFFE2E8F0),
        calorieRing = Color(0xFF2563EB)
    )
}
