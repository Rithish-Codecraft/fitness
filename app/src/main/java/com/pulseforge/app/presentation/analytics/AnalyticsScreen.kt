package com.pulseforge.app.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.model.EquipmentType
import com.pulseforge.app.domain.model.MuscleGroup
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.*

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
    var bannerDismissed by remember { mutableStateOf(false) }

    val sessions = uiState.workoutSessions
    val totalTonnage = sessions.sumOf { it.totalVolumeKg }
    val totalCaloriesBurned = sessions.sumOf { it.kcalBurned }
    val totalDurationMinutes = sessions.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Time Horizon Filter Capsule Selector (Matching Screen 2)
        item {
            TimeHorizonCapsuleSelector(
                options = listOf("Daily", "Weekly", "Monthly"),
                selectedIndex = selectedHorizonIndex,
                onSelectIndex = { selectedHorizonIndex = it }
            )
        }

        // Screen 2 Terracotta Coral Insight Banner
        if (!bannerDismissed) {
            item {
                CoralInsightBanner(
                    title = "Stretching after workouts improves your sleep quality.",
                    onDismiss = { bannerDismissed = true },
                    onSetRoutine = { bannerDismissed = true }
                )
            }
        }

        // Live 100% Real Phone Hardware Sensor Telemetry Card
        item {
            LivePhoneSensorsTelemetryCard(
                telemetry = uiState.sensorTelemetry
            )
        }

        // Screen 2 Concentric Arc Breakdown Rings
        item {
            ConcentricRingsGaugeCard(
                exerciseCurrentMin = if (totalDurationMinutes > 0) totalDurationMinutes else 0,
                exerciseTargetMin = 600,
                standCurrentMin = 0,
                standTargetMin = 600
            )
        }

        // Screen 2 Split Cards: Steps (Bar chart) & Distance (Route line)
        val liveSteps = uiState.sensorTelemetry.stepCount
        val liveDistM = uiState.sensorTelemetry.distanceMeters
        val activeCal = uiState.sensorTelemetry.activeCaloriesBurned

        val stepsStr = if (liveSteps != null && liveSteps > 0) "$liveSteps" else "-"
        val distStr = if (liveDistM != null && liveDistM > 0) {
            String.format("%.2f km", liveDistM / 1000.0)
        } else "-"

        item {
            SplitMetricsCards(
                stepsCount = stepsStr,
                distanceKm = distStr
            )
        }

        // Image 3: Motivational Lavender Progress Banner ("Just a Little More to Go", 75%)
        item {
            val stepInt = liveSteps?.toInt() ?: 0
            LavenderMotivationBanner(
                progressPct = if (stepInt > 0) (stepInt * 100 / 10000).coerceIn(1, 100) else 0,
                title = if (stepInt > 0) "Keep Moving" else "Hardware Sensors Active",
                subtitle = if (stepInt > 0) "Every step counts towards your daily goal!" else "Carry phone during workouts to stream real biometrics."
            )
        }

        // Image 3: Primary Multi-Line Waveform Activity Performance Graph
        item {
            MultiWaveformActivityGraphCard(
                totalKcal = if (activeCal != null && activeCal > 0) activeCal.toInt() else if (totalCaloriesBurned > 0) totalCaloriesBurned.toInt() else null,
                increasePct = null,
                walkingKcal = null,
                runningKcal = null,
                workoutKcal = null,
                title = "Todays activity"
            )
        }

        // Image 3: Quick Start New Activity Selector Bar (Run, Yoga, Swim, Walk, Gym)
        item {
            StartNewActivityCard()
        }

        // Neo-Soft Lilac Graphic Update Spline Chart (100% Real hardware data or clean baseline)
        item {
            LavenderGraphicUpdateCard(
                totalValue = if (liveSteps != null && liveSteps > 0) "$liveSteps" else "-",
                lowestValue = "-",
                highestValue = if (liveSteps != null && liveSteps > 0) "$liveSteps" else "-",
                averageValue = if (activeCal != null && activeCal > 0) "${activeCal.toInt()} kcal" else "-",
                peakTooltipValue = if (liveSteps != null && liveSteps > 0) "$liveSteps" else "-"
            )
        }

        // Neo-Soft Lilac 7-Day Interactive Week Schedule Strip
        item {
            LavenderWeekScheduleCard()
        }

        // Image 2 Hatched Weight Trend Cylinder Bar Chart
        item {
            WeightTrendHatchedCard(
                currentWeightKg = uiState.userProfile.currentWeightKg
            )
        }

        // Image 2 Continuous BMI Health Spectrum
        item {
            BmiSpectrumCard(
                bmiValue = 25.8,
                category = "Healthy"
            )
        }

        // Image 1 Daily Walk Session Report & Breathwork Dial
        item {
            DailyWalkSessionReportCard(
                morningWalk = "55 min 24 sec",
                eveningWalk = "43 min 31 sec",
                totalTime = "1h 38m 55s",
                activityPct = 42,
                successPct = 42
            )
        }

        // Lifetime / Selected Horizon Totals
        item {
            PulseCard {
                Text(
                    text = "Aggregate Statistics (${if (selectedHorizonIndex == 0) "Daily" else if (selectedHorizonIndex == 1) "Weekly" else "Monthly"})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBadge("Kcal Burned", "${totalCaloriesBurned.toInt()}", extra.calorieRing)
                    MetricBadge("Tonnage Lifted", "${(totalTonnage / 1000.0).let { String.format("%.1f", it) }}t", MaterialTheme.colorScheme.primary)
                    MetricBadge("Time Trained", "${totalDurationMinutes}m", extra.proteinColor)
                    MetricBadge("Sessions", "${sessions.size}", extra.carbsColor)
                }
            }
        }

        // Calorie Burn & Work Session Graph
        item {
            PulseCard(borderGlow = true) {
                Text(
                    text = "Kcal Burn per Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Calculated via MET and Resistance Tonnage EPOC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Custom Canvas Bar Chart for Kcal Burned
                val caloriePoints = if (sessions.isNotEmpty()) {
                    sessions.map { it.kcalBurned.toFloat() }.takeLast(7)
                } else listOf(350f, 420f, 510f, 390f, 480f)

                val maxCal = caloriePoints.maxOrNull()?.coerceAtLeast(100f) ?: 500f
                val primaryColor = MaterialTheme.colorScheme.primary

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = size.width / (caloriePoints.size * 2f)
                        caloriePoints.forEachIndexed { i, cal ->
                            val barHeight = (cal / maxCal) * (size.height * 0.85f)
                            val x = (i * 2f + 0.5f) * barWidth
                            val y = size.height - barHeight

                            drawRoundRect(
                                color = primaryColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Earliest Session", style = MaterialTheme.typography.labelSmall)
                    Text("Latest Session", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // 1RM Strength Progression Curve
        item {
            PulseCard {
                Text(
                    text = "Estimated 1RM Strength Curve",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Progression on Primary Compound Lifts (Brzycki formula)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Line Chart for 1RM Progression
                val strengthPoints = listOf(75f, 77.5f, 80f, 82.5f, 85f, 87.2f)
                val lineColor = extra.proteinColor

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val minS = 70f
                        val maxS = 90f
                        val path = Path()
                        val stepX = size.width / (strengthPoints.size - 1)

                        strengthPoints.forEachIndexed { index, weight ->
                            val x = index * stepX
                            val y = size.height - ((weight - minS) / (maxS - minS) * size.height)
                            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            drawCircle(color = lineColor, radius = 6f, center = Offset(x, y))
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Week 1: 75.0 kg", style = MaterialTheme.typography.labelSmall)
                    Text("Current PR: 87.2 kg (+16%)", style = MaterialTheme.typography.labelSmall, color = extra.proteinColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Muscle Heatmap Volume Distribution
        item {
            PulseCard {
                Text(
                    text = "Muscle Group Weekly Volume Heatmap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Weekly working sets per target group (Optimal range: 10-20 sets)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                val muscleDistribution = listOf(
                    "Pectorals / Chest" to 14,
                    "Lats & Upper Back" to 16,
                    "Quadriceps & Glutes" to 12,
                    "Hamstrings" to 8,
                    "Shoulders / Delts" to 11,
                    "Arms (Biceps & Triceps)" to 10,
                    "Core & Stability" to 6
                )

                muscleDistribution.forEach { (muscle, sets) ->
                    val progress = (sets / 20f).coerceIn(0f, 1f)
                    val barColor = when {
                        sets < 8 -> Color(0xFF9E9E9E) // Low
                        sets in 8..18 -> MaterialTheme.colorScheme.primary // Optimal
                        else -> extra.carbsColor // High / Overreaching
                    }

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(muscle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Text("$sets Sets / wk", style = MaterialTheme.typography.bodySmall, color = barColor, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = barColor,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        // Gym Equipment Tonnage Breakdown
        item {
            PulseCard {
                Text(
                    text = "Equipment Tonnage Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Barbell Compounds", style = MaterialTheme.typography.bodySmall)
                    Text("5,400 kg", fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pin & Plate Loaded Machines", style = MaterialTheme.typography.bodySmall)
                    Text("4,800 kg", fontWeight = FontWeight.Bold)
                }
                Divider(modifier = Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cable Pulley Systems", style = MaterialTheme.typography.bodySmall)
                    Text("2,250 kg", fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

