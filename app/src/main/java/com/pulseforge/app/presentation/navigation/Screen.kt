package com.pulseforge.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Nutrition : Screen("nutrition", "Nutrition", Icons.Default.Restaurant)
    object Workout : Screen("workout", "Workout", Icons.Default.FitnessCenter)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.ShowChart)
    object Coach : Screen("coach", "AI Coach", Icons.Default.AutoAwesome)
    object Themes : Screen("themes", "Themes", Icons.Default.Palette)
    object Onboarding : Screen("onboarding", "Profile Setup", Icons.Default.Person)
}

val BottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Nutrition,
    Screen.Workout,
    Screen.Analytics,
    Screen.Coach
)

