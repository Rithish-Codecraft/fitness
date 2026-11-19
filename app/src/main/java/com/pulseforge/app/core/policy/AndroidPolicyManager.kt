package com.pulseforge.app.core.policy

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.pulseforge.app.core.telemetry.HealthConnectStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class AccessCategory {
    RUNTIME,
    SPECIAL_RESTRICTED,
    HEALTH_CONNECT
}

data class PermissionAccessItem(
    val id: String,
    val title: String,
    val androidPermission: String,
    val purpose: String,
    val category: AccessCategory,
    val isGranted: Boolean,
    val isApplicable: Boolean = true,
    val restrictionNote: String? = null
)

data class AndroidPolicyState(
    val policyVersion: String = AndroidPolicyManager.POLICY_VERSION,
    val effectiveDate: String = AndroidPolicyManager.POLICY_EFFECTIVE_DATE,
    val isPolicyAccepted: Boolean = false,
    val acceptedTimestamp: String? = null,
    val accessItems: List<PermissionAccessItem> = emptyList(),
    val grantedCount: Int = 0,
    val totalApplicableCount: Int = 0
)

/**
 * Manages Android User Data Policy disclosures, explicit user consent,
 * runtime permissions, and Android 13+ Restricted Settings (support.google.com/android/answer/12623953).
 */
class AndroidPolicyManager(private val context: Context) {

    companion object {
        const val POLICY_VERSION = "2.0.0"
        const val POLICY_EFFECTIVE_DATE = "2026-11-20"
        const val RESTRICTED_SETTINGS_HELP_URL = "https://support.google.com/android/answer/12623953"

        private const val PREFS_NAME = "pulseforge_policy_prefs"
        private const val KEY_ACCEPTED_VERSION = "accepted_policy_version"
        private const val KEY_ACCEPTED_AT = "accepted_policy_timestamp"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getRequiredRuntimePermissions(): Array<String> {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        return permissions.distinct().toTypedArray()
    }

    fun evaluatePolicyState(healthConnectStatus: HealthConnectStatus): AndroidPolicyState {
        val acceptedVersion = prefs.getString(KEY_ACCEPTED_VERSION, null)
        val isAccepted = acceptedVersion == POLICY_VERSION
        val acceptedAt = if (isAccepted) prefs.getString(KEY_ACCEPTED_AT, null) else null

        val items = buildAccessItems(healthConnectStatus).filter { it.isApplicable }
        val grantedCount = items.count { it.isGranted }

        return AndroidPolicyState(
            policyVersion = POLICY_VERSION,
            effectiveDate = POLICY_EFFECTIVE_DATE,
            isPolicyAccepted = isAccepted,
            acceptedTimestamp = acceptedAt,
            accessItems = items,
            grantedCount = grantedCount,
            totalApplicableCount = items.size
        )
    }

    fun acceptUserPolicy() {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        prefs.edit()
            .putString(KEY_ACCEPTED_VERSION, POLICY_VERSION)
            .putString(KEY_ACCEPTED_AT, timestamp)
            .apply()
    }

    fun revokeUserPolicyConsent() {
        prefs.edit()
            .remove(KEY_ACCEPTED_VERSION)
            .remove(KEY_ACCEPTED_AT)
            .apply()
    }

    private fun isRuntimeGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun hasUsageStatsAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasAllFilesAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // Scoped Storage MediaStore.Files writes to Documents/PulseForge AI without special permission
        } else {
            isRuntimeGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    fun canInstallUnknownApps(): Boolean = true

    private fun buildAccessItems(healthConnectStatus: HealthConnectStatus): List<PermissionAccessItem> {
        val activityRecognitionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isRuntimeGranted(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            true
        }

        val notificationsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isRuntimeGranted(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

        val locationGranted = isRuntimeGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
            isRuntimeGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

        return listOf(
            PermissionAccessItem(
                id = "activity_recognition",
                title = "Physical Activity & Step Sensors",
                androidPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Manifest.permission.ACTIVITY_RECOGNITION
                } else {
                    "Hardware Step Sensor"
                },
                purpose = "Reads live device step counter, cadence, and walking/running detection locally on-device.",
                category = AccessCategory.RUNTIME,
                isGranted = activityRecognitionGranted,
                isApplicable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            ),
            PermissionAccessItem(
                id = "location",
                title = "Workout GPS Location",
                androidPermission = Manifest.permission.ACCESS_FINE_LOCATION,
                purpose = "Measures outdoor distance, pace, and workout route coordinates only during active tracking.",
                category = AccessCategory.RUNTIME,
                isGranted = locationGranted
            ),
            PermissionAccessItem(
                id = "camera",
                title = "Camera (Plate & Label OCR)",
                androidPermission = Manifest.permission.CAMERA,
                purpose = "Captures food plates and nutrition labels for on-device ML Kit text recognition.",
                category = AccessCategory.RUNTIME,
                isGranted = isRuntimeGranted(Manifest.permission.CAMERA)
            ),
            PermissionAccessItem(
                id = "notifications",
                title = "Training & Hydration Alerts",
                androidPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.POST_NOTIFICATIONS
                } else {
                    "POST_NOTIFICATIONS"
                },
                purpose = "Delivers workout reminders, recovery nudges, and OTA update notifications.",
                category = AccessCategory.RUNTIME,
                isGranted = notificationsGranted,
                isApplicable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ),
            PermissionAccessItem(
                id = "health_connect",
                title = "Health Connect Fitness & Sleep",
                androidPermission = "android.permission.health.READ_*",
                purpose = "Syncs Steps, Distance, Speed, Elevation, Calories, Sleep, and Exercise records with user consent.",
                category = AccessCategory.HEALTH_CONNECT,
                isGranted = healthConnectStatus.hasPermissions,
                restrictionNote = if (healthConnectStatus.needsProviderInstall) {
                    "Health Connect app update/install required from Google Play."
                } else null
            ),
            PermissionAccessItem(
                id = "usage_access",
                title = "Usage Access (Nighttime Sleep Hygiene)",
                androidPermission = "android.permission.PACKAGE_USAGE_STATS",
                purpose = "Detects late-night screen time (10 PM - 6 AM) to estimate circadian sleep disruption locally.",
                category = AccessCategory.SPECIAL_RESTRICTED,
                isGranted = hasUsageStatsAccess(),
                restrictionNote = "Android 13+ Restricted Setting for sideloaded APKs: Open App Info -> 3-dot menu -> Allow restricted settings, then enable Usage Access."
            ),
            PermissionAccessItem(
                id = "all_files",
                title = "Dedicated Documents Backup Folder",
                androidPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    "Scoped Storage (MediaStore)"
                } else {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                purpose = "Exports user-owned JSON backups to Documents/PulseForge AI using Android Scoped Storage.",
                category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AccessCategory.RUNTIME
                } else {
                    AccessCategory.SPECIAL_RESTRICTED
                },
                isGranted = hasAllFilesAccess()
            )
        )
    }

    fun createAppInfoSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun createUsageAccessSettingsIntent(): Intent {
        val packageIntent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                data = Uri.fromParts("package", context.packageName, null)
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return if (packageIntent.resolveActivity(context.packageManager) != null) {
            packageIntent
        } else {
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    fun createAllFilesAccessIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            createAppInfoSettingsIntent()
        }
    }

    fun createInstallUnknownAppsIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            createAppInfoSettingsIntent()
        }
    }

    fun createRestrictedSettingsHelpIntent(): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(RESTRICTED_SETTINGS_HELP_URL)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}
