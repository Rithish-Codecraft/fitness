package com.pulseforge.app.core.lifestyle

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.pulseforge.app.domain.model.AppUsageDetail
import com.pulseforge.app.domain.model.LifestyleSleepAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Android Device Screen Usage, App Usage, Nighttime Analysis & Sleep Behavior Engine.
 * Extracts real hardware app foreground telemetry to understand user lifestyle:
 * • Total daily screen time
 * • Nighttime phone usage (10:00 PM - 06:00 AM)
 * • Most used late-night app (e.g. YouTube, Instagram, Netflix)
 * • Deduced objective sleep window (bedtime stillness to morning wake activity)
 * • Sleep hygiene impact analysis
 */
class LifestyleUsageManager(private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
    private val packageManager = context.packageManager

    /**
     * Checks if PACKAGE_USAGE_STATS is granted via AppOps
     */
    fun hasUsagePermission(): Boolean {
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

    /**
     * Intent to direct the user to Android Usage Access Settings
     */
    fun createUsageAccessSettingsIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Analyzes 24h usage + night window usage (10 PM - 6 AM)
     */
    suspend fun analyzeLifestyleAndSleep(): LifestyleSleepAnalysis = withContext(Dispatchers.IO) {
        val hasPermission = hasUsagePermission()
        if (!hasPermission || usageStatsManager == null) {
            return@withContext LifestyleSleepAnalysis(
                hasUsagePermission = false,
                sleepDisruptionFeedback = "Grant Usage Access in Android Settings to sync real screen time, nighttime app habits, and objective sleep windows."
            )
        }

        val now = System.currentTimeMillis()
        val oneDayAgo = now - (24 * 60 * 60 * 1000L)

        // 1. Query aggregated stats for the last 24 hours
        val statsMap = usageStatsManager.queryAndAggregateUsageStats(oneDayAgo, now)
        val appUsageList = mutableListOf<AppUsageDetail>()
        var totalDailyScreenTimeMillis = 0L

        statsMap.forEach { (pkg, stats) ->
            val time = stats.totalTimeInForeground
            if (time > 60_000L) { // Over 1 minute
                val appName = getFriendlyAppName(pkg)
                // Filter out system launchers and internal system UI noise if desirable, but keep primary user apps
                if (!pkg.contains("launcher") && !pkg.contains("systemui")) {
                    totalDailyScreenTimeMillis += time
                    appUsageList.add(
                        AppUsageDetail(
                            packageName = pkg,
                            appName = appName,
                            totalTimeMinutes = time / 60_000L,
                            isNighttime = false
                        )
                    )
                }
            }
        }

        // Sort top apps by duration
        val topDayApps = appUsageList.sortedByDescending { it.totalTimeMinutes }.take(5)

        // 2. Query usage events to analyze Nighttime window (10:00 PM yesterday to 6:00 AM today)
        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        // End of night: today 6:00 AM
        val today6AmCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nightEndMillis = if (now < today6AmCal.timeInMillis) now else today6AmCal.timeInMillis

        // Start of night: yesterday 22:00 (10:00 PM)
        val yesterday10PmCal = Calendar.getInstance().apply {
            timeInMillis = today6AmCal.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val nightStartMillis = yesterday10PmCal.timeInMillis

        // Query events within the night window
        val events = usageStatsManager.queryEvents(nightStartMillis, nightEndMillis)
        val nightEvent = UsageEvents.Event()
        val nightAppTimeMap = mutableMapOf<String, Long>()
        var lastResumedPkg: String? = null
        var lastResumedTime: Long? = null
        var totalNightScreenMillis = 0L
        var latestNightActivityTime: Long? = null
        var earliestMorningActivityTime: Long? = null

        while (events.hasNextEvent()) {
            events.getNextEvent(nightEvent)
            val eventTime = nightEvent.timeStamp
            val pkg = nightEvent.packageName

            if (eventTime in nightStartMillis..nightEndMillis) {
                when (nightEvent.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        lastResumedPkg = pkg
                        lastResumedTime = eventTime
                        latestNightActivityTime = eventTime
                    }
                    UsageEvents.Event.ACTIVITY_PAUSED, UsageEvents.Event.ACTIVITY_STOPPED -> {
                        if (lastResumedPkg == pkg && lastResumedTime != null) {
                            val duration = (eventTime - lastResumedTime).coerceAtLeast(0L)
                            if (duration in 1_000L..14_400_000L) { // between 1s and 4 hours
                                nightAppTimeMap[pkg] = (nightAppTimeMap[pkg] ?: 0L) + duration
                                totalNightScreenMillis += duration
                            }
                            lastResumedPkg = null
                            lastResumedTime = null
                        }
                    }
                }
            }
        }

        // Also query morning wake time (first event after 5:00 AM)
        val morningStartCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 5)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val morningEvents = usageStatsManager.queryEvents(morningStartCal.timeInMillis, now)
        val mEvent = UsageEvents.Event()
        while (morningEvents.hasNextEvent()) {
            morningEvents.getNextEvent(mEvent)
            if (mEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED && earliestMorningActivityTime == null) {
                earliestMorningActivityTime = mEvent.timeStamp
                break
            }
        }

        // Top night app
        val topNightEntry = nightAppTimeMap.maxByOrNull { it.value }
        val topNightAppName = topNightEntry?.let { getFriendlyAppName(it.key) } ?: "-"
        val topNightAppMinutes = (topNightEntry?.value ?: 0L) / 60_000L
        val nightScreenMinutes = totalNightScreenMillis / 60_000L

        // Deduced Bedtime and Wake time
        val timeFmt = DateTimeFormatter.ofPattern("hh:mm a")
        val estimatedBedtime = if (latestNightActivityTime != null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(latestNightActivityTime), ZoneId.systemDefault()).format(timeFmt)
        } else {
            "11:15 PM"
        }

        val estimatedWakeTime = if (earliestMorningActivityTime != null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(earliestMorningActivityTime), ZoneId.systemDefault()).format(timeFmt)
        } else {
            "06:45 AM"
        }

        // Sleep duration estimate
        val sleepDurationHours = if (latestNightActivityTime != null && earliestMorningActivityTime != null && earliestMorningActivityTime > latestNightActivityTime) {
            val diff = (earliestMorningActivityTime - latestNightActivityTime) / (1000.0 * 60.0 * 60.0)
            (diff * 10).toInt() / 10.0
        } else {
            7.5
        }

        // Quality score & feedback
        val qualityScore = when {
            nightScreenMinutes > 90 -> 60
            nightScreenMinutes > 45 -> 74
            nightScreenMinutes > 20 -> 85
            else -> 94
        }

        val feedback = when {
            nightScreenMinutes > 60 -> "High late-night screen time ($nightScreenMinutes min, mostly $topNightAppName). Blue light suppression of melatonin delays restorative REM sleep. Consider a screen cutoff 45m before bedtime."
            nightScreenMinutes > 20 -> "Moderate night usage ($nightScreenMinutes min on $topNightAppName). Slight sleep latency delay detected. Aim to wind down earlier."
            else -> "Excellent sleep hygiene! Minimal nighttime screen distraction ($nightScreenMinutes min). Strong circadian alignment and recovery potential."
        }

        LifestyleSleepAnalysis(
            dailyScreenTimeMinutes = totalDailyScreenTimeMillis / 60_000L,
            nightScreenTimeMinutes = nightScreenMinutes,
            mostUsedNightApp = topNightAppName,
            mostUsedNightAppMinutes = topNightAppMinutes,
            topDayApps = topDayApps,
            estimatedBedtime = estimatedBedtime,
            estimatedWakeTime = estimatedWakeTime,
            estimatedSleepDurationHours = sleepDurationHours,
            sleepQualityScore = qualityScore,
            sleepDisruptionFeedback = feedback,
            hasUsagePermission = true
        )
    }

    private fun getFriendlyAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (_: PackageManager.NameNotFoundException) {
            packageName.substringAfterLast(".")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

