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
    val isDarkMode: Boolean = true,
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

