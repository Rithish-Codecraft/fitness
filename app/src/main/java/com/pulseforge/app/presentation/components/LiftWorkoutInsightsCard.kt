package com.pulseforge.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.DailyRecordingSnapshot
import com.pulseforge.app.domain.model.UserProfile
import com.pulseforge.app.domain.model.WorkoutSession
import java.time.LocalDate
import kotlin.math.abs

/**
 * Clean, high-contrast Workout & 30-Day Progress Insights Suite (100% Real Data).
 * Includes: Today's Workout Hero, 3-Stat Pills (with detail dialog), 30-Day Heatmap,
 * Volume Trend & Weekly Reps Charts, and Body Weight Goal Bar.
 */
@Composable
fun LiftWorkoutInsightsSection(
    sessions: List<WorkoutSession>,
    userProfile: UserProfile,
    multiDayRecordings: List<DailyRecordingSnapshot>,
    onStartWorkoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayStr = remember { LocalDate.now().toString() }
    val todaySessions = remember(sessions, todayStr) { sessions.filter { it.date == todayStr } }
    val latestToday = todaySessions.firstOrNull()
    val latestAny = sessions.firstOrNull()

    val totalWorkouts = sessions.size
    val totalSets = remember(sessions) { sessions.sumOf { it.totalSetsCount } }
    val totalReps = remember(sessions) {
        sessions.sumOf { s -> s.exercises.sumOf { ex -> ex.sets.sumOf { it.reps } } }
    }
    val totalVolumeKg = remember(sessions) { sessions.sumOf { it.totalVolumeKg } }
    val maxSingleSetKg = remember(sessions) {
        sessions.flatMap { it.exercises }.flatMap { it.sets }.maxOfOrNull { it.weightKg } ?: 0.0
    }

    var detailMetric by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Today's Workout Hero Card
        PulseCard(borderGlow = todaySessions.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (todaySessions.isNotEmpty()) {
                        Color(0xFF10B981).copy(alpha = 0.16f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (todaySessions.isNotEmpty()) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (todaySessions.isNotEmpty()) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (todaySessions.isNotEmpty()) "Completed" else "Ready",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (todaySessions.isNotEmpty()) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val heroTitle = latestToday?.title ?: latestAny?.title ?: "Start Training"
            val heroMuscles = (latestToday ?: latestAny)?.exercises
                ?.map { it.muscleGroup.label.substringBefore(" ") }
                ?.distinct()
                ?.take(3)
                ?.joinToString(", ")
                ?.ifBlank { "Strength & Conditioning" }
                ?: "Log your first session"

            Text(
                text = heroTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = heroMuscles,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            val heroSets = (latestToday ?: latestAny)?.totalSetsCount ?: 0
            val heroVol = (latestToday ?: latestAny)?.totalVolumeKg ?: 0.0
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "$heroSets sets",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = formatCompactWeight(heroVol),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartWorkoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(999.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (todaySessions.isNotEmpty()) "Start another workout" else "Start workout",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // 2. 3-Column Stat Pills (Tap for Detail Breakdown)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InsightStatPill(
                icon = Icons.Default.FitnessCenter,
                value = "$totalWorkouts",
                label = "workouts",
                modifier = Modifier.weight(1f),
                onClick = { detailMetric = "WORKOUTS" }
            )
            InsightStatPill(
                icon = Icons.Default.Layers,
                value = "$totalSets",
                label = "sets ($totalReps reps)",
                modifier = Modifier.weight(1f),
                onClick = { detailMetric = "REPS" }
            )
            InsightStatPill(
                icon = Icons.Default.Scale,
                value = formatCompactNumber(totalVolumeKg),
                label = "kg volume",
                modifier = Modifier.weight(1f),
                onClick = { detailMetric = "VOLUME" }
            )
        }

        // 3. Last 30 Days Consistency Heatmap (3 x 10 grid)
        val activeDates = remember(sessions, multiDayRecordings) {
            val workoutDates = sessions.map { it.date }.toSet()
            val recordingDates = multiDayRecordings.filter { it.workoutCount > 0 || it.steps > 1000 }.map { it.date }.toSet()
            workoutDates + recordingDates
        }
        val last30Dates = remember {
            val today = LocalDate.now()
            (29 downTo 0).map { offset -> today.minusDays(offset.toLong()).toString() }
        }
        val activeDaysCount = last30Dates.count { it in activeDates }

        PulseCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last 30 days",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$activeDaysCount / 30 active",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                last30Dates.chunked(10).forEach { rowDates ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowDates.forEach { dateStr ->
                            val isActive = dateStr in activeDates
                            val isToday = dateStr == todayStr
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when {
                                            isToday -> MaterialTheme.colorScheme.primary
                                            isActive -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        // 4. 2-Column Mini Charts: Volume Trend + This Week Daily Reps
        val volumePoints = remember(sessions) {
            sessions.take(8).reversed().map { it.totalVolumeKg.toFloat() }
        }
        val weeklyReps = remember(sessions) {
            val today = LocalDate.now()
            (6 downTo 0).map { offset ->
                val d = today.minusDays(offset.toLong())
                val dStr = d.toString()
                val repsForDay = sessions.filter { it.date == dStr }
                    .sumOf { s -> s.exercises.sumOf { ex -> ex.sets.sumOf { it.reps } } }
                d.dayOfWeek.name.take(1) to repsForDay
            }
        }
        val weekTotalReps = weeklyReps.sumOf { it.second }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left: Volume Trend
            PulseCard(modifier = Modifier.weight(1f)) {
                Text("Volume", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "${formatCompactNumber(totalVolumeKg)} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                val primaryColor = MaterialTheme.colorScheme.primary
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (size.width > 0f && size.height > 0f && volumePoints.isNotEmpty()) {
                        val maxV = (volumePoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
                        val pts = if (volumePoints.size == 1) listOf(volumePoints[0], volumePoints[0]) else volumePoints
                        val stepX = size.width / (pts.size - 1).coerceAtLeast(1)
                        val path = Path()
                        pts.forEachIndexed { idx, v ->
                            val x = idx * stepX
                            val y = size.height - ((v / maxV) * (size.height * 0.8f)) - 4f
                            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 5f, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // Right: This Week Reps
            PulseCard(modifier = Modifier.weight(1f)) {
                Text("This week", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "$weekTotalReps reps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                val maxR = (weeklyReps.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                val barColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                val todayBarColor = MaterialTheme.colorScheme.primary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyReps.forEachIndexed { idx, (dayLetter, repCount) ->
                        val frac = if (repCount > 0) (repCount.toFloat() / maxR).coerceIn(0.15f, 1f) else 0.08f
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((36 * frac).dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (idx == weeklyReps.lastIndex) todayBarColor else barColor)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(dayLetter, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // 5. Body Weight Goal Card
        val diffKg = abs(userProfile.targetWeightKg - userProfile.currentWeightKg)
        val diffText = if (diffKg < 0.2) "Goal reached" else "${String.format("%.1f", diffKg)} kg to go"
        val startGap = 15.0
        val weightProgress = (1.0 - (diffKg / startGap)).toFloat().coerceIn(0.08f, 1f)

        PulseCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Body weight", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${userProfile.currentWeightKg} kg",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = "Goal ${userProfile.targetWeightKg} kg • $diffText",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { weightProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                strokeCap = StrokeCap.Round
            )
        }
    }

    // Interactive Stat Detail Modal (matching video's tap-to-inspect sheet)
    if (detailMetric != null) {
        val (title, headline, subline) = when (detailMetric) {
            "WORKOUTS" -> Triple(
                "Total Workouts",
                "$totalWorkouts sessions",
                "Total training time: ${sessions.sumOf { it.durationMinutes }} min across all logged workouts."
            )
            "REPS" -> Triple(
                "Reps & Sets Logged",
                "$totalReps reps",
                "$totalSets sets completed — avg ${if (totalSets > 0) String.format("%.1f", totalReps.toDouble() / totalSets) else "0"} reps per set."
            )
            else -> Triple(
                "Total Volume",
                "${formatCompactNumber(totalVolumeKg)} kg",
                "Heaviest single set logged: ${String.format("%.1f", maxSingleSetKg)} kg."
            )
        }
        AlertDialog(
            onDismissRequest = { detailMetric = null },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(headline, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text(subline, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton(onClick = { detailMetric = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun InsightStatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatCompactNumber(value: Double): String {
    return when {
        value >= 1000.0 -> String.format("%.1fk", value / 1000.0)
        value > 0.0 -> String.format("%.0f", value)
        else -> "0"
    }
}

private fun formatCompactWeight(value: Double): String {
    return "${formatCompactNumber(value)} kg"
}
