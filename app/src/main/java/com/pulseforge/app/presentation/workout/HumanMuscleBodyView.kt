package com.pulseforge.app.presentation.workout

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulseforge.app.domain.model.MuscleGroup

/**
 * Interactive anatomical human body muscle map (Front & Back views) with pulsing
 * primary/secondary muscle activation and tap-to-filter muscle selection.
 */
@Composable
fun HumanMuscleBodyView(
    primaryMuscle: MuscleGroup,
    secondaryMuscles: List<MuscleGroup> = emptyList(),
    onMuscleSelected: (MuscleGroup) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = Color(0xFFF59E0B)
    val baseBodyColor = Color(0xFF262A34)
    val baseMuscleColor = Color(0xFF343A47)
    val outlineColor = Color(0xFF4B5365)

    val infiniteTransition = rememberInfiniteTransition(label = "musclePulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

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
            Text(
                text = "ANATOMICAL TARGET MAP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.White.copy(alpha = 0.65f)
            )
            Text(
                text = "Tap any muscle",
                fontSize = 10.sp,
                color = primaryColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // FRONT VIEW
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val nx = offset.x / size.width.toFloat()
                                val ny = offset.y / size.height.toFloat()
                                detectFrontMuscleTap(nx, ny)?.let(onMuscleSelected)
                            }
                        }
                ) {
                    drawFrontHumanBody(
                        primaryMuscle = primaryMuscle,
                        secondaryMuscles = secondaryMuscles,
                        pulse = pulse,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        baseBodyColor = baseBodyColor,
                        baseMuscleColor = baseMuscleColor,
                        outlineColor = outlineColor
                    )
                }
                Text(
                    text = "ANTERIOR (FRONT)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = Color.White.copy(alpha = 0.55f)
                )
            }

            // BACK VIEW
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val nx = offset.x / size.width.toFloat()
                                val ny = offset.y / size.height.toFloat()
                                detectBackMuscleTap(nx, ny)?.let(onMuscleSelected)
                            }
                        }
                ) {
                    drawBackHumanBody(
                        primaryMuscle = primaryMuscle,
                        secondaryMuscles = secondaryMuscles,
                        pulse = pulse,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        baseBodyColor = baseBodyColor,
                        baseMuscleColor = baseMuscleColor,
                        outlineColor = outlineColor
                    )
                }
                Text(
                    text = "POSTERIOR (BACK)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    color = Color.White.copy(alpha = 0.55f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot(
                color = primaryColor,
                label = "Primary: ${primaryMuscle.label.split(" ").first()}"
            )
            if (secondaryMuscles.isNotEmpty()) {
                LegendDot(
                    color = secondaryColor,
                    label = "Synergist: ${secondaryMuscles.joinToString(", ") { it.label.split(" ").first() }}"
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
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
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

private fun detectFrontMuscleTap(nx: Float, ny: Float): MuscleGroup? {
    return when {
        ny in 0.16f..0.28f && (nx < 0.34f || nx > 0.66f) -> MuscleGroup.SHOULDERS
        ny in 0.18f..0.31f && nx in 0.34f..0.66f -> MuscleGroup.CHEST
        ny in 0.28f..0.42f && (nx < 0.31f || nx > 0.69f) -> MuscleGroup.BICEPS
        ny in 0.31f..0.52f && nx in 0.36f..0.64f -> MuscleGroup.CORE_ABS
        ny in 0.52f..0.74f -> MuscleGroup.QUADS
        ny in 0.75f..0.94f -> MuscleGroup.CALVES
        else -> null
    }
}

private fun detectBackMuscleTap(nx: Float, ny: Float): MuscleGroup? {
    return when {
        ny in 0.16f..0.26f && (nx < 0.34f || nx > 0.66f) -> MuscleGroup.SHOULDERS
        ny in 0.18f..0.44f && nx in 0.33f..0.67f -> MuscleGroup.BACK_LATS
        ny in 0.27f..0.42f && (nx < 0.31f || nx > 0.69f) -> MuscleGroup.TRICEPS
        ny in 0.45f..0.58f && nx in 0.33f..0.67f -> MuscleGroup.GLUTES
        ny in 0.58f..0.74f -> MuscleGroup.HAMSTRINGS
        ny in 0.75f..0.94f -> MuscleGroup.CALVES
        else -> null
    }
}

private fun DrawScope.drawMuscleRegion(
    path: Path,
    muscle: MuscleGroup,
    primaryMuscle: MuscleGroup,
    secondaryMuscles: List<MuscleGroup>,
    pulse: Float,
    primaryColor: Color,
    secondaryColor: Color,
    baseMuscleColor: Color,
    outlineColor: Color
) {
    val isPrimary = muscle == primaryMuscle
    val isSecondary = !isPrimary && muscle in secondaryMuscles

    when {
        isPrimary -> {
            // Soft outer glow
            drawPath(
                path = path,
                color = primaryColor.copy(alpha = 0.28f + 0.32f * pulse),
                style = Stroke(width = 7f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Pulsing muscle belly fill
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.72f + 0.28f * pulse),
                        primaryColor.copy(alpha = 0.48f + 0.26f * pulse)
                    )
                ),
                style = Fill
            )
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.35f + 0.3f * pulse),
                style = Stroke(width = 1.6f)
            )
        }
        isSecondary -> {
            drawPath(
                path = path,
                color = secondaryColor.copy(alpha = 0.55f + 0.2f * pulse),
                style = Fill
            )
            drawPath(
                path = path,
                color = secondaryColor.copy(alpha = 0.85f),
                style = Stroke(width = 1.4f)
            )
        }
        else -> {
            drawPath(path = path, color = baseMuscleColor, style = Fill)
            drawPath(path = path, color = outlineColor.copy(alpha = 0.55f), style = Stroke(width = 1.1f))
        }
    }
}

