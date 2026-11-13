package com.pulseforge.app.data.repository

import com.google.gson.Gson
import com.pulseforge.app.data.local.PulseForgeDatabase
import com.pulseforge.app.data.local.entity.*
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.domain.repository.PulseForgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class PulseForgeRepositoryImpl(
    private val database: PulseForgeDatabase
) : PulseForgeRepository {

    private val userProfileDao = database.userProfileDao()
    private val userGoalDao = database.userGoalDao()
    private val mealEntryDao = database.mealEntryDao()
    private val workoutDao = database.workoutDao()
    private val bodyMeasurementDao = database.bodyMeasurementDao()
    private val appSettingsDao = database.appSettingsDao()

    // 1. User Profile
    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.let {
                UserProfile(
                    id = it.id,
                    name = it.name,
                    age = it.age,
                    gender = Gender.valueOf(it.gender),
                    heightCm = it.heightCm,
                    currentWeightKg = it.currentWeightKg,
                    activityLevel = ActivityLevel.valueOf(it.activityLevel),
                    isOnboarded = it.isOnboarded,
                    createdAt = it.createdAt
                )
            }
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertProfile(
            UserProfileEntity(
                id = profile.id,
                name = profile.name,
                age = profile.age,
                gender = profile.gender.name,
                heightCm = profile.heightCm,
                currentWeightKg = profile.currentWeightKg,
                activityLevel = profile.activityLevel.name,
                isOnboarded = profile.isOnboarded,
                createdAt = profile.createdAt
            )
        )
    }

    // 2. User Goal
    override fun getUserGoal(): Flow<UserGoal?> {
        return userGoalDao.getUserGoal().map { entity ->
            entity?.let {
                UserGoal(
                    id = it.id,
                    goalType = GoalType.valueOf(it.goalType),
                    targetWeightKg = it.targetWeightKg,
                    targetWeeks = it.targetWeeks,
                    bmrKcal = it.bmrKcal,
                    tdeeKcal = it.tdeeKcal,
                    targetCalories = it.targetCalories,
                    targetProteinG = it.targetProteinG,
                    targetCarbsG = it.targetCarbsG,
                    targetFatG = it.targetFatG,
                    waterTargetMl = it.waterTargetMl,
                    waterConsumedMl = it.waterConsumedMl
                )
            }
        }
    }

    override suspend fun saveUserGoal(goal: UserGoal) {
        userGoalDao.insertGoal(
            UserGoalEntity(
                id = goal.id,
                goalType = goal.goalType.name,
                targetWeightKg = goal.targetWeightKg,
                targetWeeks = goal.targetWeeks,
                bmrKcal = goal.bmrKcal,
                tdeeKcal = goal.tdeeKcal,
                targetCalories = goal.targetCalories,
                targetProteinG = goal.targetProteinG,
                targetCarbsG = goal.targetCarbsG,
                targetFatG = goal.targetFatG,
                waterTargetMl = goal.waterTargetMl,
                waterConsumedMl = goal.waterConsumedMl
            )
        )
    }

    override suspend fun addWater(amountMl: Int) {
        val currentGoal = userGoalDao.getUserGoal().firstOrNull()
        if (currentGoal != null) {
            val updated = currentGoal.copy(
                waterConsumedMl = (currentGoal.waterConsumedMl + amountMl).coerceAtLeast(0)
            )
            userGoalDao.updateGoal(updated)
        }
    }

    // 3. Nutrition
    override fun getMealsForDate(date: String): Flow<List<MealEntry>> {
        return mealEntryDao.getMealsForDate(date).map { list ->
            list.map { entity ->
                MealEntry(
                    id = entity.id,
                    date = entity.date,
                    slot = MealSlot.valueOf(entity.slot),
                    foodName = entity.foodName,
                    quantityGrams = entity.quantityGrams,
                    calories = entity.calories,
                    proteinG = entity.proteinG,
                    carbsG = entity.carbsG,
                    fatG = entity.fatG
                )
            }
        }
    }

    override fun getAllMeals(): Flow<List<MealEntry>> {
        return mealEntryDao.getAllMeals().map { list ->
            list.map { entity ->
                MealEntry(
                    id = entity.id,
                    date = entity.date,
                    slot = MealSlot.valueOf(entity.slot),
                    foodName = entity.foodName,
                    quantityGrams = entity.quantityGrams,
                    calories = entity.calories,
                    proteinG = entity.proteinG,
                    carbsG = entity.carbsG,
                    fatG = entity.fatG
                )
            }
        }
    }

    override suspend fun addMeal(meal: MealEntry) {
        mealEntryDao.insertMeal(
            MealEntryEntity(
                id = meal.id,
                date = meal.date,
                slot = meal.slot.name,
                foodName = meal.foodName,
                quantityGrams = meal.quantityGrams,
                calories = meal.calories,
                proteinG = meal.proteinG,
                carbsG = meal.carbsG,
                fatG = meal.fatG
            )
        )
    }

    override suspend fun deleteMeal(id: String) {
        mealEntryDao.deleteMealById(id)
    }

    // 4. Workouts
    override fun getAllWorkouts(): Flow<List<WorkoutSession>> {
        return workoutDao.getAllWorkouts().map { list ->
            list.map { withSets ->
                WorkoutSession(
                    id = withSets.session.id,
                    date = withSets.session.date,
                    title = withSets.session.title,
                    durationMinutes = withSets.session.durationMinutes,
                    notes = withSets.session.notes,
                    sets = withSets.sets.map { setEntity ->
                        ExerciseSet(
                            id = setEntity.id,
                            sessionId = setEntity.sessionId,
                            exerciseName = setEntity.exerciseName,
                            muscleGroup = setEntity.muscleGroup,
                            setNumber = setEntity.setNumber,
                            weightKg = setEntity.weightKg,
                            reps = setEntity.reps,
                            rpe = setEntity.rpe
                        )
                    },
                    createdAt = withSets.session.createdAt
                )
            }
        }
    }

    override suspend fun saveWorkout(session: WorkoutSession) {
        val sessionEntity = WorkoutSessionEntity(
            id = session.id,
            date = session.date,
            title = session.title,
            durationMinutes = session.durationMinutes,
            notes = session.notes,
            createdAt = session.createdAt
        )
        val setEntities = session.sets.map {
            ExerciseSetEntity(
                id = it.id,
                sessionId = session.id,
                exerciseName = it.exerciseName,
                muscleGroup = it.muscleGroup,
                setNumber = it.setNumber,
                weightKg = it.weightKg,
                reps = it.reps,
                rpe = it.rpe
            )
        }
        workoutDao.insertFullWorkout(sessionEntity, setEntities)
    }

    override suspend fun deleteWorkout(id: String) {
        workoutDao.deleteSessionById(id)
    }

    // 5. Body Measurements
    override fun getAllMeasurements(): Flow<List<BodyMeasurement>> {
        return bodyMeasurementDao.getAllMeasurements().map { list ->
            list.map { entity ->
                BodyMeasurement(
                    id = entity.id,
                    date = entity.date,
                    weightKg = entity.weightKg,
                    waistCm = entity.waistCm,
                    chestCm = entity.chestCm,
                    armsCm = entity.armsCm,
                    notes = entity.notes
                )
            }
        }
    }

    override suspend fun addMeasurement(measurement: BodyMeasurement) {
        bodyMeasurementDao.insertMeasurement(
            BodyMeasurementEntity(
                id = measurement.id,
                date = measurement.date,
                weightKg = measurement.weightKg,
                waistCm = measurement.waistCm,
                chestCm = measurement.chestCm,
                armsCm = measurement.armsCm,
                notes = measurement.notes
            )
        )
    }

    // 6. Settings
    override fun getSettings(): Flow<AppSettings> {
        return appSettingsDao.getSettings().map { entity ->
            if (entity != null) {
                AppSettings(
                    unitSystem = UnitSystem.valueOf(entity.unitSystem),
                    isDarkMode = entity.isDarkMode,
                    remindersEnabled = entity.remindersEnabled,
                    reminderHour = entity.reminderHour
                )
            } else {
                AppSettings()
            }
        }
    }

    override suspend fun saveSettings(settings: AppSettings) {
        appSettingsDao.insertOrUpdate(
            AppSettingsEntity(
                id = "default_settings",
                unitSystem = settings.unitSystem.name,
                isDarkMode = settings.isDarkMode,
                remindersEnabled = settings.remindersEnabled,
                reminderHour = settings.reminderHour
            )
        )
    }

    // 7. Data Export
    override suspend fun exportDataAsJson(): String {
        val profile = getUserProfile().firstOrNull()
        val goal = getUserGoal().firstOrNull()
        val meals = getAllMeals().firstOrNull() ?: emptyList()
        val workouts = getAllWorkouts().firstOrNull() ?: emptyList()
        val measurements = getAllMeasurements().firstOrNull() ?: emptyList()
        val settings = getSettings().firstOrNull() ?: AppSettings()

        val exportMap = mapOf(
            "app" to "PulseForge AI",
            "version" to "1.7.0",
            "exportedAt" to System.currentTimeMillis(),
            "profile" to profile,
            "goal" to goal,
            "meals" to meals,
            "workouts" to workouts,
            "measurements" to measurements,
            "settings" to settings
        )
        return Gson().toJson(exportMap)
    }
}
