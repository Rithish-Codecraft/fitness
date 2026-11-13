package com.pulseforge.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Nutrition : Screen("nutrition", "Nutrition", Icons.Default.Restaurant)
    object Workout : Screen("workout", "Workout", Icons.Default.FitnessCenter)
    object Progress : Screen("progress", "Progress", Icons.Default.TrendingUp)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Onboarding : Screen("onboarding", "Profile Setup", Icons.Default.Person)
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Nutrition,
    Screen.Workout,
    Screen.Progress,
    Screen.Settings
)
