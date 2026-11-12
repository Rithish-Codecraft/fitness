package com.pulseforge.app.domain.repository

import com.pulseforge.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PulseForgeRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun saveUserProfile(profile: UserProfile)
    suspend fun updateWater(userId: String, consumedMl: Int)
    suspend fun updateTheme(userId: String, theme: ThemePalette, isDarkMode: Boolean)

    fun getDailyNutrition(date: String, targetCalories: Int, targetProteinG: Int, targetCarbsG: Int, targetFatG: Int): Flow<DailyNutrition>
    suspend fun addFoodItem(item: FoodItem)
    suspend fun addFoodItems(items: List<FoodItem>)
    suspend fun deleteFoodItem(id: String)

    fun getAllWorkoutSessions(): Flow<List<WorkoutSession>>
    suspend fun logWorkoutSession(session: WorkoutSession)

    fun getAllReviews(): Flow<List<WeeklyProgressReview>>
    suspend fun saveReview(review: WeeklyProgressReview)
}

