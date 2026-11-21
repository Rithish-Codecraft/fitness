package com.pulseforge.app.presentation.workout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.EquipmentType
import com.pulseforge.app.domain.model.ExerciseAnimationType
import com.pulseforge.app.domain.model.ExerciseDefinition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Live biomechanical exercise animation showing proper movement mechanics, equipment path,
 * and active muscle contraction for every exercise in [ExerciseCatalog].
 */
@Composable
fun ExerciseAnimationCanvas(
    exercise: ExerciseDefinition,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var isPlaying by remember { mutableStateOf(true) }
    var speedMultiplier by remember { mutableFloatStateOf(1f) }
    var cycleTime by remember(exercise.id) { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, speedMultiplier, exercise.id) {
        if (!isPlaying) return@LaunchedEffect
        var lastNanos = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastNanos != 0L) {
                    val dt = (now - lastNanos) / 1_000_000_000f
                    cycleTime = (cycleTime + dt * speedMultiplier * 0.45f) % 1f
                }
                lastNanos = now
            }
        }
    }

    // Smooth 0 -> 1 -> 0 contraction wave
    val contraction = ((1f - cos(cycleTime * 2f * PI.toFloat())) * 0.5f).coerceIn(0f, 1f)
    val phaseText = when {
        contraction > 0.85f -> "PEAK CONTRACTION"
        cycleTime < 0.5f -> "CONCENTRIC • DRIVE"
        else -> "ECCENTRIC • CONTROL"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF14161C))
            .border(1.dp, Color(0xFF282C37), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIVE FORM ANIMATION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
                Text(
                    text = exercise.animationType.movementLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(primaryColor.copy(alpha = 0.16f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = phaseText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0E1015))
        ) {
            drawGridBackdrop()
            drawExerciseScene(
                type = exercise.animationType,
                equipment = exercise.equipment,
                t = contraction,
                accentColor = primaryColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Playback controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF222530))
                    .clickable { isPlaying = !isPlaying }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause animation" else "Play animation",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isPlaying) "Pause" else "Play",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0.5f to "0.5x", 1f to "1x", 1.5f to "1.5x").forEach { (spd, label) ->
                    val selected = speedMultiplier == spd
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) primaryColor else Color(0xFF222530))
                            .clickable { speedMultiplier = spd }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.Black else Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGridBackdrop() {
    val step = 28f
    val gridColor = Color.White.copy(alpha = 0.04f)
    var x = step
    while (x < size.width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
        x += step
    }
    var y = step
    while (y < size.height) {
        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += step
    }
    // Floor line
    drawLine(
        color = Color.White.copy(alpha = 0.16f),
        start = Offset(size.width * 0.08f, size.height * 0.88f),
        end = Offset(size.width * 0.92f, size.height * 0.88f),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawExerciseScene(
    type: ExerciseAnimationType,
    equipment: EquipmentType,
    t: Float,
    accentColor: Color
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val floorY = h * 0.88f

    val bodyColor = Color(0xFFD6DAE6)
    val limbColor = Color(0xFF9EA6BA)
    val equipColor = Color(0xFF64748B)
    val plateColor = Color(0xFF38BDF8)

    fun limb(a: Offset, b: Offset, width: Float = 10f, color: Color = limbColor) {
        drawLine(color, a, b, strokeWidth = width, cap = StrokeCap.Round)
    }

    fun muscleHighlight(a: Offset, b: Offset, width: Float = 15f) {
        drawLine(
            color = accentColor.copy(alpha = 0.35f + 0.45f * t),
            start = a,
            end = b,
            strokeWidth = width + 6f * t,
            cap = StrokeCap.Round
        )
        drawLine(
            color = accentColor,
            start = a,
            end = b,
            strokeWidth = width * 0.7f,
            cap = StrokeCap.Round
        )
    }

    fun joint(p: Offset, r: Float = 5.5f) {
        drawCircle(Color(0xFF14161C), r + 1.5f, p)
        drawCircle(bodyColor, r, p)
    }

    fun head(p: Offset, r: Float = 13f) {
        drawCircle(bodyColor, r, p)
    }

    fun barbell(center: Offset, widthPx: Float = 86f) {
        val left = Offset(center.x - widthPx * 0.5f, center.y)
        val right = Offset(center.x + widthPx * 0.5f, center.y)
        drawLine(equipColor, left, right, strokeWidth = 5f, cap = StrokeCap.Round)
        drawRoundRect(plateColor, Offset(left.x - 4f, left.y - 14f), Size(8f, 28f))
        drawRoundRect(plateColor, Offset(right.x - 4f, right.y - 14f), Size(8f, 28f))
    }

    fun dumbbell(p: Offset) {
        drawLine(equipColor, Offset(p.x - 10f, p.y), Offset(p.x + 10f, p.y), strokeWidth = 4f)
        drawCircle(plateColor, 6.5f, Offset(p.x - 10f, p.y))
        drawCircle(plateColor, 6.5f, Offset(p.x + 10f, p.y))
    }

    when (type) {
        ExerciseAnimationType.BENCH_PRESS -> {
            // Side-view incline/flat bench press
            val benchStart = Offset(cx - 75f, floorY - 34f)
            val benchEnd = Offset(cx + 55f, floorY - 58f)
            drawLine(equipColor, benchStart, benchEnd, strokeWidth = 10f, cap = StrokeCap.Round)
            drawLine(equipColor, Offset(cx - 55f, floorY - 38f), Offset(cx - 55f, floorY), strokeWidth = 6f)
            drawLine(equipColor, Offset(cx + 35f, floorY - 54f), Offset(cx + 35f, floorY), strokeWidth = 6f)

            val hip = Offset(cx - 25f, floorY - 46f)
            val knee = Offset(cx - 60f, floorY - 44f)
            val foot = Offset(cx - 65f, floorY - 4f)
            val shoulder = Offset(cx + 32f, floorY - 66f)
            val headPos = Offset(cx + 52f, floorY - 78f)
            val pressDist = 16f + 46f * t
            val hand = Offset(shoulder.x - 4f, shoulder.y - pressDist)
            val elbow = Offset(shoulder.x - 18f * (1f - t * 0.5f), (shoulder.y + hand.y) * 0.5f + 8f * (1f - t))

            limb(hip, knee, 12f)
            limb(knee, foot, 10f)
            limb(hip, shoulder, 15f, bodyColor)
            muscleHighlight(Offset(shoulder.x - 10f, shoulder.y + 4f), shoulder, 16f) // Chest highlight
            limb(shoulder, elbow, 10f)
            limb(elbow, hand, 9f)
            head(headPos)
            joint(shoulder); joint(elbow); joint(hip); joint(knee)
            if (equipment == EquipmentType.DUMBBELL) dumbbell(hand) else barbell(hand, 46f)
        }

        ExerciseAnimationType.CABLE_FLY -> {
            // Front-view standing cable fly
            val hip = Offset(cx, floorY - 68f)
            val shoulderC = Offset(cx, floorY - 122f)
            val lFoot = Offset(cx - 22f, floorY - 4f)
            val rFoot = Offset(cx + 22f, floorY - 4f)
            val lShoulder = Offset(cx - 22f, shoulderC.y)
            val rShoulder = Offset(cx + 22f, shoulderC.y)

            val spread = 56f * (1f - t * 0.78f)
            val lHand = Offset(cx - spread, shoulderC.y + 18f)
            val rHand = Offset(cx + spread, shoulderC.y + 18f)

            // Cable lines
            drawLine(equipColor.copy(alpha = 0.5f), Offset(w * 0.14f, shoulderC.y - 10f), lHand, strokeWidth = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            drawLine(equipColor.copy(alpha = 0.5f), Offset(w * 0.86f, shoulderC.y - 10f), rHand, strokeWidth = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))

            limb(hip, lFoot, 11f); limb(hip, rFoot, 11f)
            limb(hip, shoulderC, 18f, bodyColor)
            muscleHighlight(Offset(cx - 16f, shoulderC.y + 8f), Offset(cx + 16f, shoulderC.y + 8f), 14f)
            limb(lShoulder, lHand, 9f); limb(rShoulder, rHand, 9f)
            head(Offset(cx, shoulderC.y - 22f))
            joint(lShoulder); joint(rShoulder); joint(lHand, 4f); joint(rHand, 4f)
        }

        ExerciseAnimationType.LAT_PULLDOWN -> {
            // Front-view seated lat pulldown
            val seatY = floorY - 42f
            drawLine(equipColor, Offset(cx - 28f, seatY), Offset(cx + 28f, seatY), strokeWidth = 8f, cap = StrokeCap.Round)
            drawLine(equipColor, Offset(cx, seatY), Offset(cx, floorY), strokeWidth = 6f)

            val hip = Offset(cx, seatY - 6f)
            val shoulderC = Offset(cx, seatY - 58f)
            val lShoulder = Offset(cx - 22f, shoulderC.y)
            val rShoulder = Offset(cx + 22f, shoulderC.y)

            val barY = shoulderC.y - 54f + 46f * t
            val lHand = Offset(cx - 42f, barY)
            val rHand = Offset(cx + 42f, barY)
            val lElbow = Offset(cx - 38f - 8f * t, (shoulderC.y + barY) * 0.5f + 12f * t)
            val rElbow = Offset(cx + 38f + 8f * t, (shoulderC.y + barY) * 0.5f + 12f * t)

            // Cable & bar
            drawLine(equipColor, Offset(cx, 8f), Offset(cx, barY), strokeWidth = 2.5f)
            drawLine(plateColor, Offset(lHand.x - 10f, barY), Offset(rHand.x + 10f, barY), strokeWidth = 5f, cap = StrokeCap.Round)

            limb(hip, Offset(cx - 20f, floorY - 4f), 11f)
            limb(hip, Offset(cx + 20f, floorY - 4f), 11f)
            limb(hip, shoulderC, 18f, bodyColor)
            muscleHighlight(Offset(cx - 16f, shoulderC.y + 10f), Offset(cx - 10f, hip.y - 12f), 11f)
            muscleHighlight(Offset(cx + 16f, shoulderC.y + 10f), Offset(cx + 10f, hip.y - 12f), 11f)
            limb(lShoulder, lElbow, 9f); limb(lElbow, lHand, 9f)
            limb(rShoulder, rElbow, 9f); limb(rElbow, rHand, 9f)
            head(Offset(cx, shoulderC.y - 20f))
            joint(lShoulder); joint(rShoulder); joint(lElbow); joint(rElbow)
        }

        ExerciseAnimationType.BARBELL_ROW -> {
            // Side-view bent-over barbell row
            val ankle = Offset(cx - 24f, floorY - 4f)
            val knee = Offset(cx - 16f, floorY - 46f)
            val hip = Offset(cx - 34f, floorY - 84f)
            val shoulder = Offset(cx + 18f, floorY - 106f)
            val headPos = Offset(cx + 34f, floorY - 118f)

            val pullOffset = 34f * t
            val hand = Offset(shoulder.x + 4f, shoulder.y + 52f - pullOffset)
            val elbow = Offset(shoulder.x - 16f * t, (shoulder.y + hand.y) * 0.5f - 10f * t)

            limb(ankle, knee, 11f); limb(knee, hip, 12f)
            limb(hip, shoulder, 15f, bodyColor)
            muscleHighlight(Offset(hip.x + 14f, hip.y - 6f), Offset(shoulder.x - 8f, shoulder.y + 2f), 14f)
            limb(shoulder, elbow, 10f); limb(elbow, hand, 9f)
            head(headPos)
            joint(hip); joint(knee); joint(shoulder); joint(elbow)
            barbell(hand, 44f)
        }

        ExerciseAnimationType.OVERHEAD_PRESS -> {
            // Front-view standing overhead press
            val hip = Offset(cx, floorY - 68f)
            val shoulderC = Offset(cx, floorY - 120f)
            val lShoulder = Offset(cx - 20f, shoulderC.y)
            val rShoulder = Offset(cx + 20f, shoulderC.y)

            val barY = shoulderC.y - 6f - 48f * t
            val lHand = Offset(cx - 28f, barY)
            val rHand = Offset(cx + 28f, barY)
            val lElbow = Offset(cx - 28f, (shoulderC.y + barY) * 0.5f + 10f * (1f - t))
            val rElbow = Offset(cx + 28f, (shoulderC.y + barY) * 0.5f + 10f * (1f - t))

            limb(hip, Offset(cx - 16f, floorY - 4f), 11f)
            limb(hip, Offset(cx + 16f, floorY - 4f), 11f)
            limb(hip, shoulderC, 17f, bodyColor)
            muscleHighlight(lShoulder, Offset(lShoulder.x - 4f, lShoulder.y + 8f), 15f)
            muscleHighlight(rShoulder, Offset(rShoulder.x + 4f, rShoulder.y + 8f), 15f)
            limb(lShoulder, lElbow, 9f); limb(lElbow, lHand, 9f)
            limb(rShoulder, rElbow, 9f); limb(rElbow, rHand, 9f)
            head(Offset(cx, shoulderC.y - 22f))
            barbell(Offset(cx, barY), 96f)
            joint(lShoulder); joint(rShoulder); joint(lElbow); joint(rElbow)
        }

        ExerciseAnimationType.LATERAL_RAISE -> {
            val hip = Offset(cx, floorY - 68f)
            val shoulderC = Offset(cx, floorY - 120f)
            val lShoulder = Offset(cx - 20f, shoulderC.y)
            val rShoulder = Offset(cx + 20f, shoulderC.y)

            val angle = (15f + 72f * t) * (PI.toFloat() / 180f)
            val armLen = 46f
            val lHand = Offset(lShoulder.x - sin(angle) * armLen, lShoulder.y + cos(angle) * armLen)
            val rHand = Offset(rShoulder.x + sin(angle) * armLen, rShoulder.y + cos(angle) * armLen)

            limb(hip, Offset(cx - 14f, floorY - 4f), 11f)
            limb(hip, Offset(cx + 14f, floorY - 4f), 11f)
            limb(hip, shoulderC, 17f, bodyColor)
            muscleHighlight(lShoulder, Offset(lShoulder.x - 6f, lShoulder.y + 8f), 15f)
            muscleHighlight(rShoulder, Offset(rShoulder.x + 6f, rShoulder.y + 8f), 15f)
            limb(lShoulder, lHand, 9f); limb(rShoulder, rHand, 9f)
            head(Offset(cx, shoulderC.y - 22f))
            dumbbell(lHand); dumbbell(rHand)
            joint(lShoulder); joint(rShoulder)
        }

        ExerciseAnimationType.BICEP_CURL -> {
            // Side-view standing bicep curl
            val ankle = Offset(cx - 4f, floorY - 4f)
            val knee = Offset(cx - 2f, floorY - 46f)
            val hip = Offset(cx - 4f, floorY - 84f)
            val shoulder = Offset(cx - 2f, floorY - 130f)
            val elbow = Offset(cx + 2f, floorY - 96f)

            val curlAngle = (15f + 125f * t) * (PI.toFloat() / 180f)
            val forearmLen = 36f
            val hand = Offset(elbow.x + sin(curlAngle) * forearmLen, elbow.y + cos(curlAngle) * forearmLen)

            limb(ankle, knee, 11f); limb(knee, hip, 12f)
            limb(hip, shoulder, 16f, bodyColor)
            limb(shoulder, elbow, 10f)
            muscleHighlight(Offset(shoulder.x + 3f, shoulder.y + 8f), Offset(elbow.x + 3f, elbow.y - 6f), 13f)
            limb(elbow, hand, 9f)
            head(Offset(shoulder.x, shoulder.y - 20f))
            joint(shoulder); joint(elbow); joint(hip); joint(knee)
            dumbbell(hand)
        }

        ExerciseAnimationType.TRICEP_PUSHDOWN -> {
            // Side-view cable tricep pushdown
            drawLine(equipColor, Offset(cx + 48f, 10f), Offset(cx + 48f, floorY), strokeWidth = 6f)
            val ankle = Offset(cx - 12f, floorY - 4f)
            val knee = Offset(cx - 8f, floorY - 46f)
            val hip = Offset(cx - 14f, floorY - 84f)
            val shoulder = Offset(cx - 6f, floorY - 130f)
            val elbow = Offset(cx + 4f, floorY - 98f)

            val extAngle = (95f - 82f * t) * (PI.toFloat() / 180f)
            val hand = Offset(elbow.x + sin(extAngle) * 34f, elbow.y + cos(extAngle) * 34f)
            drawLine(equipColor, Offset(cx + 48f, 16f), hand, strokeWidth = 2.5f)

            limb(ankle, knee, 11f); limb(knee, hip, 12f)
            limb(hip, shoulder, 16f, bodyColor)
            limb(shoulder, elbow, 10f)
            muscleHighlight(Offset(shoulder.x - 4f, shoulder.y + 8f), Offset(elbow.x - 4f, elbow.y - 6f), 13f)
            limb(elbow, hand, 9f)
            head(Offset(shoulder.x, shoulder.y - 20f))
            joint(shoulder); joint(elbow); joint(hip); joint(knee)
        }

        ExerciseAnimationType.SQUAT -> {
            // Side-view barbell back squat
            val ankle = Offset(cx, floorY - 4f)
            val knee = Offset(cx + 12f + 18f * t, floorY - 44f - 6f * (1f - t))
            val hip = Offset(cx - 8f - 28f * t, floorY - 84f + 36f * t)
            val shoulder = Offset(hip.x + 8f + 16f * t, hip.y - 48f + 8f * t)
            val headPos = Offset(shoulder.x + 6f, shoulder.y - 18f)

            limb(ankle, knee, 11f)
            limb(knee, hip, 13f)
            muscleHighlight(Offset(knee.x - 4f, knee.y - 6f), Offset(hip.x + 6f, hip.y + 6f), 15f)
            limb(hip, shoulder, 16f, bodyColor)
            limb(shoulder, Offset(shoulder.x - 6f, shoulder.y + 18f), 8f)
            head(headPos)
            barbell(Offset(shoulder.x - 4f, shoulder.y - 4f), 42f)
            joint(hip); joint(knee); joint(shoulder)
        }

        ExerciseAnimationType.LEG_PRESS -> {
            // Side-view 45° leg press
            val seatBack = Offset(cx - 58f, floorY - 58f)
            val seatBottom = Offset(cx - 24f, floorY - 24f)
            drawLine(equipColor, seatBack, seatBottom, strokeWidth = 10f, cap = StrokeCap.Round)
            drawLine(equipColor, Offset(cx + 18f, floorY - 12f), Offset(cx + 76f, floorY - 96f), strokeWidth = 4f)

            val hip = Offset(cx - 26f, floorY - 34f)
            val shoulder = Offset(cx - 52f, floorY - 62f)
            val push = 38f * t
            val foot = Offset(cx + 28f + push * 0.7f, floorY - 66f - push * 0.7f)
            val knee = Offset(cx + 2f + push * 0.35f, floorY - 68f - push * 0.25f)

            // Sled plate
            drawLine(plateColor, Offset(foot.x - 4f, foot.y - 22f), Offset(foot.x + 12f, foot.y + 22f), strokeWidth = 8f, cap = StrokeCap.Round)

            limb(hip, shoulder, 15f, bodyColor)
            limb(hip, knee, 13f)
            muscleHighlight(hip, knee, 15f)
            limb(knee, foot, 11f)
            head(Offset(shoulder.x - 10f, shoulder.y - 14f))
            joint(hip); joint(knee); joint(shoulder)
        }

        ExerciseAnimationType.ROMANIAN_DEADLIFT -> {
            // Side-view hip hinge RDL
            val ankle = Offset(cx - 8f, floorY - 4f)
            val knee = Offset(cx - 4f, floorY - 46f)
            val hip = Offset(cx - 6f - 22f * t, floorY - 86f + 8f * t)
            val shoulder = Offset(hip.x + 48f * t, hip.y - 46f * (1f - t * 0.72f))
            val hand = Offset(shoulder.x + 4f, shoulder.y + 48f)

            limb(ankle, knee, 11f)
            limb(knee, hip, 12f)
            muscleHighlight(Offset(knee.x - 4f, knee.y), Offset(hip.x - 4f, hip.y + 6f), 14f)
            limb(hip, shoulder, 15f, bodyColor)
            limb(shoulder, hand, 9f)
            head(Offset(shoulder.x + 12f * t, shoulder.y - 18f + 6f * t))
            barbell(hand, 40f)
            joint(hip); joint(knee); joint(shoulder)
        }

        ExerciseAnimationType.HIP_THRUST -> {
            // Side-view barbell hip thrust
            val bench = Offset(cx - 56f, floorY - 42f)
            drawRoundRect(equipColor, Offset(bench.x - 20f, bench.y), Size(32f, 42f))

            val shoulder = Offset(cx - 40f, floorY - 44f)
            val foot = Offset(cx + 48f, floorY - 4f)
            val knee = Offset(cx + 38f, floorY - 48f)
            val hip = Offset(cx - 2f, floorY - 16f - 30f * t)

            limb(foot, knee, 11f)
            limb(knee, hip, 13f)
            limb(hip, shoulder, 15f, bodyColor)
            muscleHighlight(Offset(hip.x - 8f, hip.y + 2f), Offset(hip.x + 8f, hip.y + 2f), 16f)
            head(Offset(shoulder.x - 14f, shoulder.y - 12f))
            barbell(Offset(hip.x, hip.y - 6f), 38f)
            joint(hip); joint(knee); joint(shoulder)
        }

        ExerciseAnimationType.CALF_RAISE -> {
            // Side-view standing calf raise
            val lift = 18f * t
            val ballFoot = Offset(cx + 8f, floorY - 4f)
            val heel = Offset(cx - 12f, floorY - 4f - lift)
            val ankle = Offset(cx - 2f, floorY - 10f - lift)
            val knee = Offset(cx - 2f, floorY - 54f - lift)
            val hip = Offset(cx - 4f, floorY - 94f - lift)
            val shoulder = Offset(cx - 2f, floorY - 138f - lift)

            drawLine(bodyColor, heel, ballFoot, strokeWidth = 8f, cap = StrokeCap.Round)
            limb(ankle, knee, 11f)
            muscleHighlight(Offset(ankle.x - 4f, ankle.y - 14f), Offset(knee.x - 4f, knee.y + 10f), 14f)
            limb(knee, hip, 12f)
            limb(hip, shoulder, 16f, bodyColor)
            head(Offset(shoulder.x, shoulder.y - 20f))
            joint(ankle); joint(knee); joint(hip); joint(shoulder)
        }

        ExerciseAnimationType.CRUNCH_PLANK -> {
            // Side-view mat crunch
            drawLine(equipColor, Offset(cx - 85f, floorY - 2f), Offset(cx + 85f, floorY - 2f), strokeWidth = 6f, cap = StrokeCap.Round)

            val hip = Offset(cx + 4f, floorY - 12f)
            val knee = Offset(cx + 38f, floorY - 46f)
            val foot = Offset(cx + 66f, floorY - 8f)
            val curl = 34f * t
            val shoulder = Offset(cx - 48f + curl * 0.65f, floorY - 16f - curl)
            val headPos = Offset(shoulder.x - 14f + curl * 0.25f, shoulder.y - 10f - curl * 0.2f)

            limb(foot, knee, 11f); limb(knee, hip, 12f)
            limb(hip, shoulder, 15f, bodyColor)
            muscleHighlight(Offset(hip.x - 10f, hip.y - 6f), Offset(shoulder.x + 10f, shoulder.y - 4f), 14f)
            limb(shoulder, Offset(shoulder.x + 14f, shoulder.y - 14f), 8f)
            head(headPos)
            joint(hip); joint(knee); joint(shoulder)
        }
    }
}
