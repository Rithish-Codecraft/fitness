package com.pulseforge.app.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.calculator.GoalFeasibilityEngine
import com.pulseforge.app.domain.calculator.TdeeCalculator
import com.pulseforge.app.domain.model.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentProfile: UserProfile,
    currentGoal: UserGoal,
    onCompleteOnboarding: (UserProfile, UserGoal) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var age by remember { mutableStateOf(currentProfile.age.toString()) }
    var heightCm by remember { mutableStateOf(currentProfile.heightCm.roundToInt().toString()) }
    var currentWeightKg by remember { mutableStateOf(currentProfile.currentWeightKg.roundToInt().toString()) }
    var gender by remember { mutableStateOf(currentProfile.gender) }
    var activityLevel by remember { mutableStateOf(currentProfile.activityLevel) }

    var goalType by remember { mutableStateOf(currentGoal.goalType) }
    var targetWeightKg by remember { mutableStateOf(currentGoal.targetWeightKg.roundToInt().toString()) }
    var timelineWeeks by remember { mutableStateOf(currentGoal.targetWeeks.toFloat()) }

    // Real-time calculation of BMR, TDEE and Feasibility
    val parsedWeight = currentWeightKg.toDoubleOrNull() ?: 70.0
    val parsedTargetWeight = targetWeightKg.toDoubleOrNull() ?: 75.0
    val parsedHeight = heightCm.toDoubleOrNull() ?: 175.0
    val parsedAge = age.toIntOrNull() ?: 25
    val weeks = timelineWeeks.roundToInt().coerceAtLeast(1)

    val bmr = remember(parsedWeight, parsedHeight, parsedAge, gender) {
        TdeeCalculator.calculateBmr(parsedWeight, parsedHeight, parsedAge, gender).roundToInt()
    }

    val tdee = remember(parsedWeight, parsedHeight, parsedAge, gender, activityLevel) {
        TdeeCalculator.calculateTdee(parsedWeight, parsedHeight, parsedAge, gender, activityLevel)
    }

    val feasibility = remember(parsedWeight, parsedTargetWeight, weeks, tdee, goalType) {
        GoalFeasibilityEngine.evaluate(
            currentWeightKg = parsedWeight,
            targetWeightKg = parsedTargetWeight,
            timelineWeeks = weeks,
            tdeeKcal = tdee,
            goalType = goalType
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Text(
                text = "Profile & Goal Setup",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Transparent Mifflin-St Jeor energy calculations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 2. Personal Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "1. Personal Baseline",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Gender.values().forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g.label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = heightCm,
                            onValueChange = { heightCm = it },
                            label = { Text("Height (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = currentWeightKg,
                            onValueChange = { currentWeightKg = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "Activity Level",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ActivityLevel.values()) { level ->
                            FilterChip(
                                selected = activityLevel == level,
                                onClick = { activityLevel = level },
                                label = { Text(level.label.take(12), fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 3. Goal Selection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "2. Target Objective",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(GoalType.values()) { g ->
                            FilterChip(
                                selected = goalType == g,
                                onClick = { goalType = g },
                                label = { Text(g.label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetWeightKg,
                            onValueChange = { targetWeightKg = it },
                            label = { Text("Target Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Timeline: ${timelineWeeks.toInt()} weeks",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Slider(
                                value = timelineWeeks,
                                onValueChange = { timelineWeeks = it },
                                valueRange = 4f..52f,
                                steps = 24
                            )
                        }
                    }
                }
            }
        }

        // 4. Transparent Energy Calculation & Feasibility Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .border(
                        1.dp,
                        if (feasibility.isSafe) Color(0xFF10B981) else Color(0xFFF59E0B),
                        RoundedCornerShape(22.dp)
                    ),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Feasibility & Energy Targets",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = if (feasibility.isSafe) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (feasibility.isSafe) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )
                    }

                    Text(
                        text = feasibility.message,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$bmr", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "BMR kcal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$tdee", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "TDEE kcal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${feasibility.suggestedCalories}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF7358F6))
                            Text(text = "Target kcal", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "Protein: ${feasibility.suggestedProteinG}g", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF8F75FF))
                        Text(text = "Carbs: ${feasibility.suggestedCarbsG}g", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFFFB86C))
                        Text(text = "Fat: ${feasibility.suggestedFatG}g", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF38BDF8))
                    }
                }
            }
        }

        // 5. Complete Button
        item {
            Button(
                onClick = {
                    val updatedProfile = currentProfile.copy(
                        name = name.ifBlank { "Athlete" },
                        age = parsedAge,
                        gender = gender,
                        heightCm = parsedHeight,
                        currentWeightKg = parsedWeight,
                        activityLevel = activityLevel,
                        isOnboarded = true
                    )
                    val updatedGoal = currentGoal.copy(
                        goalType = goalType,
                        targetWeightKg = parsedTargetWeight,
                        targetWeeks = weeks,
                        bmrKcal = bmr,
                        tdeeKcal = tdee,
                        targetCalories = feasibility.suggestedCalories,
                        targetProteinG = feasibility.suggestedProteinG,
                        targetCarbsG = feasibility.suggestedCarbsG,
                        targetFatG = feasibility.suggestedFatG,
                        waterTargetMl = 3000
                    )
                    onCompleteOnboarding(updatedProfile, updatedGoal)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7358F6))
            ) {
                Text(
                    text = "Save & Enter Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}
