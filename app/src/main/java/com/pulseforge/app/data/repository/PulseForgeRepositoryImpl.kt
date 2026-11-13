package com.pulseforge.app.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pulseforge.app.data.local.PulseForgeDatabase
import com.pulseforge.app.data.local.entity.*
import com.pulseforge.app.domain.calculator.OneRepMaxCalculator
import com.pulseforge.app.domain.model.*
import com.pulseforge.app.domain.repository.PulseForgeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PulseForgeRepositoryImpl(
    private val database: PulseForgeDatabase
) : PulseForgeRepository {

    private val gson = Gson()
    private val userDao = database.userDao()
    private val nutritionDao = database.nutritionDao()
    private val workoutDao = database.workoutDao()
    private val reviewDao = database.reviewDao()

    override fun getUserProfile(): Flow<UserProfile?> {
        return userDao.getUserProfile().map { entity ->
            entity?.toDomain(gson)
        }
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        userDao.insertOrUpdateProfile(profile.toEntity(gson))
    }

    override suspend fun updateWater(userId: String, consumedMl: Int) {
        userDao.updateWater(userId, consumedMl)
    }

    override suspend fun updateTheme(userId: String, theme: ThemePalette, isDarkMode: Boolean) {
        userDao.updateTheme(userId, theme.name, isDarkMode)
    }

    override fun getDailyNutrition(
        date: String,
        targetCalories: Int,
        targetProteinG: Int,
        targetCarbsG: Int,
        targetFatG: Int
    ): Flow<DailyNutrition> {
        return nutritionDao.getFoodItemsForDate(date).map { entities ->
            val foodItems = entities.map { it.toDomain() }
            val grouped = foodItems.groupBy { it.mealType }
            DailyNutrition(
                date = date,
                targetCalories = targetCalories,
                targetProteinG = targetProteinG,
                targetCarbsG = targetCarbsG,
                targetFatG = targetFatG,
                meals = grouped,
                waterTargetMl = 3000,
                waterConsumedMl = 0
            )
        }
    }

    override suspend fun addFoodItem(item: FoodItem) {
        nutritionDao.insertFoodItem(item.toEntity())
    }

    override suspend fun addFoodItems(items: List<FoodItem>) {
        nutritionDao.insertFoodItems(items.map { it.toEntity() })
    }

    override suspend fun deleteFoodItem(id: String) {
        nutritionDao.deleteFoodItemById(id)
    }

    override fun getAllWorkoutSessions(): Flow<List<WorkoutSession>> {
        return workoutDao.getAllWorkoutSessions().map { sessions ->
            sessions.map { sessionEntity ->
                val exercises = workoutDao.getExercisesForSession(sessionEntity.id).map { exEntity ->
                    val sets = workoutDao.getSetsForExercise(exEntity.id).map { setEntity ->
                        ExerciseSet(
                            id = setEntity.id,
                            setNumber = setEntity.setNumber,
                            weightKg = setEntity.weightKg,
                            reps = setEntity.reps,
                            rpe = setEntity.rpe,
                            isPersonalRecord = setEntity.isPersonalRecord,
                            estimated1RmKg = setEntity.estimated1RmKg
                        )
                    }
                    ExerciseLog(
                        id = exEntity.id,
                        exerciseName = exEntity.exerciseName,
                        muscleGroup = try { MuscleGroup.valueOf(exEntity.muscleGroup) } catch (e: Exception) { MuscleGroup.CHEST },
                        equipmentType = try { EquipmentType.valueOf(exEntity.equipmentType) } catch (e: Exception) { EquipmentType.BARBELL },
                        machineSettings = exEntity.machineSettings,
                        sets = sets,
                        cardioDistanceKm = exEntity.cardioDistanceKm,
                        cardioDurationMinutes = exEntity.cardioDurationMinutes
                    )
                }
                WorkoutSession(
                    id = sessionEntity.id,
                    date = sessionEntity.date,
                    title = sessionEntity.title,
                    durationMinutes = sessionEntity.durationMinutes,
                    kcalBurned = sessionEntity.kcalBurned,
                    exercises = exercises,
                    notes = sessionEntity.notes
                )
            }
        }
    }

    override suspend fun logWorkoutSession(session: WorkoutSession) {
        val sessionEntity = WorkoutSessionEntity(
            id = session.id,
            date = session.date,
            title = session.title,
            durationMinutes = session.durationMinutes,
            kcalBurned = session.kcalBurned,
            notes = session.notes
        )
        workoutDao.insertWorkoutSession(sessionEntity)

        for (exercise in session.exercises) {
            val exEntity = ExerciseLogEntity(
                id = exercise.id,
                workoutSessionId = session.id,
                exerciseName = exercise.exerciseName,
                muscleGroup = exercise.muscleGroup.name,
                equipmentType = exercise.equipmentType.name,
                machineSettings = exercise.machineSettings,
                cardioDistanceKm = exercise.cardioDistanceKm,
                cardioDurationMinutes = exercise.cardioDurationMinutes
            )
            workoutDao.insertExerciseLog(exEntity)

            val setEntities = exercise.sets.map { set ->
                val estimated1Rm = OneRepMaxCalculator.calculate(set.weightKg, set.reps)
                val bestPast = workoutDao.getBestEstimated1Rm(exercise.exerciseName) ?: 0.0
                ExerciseSetEntity(
                    id = set.id,
                    exerciseLogId = exercise.id,
                    setNumber = set.setNumber,
                    weightKg = set.weightKg,
                    reps = set.reps,
                    rpe = set.rpe,
                    isPersonalRecord = estimated1Rm > bestPast && bestPast > 0.0,
                    estimated1RmKg = estimated1Rm
                )
            }
            workoutDao.insertExerciseSets(setEntities)
        }
    }

    override fun getAllReviews(): Flow<List<WeeklyProgressReview>> {
        return reviewDao.getAllReviews().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveReview(review: WeeklyProgressReview) {
        reviewDao.insertReview(review.toEntity())
    }

    // Mapping Extensions
    private fun UserProfileEntity.toDomain(gson: Gson): UserProfile {
        val muscleType = object : TypeToken<List<String>>() {}.type
        val muscleNames: List<String> = try {
            gson.fromJson(targetMuscleGroupsJson, muscleType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        val targetMuscles = muscleNames.mapNotNull { name ->
            try { MuscleGroup.valueOf(name) } catch (e: Exception) { null }
        }

        return UserProfile(
            id = id,
            age = age,
            gender = try { Gender.valueOf(gender) } catch (e: Exception) { Gender.MALE },
            heightCm = heightCm,
            currentWeightKg = currentWeightKg,
            targetWeightKg = targetWeightKg,
            targetTimelineWeeks = targetTimelineWeeks,
            primaryGoal = try { GoalType.valueOf(primaryGoal) } catch (e: Exception) { GoalType.MUSCLE_GAIN },
            targetMuscleGroups = if (targetMuscles.isEmpty()) listOf(MuscleGroup.CHEST, MuscleGroup.BACK_LATS) else targetMuscles,
            locality = try { Locality.valueOf(locality) } catch (e: Exception) { Locality.SOUTH_ASIAN },
            dietPreference = try { DietPreference.valueOf(dietPreference) } catch (e: Exception) { DietPreference.NON_VEGETARIAN },
            activityLevel = try { ActivityLevel.valueOf(activityLevel) } catch (e: Exception) { ActivityLevel.MODERATE },
            experienceLevel = try { ExperienceLevel.valueOf(experienceLevel) } catch (e: Exception) { ExperienceLevel.BEGINNER },
            tdeeKcal = tdeeKcal,
            targetKcal = targetKcal,
            targetProteinG = targetProteinG,
            targetCarbsG = targetCarbsG,
            targetFatG = targetFatG,
            waterTargetMl = waterTargetMl,
            waterConsumedMl = waterConsumedMl,
            activeTheme = try { ThemePalette.valueOf(activeTheme) } catch (e: Exception) { ThemePalette.POWER },
            isDarkMode = isDarkMode,
            isOnboarded = isOnboarded
        )
    }

    private fun UserProfile.toEntity(gson: Gson): UserProfileEntity {
        return UserProfileEntity(
            id = id,
            age = age,
            gender = gender.name,
            heightCm = heightCm,
            currentWeightKg = currentWeightKg,
            targetWeightKg = targetWeightKg,
            targetTimelineWeeks = targetTimelineWeeks,
            primaryGoal = primaryGoal.name,
            targetMuscleGroupsJson = gson.toJson(targetMuscleGroups.map { it.name }),
            locality = locality.name,
            dietPreference = dietPreference.name,
            activityLevel = activityLevel.name,
            experienceLevel = experienceLevel.name,
            tdeeKcal = tdeeKcal,
            targetKcal = targetKcal,
            targetProteinG = targetProteinG,
            targetCarbsG = targetCarbsG,
            targetFatG = targetFatG,
            waterTargetMl = waterTargetMl,
            waterConsumedMl = waterConsumedMl,
            activeTheme = activeTheme.name,
            isDarkMode = isDarkMode,
            isOnboarded = isOnboarded
        )
    }

    private fun FoodItemEntity.toDomain(): FoodItem {
        return FoodItem(
            id = id,
            mealType = try { MealType.valueOf(mealType) } catch (e: Exception) { MealType.LUNCH },
            name = name,
            quantity = quantity,
            unit = unit,
            calories = calories,
            proteinG = proteinG,
            carbsG = carbsG,
            fatG = fatG,
            fiberG = fiberG,
            date = date,
            isAiScanned = isAiScanned,
            isLabelOcr = isLabelOcr
        )
    }

    private fun FoodItem.toEntity(): FoodItemEntity {
        return FoodItemEntity(
            id = id,
            mealType = mealType.name,
            name = name,
            quantity = quantity,
            unit = unit,
            calories = calories,
            proteinG = proteinG,
            carbsG = carbsG,
            fatG = fatG,
            fiberG = fiberG,
            date = date,
            isAiScanned = isAiScanned,
            isLabelOcr = isLabelOcr
        )
    }

    private fun ProgressReviewEntity.toDomain(): WeeklyProgressReview {
        return WeeklyProgressReview(
            id = id,
            weekNumber = weekNumber,
            startDate = startDate,
            endDate = endDate,
            startingWeightKg = startingWeightKg,
            endingWeightKg = endingWeightKg,
            avgWeightKg = avgWeightKg,
            calorieAdherencePct = calorieAdherencePct,
            proteinAdherencePct = proteinAdherencePct,
            totalVolumeTonnage = totalVolumeTonnage,
            workoutsCompleted = workoutsCompleted,
            workoutsTarget = workoutsTarget,
            fatigueScore = fatigueScore,
            adherenceRating = adherenceRating,
            aiFeedbackSummary = aiFeedbackSummary,
            aiCalorieAdjustment = aiCalorieAdjustment,
            aiRecommendation = aiRecommendation
        )
    }

    private fun WeeklyProgressReview.toEntity(): ProgressReviewEntity {
        return ProgressReviewEntity(
            id = id,
            weekNumber = weekNumber,
            startDate = startDate,
            endDate = endDate,
            startingWeightKg = startingWeightKg,
            endingWeightKg = endingWeightKg,
            avgWeightKg = avgWeightKg,
            calorieAdherencePct = calorieAdherencePct,
            proteinAdherencePct = proteinAdherencePct,
            totalVolumeTonnage = totalVolumeTonnage,
            workoutsCompleted = workoutsCompleted,
            workoutsTarget = workoutsTarget,
            fatigueScore = fatigueScore,
            adherenceRating = adherenceRating,
            aiFeedbackSummary = aiFeedbackSummary,
            aiCalorieAdjustment = aiCalorieAdjustment,
            aiRecommendation = aiRecommendation
        )
    }
}

