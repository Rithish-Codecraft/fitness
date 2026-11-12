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
    val proteinColor: Color = Color(0xFF00E5FF),
    val carbsColor: Color = Color(0xFFFFB300),
    val fatColor: Color = Color(0xFFFF5252),
    val calorieRing: Color
)

object PulseThemeColors {
    // 1. WHOOP VOLT (Stealth Onyx & Bio-Volt — Inspired by WHOOP 4.0 Strain/Recovery)
    val PowerDarkScheme = darkColorScheme(
        primary = Color(0xFF00FF87),       // Whoop Neon Volt
        onPrimary = Color(0xFF051B11),
        secondary = Color(0xFF00E5FF),     // Whoop Electric Cyan
        onSecondary = Color(0xFF002026),
        background = Color(0xFF090A0D),    // Stealth pitch black
        onBackground = Color(0xFFF1F5F9),
        surface = Color(0xFF13151C),       // Whoop stealth card surface
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = Color(0xFF1C1F2B),
        onSurfaceVariant = Color(0xFFA0AEC0)
    )
    val PowerExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFF00FF87),
        cardBackground = Color(0xFF13151C),
        cardBorder = Color(0xFF22283A),
        calorieRing = Color(0xFF00FF87)
    )

    // 2. MACROFACTOR AMBER (Obsidian & Warm Coral — Inspired by MacroFactor dynamic coaching)
    val FireDarkScheme = darkColorScheme(
        primary = Color(0xFFFF643D),       // MacroFactor Coral Orange
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFF59E0B),     // Warm Golden Amber
        onSecondary = Color(0xFF291200),
        background = Color(0xFF0E0E12),    // MacroFactor obsidian
        onBackground = Color(0xFFFBF8F5),
        surface = Color(0xFF17171E),       // MacroFactor card
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

    // 3. OURA EMERALD (Nordic Spruce & Champagne — Inspired by Oura Ring Horizon Readiness)
    val CalmDarkScheme = darkColorScheme(
        primary = Color(0xFF10B981),       // Oura Emerald
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFFBBF24),     // Champagne Amber
        onSecondary = Color(0xFF261900),
        background = Color(0xFF070E0B),    // Nordic Deep Spruce
        onBackground = Color(0xFFEAFBF5),
        surface = Color(0xFF0E1A15),       // Spruce Card
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

    // 4. HEVY COBALT (Electric Cobalt & Gym Slate — Inspired by Hevy & Strong gym tracker)
    val PeaceDarkScheme = darkColorScheme(
        primary = Color(0xFF3B82F6),       // Hevy Electric Blue
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF38BDF8),     // Gym Sky Blue
        onSecondary = Color(0xFF042033),
        background = Color(0xFF0A0E17),    // Hevy Deep Midnight Slate
        onBackground = Color(0xFFF0F6FF),
        surface = Color(0xFF131B2C),       // Slate workout card
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

    // 5. APPLE ACTIVITY (Activity Crimson & Lime — Inspired by Apple Fitness+ Activity Rings)
    val WarmDarkScheme = darkColorScheme(
        primary = Color(0xFFFA114F),       // Apple Move Red
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFFA1FF00),     // Apple Exercise Lime
        onSecondary = Color(0xFF132000),
        background = Color(0xFF050505),    // True Apple Watch OLED Black
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF151518),       // Apple fitness card
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

    // 6. GYMSHARK ONYX (Monochrome Minimalist — Inspired by Gymshark Conditioning / Onyx)
    val MoodDarkScheme = darkColorScheme(
        primary = Color(0xFFFFFFFF),       // Pure High-Contrast White
        onPrimary = Color(0xFF090D11),
        secondary = Color(0xFF94A3B8),     // Titanium Slate
        onSecondary = Color(0xFF090D11),
        background = Color(0xFF0C0C0E),    // Stealth Carbon
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF18181B),       // Zinc Monolith Card
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

    // 7. STUDIO PORCELAIN (Clean Day Mode — Inspired by Apple Health & MacroFactor Light)
    val LightScheme = lightColorScheme(
        primary = Color(0xFF2563EB),       // Royal Cobalt
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF0284C7),     // Deep Cyan
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF8FAFC),    // Pure Porcelain
        onBackground = Color(0xFF0F172A),  // Deep Slate Text
        surface = Color(0xFFFFFFFF),       // Crisp White Card
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
