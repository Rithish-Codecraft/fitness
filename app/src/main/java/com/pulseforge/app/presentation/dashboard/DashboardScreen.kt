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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.calculator.DashboardWidgetId
import com.pulseforge.app.domain.model.MealType
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.*

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

    val calProgress = if (nutrition.targetCalories > 0) {
        (nutrition.consumedCalories / nutrition.targetCalories).toFloat()
    } else 0f
    val percentInt = (calProgress * 100).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Bar Header & Actions
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (profile.activeTheme == com.pulseforge.app.domain.model.ThemePalette.LONGEVITY) "Your Fitness" 
                               else if (profile.activeTheme == com.pulseforge.app.domain.model.ThemePalette.WELLNESS) "Good Morning"
                               else "PulseForge AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (profile.activeTheme == com.pulseforge.app.domain.model.ThemePalette.LONGEVITY) "Tina Lumia • Biometrics"
                               else if (profile.activeTheme == com.pulseforge.app.domain.model.ThemePalette.WELLNESS) "Claire Rosinkranz • Diet Plan"
                               else "${profile.primaryGoal.label} • ${profile.locality.label}",
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

        // Layout variant based on active theme
        when (profile.activeTheme) {
            com.pulseforge.app.domain.model.ThemePalette.LONGEVITY -> {
                // IMAGE 1: LONGEVITY CYBER / STEALTH LAYOUT
                item {
                    LongevityProgressHeroCard(
                        progressPct = 60,
                        timeframe = "Last week",
                        onStartNow = onNavigateToWorkout
                    )
                }
                item {
                    BiometricTriMetricsCard(
                        heartRateBpm = 82,
                        caloriesBurned = 25,
                        caloriesTarget = 250,
                        stepsCount = 450,
                        stepsTarget = 5500
                    )
                }
                item {
                    BloodPressureCandleCard(
                        systolic = 120,
                        diastolic = 80
                    )
                }
                item {
                    EcgHeartRateCard(
                        bpm = 80.5
                    )
                }
                item {
                    DailyWalkSessionReportCard(
                        morningWalk = "55 min 24 sec",
                        eveningWalk = "43 min 31 sec",
                        totalTime = "1h 38m 55s",
                        activityPct = 42,
                        successPct = 42
                    )
                }
            }
            com.pulseforge.app.domain.model.ThemePalette.WELLNESS -> {
                // IMAGE 2: CLEAN WELLNESS & MACRO TACHOMETER LAYOUT
                item {
                    RadiantMacroTachometerCard(
                        currentKcal = 1190,
                        targetKcal = 3000,
                        proteinG = 80,
                        proteinTargetG = 150,
                        fatG = 290,
                        fatTargetG = 350,
                        carbsG = 221,
                        carbsTargetG = 250
                    )
                }
                item {
                    HydrationAndHeartSplitCards(
                        cupsCurrent = 7,
                        cupsTarget = 10,
                        heartBpm = 68,
                        heartTargetBpm = 125
                    )
                }
                item {
                    TodaysPlanSection(
                        onSeeAllClick = onNavigateToNutrition
                    )
                }
                item {
                    WeightTrendHatchedCard(
                        currentWeightKg = 94.13
                    )
                }
                item {
                    BmiSpectrumCard(
                        bmiValue = 25.8,
                        category = "Healthy"
                    )
                }
                item {
                    PremiumUpgradeCard(
                        onUpgradeClick = onNavigateToThemes
                    )
                }
            }
            else -> {
                // SIGNATURE SOFT-CLAY TERRACOTTA CORAL LAYOUT
                item {
                    Text(
                        text = "Let's start\nstrong!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        lineHeight = 34.sp
                    )
                }

                item {
                    DailyGoalHeroCard(
                        percentText = "You're ${percentInt.coerceAtLeast(45)}% to your daily goal",
                        currentVal = "${nutrition.consumedCalories.toInt().coerceAtLeast(1240)}",
                        targetVal = "${nutrition.targetCalories}",
                        progress = calProgress.coerceAtLeast(0.45f),
                        onBoltClick = onNavigateToWorkout
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CircularActionButton(
                            icon = Icons.Default.FitnessCenter,
                            label = "Workout",
                            onClick = onNavigateToWorkout
                        )
                        CircularActionButton(
                            icon = Icons.Default.Restaurant,
                            label = "Meal",
                            onClick = onNavigateToNutrition
                        )
                        CircularActionButton(
                            icon = Icons.Default.WaterDrop,
                            label = "Water",
                            tint = Color(0xFF38BDF8),
                            onClick = { onAddWater(250) }
                        )
                        CircularActionButton(
                            icon = Icons.Default.CloudSync,
                            label = "Sync",
                            tint = MaterialTheme.colorScheme.primary,
                            onClick = onCheckForUpdates
                        )
                    }
                }

                item {
                    Text(
                        text = "Daily Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    PulseCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = "Steps",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "2.340",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Protein",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${nutrition.consumedProteinG.toInt()} / ${nutrition.targetProteinG}g",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = extra.proteinColor
                                )
                            }

                            WhoopCircularTelemetryDial(
                                current = nutrition.consumedCalories,
                                target = nutrition.targetCalories.toDouble(),
                                unit = "KCAL",
                                label = "BURNED",
                                size = 110.dp,
                                strokeWidth = 10.dp,
                                activeColor = MaterialTheme.colorScheme.primary
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Calories Burned",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "320 kcal",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Water",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${profile.waterConsumedMl} ml",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }
                    }
                }

                item {
                    CoralInsightBanner(
                        title = "Stretching after workouts improves your sleep quality.",
                        onDismiss = {},
                        onSetRoutine = onNavigateToWorkout
                    )
                }

                // Quick Previews of Additional Layouts (Tachometer & Longevity)
                item {
                    RadiantMacroTachometerCard(
                        currentKcal = nutrition.consumedCalories.toInt().coerceAtLeast(1190),
                        targetKcal = nutrition.targetCalories.coerceAtLeast(3000),
                        proteinG = nutrition.consumedProteinG.toInt().coerceAtLeast(80),
                        proteinTargetG = nutrition.targetProteinG,
                        fatG = 290,
                        fatTargetG = 350,
                        carbsG = 221,
                        carbsTargetG = 250
                    )
                }

                item {
                    HydrationAndHeartSplitCards(
                        cupsCurrent = (profile.waterConsumedMl / 250).coerceIn(1, 10),
                        cupsTarget = 10,
                        heartBpm = 68,
                        heartTargetBpm = 125
                    )
                }

                item {
                    TodaysPlanSection(onSeeAllClick = onNavigateToNutrition)
                }

                item {
                    MultiWaveformActivityGraphCard(
                        totalKcal = 810,
                        increasePct = 23,
                        walkingKcal = 421,
                        runningKcal = 109,
                        workoutKcal = 280,
                        title = "Todays activity"
                    )
                }
            }
        }

        // High-Protein Shake Suggestion Card
        item {
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

        // Muscle Volume Distribution
        item {
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

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
