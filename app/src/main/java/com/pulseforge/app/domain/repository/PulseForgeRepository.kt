package com.pulseforge.app.domain.repository

import com.pulseforge.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PulseForgeRepository {
    // User Profile
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)

    // User Goal
    fun getUserGoal(): Flow<UserGoal?>
    suspend fun saveUserGoal(goal: UserGoal)
    suspend fun addWater(amountMl: Int)

    // Nutrition
    fun getMealsForDate(date: String): Flow<List<MealEntry>>
    fun getAllMeals(): Flow<List<MealEntry>>
    suspend fun addMeal(meal: MealEntry)
    suspend fun deleteMeal(id: String)

    // Workouts
    fun getAllWorkouts(): Flow<List<WorkoutSession>>
    suspend fun saveWorkout(session: WorkoutSession)
    suspend fun deleteWorkout(id: String)

    // Body Measurements
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>
    suspend fun addMeasurement(measurement: BodyMeasurement)

    // Settings
    fun getSettings(): Flow<AppSettings>
    suspend fun saveSettings(settings: AppSettings)

    // Data Export
    suspend fun exportDataAsJson(): String
}
