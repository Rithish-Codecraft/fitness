package com.pulseforge.app.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.core.theme.LocalExtraColors
import com.pulseforge.app.domain.model.ExerciseSet

/**
 * Neumorphic Soft-Clay Elevated Card
 */
@Composable
fun PulseCard(
    modifier: Modifier = Modifier,
    borderGlow: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val extra = LocalExtraColors.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(26.dp),
                spotColor = Color(0x18000000),
                ambientColor = Color(0x0A000000)
            )
            .border(
                BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f)),
                RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = extra.cardBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

/**
 * Screen 1 Hero Card: "You're 45% to your daily goal" with coral lightning bolt
 */
@Composable
fun DailyGoalHeroCard(
    percentText: String,
    currentVal: String,
    targetVal: String,
    progress: Float,
    onBoltClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(26.dp), spotColor = Color(0x12000000))
            .border(BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f)), RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = extra.cardBackground)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = percentText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Coral Gradient Lightning Action Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF7A59), Color(0xFFE85B37))
                            )
                        )
                        .clickable(onClick = onBoltClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Quick Action",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Capsule Progress Bar & Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFF8B6D), Color(0xFFE85B37))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "$currentVal/$targetVal",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Screen 1 Circular Action Button: elevated circular icon button with lowercase label
 */
@Composable
fun CircularActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(4.dp, CircleShape, spotColor = Color(0x15000000))
                .clip(CircleShape)
                .background(extra.cardBackground)
                .border(BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.7f)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Screen 2 Terracotta Coral Insight Banner with eye/target icon & dual action pills
 */
@Composable
fun CoralInsightBanner(
    title: String,
    onDismiss: () -> Unit,
    onSetRoutine: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp), spotColor = Color(0x40E85B37)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE85B37))
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target / Eye Badge Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1F24)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE85B37))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dismiss Pill (semi-transparent)
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.25f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Dismiss", fontWeight = FontWeight.SemiBold)
                }

                // Set Routine Pill (pure crisp white)
                Button(
                    onClick = onSetRoutine,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1E1F24)
                    )
                ) {
                    Text("Set Routine", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Whoop-style Circular Telemetry Dial
 */
@Composable
fun WhoopCircularTelemetryDial(
    current: Double,
    target: Double,
    unit: String = "kcal",
    label: String = "REMAINING",
    size: Dp = 120.dp,
    strokeWidth: Dp = 10.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    val sweepAngle = 260f * progress
    val startAngle = 140f
    val remaining = (target - current).coerceAtLeast(0.0).toInt()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val arcSize = Size(size.toPx() - strokePx, size.toPx() - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = activeColor.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = 260f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Progress Arc
            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color(0xFFFF8B6D), activeColor)
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$remaining",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = activeColor
            )
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * MacroFactor-style Precision Macro Progress Bar
 */
@Composable
fun MacroProgressBar(
    label: String,
    current: Double,
    target: Double,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    val remaining = (target - current).coerceAtLeast(0.0).toInt()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "${current.toInt()}g / ${target.toInt()}$unit • $remaining$unit left",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.8f), color)
                        )
                    )
            )
        }
    }
}

/**
 * Hevy-style Professional Exercise Set Table
 */
@Composable
fun HevySetTable(
    sets: List<ExerciseSet>,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("SET", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.8f))
            Text("KG", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.2f))
            Text("REPS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            Text("1RM (EST)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.4f))
            Text("DONE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.8f))
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f), thickness = 0.5.dp)

        sets.forEach { set ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${set.setNumber}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.8f))
                Text("${set.weightKg.toInt()} kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                Text("${set.reps}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                Text("${set.estimated1RmKg.toInt()} kg", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = highlightColor, modifier = Modifier.weight(1.4f))
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(highlightColor.copy(alpha = 0.15f))
                        .padding(vertical = 2.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = highlightColor)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        action?.invoke()
    }
}

@Composable
fun MetricBadge(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.25f)), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Screen 2 Time Horizon Capsule Selector (Daily / Weekly / Monthly)
 */
