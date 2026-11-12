package com.pulseforge.app.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.calculator.DashboardWidgetId
import com.pulseforge.app.domain.model.MealType
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.MacroProgressBar
import com.pulseforge.app.presentation.components.MetricBadge
import com.pulseforge.app.presentation.components.PulseCard
import com.pulseforge.app.presentation.components.SectionHeader

@Composable
fun DashboardScreen(
    uiState: MainUiState,
    onNavigateToNutrition: () -> Unit,
    onNavigateToWorkout: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToCoach: () -> Unit,
    onNavigateToThemes: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onAddWater: (Int) -> Unit,
    onCheckForUpdates: () -> Unit = {}
) {
    val extra = LocalExtraColors.current
    val profile = uiState.userProfile
    val nutrition = uiState.dailyNutrition

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Bar Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PulseForge AI",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${profile.primaryGoal.label} • ${profile.locality.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCheckForUpdates) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Check Web Updates",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToThemes) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Themes",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToOnboarding) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Edit Profile & Goals",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Render Dynamic Widgets according to prioritized dispatcher
        items(uiState.dashboardWidgets) { widgetId ->
            when (widgetId) {
                DashboardWidgetId.NUTRITION_RINGS -> {
                    PulseCard(borderGlow = true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Daily Calorie & Macro Target",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Target: ${nutrition.targetCalories} kcal • ${nutrition.targetProteinG}g Protein",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(extra.calorieRing.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${nutrition.consumedCalories.toInt()} KCAL IN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = extra.calorieRing
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            com.pulseforge.app.presentation.components.WhoopCircularTelemetryDial(
                                current = nutrition.consumedCalories,
                                target = nutrition.targetCalories.toDouble(),
                                unit = "kcal",
                                label = "LEFT",
                                size = 115.dp,
                                strokeWidth = 9.dp,
                                activeColor = extra.calorieRing
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                MacroProgressBar(
                                    label = "Protein",
                                    current = nutrition.consumedProteinG,
                                    target = nutrition.targetProteinG.toDouble(),
                                    unit = "g",
                                    color = extra.proteinColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                MacroProgressBar(
                                    label = "Carbohydrates",
                                    current = nutrition.consumedCarbsG,
                                    target = nutrition.targetCarbsG.toDouble(),
                                    unit = "g",
                                    color = extra.carbsColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                MacroProgressBar(
                                    label = "Healthy Fats",
                                    current = nutrition.consumedFatG,
                                    target = nutrition.targetFatG.toDouble(),
                                    unit = "g",
                                    color = extra.fatColor
                                )
                            }
                        }
                    }
                }

                DashboardWidgetId.FOOD_CAMERA_SCANNER -> {
                    PulseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "AI Food & Label Scanner",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Snap meal plate or scan nutrition label",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Button(
                                onClick = onNavigateToNutrition,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Scan")
                            }
                        }
                    }
                }

                DashboardWidgetId.ACTIVE_WORKOUT_CARD -> {
                    PulseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Target Hypertrophy Session",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Focus: ${profile.targetMuscleGroups.joinToString { it.label }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Button(
                                onClick = onNavigateToWorkout,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Log Sets")
                            }
                        }
                    }
                }

                DashboardWidgetId.RECOMMENDED_SHAKE -> {
                    val shake = uiState.recommendedShakes.firstOrNull()
                    if (shake != null) {
                        PulseCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI High-Protein Shake Suggestion",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${shake.proteinG}g Protein",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = extra.proteinColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = shake.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = shake.purpose,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ingredients: ${shake.ingredients.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                DashboardWidgetId.MUSCLE_VOLUME_HEATMAP -> {
                    PulseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Muscle Volume Distribution",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = onNavigateToAnalytics) {
                                Text("Full Chart")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetricBadge("Chest", "12 Sets", MaterialTheme.colorScheme.primary)
                            MetricBadge("Back", "14 Sets", extra.proteinColor)
                            MetricBadge("Quads", "10 Sets", extra.carbsColor)
                            MetricBadge("Core", "6 Sets", MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                DashboardWidgetId.STRENGTH_PROGRESSION_GRAPH -> {
                    PulseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "1RM Strength Progression",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Incline Bench: 87.2 kg est. 1RM (+4.5% this month)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = onNavigateToAnalytics) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "View")
                            }
                        }
                    }
                }

                DashboardWidgetId.HYDRATION_QUICK_ADD -> {
                    PulseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Hydration Tracker",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${profile.waterConsumedMl} / ${profile.waterTargetMl} ml",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { onAddWater(250) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+250 ml")
                            }
                        }
                    }
                }

                DashboardWidgetId.AI_COACH_WEEKLY_INSIGHT -> {
                    val review = uiState.progressReviews.firstOrNull()
                    if (review != null) {
                        PulseCard {
                            Text(
                                text = "Weekly AI Coach Recommendation",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = review.aiRecommendation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(
                                onClick = onNavigateToCoach,
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Open Review Sheet")
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

