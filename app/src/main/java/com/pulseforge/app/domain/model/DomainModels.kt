package com.pulseforge.app.domain.model

import java.time.LocalDate
import java.util.UUID

data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val heightCm: Double = 175.0,
    val currentWeightKg: Double = 72.0,
    val targetWeightKg: Double = 76.0,
    val targetTimelineWeeks: Int = 12,
    val primaryGoal: GoalType = GoalType.MUSCLE_GAIN,
    val targetMuscleGroups: List<MuscleGroup> = listOf(MuscleGroup.CHEST, MuscleGroup.BACK_LATS),
    val locality: Locality = Locality.SOUTH_ASIAN,
    val dietPreference: DietPreference = DietPreference.NON_VEGETARIAN,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER,
    val tdeeKcal: Int = 2350,
    val targetKcal: Int = 2650,
    val targetProteinG: Int = 160,
    val targetCarbsG: Int = 320,
    val targetFatG: Int = 75,
    val waterTargetMl: Int = 3000,
    val waterConsumedMl: Int = 1250,
    val activeTheme: ThemePalette = ThemePalette.POWER,
    val isDarkMode: Boolean = false,
    val isOnboarded: Boolean = true
)

data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val mealType: MealType = MealType.LUNCH,
    val name: String,
    val quantity: Double,
    val unit: String = "g",
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val fiberG: Double = 0.0,
    val date: String = LocalDate.now().toString(),
    val isAiScanned: Boolean = false,
    val isLabelOcr: Boolean = false
)

data class MealSummary(
    val mealType: MealType,
    val items: List<FoodItem>,
    val totalCalories: Double = items.sumOf { it.calories },
    val totalProteinG: Double = items.sumOf { it.proteinG },
    val totalCarbsG: Double = items.sumOf { it.carbsG },
    val totalFatG: Double = items.sumOf { it.fatG }
)

data class DailyNutrition(
    val date: String = LocalDate.now().toString(),
    val targetCalories: Int,
    val targetProteinG: Int,
    val targetCarbsG: Int,
    val targetFatG: Int,
    val meals: Map<MealType, List<FoodItem>> = emptyMap(),
    val waterTargetMl: Int = 3000,
    val waterConsumedMl: Int = 0
) {
    val consumedCalories: Double get() = meals.values.flatten().sumOf { it.calories }
    val consumedProteinG: Double get() = meals.values.flatten().sumOf { it.proteinG }
    val consumedCarbsG: Double get() = meals.values.flatten().sumOf { it.carbsG }
    val consumedFatG: Double get() = meals.values.flatten().sumOf { it.fatG }

    val remainingCalories: Double get() = (targetCalories - consumedCalories).coerceAtLeast(0.0)
    val remainingProteinG: Double get() = (targetProteinG - consumedProteinG).coerceAtLeast(0.0)
    val remainingCarbsG: Double get() = (targetCarbsG - consumedCarbsG).coerceAtLeast(0.0)
    val remainingFatG: Double get() = (targetFatG - consumedFatG).coerceAtLeast(0.0)
}

data class ExerciseSet(
    val id: String = UUID.randomUUID().toString(),
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rpe: Double = 8.0,
    val isPersonalRecord: Boolean = false,
    val estimated1RmKg: Double = 0.0
)

data class ExerciseLog(
    val id: String = UUID.randomUUID().toString(),
    val exerciseName: String,
    val muscleGroup: MuscleGroup,
    val equipmentType: EquipmentType,
    val machineSettings: String = "", // e.g. "Seat 4, Pin 55kg"
    val sets: List<ExerciseSet> = emptyList(),
    val cardioDistanceKm: Double? = null,
    val cardioDurationMinutes: Int? = null
) {
    val totalVolumeKg: Double get() = sets.sumOf { it.weightKg * it.reps }
    val bestSetWeightKg: Double get() = sets.maxOfOrNull { it.weightKg } ?: 0.0
    val maxEstimated1Rm: Double get() = sets.maxOfOrNull { it.estimated1RmKg } ?: 0.0
}

data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val date: String = LocalDate.now().toString(),
    val title: String,
    val durationMinutes: Int,
    val kcalBurned: Double,
    val exercises: List<ExerciseLog> = emptyList(),
    val notes: String = ""
) {
    val totalVolumeKg: Double get() = exercises.sumOf { it.totalVolumeKg }
}

data class FeasibilityReport(
    val isFeasible: Boolean,
    val targetWeeks: Int,
    val requestedChangeKg: Double,
    val maxSafeChangeKg: Double,
    val recommendedWeeks: Int,
    val weeklyPaceKg: Double,
    val maxSafeWeeklyPaceKg: Double,
    val statusTitle: String,
    val message: String,
    val dailyCalorieTarget: Int,
    val proteinTargetG: Int,
    val carbsTargetG: Int,
    val fatTargetG: Int
)

data class ShakeRecipe(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val purpose: String,
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val ingredients: List<String>,
    val instructions: String,
    val regionalTag: Locality? = null
)