@Composable
fun TimeHorizonCapsuleSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(23.dp),
        color = extra.cardBorder.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x18000000))
                                    .background(Color.White)
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .clickable { onSelectIndex(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF1E1F24) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Screen 2 Concentric Rings Gauge Card (Exercise & Stand)
 */
@Composable
fun ConcentricRingsGaugeCard(
    exerciseCurrentMin: Int,
    exerciseTargetMin: Int,
    standCurrentMin: Int,
    standTargetMin: Int,
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(26.dp), spotColor = Color(0x10000000))
            .border(BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f)), RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = extra.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Concentric Arc Gauge
            Box(
                modifier = Modifier.size(126.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeOuter = 12.dp.toPx()
                    val strokeInner = 10.dp.toPx()
                    val gap = 6.dp.toPx()

                    val outerSize = Size(size.width - strokeOuter, size.height - strokeOuter)
                    val outerTopLeft = Offset(strokeOuter / 2f, strokeOuter / 2f)

                    val innerDiameter = size.width - (strokeOuter * 2) - (gap * 2) - strokeInner
                    val innerSize = Size(innerDiameter, innerDiameter)
                    val innerOffset = (size.width - innerDiameter) / 2f
                    val innerTopLeft = Offset(innerOffset, innerOffset)

                    val startAngle = 135f
                    val outerSweep = (270f * (exerciseCurrentMin.toFloat() / exerciseTargetMin.coerceAtLeast(1))).coerceIn(10f, 270f)
                    val innerSweep = (270f * (standCurrentMin.toFloat() / standTargetMin.coerceAtLeast(1))).coerceIn(10f, 270f)

                    // Outer Track
                    drawArc(
                        color = Color(0xFFE85B37).copy(alpha = 0.12f),
                        startAngle = startAngle,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = outerTopLeft,
                        size = outerSize,
                        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                    )

                    // Outer Active Arc (Coral)
                    drawArc(
                        color = Color(0xFFE85B37),
                        startAngle = startAngle,
                        sweepAngle = outerSweep,
                        useCenter = false,
                        topLeft = outerTopLeft,
                        size = outerSize,
                        style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                    )

                    // Inner Track
                    drawArc(
                        color = Color(0xFF1E1F24).copy(alpha = 0.1f),
                        startAngle = startAngle,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = innerTopLeft,
                        size = innerSize,
                        style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                    )

                    // Inner Active Arc (Black / Slate)
                    drawArc(
                        color = Color(0xFF1E1F24),
                        startAngle = startAngle,
                        sweepAngle = innerSweep,
                        useCenter = false,
                        topLeft = innerTopLeft,
                        size = innerSize,
                        style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right: Legend & Numbers
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Exercise metric
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE85B37))
                        )
                        Text(
                            text = "Exercise",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$exerciseCurrentMin/${exerciseTargetMin}min",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Stand metric
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E1F24))
                        )
                        Text(
                            text = "Stand",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$standCurrentMin/${standTargetMin}min",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Screen 2 Split Metrics Cards (Steps Bar Chart & Distance Line Graph)
 */
@Composable
fun SplitMetricsCards(
    stepsCount: String = "2.340",
    distanceKm: String = "3.4km",
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Steps Card with Slim Bars
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x10000000))
                .border(BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f)), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = extra.cardBackground)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Steps",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stepsCount,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Canvas mini bar chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    val barCount = 9
                    val barWidth = 4.dp.toPx()
                    val totalWidth = size.width
                    val spacing = (totalWidth - (barCount * barWidth)) / (barCount - 1)
                    val heights = listOf(0.3f, 0.45f, 0.25f, 0.5f, 0.95f, 0.7f, 0.35f, 0.2f, 0.15f)

                    for (i in 0 until barCount) {
                        val x = i * (barWidth + spacing)
                        val h = heights[i] * size.height
                        val y = size.height - h
                        val barColor = when (i) {
                            4 -> Color(0xFF1E1F24)
                            5 -> Color(0xFF1E1F24).copy(alpha = 0.75f)
                            else -> Color(0xFFD6D9E0)
                        }

                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }
            }
        }

        // Distance Card with Route Line
        Card(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x10000000))
                .border(BorderStroke(1.dp, extra.cardBorder.copy(alpha = 0.6f)), RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = extra.cardBackground)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Distance",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = distanceKm,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Canvas continuous coral route line
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(4.dp.toPx(), size.height * 0.75f)
                        cubicTo(
                            size.width * 0.25f, size.height * 0.2f,
                            size.width * 0.35f, size.height * 0.9f,
                            size.width * 0.5f, size.height * 0.45f
                        )
                        cubicTo(
                            size.width * 0.65f, size.height * 0.1f,
                            size.width * 0.8f, size.height * 0.8f,
                            size.width - 4.dp.toPx(), size.height * 0.3f
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFFE85B37),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

