package com.pulseforge.app.presentation.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.MetricBadge
import com.pulseforge.app.presentation.components.PulseCard
import com.pulseforge.app.presentation.components.SectionHeader
import kotlin.math.roundToInt

@Composable
fun CoachReviewScreen(
    uiState: MainUiState,
    onGenerateNewReview: (Int) -> Unit
) {
    val extra = LocalExtraColors.current
    var selectedFatigue by remember { mutableStateOf(2f) }
    var showReviewDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Algorithmic AI Coach & Review",
                subtitle = "Weekly structured progress sheets with physiological rebalancing"
            ) {
                Button(onClick = { showReviewDialog = true }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Check-in")
                }
            }
        }

        // Current Trajectory Header
        item {
            PulseCard(borderGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Primary Plan Horizon",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.userProfile.primaryGoal.label} • Week 1 of ${uiState.userProfile.targetTimelineWeeks}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    MetricBadge(
                        label = "Weight",
                        value = "${uiState.userProfile.currentWeightKg} kg",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Structured Progress Sheets List
        items(uiState.progressReviews) { review ->
            PulseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Structured Check-in: Week ${review.weekNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${review.startDate} to ${review.endDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                        Text(
                            text = review.adherenceRating,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Adherence & Volume Metric Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Calorie Compliance", style = MaterialTheme.typography.labelSmall)
                        Text("${review.calorieAdherencePct.toInt()}%", fontWeight = FontWeight.Bold, color = extra.calorieRing)
                    }
                    Column {
                        Text("Protein Hit Rate", style = MaterialTheme.typography.labelSmall)
                        Text("${review.proteinAdherencePct.toInt()}%", fontWeight = FontWeight.Bold, color = extra.proteinColor)
                    }
                    Column {
                        Text("Workouts", style = MaterialTheme.typography.labelSmall)
                        Text("${review.workoutsCompleted}/${review.workoutsTarget}", fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Tonnage", style = MaterialTheme.typography.labelSmall)
                        Text("${(review.totalVolumeTonnage / 1000.0).let { String.format("%.1f", it) }}t", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AI Feedback Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Algorithmic Analysis",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.aiFeedbackSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prescription: ${review.aiRecommendation}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (review.aiCalorieAdjustment != 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val sign = if (review.aiCalorieAdjustment > 0) "+" else ""
                            Text(
                                text = "Target Recalibration: $sign${review.aiCalorieAdjustment} kcal/day",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = extra.proteinColor
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // New Weekly Check-in Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Generate Weekly AI Check-in") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "PulseForge will analyze your 7-day caloric adherence, progressive overload tonnage, and bodyweight kinetics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Subjective Fatigue & Joint Soreness: ${selectedFatigue.toInt()} / 5",
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = selectedFatigue,
                        onValueChange = { selectedFatigue = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text(
                        text = when (selectedFatigue.roundToInt()) {
                            1 -> "1: Fully fresh, peak recovery"
                            2 -> "2: Normal muscular fatigue, ready to train"
                            3 -> "3: Moderate soreness across trained muscle groups"
                            4 -> "4: High fatigue, heavy joints, sluggish CNS"
                            else -> "5: Severe exhaustion (Triggers deload/diet break)"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onGenerateNewReview(selectedFatigue.roundToInt())
                        showReviewDialog = false
                    }
                ) {
                    Text("Run AI Analysis")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

