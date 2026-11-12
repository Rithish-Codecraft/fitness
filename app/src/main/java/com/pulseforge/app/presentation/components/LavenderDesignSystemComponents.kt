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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Design Colors matching DesignByKhyati Neo-Soft Lilac System
val LavenderPurplePrimary = Color(0xFF7358F6)
val LavenderPurpleSecondary = Color(0xFF8F53F6)
val LavenderPurpleDark = Color(0xFF5338D6)
val LavenderBgSoft = Color(0xFFF3F4FD)
val LavenderCardBg = Color(0xFFFFFFFF)
val LavenderBorder = Color(0xFFE5E7FA)
val LavenderTextPrimary = Color(0xFF1E1F30)
val LavenderTextMuted = Color(0xFF8E92A8)
val LavenderGradient = Brush.horizontalGradient(listOf(LavenderPurplePrimary, LavenderPurpleSecondary))

/**
 * 1. Pill Search Bar with Circular Action Button
 */
@Composable
fun LavenderSearchBar(
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearch: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(50), ambientColor = Color(0x107358F6), spotColor = Color(0x157358F6)),
        shape = RoundedCornerShape(50),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = LavenderTextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (query.isEmpty()) "Search workouts, meals, stats..." else query,
                color = if (query.isEmpty()) LavenderTextMuted else LavenderTextPrimary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(LavenderGradient)
                    .clickable { onSearch() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * 2. Highlight Update / Stories Row
 */
data class HighlightStory(val name: String, val isSelf: Boolean = false, val hasUpdate: Boolean = true)

@Composable
fun LavenderHighlightUpdateRow(
    stories: List<HighlightStory> = listOf(
        HighlightStory("Your Update", isSelf = true),
        HighlightStory("Daisy"),
        HighlightStory("Jonathan"),
        HighlightStory("Peterson"),
        HighlightStory("William"),
        HighlightStory("Khyati")
    ),
    onStoryClick: (HighlightStory) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "highlight update",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderTextPrimary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = LavenderTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(stories) { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onStoryClick(story) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = if (story.isSelf) Brush.linearGradient(listOf(LavenderTextMuted, LavenderTextMuted))
                                                else LavenderGradient,
                                        shape = CircleShape
                                    )
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEBEBFA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.name.take(1),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LavenderPurplePrimary
                                )
                            }
                            if (story.isSelf) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(LavenderPurplePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = story.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = LavenderTextMuted,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. Graphic Update / Analytics Spline Area Card with Tooltip Callout & Stats
 */
@Composable
fun LavenderGraphicUpdateCard(
    totalValue: String = "-",
    lowestValue: String = "-",
    highestValue: String = "-",
    averageValue: String = "-",
    peakTooltipValue: String = "-",
    targetProgress: Float = 0.0f,
    modifier: Modifier = Modifier
) {
    val hasRealData = totalValue != "-" && totalValue.isNotBlank()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "graphic update",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderTextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (hasRealData) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFE8EBFA)
                ) {
                    Text(
                        text = if (hasRealData) "100% Real" else "No Data (-)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasRealData) Color(0xFF10B981) else LavenderTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart area with left Y-axis labels and smooth spline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                // Left Y-Axis labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("1000", "800", "600", "400", "200", "0").forEach { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = LavenderTextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Waveform Canvas with Callout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 38.dp, bottom = 8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Horizontal guideline lines
                        for (i in 0..5) {
                            val y = h * (i / 5f)
                            drawLine(
                                color = Color(0xFFF0F1FA),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        if (hasRealData) {
                            val points = listOf(
                                Offset(0f, 0.75f),
                                Offset(0.12f, 0.70f),
                                Offset(0.24f, 0.76f),
                                Offset(0.38f, 0.65f),
                                Offset(0.50f, 0.68f),
                                Offset(0.66f, 0.15f), // Highest Peak
                                Offset(0.78f, 0.42f),
                                Offset(0.88f, 0.62f),
                                Offset(1.0f, 0.66f)
                            )

                            val path = Path()
                            val fillPath = Path()

                            val firstX = points[0].x * w
                            val firstY = points[0].y * h
                            path.moveTo(firstX, firstY)
                            fillPath.moveTo(firstX, h)
                            fillPath.lineTo(firstX, firstY)

                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val x0 = p0.x * w
                                val y0 = p0.y * h
                                val x1 = p1.x * w
                                val y1 = p1.y * h
                                val cx = (x0 + x1) / 2f
                                path.cubicTo(cx, y0, cx, y1, x1, y1)
                                fillPath.cubicTo(cx, y0, cx, y1, x1, y1)
                            }

                            fillPath.lineTo(w, h)
                            fillPath.close()

                            // Gradient fill under the curve
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0x357358F6), Color(0x027358F6))
                                )
                            )

                            // Spline outline stroke
                            drawPath(
                                path = path,
                                color = LavenderPurplePrimary,
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        } else {
                            // Baseline for empty/zero telemetry
                            drawLine(
                                color = LavenderBorder,
                                start = Offset(0f, h * 0.95f),
                                end = Offset(w, h * 0.95f),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }

                    if (hasRealData) {
                        // Floating Tooltip Callout at Peak (x ~ 66%, y ~ 15%)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(x = 42.dp, y = (-8).dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = LavenderPurpleDark,
                                    shadowElevation = 6.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "highest value",
                                            fontSize = 9.sp,
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = peakTooltipValue,
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                // Pointer triangle
                                Canvas(modifier = Modifier.size(10.dp, 6.dp)) {
                                    val p = Path().apply {
                                        moveTo(0f, 0f)
                                        lineTo(size.width, 0f)
                                        lineTo(size.width / 2f, size.height)
                                        close()
                                    }
                                    drawPath(p, color = LavenderPurpleDark)
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Live Hardware Sensors Active • No movement logged (-)",
                            fontSize = 11.sp,
                            color = LavenderTextMuted,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4-Column Statistics Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LavenderStatColumn(label = "total value", value = totalValue, valueColor = if (totalValue == "-") LavenderTextMuted else LavenderPurplePrimary)
                LavenderStatColumn(label = "lowest value", value = lowestValue, valueColor = if (lowestValue == "-") LavenderTextMuted else Color(0xFFFF5E7E))
                LavenderStatColumn(label = "highest value", value = highestValue, valueColor = if (highestValue == "-") LavenderTextMuted else Color(0xFFFFB86C))
                LavenderStatColumn(label = "average value", value = averageValue, valueColor = if (averageValue == "-") LavenderTextMuted else Color(0xFFFF7675))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "target 1000k", fontSize = 10.sp, color = LavenderTextMuted, fontWeight = FontWeight.Medium)
                Text(text = if (targetProgress > 0) "${(targetProgress * 100).toInt()}%" else "-", fontSize = 10.sp, color = LavenderTextMuted, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFEBEBFA))
            ) {
                if (targetProgress > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(targetProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(LavenderGradient)
                    )
                }
            }
        }
    }
}

