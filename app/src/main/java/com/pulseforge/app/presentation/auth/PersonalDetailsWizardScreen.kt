package com.pulseforge.app.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.calculator.GoalFeasibilityEngine
import com.pulseforge.app.domain.calculator.TdeeCalculator
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.presentation.components.PulseCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailsWizardScreen(
    userId: String,
    initialName: String,
    currentProfile: UserProfile,
    onCompleteWizard: (UserProfile) -> Unit
) {
    val totalSteps = 6
    var currentStep by remember { mutableIntStateOf(0) }

    var name by remember { mutableStateOf(initialName.ifBlank { currentProfile.name }) }
    var ageText by remember { mutableStateOf(currentProfile.age.toString()) }
    var gender by remember { mutableStateOf(currentProfile.gender) }
    var heightText by remember { mutableStateOf(currentProfile.heightCm.roundToInt().toString()) }
    var weightText by remember { mutableStateOf(currentProfile.currentWeightKg.roundToInt().toString()) }
    var goalType by remember { mutableStateOf(currentProfile.primaryGoal) }
    var targetWeightText by remember { mutableStateOf(currentProfile.targetWeightKg.roundToInt().toString()) }
    var timelineWeeks by remember { mutableFloatStateOf(currentProfile.targetTimelineWeeks.toFloat()) }
    var activityLevel by remember { mutableStateOf(currentProfile.activityLevel) }
    var experienceLevel by remember { mutableStateOf(currentProfile.experienceLevel) }
    var dietPreference by remember { mutableStateOf(currentProfile.dietPreference) }
    var locality by remember { mutableStateOf(currentProfile.locality) }
    var selectedMuscles by remember { mutableStateOf(currentProfile.targetMuscleGroups.toSet()) }

    val parsedAge = (ageText.toIntOrNull() ?: 25).coerceIn(13, 100)
    val parsedHeight = (heightText.toDoubleOrNull() ?: 175.0).coerceIn(100.0, 240.0)
    val parsedWeight = (weightText.toDoubleOrNull() ?: 72.0).coerceIn(30.0, 250.0)
    val parsedTargetWeight = (targetWeightText.toDoubleOrNull() ?: 76.0).coerceIn(30.0, 250.0)
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "User ID",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "User ID: $userId",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "Card ${currentStep + 1} of $totalSteps",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LinearProgressIndicator(
                    progress = { (currentStep + 1).toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            val targetKcal = feasibility.dailyCalorieTarget
                            val proteinG = (parsedWeight * 2.0).roundToInt().coerceAtLeast(80)
                            val fatG = ((targetKcal * 0.25) / 9.0).roundToInt().coerceAtLeast(40)
                            val carbsG = ((targetKcal - (proteinG * 4) - (fatG * 9)) / 4.0).roundToInt().coerceAtLeast(50)
                            val waterMl = (parsedWeight * 35.0).roundToInt().coerceIn(2000, 5000)

                            onCompleteWizard(
                                currentProfile.copy(
                                    id = userId,
                                    name = name.ifBlank { initialName },
                                    age = parsedAge,
                                    gender = gender,
                                    heightCm = parsedHeight,
                                    currentWeightKg = parsedWeight,
                                    targetWeightKg = parsedTargetWeight,
                                    targetTimelineWeeks = weeks,
                                    primaryGoal = goalType,
                                    targetMuscleGroups = selectedMuscles.toList().ifEmpty {
                                        listOf(MuscleGroup.CHEST, MuscleGroup.BACK_LATS)
                                    },
                                    locality = locality,
                                    dietPreference = dietPreference,
                                    activityLevel = activityLevel,
                                    experienceLevel = experienceLevel,
                                    tdeeKcal = tdee,
                                    targetKcal = targetKcal,
                                    targetProteinG = proteinG,
                                    targetCarbsG = carbsG,
                                    targetFatG = fatG,
                                    waterTargetMl = waterMl,
                                    isOnboarded = true
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1.4f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (currentStep < totalSteps - 1) "Next Card" else "Complete Setup",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (currentStep < totalSteps - 1) {
                            Icons.AutoMirrored.Filled.ArrowForward
                        } else {
                            Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = currentStep, label = "wizard_card") { step ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (step) {
                        0 -> {
                            PulseCard(borderGlow = true) {
                                Text(
                                    text = "Welcome! Let's personalize your profile",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Step 1: Confirm your name, age, and gender.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Display Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = ageText,
                                    onValueChange = { ageText = it.filter { ch -> ch.isDigit() } },
                                    label = { Text("Age (years)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Gender", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Gender.entries.forEach { g ->
                                        FilterChip(
                                            selected = gender == g,
                                            onClick = { gender = g },
                                            label = { Text(g.label) }
                                        )
                                    }
                                }
                            }
                        }

                        1 -> {
                            PulseCard(borderGlow = true) {
                                Text(
                                    text = "Body Measurements",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Step 2: Used to calibrate TDEE, stride length, and active calories.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = heightText,
                                    onValueChange = { heightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                    label = { Text("Height (cm)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = weightText,
                                    onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                    label = { Text("Current Weight (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        2 -> {
                            PulseCard(borderGlow = true) {
                                Text(
                                    text = "Primary Fitness Goal",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Step 3: Choose what matters most right now.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                GoalType.entries.forEach { goal ->
                                    val selected = goalType == goal
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { goalType = goal },
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(goal.label, fontWeight = FontWeight.Bold)
                                                Text(
                                                    goal.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (selected) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            PulseCard(borderGlow = true) {
                                Text(
                                    text = "Target Weight & Timeline",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Step 4: Set your target weight and realistic timeline.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                OutlinedTextField(
                                    value = targetWeightText,
                                    onValueChange = { targetWeightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                    label = { Text("Target Weight (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Timeline: $weeks weeks",
                                    fontWeight = FontWeight.Bold
                                )
                                Slider(
                                    value = timelineWeeks,
                                    onValueChange = { timelineWeeks = it },
                                    valueRange = 4f..52f
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Estimated TDEE: $tdee kcal/day • Target: ${feasibility.dailyCalorieTarget} kcal/day",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = feasibility.message,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        4 -> {
                            PulseCard(borderGlow = true) {
                                Text(
                                    text = "Activity & Experience",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Step 5: Helps us scale workout volume and daily energy burn.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Daily Activity Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(ActivityLevel.entries) { level ->
                                        FilterChip(
                                            selected = activityLevel == level,
                                            onClick = { activityLevel = level },
                                            label = { Text(level.label) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("Training Experience", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(ExperienceLevel.entries) { exp ->
                                        FilterChip(
                                            selected = experienceLevel == exp,
                                            onClick = { experienceLevel = exp },
                                            label = { Text(exp.label) }
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            PulseCard(borderGlow = true) {
                                Text(
                                    text = "Diet & Muscle Focus",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Step 6: Final step! Customize meals and target muscles.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Diet Preference", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(DietPreference.entries) { diet ->
                                        FilterChip(
                                            selected = dietPreference == diet,
                                            onClick = { dietPreference = diet },
                                            label = { Text(diet.label) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Regional Cuisine", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(Locality.entries) { loc ->
                                        FilterChip(
                                            selected = locality == loc,
                                            onClick = { locality = loc },
                                            label = { Text(loc.label) }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Target Muscle Groups", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(MuscleGroup.entries) { muscle ->
                                        val isSelected = muscle in selectedMuscles
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                selectedMuscles = if (isSelected) {
                                                    selectedMuscles - muscle
                                                } else {
                                                    selectedMuscles + muscle
                                                }
                                            },
                                            label = { Text(muscle.label) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
