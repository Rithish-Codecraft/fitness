package com.pulseforge.app.domain.model

import java.time.LocalDate
import java.util.UUID

enum class Gender(val label: String) {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other")
}

enum class GoalType(val label: String, val description: String = "") {
    WEIGHT_LOSS("Weight Loss", "Caloric deficit designed to shed fat while preserving muscle"),
    MUSCLE_GAIN("Muscle Gain", "Caloric surplus focused on building lean muscle mass"),
    MAINTENANCE("Maintenance", "Maintain current weight and optimize metabolic health"),
    ENDURANCE("Endurance & Stamina", "Cardiovascular health, running/cycling stamina"),
    FLEXIBILITY("Flexibility & Yoga", "Mobility, core stabilization, and joint recovery")
}

enum class ActivityLevel(val label: String, val factor: Double) {
    SEDENTARY("Sedentary (Little/No exercise)", 1.2),
    LIGHT("Lightly Active (1-3 days/wk)", 1.375),
    MODERATE("Moderately Active (3-5 days/wk)", 1.55),
    VERY_ACTIVE("Very Active (6-7 days/wk)", 1.725),
    EXTRA_ACTIVE("Extra Active (Physical labor)", 1.9)
}

enum class MealSlot(val label: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snacks")
}

enum class UnitSystem(val label: String) {
    METRIC("Metric (kg, cm, ml)"),
    IMPERIAL("Imperial (lbs, in, oz)")
}

enum class MuscleGroup(val label: String, val category: String) {
    CHEST("Chest / Pectorals", "Upper Body"),
    BACK_LATS("Lats & Upper Back", "Upper Body"),
    SHOULDERS("Shoulders / Deltoids", "Upper Body"),
    BICEPS("Biceps", "Arms"),
    TRICEPS("Triceps", "Arms"),
    QUADS("Quadriceps", "Lower Body"),
    HAMSTRINGS("Hamstrings", "Lower Body"),
    GLUTES("Glutes", "Lower Body"),
    CALVES("Calves", "Lower Body"),
    CORE_ABS("Abs & Core", "Core"),
    FULL_BODY("Full Body", "Compound")
}

enum class ThemePalette(val label: String, val subtitle: String) {
    LAVENDER("Neo-Soft Lilac", "Soft Lavender & Vibrant Purple (DesignByKhyati UI System)"),
    POWER("Pulse Minimal", "Coral Terracotta & Soft Alabaster (Signature UI)"),
    LONGEVITY("Longevity Cyber", "Deep Obsidian & Biometrics"),
    WELLNESS("Macro Tachometer", "Radiant Gauge & Clean Wellness")
}

data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Mitchell",
    val age: Int = 26,
    val gender: Gender = Gender.MALE,
    val heightCm: Double = 178.0,
    val currentWeightKg: Double = 74.5,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val isOnboarded: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserGoal(
    val id: String = UUID.randomUUID().toString(),
    val goalType: GoalType = GoalType.MUSCLE_GAIN,
    val targetWeightKg: Double = 78.0,
    val targetWeeks: Int = 12,
    val bmrKcal: Int = 1750,
    val tdeeKcal: Int = 2450,
    val targetCalories: Int = 2750,
    val targetProteinG: Int = 160,
    val targetCarbsG: Int = 320,
    val targetFatG: Int = 75,
    val waterTargetMl: Int = 3000,
    val waterConsumedMl: Int = 1250
)

data class MealEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: String = LocalDate.now().toString(),
    val slot: MealSlot = MealSlot.LUNCH,
    val foodName: String,
    val quantityGrams: Double = 100.0,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double
)

data class DailyNutritionSummary(
    val date: String = LocalDate.now().toString(),
    val targetCalories: Int = 2750,
    val targetProteinG: Int = 160,
    val targetCarbsG: Int = 320,
    val targetFatG: Int = 75,
    val entries: List<MealEntry> = emptyList(),
    val waterTargetMl: Int = 3000,
    val waterConsumedMl: Int = 1250
) {
    val consumedCalories: Double get() = entries.sumOf { it.calories }
    val consumedProteinG: Double get() = entries.sumOf { it.proteinG }
    val consumedCarbsG: Double get() = entries.sumOf { it.carbsG }
    val consumedFatG: Double get() = entries.sumOf { it.fatG }

    val remainingCalories: Double get() = (targetCalories - consumedCalories).coerceAtLeast(0.0)
    val remainingProteinG: Double get() = (targetProteinG - consumedProteinG).coerceAtLeast(0.0)
    val remainingCarbsG: Double get() = (targetCarbsG - consumedCarbsG).coerceAtLeast(0.0)
    val remainingFatG: Double get() = (targetFatG - consumedFatG).coerceAtLeast(0.0)
}

data class ExerciseSet(
    val id: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val exerciseName: String,
    val muscleGroup: String = "General",
    val setNumber: Int = 1,
    val weightKg: Double = 60.0,
    val reps: Int = 10,
    val rpe: Double = 8.0
) {
    val volumeKg: Double get() = weightKg * reps
}

data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val date: String = LocalDate.now().toString(),
    val title: String = "Hypertrophy Push Session",
    val durationMinutes: Int = 50,
    val notes: String = "",
    val sets: List<ExerciseSet> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalVolumeKg: Double get() = sets.sumOf { it.volumeKg }
    val totalSetsCount: Int get() = sets.size
}

data class BodyMeasurement(
    val id: String = UUID.randomUUID().toString(),
    val date: String = LocalDate.now().toString(),
    val weightKg: Double,
    val waistCm: Double? = null,
    val chestCm: Double? = null,
    val armsCm: Double? = null,
    val notes: String = ""
)

data class AppSettings(
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val isDarkMode: Boolean = true,
    val remindersEnabled: Boolean = true,
    val reminderHour: Int = 20,
    val themePalette: ThemePalette = ThemePalette.LAVENDER
)

data class FeasibilityEstimate(
    val isSafe: Boolean,
    val message: String,
    val recommendedWeeks: Int,
    val suggestedWeeklyRateKg: Double,
    val suggestedCalories: Int = 2200,
    val suggestedProteinG: Int = 150,
    val suggestedCarbsG: Int = 220,
    val suggestedFatG: Int = 65
)
