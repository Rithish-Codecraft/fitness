package com.pulseforge.app

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pulseforge.app.core.theme.PulseForgeTheme
import com.pulseforge.app.domain.model.MealType
import com.pulseforge.app.presentation.MainViewModel
import com.pulseforge.app.presentation.analytics.AnalyticsScreen
import com.pulseforge.app.presentation.coach.CoachReviewScreen
import com.pulseforge.app.presentation.components.UpdateDialog
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

            // Camera launcher for Food Plate & Label scanning
            var pendingScanType by remember { mutableStateOf("PLATE") }
            var targetMealType by remember { mutableStateOf(MealType.LUNCH) }

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap: Bitmap? ->
                if (bitmap != null) {
                    if (pendingScanType == "PLATE") {
                        viewModel.scanFoodPlate(bitmap, targetMealType)
                    } else {
                        viewModel.scanNutritionLabel(bitmap, targetMealType)
                    }
                } else {
                    // Fallback to synthetic demonstration image analysis if user cancelled or camera preview not supported
                    val dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                    if (pendingScanType == "PLATE") {
                        viewModel.scanFoodPlate(dummyBitmap, targetMealType)
                    } else {
                        viewModel.scanNutritionLabel(dummyBitmap, targetMealType)
                    }
                }
            }

            PulseForgeTheme(
                palette = uiState.userProfile.activeTheme,
                isDarkMode = uiState.userProfile.isDarkMode
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Only show bottom bar on primary tabs
                        val showBottomBar = BottomNavItems.any { it.route == currentRoute }
                        if (showBottomBar) {
                            NavigationBar {
                                BottomNavItems.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            if (currentRoute != screen.route) {
                                                navController.navigate(screen.route) {
                                                    popUpTo(Screen.Dashboard.route) { saveState = true }
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
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                uiState = uiState,
                                onNavigateToNutrition = { navController.navigate(Screen.Nutrition.route) },
                                onNavigateToWorkout = { navController.navigate(Screen.Workout.route) },
                                onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                                onNavigateToCoach = { navController.navigate(Screen.Coach.route) },
                                onNavigateToThemes = { navController.navigate(Screen.Themes.route) },
                                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                                onAddWater = { viewModel.addWater(it) },
                                onCheckForUpdates = { viewModel.checkForUpdates() }
                            )
                        }

                        composable(Screen.Nutrition.route) {
                            NutritionScreen(
                                uiState = uiState,
                                onAddManualFood = { meal, name, qty, unit, cal, p, c, f ->
                                    viewModel.addManualFood(meal, name, qty, unit, cal, p, c, f)
                                },
                                onDeleteFood = { viewModel.deleteFoodItem(it) },
                                onScanPlateTrigger = { meal ->
                                    targetMealType = meal
                                    pendingScanType = "PLATE"
                                    cameraLauncher.launch(null)
                                },
                                onScanLabelTrigger = { meal ->
                                    targetMealType = meal
                                    pendingScanType = "LABEL"
                                    cameraLauncher.launch(null)
                                },
                                onConfirmScannedFood = { candidates ->
                                    viewModel.confirmScannedFood(candidates)
                                },
                                onDismissVerification = { viewModel.dismissVerification() }
                            )
                        }

                        composable(Screen.Workout.route) {
                            WorkoutScreen(
                                uiState = uiState,
                                onLogWorkout = { title, dur, exercises, notes ->
                                    viewModel.logWorkout(title, dur, exercises, notes)
                                }
                            )
                        }

                        composable(Screen.Analytics.route) {
                            AnalyticsScreen(uiState = uiState)
                        }

                        composable(Screen.Coach.route) {
                            CoachReviewScreen(
                                uiState = uiState,
                                onGenerateNewReview = { fatigue ->
                                    viewModel.generateNewWeeklyReview(fatigue)
                                }
                            )
                        }

                        composable(Screen.Themes.route) {
                            ThemeSettingsScreen(
                                activeTheme = uiState.userProfile.activeTheme,
                                isDarkMode = uiState.userProfile.isDarkMode,
                                manifestUrl = uiState.updateManifestUrl,
                                onSelectTheme = { viewModel.switchTheme(it) },
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                onCheckForUpdates = { viewModel.checkForUpdates() },
                                onTriggerDemoUpdate = { viewModel.triggerDemoUpdate() },
                                onUpdateManifestUrl = { viewModel.updateManifestUrl(it) }
                            )
                        }

                        composable(Screen.Onboarding.route) {
                            OnboardingScreen(
                                currentProfile = uiState.userProfile,
                                onSaveProfile = { viewModel.saveUserProfile(it) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }

                    // Global Web OTA In-App Update Dialog
                    UpdateDialog(
                        updateState = uiState.updateState,
                        onStartDownload = { manifest -> viewModel.downloadAndInstallUpdate(manifest) },
                        onInstallApk = { apkPath -> viewModel.installApkFile(apkPath) },
                        onDismiss = { viewModel.dismissUpdateDialog() }
                    )
                }
            }
        }
    }
}

