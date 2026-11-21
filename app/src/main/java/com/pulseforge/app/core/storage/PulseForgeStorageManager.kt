package com.pulseforge.app.core.storage

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pulseforge.app.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Structured PulseForce Device Storage & Multi-Day Recordings Manager.
 * Maintains the organized `pulseforce/` directory tree:
 *   pulseforce/
 *     ├── README_PULSEFORCE.txt
 *     ├── profile/user_profile.json
 *     ├── daily_readings/multi_day_history.json & YYYY-MM-DD_readings.json
 *     ├── nutrition/YYYY-MM-DD_nutrition.json
 *     ├── workouts/workouts_history.json
 *     ├── telemetry/YYYY-MM-DD_sensors.json
 *     └── sleep_lifestyle/YYYY-MM-DD_lifestyle_sleep.json
 */
class PulseForgeStorageManager(private val context: Context) {

    companion object {
        const val ROOT_FOLDER_NAME = "pulseforce"
        private val SUBFOLDERS = listOf(
            "profile",
            "daily_readings",
            "nutrition",
            "workouts",
            "telemetry",
            "sleep_lifestyle"
        )
    }

    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun getPulseForgeDirectory(): File {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val publicDir = File(docsDir, ROOT_FOLDER_NAME)
            if (publicDir.exists() || publicDir.mkdirs()) {
                return publicDir
            }
        }

