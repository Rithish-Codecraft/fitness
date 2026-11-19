package com.pulseforge.app.core.storage

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.GsonBuilder
import com.pulseforge.app.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Dedicated PulseForge AI Device Storage Manager (Android Scoped Storage Compliant).
 * • Android 10+ (API 29+): Exports to public Documents/PulseForge AI via MediaStore.Files (no broad storage permission required)
 * • Android 8-9 (API 26-28): Exports to public Documents/PulseForge AI or app-specific external storage
 * • Also maintains app-private external mirror for fast offline access
 */
class PulseForgeStorageManager(private val context: Context) {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Resolves the app-specific external directory for PulseForge AI
     */
    fun getPulseForgeDirectory(): File {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val publicDir = File(docsDir, "PulseForge AI")
            if (publicDir.exists() || publicDir.mkdirs()) {
                return publicDir
            }
        }

        val appExtDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        val fallbackDir = File(appExtDir, "PulseForge AI")
        if (!fallbackDir.exists()) {
            fallbackDir.mkdirs()
        }
        return fallbackDir
    }

    /**
     * Writes a text file to public Documents/PulseForge AI using Scoped Storage MediaStore on API 29+.
     */
    private fun writeScopedDocumentFile(fileName: String, content: String, mimeType: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        val resolver = context.contentResolver
        val relativePath = "${Environment.DIRECTORY_DOCUMENTS}/PulseForge AI"
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf(fileName, "$relativePath%")

        try {
            resolver.query(collection, arrayOf(MediaStore.MediaColumns._ID), selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(0)
                    val existingUri = android.content.ContentUris.withAppendedId(collection, id)
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
        } catch (_: Exception) {
            // App-specific storage mirror remains available as fallback
        }
    }

    /**
     * Initializes the directory and creates a README metadata file
     */
    fun ensureDirectoryInitialized(): File {
        val dir = getPulseForgeDirectory()
        try {
            val readmeText = """
                ========================================
                PULSEFORGE AI - USER & APP DATA REPOSITORY
                ========================================
                This folder stores real user fitness, nutrition, workout,
                hardware telemetry, and lifestyle sleep analysis records.

                Synchronized automatically by PulseForge AI.
                Created: ${LocalDateTime.now()}
                ========================================
            """.trimIndent()
            val readmeFile = File(dir, "README_PULSEFORGE.txt")
            if (!readmeFile.exists()) {
                readmeFile.writeText(readmeText)
                writeScopedDocumentFile("README_PULSEFORGE.txt", readmeText, "text/plain")
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
            val exports = listOf(
                "pulseforge_user_profile.json" to gson.toJson(userProfile),
                "pulseforge_daily_nutrition.json" to gson.toJson(dailyNutrition),
                "pulseforge_workouts_history.json" to gson.toJson(workoutSessions),
                "pulseforge_sensor_telemetry.json" to gson.toJson(sensorTelemetry),
                "pulseforge_lifestyle_sleep.json" to gson.toJson(lifestyleSleepAnalysis)
            )

            exports.forEach { (fileName, json) ->
                File(dir, fileName).writeText(json)
                writeScopedDocumentFile(fileName, json, "application/json")
            }

            val fileCount = dir.listFiles()?.size ?: exports.size
            val displayPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "Documents/PulseForge AI"
            } else {
                dir.absolutePath
            }

            StorageStatus(
                targetDirectoryPath = displayPath,
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
        val displayPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "Documents/PulseForge AI"
        } else {
            dir.absolutePath
        }
        return StorageStatus(
            targetDirectoryPath = displayPath,
            isDirectoryReady = dir.exists(),
            syncedFileCount = fileCount,
            lastSyncTimestamp = if (fileCount > 0) "Synchronized" else "-",
            hasStoragePermission = true
        )
    }
}