data class WeeklyProgressReview(
    val id: String = UUID.randomUUID().toString(),
    val weekNumber: Int = 1,
    val startDate: String,
    val endDate: String,
    val startingWeightKg: Double,
    val endingWeightKg: Double,
    val avgWeightKg: Double,
    val calorieAdherencePct: Double,
    val proteinAdherencePct: Double,
    val totalVolumeTonnage: Double,
    val workoutsCompleted: Int,
    val workoutsTarget: Int,
    val fatigueScore: Int, // 1 - 5
    val adherenceRating: String,
    val aiFeedbackSummary: String,
    val aiCalorieAdjustment: Int, // e.g. -150 or +200
    val aiRecommendation: String
)

/**
 * 100% Real Android Phone Hardware Sensor & Telemetry Data
 */
data class PhoneSensorTelemetry(
    val stepCount: Long? = null,
    val stepCadenceSpm: Double? = null,
    val activityType: DetectedActivityType = DetectedActivityType.STILL,
    val distanceMeters: Double? = null,
    val speedKmh: Double? = null,
    val paceMinPerKm: Double? = null,
    val gpsRoute: List<GpsWaypoint> = emptyList(),
    val elevationGainMeters: Double? = null,
    val floorsClimbed: Int? = null,
    val activeCaloriesBurned: Double? = null,
    val sleepEstimate: SleepTelemetryEstimate? = null,
    val isSensorAvailable: Boolean = true
)

enum class DetectedActivityType(val label: String) {
    STILL("Still / Sedentary"),
    WALKING("Walking"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    IN_VEHICLE("In Vehicle"),
    UNKNOWN("Detecting...")
}

data class GpsWaypoint(
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double,
    val timestampMillis: Long
)

data class SleepTelemetryEstimate(
    val sleepStartTime: String = "-",
    val wakeTime: String = "-",
    val totalHours: Double? = null,
    val sleepEfficiencyPct: Int? = null,
    val deepSleepPct: Int? = null
)

/**
 * 7-Category Comprehensive Mobile Health Data Hub Model
 * Aggregates:
 * 1. Activity & Motion Data (Steps, distance, speed, cadence, floors climbed, active/idle time)
 * 2. Exercise & Workouts (Running, cycling, swimming, yoga, weightlifting, HIIT, duration, laps, GPS tracks)
 * 3. Location & Environmental (Real-time & historical GPS, altitude, elevation gain, path mapping)
 * 4. Body Metrics & Vitals (Weight, height, body fat %, lean body mass, bone mass, heart rate)
 * 5. Sleep & Recovery (Duration, stages, movement stillness, readiness score)
 * 6. Nutrition & Hydration (Calorie intake, water consumption, macros, logged meals)
 * 7. Cycle & Health Tracking (Cycle day, phase, ovulation status, symptom notes)
 */
data class MobileHealthDataHub(
    // 1. Activity & Motion Data
    val stepCount: Long? = null,
    val distanceMeters: Double? = null,
    val speedKmh: Double? = null,
    val cadenceSpm: Double? = null,
    val floorsClimbed: Int? = null,
    val activeMinutes: Int? = null,
    val idleMinutes: Int? = null,

    // 2. Exercise & Workouts
    val workoutType: String = "-",
    val workoutDurationMinutes: Int? = null,
    val workoutLaps: Int? = null,
    val workoutCaloriesBurned: Double? = null,
    val workoutGpsWaypoints: List<GpsWaypoint> = emptyList(),

    // 3. Location & Environmental Data
    val currentGpsCoordinates: String = "-",
    val currentAltitudeMeters: Double? = null,
    val elevationGainMeters: Double? = null,
    val outdoorRouteDescription: String = "-",

    // 4. Body Metrics & Vitals
    val weightKg: Double? = null,
    val heightCm: Double? = null,
    val bodyFatPct: Double? = null,
    val leanBodyMassKg: Double? = null,
    val boneMassKg: Double? = null,
    val heartRateBpm: Int? = null,
    val restingHeartRateBpm: Int? = null,
    val heartRateMeasurementSource: String = "-",

    // 5. Sleep & Recovery
    val sleepDurationHours: Double? = null,
    val sleepStagesSummary: String = "-",
    val movementStillnessScore: Int? = null,
    val recoveryReadinessScore: Int? = null,

    // 6. Nutrition & Hydration
    val caloriesConsumed: Double? = null,
    val calorieTarget: Double? = null,
    val waterConsumedMl: Int? = null,
    val waterTargetMl: Int? = null,
    val loggedMealsSummary: String = "-",

    // 7. Cycle & Health Tracking
    val cycleDay: Int? = null,
    val cyclePhase: String = "-",
    val ovulationStatus: String = "-",
    val healthNotes: String = "-",

    // Sync Metadata
    val lastSyncTime: String = "Just now",
    val healthConnectConnected: Boolean = true,
    val companionSourcesConnected: List<String> = listOf("SensorManager", "GPS Provider", "Health Connect", "Camera PPG", "Smart Peripheral")
)

