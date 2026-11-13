package com.pulseforge.app.core.storage

import android.content.Context
import android.os.Environment
import com.google.gson.GsonBuilder
import com.pulseforge.app.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Dedicated PulseForge AI Device Storage Manager.
 * Creates and maintains the official "PulseForge AI" directory on the user device:
 * • Resolves public Documents / PulseForge AI directory for easy user file-manager access
 * • Synchronizes and exports all user, nutrition, workout, telemetry, and lifestyle data to JSON
 * • Generates persistent local backups
 */
class PulseForgeStorageManager(private val context: Context) {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Resolves the primary storage directory for PulseForge AI
     */
    fun getPulseForgeDirectory(): File {
        // Preferred: Public Documents/PulseForge AI (accessible via Android Files app)
        val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val publicDir = File(docsDir, "PulseForge AI")

        if (publicDir.exists() || publicDir.mkdirs()) {
            return publicDir
        }

        // Fallback 1: External root PulseForge AI
        val extRoot = Environment.getExternalStorageDirectory()
        val extDir = File(extRoot, "PulseForge AI")
        if (extDir.exists() || extDir.mkdirs()) {
            return extDir
        }

        // Fallback 2: App-specific external storage files
        val appExtDir = context.getExternalFilesDir(null)
        val fallbackDir = File(appExtDir, "PulseForge AI")
        if (!fallbackDir.exists()) {
            fallbackDir.mkdirs()
        }
        return fallbackDir
    }

    /**
     * Initializes the directory and creates a README metadata file
     */
    fun ensureDirectoryInitialized(): File {
        val dir = getPulseForgeDirectory()
        try {
            val readmeFile = File(dir, "README_PULSEFORGE.txt")
            if (!readmeFile.exists()) {
                readmeFile.writeText(
                    """
                    ========================================
                    PULSEFORGE AI - USER & APP DATA REPOSITORY
                    ========================================
                    This folder stores real user fitness, nutrition, workout,
                    hardware telemetry, and lifestyle sleep analysis records.
                    
                    Synchronized automatically by PulseForge AI.
                    Created: ${LocalDateTime.now()}
                    ========================================
                    """.trimIndent()
                )
            }
        } catch (_: Exception) {}
        return dir
    }

    /**
     * Exports all user profile, nutrition, workouts, sensor telemetry, and lifestyle data
     */
    suspend fun exportAllUserData(
        userProfile: UserProfile,
        dailyNutrition: DailyNutrition,
        workoutSessions: List<WorkoutSession>,
        sensorTelemetry: PhoneSensorTelemetry,
        lifestyleSleepAnalysis: LifestyleSleepAnalysis
    ): StorageStatus = withContext(Dispatchers.IO) {
        val dir = ensureDirectoryInitialized()
        val nowFmt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        try {
            // 1. User Profile
            val profileFile = File(dir, "pulseforge_user_profile.json")
            profileFile.writeText(gson.toJson(userProfile))

            // 2. Daily Nutrition
            val nutritionFile = File(dir, "pulseforge_daily_nutrition.json")
            nutritionFile.writeText(gson.toJson(dailyNutrition))

            // 3. Workouts History
            val workoutsFile = File(dir, "pulseforge_workouts_history.json")
            workoutsFile.writeText(gson.toJson(workoutSessions))

            // 4. Hardware Sensor Telemetry
            val telemetryFile = File(dir, "pulseforge_sensor_telemetry.json")
            telemetryFile.writeText(gson.toJson(sensorTelemetry))

            // 5. Lifestyle & Nighttime Sleep Behavior
            val lifestyleFile = File(dir, "pulseforge_lifestyle_sleep.json")
            lifestyleFile.writeText(gson.toJson(lifestyleSleepAnalysis))

            val fileCount = dir.listFiles()?.size ?: 5

            StorageStatus(
                targetDirectoryPath = dir.absolutePath,
                isDirectoryReady = true,
                syncedFileCount = fileCount,
                lastSyncTimestamp = nowFmt,
                hasStoragePermission = true
            )
        } catch (e: Exception) {
            StorageStatus(
                targetDirectoryPath = dir.absolutePath,
                isDirectoryReady = dir.exists(),
                syncedFileCount = dir.listFiles()?.size ?: 0,
                lastSyncTimestamp = "Error: ${e.localizedMessage ?: "Write failed"}",
                hasStoragePermission = false
            )
        }
    }

    fun getStorageStatus(): StorageStatus {
        val dir = getPulseForgeDirectory()
        val fileCount = dir.listFiles()?.size ?: 0
        return StorageStatus(
            targetDirectoryPath = dir.absolutePath,
            isDirectoryReady = dir.exists(),
            syncedFileCount = fileCount,
            lastSyncTimestamp = if (fileCount > 0) "Synchronized" else "-",
            hasStoragePermission = true
        )
    }
}

