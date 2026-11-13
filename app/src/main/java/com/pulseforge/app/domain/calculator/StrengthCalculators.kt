package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.EquipmentType
import kotlin.math.exp
import kotlin.math.roundToInt

object OneRepMaxCalculator {
    /**
     * Estimates 1-Repetition Maximum (1RM) in kg.
     * Uses Brzycki equation for reps <= 10, Wathan equation for higher reps.
     */
    fun calculate(weightKg: Double, reps: Int): Double {
        if (reps <= 0 || weightKg <= 0.0) return 0.0
        if (reps == 1) return weightKg

        return if (reps <= 10) {
            // Brzycki formula: 1RM = Weight / (1.0278 - (0.0278 * Reps))
            val divisor = 1.0278 - (0.0278 * reps)
            if (divisor > 0) weightKg / divisor else weightKg
        } else {
            // Wathan formula: 1RM = (100 * Weight) / (48.8 + (53.8 * e^(-0.075 * Reps)))
            val denominator = 48.8 + (53.8 * exp(-0.075 * reps))
            (100.0 * weightKg) / denominator
        }
    }
}

object CalorieBurnCalculator {
    /**
     * Calculates energetic cost (kcal burned) during exercise:
     * Kcal = MET × Bodyweight(kg) × Duration(hours) + (Tonnage × EPOC factor)
     */
    fun calculateSessionBurn(
        userWeightKg: Double,
        durationMinutes: Int,
        equipmentType: EquipmentType,
        totalVolumeKg: Double,
        cardioDistanceKm: Double? = null
    ): Double {
        val durationHours = durationMinutes / 60.0
        val baseMet = when (equipmentType) {
            EquipmentType.BARBELL, EquipmentType.PLATE_LOADED_MACHINE -> 6.5
            EquipmentType.DUMBBELL, EquipmentType.CABLE, EquipmentType.PIN_LOADED_MACHINE -> 5.5
            EquipmentType.BODYWEIGHT -> 5.0
            EquipmentType.CARDIO_MACHINE -> {
                if (cardioDistanceKm != null && durationMinutes > 0) {
                    val speedKmh = cardioDistanceKm / durationHours
                    when {
                        speedKmh > 10.0 -> 10.5
                        speedKmh > 7.0 -> 8.0
                        else -> 5.5
                    }
                } else 7.0
            }
            EquipmentType.YOGA_MAT -> 3.2
        }

        val baseBurn = baseMet * userWeightKg * durationHours
        // Tonnage bonus: lifting heavy resistance consumes extra phosphagen/glycogen & EPOC
        val volumeBonus = (totalVolumeKg / 1000.0) * 12.5

        return (baseBurn + volumeBonus).roundToInt().toDouble()
    }
}

