package com.pulseforge.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import com.pulseforge.app.presentation.policy.UserPolicyScreen
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
            var pendingCameraLaunch by remember { mutableStateOf(false) }

            val context = LocalContext.current

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

            val cameraPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                viewModel.setCameraPermissionGranted(isGranted)
                if (isGranted && pendingCameraLaunch) {
                    pendingCameraLaunch = false
                    try {
                        cameraLauncher.launch(null)
                    } catch (_: Exception) {}
                } else {
                    pendingCameraLaunch = false
                }
            }

            val storagePermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { _ ->
                viewModel.syncDataToPulseForgeStorage()
            }

            val healthPermissionLauncher = rememberLauncherForActivityResult(
                contract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
            ) { _ ->
                viewModel.syncAndroidFitnessManager()
                viewModel.refreshPolicyAndPermissions()
            }

            val runtimePermissionsLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { results ->
                results[Manifest.permission.CAMERA]?.let { viewModel.setCameraPermissionGranted(it) }
                viewModel.restartTelemetryListeners()
                viewModel.syncDataToPulseForgeStorage()
                viewModel.refreshPolicyAndPermissions()
            }

            val launchPermissionsRationale = remember(intent) {
                intent?.action == "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE" ||
                    intent?.action == Intent.ACTION_VIEW_PERMISSION_USAGE
            }

            LaunchedEffect(launchPermissionsRationale) {
                if (launchPermissionsRationale) {
                    navController.navigate(Screen.UserPolicy.route) {
                        launchSingleTop = true
                    }
                }
            }

            LaunchedEffect(Unit) {
                val hasCam = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                viewModel.setCameraPermissionGranted(hasCam)

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    storagePermissionLauncher.launch(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                }
                viewModel.syncAndroidFitnessManager()
                viewModel.refreshLifestyleAndSleep()
                viewModel.syncDataToPulseForgeStorage()
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
                                onNavigateToUserPolicy = { navController.navigate(Screen.UserPolicy.route) },
                                onAddWater = { viewModel.addWater(it) },
                                onCheckForUpdates = { viewModel.checkForUpdates() },
                                onSimulateStepPulse = { viewModel.triggerHardwareStepPulse(25) },
                                onOpenUsageSettings = {
                                    try {
                                        startActivity(viewModel.getUsageAccessSettingsIntent())
                                    } catch (_: Exception) {
                                        try {
                                            startActivity(viewModel.getAppInfoSettingsIntent())
                                        } catch (_: Exception) {}
                                    }
                                },
                                onRefreshLifestyle = {
                                    viewModel.refreshLifestyleAndSleep()
                                },
                                onSyncStorage = {
                                    viewModel.syncDataToPulseForgeStorage()
                                },
                                onRequestCamera = {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                onSyncAndroidFit = {
                                    viewModel.syncAndroidFitnessManager()
                                },
                                onRequestHealthPermissions = {
                                    if (uiState.healthConnectStatus.needsProviderInstall) {
                                        try {
                                            startActivity(viewModel.getHealthConnectInstallIntent())
                                        } catch (_: Exception) {}
                                    } else {
                                        healthPermissionLauncher.launch(viewModel.getRequiredHealthPermissions())
                                    }
                                }
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
                                    val hasCam = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasCam) {
                                        try {
                                            cameraLauncher.launch(null)
                                        } catch (_: Exception) {}
                                    } else {
                                        pendingCameraLaunch = true
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                },
                                onScanLabelTrigger = { meal ->
                                    targetMealType = meal
                                    pendingScanType = "LABEL"
                                    val hasCam = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasCam) {
                                        try {
                                            cameraLauncher.launch(null)
                                        } catch (_: Exception) {}
                                    } else {
                                        pendingCameraLaunch = true
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
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
                                policyState = uiState.policyState,
                                onSelectTheme = { viewModel.switchTheme(it) },
                                onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                                onCheckForUpdates = { viewModel.checkForUpdates() },
                                onTriggerDemoUpdate = { viewModel.triggerDemoUpdate() },
                                onUpdateManifestUrl = { viewModel.updateManifestUrl(it) },
                                onOpenUserPolicy = { navController.navigate(Screen.UserPolicy.route) }
                            )
                        }

                        composable(Screen.UserPolicy.route) {
                            UserPolicyScreen(
                                policyState = uiState.policyState,
                                onAcceptPolicy = { viewModel.acceptUserPolicy() },
                                onRevokePolicy = { viewModel.revokeUserPolicyConsent() },
                                onGrantAllRuntimePermissions = {
                                    runtimePermissionsLauncher.launch(viewModel.getRequiredRuntimePermissions())
                                },
                                onGrantSingleItem = { item ->
                                    when (item.category) {
                                        com.pulseforge.app.core.policy.AccessCategory.RUNTIME -> {
                                            if (item.id == "location") {
                                                runtimePermissionsLauncher.launch(
                                                    arrayOf(
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                    )
                                                )
                                            } else {
                                                runtimePermissionsLauncher.launch(arrayOf(item.androidPermission))
                                            }
                                        }
                                        com.pulseforge.app.core.policy.AccessCategory.HEALTH_CONNECT -> {
                                            if (uiState.healthConnectStatus.needsProviderInstall) {
                                                try {
                                                    startActivity(viewModel.getHealthConnectInstallIntent())
                                                } catch (_: Exception) {}
                                            } else {
                                                healthPermissionLauncher.launch(viewModel.getRequiredHealthPermissions())
                                            }
                                        }
                                        com.pulseforge.app.core.policy.AccessCategory.SPECIAL_RESTRICTED -> {
                                            try {
                                                when (item.id) {
                                                    "usage_access" -> startActivity(viewModel.getUsageAccessSettingsIntent())
                                                    "all_files" -> startActivity(viewModel.getAllFilesAccessIntent())
                                                    "install_packages" -> startActivity(viewModel.getInstallUnknownAppsIntent())
                                                    else -> startActivity(viewModel.getAppInfoSettingsIntent())
                                                }
                                            } catch (_: Exception) {
                                                try {
                                                    startActivity(viewModel.getAppInfoSettingsIntent())
                                                } catch (_: Exception) {}
                                            }
                                        }
                                    }
                                },
                                onOpenAppInfoSettings = {
                                    try {
                                        startActivity(viewModel.getAppInfoSettingsIntent())
                                    } catch (_: Exception) {}
                                },
                                onOpenUsageAccessSettings = {
                                    try {
                                        startActivity(viewModel.getUsageAccessSettingsIntent())
                                    } catch (_: Exception) {}
                                },
                                onOpenRestrictedSettingsHelp = {
                                    try {
                                        startActivity(viewModel.getRestrictedSettingsHelpIntent())
                                    } catch (_: Exception) {}
                                },
                                onNavigateBack = { navController.popBackStack() }
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

                    // Startup Missing Permissions Grant Popup
                    var startupPromptDismissed by remember { mutableStateOf(false) }
                    val missingAccessItems = uiState.policyState.accessItems.filter { !it.isGranted }
                    val hasMissingRuntime = missingAccessItems.any {
                        it.category == com.pulseforge.app.core.policy.AccessCategory.RUNTIME
                    }
                    if (!startupPromptDismissed &&
                        missingAccessItems.isNotEmpty() &&
                        currentRoute != Screen.UserPolicy.route
                    ) {
                        AlertDialog(
                            onDismissRequest = { startupPromptDismissed = true },
                            title = {
                                Text(
                                    text = "Grant Android Permissions",
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "PulseForge AI needs the following permissions to enable live sensors, camera food scanning, and sleep analytics:",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    missingAccessItems.forEach { item ->
                                        Text(
                                            text = "• ${item.title}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (hasMissingRuntime) {
                                            runtimePermissionsLauncher.launch(viewModel.getRequiredRuntimePermissions())
                                        } else {
                                            startupPromptDismissed = true
                                            navController.navigate(Screen.UserPolicy.route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                ) {
                                    Text(if (hasMissingRuntime) "Grant All Access" else "Grant Remaining")
                                }
                            },
                            dismissButton = {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(
                                        onClick = {
                                            startupPromptDismissed = true
                                            navController.navigate(Screen.UserPolicy.route) {
                                                launchSingleTop = true
                                            }
                                        }
                                    ) {
                                        Text("Details")
                                    }
                                    TextButton(onClick = { startupPromptDismissed = true }) {
                                        Text("Later")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val hasCam = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.setCameraPermissionGranted(hasCam)
        viewModel.syncAndroidFitnessManager()
        viewModel.refreshLifestyleAndSleep()
        viewModel.syncDataToPulseForgeStorage()
        viewModel.refreshPolicyAndPermissions()
    }
}