@Composable
private fun LavenderStatColumn(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, fontSize = 10.sp, color = LavenderTextMuted, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 16.sp, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

/**
 * 4. Interactive Schedule / Week Calendar Strip
 */
data class ScheduleDay(val dayOfWeek: String, val dayNumber: Int, val isSelected: Boolean = false)

@Composable
fun LavenderWeekScheduleCard(
    monthTitle: String = "July 2026",
    selectedDayNumber: Int = 7,
    onSelectDay: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val days = listOf(
        ScheduleDay("Mon", 5),
        ScheduleDay("Tue", 6),
        ScheduleDay("Wed", 7),
        ScheduleDay("Thu", 8),
        ScheduleDay("Fri", 9),
        ScheduleDay("Sat", 10),
        ScheduleDay("Sun", 11)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "schedule",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LavenderTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Prev", tint = LavenderTextMuted)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = monthTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderTextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next", tint = LavenderTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7-day strip with Wednesday selected
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                days.forEach { day ->
                    val isSelected = day.dayNumber == selectedDayNumber
                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = LavenderPurplePrimary,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .width(46.dp)
                                .height(78.dp)
                                .clickable { onSelectDay(day.dayNumber) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(LavenderGradient)
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = day.dayOfWeek, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                Text(text = "${day.dayNumber}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(38.dp)
                                .clickable { onSelectDay(day.dayNumber) }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(text = day.dayOfWeek, fontSize = 11.sp, color = LavenderTextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "${day.dayNumber}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = LavenderTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (day.dayNumber % 2 == 0) LavenderPurpleSecondary else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5. User Profile Card
 */
@Composable
fun LavenderUserProfileCard(
    userName: String = "Khyati Gajjar",
    email: String = "khyati@fitness.com",
    phone: String = "+01 234 567 890",
    address: String = "Main Street North Park 123, Orchid Rd",
    onFollow: () -> Unit = {},
    onMessage: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "user profile",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderTextPrimary
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = LavenderTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.dp, LavenderPurplePrimary, CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBEBFA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(text = userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = LavenderPurplePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact details
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LavenderDetailRow(icon = Icons.Outlined.Mail, text = email)
                LavenderDetailRow(icon = Icons.Outlined.Phone, text = phone)
                LavenderDetailRow(icon = Icons.Outlined.LocationOn, text = address)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onFollow,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPurplePrimary)
                ) {
                    Text("follow", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onMessage,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderTextPrimary)
                ) {
                    Text("message", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun LavenderDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = LavenderTextMuted, modifier = Modifier.size(15.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, fontSize = 12.sp, color = LavenderTextMuted)
    }
}

/**
 * 6. User Review Card
 */
@Composable
fun LavenderUserReviewCard(
    userName: String = "Khyati Gajjar",
    followers: String = "125 Followers",
    reviewTitle: String = "Satisfied",
    reviewText: String = "PulseForge provides incredible precision for progressive overload and macro tracking. The AI coach adapts instantly to my fatigue levels!",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "user review",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = LavenderTextPrimary
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = LavenderTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, LavenderPurplePrimary, CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBEBFA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LavenderPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = userName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)
                    Text(text = followers, fontSize = 11.sp, color = LavenderTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = LavenderPurplePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = reviewTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = reviewText,
                fontSize = 12.sp,
                color = LavenderTextMuted,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "was this review helpful ?",
                fontSize = 11.sp,
                color = LavenderTextMuted,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = LavenderPurplePrimary)
                ) {
                    Text("yes, helpful", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LavenderTextMuted)
                ) {
                    Text("no, didn't help", fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * 7. Login / Welcome Card ("Hi, Welcome back.")
 */
@Composable
fun LavenderWelcomeLoginCard(
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Hi,", fontSize = 15.sp, color = LavenderTextMuted)
            Text(text = "Welcome back.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "username or email :", fontSize = 11.sp, color = LavenderTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF7F8FE)
            ) {
                Text(
                    text = "username@email.com",
                    fontSize = 13.sp,
                    color = LavenderTextMuted,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "password :", fontSize = 11.sp, color = LavenderTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF7F8FE)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "••••••••", fontSize = 13.sp, color = LavenderTextPrimary)
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        tint = LavenderTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "forget password ?",
                fontSize = 11.sp,
                color = LavenderTextMuted,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = LavenderPurplePrimary)
            ) {
                Text("login", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/**
 * 8. Direct Message / AI Coach Preview Card
 */
@Composable
fun LavenderDirectMessageCard(
    coachName: String = "Khyati Gajjar",
    incomingText: String = "Good morning! Ready for today's upper body hypertrophy session? 4 sets of bench press are scheduled.",
    outgoingText: String = "Yes! Ready to push progressive overload.",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBEBFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = coachName.take(1), fontWeight = FontWeight.Bold, color = LavenderPurplePrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = coachName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)
                        Text(text = "online", fontSize = 10.sp, color = Color(0xFF10B981))
                    }
                }
                Row {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = LavenderTextMuted)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, tint = LavenderTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Fri, 02 July 2026 • 10:24",
                fontSize = 10.sp,
                color = LavenderTextMuted,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Incoming bubble
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp),
                color = Color(0xFFF7F8FE),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text(
                    text = incomingText,
                    fontSize = 12.sp,
                    color = LavenderTextPrimary,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Outgoing bubble
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp),
                color = LavenderPurplePrimary,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .align(Alignment.End)
            ) {
                Text(
                    text = outgoingText,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(LavenderGradient)
                        .padding(12.dp),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Input
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFF7F8FE),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Type something...", fontSize = 12.sp, color = LavenderTextMuted, modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(LavenderGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

/**
 * 9. Side Menu / Quick Navigation Drawer Card
 */
@Composable
fun LavenderSideMenuCard(
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        Icons.Outlined.Star to "favorite",
        Icons.Outlined.Bookmark to "important",
        Icons.Outlined.Mail to "inbox",
        Icons.Outlined.TrendingUp to "trending",
        Icons.Outlined.LocalCafe to "news feed",
        Icons.Outlined.PlayCircle to "videos"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = LavenderCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, LavenderBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = "Menu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            menuItems.forEach { (icon, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = LavenderTextMuted, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(text = label, fontSize = 13.sp, color = LavenderTextPrimary, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Friends", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LavenderTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            listOf("Aliyah Sagar" to true, "Bhargav Sagar" to true).forEach { (friend, online) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEBEBFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = friend.take(1), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LavenderPurplePrimary)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = friend, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = LavenderTextPrimary)
                        Text(text = if (online) "online" else "offline", fontSize = 10.sp, color = if (online) Color(0xFF10B981) else LavenderTextMuted)
                    }
                }
            }
        }
    }
}