private fun DrawScope.drawFrontHumanBody(
    primaryMuscle: MuscleGroup,
    secondaryMuscles: List<MuscleGroup>,
    pulse: Float,
    primaryColor: Color,
    secondaryColor: Color,
    baseBodyColor: Color,
    baseMuscleColor: Color,
    outlineColor: Color
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f

    // Helper to scale normalized coordinates (centered horizontally around cx)
    fun px(nx: Float) = cx + (nx - 0.5f) * minOf(w, h * 0.62f)
    fun py(ny: Float) = ny * h

    // 1. Head & Neck
    drawOval(
        color = baseBodyColor,
        topLeft = Offset(px(0.415f), py(0.03f)),
        size = androidx.compose.ui.geometry.Size(px(0.585f) - px(0.415f), py(0.135f) - py(0.03f))
    )
    drawOval(
        color = outlineColor,
        topLeft = Offset(px(0.415f), py(0.03f)),
        size = androidx.compose.ui.geometry.Size(px(0.585f) - px(0.415f), py(0.135f) - py(0.03f)),
        style = Stroke(1.2f)
    )
    val neckPath = Path().apply {
        moveTo(px(0.44f), py(0.13f))
        lineTo(px(0.56f), py(0.13f))
        lineTo(px(0.60f), py(0.18f))
        lineTo(px(0.40f), py(0.18f))
        close()
    }
    drawPath(neckPath, baseBodyColor)

    // 2. Neutral Torso & Forearm/Foot Base
    val torsoBase = Path().apply {
        moveTo(px(0.32f), py(0.185f))
        lineTo(px(0.68f), py(0.185f))
        lineTo(px(0.63f), py(0.50f))
        lineTo(px(0.50f), py(0.53f))
        lineTo(px(0.37f), py(0.50f))
        close()
    }
    drawPath(torsoBase, baseBodyColor)
    drawPath(torsoBase, outlineColor.copy(alpha = 0.5f), style = Stroke(1.1f))

    // Forearms & Hands (Left & Right)
    for (side in listOf(-1f, 1f)) {
        val sx = { dx: Float -> px(0.5f + side * dx) }
        val forearm = Path().apply {
            moveTo(sx(0.24f), py(0.39f))
            lineTo(sx(0.305f), py(0.39f))
            lineTo(sx(0.345f), py(0.53f))
            lineTo(sx(0.29f), py(0.54f))
            close()
        }
        drawPath(forearm, baseBodyColor)
        drawPath(forearm, outlineColor.copy(alpha = 0.5f), style = Stroke(1f))

        // Feet
        val foot = Path().apply {
            moveTo(sx(0.06f), py(0.91f))
            lineTo(sx(0.135f), py(0.91f))
            lineTo(sx(0.155f), py(0.97f))
            lineTo(sx(0.055f), py(0.97f))
            close()
        }
        drawPath(foot, baseBodyColor)
    }

    // 3. Bilateral Upper Body Muscles
    for (side in listOf(-1f, 1f)) {
        val sx = { dx: Float -> px(0.5f + side * dx) }

        // SHOULDERS (Anterior/Lateral Deltoid)
        val delt = Path().apply {
            moveTo(sx(0.175f), py(0.18f))
            quadraticBezierTo(sx(0.275f), py(0.18f), sx(0.295f), py(0.255f))
            lineTo(sx(0.24f), py(0.295f))
            quadraticBezierTo(sx(0.19f), py(0.25f), sx(0.175f), py(0.18f))
            close()
        }
        drawMuscleRegion(delt, MuscleGroup.SHOULDERS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // CHEST (Pectoralis Major)
        val pec = Path().apply {
            moveTo(sx(0.015f), py(0.20f))
            lineTo(sx(0.17f), py(0.195f))
            quadraticBezierTo(sx(0.195f), py(0.255f), sx(0.165f), py(0.295f))
            quadraticBezierTo(sx(0.08f), py(0.31f), sx(0.015f), py(0.285f))
            close()
        }
        drawMuscleRegion(pec, MuscleGroup.CHEST, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // BICEPS
        val bicep = Path().apply {
            moveTo(sx(0.235f), py(0.295f))
            lineTo(sx(0.295f), py(0.265f))
            quadraticBezierTo(sx(0.32f), py(0.335f), sx(0.295f), py(0.39f))
            lineTo(sx(0.24f), py(0.39f))
            quadraticBezierTo(sx(0.22f), py(0.34f), sx(0.235f), py(0.295f))
            close()
        }
        drawMuscleRegion(bicep, MuscleGroup.BICEPS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // QUADS
        val quad = Path().apply {
            moveTo(sx(0.025f), py(0.525f))
            lineTo(sx(0.16f), py(0.505f))
            quadraticBezierTo(sx(0.205f), py(0.61f), sx(0.165f), py(0.73f))
            lineTo(sx(0.055f), py(0.73f))
            quadraticBezierTo(sx(0.02f), py(0.62f), sx(0.025f), py(0.525f))
            close()
        }
        drawMuscleRegion(quad, MuscleGroup.QUADS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // CALVES (Lower leg)
        val calf = Path().apply {
            moveTo(sx(0.055f), py(0.745f))
            lineTo(sx(0.155f), py(0.745f))
            quadraticBezierTo(sx(0.175f), py(0.815f), sx(0.13f), py(0.905f))
            lineTo(sx(0.065f), py(0.905f))
            quadraticBezierTo(sx(0.045f), py(0.82f), sx(0.055f), py(0.745f))
            close()
        }
        drawMuscleRegion(calf, MuscleGroup.CALVES, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)
    }

    // 4. CORE & ABS (6-pack + obliques)
    val absPath = Path().apply {
        moveTo(px(0.41f), py(0.31f))
        lineTo(px(0.59f), py(0.31f))
        lineTo(px(0.575f), py(0.495f))
        lineTo(px(0.50f), py(0.525f))
        lineTo(px(0.425f), py(0.495f))
        close()
    }
    drawMuscleRegion(absPath, MuscleGroup.CORE_ABS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

    // Subtle ab segment lines
    val abLineColor = if (primaryMuscle == MuscleGroup.CORE_ABS) Color.White.copy(alpha = 0.45f) else outlineColor.copy(alpha = 0.45f)
    drawLine(abLineColor, Offset(px(0.5f), py(0.32f)), Offset(px(0.5f), py(0.50f)), strokeWidth = 1f)
    for (rowY in listOf(0.365f, 0.415f, 0.46f)) {
        drawLine(abLineColor, Offset(px(0.435f), py(rowY)), Offset(px(0.565f), py(rowY)), strokeWidth = 1f)
    }
}

private fun DrawScope.drawBackHumanBody(
    primaryMuscle: MuscleGroup,
    secondaryMuscles: List<MuscleGroup>,
    pulse: Float,
    primaryColor: Color,
    secondaryColor: Color,
    baseBodyColor: Color,
    baseMuscleColor: Color,
    outlineColor: Color
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f

    fun px(nx: Float) = cx + (nx - 0.5f) * minOf(w, h * 0.62f)
    fun py(ny: Float) = ny * h

    // Head & Neck
    drawOval(
        color = baseBodyColor,
        topLeft = Offset(px(0.415f), py(0.03f)),
        size = androidx.compose.ui.geometry.Size(px(0.585f) - px(0.415f), py(0.135f) - py(0.03f))
    )
    drawOval(
        color = outlineColor,
        topLeft = Offset(px(0.415f), py(0.03f)),
        size = androidx.compose.ui.geometry.Size(px(0.585f) - px(0.415f), py(0.135f) - py(0.03f)),
        style = Stroke(1.2f)
    )
    val neckPath = Path().apply {
        moveTo(px(0.44f), py(0.13f))
        lineTo(px(0.56f), py(0.13f))
        lineTo(px(0.61f), py(0.185f))
        lineTo(px(0.39f), py(0.185f))
        close()
    }
    drawPath(neckPath, baseBodyColor)

    // Neutral Torso Base
    val torsoBase = Path().apply {
        moveTo(px(0.32f), py(0.185f))
        lineTo(px(0.68f), py(0.185f))
        lineTo(px(0.62f), py(0.48f))
        lineTo(px(0.38f), py(0.48f))
        close()
    }
    drawPath(torsoBase, baseBodyColor)
    drawPath(torsoBase, outlineColor.copy(alpha = 0.5f), style = Stroke(1.1f))

    // Forearms & Feet
    for (side in listOf(-1f, 1f)) {
        val sx = { dx: Float -> px(0.5f + side * dx) }
        val forearm = Path().apply {
            moveTo(sx(0.24f), py(0.39f))
            lineTo(sx(0.305f), py(0.39f))
            lineTo(sx(0.345f), py(0.53f))
            lineTo(sx(0.29f), py(0.54f))
            close()
        }
        drawPath(forearm, baseBodyColor)
        drawPath(forearm, outlineColor.copy(alpha = 0.5f), style = Stroke(1f))

        val foot = Path().apply {
            moveTo(sx(0.06f), py(0.91f))
            lineTo(sx(0.135f), py(0.91f))
            lineTo(sx(0.155f), py(0.97f))
            lineTo(sx(0.055f), py(0.97f))
            close()
        }
        drawPath(foot, baseBodyColor)
    }

    // Bilateral Back Muscles
    for (side in listOf(-1f, 1f)) {
        val sx = { dx: Float -> px(0.5f + side * dx) }

        // REAR DELTS (SHOULDERS)
        val rearDelt = Path().apply {
            moveTo(sx(0.175f), py(0.18f))
            quadraticBezierTo(sx(0.275f), py(0.18f), sx(0.295f), py(0.255f))
            lineTo(sx(0.235f), py(0.285f))
            lineTo(sx(0.175f), py(0.245f))
            close()
        }
        drawMuscleRegion(rearDelt, MuscleGroup.SHOULDERS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // LATS & UPPER BACK
        val lat = Path().apply {
            moveTo(sx(0.015f), py(0.195f))
            lineTo(sx(0.17f), py(0.20f))
            quadraticBezierTo(sx(0.20f), py(0.31f), sx(0.135f), py(0.43f))
            lineTo(sx(0.015f), py(0.445f))
            close()
        }
        drawMuscleRegion(lat, MuscleGroup.BACK_LATS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // TRICEPS
        val tricep = Path().apply {
            moveTo(sx(0.235f), py(0.285f))
            lineTo(sx(0.295f), py(0.26f))
            quadraticBezierTo(sx(0.32f), py(0.33f), sx(0.295f), py(0.39f))
            lineTo(sx(0.24f), py(0.39f))
            close()
        }
        drawMuscleRegion(tricep, MuscleGroup.TRICEPS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // GLUTES
        val glute = Path().apply {
            moveTo(sx(0.012f), py(0.455f))
            lineTo(sx(0.145f), py(0.455f))
            quadraticBezierTo(sx(0.185f), py(0.515f), sx(0.155f), py(0.575f))
            quadraticBezierTo(sx(0.08f), py(0.585f), sx(0.012f), py(0.555f))
            close()
        }
        drawMuscleRegion(glute, MuscleGroup.GLUTES, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // HAMSTRINGS
        val ham = Path().apply {
            moveTo(sx(0.025f), py(0.575f))
            lineTo(sx(0.155f), py(0.575f))
            quadraticBezierTo(sx(0.18f), py(0.65f), sx(0.145f), py(0.73f))
            lineTo(sx(0.055f), py(0.73f))
            quadraticBezierTo(sx(0.02f), py(0.65f), sx(0.025f), py(0.575f))
            close()
        }
        drawMuscleRegion(ham, MuscleGroup.HAMSTRINGS, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)

        // CALVES (Gastrocnemius diamond)
        val calf = Path().apply {
            moveTo(sx(0.055f), py(0.745f))
            lineTo(sx(0.145f), py(0.745f))
            quadraticBezierTo(sx(0.175f), py(0.81f), sx(0.125f), py(0.905f))
            lineTo(sx(0.065f), py(0.905f))
            quadraticBezierTo(sx(0.045f), py(0.815f), sx(0.055f), py(0.745f))
            close()
        }
        drawMuscleRegion(calf, MuscleGroup.CALVES, primaryMuscle, secondaryMuscles, pulse, primaryColor, secondaryColor, baseMuscleColor, outlineColor)
    }
}
