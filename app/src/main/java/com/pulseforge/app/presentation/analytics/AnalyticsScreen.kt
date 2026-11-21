package com.pulseforge.app.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.model.EquipmentType
import com.pulseforge.app.domain.model.MuscleGroup
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.*
import kotlin.math.max

enum class TimeHorizon(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

@Composable
fun AnalyticsScreen(
    uiState: MainUiState
) {
    val extra = LocalExtraColors.current
    var selectedHorizonIndex by remember { mutableStateOf(1) } // Weekly default

    val sessions = uiState.workoutSessions
    val totalTonnage = sessions.sumOf { it.totalVolumeKg }
    val totalCaloriesBurned = sessions.sumOf { it.kcalBurned }
    val totalDurationMinutes = sessions.sumOf { it.durationMinutes }

    val liveSteps = uiState.sensorTelemetry.stepCount
    val liveDistM = uiState.sensorTelemetry.distanceMeters
    val activeCal = uiState.sensorTelemetry.activeCaloriesBurned

    val stepsStr = if (liveSteps != null && liveSteps > 0) "$liveSteps" else "-"
    val distStr = if (liveDistM != null && liveDistM > 0) {
        String.format("%.2f km", liveDistM / 1000.0)
    } else "-"

    val heightM = (uiState.userProfile.heightCm / 100.0).coerceAtLeast(1.0)
    val bmi = uiState.userProfile.currentWeightKg / (heightM * heightM)
    val bmiRounded = (bmi * 10).toInt() / 10.0
    val bmiCategory = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Healthy"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TimeHorizonCapsuleSelector(
                options = listOf("Daily", "Weekly", "Monthly"),
                selectedIndex = selectedHorizonIndex,
                onSelectIndex = { selectedHorizonIndex = it }
            )
        }

        // Live Real Phone Hardware Sensor Telemetry Card
        item {
            LivePhoneSensorsTelemetryCard(
                telemetry = uiState.sensorTelemetry,
                healthConnectStatus = uiState.healthConnectStatus
            )
        }

        // Real Steps & Distance Split Cards
        item {
            SplitMetricsCards(
                stepsCount = stepsStr,
                distanceKm = distStr
            )
        }

        // Real Aggregate Training Statistics
        item {
            PulseCard {
                Text(
                    text = "Training Summary (${TimeHorizon.entries[selectedHorizonIndex].label})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        MetricBadge("Kcal Burned", "${totalCaloriesBurned.toInt()}", extra.calorieRing)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        MetricBadge("Tonnage", "${String.format("%.1f", totalTonnage / 1000.0)}t", MaterialTheme.colorScheme.primary)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        MetricBadge("Trained", "${totalDurationMinutes}m", extra.proteinColor)
                    }
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        MetricBadge("Sessions", "${sessions.size}", extra.carbsColor)
                    }
                }
            }
        }

        // Real BMI Spectrum from User Profile
        item {
            BmiSpectrumCard(
                bmiValue = bmiRounded,
                category = bmiCategory
            )
        }

        // Real Kcal Burn per Session Bar Chart
        item {
            PulseCard(borderGlow = true) {
                Text(
                    text = "Kcal Burn per Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Calculated from logged workouts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                val caloriePoints = sessions.map { it.kcalBurned.toFloat() }.takeLast(7)
                if (caloriePoints.isEmpty()) {
                    Text(
                        text = "No workouts logged yet. Log a workout to view session calorie burn.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val maxCal = max(100f, caloriePoints.maxOrNull() ?: 100f)
                    val primaryColor = MaterialTheme.colorScheme.primary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (size.width > 0f && size.height > 0f) {
                                val count = caloriePoints.size.coerceAtLeast(1)
                                val barWidth = (size.width / (count * 2f)).coerceAtLeast(8f)
                                caloriePoints.forEachIndexed { i, cal ->
                                    val barHeight = ((cal / maxCal) * (size.height * 0.85f)).coerceIn(4f, size.height)
                                    val x = (i * 2f + 0.5f) * barWidth
                                    val y = (size.height - barHeight).coerceAtLeast(0f)

                                    drawRoundRect(
                                        color = primaryColor,
                                        topLeft = Offset(x, y),
                                        size = Size(barWidth, barHeight),
                                        cornerRadius = CornerRadius(8f, 8f)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Earliest", style = MaterialTheme.typography.labelSmall)
                        Text("Latest (${caloriePoints.last().toInt()} kcal)", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Real Muscle Group Volume Distribution
        item {
            val setsByMuscle = sessions
                .flatMap { it.exercises }
                .groupBy { it.muscleGroup }
                .mapValues { (_, logs) -> logs.sumOf { it.sets.size } }

            PulseCard {
                Text(
                    text = "Muscle Group Volume",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Logged working sets per target group (Optimal: 10–20 sets)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                MuscleGroup.entries.forEach { muscle ->
                    val sets = setsByMuscle[muscle] ?: 0
                    val progress = (sets / 20f).coerceIn(0f, 1f)
                    val barColor = when {
                        sets == 0 -> MaterialTheme.colorScheme.outline
                        sets < 8 -> Color(0xFF9E9E9E)
                        sets in 8..18 -> MaterialTheme.colorScheme.primary
                        else -> extra.carbsColor
                    }

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(muscle.label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text("$sets Sets", style = MaterialTheme.typography.bodySmall, color = barColor, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = barColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Real Equipment Tonnage Breakdown
        item {
            val tonnageByEquipment = sessions
                .flatMap { it.exercises }
                .groupBy { it.equipmentType }
                .mapValues { (_, logs) ->
                    logs.sumOf { log -> log.sets.sumOf { it.weightKg * it.reps } }
                }

            PulseCard {
                Text(
                    text = "Equipment Tonnage Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                EquipmentType.entries.forEachIndexed { index, eq ->
                    val kg = tonnageByEquipment[eq] ?: 0.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(eq.label, style = MaterialTheme.typography.bodySmall)
                        Text("${String.format("%,.0f", kg)} kg", fontWeight = FontWeight.Bold)
                    }
                    if (index < EquipmentType.entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Multi-Day Recordings Log (stored in Documents/pulseforce/daily_readings/)
        item {
            val recordings = uiState.multiDayRecordings
            PulseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Multi-Day Recordings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${recordings.size} days",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (recordings.isEmpty()) {
                    Text(
                        text = "Daily snapshots are saved automatically to Documents/pulseforce/daily_readings/.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    recordings.take(14).forEachIndexed { idx, day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = day.date,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${day.steps} steps • ${day.consumedCalories.toInt()} kcal • ${day.waterMl} ml",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (idx < recordings.take(14).lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
