package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.FeasibilityEstimate
import com.pulseforge.app.domain.model.GoalType
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

object GoalFeasibilityEngine {

    /**
     * Evaluates requested weight/muscle target vs timeline feasibility using
     * sports nutrition peer-reviewed boundaries (Alan Aragon / Eric Helms models).
     */
    fun evaluate(
        currentWeightKg: Double,
        targetWeightKg: Double,
        timelineWeeks: Int,
        tdeeKcal: Int,
        goalType: GoalType
    ): FeasibilityEstimate {
        val safeWeeks = max(1, timelineWeeks)
        val deltaKg = targetWeightKg - currentWeightKg
        val weeklyRate = abs(deltaKg) / safeWeeks

        return when (goalType) {
            GoalType.MUSCLE_GAIN -> {
                // Natural muscle gain limits: ~0.25 - 0.5% body weight per week max
                val maxWeeklySafeKg = (currentWeightKg * 0.0035).coerceAtLeast(0.15)
                val maxSafeChangeKg = maxWeeklySafeKg * safeWeeks
                val isSafe = deltaKg <= (maxSafeChangeKg * 1.20)

                val recommendedWeeks = if (isSafe) safeWeeks else ceil(deltaKg / maxWeeklySafeKg).toInt()
                val surplus = 350
                val dailyKcal = tdeeKcal + surplus
                val proteinG = (currentWeightKg * 2.0).roundToInt()
                val fatG = (currentWeightKg * 0.85).roundToInt()
                val carbsG = ((dailyKcal - (proteinG * 4) - (fatG * 9)) / 4).coerceAtLeast(100)

                FeasibilityEstimate(
                    isSafe = isSafe,
                    message = if (isSafe) {
                        "Realistic muscle hypertrophy pace (${String.format("%.2f", weeklyRate)} kg/wk). Safe for lean mass development."
                    } else {
                        "Gaining ${String.format("%.1f", deltaKg)} kg in $safeWeeks weeks is overly aggressive. Recommended timeline is $recommendedWeeks weeks to prevent excessive body fat gain."
                    },
                    recommendedWeeks = recommendedWeeks,
                    suggestedWeeklyRateKg = maxWeeklySafeKg,
                    suggestedCalories = dailyKcal,
                    suggestedProteinG = proteinG,
                    suggestedCarbsG = carbsG,
                    suggestedFatG = fatG
                )
            }

            GoalType.WEIGHT_LOSS -> {
                // Safe fat loss rate: 0.5% to 1.0% body weight per week
                val maxSafeWeeklyKg = (currentWeightKg * 0.010).coerceAtLeast(0.4)
                val targetLossKg = abs(deltaKg)
                val isSafe = weeklyRate <= (maxSafeWeeklyKg * 1.10)

                val recommendedWeeks = if (isSafe) safeWeeks else ceil(targetLossKg / maxSafeWeeklyKg).toInt()
                val dailyDeficit = ((weeklyRate * 7700) / 7.0).coerceIn(300.0, (tdeeKcal * 0.25))
                val dailyKcal = (tdeeKcal - dailyDeficit).roundToInt().coerceAtLeast(1400)
                val proteinG = (currentWeightKg * 2.2).roundToInt()
                val fatG = (currentWeightKg * 0.8).roundToInt()
                val carbsG = ((dailyKcal - (proteinG * 4) - (fatG * 9)) / 4).coerceAtLeast(80)

                FeasibilityEstimate(
                    isSafe = isSafe,
                    message = if (isSafe) {
                        "Sustainable fat loss protocol (${String.format("%.2f", weeklyRate)} kg/wk). Preserves lean muscle mass."
                    } else {
                        "Losing ${String.format("%.1f", targetLossKg)} kg in $safeWeeks weeks (${String.format("%.2f", weeklyRate)} kg/wk) is dangerously fast. Recommended timeline is $recommendedWeeks weeks."
                    },
                    recommendedWeeks = recommendedWeeks,
                    suggestedWeeklyRateKg = maxSafeWeeklyKg,
                    suggestedCalories = dailyKcal,
                    suggestedProteinG = proteinG,
                    suggestedCarbsG = carbsG,
                    suggestedFatG = fatG
                )
            }

            GoalType.MAINTENANCE, GoalType.ENDURANCE, GoalType.FLEXIBILITY -> {
                val dailyKcal = tdeeKcal
                val proteinG = (currentWeightKg * 1.8).roundToInt()
                val fatG = (currentWeightKg * 0.85).roundToInt()
                val carbsG = ((dailyKcal - (proteinG * 4) - (fatG * 9)) / 4).coerceAtLeast(100)

                FeasibilityEstimate(
                    isSafe = true,
                    message = "Balanced maintenance and recovery protocol for sustained energy.",
                    recommendedWeeks = safeWeeks,
                    suggestedWeeklyRateKg = 0.0,
                    suggestedCalories = dailyKcal,
                    suggestedProteinG = proteinG,
                    suggestedCarbsG = carbsG,
                    suggestedFatG = fatG
                )
            }
        }
    }
}
