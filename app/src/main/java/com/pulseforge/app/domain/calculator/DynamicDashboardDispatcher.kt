package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.GoalType
import java.time.LocalTime

enum class DashboardWidgetId {
    NUTRITION_RINGS,
    FOOD_CAMERA_SCANNER,
    ACTIVE_WORKOUT_CARD,
    RECOMMENDED_SHAKE,
    MUSCLE_VOLUME_HEATMAP,
    STRENGTH_PROGRESSION_GRAPH,
    HYDRATION_QUICK_ADD,
    AI_COACH_WEEKLY_INSIGHT
}

data class PrioritizedWidget(
    val id: DashboardWidgetId,
    val score: Int
)

object DynamicDashboardDispatcher {

    fun computeWidgetOrder(
        goalType: GoalType,
        currentTime: LocalTime = LocalTime.now(),
        isWorkoutInProgress: Boolean = false
    ): List<DashboardWidgetId> {
        val scores = mutableMapOf<DashboardWidgetId, Int>()

        // 1. Time of Day Weighting
        val hour = currentTime.hour
        when {
            // Morning: 05:00 - 11:59
            hour in 5..11 -> {
                scores[DashboardWidgetId.NUTRITION_RINGS] = 90
                scores[DashboardWidgetId.HYDRATION_QUICK_ADD] = 85
                scores[DashboardWidgetId.FOOD_CAMERA_SCANNER] = 80
                scores[DashboardWidgetId.ACTIVE_WORKOUT_CARD] = 70
                scores[DashboardWidgetId.RECOMMENDED_SHAKE] = 60
                scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] = 50
                scores[DashboardWidgetId.STRENGTH_PROGRESSION_GRAPH] = 45
                scores[DashboardWidgetId.AI_COACH_WEEKLY_INSIGHT] = 40
            }
            // Afternoon / Workout Window: 12:00 - 18:59
            hour in 12..18 -> {
                scores[DashboardWidgetId.ACTIVE_WORKOUT_CARD] = if (isWorkoutInProgress) 100 else 85
                scores[DashboardWidgetId.RECOMMENDED_SHAKE] = 80
                scores[DashboardWidgetId.NUTRITION_RINGS] = 75
                scores[DashboardWidgetId.FOOD_CAMERA_SCANNER] = 70
                scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] = 65
                scores[DashboardWidgetId.STRENGTH_PROGRESSION_GRAPH] = 60
                scores[DashboardWidgetId.HYDRATION_QUICK_ADD] = 55
                scores[DashboardWidgetId.AI_COACH_WEEKLY_INSIGHT] = 40
            }
            // Evening / Night: 19:00 - 04:59
            else -> {
                scores[DashboardWidgetId.NUTRITION_RINGS] = 90
                scores[DashboardWidgetId.AI_COACH_WEEKLY_INSIGHT] = 85
                scores[DashboardWidgetId.RECOMMENDED_SHAKE] = 80
                scores[DashboardWidgetId.FOOD_CAMERA_SCANNER] = 70
                scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] = 65
                scores[DashboardWidgetId.STRENGTH_PROGRESSION_GRAPH] = 60
                scores[DashboardWidgetId.HYDRATION_QUICK_ADD] = 50
                scores[DashboardWidgetId.ACTIVE_WORKOUT_CARD] = 45
            }
        }

        // 2. Goal-Based Boost
        when (goalType) {
            GoalType.MUSCLE_GAIN, GoalType.WEIGHT_GAIN -> {
                scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] = (scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] ?: 0) + 25
                scores[DashboardWidgetId.STRENGTH_PROGRESSION_GRAPH] = (scores[DashboardWidgetId.STRENGTH_PROGRESSION_GRAPH] ?: 0) + 25
                scores[DashboardWidgetId.RECOMMENDED_SHAKE] = (scores[DashboardWidgetId.RECOMMENDED_SHAKE] ?: 0) + 20
            }
            GoalType.WEIGHT_LOSS -> {
                scores[DashboardWidgetId.NUTRITION_RINGS] = (scores[DashboardWidgetId.NUTRITION_RINGS] ?: 0) + 30
                scores[DashboardWidgetId.FOOD_CAMERA_SCANNER] = (scores[DashboardWidgetId.FOOD_CAMERA_SCANNER] ?: 0) + 25
                scores[DashboardWidgetId.HYDRATION_QUICK_ADD] = (scores[DashboardWidgetId.HYDRATION_QUICK_ADD] ?: 0) + 15
            }
            GoalType.YOGA, GoalType.GENERAL_FITNESS -> {
                scores[DashboardWidgetId.ACTIVE_WORKOUT_CARD] = (scores[DashboardWidgetId.ACTIVE_WORKOUT_CARD] ?: 0) + 20
                scores[DashboardWidgetId.HYDRATION_QUICK_ADD] = (scores[DashboardWidgetId.HYDRATION_QUICK_ADD] ?: 0) + 20
            }
            GoalType.BODY_RECOMP -> {
                scores[DashboardWidgetId.NUTRITION_RINGS] = (scores[DashboardWidgetId.NUTRITION_RINGS] ?: 0) + 20
                scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] = (scores[DashboardWidgetId.MUSCLE_VOLUME_HEATMAP] ?: 0) + 20
            }
        }

        // Sort descending by score
        return scores.entries
            .sortedByDescending { it.value }
            .map { it.key }
    }
}