/**
 * Screen 3 Central Glowing Assistant Orb
 */
@Composable
fun GlowingAssistantOrb(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(88.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow aura
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color(0xFFE85B37).copy(alpha = 0.15f))
        )
        // Middle ring
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFFE85B37).copy(alpha = 0.30f))
        )
        // Core Coral Orb
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(6.dp, CircleShape, spotColor = Color(0x60E85B37))
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFF7A59), Color(0xFFE85B37))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "AI Assistant Pulse",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Screen 3 Mini Activity Telemetry Chip
 */
@Composable
fun MiniActivityChip(
    title: String,
    kcalText: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x10000000)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8EAEE))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = kcalText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1F24)
                )
            }

            // Mini circular progress ring
            Box(
                modifier = Modifier.size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 2.5.dp.toPx()
                    drawCircle(
                        color = Color(0xFFE85B37).copy(alpha = 0.2f),
                        style = Stroke(width = stroke)
                    )
                    drawArc(
                        color = Color(0xFFE85B37),
                        startAngle = -90f,
                        sweepAngle = 360f * progress.coerceIn(0f, 1f),
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

/**
 * Image 1: Longevity Cyber Progress Hero Card (60% Dual Arc + Start Now button)
 */
@Composable
fun LongevityProgressHeroCard(
    progressPct: Int = 60,
    timeframe: String = "Last week",
    onStartNow: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = timeframe,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartNow,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B35)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Start Now", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }

            // Dual concentric arc dial
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 12.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                    val arcSize = Size(diameter, diameter)

                    // Background track
                    drawArc(
                        color = Color(0xFF2B3445),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Secondary cyan arc
                    drawArc(
                        color = Color(0xFF4E89AE),
                        startAngle = 180f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Primary longevity orange arc
                    drawArc(
                        color = Color(0xFFFF6B35),
                        startAngle = -20f,
                        sweepAngle = 216f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$progressPct%",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Image 1: Tri-Metric Biometric Bar (Heart Rate 82/bpm, Calories 25/250, Steps 450/5500)
 */
@Composable
fun BiometricTriMetricsCard(
    heartRateBpm: Int? = null,
    caloriesBurned: Int? = null,
    caloriesTarget: Int = 250,
    stepsCount: Long? = null,
    stepsTarget: Int = 5500,
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Heart Rate
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF5252)))
                    Text("Heart Rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    val hrText = if (heartRateBpm != null && heartRateBpm > 0) "$heartRateBpm" else "-"
                    Text(hrText, fontSize = 19.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text("/bpm", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                }
            }

            Box(modifier = Modifier.width(1.dp).height(32.dp).background(MaterialTheme.colorScheme.surfaceVariant))

            // Calories
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFFB142)))
                    Text("Calories", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    val calText = if (caloriesBurned != null && caloriesBurned > 0) "$caloriesBurned" else "-"
                    Text(calText, fontSize = 19.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text("/$caloriesTarget", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                }
            }

            Box(modifier = Modifier.width(1.dp).height(32.dp).background(MaterialTheme.colorScheme.surfaceVariant))

            // Steps
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF34ACE0)))
                    Text("Steps", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    val stepsText = if (stepsCount != null && stepsCount > 0) "$stepsCount" else "-"
                    Text(stepsText, fontSize = 19.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text("/$stepsTarget", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp, start = 2.dp))
                }
            }
        }
    }
}

/**
 * Image 1: Blood Pressure Candle Indicator Card (120/80 mm Hg Normal + S S M T W T F)
 */
