package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.ActivityLevel
import com.pulseforge.app.domain.model.Gender
import kotlin.math.roundToInt

object TdeeCalculator {
    /**
     * Calculates Basal Metabolic Rate (BMR) using Mifflin-St Jeor formula:
     * Men: BMR = (10 × weight) + (6.25 × height) - (5 × age) + 5
     * Women: BMR = (10 × weight) + (6.25 × height) - (5 × age) - 161
     */
    fun calculateBmr(
        weightKg: Double,
        heightCm: Double,
        age: Int,
        gender: Gender
    ): Double {
        val base = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age)
        return if (gender == Gender.MALE) base + 5.0 else base - 161.0
    }

    /**
     * Total Daily Energy Expenditure (TDEE) = BMR × Activity Factor
     */
    fun calculateTdee(
        weightKg: Double,
        heightCm: Double,
        age: Int,
        gender: Gender,
        activityLevel: ActivityLevel
    ): Int {
        val bmr = calculateBmr(weightKg, heightCm, age, gender)
        return (bmr * activityLevel.factor).roundToInt()
    }
}
