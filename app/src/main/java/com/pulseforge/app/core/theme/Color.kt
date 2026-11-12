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

    // 2. LONGEVITY CYBER / STEALTH (Obsidian, Electric Orange #FF6B35 & Cyan #4E89AE - Exact to image 1)
    val LongevityDarkScheme = darkColorScheme(
        primary = Color(0xFFFF6B35),       // Electric Longevity Orange
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF4E89AE),     // Biometric Cyan / Blue
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFF0F1218),    // Deep obsidian graphite
        onBackground = Color(0xFFF1F5F9),
        surface = Color(0xFF1B202A),       // Elevated charcoal slate card
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = Color(0xFF262D3B),
        onSurfaceVariant = Color(0xFF94A3B8)
    )
    val LongevityLightScheme = lightColorScheme(
        primary = Color(0xFFFF6B35),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF4E89AE),
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF0F3F7),
        onBackground = Color(0xFF0F1218),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF0F1218),
        surfaceVariant = Color(0xFFE2E7EF),
        onSurfaceVariant = Color(0xFF64748B)
    )
    val LongevityExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFFFF6B35),
        cardBackground = Color(0xFF1B202A),
        cardBorder = Color(0xFF2B3445),
        proteinColor = Color(0xFFFF6B35),
        carbsColor = Color(0xFF4E89AE),
        fatColor = Color(0xFFFFC048),
        calorieRing = Color(0xFFFF6B35),
        floatingNavBackground = Color(0xFF12161E),
        floatingNavActivePill = Color(0xFFFF6B35),
        floatingNavActiveText = Color(0xFFFFFFFF)
    )
    val LongevityExtraLight = ExtraThemeColors(
        accentGlow = Color(0xFFFF6B35),
        cardBackground = Color(0xFFFFFFFF),
        cardBorder = Color(0xFFDCE2EC),
        proteinColor = Color(0xFFFF6B35),
        carbsColor = Color(0xFF4E89AE),
        fatColor = Color(0xFFFFC048),
        calorieRing = Color(0xFFFF6B35),
        floatingNavBackground = Color(0xFF12161E),
        floatingNavActivePill = Color(0xFFFF6B35),
        floatingNavActiveText = Color(0xFFFFFFFF)
    )

    // 3. MACRO TACHOMETER & CLEAN WELLNESS (Radiant Flame Orange & Daylight Alabaster - Exact to image 2)
    val WellnessLightScheme = lightColorScheme(
        primary = Color(0xFFFF6B35),       // Radiant Fire
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF4F46E5),     // Royal Indigo Accent
        onSecondary = Color(0xFFFFFFFF),
        background = Color(0xFFF7F8FA),    // Pristine porcelain alabaster
        onBackground = Color(0xFF181A20),
        surface = Color(0xFFFFFFFF),       // Elevated crisp white card
        onSurface = Color(0xFF181A20),
        surfaceVariant = Color(0xFFEFF1F5),
        onSurfaceVariant = Color(0xFF717584)
    )
    val WellnessDarkScheme = darkColorScheme(
        primary = Color(0xFFFF6B35),
        onPrimary = Color(0xFFFFFFFF),
        secondary = Color(0xFF818CF8),
        onSecondary = Color(0xFF0F172A),
        background = Color(0xFF111317),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1C1E24),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF282C35),
        onSurfaceVariant = Color(0xFFA0A5B5)
    )
    val WellnessExtraLight = ExtraThemeColors(
        accentGlow = Color(0xFFFF6B35),
        cardBackground = Color(0xFFFFFFFF),
        cardBorder = Color(0xFFE8EBF2),
        proteinColor = Color(0xFF3B82F6),   // Blue underline
        fatColor = Color(0xFF10B981),       // Green underline
        carbsColor = Color(0xFFA855F7),     // Purple underline
        calorieRing = Color(0xFFFF6B35),
        floatingNavBackground = Color(0xFFFFFFFF),
        floatingNavActivePill = Color(0xFFFF6B35),
        floatingNavActiveText = Color(0xFFFFFFFF)
    )
    val WellnessExtraDark = ExtraThemeColors(
        accentGlow = Color(0xFFFF6B35),
        cardBackground = Color(0xFF1C1E24),
        cardBorder = Color(0xFF2E3340),
        proteinColor = Color(0xFF60A5FA),
        fatColor = Color(0xFF34D399),
        carbsColor = Color(0xFFC084FC),
        calorieRing = Color(0xFFFF6B35),
        floatingNavBackground = Color(0xFF181A20),
        floatingNavActivePill = Color(0xFFFF6B35),
        floatingNavActiveText = Color(0xFFFFFFFF)
    )

    // 4. MACROFACTOR AMBER
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