        val appExtDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        val fallbackDir = File(appExtDir, ROOT_FOLDER_NAME)
        if (!fallbackDir.exists()) {
            fallbackDir.mkdirs()
        }
        return fallbackDir
    }

    fun ensureDirectoryInitialized(): File {
        val root = getPulseForgeDirectory()
        SUBFOLDERS.forEach { sub ->
            val subDir = File(root, sub)
            if (!subDir.exists()) subDir.mkdirs()
        }
        try {
            val readmeText = """
                PULSEFORCE - USER & MULTI-DAY DATA TREE
                Root: Documents/$ROOT_FOLDER_NAME/
                Subfolders: profile, daily_readings, nutrition, workouts, telemetry, sleep_lifestyle
                Created: ${LocalDateTime.now()}
            """.trimIndent()
            val readmeFile = File(root, "README_PULSEFORCE.txt")
            if (!readmeFile.exists()) {
                readmeFile.writeText(readmeText)
                writeScopedFile(null, "README_PULSEFORCE.txt", readmeText, "text/plain")
            }
        } catch (_: Exception) {}
        return root
    }

    private fun writeScopedFile(subfolder: String?, fileName: String, content: String, mimeType: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        val resolver = context.contentResolver
        val relativePath = if (subfolder.isNullOrBlank()) {
            "${Environment.DIRECTORY_DOCUMENTS}/$ROOT_FOLDER_NAME"
        } else {
            "${Environment.DIRECTORY_DOCUMENTS}/$ROOT_FOLDER_NAME/$subfolder"
        }
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf(fileName, "$relativePath%")

        try {
            resolver.query(collection, arrayOf(MediaStore.MediaColumns._ID), selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val existingUri = ContentUris.withAppendedId(collection, cursor.getLong(0))
                    resolver.openOutputStream(existingUri, "wt")?.use { out ->
                        out.write(content.toByteArray(Charsets.UTF_8))
                    }
                    return
                }
            }

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            }
            val uri = resolver.insert(collection, values) ?: return
            resolver.openOutputStream(uri, "wt")?.use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
            }
        } catch (_: Exception) {}
    }

    private fun writeTreeFile(root: File, subfolder: String, fileName: String, json: String) {
        val targetDir = File(root, subfolder)
        if (!targetDir.exists()) targetDir.mkdirs()
        File(targetDir, fileName).writeText(json)
        writeScopedFile(subfolder, fileName, json, "application/json")
    }

    fun loadMultiDayRecordings(): List<DailyRecordingSnapshot> {
        val root = ensureDirectoryInitialized()
        val historyFile = File(File(root, "daily_readings"), "multi_day_history.json")
        if (!historyFile.exists()) return emptyList()
        return try {
            val type = object : TypeToken<List<DailyRecordingSnapshot>>() {}.type
            gson.fromJson<List<DailyRecordingSnapshot>>(historyFile.readText(), type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun exportAllUserData(
        userProfile: UserProfile,
        dailyNutrition: DailyNutrition,
        workoutSessions: List<WorkoutSession>,
        sensorTelemetry: PhoneSensorTelemetry,
        lifestyleSleepAnalysis: LifestyleSleepAnalysis
    ): StorageStatus = withContext(Dispatchers.IO) {
        val root = ensureDirectoryInitialized()
        val today = LocalDate.now().toString()
        val nowFmt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        try {
            // 1. Profile
            writeTreeFile(root, "profile", "user_profile.json", gson.toJson(userProfile))

            // 2. Dated Nutrition
            writeTreeFile(root, "nutrition", "${today}_nutrition.json", gson.toJson(dailyNutrition))

            // 3. Workouts History
            writeTreeFile(root, "workouts", "workouts_history.json", gson.toJson(workoutSessions))

            // 4. Dated Telemetry
            writeTreeFile(root, "telemetry", "${today}_sensors.json", gson.toJson(sensorTelemetry))

            // 5. Dated Sleep & Lifestyle
            writeTreeFile(root, "sleep_lifestyle", "${today}_lifestyle_sleep.json", gson.toJson(lifestyleSleepAnalysis))

            // 6. Multi-Day Daily Recordings Snapshot
            val todayWorkouts = workoutSessions.filter { it.date == today }
            val snapshot = DailyRecordingSnapshot(
                date = today,
                userId = userProfile.id,
                steps = sensorTelemetry.stepCount ?: 0L,
                distanceMeters = sensorTelemetry.distanceMeters ?: 0.0,
                activeCaloriesKcal = sensorTelemetry.activeCaloriesBurned ?: 0.0,
                elevationGainMeters = sensorTelemetry.elevationGainMeters ?: 0.0,
                floorsClimbed = sensorTelemetry.floorsClimbed ?: 0,
                consumedCalories = dailyNutrition.consumedCalories,
                proteinG = dailyNutrition.consumedProteinG,
                carbsG = dailyNutrition.consumedCarbsG,
                fatG = dailyNutrition.consumedFatG,
                waterMl = dailyNutrition.waterConsumedMl,
                workoutCount = todayWorkouts.size,
                workoutVolumeKg = todayWorkouts.sumOf { it.totalVolumeKg },
                sleepHours = lifestyleSleepAnalysis.estimatedSleepDurationHours
                    ?: sensorTelemetry.sleepEstimate?.totalHours
                    ?: 0.0,
                nightScreenMinutes = lifestyleSleepAnalysis.nightScreenTimeMinutes,
                dailyScreenMinutes = lifestyleSleepAnalysis.dailyScreenTimeMinutes,
                capturedAt = nowFmt
            )

            val history = loadMultiDayRecordings()
                .filterNot { it.date == today && it.userId == userProfile.id }
                .toMutableList()
            history.add(0, snapshot)
            val trimmedHistory = history.sortedByDescending { it.date }.take(365)

            writeTreeFile(root, "daily_readings", "${today}_readings.json", gson.toJson(snapshot))
            writeTreeFile(root, "daily_readings", "multi_day_history.json", gson.toJson(trimmedHistory))

            val totalFiles = SUBFOLDERS.sumOf { File(root, it).listFiles()?.size ?: 0 }
            val displayPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "Documents/$ROOT_FOLDER_NAME/"
            } else {
                root.absolutePath
            }

            StorageStatus(
                targetDirectoryPath = displayPath,
                isDirectoryReady = true,
                syncedFileCount = totalFiles,
                recordedDaysCount = trimmedHistory.size,
                lastSyncTimestamp = nowFmt,
                hasStoragePermission = true
            )
        } catch (e: Exception) {
            StorageStatus(
                targetDirectoryPath = root.absolutePath,
                isDirectoryReady = root.exists(),
                syncedFileCount = 0,
                recordedDaysCount = loadMultiDayRecordings().size,
                lastSyncTimestamp = "Error: ${e.localizedMessage ?: "Write failed"}",
                hasStoragePermission = false
            )
        }
    }

    fun getStorageStatus(): StorageStatus {
        val root = ensureDirectoryInitialized()
        val totalFiles = SUBFOLDERS.sumOf { File(root, it).listFiles()?.size ?: 0 }
        val daysCount = loadMultiDayRecordings().size
        val displayPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Documents/$ROOT_FOLDER_NAME/"
        } else {
            root.absolutePath
        }
        return StorageStatus(
            targetDirectoryPath = displayPath,
            isDirectoryReady = root.exists(),
            syncedFileCount = totalFiles,
            recordedDaysCount = daysCount,
            lastSyncTimestamp = if (totalFiles > 0) "Synchronized" else "-",
            hasStoragePermission = true
        )
    }
}
