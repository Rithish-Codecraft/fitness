package com.pulseforge.app

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
                        // Floating Matte Capsule Bottom Navigation Bar (Matching uploaded UI)
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
                                            spotColor = Color(0x30000000)
                                        ),
                                    shape = RoundedCornerShape(32.dp),
                                    color = Color(0xFF1E1F24)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BottomNavItems.forEach { screen ->
                                            val isSelected = currentRoute == screen.route
                                            if (isSelected) {
                                                // Active Tab: Elevated Crisp White Pill
                                                Surface(
                                                    shape = RoundedCornerShape(22.dp),
                                                    color = Color.White,
                                                    shadowElevation = 4.dp
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = screen.icon,
                                                            contentDescription = screen.title,
                                                            tint = Color(0xFFE85B37),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Text(
                                                            text = screen.title,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF16171B)
                                                        )
                                                    }
                                                }
                                            } else {
                                                // Inactive Tab: Clean White Icon
                                                IconButton(
                                                    onClick = {
                                                        if (currentRoute != screen.route) {
                                                            navController.navigate(screen.route) {
                                                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = screen.icon,
                                                        contentDescription = screen.title,
                                                        tint = Color.White.copy(alpha = 0.55f),
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
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
                                onCheckForUpdates = { viewModel.checkForUpdates() },
                                onSimulateStepPulse = { viewModel.triggerHardwareStepPulse(25) },
                                onSyncMobileHealthData = { viewModel.syncMobileHealthData() },
                                onMeasureHeartRate = { viewModel.measureHeartRateViaCamera() }
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
                            AnalyticsScreen(
                                uiState = uiState,
                                onSyncMobileHealthData = { viewModel.syncMobileHealthData() },
                                onMeasureHeartRate = { viewModel.measureHeartRateViaCamera() }
                            )
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

