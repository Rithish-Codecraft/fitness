package com.pulseforge.app.core.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object AppUpdateManager {

    private val gson = Gson()

    /**
     * Checks remote web endpoint for new version manifest.
     */
    suspend fun checkForUpdates(
        manifestUrl: String,
        currentVersionCode: Int
    ): Result<UpdateManifest?> = withContext(Dispatchers.IO) {
        try {
            val url = URL(manifestUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 8000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val json = connection.inputStream.bufferedReader().use { it.readText() }
                val manifest = gson.fromJson(json, UpdateManifest::class.java)
                if (manifest.versionCode > currentVersionCode) {
                    Result.success(manifest)
                } else {
                    Result.success(null) // Up to date
                }
            } else if (connection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                if (manifestUrl.contains("username/pulseforge-releases")) {
                    Result.failure(Exception("No web release server configured yet.\n\nTo test the Web OTA update workflow right now, tap the Palette icon at the top right and click 'Test Update'.\n\nWhen ready, you can paste your own GitHub or web version.json URL in Theme Settings."))
                } else {
                    Result.failure(Exception("Update server returned 404 (Not Found).\nPlease verify your version.json URL in Theme Settings."))
                }
            } else {
                Result.failure(Exception("HTTP Error ${connection.responseCode} while checking update."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads the APK file from the web URL to the local app cache directory
     * with real-time download percentage progress reporting.
     */
    suspend fun downloadApk(
        context: Context,
        apkUrl: String,
        onProgress: (percent: Int, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val destinationDir = File(context.cacheDir, "updates").apply { mkdirs() }
            val destinationFile = File(destinationDir, "pulseforge_latest.apk")
            if (destinationFile.exists()) destinationFile.delete()

            val url = URL(apkUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 20000
                requestMethod = "GET"
            }

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("HTTP error ${connection.responseCode} while downloading APK."))
            }

            val fileLength = connection.contentLengthLong
            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(destinationFile)

            val buffer = ByteArray(8192)
            var totalBytesRead = 0L
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (fileLength > 0) {
                    val percent = ((totalBytesRead * 100) / fileLength).toInt()
                    onProgress(percent, totalBytesRead, fileLength)
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Launches Android PackageInstaller via FileProvider and ACTION_VIEW intent.
     */
    fun installApk(context: Context, apkFile: File): Result<Unit> {
        return try {
            if (!apkFile.exists()) {
                return Result.failure(Exception("APK file not found at ${apkFile.absolutePath}"))
            }

            // Check if app has permission to install packages on Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                    return Result.failure(Exception("Please grant permission to install from this app and retry."))
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(installIntent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

