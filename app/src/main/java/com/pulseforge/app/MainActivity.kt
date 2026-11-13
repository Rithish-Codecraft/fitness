package com.pulseforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pulseforge.app.core.theme.PulseForgeTheme
import com.pulseforge.app.presentation.MainViewModel
import com.pulseforge.app.presentation.analytics.AnalyticsScreen
import com.pulseforge.app.presentation.dashboard.DashboardScreen
import com.pulseforge.app.presentation.navigation.BottomNavItems
import com.pulseforge.app.presentation.navigation.Screen
import com.pulseforge.app.presentation.nutrition.NutritionScreen
import com.pulseforge.app.presentation.onboarding.OnboardingScreen
import com.pulseforge.app.presentation.settings.ThemeSettingsScreen
import com.pulseforge.app.presentation.workout.WorkoutScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val navController = rememberNavController()

            PulseForgeTheme(
                palette = uiState.appSettings.themePalette,
                isDarkMode = uiState.appSettings.isDarkMode
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val showBottomBar = BottomNavItems.any { it.route == currentRoute }
                        if (showBottomBar) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .shadow(
                                            elevation = 10.dp,
                                            shape = RoundedCornerShape(32.dp),
                                            spotColor = Color(0x33000000)
                                        ),
                                    shape = RoundedCornerShape(32.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                                    tonalElevation = 8.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BottomNavItems.forEach { screen ->
                                            val isSelected = currentRoute == screen.route
                                            NavigationBarItem(
                                                icon = {
                                                    Icon(
                                                        imageVector = screen.icon,
                                                        contentDescription = screen.title,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = screen.title,
                                                        fontSize = 10.sp,
                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal
                                                    )
                                                },
                                                selected = isSelected,
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = Color.White,
                                                    selectedTextColor = Color(0xFF7358F6),
                                                    indicatorColor = Color(0xFF7358F6),
                                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                ),
                                                onClick = {
                                                    if (currentRoute != screen.route) {
                                                        navController.navigate(screen.route) {
                                                            popUpTo(Screen.Home.route) { saveState = true }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 1. Home Dashboard
                        composable(Screen.Home.route) {
                            DashboardScreen(
                                uiState = uiState,
                                onNavigateToNutrition = { navController.navigate(Screen.Nutrition.route) },
                                onNavigateToWorkout = { navController.navigate(Screen.Workout.route) },
                                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                                onAddWater = { amount -> viewModel.addWater(amount) }
                            )
                        }

                        // 2. Nutrition
                        composable(Screen.Nutrition.route) {
                            NutritionScreen(
                                uiState = uiState,
                                onAddMeal = { slot, name, grams, cal, p, c, f ->
                                    viewModel.addMeal(slot, name, grams, cal, p, c, f)
                                },
                                onDeleteMeal = { mealId ->
                                    viewModel.deleteMeal(mealId)
                                }
                            )
                        }

                        // 3. Workout
                        composable(Screen.Workout.route) {
                            WorkoutScreen(
                                uiState = uiState,
                                onSaveWorkout = { title, duration, notes, sets ->
                                    viewModel.saveWorkout(title, duration, notes, sets)
                                },
                                onDeleteWorkout = { sessionId ->
                                    viewModel.deleteWorkout(sessionId)
                                }
                            )
                        }

                        // 4. Progress
                        composable(Screen.Progress.route) {
                            AnalyticsScreen(
                                uiState = uiState,
                                onAddMeasurement = { weight, waist, chest, arms, notes ->
                                    viewModel.addMeasurement(weight, waist, chest, arms, notes)
                                }
                            )
                        }

                        // 5. Settings
                        composable(Screen.Settings.route) {
                            ThemeSettingsScreen(
                                uiState = uiState,
                                onSaveSettings = { settings ->
                                    viewModel.saveSettings(settings)
                                },
                                onExportData = {
                                    viewModel.exportData()
                                },
                                onDismissExportDialog = {
                                    viewModel.dismissExportDialog()
                                }
                            )
                        }

                        // 6. Onboarding
                        composable(Screen.Onboarding.route) {
                            OnboardingScreen(
                                currentProfile = uiState.userProfile,
                                currentGoal = uiState.userGoal,
                                onCompleteOnboarding = { profile, goal ->
                                    viewModel.saveProfile(profile)
                                    viewModel.saveGoal(goal)
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
