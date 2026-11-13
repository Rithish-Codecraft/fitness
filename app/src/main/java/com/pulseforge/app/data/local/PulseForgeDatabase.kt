package com.pulseforge.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pulseforge.app.data.local.dao.*
import com.pulseforge.app.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        UserGoalEntity::class,
        MealEntryEntity::class,
        WorkoutSessionEntity::class,
        ExerciseSetEntity::class,
        BodyMeasurementEntity::class,
        AppSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PulseForgeDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userGoalDao(): UserGoalDao
    abstract fun mealEntryDao(): MealEntryDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun appSettingsDao(): AppSettingsDao

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
