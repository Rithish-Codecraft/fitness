package com.pulseforge.app.presentation.workout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.calculator.OneRepMaxCalculator
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    uiState: MainUiState,
    onLogWorkout: (String, Int, List<ExerciseLog>, String) -> Unit
) {
    val extra = LocalExtraColors.current
    var isLoggingNewSession by remember { mutableStateOf(false) }

    // Muscle & Exercise Studio state
    var selectedMuscleFilter by remember { mutableStateOf<MuscleGroup?>(null) }
    val filteredExercises = remember(selectedMuscleFilter) {
        ExerciseCatalog.forMuscle(selectedMuscleFilter)
    }
    var selectedExercise by remember { mutableStateOf(ExerciseCatalog.allExercises.first()) }

    // Ensure selectedExercise stays aligned when muscle filter changes
    LaunchedEffect(selectedMuscleFilter) {
        if (selectedMuscleFilter != null &&
            selectedExercise.primaryMuscle != selectedMuscleFilter &&
            selectedMuscleFilter !in selectedExercise.secondaryMuscles
        ) {
            filteredExercises.firstOrNull()?.let { selectedExercise = it }
        }
    }

    // Active workout session state
    var sessionTitle by remember { mutableStateOf("Hypertrophy Push & Lats Session") }
    var durationMinutes by remember { mutableStateOf("60") }
    var sessionNotes by remember { mutableStateOf("") }
    val currentExercises = remember { mutableStateListOf<ExerciseLog>() }

    // Rest Timer & Celebration Motion state
    var restSecondsRemaining by remember { mutableIntStateOf(0) }
    var restTotalSeconds by remember { mutableIntStateOf(60) }
    var isRestTimerRunning by remember { mutableStateOf(false) }
    var showCelebrationBanner by remember { mutableStateOf(false) }
    LaunchedEffect(isRestTimerRunning, restSecondsRemaining) {
        if (isRestTimerRunning && restSecondsRemaining > 0) {
            delay(1000L)
            restSecondsRemaining -= 1
            if (restSecondsRemaining == 0) isRestTimerRunning = false
        }
    }
    LaunchedEffect(showCelebrationBanner) {
        if (showCelebrationBanner) {
            delay(3500L)
            showCelebrationBanner = false
        }
    }

    // Dialog state for adding an exercise to the active session
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf(selectedExercise.name) }
    var selectedMuscle by remember { mutableStateOf(selectedExercise.primaryMuscle) }
    var selectedEquipment by remember { mutableStateOf(selectedExercise.equipment) }
    var machineSettings by remember { mutableStateOf(selectedExercise.defaultSettings) }

    fun openDialogForExercise(def: ExerciseDefinition) {
        exerciseName = def.name
        selectedMuscle = def.primaryMuscle
        selectedEquipment = def.equipment
        machineSettings = def.defaultSettings
        isLoggingNewSession = true
        showAddExerciseDialog = true
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
                title = "Workout & Muscle Studio",
                subtitle = "Anatomical muscle targeting, live form animation & 1RM logging"
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

        if (!isLoggingNewSession) {
            item {
                LiftWorkoutInsightsSection(
                    sessions = uiState.workoutSessions,
                    userProfile = uiState.userProfile,
                    multiDayRecordings = uiState.multiDayRecordings,
                    onStartWorkoutClick = { isLoggingNewSession = true }
                )
            }
        }

        // Interactive Muscle Group Filter Bar (All + all 10 MuscleGroups)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    MuscleChip(
                        label = "All Muscles",
                        isSelected = selectedMuscleFilter == null,
                        onClick = { selectedMuscleFilter = null }
                    )
                }
                items(MuscleGroup.entries) { muscle ->
                    MuscleChip(
                        label = muscle.label.split(" ").first(),
                        isSelected = selectedMuscleFilter == muscle,
                        onClick = {
                            selectedMuscleFilter = if (selectedMuscleFilter == muscle) null else muscle
                        }
                    )
                }
            }
        }

        // Exercise Picker Strip for selected muscle
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredExercises, key = { it.id }) { ex ->
                    val isSelected = ex.id == selectedExercise.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .width(185.dp)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else extra.cardBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedExercise = ex }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = ex.primaryMuscle.label.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.7.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ex.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${ex.equipment.label.split(" ").first()} • ${ex.difficulty}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Workout Logged Celebration Banner (UI/UX Motion)
        item {
            AnimatedVisibility(
                visible = showCelebrationBanner,
                enter = fadeIn(tween(260)) + expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + scaleIn(initialScale = 0.92f),
                exit = fadeOut(tween(220)) + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.16f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Workout Logged & 1RM Updated!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tonnage and EPOC burn synced to your weekly load.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Anatomical Human Body Muscle Map + Live Form Animation Card with smooth transition
        item {
            AnimatedContent(
                targetState = selectedExercise,
                transitionSpec = {
                    (fadeIn(tween(260)) + slideInHorizontally { it / 12 }) togetherWith
                        (fadeOut(tween(180)) + slideOutHorizontally { -it / 12 })
                },
                label = "exerciseStudioTransition"
            ) { activeEx ->
                PulseCard(borderGlow = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Text(
                                text = activeEx.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activeEx.primaryMuscle.label} • ${activeEx.equipment.label}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Button(
                            onClick = { openDialogForExercise(activeEx) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Set", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Actual Human Body Muscle Map (Front & Back)
                    HumanMuscleBodyView(
                        primaryMuscle = activeEx.primaryMuscle,
                        secondaryMuscles = activeEx.secondaryMuscles,
                        onMuscleSelected = { tappedMuscle ->
                            selectedMuscleFilter = tappedMuscle
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Live 3D-Shaded Exercise Animation
                    ExerciseAnimationCanvas(exercise = activeEx)

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. Form Cues & Default Machine Settings
                    Text(
                        text = "Setup: ${activeEx.defaultSettings}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    activeEx.cues.forEachIndexed { i, cue ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${i + 1}.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(18.dp)
                            )
                            Text(
                                text = cue,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Dynamic Workout Set Table Card synced to selectedExercise
        item {
            val secondaryText = if (selectedExercise.secondaryMuscles.isNotEmpty()) {
                " • " + selectedExercise.secondaryMuscles.joinToString(" • ") { it.label.split(" ").first() }
            } else ""
            val activeMatch = currentExercises.firstOrNull {
                it.exerciseName.equals(selectedExercise.name, ignoreCase = true)
            }
            val tableRows = if (activeMatch != null && activeMatch.sets.isNotEmpty()) {
                activeMatch.sets.map { s ->
                    ExerciseSetRow(
                        setNumber = s.setNumber,
                        weightKg = s.weightKg,
                        reps = s.reps,
                        difficulty = when {
                            s.rpe >= 9.0 -> "Heavy"
                            s.rpe <= 6.5 -> "Warmup"
                            else -> "Normal"
                        }
                    )
                }
            } else {
                val baseW = selectedExercise.defaultWeightKg
                listOf(
                    ExerciseSetRow(1, (baseW * 0.65).coerceAtLeast(0.0), selectedExercise.defaultReps + 2, "Warmup"),
                    ExerciseSetRow(2, baseW, selectedExercise.defaultReps, "Normal"),
                    ExerciseSetRow(3, (baseW * 1.1).coerceAtLeast(0.0), (selectedExercise.defaultReps - 2).coerceAtLeast(4), "Heavy")
                )
            }

            WorkoutSetTableCard(
                exerciseName = selectedExercise.name,
                muscles = "${selectedExercise.primaryMuscle.label.split(" ").first()}$secondaryText",
                sets = tableRows,
                onAddSet = { openDialogForExercise(selectedExercise) }
            )
        }

        // Active Session Creation Card
        if (isLoggingNewSession) {
            item {
                PulseCard(borderGlow = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Workout Session",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Animated Circular Rest Timer Ring
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (restSecondsRemaining > 0) {
                                val progress by animateFloatAsState(
                                    targetValue = restSecondsRemaining.toFloat() / restTotalSeconds.coerceAtLeast(1),
                                    animationSpec = tween(450),
                                    label = "restRing"
                                )
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = extra.calorieRing.copy(alpha = 0.14f),
                                    modifier = Modifier.clickable { isRestTimerRunning = !isRestTimerRunning }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Canvas(modifier = Modifier.size(16.dp)) {
                                            drawArc(
                                                color = extra.calorieRing.copy(alpha = 0.25f),
                                                startAngle = -90f,
                                                sweepAngle = 360f,
                                                useCenter = false,
                                                style = Stroke(width = 3f, cap = StrokeCap.Round)
                                            )
                                            drawArc(
                                                color = extra.calorieRing,
                                                startAngle = -90f,
                                                sweepAngle = 360f * progress,
                                                useCenter = false,
                                                style = Stroke(width = 3f, cap = StrokeCap.Round)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${restSecondsRemaining}s",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = extra.calorieRing
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable {
                                        restSecondsRemaining += 15
                                        restTotalSeconds = maxOf(restTotalSeconds, restSecondsRemaining)
                                    }
                                ) {
                                    Text(
                                        text = "+15s",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            } else {
                                listOf(60, 90).forEach { sec ->
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            restTotalSeconds = sec
                                            restSecondsRemaining = sec
                                            isRestTimerRunning = true
                                        }
                                    ) {
                                        Text(
                                            text = "${sec}s Rest",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

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
                                    Column(modifier = Modifier.weight(1f)) {
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
                            onClick = { openDialogForExercise(selectedExercise) },
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
                                    isRestTimerRunning = false
                                    restSecondsRemaining = 0
                                    showCelebrationBanner = true
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

        // Weekly Training Load Hero Banner with animated progress counters
        item {
            val totalTonnage = (uiState.workoutSessions.sumOf { it.totalVolumeKg } / 1000.0).toFloat()
            val totalKcal = uiState.workoutSessions.sumOf { it.kcalBurned }.toInt()
            val top1Rm = (uiState.workoutSessions
                .flatMap { it.exercises }
                .maxOfOrNull { it.maxEstimated1Rm } ?: 87.0).toInt()

            val animTonnage by animateFloatAsState(totalTonnage, tween(600), label = "animTonnage")
            val animKcal by animateIntAsState(totalKcal, tween(600), label = "animKcal")
            val animTop1Rm by animateIntAsState(top1Rm, tween(600), label = "anim1Rm")

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
                        value = "${String.format("%.1f", animTonnage)}t",
                        color = MaterialTheme.colorScheme.primary
                    )
                    MetricBadge(
                        label = "Burned",
                        value = "$animKcal kcal",
                        color = extra.calorieRing
                    )
                    MetricBadge(
                        label = "Top 1RM",
                        value = "$animTop1Rm kg",
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

                session.exercises.forEach { ex ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .clickable {
                                selectedMuscleFilter = ex.muscleGroup
                                ExerciseCatalog.findByName(ex.exerciseName)?.let { selectedExercise = it }
                            }
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
        var set1Weight by remember(exerciseName) { mutableStateOf(selectedExercise.defaultWeightKg.toInt().toString()) }
        var set1Reps by remember(exerciseName) { mutableStateOf(selectedExercise.defaultReps.toString()) }
        var set2Weight by remember(exerciseName) { mutableStateOf((selectedExercise.defaultWeightKg * 1.05).toInt().toString()) }
        var set2Reps by remember(exerciseName) { mutableStateOf((selectedExercise.defaultReps - 2).coerceAtLeast(4).toString()) }
        var set3Weight by remember(exerciseName) { mutableStateOf((selectedExercise.defaultWeightKg * 1.1).toInt().toString()) }
        var set3Reps by remember(exerciseName) { mutableStateOf((selectedExercise.defaultReps - 4).coerceAtLeast(4).toString()) }

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
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(MuscleGroup.entries) { m ->
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
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(EquipmentType.entries) { eq ->
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
                        if (r1 > 0) sets.add(ExerciseSet(setNumber = 1, weightKg = w1, reps = r1, estimated1RmKg = OneRepMaxCalculator.calculate(w1, r1)))

                        val w2 = set2Weight.toDoubleOrNull() ?: 0.0
                        val r2 = set2Reps.toIntOrNull() ?: 0
                        if (r2 > 0) sets.add(ExerciseSet(setNumber = 2, weightKg = w2, reps = r2, estimated1RmKg = OneRepMaxCalculator.calculate(w2, r2)))

                        val w3 = set3Weight.toDoubleOrNull() ?: 0.0
                        val r3 = set3Reps.toIntOrNull() ?: 0
                        if (r3 > 0) sets.add(ExerciseSet(setNumber = 3, weightKg = w3, reps = r3, estimated1RmKg = OneRepMaxCalculator.calculate(w3, r3)))

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

@Composable
private fun MuscleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) Color(0xFFD4FB44) else Color(0xFF1E1F24),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
