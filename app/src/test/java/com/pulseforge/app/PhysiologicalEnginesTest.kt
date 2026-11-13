package com.pulseforge.app

import com.pulseforge.app.domain.calculator.*
import com.pulseforge.app.domain.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalTime

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
    fun testGoalFeasibility_RealisticMuscleGain() {
        // Gaining 2kg in 12 weeks for a beginner: 2 / 12 = 0.166 kg/week -> easily feasible
        val report = GoalFeasibilityEngine.evaluate(
            currentWeightKg = 70.0,
            targetWeightKg = 72.0,
            timelineWeeks = 12,
            tdeeKcal = 2400,
            goalType = GoalType.MUSCLE_GAIN,
            experienceLevel = ExperienceLevel.BEGINNER
        )

        assertTrue(report.isFeasible)
        assertEquals(12, report.recommendedWeeks)
        assertTrue(report.dailyCalorieTarget > 2400) // Surplus added
        assertTrue(report.proteinTargetG >= 140)     // At least 2.0g/kg
    }

    @Test
    fun testGoalFeasibility_UnrealisticCrashDiet() {
        // Attempting to lose 15kg in 4 weeks: 15 / 4 = 3.75 kg/week (Dangerous crash diet!)
        val report = GoalFeasibilityEngine.evaluate(
            currentWeightKg = 80.0,
            targetWeightKg = 65.0,
            timelineWeeks = 4,
            tdeeKcal = 2500,
            goalType = GoalType.WEIGHT_LOSS,
            experienceLevel = ExperienceLevel.INTERMEDIATE
        )

        assertFalse(report.isFeasible)
        assertTrue(report.recommendedWeeks > 4) // Recommends extending to at least ~15-18 weeks
        assertNotNull(report.message)
    }

    @Test
    fun testOneRepMaxCalculation_BrzyckiFormula() {
        // 80kg for 8 reps: 80 / (1.0278 - (0.0278 * 8)) = 80 / (1.0278 - 0.2224) = 80 / 0.8054 = 99.3 kg
        val est1Rm = OneRepMaxCalculator.calculate(80.0, 8)
        assertEquals(99.3, est1Rm, 0.5)

        // 1 rep of 100kg should equal 100kg
        assertEquals(100.0, OneRepMaxCalculator.calculate(100.0, 1), 0.01)
    }

    @Test
    fun testCalorieBurnCalculation() {
        // 75kg athlete lifting heavy barbell compounds for 60 mins with 5000kg tonnage
        val burn = CalorieBurnCalculator.calculateSessionBurn(
            userWeightKg = 75.0,
            durationMinutes = 60,
            equipmentType = EquipmentType.BARBELL,
            totalVolumeKg = 5000.0
        )
        // Base MET = 6.5 * 75 * 1.0 = 487.5 kcal. Tonnage bonus = 5 * 12.5 = 62.5 kcal. Total ~550 kcal
        assertEquals(550.0, burn, 5.0)
    }

    @Test
    fun testDynamicDashboardDispatcher_TimeOfDayPrioritization() {
        // In the morning (08:00), Nutrition and Hydration should be prioritized
        val morningOrder = DynamicDashboardDispatcher.computeWidgetOrder(
            goalType = GoalType.WEIGHT_LOSS,
            currentTime = LocalTime.of(8, 0),
            isWorkoutInProgress = false
        )
        assertEquals(DashboardWidgetId.NUTRITION_RINGS, morningOrder.first())

        // In the afternoon during workout (17:00), Active Workout Card should be elevated
        val workoutOrder = DynamicDashboardDispatcher.computeWidgetOrder(
            goalType = GoalType.MUSCLE_GAIN,
            currentTime = LocalTime.of(17, 0),
            isWorkoutInProgress = true
        )
        assertEquals(DashboardWidgetId.ACTIVE_WORKOUT_CARD, workoutOrder.first())
    }
}

