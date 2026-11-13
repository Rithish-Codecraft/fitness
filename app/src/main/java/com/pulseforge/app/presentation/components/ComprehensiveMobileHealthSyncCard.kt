package com.pulseforge.app.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.MobileHealthDataHub

/**
 * 7-Category Comprehensive Mobile Health Data Sync Hub
 * 1. Activity & Motion Data (Steps, Distance, Speed, Cadence, Floors, Active/Idle)
 * 2. Exercise & Workouts (Sessions, Laps, Duration, GPS Tracks)
 * 3. Location & Environmental (GPS Coordinates, Altitude, Elevation Gain)
 * 4. Body Metrics & Vitals (Weight, Height, Body Fat %, Lean Mass, Bone Mass, Heart Rate)
 * 5. Sleep & Recovery (Duration, Sleep Stages, Movement Stillness, Readiness)
 * 6. Nutrition & Hydration (Calories, Water, Macro Logs)
 * 7. Cycle & Health Tracking (Cycle Day, Phase, Conception Window, Notes)
 */
@Composable
fun ComprehensiveMobileHealthSyncCard(
    hub: MobileHealthDataHub,
    onSyncAll: () -> Unit,
    onMeasurePulseViaCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val categories = listOf(
        "Activity & Motion",
        "Workouts & GPS",
        "Location & Geo",
        "Body & Vitals",
        "Sleep & Recovery",
        "Nutrition & Water",
        "Cycle & Health"
    )

    // Pulsing green beacon for active hardware telemetry
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val beaconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconAlpha"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF13151A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232733))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Beacon + Title + One-Tap Sync Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = beaconAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Mobile Health Data Hub",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Synced: ${hub.lastSyncTime} • Health Connect ${if (hub.healthConnectConnected) "Active" else "Ready"}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Button(
                    onClick = onSyncAll,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE85B37)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync Now", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Pills (Scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEachIndexed { index, label ->
                    val isSelected = selectedCategoryIndex == index
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { selectedCategoryIndex = index },
                        shape = RoundedCornerShape(50),
                        color = if (isSelected) Color(0xFFE85B37) else Color(0xFF1E212B),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A2E3D))
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Category Panels
            when (selectedCategoryIndex) {
                0 -> {
                    // Category 1: Activity & Motion Data
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Step Count",
                                value = if (hub.stepCount != null && hub.stepCount > 0) "${hub.stepCount}" else "-",
                                unit = "steps",
                                icon = Icons.Default.DirectionsWalk,
                                accentColor = Color(0xFF10B981)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Cadence",
                                value = if (hub.cadenceSpm != null && hub.cadenceSpm > 0) "${hub.cadenceSpm.toInt()}" else "-",
                                unit = "spm",
                                icon = Icons.Default.Speed,
                                accentColor = Color(0xFF38BDF8)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val distKm = if (hub.distanceMeters != null && hub.distanceMeters > 0) {
                                String.format("%.2f", hub.distanceMeters / 1000.0)
                            } else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Distance Traveled",
                                value = distKm,
                                unit = "km",
                                icon = Icons.Default.Straighten,
                                accentColor = Color(0xFFF59E0B)
                            )
                            val speedStr = if (hub.speedKmh != null && hub.speedKmh > 0) {
                                String.format("%.1f", hub.speedKmh)
                            } else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Speed",
                                value = speedStr,
                                unit = "km/h",
                                icon = Icons.Default.ElectricBolt,
                                accentColor = Color(0xFFEC4899)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Floors Climbed",
                                value = if (hub.floorsClimbed != null && hub.floorsClimbed > 0) "${hub.floorsClimbed}" else "-",
                                unit = "floors",
                                icon = Icons.Default.Stairs,
                                accentColor = Color(0xFF8B5CF6)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Active vs Idle",
                                value = if (hub.activeMinutes != null) "${hub.activeMinutes}m" else "-",
                                unit = if (hub.idleMinutes != null) "/ ${hub.idleMinutes}m idle" else "activity",
                                icon = Icons.Default.Timelapse,
                                accentColor = Color(0xFF06B6D4)
                            )
                        }
                    }
                }
                1 -> {
                    // Category 2: Exercise & Workouts
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Workout Routine",
                                value = hub.workoutType,
                                unit = "session",
                                icon = Icons.Default.FitnessCenter,
                                accentColor = Color(0xFFE85B37)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Duration",
                                value = if (hub.workoutDurationMinutes != null) "${hub.workoutDurationMinutes} min" else "-",
                                unit = "active",
                                icon = Icons.Default.Timer,
                                accentColor = Color(0xFF38BDF8)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Laps & Sets",
                                value = if (hub.workoutLaps != null) "${hub.workoutLaps}" else "-",
                                unit = "intervals",
                                icon = Icons.Default.Repeat,
                                accentColor = Color(0xFF10B981)
                            )
                            val calStr = if (hub.workoutCaloriesBurned != null && hub.workoutCaloriesBurned > 0) {
                                "${hub.workoutCaloriesBurned.toInt()}"
                            } else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Calories Burned",
                                value = calStr,
                                unit = "kcal (MET)",
                                icon = Icons.Default.LocalFireDepartment,
                                accentColor = Color(0xFFFF6B35)
                            )
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1B1E26)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Route, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("GPS Workout Track", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                    Text(
                                        text = if (hub.workoutGpsWaypoints.isNotEmpty()) "${hub.workoutGpsWaypoints.size} satellite path waypoints recorded" else "-",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Category 3: Location & Environmental Data
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Live GPS Coordinates",
                                value = hub.currentGpsCoordinates,
                                unit = "WGS-84",
                                icon = Icons.Default.MyLocation,
                                accentColor = Color(0xFF38BDF8)
                            )
                            val altStr = if (hub.currentAltitudeMeters != null) "${hub.currentAltitudeMeters.toInt()}m" else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Altitude",
                                value = altStr,
                                unit = "above sea level",
                                icon = Icons.Default.Landscape,
                                accentColor = Color(0xFF10B981)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val elevStr = if (hub.elevationGainMeters != null && hub.elevationGainMeters > 0) {
                                String.format("+%.1f m", hub.elevationGainMeters)
                            } else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Elevation Gain",
                                value = elevStr,
                                unit = "hypsometric",
                                icon = Icons.Default.TrendingUp,
                                accentColor = Color(0xFFE85B37)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Outdoor Path Mapping",
                                value = if (hub.workoutGpsWaypoints.isNotEmpty()) "Map Recorded" else "-",
                                unit = "satellite lock",
                                icon = Icons.Default.Map,
                                accentColor = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
                3 -> {
                    // Category 4: Body Metrics & Vitals
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Body Weight",
                                value = if (hub.weightKg != null && hub.weightKg > 0) "${hub.weightKg} kg" else "-",
                                unit = if (hub.heightCm != null) "${hub.heightCm.toInt()} cm height" else "scale",
                                icon = Icons.Default.Scale,
                                accentColor = Color(0xFF10B981)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Body Fat %",
                                value = if (hub.bodyFatPct != null) "${hub.bodyFatPct}%" else "-",
                                unit = "composition",
                                icon = Icons.Default.Percent,
                                accentColor = Color(0xFF38BDF8)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Lean Body Mass",
                                value = if (hub.leanBodyMassKg != null) "${hub.leanBodyMassKg} kg" else "-",
                                unit = "muscle & organs",
                                icon = Icons.Default.Accessibility,
                                accentColor = Color(0xFFE85B37)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Bone Mass",
                                value = if (hub.boneMassKg != null) "${hub.boneMassKg} kg" else "-",
                                unit = "mineral content",
                                icon = Icons.Default.HealthAndSafety,
                                accentColor = Color(0xFF8B5CF6)
                            )
                        }
                        // Heart Rate & PPG Measurement Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1B1E26)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFF5252).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Heart Rate / Vitals", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = if (hub.heartRateBpm != null) "${hub.heartRateBpm}" else if (hub.restingHeartRateBpm != null) "${hub.restingHeartRateBpm}" else "-",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                            Text(" bpm", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 2.dp))
                                        }
                                        Text(text = hub.heartRateMeasurementSource, fontSize = 9.sp, color = Color.White.copy(alpha = 0.4f))
                                    }
                                }

                                Button(
                                    onClick = onMeasurePulseViaCamera,
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF272A36)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Camera PPG", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Category 5: Sleep & Recovery
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val sleepHours = if (hub.sleepDurationHours != null) "${hub.sleepDurationHours} hrs" else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Sleep Duration",
                                value = sleepHours,
                                unit = "nightly stillness",
                                icon = Icons.Default.Bedtime,
                                accentColor = Color(0xFF6366F1)
                            )
                            val readiness = if (hub.recoveryReadinessScore != null) "${hub.recoveryReadinessScore}%" else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Readiness Score",
                                value = readiness,
                                unit = "nervous system",
                                icon = Icons.Default.BatteryChargingFull,
                                accentColor = Color(0xFF10B981)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Sleep Stages",
                                value = hub.sleepStagesSummary,
                                unit = "deep / light / rem",
                                icon = Icons.Default.NightsStay,
                                accentColor = Color(0xFF38BDF8)
                            )
                            val stillness = if (hub.movementStillnessScore != null) "${hub.movementStillnessScore}/100" else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Movement Patterns",
                                value = stillness,
                                unit = "accelerometer",
                                icon = Icons.Default.MotionPhotosOn,
                                accentColor = Color(0xFFF59E0B)
                            )
                        }
                    }
                }
                5 -> {
                    // Category 6: Nutrition & Hydration
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val calStr = if (hub.caloriesConsumed != null && hub.caloriesConsumed > 0) "${hub.caloriesConsumed.toInt()}" else "-"
                            val targetStr = if (hub.calorieTarget != null) " / ${hub.calorieTarget.toInt()} kcal" else ""
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Calorie Intake",
                                value = calStr,
                                unit = targetStr,
                                icon = Icons.Default.Restaurant,
                                accentColor = Color(0xFFE85B37)
                            )
                            val waterStr = if (hub.waterConsumedMl != null && hub.waterConsumedMl > 0) "${hub.waterConsumedMl}" else "-"
                            val waterTargetStr = if (hub.waterTargetMl != null) " / ${hub.waterTargetMl} ml" else ""
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Water Consumption",
                                value = waterStr,
                                unit = waterTargetStr,
                                icon = Icons.Default.WaterDrop,
                                accentColor = Color(0xFF38BDF8)
                            )
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1B1E26)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Logged Meals & Macros", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                    Text(
                                        text = hub.loggedMealsSummary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                6 -> {
                    // Category 7: Cycle & Health Tracking
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val dayStr = if (hub.cycleDay != null) "Day ${hub.cycleDay}" else "-"
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Cycle Day",
                                value = dayStr,
                                unit = "28-day baseline",
                                icon = Icons.Default.CalendarMonth,
                                accentColor = Color(0xFFEC4899)
                            )
                            MetricHubTile(
                                modifier = Modifier.weight(1f),
                                label = "Cycle Phase",
                                value = hub.cyclePhase,
                                unit = "hormonal rhythm",
                                icon = Icons.Default.WbTwilight,
                                accentColor = Color(0xFF8B5CF6)
                            )
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1B1E26)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = Color(0xFFEC4899), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Health & Limitation Notes", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                    Text(
                                        text = hub.healthNotes,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Connected Data Sources Footer Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Sensors, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(13.dp))
                Text(
                    text = "Sources: SensorManager • GPS • Health Connect • Camera PPG • Bluetooth Peripheral",
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }
        }
    }
}

@Composable
private fun MetricHubTile(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1B1E26)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = if (value == "-") Color.White.copy(alpha = 0.35f) else Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = unit,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

