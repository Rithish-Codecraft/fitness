package com.pulseforge.app.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pulseforge.app.data.local.PulseForgeDatabase
import com.pulseforge.app.data.repository.PulseForgeRepositoryImpl
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.domain.repository.PulseForgeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class MainUiState(
    val userProfile: UserProfile = UserProfile(),
    val userGoal: UserGoal = UserGoal(),
    val dailyNutrition: DailyNutritionSummary = DailyNutritionSummary(),
    val workoutSessions: List<WorkoutSession> = emptyList(),
    val bodyMeasurements: List<BodyMeasurement> = emptyList(),
    val appSettings: AppSettings = AppSettings(),
    val selectedDate: String = LocalDate.now().toString(),
    val exportedJson: String? = null,
    val isExportDialogVisible: Boolean = false,
    val isLoading: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = PulseForgeDatabase.getInstance(application)
    private val repository: PulseForgeRepository = PulseForgeRepositoryImpl(database)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeUserProfile()
        observeUserGoal()
        observeDailyMeals()
        observeWorkouts()
        observeMeasurements()
        observeSettings()
        seedInitialDataIfEmpty()
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                if (profile != null) {
                    _uiState.update { it.copy(userProfile = profile) }
                }
            }
        }
    }

    private fun observeUserGoal() {
        viewModelScope.launch {
            repository.getUserGoal().collect { goal ->
                if (goal != null) {
                    _uiState.update { state ->
                        state.copy(
                            userGoal = goal,
                            dailyNutrition = state.dailyNutrition.copy(
                                targetCalories = goal.targetCalories,
                                targetProteinG = goal.targetProteinG,
                                targetCarbsG = goal.targetCarbsG,
                                targetFatG = goal.targetFatG,
                                waterTargetMl = goal.waterTargetMl,
                                waterConsumedMl = goal.waterConsumedMl
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeDailyMeals() {
        viewModelScope.launch {
            repository.getMealsForDate(LocalDate.now().toString()).collect { meals ->
                _uiState.update { state ->
                    val updatedSummary = state.dailyNutrition.copy(
                        date = state.selectedDate,
                        entries = meals
                    )
                    state.copy(dailyNutrition = updatedSummary)
                }
            }
        }
    }

    private fun observeWorkouts() {
        viewModelScope.launch {
            repository.getAllWorkouts().collect { workouts ->
                _uiState.update { it.copy(workoutSessions = workouts) }
            }
        }
    }

    private fun observeMeasurements() {
        viewModelScope.launch {
            repository.getAllMeasurements().collect { measurements ->
                _uiState.update { it.copy(bodyMeasurements = measurements) }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            repository.getSettings().collect { settings ->
                _uiState.update { it.copy(appSettings = settings) }
            }
        }
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            val existingProfile = repository.getUserProfile().firstOrNull()
            if (existingProfile == null) {
                val defaultProfile = UserProfile(
                    name = "Mitchell",
                    age = 26,
                    gender = Gender.MALE,
                    heightCm = 178.0,
                    currentWeightKg = 74.5,
                    activityLevel = ActivityLevel.MODERATE,
                    isOnboarded = true
                )
                repository.saveUserProfile(defaultProfile)

                val defaultGoal = UserGoal(
                    goalType = GoalType.MUSCLE_GAIN,
                    targetWeightKg = 78.0,
                    targetWeeks = 12,
                    bmrKcal = 1750,
                    tdeeKcal = 2450,
                    targetCalories = 2750,
                    targetProteinG = 160,
                    targetCarbsG = 320,
                    targetFatG = 75,
                    waterTargetMl = 3000,
                    waterConsumedMl = 1250
                )
                repository.saveUserGoal(defaultGoal)

                // Seed initial verified meals
                val today = LocalDate.now().toString()
                repository.addMeal(
                    MealEntry(
                        date = today,
                        slot = MealSlot.BREAKFAST,
                        foodName = "Rolled Oats & Whey Protein",
                        quantityGrams = 80.0,
                        calories = 420.0,
                        proteinG = 35.0,
                        carbsG = 55.0,
                        fatG = 6.0
                    )
                )
                repository.addMeal(
                    MealEntry(
                        date = today,
                        slot = MealSlot.LUNCH,
                        foodName = "Grilled Chicken Breast & Basmati Rice",
                        quantityGrams = 200.0,
                        calories = 650.0,
                        proteinG = 58.0,
                        carbsG = 70.0,
                        fatG = 12.0
                    )
                )

                // Seed initial verified workout session
                val sessionId = UUID.randomUUID().toString()
                val session = WorkoutSession(
                    id = sessionId,
                    date = today,
                    title = "Hypertrophy Push Routine",
                    durationMinutes = 55,
                    notes = "RPE 8.5 on top sets. Strict form on dumbbell presses.",
                    sets = listOf(
                        ExerciseSet(
                            sessionId = sessionId,
                            exerciseName = "Incline Dumbbell Press",
                            muscleGroup = "Chest",
                            setNumber = 1,
                            weightKg = 32.0,
                            reps = 10,
                            rpe = 8.0
                        ),
                        ExerciseSet(
                            sessionId = sessionId,
                            exerciseName = "Incline Dumbbell Press",
                            muscleGroup = "Chest",
                            setNumber = 2,
                            weightKg = 32.0,
                            reps = 10,
                            rpe = 8.5
                        ),
                        ExerciseSet(
                            sessionId = sessionId,
                            exerciseName = "Barbell Overhead Press",
                            muscleGroup = "Shoulders",
                            setNumber = 1,
                            weightKg = 50.0,
                            reps = 8,
                            rpe = 8.0
                        ),
                        ExerciseSet(
                            sessionId = sessionId,
                            exerciseName = "Cable Tricep Pushdown",
                            muscleGroup = "Triceps",
                            setNumber = 1,
                            weightKg = 25.0,
                            reps = 12,
                            rpe = 9.0
                        )
                    )
                )
                repository.saveWorkout(session)

                // Seed historical body weight measurements for clean progress graphs
                val yesterday = LocalDate.now().minusDays(1).toString()
                val twoDaysAgo = LocalDate.now().minusDays(2).toString()
                val threeDaysAgo = LocalDate.now().minusDays(3).toString()
                repository.addMeasurement(BodyMeasurement(date = threeDaysAgo, weightKg = 74.0, waistCm = 81.0, chestCm = 102.0, armsCm = 37.0, notes = "Morning fasted"))
                repository.addMeasurement(BodyMeasurement(date = twoDaysAgo, weightKg = 74.2, waistCm = 81.0, chestCm = 102.5, armsCm = 37.0, notes = "Post-rest day"))
                repository.addMeasurement(BodyMeasurement(date = yesterday, weightKg = 74.4, waistCm = 80.8, chestCm = 103.0, armsCm = 37.2, notes = "Pump intact"))
                repository.addMeasurement(BodyMeasurement(date = today, weightKg = 74.5, waistCm = 80.5, chestCm = 103.2, armsCm = 37.5, notes = "Current benchmark"))

                repository.saveSettings(AppSettings(isDarkMode = true, themePalette = ThemePalette.LAVENDER))
            }
        }
    }

    // Public Actions
    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun saveGoal(goal: UserGoal) {
        viewModelScope.launch {
            repository.saveUserGoal(goal)
        }
    }

    fun addMeal(
        slot: MealSlot,
        foodName: String,
        grams: Double,
        calories: Double,
        protein: Double,
        carbs: Double,
        fat: Double
    ) {
        viewModelScope.launch {
            val entry = MealEntry(
                date = _uiState.value.selectedDate,
                slot = slot,
                foodName = foodName,
                quantityGrams = grams,
                calories = calories,
                proteinG = protein,
                carbsG = carbs,
                fatG = fat
            )
            repository.addMeal(entry)
        }
    }

    fun deleteMeal(mealId: String) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(amountMl)
        }
    }

    fun saveWorkout(
        title: String,
        durationMinutes: Int,
        notes: String,
        sets: List<ExerciseSet>
    ) {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val session = WorkoutSession(
                id = sessionId,
                date = LocalDate.now().toString(),
                title = title.ifBlank { "Workout Session" },
                durationMinutes = durationMinutes.coerceAtLeast(1),
                notes = notes,
                sets = sets.mapIndexed { index, set ->
                    set.copy(sessionId = sessionId, setNumber = index + 1)
                }
            )
            repository.saveWorkout(session)
        }
    }

    fun deleteWorkout(sessionId: String) {
        viewModelScope.launch {
            repository.deleteWorkout(sessionId)
        }
    }

    fun addMeasurement(
        weightKg: Double,
        waistCm: Double? = null,
        chestCm: Double? = null,
        armsCm: Double? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val measurement = BodyMeasurement(
                date = LocalDate.now().toString(),
                weightKg = weightKg,
                waistCm = waistCm,
                chestCm = chestCm,
                armsCm = armsCm,
                notes = notes
            )
            repository.addMeasurement(measurement)
            // Also update current weight on profile
            val currentProfile = _uiState.value.userProfile
            repository.saveUserProfile(currentProfile.copy(currentWeightKg = weightKg))
        }
    }

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch {
            repository.saveSettings(settings)
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val json = repository.exportDataAsJson()
            _uiState.update { it.copy(exportedJson = json, isExportDialogVisible = true) }
        }
    }

    fun dismissExportDialog() {
        _uiState.update { it.copy(isExportDialogVisible = false) }
    }
}
