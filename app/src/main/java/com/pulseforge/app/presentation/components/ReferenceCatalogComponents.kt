package com.pulseforge.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.MealType

/**
 * 1. Arch Vault Trio Cards (Calories, Time, Distance) - from reference sY4MeDuAEY.jpg
 */
@Composable
fun ArchVaultTrioCard(
    caloriesKcal: Int = 456,
    timeMinutes: String = "2:05",
    distanceMiles: String = "3.25",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Vault 1: Calories (Blue Arch)
        ArchVaultItem(
            modifier = Modifier.weight(1f),
            archColor = Color(0xFF68B2BD),
            icon = Icons.Default.LocalFireDepartment,
            label = "Calories",
            value = "$caloriesKcal",
            unit = "kcal"
        )
        // Vault 2: Time (Green Arch)
        ArchVaultItem(
            modifier = Modifier.weight(1f),
            archColor = Color(0xFF67B594),
            icon = Icons.Outlined.Schedule,
            label = "Time",
            value = timeMinutes,
            unit = "Min"
        )
        // Vault 3: Distance (Mustard Yellow Arch)
        ArchVaultItem(
            modifier = Modifier.weight(1f),
            archColor = Color(0xFFE2B755),
            icon = Icons.Default.DirectionsRun,
            label = "Distance",
            value = distanceMiles,
            unit = "Mile"
        )
    }
}

@Composable
private fun ArchVaultItem(
    archColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(170.dp),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
        color = archColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.8f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Text(
                    text = unit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 2. Strength PR & Move Workouts Summary Card - from reference 57ZPuhS5hC.jpg
 */
@Composable
fun StrengthPrSummaryCard(
    moveCompleted: Int = 6,
    moveTotal: Int = 6,
    strengthCompleted: Int = 3,
    strengthTotal: Int = 5,
    benchPressLbs: Int = 130,
    benchGainLbs: Int = 10,
    backSquatLbs: Int = 180,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top 2 mini tiles: Move Workouts & Strength Workouts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E1F24)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Move Workouts", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$moveCompleted/$moveTotal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFD4FB44), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFD4FB44), modifier = Modifier.size(20.dp))
                    }
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E1F24)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Strength Workouts", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$strengthCompleted/$strengthTotal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFD4FB44), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFFD4FB44), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Bottom PR Tiles: Bench Press & Back Squat
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E1F24)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Bench Press", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFFD4FB44), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$benchPressLbs lbs", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4FB44))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("+$benchGainLbs lbs last week", fontSize = 10.sp, color = Color(0xFFD4FB44).copy(alpha = 0.8f))
                }
            }

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1E1F24)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Back Squat", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFFD4FB44), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$backSquatLbs lbs", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD4FB44))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("New Personal Record!", fontSize = 10.sp, color = Color(0xFF10B981))
                }
            }
        }
    }
}

/**
 * 3. Activity Timeline Summary Card - from reference FVupAAdzUP.jpg
 */
