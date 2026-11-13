package com.pulseforge.app.presentation.nutrition

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.data.ai.ScannedFoodCandidate
import com.pulseforge.app.domain.model.FoodItem
import com.pulseforge.app.domain.model.MealType
import com.pulseforge.app.domain.model.ShakeRecipe
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    uiState: MainUiState,
    onAddManualFood: (MealType, String, Double, String, Double, Double, Double, Double) -> Unit,
    onDeleteFood: (String) -> Unit,
    onScanPlateTrigger: (MealType) -> Unit,
    onScanLabelTrigger: (MealType) -> Unit,
    onConfirmScannedFood: (List<ScannedFoodCandidate>) -> Unit,
    onDismissVerification: () -> Unit
) {
    val extra = LocalExtraColors.current
    val daily = uiState.dailyNutrition
    var showManualDialog by remember { mutableStateOf(false) }
    var selectedMealForAdd by remember { mutableStateOf(MealType.LUNCH) }
    var showShakeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Nutrition & Macro Tracker",
                subtitle = "Track meals via AI plate scan, label OCR, or manual entry"
            ) {
                OutlinedButton(onClick = { showShakeDialog = true }) {
                    Icon(Icons.Default.LocalDrink, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Shakes")
                }
            }
        }

        // Daily Macro Totals - MacroFactor & Whoop Hybrid Telemetry
        item {
            PulseCard(borderGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Calorie Budget",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Budget: ${daily.targetCalories} kcal • ${daily.targetProteinG}g Protein",
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
                            text = "${daily.remainingCalories.toInt()} KCAL LEFT",
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
                        current = daily.consumedCalories,
                        target = daily.targetCalories.toDouble(),
                        unit = "kcal",
                        label = "REMAINING",
                        size = 115.dp,
                        strokeWidth = 9.dp,
                        activeColor = extra.calorieRing
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        MacroProgressBar("Protein", daily.consumedProteinG, daily.targetProteinG.toDouble(), "g", extra.proteinColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        MacroProgressBar("Carbs", daily.consumedCarbsG, daily.targetCarbsG.toDouble(), "g", extra.carbsColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        MacroProgressBar("Fat", daily.consumedFatG, daily.targetFatG.toDouble(), "g", extra.fatColor)
                    }
                }
            }
        }

        // 4 Daily Meals Sections
        MealType.values().forEach { mealType ->
            val itemsForMeal = daily.meals[mealType] ?: emptyList()
            item {
                PulseCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = mealType.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${itemsForMeal.sumOf { it.calories }.toInt()} kcal • ${itemsForMeal.sumOf { it.proteinG }.toInt()}g P",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { onScanPlateTrigger(mealType) }) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Plate", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onScanLabelTrigger(mealType) }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan Label OCR", tint = extra.proteinColor)
                            }
                            IconButton(onClick = {
                                selectedMealForAdd = mealType
                                showManualDialog = true
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Food")
                            }
                        }
                    }

                    if (itemsForMeal.isEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No food logged for ${mealType.label.lowercase()}. Tap + or Scan to log.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        itemsForMeal.forEach { food ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = food.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (food.isAiScanned) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "AI Verified",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${food.quantity.toInt()} ${food.unit} • ${food.calories.toInt()} kcal • ${food.proteinG.toInt()}g P • ${food.carbsG.toInt()}g C • ${food.fatG.toInt()}g F",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteFood(food.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                        }
                    }
                }
            }
        }

        // Reference: AI Food Scanner Result Sheet (from reference eZroVTc5rY.jpg)
        item {
            AiFoodScannerResultCard(
                foodName = "Protein Oats with Mixed Berries",
                portionText = "1 portion (300g)",
                aiConfidencePct = 98,
                caloriesKcal = 420,
                proteinG = 32,
                carbsG = 54,
                fatG = 8,
                healthScore = 9,
                onSaveMeal = { type: MealType ->
                    onAddManualFood(
                        type,
                        "Protein Oats with Mixed Berries",
                        300.0,
                        "g",
                        420.0,
                        32.0,
                        54.0,
                        8.0
                    )
                }
            )
        }

        // Reference: Nutrition Facts Label OCR & Micronutrients (from reference 2HGQuXbdT7.jpg)
        item {
            NutritionLabelMicronutrientCard(
                calories = 170,
                proteinG = 6.0,
                carbsG = 31.0,
                fatG = 3.0
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // AI Scanned Food Verification Dialog / BottomSheet
    if (uiState.isVerificationSheetOpen) {
        AlertDialog(
            onDismissRequest = onDismissVerification,
            title = {
                Text(
                    text = "Verify AI Food Identification",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "PulseForge AI detected the following food items. Verify portions before adding to ${uiState.pendingMealType.label}:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    uiState.scannedCandidatesForVerification.forEach { candidate ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(candidate.name, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Estimated Portion: ${candidate.quantity.toInt()}${candidate.unit}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${candidate.calories.toInt()} kcal", color = extra.calorieRing, fontWeight = FontWeight.Bold)
                                    Text("${candidate.proteinG.toInt()}g Protein", color = extra.proteinColor)
                                    Text("${candidate.carbsG.toInt()}g Carbs", color = extra.carbsColor)
                                    Text("${candidate.fatG.toInt()}g Fat", color = extra.fatColor)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { onConfirmScannedFood(uiState.scannedCandidatesForVerification) }) {
                    Text("Approve & Add to Meal")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissVerification) {
                    Text("Cancel")
                }
            }
        )
    }

    // Manual Food Add Dialog
    if (showManualDialog) {
        var foodName by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("100") }
        var calories by remember { mutableStateOf("200") }
        var protein by remember { mutableStateOf("20") }
        var carbs by remember { mutableStateOf("20") }
        var fat by remember { mutableStateOf("5") }

        AlertDialog(
            onDismissRequest = { showManualDialog = false },
            title = { Text("Add Food to ${selectedMealForAdd.label}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Food Name (e.g. Boiled Eggs)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity (g / ml)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            label = { Text("Calories") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = protein,
                            onValueChange = { protein = it },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = carbs,
                            onValueChange = { carbs = it },
                            label = { Text("Carbs (g)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fat,
                            onValueChange = { fat = it },
                            label = { Text("Fat (g)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cal = calories.toDoubleOrNull() ?: 0.0
                        val p = protein.toDoubleOrNull() ?: 0.0
                        val c = carbs.toDoubleOrNull() ?: 0.0
                        val f = fat.toDoubleOrNull() ?: 0.0
                        val q = quantity.toDoubleOrNull() ?: 100.0
                        if (foodName.isNotBlank()) {
                            onAddManualFood(selectedMealForAdd, foodName, q, "g", cal, p, c, f)
                            showManualDialog = false
                        }
                    }
                ) {
                    Text("Add Item")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // High Protein Shake Recommendations Dialog
    if (showShakeDialog) {
        AlertDialog(
            onDismissRequest = { showShakeDialog = false },
            title = { Text("AI Protein Shake Formulations") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.recommendedShakes) { shake ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(shake.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    Text("${shake.proteinG}g Protein", color = extra.proteinColor, fontWeight = FontWeight.Bold)
                                }
                                Text(shake.purpose, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Calories: ${shake.calories} kcal | Carbs: ${shake.carbsG}g | Fat: ${shake.fatG}g", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Ingredients: ${shake.ingredients.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showShakeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