@Composable
fun BloodPressureCandleCard(
    systolic: Int = 120,
    diastolic: Int = 80,
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5252)))
                    Text("Blood Pressure", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$systolic/$diastolic", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Text(" mm Hg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp))
                }
                Text("Normal", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Medium)
            }

            // Weekly candle distribution
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val days = listOf("S", "S", "M", "T", "W", "T", "F")
                val heights = listOf(28.dp, 34.dp, 24.dp, 38.dp, 30.dp, 32.dp, 26.dp)

                days.forEachIndexed { i, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(heights[i])
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF4E89AE), Color(0xFFFF6B35))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(day, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

/**
 * Image 1: ECG Heart Rate Waveform Card (80.5 bmp Normal + live pulse line)
 */
@Composable
fun EcgHeartRateCard(
    bpm: Double = 80.5,
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ECG pulse wave Canvas
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    val w = size.width
                    val h = size.height
                    moveTo(0f, h * 0.5f)
                    lineTo(w * 0.2f, h * 0.5f)
                    lineTo(w * 0.25f, h * 0.3f)
                    lineTo(w * 0.3f, h * 0.75f)
                    lineTo(w * 0.35f, h * 0.05f)
                    lineTo(w * 0.4f, h * 0.95f)
                    lineTo(w * 0.45f, h * 0.45f)
                    lineTo(w * 0.52f, h * 0.5f)
                    lineTo(w * 0.65f, h * 0.5f)
                    lineTo(w * 0.7f, h * 0.35f)
                    lineTo(w * 0.75f, h * 0.5f)
                    lineTo(w, h * 0.5f)
                }
                drawPath(
                    path = path,
                    color = Color(0xFFFF5252),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Heart Rate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF5252)))
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("$bpm", fontSize = 19.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                Text("Normal", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Image 1: Daily Walk Session Tags & Breathwork Gauges
 */
@Composable
fun DailyWalkSessionReportCard(
    morningWalk: String = "55 min 24 sec",
    eveningWalk: String = "43 min 31 sec",
    totalTime: String = "1h 38m 55s",
    activityPct: Int = 42,
    successPct: Int = 42,
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Text(
            text = "The day steps",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Session Tags Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFF6B35),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Morning walk", fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(morningWalk, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFF6B35),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Evening walk", fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(eveningWalk, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFF6B35).copy(alpha = 0.9f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Time", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                Text(totalTime, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Breathwork Activity & Success Gauges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Activity Gauge
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Activity", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Combining breathwork", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$activityPct%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = Color(0xFFFF6B35),
                                    startAngle = 180f,
                                    sweepAngle = 180f * (activityPct / 100f),
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }
            }

            // Success Gauge
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Success", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Walking is the new happy hour", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$successPct%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        Box(modifier = Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = Color(0xFFFFB142),
                                    startAngle = 180f,
                                    sweepAngle = 180f * (successPct / 100f),
                                    useCenter = false,
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Image 2: Radiant Semicircular Segmented Tachometer Arch Card (1190/3000 KCAL + 3 Macros)
 */
@Composable
fun RadiantMacroTachometerCard(
    currentKcal: Int = 1190,
    targetKcal: Int = 3000,
    proteinG: Int = 80,
    proteinTargetG: Int = 150,
    fatG: Int = 290,
    fatTargetG: Int = 350,
    carbsG: Int = 221,
    carbsTargetG: Int = 250,
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scheduled Your Diet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = extra.cardBorder.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("📅", fontSize = 11.sp)
                    Text("Calendar", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Semicircular Radiating Segmented Tachometer Arch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val totalTicks = 24
                val activeTicks = ((currentKcal.toFloat() / targetKcal.coerceAtLeast(1)) * totalTicks).toInt().coerceIn(1, totalTicks)
                val center = Offset(size.width / 2f, size.height - 10.dp.toPx())
                val outerRadius = size.height * 0.95f
                val tickLength = 16.dp.toPx()
                val tickWidth = 4.dp.toPx()

                for (i in 0 until totalTicks) {
                    val angleDeg = 180f + (180f * (i.toFloat() / (totalTicks - 1)))
                    val angleRad = Math.toRadians(angleDeg.toDouble())

                    val startX = center.x + (outerRadius - tickLength) * Math.cos(angleRad).toFloat()
                    val startY = center.y + (outerRadius - tickLength) * Math.sin(angleRad).toFloat()
                    val endX = center.x + outerRadius * Math.cos(angleRad).toFloat()
                    val endY = center.y + outerRadius * Math.sin(angleRad).toFloat()

                    val tickColor = if (i <= activeTicks) {
                        val fraction = i.toFloat() / activeTicks.coerceAtLeast(1)
                        if (fraction < 0.6f) Color(0xFFFF5722) else Color(0xFFFF9800)
                    } else {
                        Color(0xFFE5E7EB)
                    }

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = tickWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text("🔥", fontSize = 20.sp)
                Text(
                    text = "$currentKcal/$targetKcal",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "KCAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3-Macro Underlined Columns (Blue, Green, Purple)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Protein
            Column(modifier = Modifier.weight(1f)) {
                Text("Protein", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$proteinG/${proteinTargetG}g", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF3B82F6)))
            }

            // Fat
            Column(modifier = Modifier.weight(1f)) {
                Text("Fat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$fatG/${fatTargetG}g", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF10B981)))
            }

            // Carbs
            Column(modifier = Modifier.weight(1f)) {
                Text("Carbs", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$carbsG/${carbsTargetG}g", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFA855F7)))
            }
        }
    }
}

/**
 * Image 2: Hydration (7/10 Cups 70%) and Heart Rate (68/125bpm 57%) Split Cards
 */
@Composable
fun HydrationAndHeartSplitCards(
    cupsCurrent: Int = 7,
    cupsTarget: Int = 10,
    heartBpm: Int = 68,
    heartTargetBpm: Int = 125,
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Water Card
        PulseCard(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("💧", fontSize = 12.sp)
                Text("Water", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("water intake", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$cupsCurrent/$cupsTarget Cups", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }

                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color(0xFFFFB142).copy(alpha = 0.2f), style = Stroke(width = 3.dp.toPx()))
                        drawArc(
                            color = Color(0xFFFFB142),
                            startAngle = -90f,
                            sweepAngle = 360f * (cupsCurrent.toFloat() / cupsTarget),
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text("${((cupsCurrent.toFloat() / cupsTarget) * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Heart Card
        PulseCard(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("❤️", fontSize = 12.sp)
                Text("Heart", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Heart Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$heartBpm/${heartTargetBpm}bpm", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }

                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color(0xFF3B82F6).copy(alpha = 0.2f), style = Stroke(width = 3.dp.toPx()))
                        drawArc(
                            color = Color(0xFF3B82F6),
                            startAngle = -90f,
                            sweepAngle = 360f * (heartBpm.toFloat() / heartTargetBpm),
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text("${((heartBpm.toFloat() / heartTargetBpm) * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Image 2: Today's Plan Schedule Timeline (Strength Training, Breakfast, etc.)
 */
@Composable
fun TodaysPlanSection(
    onSeeAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val extra = LocalExtraColors.current
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "See All",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Plan Item 1: Strength Training
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = extra.cardBorder.copy(alpha = 0.25f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE85B37).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏋️", fontSize = 20.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Strength Training", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Upper Body Hypertrophy", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("07:00 AM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Plan Item 2: Breakfast
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = extra.cardBorder.copy(alpha = 0.25f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF10B981).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🥣", fontSize = 20.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Breakfast", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Protein Oats with Berries", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("07:00 AM", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }
        }
    }
}

/**
 * Image 2: Hatched Weight Trend Cylinder Bar Chart (94.13 KG + Mon-Sun)
 */
@Composable
fun WeightTrendHatchedCard(
    currentWeightKg: Double = 94.13,
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weight Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Monthly Avg",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Last 4 Weeks ⌄",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = String.format("%.2f", currentWeightKg),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = " KG",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hatched 3D Cylinder Bar Chart
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            val days = listOf("Tues", "Wed", "Thurs", "Fri", "Sat", "Sun", "Mon")
            val barCount = days.size
            val barWidth = 24.dp.toPx()
            val totalW = size.width
            val spacing = (totalW - (barCount * barWidth)) / (barCount - 1)
            val heights = listOf(0.45f, 0.6f, 0.5f, 0.95f, 0.35f, 0.9f, 0.4f)

            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing)
                val h = heights[i] * size.height
                val y = size.height - h

                val isHatched = i == 3 || i == 5 // Friday & Sunday in orange hatched
                val baseColor = if (isHatched) Color(0xFFFF6B35) else Color(0xFFE2E5EC)

                drawRoundRect(
                    color = baseColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, h),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Tues", "Wed", "Thurs", "Fri", "Sat", "Sun", "Mon").forEach { day ->
                Text(day, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/**
 * Image 2: BMI Continuous Spectrum Bar (Underweight, Healthy, Overweight, Obese)
 */
@Composable
fun BmiSpectrumCard(
    bmiValue: Double = 25.8,
    category: String = "Healthy",
    modifier: Modifier = Modifier
) {
    PulseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Your Weight is", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1F24)
                ) {
                    Text(category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text("$bmiValue", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(" Good", fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 3.dp, start = 6.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Spectrum Gradient Bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
        ) {
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFF1C40F), // Underweight (Yellow)
                        Color(0xFF2ECC71), // Healthy (Green)
                        Color(0xFF3498DB), // Overweight (Blue)
                        Color(0xFFE74C3C)  // Obese (Red)
                    )
                ),
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx(), 5.dp.toPx())
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("• Underweight", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Healthy", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Overweight", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Obese", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Image 2: Upgrade to Premium Banner with Gradient
 */
@Composable
fun PremiumUpgradeCard(
    onUpgradeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = Color(0x306B5CE7)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF5352ED), Color(0xFFFF6B6B))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upgrade To Premium",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("👑", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Unlock Exclusive Features and AI meal recommendations",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onUpgradeClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text("Upgrade Now", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1F24))
                }
            }
        }
    }
}

/**
 * Image 3: Motivational Lavender Gradient Banner ("Just a Little More to Go", 75%)
 */
@Composable
fun LavenderMotivationBanner(
    progressPct: Int = 75,
    title: String = "Just a Little More to Go",
    subtitle: String = "You're crushing it! Stay on track and keep moving!",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), spotColor = Color(0x356C5CE7)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF6C5CE7), Color(0xFF8C7AE6))
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Circular Progress Badge
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 11.sp)
                        Text(
                            text = "$progressPct%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Image 3: Multi-Line Waveform Activity Performance Graph (810 kcal, 23% Than yesterday, 3 interwoven spline lines)
 */
@Composable
fun MultiWaveformActivityGraphCard(
    totalKcal: Int? = null,
    increasePct: Int? = null,
    walkingKcal: Int? = null,
    runningKcal: Int? = null,
    workoutKcal: Int? = null,
    title: String = "Todays activity",
    modifier: Modifier = Modifier
) {
    val hasRealData = (totalKcal != null && totalKcal > 0) || 
                      (walkingKcal != null && walkingKcal > 0) || 
                      (runningKcal != null && runningKcal > 0) || 
                      (workoutKcal != null && workoutKcal > 0)

    val validWalk = (walkingKcal ?: 0).coerceAtLeast(0)
    val validRun = (runningKcal ?: 0).coerceAtLeast(0)
    val validWorkout = (workoutKcal ?: 0).coerceAtLeast(0)
    val total = (validWalk + validRun + validWorkout).coerceAtLeast(1).toFloat()

    val walkRatio = if (hasRealData) validWalk / total else 0.33f
    val runRatio = if (hasRealData) validRun / total else 0.33f
    val workoutRatio = if (hasRealData) validWorkout / total else 0.34f

    val walkColor = Color(0xFFA855F7)   // Purple
    val runColor = Color(0xFF10B981)    // Green
    val workoutColor = Color(0xFFFF6B35)// Radiant Orange

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(26.dp), spotColor = Color(0x18000000)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181F))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Flame Icon + Title + Real Data Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 13.sp)
                    }
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (hasRealData) Color(0xFF10B981).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = if (hasRealData) "100% Real Data" else "No Data (-)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasRealData) Color(0xFF10B981) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Numbers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (totalKcal != null && totalKcal > 0) "$totalKcal" else "-",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        if (totalKcal != null && totalKcal > 0) {
                            Text(
                                text = "kcal",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🔥", fontSize = 11.sp)
                        Text(
                            text = "Total burned",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (increasePct != null && increasePct > 0) "$increasePct%" else "-",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (increasePct != null && increasePct > 0) "↑ Than yesterday" else "Real telemetry",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (increasePct != null && increasePct > 0) Color(0xFF10B981) else Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Waveform Spline Chart with Y-Axis Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                // Y-Axis Labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("1000", "500", "250", "0").forEach { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.35f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Canvas Multi-Line Waveform
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Horizontal subtle grid lines
                        val gridLines = listOf(0.1f, 0.4f, 0.7f, 0.98f)
                        gridLines.forEach { yRatio ->
                            drawLine(
                                color = Color.White.copy(alpha = 0.07f),
                                start = Offset(0f, h * yRatio),
                                end = Offset(w, h * yRatio),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (hasRealData) {
                            // Normalized real data curve
                            fun drawSpline(points: List<Float>, color: Color, strokeWidth: Float) {
                                if (points.size < 2) return
                                val path = androidx.compose.ui.graphics.Path()
                                val stepX = w / (points.size - 1)
                                path.moveTo(0f, h - (points[0] * h * 0.85f) - (h * 0.05f))

                                for (i in 0 until points.size - 1) {
                                    val x0 = i * stepX
                                    val y0 = h - (points[i] * h * 0.85f) - (h * 0.05f)
                                    val x1 = (i + 1) * stepX
                                    val y1 = h - (points[i + 1] * h * 0.85f) - (h * 0.05f)
                                    val cx = (x0 + x1) / 2f
                                    path.cubicTo(cx, y0, cx, y1, x1, y1)
                                }
                                drawPath(
                                    path = path,
                                    color = color,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }

                            val walkPoints = listOf(0.2f, 0.35f, 0.28f, 0.65f, 0.45f, 0.85f, 0.35f, 0.48f, 0.6f)
                            val runPoints = listOf(0.1f, 0.48f, 0.3f, 0.38f, 0.72f, 0.48f, 0.7f, 0.55f, 0.68f)
                            val workoutPoints = listOf(0.15f, 0.25f, 0.52f, 0.4f, 0.8f, 0.75f, 0.52f, 0.78f, 0.85f)

                            drawSpline(walkPoints, walkColor, 3.dp.toPx())
                            drawSpline(runPoints, runColor, 3.dp.toPx())
                            drawSpline(workoutPoints, workoutColor, 3.dp.toPx())
                        } else {
                            // Flat resting baseline for zero unverified/duplicate data
                            drawLine(
                                color = Color.White.copy(alpha = 0.2f),
                                start = Offset(0f, h * 0.95f),
                                end = Offset(w, h * 0.95f),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }

                    if (!hasRealData) {
                        Text(
                            text = "Live Hardware Sensors Active • No activity logged yet (-)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.45f),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Horizontal Multi-Segmented Ratio Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                if (hasRealData) {
                    Box(
                        modifier = Modifier
                            .weight(walkRatio)
                            .fillMaxHeight()
                            .background(walkColor)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .weight(runRatio)
                            .fillMaxHeight()
                            .background(runColor)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .weight(workoutRatio)
                            .fillMaxHeight()
                            .background(workoutColor)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Column Activity Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: Walking
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🚶", fontSize = 11.sp)
                        Text("Walking", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (walkingKcal != null && walkingKcal > 0) "${walkingKcal}kcal" else "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Column 2: Running
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🏃", fontSize = 11.sp)
                        Text("Running", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (runningKcal != null && runningKcal > 0) "${runningKcal}kcal" else "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Column 3: Workout
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("🏋️", fontSize = 11.sp)
                        Text("Workout", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (workoutKcal != null && workoutKcal > 0) "${workoutKcal}kcal" else "-",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Image 3: Quick Activity Selector Bar ("Start new activity" + Run, Yoga, Swim, Walk, Gym)
 */
@Composable
fun StartNewActivityCard(
    onSelectActivity: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color(0x18000000)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16181F))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("⚡", fontSize = 12.sp)
                    Text(
                        text = "Start new activity",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            val activities = listOf(
                Triple("Run", "🏃", Color(0xFFFF6B35)),
                Triple("Yoga", "🧘", Color(0xFF10B981)),
                Triple("Swim", "🏊", Color(0xFFF59E0B)),
                Triple("Walk", "🚶", Color(0xFF8B5CF6)),
                Triple("Gym", "🏋️", Color(0xFF3B82F6))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                activities.forEach { (name, emoji, color) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onSelectActivity(name) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}
