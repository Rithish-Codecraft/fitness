package com.pulseforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val age: Int,
    val gender: String,
    val heightCm: Double,
    val currentWeightKg: Double,
    val targetWeightKg: Double,
    val targetTimelineWeeks: Int,
    val primaryGoal: String,
    val targetMuscleGroupsJson: String,
    val locality: String,
    val dietPreference: String,
    val activityLevel: String,
    val experienceLevel: String,
    val tdeeKcal: Int,
    val targetKcal: Int,
    val targetProteinG: Int,
    val targetCarbsG: Int,
    val targetFatG: Int,
    val waterTargetMl: Int,
    val waterConsumedMl: Int,
    val activeTheme: String,
    val isDarkMode: Boolean,
    val isOnboarded: Boolean
)

@Entity(
    tableName = "food_items",
    indices = [Index(value = ["date"]), Index(value = ["mealType"])]
)
data class FoodItemEntity(
    @PrimaryKey val id: String,
    val mealType: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val fiberG: Double,
    val date: String,
    val isAiScanned: Boolean,
    val isLabelOcr: Boolean
)

@Entity(
    tableName = "workout_sessions",
    indices = [Index(value = ["date"])]
)
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val date: String,
    val title: String,
    val durationMinutes: Int,
    val kcalBurned: Double,
    val notes: String
)

@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutSessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["workoutSessionId"])]
)
data class ExerciseLogEntity(
    @PrimaryKey val id: String,
    val workoutSessionId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val equipmentType: String,
    val machineSettings: String,
    val cardioDistanceKm: Double?,
    val cardioDurationMinutes: Int?
)

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exerciseLogId"])]
)
data class ExerciseSetEntity(
    @PrimaryKey val id: String,
    val exerciseLogId: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rpe: Double,
    val isPersonalRecord: Boolean,
    val estimated1RmKg: Double
)

@Entity(tableName = "progress_reviews")
data class ProgressReviewEntity(
    @PrimaryKey val id: String,
    val weekNumber: Int,
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
    val fatigueScore: Int,
    val adherenceRating: String,
    val aiFeedbackSummary: String,
    val aiCalorieAdjustment: Int,
    val aiRecommendation: String
)

