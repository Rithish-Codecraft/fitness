package com.pulseforge.app.presentation.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.presentation.MainUiState
import com.pulseforge.app.presentation.components.*
import kotlin.math.roundToInt

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val timeAgo: String,
    val activities: List<Triple<String, String, Float>>? = null
)

@Composable
fun CoachReviewScreen(
    uiState: MainUiState,
    onGenerateNewReview: (Int) -> Unit
) {
    val extra = LocalExtraColors.current
    var selectedFatigue by remember { mutableStateOf(2f) }
    var showReviewDialog by remember { mutableStateOf(false) }

    var inputPrompt by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                isUser = true,
                text = "How many calories did I burn yesterday?",
                timeAgo = "2 hours ago"
            ),
            ChatMessage(
                isUser = false,
                text = "You burned 420 kcal across 3 activities",
                timeAgo = "2 hours ago",
                activities = listOf(
                    Triple("Running", "120kcal", 0.65f),
                    Triple("Push up", "200kcal", 0.85f),
                    Triple("Sit up", "100kcal", 0.45f)
                )
            ),
            ChatMessage(
                isUser = true,
                text = "Remind me to stretch at 8 PM",
                timeAgo = "1 hours ago"
            )
        )
    }

    fun handleSend(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(isUser = true, text = text, timeAgo = "Just now"))
        inputPrompt = ""

        val response = when {
            text.contains("water", ignoreCase = true) ->
                ChatMessage(isUser = false, text = "Target: 3.5L/day. You've logged 1,500ml so far! Keep hydrating.", timeAgo = "Just now")
            text.contains("workout", ignoreCase = true) ->
                ChatMessage(isUser = false, text = "Ready! Upper Body Hypertrophy session scheduled for today (Chest & Back focus).", timeAgo = "Just now")
            text.contains("sleep", ignoreCase = true) ->
                ChatMessage(isUser = false, text = "Your recovery index is 84%. Aim for 8 hours to maximize myofibrillar protein synthesis.", timeAgo = "Just now")
            else ->
                ChatMessage(isUser = false, text = "Understood. Adjusting your metabolic plan and progressive overload curve accordingly.", timeAgo = "Just now")
        }
        messages.add(response)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chat Stream Items (Matching Screen 3)
        items(messages) { msg ->
            if (msg.isUser) {
                // User Bubble (Right Aligned)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 6.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                        color = Color.White,
                        shadowElevation = 3.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = msg.text,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E1F24)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = msg.timeAgo,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            } else {
                // Coach Response Card (Left Aligned)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1F24)
                            )

                            if (!msg.activities.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    msg.activities.forEach { (name, kcal, prog) ->
                                        MiniActivityChip(
                                            title = name,
                                            kcalText = kcal,
                                            progress = prog
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Screen 3 Central Glowing Assistant Orb
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                GlowingAssistantOrb(onClick = { showReviewDialog = true })
            }
        }

        // Screen 3 Quick Action Suggestion Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Start workout", "Log water", "How did I sleep?").forEach { prompt ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f)),
                        modifier = Modifier.clickable { handleSend(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E1F24),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Screen 3 Capsule Message Input Bar
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = { Text("Type something..", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { handleSend(inputPrompt) })
                    )

                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank()) {
                                handleSend(inputPrompt)
                            } else {
                                handleSend("Review my workout fatigue")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (inputPrompt.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                            contentDescription = "Send",
                            tint = Color(0xFFE85B37)
                        )
                    }
                }
            }
        }

        // Structured Weekly Progress Sheets Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = "Weekly Physiological Reviews",
                subtitle = "Algorithmic autoregulation & volume balance"
            ) {
                Button(
                    onClick = { showReviewDialog = true },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1F24))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFE85B37), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Check-in", fontSize = 12.sp)
                }
            }
        }

        // Current Trajectory Header
        item {
            PulseCard(borderGlow = true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Primary Plan Horizon",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.userProfile.primaryGoal.label} • Week 1 of ${uiState.userProfile.targetTimelineWeeks}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    MetricBadge(
                        label = "Weight",
                        value = "${uiState.userProfile.currentWeightKg} kg",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Structured Progress Sheets List
        items(uiState.progressReviews) { review ->
            PulseCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Structured Check-in: Week ${review.weekNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${review.startDate} to ${review.endDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Badge(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                        Text(
                            text = review.adherenceRating,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Adherence & Volume Metric Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Calorie Compliance", style = MaterialTheme.typography.labelSmall)
                        Text("${review.calorieAdherencePct.toInt()}%", fontWeight = FontWeight.Bold, color = extra.calorieRing)
                    }
                    Column {
                        Text("Protein Hit Rate", style = MaterialTheme.typography.labelSmall)
                        Text("${review.proteinAdherencePct.toInt()}%", fontWeight = FontWeight.Bold, color = extra.proteinColor)
                    }
                    Column {
                        Text("Workouts", style = MaterialTheme.typography.labelSmall)
                        Text("${review.workoutsCompleted}/${review.workoutsTarget}", fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Tonnage", style = MaterialTheme.typography.labelSmall)
                        Text("${(review.totalVolumeTonnage / 1000.0).let { String.format("%.1f", it) }}t", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AI Feedback Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Algorithmic Analysis",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.aiFeedbackSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Prescription: ${review.aiRecommendation}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (review.aiCalorieAdjustment != 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val sign = if (review.aiCalorieAdjustment > 0) "+" else ""
                            Text(
                                text = "Target Recalibration: $sign${review.aiCalorieAdjustment} kcal/day",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = extra.proteinColor
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // New Weekly Check-in Dialog
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Generate Weekly AI Check-in") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "PulseForge will analyze your 7-day caloric adherence, progressive overload tonnage, and bodyweight kinetics.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Subjective Fatigue & Joint Soreness: ${selectedFatigue.toInt()} / 5",
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = selectedFatigue,
                        onValueChange = { selectedFatigue = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Text(
                        text = when (selectedFatigue.roundToInt()) {
                            1 -> "1: Fully fresh, peak recovery"
                            2 -> "2: Normal muscular fatigue, ready to train"
                            3 -> "3: Moderate soreness across trained muscle groups"
                            4 -> "4: High fatigue, heavy joints, sluggish CNS"
                            else -> "5: Severe exhaustion (Triggers deload/diet break)"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onGenerateNewReview(selectedFatigue.roundToInt())
                        showReviewDialog = false
                    }
                ) {
                    Text("Run AI Analysis")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

