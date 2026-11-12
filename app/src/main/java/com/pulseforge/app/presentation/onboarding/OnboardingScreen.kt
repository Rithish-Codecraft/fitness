package com.pulseforge.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.domain.calculator.GoalFeasibilityEngine
import com.pulseforge.app.domain.calculator.TdeeCalculator
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.presentation.components.PulseCard
import com.pulseforge.app.presentation.components.SectionHeader
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    var age by remember { mutableStateOf(currentProfile.age.toString()) }
    var heightCm by remember { mutableStateOf(currentProfile.heightCm.roundToInt().toString()) }
    var currentWeightKg by remember { mutableStateOf(currentProfile.currentWeightKg.roundToInt().toString()) }
    var targetWeightKg by remember { mutableStateOf(currentProfile.targetWeightKg.roundToInt().toString()) }
    var timelineWeeks by remember { mutableStateOf(currentProfile.targetTimelineWeeks.toFloat()) }
    var gender by remember { mutableStateOf(currentProfile.gender) }
    var goalType by remember { mutableStateOf(currentProfile.primaryGoal) }
    var selectedMuscles by remember { mutableStateOf(currentProfile.targetMuscleGroups.toSet()) }
    var locality by remember { mutableStateOf(currentProfile.locality) }
    var dietPreference by remember { mutableStateOf(currentProfile.dietPreference) }
    var activityLevel by remember { mutableStateOf(currentProfile.activityLevel) }
    var experienceLevel by remember { mutableStateOf(currentProfile.experienceLevel) }

    // Real-time calculation of Feasibility
    val parsedWeight = currentWeightKg.toDoubleOrNull() ?: 70.0
    val parsedTargetWeight = targetWeightKg.toDoubleOrNull() ?: 75.0
    val parsedHeight = heightCm.toDoubleOrNull() ?: 175.0
    val parsedAge = age.toIntOrNull() ?: 25
    val weeks = timelineWeeks.roundToInt().coerceAtLeast(1)

    val tdee = remember(parsedWeight, parsedHeight, parsedAge, gender, activityLevel) {
        TdeeCalculator.calculateTdee(parsedWeight, parsedHeight, parsedAge, gender, activityLevel)
    }

    val feasibility = remember(parsedWeight, parsedTargetWeight, weeks, tdee, goalType, experienceLevel) {
        GoalFeasibilityEngine.evaluate(
            currentWeightKg = parsedWeight,
            targetWeightKg = parsedTargetWeight,
            timelineWeeks = weeks,
            tdeeKcal = tdee,
            goalType = goalType,
            experienceLevel = experienceLevel
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Profile & Goal Setup",
                subtitle = "Anthropometrics, cultural diet context, and physiological goal modeling"
            )
        }

        // Anthropometrics Card
        item {
            PulseCard {
                Text(
                    text = "Personal Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = currentWeightKg,
                        onValueChange = { currentWeightKg = it },
                        label = { Text("Current (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetWeightKg,
                        onValueChange = { targetWeightKg = it },
                        label = { Text("Target (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Biological Sex", style = MaterialTheme.typography.labelSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Gender.values().forEach { g ->
                        FilterChip(
                            selected = gender == g,
                            onClick = { gender = g },
                            label = { Text(g.label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Timeline Slider & Real-time Feasibility
        item {
            PulseCard(borderGlow = !feasibility.isFeasible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Target Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$weeks Weeks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = timelineWeeks,
                    onValueChange = { timelineWeeks = it },
                    valueRange = 2f..36f,
                    steps = 34
                )

                // Feasibility Status Box
                val statusColor = if (feasibility.isFeasible) Color(0xFF10B981) else Color(0xFFFF5252)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (feasibility.isFeasible) Icons.Default.Check else Icons.Default.Warning,
                                contentDescription = null,
                                tint = statusColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feasibility.statusTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = feasibility.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!feasibility.isFeasible) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 Max safe change in $weeks wks: ${String.format("%.1f", feasibility.maxSafeChangeKg)}kg. Or extend to ${feasibility.recommendedWeeks} wks.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Daily Target: ${feasibility.dailyCalorieTarget} kcal", fontWeight = FontWeight.Bold)
                    Text("Protein: ${feasibility.proteinTargetG}g | Carbs: ${feasibility.carbsTargetG}g", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Primary Goal & Target Muscle Selection
        item {
            PulseCard {
                Text(
                    text = "Primary Goal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                GoalType.values().forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { goalType = goal }
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = goalType == goal, onClick = { goalType = goal })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(goal.label, fontWeight = FontWeight.SemiBold)
                            Text(goal.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Specific Muscle Hypertrophy Focus",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Select muscle groups you want to prioritize in your training split:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MuscleGroup.values().toList().chunked(2).forEach { rowMuscles: List<MuscleGroup> ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowMuscles.forEach { muscle: MuscleGroup ->
                            val isSelected = selectedMuscles.contains(muscle)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val currentSet = selectedMuscles.toMutableSet()
                                    if (isSelected) currentSet.remove(muscle) else currentSet.add(muscle)
                                    selectedMuscles = currentSet
                                },
                                label = { Text(muscle.label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Locality & Dietary Culture
        item {
            PulseCard {
                Text(
                    text = "Locality & Dietary Culture",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tailors food recognition and shake recommendations to your regional pantry:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Locality.values().forEach { loc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { locality = loc }
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = locality == loc, onClick = { locality = loc })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(loc.label, fontWeight = FontWeight.SemiBold)
                            Text(loc.dietaryContext, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Dietary Preference", style = MaterialTheme.typography.labelSmall)
                DietPreference.values().forEach { diet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dietPreference = diet }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = dietPreference == diet, onClick = { dietPreference = diet })
                        Text(diet.label)
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = currentProfile.copy(
                        age = parsedAge,
                        gender = gender,
                        heightCm = parsedHeight,
                        currentWeightKg = parsedWeight,
                        targetWeightKg = parsedTargetWeight,
                        targetTimelineWeeks = weeks,
                        primaryGoal = goalType,
                        targetMuscleGroups = selectedMuscles.toList(),
                        locality = locality,
                        dietPreference = dietPreference,
                        activityLevel = activityLevel,
                        experienceLevel = experienceLevel,
                        tdeeKcal = tdee,
                        targetKcal = feasibility.dailyCalorieTarget,
                        targetProteinG = feasibility.proteinTargetG,
                        targetCarbsG = feasibility.carbsTargetG,
                        targetFatG = feasibility.fatTargetG,
                        isOnboarded = true
                    )
                    onSaveProfile(updated)
                    onNavigateBack?.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Save Profile & Generate Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
