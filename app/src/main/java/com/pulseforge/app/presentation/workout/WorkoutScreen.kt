package com.pulseforge.app.presentation.workout

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
import androidx.compose.ui.unit.sp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.calculator.OneRepMaxCalculator
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.MetricBadge
import com.pulseforge.app.presentation.components.PulseCard
import com.pulseforge.app.presentation.components.SectionHeader
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    uiState: MainUiState,
    onLogWorkout: (String, Int, List<ExerciseLog>, String) -> Unit
) {
    val extra = LocalExtraColors.current
    var isLoggingNewSession by remember { mutableStateOf(false) }

    // State for creating a new workout session
    var sessionTitle by remember { mutableStateOf("Hypertrophy Push & Lats Session") }
    var durationMinutes by remember { mutableStateOf("60") }
    var sessionNotes by remember { mutableStateOf("") }
    val currentExercises = remember { mutableStateListOf<ExerciseLog>() }

    // Dialog state for adding an exercise to the active session
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("Incline Dumbbell Press") }
    var selectedMuscle by remember { mutableStateOf(MuscleGroup.CHEST) }
    var selectedEquipment by remember { mutableStateOf(EquipmentType.DUMBBELL) }
    var machineSettings by remember { mutableStateOf("Seat Notch 3, 30° Incline") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionHeader(
                title = "Workout & Equipment Logger",
                subtitle = "Track sets, reps, pin/plate weights, 1RM, and kcal burned"
            ) {
                Button(
                    onClick = { isLoggingNewSession = !isLoggingNewSession },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(if (isLoggingNewSession) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isLoggingNewSession) "Cancel" else "New Workout")
                }
            }
        }

        // Active Session Creation Card
        if (isLoggingNewSession) {
            item {
                PulseCard(borderGlow = true) {
                    Text(
                        text = "Active Workout Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = sessionTitle,
                            onValueChange = { sessionTitle = it },
                            label = { Text("Workout Title") },
                            modifier = Modifier.weight(2f)
                        )
                        OutlinedTextField(
                            value = durationMinutes,
                            onValueChange = { durationMinutes = it },
                            label = { Text("Mins") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // List of exercises in current session
                    currentExercises.forEachIndexed { index, exercise ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(exercise.exerciseName, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "${exercise.muscleGroup.label} • ${exercise.equipmentType.label}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (exercise.machineSettings.isNotBlank()) {
                                            Text(
                                                text = "Settings: ${exercise.machineSettings}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    IconButton(onClick = { currentExercises.removeAt(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Sets summary
                                exercise.sets.forEach { s ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Set ${s.setNumber}: ${s.weightKg} kg × ${s.reps} reps", style = MaterialTheme.typography.bodySmall)
                                        Text("Est. 1RM: ${String.format("%.1f", s.estimated1RmKg)} kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = extra.proteinColor)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showAddExerciseDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Exercise")
                        }

                        Button(
                            onClick = {
                                val dur = durationMinutes.toIntOrNull() ?: 45
                                if (currentExercises.isNotEmpty()) {
                                    onLogWorkout(sessionTitle, dur, currentExercises.toList(), sessionNotes)
                                    currentExercises.clear()
                                    isLoggingNewSession = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = currentExercises.isNotEmpty()
                        ) {
                            Text("Save Workout")
                        }
                    }
                }
            }
        }

        // Hevy-Style Workout Performance Hero Banner
        item {
            val totalTonnage = uiState.workoutSessions.sumOf { it.totalVolumeKg } / 1000.0
            val totalKcal = uiState.workoutSessions.sumOf { it.kcalBurned }
            PulseCard(borderGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weekly Training Load",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Resistance tonnage & EPOC energy expenditure",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${uiState.workoutSessions.size} SESSIONS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricBadge(
                        label = "Tonnage",
                        value = "${String.format("%.1f", totalTonnage)}t",
                        color = MaterialTheme.colorScheme.primary
                    )
                    MetricBadge(
                        label = "Burned",
                        value = "${totalKcal.toInt()} kcal",
                        color = extra.calorieRing
                    )
                    MetricBadge(
                        label = "Top 1RM",
                        value = "87 kg",
                        color = extra.proteinColor
                    )
                }
            }
        }

        // Past Logged Sessions
        item {
            Text(
                text = "Past Workout Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.workoutSessions) { session ->
            PulseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${session.date} • ${session.durationMinutes} mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(extra.calorieRing.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${session.kcalBurned.toInt()} kcal",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = extra.calorieRing
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%.1f", session.totalVolumeKg / 1000.0)}t Volume",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Exercises in session with Hevy style cards
                session.exercises.forEach { ex ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ex.exerciseName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = ex.equipmentType.label.split(" ").first(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = "1RM: ${ex.maxEstimated1Rm.toInt()}kg",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = extra.proteinColor
                            )
                        }
                        if (ex.machineSettings.isNotBlank()) {
                            Text(
                                text = "Settings: ${ex.machineSettings}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ex.sets.forEach { s ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                        .border(
                                            androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "S${s.setNumber}: ${s.weightKg.toInt()}kg × ${s.reps}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // Add Exercise to Session Dialog
    if (showAddExerciseDialog) {
        var set1Weight by remember { mutableStateOf("60") }
        var set1Reps by remember { mutableStateOf("10") }
        var set2Weight by remember { mutableStateOf("70") }
        var set2Reps by remember { mutableStateOf("8") }
        var set3Weight by remember { mutableStateOf("75") }
        var set3Reps by remember { mutableStateOf("6") }

        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Log Equipment & Exercise") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        OutlinedTextField(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            label = { Text("Exercise Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = machineSettings,
                            onValueChange = { machineSettings = it },
                            label = { Text("Equipment Specifics / Machine Pin / Seat Notch") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text("Target Muscle Group", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(MuscleGroup.CHEST, MuscleGroup.BACK_LATS, MuscleGroup.QUADS, MuscleGroup.SHOULDERS).forEach { m ->
                                FilterChip(
                                    selected = selectedMuscle == m,
                                    onClick = { selectedMuscle = m },
                                    label = { Text(m.label.split(" ").first()) }
                                )
                            }
                        }
                    }
                    item {
                        Text("Equipment Category", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(EquipmentType.BARBELL, EquipmentType.DUMBBELL, EquipmentType.CABLE, EquipmentType.PIN_LOADED_MACHINE).forEach { eq ->
                                FilterChip(
                                    selected = selectedEquipment == eq,
                                    onClick = { selectedEquipment = eq },
                                    label = { Text(eq.label.split(" ").first()) }
                                )
                            }
                        }
                    }
                    item {
                        Text("Working Sets (Weight kg × Reps)", fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = set1Weight, onValueChange = { set1Weight = it }, label = { Text("Set 1 kg") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = set1Reps, onValueChange = { set1Reps = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = set2Weight, onValueChange = { set2Weight = it }, label = { Text("Set 2 kg") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = set2Reps, onValueChange = { set2Reps = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = set3Weight, onValueChange = { set3Weight = it }, label = { Text("Set 3 kg") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = set3Reps, onValueChange = { set3Reps = it }, label = { Text("Reps") }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sets = mutableListOf<ExerciseSet>()
                        val w1 = set1Weight.toDoubleOrNull() ?: 0.0
                        val r1 = set1Reps.toIntOrNull() ?: 0
                        if (w1 > 0 && r1 > 0) sets.add(ExerciseSet(setNumber = 1, weightKg = w1, reps = r1, estimated1RmKg = OneRepMaxCalculator.calculate(w1, r1)))

                        val w2 = set2Weight.toDoubleOrNull() ?: 0.0
                        val r2 = set2Reps.toIntOrNull() ?: 0
                        if (w2 > 0 && r2 > 0) sets.add(ExerciseSet(setNumber = 2, weightKg = w2, reps = r2, estimated1RmKg = OneRepMaxCalculator.calculate(w2, r2)))

                        val w3 = set3Weight.toDoubleOrNull() ?: 0.0
                        val r3 = set3Reps.toIntOrNull() ?: 0
                        if (w3 > 0 && r3 > 0) sets.add(ExerciseSet(setNumber = 3, weightKg = w3, reps = r3, estimated1RmKg = OneRepMaxCalculator.calculate(w3, r3)))

                        val exercise = ExerciseLog(
                            exerciseName = exerciseName,
                            muscleGroup = selectedMuscle,
                            equipmentType = selectedEquipment,
                            machineSettings = machineSettings,
                            sets = sets
                        )
                        currentExercises.add(exercise)
                        showAddExerciseDialog = false
                    }
                ) {
                    Text("Add to Workout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

