package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.ExperienceLevel
import com.pulseforge.app.domain.model.FeasibilityReport
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
        goalType: GoalType,
        experienceLevel: ExperienceLevel
    ): FeasibilityReport {
        val safeWeeks = max(1, timelineWeeks)
        val deltaKg = targetWeightKg - currentWeightKg
        val weeklyRate = abs(deltaKg) / safeWeeks

        return when (goalType) {
            GoalType.MUSCLE_GAIN, GoalType.WEIGHT_GAIN -> {
                // Alan Aragon natural muscle gain model:
                // Beginner: 1.0% - 1.5% body weight per month
                // Intermediate: 0.5% - 1.0% body weight per month
                // Advanced: 0.25% - 0.5% body weight per month
                val maxMonthlyRatePercent = when (experienceLevel) {
                    ExperienceLevel.BEGINNER -> 0.015
                    ExperienceLevel.INTERMEDIATE -> 0.010
                    ExperienceLevel.ADVANCED -> 0.005
                }
                val maxWeeklySafeKg = (currentWeightKg * maxMonthlyRatePercent) / 4.33
                val maxSafeChangeKg = maxWeeklySafeKg * safeWeeks
                val isFeasible = deltaKg <= (maxSafeChangeKg * 1.15) // 15% tolerance margin

                val recommendedWeeks = if (isFeasible) safeWeeks else ceil(deltaKg / maxWeeklySafeKg).toInt()
                
                // Lean surplus: +300 to +450 kcal
                val surplus = if (experienceLevel == ExperienceLevel.BEGINNER) 400 else 280
                val dailyKcal = tdeeKcal + surplus

                // Optimal protein: 2.0g per kg of bodyweight
                val proteinG = (currentWeightKg * 2.2).roundToInt()
                val fatG = (currentWeightKg * 0.9).roundToInt()
                val carbsG = ((dailyKcal - (proteinG * 4) - (fatG * 9)) / 4).coerceAtLeast(100)

                FeasibilityReport(
                    isFeasible = isFeasible,
                    targetWeeks = safeWeeks,
                    requestedChangeKg = deltaKg,
                    maxSafeChangeKg = maxSafeChangeKg,
                    recommendedWeeks = recommendedWeeks,
                    weeklyPaceKg = weeklyRate,
                    maxSafeWeeklyPaceKg = maxWeeklySafeKg,
                    statusTitle = if (isFeasible) "Optimal Muscle Gain Horizon" else "Unrealistic Muscle Hypertrophy Pace",
                    message = if (isFeasible) {
                        "Your goal of gaining ${String.format("%.1f", deltaKg)}kg in $safeWeeks weeks (${String.format("%.2f", weeklyRate)}kg/wk) aligns with natural muscle protein synthesis limits."
                    } else {
                        "Human muscle synthesis caps naturally at ~${String.format("%.2f", maxWeeklySafeKg)}kg/week. Gaining ${String.format("%.1f", deltaKg)}kg requires at least $recommendedWeeks weeks; faster gain will result primarily in excess visceral fat."
                    },
                    dailyCalorieTarget = dailyKcal,
                    proteinTargetG = proteinG,
                    carbsTargetG = carbsG,
                    fatTargetG = fatG
                )
            }

            GoalType.WEIGHT_LOSS -> {
                // Safe fat loss rate: 0.5% to 1.0% body weight per week
                val maxSafeWeeklyKg = currentWeightKg * 0.010
                val maxSafeChangeKg = maxSafeWeeklyKg * safeWeeks
                val targetLossKg = abs(deltaKg)
                val isFeasible = weeklyRate <= (maxSafeWeeklyKg * 1.10)

                val recommendedWeeks = if (isFeasible) safeWeeks else ceil(targetLossKg / maxSafeWeeklyKg).toInt()
                
                // 1 kg fat ~ 7700 kcal
                val calculatedDailyDeficit = ((weeklyRate * 7700) / 7.0)
                val safeDeficit = calculatedDailyDeficit.coerceIn(300.0, (tdeeKcal * 0.25))
                val dailyKcal = (tdeeKcal - safeDeficit).roundToInt().coerceAtLeast(1300)

                // High protein during deficit to preserve lean mass: 2.2g/kg
                val proteinG = (currentWeightKg * 2.2).roundToInt()
                val fatG = (currentWeightKg * 0.8).roundToInt()
                val carbsG = ((dailyKcal - (proteinG * 4) - (fatG * 9)) / 4).coerceAtLeast(75)

                FeasibilityReport(
                    isFeasible = isFeasible,
                    targetWeeks = safeWeeks,
                    requestedChangeKg = deltaKg,
                    maxSafeChangeKg = maxSafeChangeKg,
                    recommendedWeeks = recommendedWeeks,
                    weeklyPaceKg = weeklyRate,
                    maxSafeWeeklyPaceKg = maxSafeWeeklyKg,
                    statusTitle = if (isFeasible) "Safe & Sustainable Fat Loss Plan" else "Aggressive Crash Diet Risk",
                    message = if (isFeasible) {
                        "Losing ${String.format("%.1f", targetLossKg)}kg over $safeWeeks weeks (${String.format("%.2f", weeklyRate)}kg/wk) protects muscle mass and thyroid regulation."
                    } else {
                        "Targeting ${String.format("%.1f", targetLossKg)}kg in only $safeWeeks weeks (${String.format("%.2f", weeklyRate)}kg/wk) exceeds the 1.0%/week threshold. Recommended timeline is $recommendedWeeks weeks to prevent severe muscle catabolism."
                    },
                    dailyCalorieTarget = dailyKcal,
                    proteinTargetG = proteinG,
                    carbsTargetG = carbsG,
                    fatTargetG = fatG
                )
            }

            GoalType.BODY_RECOMP, GoalType.YOGA, GoalType.GENERAL_FITNESS -> {
                val dailyKcal = when (goalType) {
                    GoalType.BODY_RECOMP -> tdeeKcal - 100
                    GoalType.YOGA -> tdeeKcal
                    GoalType.GENERAL_FITNESS -> tdeeKcal
                    else -> tdeeKcal
                }
                val proteinG = (currentWeightKg * 1.8).roundToInt()
                val fatG = (currentWeightKg * 0.85).roundToInt()
                val carbsG = ((dailyKcal - (proteinG * 4) - (fatG * 9)) / 4).coerceAtLeast(100)

                FeasibilityReport(
                    isFeasible = true,
                    targetWeeks = safeWeeks,
                    requestedChangeKg = deltaKg,
                    maxSafeChangeKg = deltaKg,
                    recommendedWeeks = safeWeeks,
                    weeklyPaceKg = weeklyRate,
                    maxSafeWeeklyPaceKg = 0.5,
                    statusTitle = "Balanced Progression Matrix",
                    message = "Steady recomposition and conditioning protocol calibrated for sustained energy and joint resilience.",
                    dailyCalorieTarget = dailyKcal,
                    proteinTargetG = proteinG,
                    carbsTargetG = carbsG,
                    fatTargetG = fatG
                )
            }
        }
    }
}

