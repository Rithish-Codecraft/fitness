package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.GoalType
import com.pulseforge.app.domain.model.WeeklyProgressReview
import java.time.LocalDate

object AdaptiveCoachEngine {

    fun generateWeeklyReview(
        weekNumber: Int,
        startingWeightKg: Double,
        endingWeightKg: Double,
        avgWeightKg: Double,
        calorieAdherencePct: Double,
        proteinAdherencePct: Double,
        workoutsCompleted: Int,
        workoutsTarget: Int,
        fatigueScore: Int, // 1 to 5
        goalType: GoalType,
        totalTonnage: Double
    ): WeeklyProgressReview {
        val weightDelta = endingWeightKg - startingWeightKg
        val adherenceRating = when {
            calorieAdherencePct >= 90.0 && workoutsCompleted >= workoutsTarget -> "Exceptional (Tier 1)"
            calorieAdherencePct >= 75.0 && workoutsCompleted >= (workoutsTarget - 1) -> "Solid Progress (Tier 2)"
            else -> "Needs Consistency (Tier 3)"
        }

        var calorieAdjustment = 0
        val feedbackSummary: String
        val recommendation: String

        // Algorithmic evaluation
        when (goalType) {
            GoalType.WEIGHT_LOSS -> {
                when {
                    fatigueScore >= 4 -> {
                        calorieAdjustment = +150
                        feedbackSummary = "High neurological/systemic fatigue detected. Elevated cortisol slows lipid oxidation."
                        recommendation = "Prescribe a 4-day 'Diet Break' at maintenance (+150 kcal) to reset leptin and lower fatigue before resuming deficit."
                    }
                    weightDelta >= -0.1 && calorieAdherencePct >= 85.0 -> {
                        calorieAdjustment = -125
                        feedbackSummary = "Weight plateaued over 7 days despite high caloric adherence (>85%). Metabolic adaptation has lowered TDEE."
                        recommendation = "Nudge daily calorie ceiling down by -125 kcal and increase daily neat step count by 1,200 steps."
                    }
                    weightDelta < -1.2 -> {
                        calorieAdjustment = +100
                        feedbackSummary = "Rate of weight loss (${String.format("%.2f", weightDelta)}kg/wk) exceeds safe boundary (>1.0kg/wk). Risk of lean skeletal muscle loss."
                        recommendation = "Increase daily calories by +100 kcal with an extra 20g carbohydrate portion around your workout."
                    }
                    else -> {
                        calorieAdjustment = 0
                        feedbackSummary = "Weight trajectory is on track with optimal fat oxidation kinetics."
                        recommendation = "Maintain current caloric deficit and progressive overload training split."
                    }
                }
            }

            GoalType.MUSCLE_GAIN, GoalType.WEIGHT_GAIN -> {
                when {
                    fatigueScore >= 4 -> {
                        calorieAdjustment = 0
                        feedbackSummary = "High central nervous fatigue and joint strain reported."
                        recommendation = "Implement a Deload Week: reduce working sets by 40% while preserving load intensity to allow connective tissue adaptation."
                    }
                    weightDelta < 0.1 && calorieAdherencePct >= 80.0 -> {
                        calorieAdjustment = +175
                        feedbackSummary = "Hypertrophic stall detected. Bodyweight has not shifted, indicating current calories match elevated expenditure."
                        recommendation = "Increase daily caloric surplus by +175 kcal (e.g., adding an afternoon protein shake or extra serving of oats)."
                    }
                    weightDelta > 0.6 -> {
                        calorieAdjustment = -100
                        feedbackSummary = "Weight gain pace (${String.format("%.2f", weightDelta)}kg/wk) is faster than maximum protein synthesis ceiling."
                        recommendation = "Scale back surplus by -100 kcal to prioritize lean myofibrillar tissue over adipose accumulation."
                    }
                    else -> {
                        calorieAdjustment = 0
                        feedbackSummary = "Steady hypertrophy pace with high volume progression (${String.format("%.0f", totalTonnage)}kg lifted)."
                        recommendation = "Continue weekly overload on key compound movements."
                    }
                }
            }

            else -> {
                feedbackSummary = "Consistent training and mobility adherence logged."
                recommendation = "Maintain balanced nutritional equilibrium and hydration."
            }
        }

        val now = LocalDate.now()
        val startDate = now.minusDays(7).toString()
        val endDate = now.toString()

        return WeeklyProgressReview(
            weekNumber = weekNumber,
            startDate = startDate,
            endDate = endDate,
            startingWeightKg = startingWeightKg,
            endingWeightKg = endingWeightKg,
            avgWeightKg = avgWeightKg,
            calorieAdherencePct = calorieAdherencePct,
            proteinAdherencePct = proteinAdherencePct,
            totalVolumeTonnage = totalTonnage,
            workoutsCompleted = workoutsCompleted,
            workoutsTarget = workoutsTarget,
            fatigueScore = fatigueScore,
            adherenceRating = adherenceRating,
            aiFeedbackSummary = feedbackSummary,
            aiCalorieAdjustment = calorieAdjustment,
            aiRecommendation = recommendation
        )
    }
}

