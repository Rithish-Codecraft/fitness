package com.pulseforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val heightCm: Double,
    val currentWeightKg: Double,
    val activityLevel: String,
    val isOnboarded: Boolean,
    val createdAt: Long
)

@Entity(tableName = "user_goal")
data class UserGoalEntity(
    @PrimaryKey val id: String,
    val goalType: String,
    val targetWeightKg: Double,
    val targetWeeks: Int,
    val bmrKcal: Int,
    val tdeeKcal: Int,
    val targetCalories: Int,
    val targetProteinG: Int,
    val targetCarbsG: Int,
    val targetFatG: Int,
    val waterTargetMl: Int,
    val waterConsumedMl: Int
)

@Entity(
    tableName = "meal_entries",
    indices = [Index(value = ["date"]), Index(value = ["slot"])]
)
data class MealEntryEntity(
    @PrimaryKey val id: String,
    val date: String,
    val slot: String,
    val foodName: String,
    val quantityGrams: Double,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double
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
    val notes: String,
    val createdAt: Long
)

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ExerciseSetEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rpe: Double
)

@Entity(
    tableName = "body_measurements",
    indices = [Index(value = ["date"])]
)
data class BodyMeasurementEntity(
    @PrimaryKey val id: String,
    val date: String,
    val weightKg: Double,
    val waistCm: Double?,
    val chestCm: Double?,
    val armsCm: Double?,
    val notes: String
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: String = "default_settings",
    val unitSystem: String,
    val isDarkMode: Boolean,
    val remindersEnabled: Boolean,
    val reminderHour: Int
)
