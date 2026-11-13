package com.pulseforge.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.LifestyleSleepAnalysis

/**
 * Real Android Screen Usage, Nighttime App Addiction & Sleeping Behavior Card.
 * Powered by UsageStatsManager and real device foreground event tracking.
 */
@Composable
fun LifestyleAndSleepCard(
    analysis: LifestyleSleepAnalysis,
    onOpenUsageSettings: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF13151D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B2E3D))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF6C5CE7), Color(0xFF8E44AD)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Sleep & Lifestyle",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "LIFESTYLE & SLEEP BEHAVIOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFFA29BFE)
                        )
                        Text(
                            text = "Real Nighttime Usage & Circadian Rhythm",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF1F2230), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh usage",
                        tint = Color(0xFFA29BFE),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!analysis.hasUsagePermission) {
                // Permission Request Box
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF231C1A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD63031).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = Color(0xFFFF7675),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Android Usage Access Required",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "To track late-night mobile usage, identify sleep-disrupting apps, and measure screen time, enable Usage Access in Android Settings.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                        Button(
                            onClick = onOpenUsageSettings,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE85B37)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Grant Usage Access in Settings",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Nighttime Usage & App Callout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Late Night Mobile Time
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1B1E2B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3245))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NightlightRound,
                                    contentDescription = "Night time",
                                    tint = Color(0xFFFFD166),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "NIGHT USAGE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFFFFD166)
                                )
                            }
                            Text(
                                text = if (analysis.nightScreenTimeMinutes > 0) "${analysis.nightScreenTimeMinutes} min" else "-",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "10 PM – 6 AM window",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Most Used Night App
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1B1E2B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3245))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Smartphone,
                                    contentDescription = "Night App",
                                    tint = Color(0xFFEF5777),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "TOP NIGHT APP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFFEF5777)
                                )
                            }
                            Text(
                                text = analysis.mostUsedNightApp,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = if (analysis.mostUsedNightAppMinutes > 0) "${analysis.mostUsedNightAppMinutes} min before sleep" else "-",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Deduced Sleep Window & Circadian Score
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF181B26),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3245))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "OBJECTIVE SLEEP WINDOW",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFF00CEC9)
                                )
                                Text(
                                    text = "${analysis.estimatedBedtime} → ${analysis.estimatedWakeTime}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF00CEC9).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (analysis.estimatedSleepDurationHours != null) "${analysis.estimatedSleepDurationHours} hrs" else "-",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00CEC9),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Sleep Disruption Feedback
                        if (analysis.sleepDisruptionFeedback.isNotBlank()) {
                            Text(
                                text = analysis.sleepDisruptionFeedback,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Daily Screen Time & Top Apps Breakdown
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Screen Time",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${analysis.dailyScreenTimeMinutes / 60}h ${analysis.dailyScreenTimeMinutes % 60}m",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Top Day Apps Chips
                    if (analysis.topDayApps.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            analysis.topDayApps.take(3).forEach { app ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1E2130),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3245))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = app.appName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${app.totalTimeMinutes}m",
                                            fontSize = 10.sp,
                                            color = Color(0xFFA29BFE)
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