@Composable
fun ActivityTimelineSummaryCard(
    duration: String = "17h 18m",
    durationTarget: String = "21h 1m",
    activeEnergyKcal: String = "4 979",
    activeEnergyTarget: String = "6 149 kcal",
    distanceKm: String = "99,14",
    distanceTarget: String = "119,98 km",
    elevationM: String = "200",
    elevationTarget: String = "191 m",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF141416)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with Period Capsule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF22242A)
                ) {
                    Text(
                        text = "DAYS 1 – 15 ✕",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-box grid
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Duration (Highlighted in Warm Orange)
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFF6B35)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Duration", fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(duration, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$durationTarget target", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    // Active Energy
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF22242A)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Active Energy", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$activeEnergyKcal kcal", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(activeEnergyTarget, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Distance
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF22242A)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Distance", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$distanceKm km", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(distanceTarget, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    }

                    // Elevation Gain
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF22242A)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Elevation Gain", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$elevationM m", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(elevationTarget, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. Nutrition Label OCR & Micronutrient Checklist Card - from reference 2HGQuXbdT7.jpg
 */
@Composable
fun NutritionLabelMicronutrientCard(
    calories: Int = 170,
    proteinG: Double = 6.0,
    carbsG: Double = 31.0,
    fatG: Double = 3.0,
    modifier: Modifier = Modifier
) {
    val nutrients = listOf(
        "Total Fat" to ("3/78g" to 0.04f),
        "Saturated Fat" to ("0.5/20g" to 0.03f),
        "Trans Fat" to ("0/1g" to 0.00f),
        "Cholesterol" to ("0/300mg" to 0.00f),
        "Sodium" to ("0/2300mg" to 0.00f),
        "Potassium" to ("170/4700mg" to 0.04f),
        "Total Carbohydrate" to ("31/275g" to 0.11f),
        "Dietary Fiber" to ("4/28g" to 0.14f),
        "Sugars" to ("1/50g" to 0.02f),
        "Added Sugars" to ("0/50g" to 0.00f)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Nutrition Facts (OCR Verified)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Top Macro Dials
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroArcDial(label = "PROTEIN", amount = "${proteinG.toInt()}g", arcColor = Color(0xFF38BDF8), progress = 0.35f)
                MacroArcDial(label = "CARBS", amount = "${carbsG.toInt()}g", arcColor = Color(0xFFF97316), progress = 0.55f)
                MacroArcDial(label = "FAT", amount = "${fatG.toInt()}g", arcColor = Color(0xFFA855F7), progress = 0.20f)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Calories $calories Kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text(text = "Daily Value %", fontSize = 12.sp, color = Color(0xFF6B7280), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Micronutrient checklist list
            nutrients.forEach { (name, pair) ->
                val (amt, ratio) = pair
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$name $amt", fontSize = 12.sp, color = Color(0xFF374151), fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${(ratio * 100).toInt()}%", fontSize = 12.sp, color = Color(0xFF6B7280))
                            if (ratio < 0.15f) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xFF3B82F6),
                        trackColor = Color(0xFFF3F4F6)
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroArcDial(label: String, amount: String, arcColor: Color, progress: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(62.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 5.dp.toPx()
                drawArc(
                    color = arcColor.copy(alpha = 0.15f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = arcColor,
                    startAngle = 135f,
                    sweepAngle = 270f * progress,
                    useCenter = false,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF6B7280))
    }
}

/**
 * 5. AI Food Recognition Result Sheet Card - from reference eZroVTc5rY.jpg
 */
@Composable
fun AiFoodScannerResultCard(
    foodName: String = "Protein Oats with Mixed Berries",
    portionText: String = "1 portion (300g)",
    aiConfidencePct: Int = 98,
    caloriesKcal: Int = 420,
    proteinG: Int = 32,
    carbsG: Int = 54,
    fatG: Int = 8,
    healthScore: Int = 9,
    onSaveMeal: (MealType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MealType.BREAKFAST) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with AI confidence badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = foodName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Text(text = portionText, fontSize = 12.sp, color = Color(0xFF6B7280))
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFECFDF5)
                ) {
                    Text(
                        text = "✨ AI Conf: $aiConfidencePct%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total Calories & 3 Macros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$caloriesKcal KCAL",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE11D48)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MacroPillBadge(label = "PROTEIN", amount = "${proteinG}g", color = Color(0xFF2563EB))
                    MacroPillBadge(label = "CARBS", amount = "${carbsG}g", color = Color(0xFFD97706))
                    MacroPillBadge(label = "FAT", amount = "${fatG}g", color = Color(0xFF7C3AED))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Health / Quality Score Slider (3/10 or 9/10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Health Quality Score", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
                Text(text = "$healthScore/10", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { healthScore / 10f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF10B981),
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Category Selectors
            Text(text = "Meal Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    MealType.BREAKFAST to "Breakfast",
                    MealType.LUNCH to "Lunch",
                    MealType.DINNER to "Dinner",
                    MealType.SNACK to "Snack"
                ).forEach { (type, label) ->
                    val isSelected = selectedCategory == type
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCategory = type }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSaveMeal(selectedCategory) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm & Log Meal", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun MacroPillBadge(label: String, amount: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = amount, fontSize = 11.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

/**
 * 6. Elite Workout Set Table Card - from reference KJ93mTngMB.jpg
 */
data class ExerciseSetRow(val setNumber: Int, val weightKg: Double, val reps: Int, val difficulty: String)

@Composable
fun WorkoutSetTableCard(
    exerciseName: String = "Incline Bench Press",
    muscles: String = "Chest • Triceps • Shoulders",
    sets: List<ExerciseSetRow> = listOf(
        ExerciseSetRow(1, 40.0, 8, "Warmup"),
        ExerciseSetRow(2, 55.0, 8, "Normal"),
        ExerciseSetRow(3, 60.0, 4, "Heavy"),
        ExerciseSetRow(4, 50.0, 10, "Light")
    ),
    onAddSet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF17181C)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = exerciseName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = muscles, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                }
                IconButton(onClick = onAddSet) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add set", tint = Color(0xFFD4FB44))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Set", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
                Text("Weight", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(2f))
                Text("Reps", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(1.5f))
                Text("Difficulty", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.weight(2f), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = Color(0xFF262830), thickness = 1.dp)

            // Set Rows
            sets.forEach { row ->
                val diffColor = when (row.difficulty.lowercase()) {
                    "warmup" -> Color(0xFF38BDF8)
                    "heavy" -> Color(0xFFEF4444)
                    "light" -> Color(0xFF10B981)
                    else -> Color(0xFFFBBF24)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${row.setNumber}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                    Text("${row.weightKg.toInt()} kg", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.weight(2f))
                    Text("x${row.reps}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.weight(1.5f))
                    Row(
                        modifier = Modifier.weight(2f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(diffColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(row.difficulty, fontSize = 12.sp, color = diffColor, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

/**
 * 7. Muscle Group Filter Chips - from reference KJ93mTngMB.jpg
 */
@Composable
fun MuscleGroupFilterBar(
    selectedMuscle: String = "All",
    onSelectMuscle: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val muscleGroups = listOf("All", "Abs", "Back", "Biceps", "Cardio", "Chest", "Legs", "Shoulders", "Triceps")

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(muscleGroups) { muscle ->
            val isSelected = muscle == selectedMuscle
            Surface(
                shape = RoundedCornerShape(50),
                color = if (isSelected) Color(0xFFD4FB44) else Color(0xFF1E1F24),
                modifier = Modifier.clickable { onSelectMuscle(muscle) }
            ) {
                Text(
                    text = muscle,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

