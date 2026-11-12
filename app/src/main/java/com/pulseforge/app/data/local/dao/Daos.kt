package com.pulseforge.app.data.local.dao

import androidx.room.*
import com.pulseforge.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET waterConsumedMl = :consumed WHERE id = :userId")
    suspend fun updateWater(userId: String, consumed: Int)

    @Query("UPDATE user_profile SET activeTheme = :theme, isDarkMode = :isDark WHERE id = :userId")
    suspend fun updateTheme(userId: String, theme: String, isDark: Boolean)
}

@Dao
interface NutritionDao {
    @Query("SELECT * FROM food_items WHERE date = :date")
    fun getFoodItemsForDate(date: String): Flow<List<FoodItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItemEntity>)

    @Delete
    suspend fun deleteFoodItem(item: FoodItemEntity)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteFoodItemById(id: String)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllWorkoutSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: String): WorkoutSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(exercise: ExerciseLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSets(sets: List<ExerciseSetEntity>)

    @Query("SELECT * FROM exercise_logs WHERE workoutSessionId = :sessionId")
    suspend fun getExercisesForSession(sessionId: String): List<ExerciseLogEntity>

    @Query("SELECT * FROM exercise_sets WHERE exerciseLogId = :exerciseLogId")
    suspend fun getSetsForExercise(exerciseLogId: String): List<ExerciseSetEntity>

    @Query("SELECT MAX(estimated1RmKg) FROM exercise_sets WHERE exerciseLogId IN (SELECT id FROM exercise_logs WHERE exerciseName = :exerciseName)")
    suspend fun getBestEstimated1Rm(exerciseName: String): Double?
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM progress_reviews ORDER BY weekNumber DESC")
    fun getAllReviews(): Flow<List<ProgressReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ProgressReviewEntity)
}

