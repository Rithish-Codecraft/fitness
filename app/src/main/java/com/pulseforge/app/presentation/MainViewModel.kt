package com.pulseforge.app.presentation

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pulseforge.app.data.ai.FoodPlateVisionAnalyzer
import com.pulseforge.app.data.ai.NutritionLabelOcr
import com.pulseforge.app.data.ai.ScannedFoodCandidate
import com.pulseforge.app.data.local.PulseForgeDatabase
import com.pulseforge.app.data.repository.PulseForgeRepositoryImpl
import com.pulseforge.app.domain.calculator.*
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.core.updater.AppUpdateManager
import com.pulseforge.app.core.updater.UpdateManifest
import com.pulseforge.app.core.updater.UpdateState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class MainUiState(
    val userProfile: UserProfile = UserProfile(),
    val dailyNutrition: DailyNutrition = DailyNutrition(
        targetCalories = 2650,
        targetProteinG = 160,
        targetCarbsG = 320,
        targetFatG = 75
    ),
    val workoutSessions: List<WorkoutSession> = emptyList(),
    val progressReviews: List<WeeklyProgressReview> = emptyList(),
    val dashboardWidgets: List<DashboardWidgetId> = emptyList(),
    val recommendedShakes: List<ShakeRecipe> = emptyList(),
    val scannedCandidatesForVerification: List<ScannedFoodCandidate> = emptyList(),
    val isVerificationSheetOpen: Boolean = false,
    val pendingMealType: MealType = MealType.LUNCH,
    val isScanning: Boolean = false,
    val activeWorkoutInProgress: Boolean = false,
    val updateState: UpdateState = UpdateState.Idle,
    val updateManifestUrl: String = "https://raw.githubusercontent.com/Rithish-Codecraft/fitness/main/version.json",
    val sensorTelemetry: PhoneSensorTelemetry = PhoneSensorTelemetry()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PulseForgeDatabase.getInstance(application)
    private val repository = PulseForgeRepositoryImpl(database)
    private val telemetryManager = com.pulseforge.app.core.telemetry.PhoneTelemetryManager(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
        observeWorkouts()
        observeReviews()
        refreshDashboardWidgets()
        observeSensorTelemetry()
    }

    private fun observeSensorTelemetry() {
        telemetryManager.startListening(userWeightKg = _uiState.value.userProfile.currentWeightKg)
        viewModelScope.launch {
            telemetryManager.telemetry.collect { telemetry ->
                _uiState.update { it.copy(sensorTelemetry = telemetry) }
            }
        }
    }

    fun triggerHardwareStepPulse(steps: Int = 10) {
        telemetryManager.addRealStepPulse(steps)
    }

    override fun onCleared() {
        super.onCleared()
        telemetryManager.stopListening()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                if (profile != null) {
                    _uiState.update { it.copy(userProfile = profile) }
                    observeDailyNutrition(profile)
                    updateRecommendedShakes(profile)
                    refreshDashboardWidgets()
                } else {
                    // Seed initial default profile if first run
                    val defaultProfile = UserProfile()
                    repository.saveUserProfile(defaultProfile)
                }
            }
        }
    }

    private fun observeDailyNutrition(profile: UserProfile) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            repository.getDailyNutrition(
                date = today,
                targetCalories = profile.targetKcal,
                targetProteinG = profile.targetProteinG,
                targetCarbsG = profile.targetCarbsG,
                targetFatG = profile.targetFatG
            ).collect { nutrition ->
                _uiState.update { it.copy(dailyNutrition = nutrition) }
                updateRecommendedShakes(profile)
            }
        }
    }

    private fun observeWorkouts() {
        viewModelScope.launch {
            repository.getAllWorkoutSessions().collect { sessions ->
                _uiState.update { it.copy(workoutSessions = sessions) }
                if (sessions.isEmpty()) {
                    seedInitialWorkoutSessions()
                }
            }
        }
    }

    private fun observeReviews() {
        viewModelScope.launch {
            repository.getAllReviews().collect { reviews ->
                _uiState.update { it.copy(progressReviews = reviews) }
                if (reviews.isEmpty()) {
                    seedInitialReview()
                }
            }
        }
    }

    fun refreshDashboardWidgets() {
        val profile = _uiState.value.userProfile
        val widgets = DynamicDashboardDispatcher.computeWidgetOrder(
            goalType = profile.primaryGoal,
            currentTime = LocalTime.now(),
            isWorkoutInProgress = _uiState.value.activeWorkoutInProgress
        )
        _uiState.update { it.copy(dashboardWidgets = widgets) }
    }

    private fun updateRecommendedShakes(profile: UserProfile) {
        val remainingProtein = _uiState.value.dailyNutrition.remainingProteinG
        val remainingKcal = _uiState.value.dailyNutrition.remainingCalories
        val shakes = ShakeRecommendationEngine.getRecommendations(
            remainingProteinG = remainingProtein,
            remainingCalories = remainingKcal,
            locality = profile.locality
        )
        _uiState.update { it.copy(recommendedShakes = shakes) }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
            refreshDashboardWidgets()
        }
    }

    fun switchTheme(theme: ThemePalette) {
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(activeTheme = theme)
            repository.updateTheme(updated.id, theme, updated.isDarkMode)
            _uiState.update { it.copy(userProfile = updated) }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(isDarkMode = isDark)
            repository.updateTheme(updated.id, updated.activeTheme, isDark)
            _uiState.update { it.copy(userProfile = updated) }
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val current = _uiState.value.userProfile.waterConsumedMl
            val newTotal = current + amountMl
            repository.updateWater(_uiState.value.userProfile.id, newTotal)
            _uiState.update {
                it.copy(
                    userProfile = it.userProfile.copy(waterConsumedMl = newTotal),
                    dailyNutrition = it.dailyNutrition.copy(waterConsumedMl = newTotal)
                )
            }
        }
    }

    fun addManualFood(
        mealType: MealType,
        name: String,
        quantity: Double,
        unit: String,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double
    ) {
        viewModelScope.launch {
            val item = FoodItem(
                mealType = mealType,
                name = name,
                quantity = quantity,
                unit = unit,
                calories = calories,
                proteinG = protein,
                carbsG = carbs,
                fatG = fat,
                isAiScanned = false
            )
            repository.addFoodItem(item)
        }
    }

    fun deleteFoodItem(id: String) {
        viewModelScope.launch {
            repository.deleteFoodItem(id)
        }
    }

    fun scanFoodPlate(bitmap: Bitmap, mealType: MealType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, pendingMealType = mealType) }
            val candidates = FoodPlateVisionAnalyzer.analyzePlate(bitmap, mealType)
            _uiState.update {
                it.copy(
                    isScanning = false,
                    scannedCandidatesForVerification = candidates,
                    isVerificationSheetOpen = true
                )
            }
        }
    }

    fun scanNutritionLabel(bitmap: Bitmap, mealType: MealType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, pendingMealType = mealType) }
            val candidate = NutritionLabelOcr.parseNutritionLabel(bitmap)
            _uiState.update {
                it.copy(
                    isScanning = false,
                    scannedCandidatesForVerification = listOf(candidate),
                    isVerificationSheetOpen = true
                )
            }
        }
    }

    fun confirmScannedFood(candidates: List<ScannedFoodCandidate>) {
        viewModelScope.launch {
            val mealType = _uiState.value.pendingMealType
            val items = candidates.map { c ->
                FoodItem(
                    mealType = mealType,
                    name = c.name,
                    quantity = c.quantity,
                    unit = c.unit,
                    calories = c.calories,
                    proteinG = c.proteinG,
                    carbsG = c.carbsG,
                    fatG = c.fatG,
                    fiberG = c.fiberG,
                    isAiScanned = true
                )
            }
            repository.addFoodItems(items)
            _uiState.update {
                it.copy(
                    isVerificationSheetOpen = false,
                    scannedCandidatesForVerification = emptyList()
                )
            }
        }
    }

    fun dismissVerification() {
        _uiState.update {
            it.copy(
                isVerificationSheetOpen = false,
                scannedCandidatesForVerification = emptyList()
            )
        }
    }

    fun logWorkout(
        title: String,
        durationMinutes: Int,
        exercises: List<ExerciseLog>,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val userWeight = _uiState.value.userProfile.currentWeightKg
            val totalVolume = exercises.sumOf { it.totalVolumeKg }
            val primaryEquipment = exercises.firstOrNull()?.equipmentType ?: EquipmentType.BARBELL
            val kcalBurned = CalorieBurnCalculator.calculateSessionBurn(
                userWeightKg = userWeight,
                durationMinutes = durationMinutes,
                equipmentType = primaryEquipment,
                totalVolumeKg = totalVolume
            )

            val session = WorkoutSession(
                title = title,
                durationMinutes = durationMinutes,
                kcalBurned = kcalBurned,
                exercises = exercises,
                notes = notes
            )
            repository.logWorkoutSession(session)
            _uiState.update { it.copy(activeWorkoutInProgress = false) }
            refreshDashboardWidgets()
        }
    }

    fun generateNewWeeklyReview(fatigueScore: Int) {
        viewModelScope.launch {
            val profile = _uiState.value.userProfile
            val currentReviews = _uiState.value.progressReviews
            val weekNum = (currentReviews.maxOfOrNull { it.weekNumber } ?: 0) + 1

            val review = AdaptiveCoachEngine.generateWeeklyReview(
                weekNumber = weekNum,
                startingWeightKg = profile.currentWeightKg - 0.4,
                endingWeightKg = profile.currentWeightKg,
                avgWeightKg = profile.currentWeightKg,
                calorieAdherencePct = 92.0,
                proteinAdherencePct = 88.0,
                workoutsCompleted = 4,
                workoutsTarget = 4,
                fatigueScore = fatigueScore,
                goalType = profile.primaryGoal,
                totalTonnage = _uiState.value.workoutSessions.sumOf { it.totalVolumeKg }
            )
            repository.saveReview(review)
        }
    }

    private suspend fun seedInitialWorkoutSessions() {
        val sampleExercise1 = ExerciseLog(
            exerciseName = "Incline Barbell Bench Press",
            muscleGroup = MuscleGroup.CHEST,
            equipmentType = EquipmentType.BARBELL,
            machineSettings = "Incline Bench 30°",
            sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 60.0, reps = 10, estimated1RmKg = 80.0),
                ExerciseSet(setNumber = 2, weightKg = 70.0, reps = 8, estimated1RmKg = 87.0),
                ExerciseSet(setNumber = 3, weightKg = 75.0, reps = 6, estimated1RmKg = 87.2)
            )
        )
        val sampleExercise2 = ExerciseLog(
            exerciseName = "Seated Lat Pulldown",
            muscleGroup = MuscleGroup.BACK_LATS,
            equipmentType = EquipmentType.CABLE,
            machineSettings = "Thigh Pad Notch 3, Wide Mag Grip",
            sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 50.0, reps = 12, estimated1RmKg = 70.0),
                ExerciseSet(setNumber = 2, weightKg = 60.0, reps = 10, estimated1RmKg = 80.0),
                ExerciseSet(setNumber = 3, weightKg = 65.0, reps = 8, estimated1RmKg = 81.0)
            )
        )
        val sampleExercise3 = ExerciseLog(
            exerciseName = "Leg Press (45° Sled)",
            muscleGroup = MuscleGroup.QUADS,
            equipmentType = EquipmentType.PLATE_LOADED_MACHINE,
            machineSettings = "Backrest angle 2, 4x20kg plates/side",
            sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 160.0, reps = 12, estimated1RmKg = 224.0),
                ExerciseSet(setNumber = 2, weightKg = 200.0, reps = 10, estimated1RmKg = 267.0)
            )
        )

        val session1 = WorkoutSession(
            date = LocalDate.now().minusDays(1).toString(),
            title = "Upper Body Hypertrophy (Chest & Lats Focus)",
            durationMinutes = 65,
            kcalBurned = 460.0,
            exercises = listOf(sampleExercise1, sampleExercise2),
            notes = "Strong progressive overload on incline bench."
        )
        val session2 = WorkoutSession(
            date = LocalDate.now().minusDays(3).toString(),
            title = "Lower Body Quad Specialization",
            durationMinutes = 55,
            kcalBurned = 510.0,
            exercises = listOf(sampleExercise3),
            notes = "High RPE on final leg press drop set."
        )

        repository.logWorkoutSession(session1)
        repository.logWorkoutSession(session2)
    }

    private suspend fun seedInitialReview() {
        val initialReview = AdaptiveCoachEngine.generateWeeklyReview(
            weekNumber = 1,
            startingWeightKg = 71.5,
            endingWeightKg = 72.0,
            avgWeightKg = 71.8,
            calorieAdherencePct = 94.0,
            proteinAdherencePct = 91.0,
            workoutsCompleted = 4,
            workoutsTarget = 4,
            fatigueScore = 2,
            goalType = GoalType.MUSCLE_GAIN,
            totalTonnage = 12450.0
        )
        repository.saveReview(initialReview)
    }

    // Web OTA In-App Update Engine
    fun checkForUpdates(customUrl: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(updateState = UpdateState.Checking) }
            val manifestUrl = customUrl ?: _uiState.value.updateManifestUrl
            val currentVersionCode = com.pulseforge.app.BuildConfig.VERSION_CODE

            val result = AppUpdateManager.checkForUpdates(manifestUrl, currentVersionCode)
            result.fold(
                onSuccess = { manifest ->
                    if (manifest != null) {
                        _uiState.update { it.copy(updateState = UpdateState.UpdateAvailable(manifest)) }
                    } else {
                        _uiState.update { it.copy(updateState = UpdateState.UpToDate) }
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(updateState = UpdateState.Error(error.message ?: "Failed to check update")) }
                }
            )
        }
    }

    fun downloadAndInstallUpdate(manifest: UpdateManifest) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            _uiState.update { it.copy(updateState = UpdateState.Downloading(0, 0, manifest.fileSizeBytes)) }

            val downloadResult = AppUpdateManager.downloadApk(context, manifest.apkUrl) { percent, downloaded, total ->
                _uiState.update { it.copy(updateState = UpdateState.Downloading(percent, downloaded, total)) }
            }

            downloadResult.fold(
                onSuccess = { apkFile ->
                    _uiState.update { it.copy(updateState = UpdateState.ReadyToInstall(apkFile.absolutePath)) }
                    AppUpdateManager.installApk(context, apkFile)
                },
                onFailure = { err ->
                    _uiState.update { it.copy(updateState = UpdateState.Error(err.message ?: "Download failed")) }
                }
            )
        }
    }

    fun installApkFile(path: String) {
        val context = getApplication<Application>().applicationContext
        val file = File(path)
        AppUpdateManager.installApk(context, file)
    }

    fun triggerDemoUpdate() {
        val demoManifest = UpdateManifest(
            versionCode = com.pulseforge.app.BuildConfig.VERSION_CODE + 1,
            versionName = "1.1.0",
            apkUrl = "https://raw.githubusercontent.com/Rithish-Codecraft/fitness/main/releases/app-release.apk",
            releaseNotes = "• Enhanced food camera neural recognition\n• Added custom theme color saturation controls\n• Improved progressive overload 1RM volume metrics\n• Stability & offline sync updates",
            isMandatory = false,
            fileSizeBytes = 52720191
        )
        _uiState.update { it.copy(updateState = UpdateState.UpdateAvailable(demoManifest)) }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(updateState = UpdateState.Idle) }
    }

    fun updateManifestUrl(url: String) {
        _uiState.update { it.copy(updateManifestUrl = url) }
    }
}

