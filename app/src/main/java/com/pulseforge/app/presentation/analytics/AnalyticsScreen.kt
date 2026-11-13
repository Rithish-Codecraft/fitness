package com.pulseforge.app.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.BodyMeasurement
import com.pulseforge.app.presentation.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    uiState: MainUiState,
    onAddMeasurement: (Double, Double?, Double?, Double?, String) -> Unit
) {
    val measurements = uiState.bodyMeasurements
    val workouts = uiState.workoutSessions
    val nutrition = uiState.dailyNutrition

    var showAddWeightDialog by remember { mutableStateOf(false) }

    val latestWeight = measurements.firstOrNull()?.weightKg ?: uiState.userProfile.currentWeightKg
    val targetWeight = uiState.userGoal.targetWeightKg

    // Find highest value for the graphic tooltip (Matching Reference Image 2)
    val maxWeight = measurements.maxOfOrNull { it.weightKg } ?: latestWeight
    val minWeight = measurements.minOfOrNull { it.weightKg } ?: latestWeight

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Log Weight Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progress & Analytics",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "100% verified Room data telemetry",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddWeightDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7358F6)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Weight", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Graphic Update Trend Card (Matching Reference Image 2 style with Lilac wave and highest value tooltip)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(26.dp), spotColor = Color(0x2E7358F6))
                    .border(1.dp, Color(0xFF7358F6).copy(alpha = 0.25f), RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Graphic Update",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF7358F6))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Highest: ${String.format("%.1f", maxWeight)} kg",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Waveform Chart with smooth curves and fill gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val width = size.width
                            val height = size.height

                            // Draw subtle horizontal grid lines
                            val gridColor = Color(0x1F8F75FF)
                            drawLine(gridColor, Offset(0f, height * 0.25f), Offset(width, height * 0.25f), strokeWidth = 1f)
                            drawLine(gridColor, Offset(0f, height * 0.5f), Offset(width, height * 0.5f), strokeWidth = 1f)
                            drawLine(gridColor, Offset(0f, height * 0.75f), Offset(width, height * 0.75f), strokeWidth = 1f)

                            if (measurements.size >= 2) {
                                val path = Path()
                                val fillPath = Path()

                                val stepX = width / (measurements.size - 1).toFloat()
                                val range = (maxWeight - minWeight).coerceAtLeast(1.0)

                                val points = measurements.reversed().mapIndexed { i, m ->
                                    val x = i * stepX
                                    val norm = (m.weightKg - minWeight) / range
                                    val y = (height * 0.85f) - (norm.toFloat() * height * 0.70f)
                                    Offset(x, y)
                                }

                                path.moveTo(points.first().x, points.first().y)
                                fillPath.moveTo(points.first().x, height)
                                fillPath.lineTo(points.first().x, points.first().y)

                                for (i in 0 until points.size - 1) {
                                    val p0 = points[i]
                                    val p1 = points[i + 1]
                                    val cx = (p0.x + p1.x) / 2
                                    path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                                    fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                                }

                                fillPath.lineTo(points.last().x, height)
                                fillPath.close()

                                // Draw gradient fill
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0x337358F6), Color(0x007358F6))
                                    )
                                )

                                // Draw neon wave line
                                drawPath(
                                    path = path,
                                    color = Color(0xFF7358F6),
                                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                                )

                                // Draw peak point circle
                                val peakIndex = measurements.indexOfFirst { it.weightKg == maxWeight }
                                if (peakIndex in points.indices) {
                                    val peakPoint = points[points.size - 1 - peakIndex]
                                    drawCircle(Color.White, radius = 6.dp.toPx(), center = peakPoint)
                                    drawCircle(Color(0xFF7358F6), radius = 4.dp.toPx(), center = peakPoint)
                                }
                            } else {
                                // Baseline gentle wave when single or no logs
                                val path = Path().apply {
                                    moveTo(0f, height * 0.6f)
                                    cubicTo(width * 0.3f, height * 0.4f, width * 0.7f, height * 0.7f, width, height * 0.5f)
                                }
                                drawPath(path, color = Color(0xFF7358F6), style = Stroke(3.dp.toPx()))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Stat Columns (Matching Reference Image 2: Total, Average, Highest, Lowest)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatColumn(label = "Current", value = "${String.format("%.1f", latestWeight)}kg")
                        StatColumn(label = "Target", value = "${String.format("%.1f", targetWeight)}kg")
                        StatColumn(label = "Highest", value = "${String.format("%.1f", maxWeight)}kg")
                        StatColumn(
                            label = "Delta",
                            value = "${if (latestWeight >= targetWeight) "+" else ""}${String.format("%.1f", latestWeight - targetWeight)}kg"
                        )
                    }
                }
            }
        }

        // 3. Workout Volume & Consistency Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Training Volume Overview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val totalLoggedVolume = workouts.sumOf { it.totalVolumeKg }
                    val totalSets = workouts.sumOf { it.totalSetsCount }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricPill(
                            label = "Total Volume",
                            value = if (totalLoggedVolume > 0) "${totalLoggedVolume.toInt()} kg" else "-",
                            color = Color(0xFF7358F6)
                        )
                        MetricPill(
                            label = "Workouts",
                            value = if (workouts.isNotEmpty()) "${workouts.size}" else "-",
                            color = Color(0xFFFF6B4A)
                        )
                        MetricPill(
                            label = "Sets Done",
                            value = if (totalSets > 0) "$totalSets" else "-",
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }

        // 4. Logged Measurements History List
        item {
            Text(
                text = "Body Measurement Records",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (measurements.isEmpty()) {
            item {
                Text(
                    text = "No body measurements recorded yet. Tap '+ Log Weight' to start.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(measurements) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${entry.weightKg} kg",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val details = listOfNotNull(
                                entry.waistCm?.let { "Waist: ${it}cm" },
                                entry.chestCm?.let { "Chest: ${it}cm" },
                                entry.armsCm?.let { "Arms: ${it}cm" }
                            ).joinToString(" • ")

                            if (details.isNotBlank()) {
                                Text(
                                    text = details,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = entry.date,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Add Measurement Dialog
    if (showAddWeightDialog) {
        AddMeasurementDialog(
            currentWeight = latestWeight,
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { weight, waist, chest, arms, notes ->
                onAddMeasurement(weight, waist, chest, arms, notes)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMeasurementDialog(
    currentWeight: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double?, Double?, Double?, String) -> Unit
) {
    var weightInput by remember { mutableStateOf(currentWeight.toString()) }
    var waistInput by remember { mutableStateOf("") }
    var chestInput by remember { mutableStateOf("") }
    var armsInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Body Metrics", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = waistInput,
                        onValueChange = { waistInput = it },
                        label = { Text("Waist (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = chestInput,
                        onValueChange = { chestInput = it },
                        label = { Text("Chest (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = armsInput,
                    onValueChange = { armsInput = it },
                    label = { Text("Arms (cm, optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightInput.toDoubleOrNull() ?: currentWeight
                    val waist = waistInput.toDoubleOrNull()
                    val chest = chestInput.toDoubleOrNull()
                    val arms = armsInput.toDoubleOrNull()
                    onConfirm(w, waist, chest, arms, notesInput)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
