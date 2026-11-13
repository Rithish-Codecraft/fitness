package com.pulseforge.app

import com.pulseforge.app.domain.calculator.GoalFeasibilityEngine
import com.pulseforge.app.domain.calculator.TdeeCalculator
import com.pulseforge.app.domain.model.ActivityLevel
import com.pulseforge.app.domain.model.Gender
import com.pulseforge.app.domain.model.GoalType
import org.junit.Assert.*
import org.junit.Test

class PhysiologicalEnginesTest {

    @Test
    fun testTdeeCalculation_MifflinStJeor() {
        // 75kg male, 180cm, 25 years old
        // BMR = (10 * 75) + (6.25 * 180) - (5 * 25) + 5 = 750 + 1125 - 125 + 5 = 1755 kcal
        val bmrMale = TdeeCalculator.calculateBmr(75.0, 180.0, 25, Gender.MALE)
        assertEquals(1755.0, bmrMale, 1.0)

        // Moderate activity (x1.55) -> 1755 * 1.55 = 2720 kcal
        val tdee = TdeeCalculator.calculateTdee(75.0, 180.0, 25, Gender.MALE, ActivityLevel.MODERATE)
        assertEquals(2720, tdee)
    }

    @Test
    fun testTdeeCalculation_Female() {
        // 60kg female, 165cm, 28 years old
        // BMR = (10 * 60) + (6.25 * 165) - (5 * 28) - 161 = 600 + 1031.25 - 140 - 161 = 1330.25 kcal
        val bmrFemale = TdeeCalculator.calculateBmr(60.0, 165.0, 28, Gender.FEMALE)
        assertEquals(1330.25, bmrFemale, 1.0)

        val tdee = TdeeCalculator.calculateTdee(60.0, 165.0, 28, Gender.FEMALE, ActivityLevel.LIGHT)
        assertEquals(1829, tdee)
    }

    @Test
    fun testGoalFeasibility_RealisticMuscleGain() {
        // Gaining 2kg in 12 weeks: ~0.166 kg/week -> easily feasible
        val estimate = GoalFeasibilityEngine.evaluate(
            currentWeightKg = 70.0,
            targetWeightKg = 72.0,
            timelineWeeks = 12,
            tdeeKcal = 2400,
            goalType = GoalType.MUSCLE_GAIN
        )

        assertTrue(estimate.isSafe)
        assertEquals(12, estimate.recommendedWeeks)
        assertTrue(estimate.suggestedCalories > 2400) // Surplus added
        assertTrue(estimate.suggestedProteinG >= 140) // At least 2.0g/kg
    }

    @Test
    fun testGoalFeasibility_UnrealisticCrashDiet() {
        // Attempting to lose 15kg in 4 weeks: 3.75 kg/week (Dangerous crash diet!)
        val estimate = GoalFeasibilityEngine.evaluate(
            currentWeightKg = 80.0,
            targetWeightKg = 65.0,
            timelineWeeks = 4,
            tdeeKcal = 2500,
            goalType = GoalType.WEIGHT_LOSS
        )

        assertFalse(estimate.isSafe)
        assertTrue(estimate.recommendedWeeks > 4) // Recommends extending
        assertNotNull(estimate.message)
    }

    @Test
    fun testGoalFeasibility_Maintenance() {
        val estimate = GoalFeasibilityEngine.evaluate(
            currentWeightKg = 75.0,
            targetWeightKg = 75.0,
            timelineWeeks = 8,
            tdeeKcal = 2400,
            goalType = GoalType.MAINTENANCE
        )

        assertTrue(estimate.isSafe)
        assertEquals(2400, estimate.suggestedCalories)
    }
}
