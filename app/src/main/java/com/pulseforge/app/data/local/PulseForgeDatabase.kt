package com.pulseforge.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pulseforge.app.data.local.dao.NutritionDao
import com.pulseforge.app.data.local.dao.ReviewDao
import com.pulseforge.app.data.local.dao.UserDao
import com.pulseforge.app.data.local.dao.WorkoutDao
import com.pulseforge.app.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        FoodItemEntity::class,
        WorkoutSessionEntity::class,
        ExerciseLogEntity::class,
        ExerciseSetEntity::class,
        ProgressReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PulseForgeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: PulseForgeDatabase? = null

        fun getInstance(context: Context): PulseForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PulseForgeDatabase::class.java,
                    "pulseforge_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

