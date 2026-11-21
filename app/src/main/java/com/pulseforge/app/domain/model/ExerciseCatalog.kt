package com.pulseforge.app.domain.model

/**
 * Distinct biomechanical movement patterns rendered by [ExerciseAnimationCanvas].
 */
enum class ExerciseAnimationType(val movementLabel: String) {
    BENCH_PRESS("Horizontal / Incline Press"),
    CABLE_FLY("Pec Fly Arc"),
    LAT_PULLDOWN("Vertical Pull"),
    BARBELL_ROW("Bent-Over Row"),
    OVERHEAD_PRESS("Overhead Press"),
    LATERAL_RAISE("Lateral Abduction"),
    BICEP_CURL("Elbow Flexion Curl"),
    TRICEP_PUSHDOWN("Elbow Extension Pushdown"),
    SQUAT("Squat Pattern"),
    LEG_PRESS("Leg Press / Extension"),
    ROMANIAN_DEADLIFT("Hip Hinge"),
    HIP_THRUST("Glute Bridge / Thrust"),
    CALF_RAISE("Plantarflexion Raise"),
    CRUNCH_PLANK("Core Flexion")
}

data class ExerciseDefinition(
    val id: String,
    val name: String,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup>,
    val equipment: EquipmentType,
    val defaultSettings: String,
    val difficulty: String,
    val animationType: ExerciseAnimationType,
    val cues: List<String>,
    val defaultSets: Int = 3,
    val defaultReps: Int = 10,
    val defaultWeightKg: Double = 40.0
)

object ExerciseCatalog {
    val allExercises: List<ExerciseDefinition> = listOf(
        // CHEST
        ExerciseDefinition(
            id = "incline_bench_press",
            name = "Incline Barbell Bench Press",
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "30° Bench • Grip Shoulder-Width+",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.BENCH_PRESS,
            cues = listOf(
                "Retract shoulder blades and plant feet firmly on the floor.",
                "Lower bar under control to upper chest at 45° elbow tuck.",
                "Drive bar up and slightly back without locking elbows hard."
            ),
            defaultSets = 4,
            defaultReps = 8,
            defaultWeightKg = 60.0
        ),
        ExerciseDefinition(
            id = "incline_db_press",
            name = "Incline Dumbbell Press",
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = EquipmentType.DUMBBELL,
            defaultSettings = "Seat Notch 3 • 30° Incline",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.BENCH_PRESS,
            cues = listOf(
                "Keep wrists stacked directly over elbows throughout the rep.",
                "Stretch pecs deeply at the bottom for a 1-second pause.",
                "Press dumbbells up in a slight arc until nearly touching."
            ),
            defaultSets = 3,
            defaultReps = 10,
            defaultWeightKg = 24.0
        ),
        ExerciseDefinition(
            id = "cable_chest_fly",
            name = "Standing Cable Chest Fly",
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.CABLE,
            defaultSettings = "Pulley Height 12 • Split Stance",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.CABLE_FLY,
            cues = listOf(
                "Maintain a soft 15° bend in both elbows like hugging a tree.",
                "Bring handles together at lower-chest level and squeeze pecs.",
                "Control the eccentric stretch back until hands align with torso."
            ),
            defaultSets = 3,
            defaultReps = 12,
            defaultWeightKg = 15.0
        ),

        // BACK & LATS
        ExerciseDefinition(
            id = "lat_pulldown",
            name = "Seated Lat Pulldown",
            primaryMuscle = MuscleGroup.BACK_LATS,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.SHOULDERS),
            equipment = EquipmentType.CABLE,
            defaultSettings = "Thigh Pad 4 • Wide Overhand Grip",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.LAT_PULLDOWN,
            cues = listOf(
                "Depress shoulders first before bending the elbows.",
                "Drive elbows down toward your hips, pulling bar to upper chest.",
                "Extend arms fully overhead on the return for a full lat stretch."
            ),
            defaultSets = 3,
            defaultReps = 10,
            defaultWeightKg = 55.0
        ),
        ExerciseDefinition(
            id = "barbell_row",
            name = "Bent-Over Barbell Row",
            primaryMuscle = MuscleGroup.BACK_LATS,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.HAMSTRINGS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "45° Torso Hinge • Pronated Grip",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.BARBELL_ROW,
            cues = listOf(
                "Brace core and keep spine neutral at a ~45° torso angle.",
                "Pull barbell toward your navel by squeezing shoulder blades together.",
                "Lower smoothly without rounding the lower back."
            ),
            defaultSets = 4,
            defaultReps = 8,
            defaultWeightKg = 65.0
        ),
        ExerciseDefinition(
            id = "pull_up",
            name = "Bodyweight Pull-Up",
            primaryMuscle = MuscleGroup.BACK_LATS,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BODYWEIGHT,
            defaultSettings = "Grip 1.5x Shoulder Width",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.LAT_PULLDOWN,
            cues = listOf(
                "Start from an active dead hang with ribs tucked down.",
                "Pull chest toward the bar while driving elbows down and back.",
                "Lower slowly for 2–3 seconds to full extension."
            ),
            defaultSets = 3,
            defaultReps = 8,
            defaultWeightKg = 70.0
        ),

        // SHOULDERS
        ExerciseDefinition(
            id = "overhead_press",
            name = "Standing Overhead Press",
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "Rack Height Chest • Just Outside Shoulders",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.OVERHEAD_PRESS,
            cues = listOf(
                "Squeeze glutes and brace abs to prevent lower-back arch.",
                "Press bar straight up, tucking chin slightly then pushing head through.",
                "Lower under control back to the upper clavicle."
            ),
            defaultSets = 4,
            defaultReps = 8,
            defaultWeightKg = 45.0
        ),
        ExerciseDefinition(
            id = "lateral_raise",
            name = "Dumbbell Lateral Raise",
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.CORE_ABS),
            equipment = EquipmentType.DUMBBELL,
            defaultSettings = "Slight Forward Lean • Scapular Plane",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.LATERAL_RAISE,
            cues = listOf(
                "Lead with elbows and wrists neutral, raising arms 30° forward of torso.",
                "Stop at shoulder height without shrugging your traps.",
                "Lower slowly against gravity to keep tension on the side delts."
            ),
            defaultSets = 3,
            defaultReps = 14,
            defaultWeightKg = 10.0
        ),

        // BICEPS
        ExerciseDefinition(
            id = "barbell_bicep_curl",
            name = "Standing Barbell Curl",
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "Supinated Shoulder-Width Grip",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.BICEP_CURL,
            cues = listOf(
                "Pin elbows tight to your ribs so shoulders don't swing.",
                "Curl bar smoothly to shoulder height and squeeze biceps hard.",
                "Lower for 2 full seconds to full elbow extension."
            ),
            defaultSets = 3,
            defaultReps = 10,
            defaultWeightKg = 30.0
        ),
        ExerciseDefinition(
            id = "hammer_curl",
            name = "Dumbbell Hammer Curl",
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS),
            equipment = EquipmentType.DUMBBELL,
            defaultSettings = "Neutral Palms-In Grip",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.BICEP_CURL,
            cues = listOf(
                "Keep thumbs pointing upward throughout the entire curl.",
                "Avoid torso sway; isolate elbow flexion.",
                "Pause briefly at the top before lowering under control."
            ),
            defaultSets = 3,
            defaultReps = 12,
            defaultWeightKg = 14.0
        ),

        // TRICEPS
        ExerciseDefinition(
            id = "tricep_pushdown",
            name = "Cable Rope Tricep Pushdown",
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS),
            equipment = EquipmentType.CABLE,
            defaultSettings = "High Pulley • Rope Attachment",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.TRICEP_PUSHDOWN,
            cues = listOf(
                "Lock upper arms vertically against your sides.",
                "Extend elbows fully and flare the rope ends apart at the bottom.",
                "Control the return up to 90° elbow flexion without drifting elbows."
            ),
            defaultSets = 3,
            defaultReps = 12,
            defaultWeightKg = 25.0
        ),
        ExerciseDefinition(
            id = "overhead_tricep_ext",
            name = "Overhead Cable Tricep Extension",
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CORE_ABS),
            equipment = EquipmentType.CABLE,
            defaultSettings = "Low/Mid Pulley • Split Stance",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.TRICEP_PUSHDOWN,
            cues = listOf(
                "Keep elbows pointing forward and tucked near ears.",
                "Stretch the long head of the tricep fully behind your head.",
                "Extend smoothly until arms are straight overhead."
            ),
            defaultSets = 3,
            defaultReps = 12,
            defaultWeightKg = 20.0
        ),

        // QUADS
        ExerciseDefinition(
            id = "barbell_back_squat",
            name = "Barbell Back Squat",
            primaryMuscle = MuscleGroup.QUADS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "Safety Pins Mid-Thigh • High Bar",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.SQUAT,
            cues = listOf(
                "Take a deep diaphragmatic breath and brace your core before descending.",
                "Push knees out over toes and sit down until thighs are at least parallel.",
                "Drive evenly through mid-foot to stand tall."
            ),
            defaultSets = 4,
            defaultReps = 8,
            defaultWeightKg = 80.0
        ),
        ExerciseDefinition(
            id = "leg_press_45",
            name = "Leg Press (45° Sled)",
            primaryMuscle = MuscleGroup.QUADS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = EquipmentType.PLATE_LOADED_MACHINE,
            defaultSettings = "Backrest Notch 2 • Feet Low-Mid",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.LEG_PRESS,
            cues = listOf(
                "Keep lower back and hips glued to the pad at all times.",
                "Lower sled until knees reach ~90–100° without pelvis tucking.",
                "Press through heels and mid-foot, stopping just short of knee lockout."
            ),
            defaultSets = 3,
            defaultReps = 10,
            defaultWeightKg = 160.0
        ),

        // HAMSTRINGS
        ExerciseDefinition(
            id = "romanian_deadlift",
            name = "Barbell Romanian Deadlift (RDL)",
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.BACK_LATS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "Soft Knee Bend • Straps Optional",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.ROMANIAN_DEADLIFT,
            cues = listOf(
                "Push hips straight backward while keeping the bar dragging along thighs.",
                "Stop descending as soon as hips stop moving back (mid-shin).",
                "Drive hips forward by squeezing hamstrings and glutes."
            ),
            defaultSets = 3,
            defaultReps = 10,
            defaultWeightKg = 75.0
        ),
        ExerciseDefinition(
            id = "db_rdl",
            name = "Dumbbell Romanian Deadlift",
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.DUMBBELL,
            defaultSettings = "15° Knee Bend • Neutral Spine",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.ROMANIAN_DEADLIFT,
            cues = listOf(
                "Keep dumbbells close to the front of your legs throughout.",
                "Feel a deep stretch across the back of the thighs at the bottom.",
                "Stand tall without hyperextending the lower back at lockout."
            ),
            defaultSets = 3,
            defaultReps = 12,
            defaultWeightKg = 22.0
        ),

        // GLUTES
        ExerciseDefinition(
            id = "barbell_hip_thrust",
            name = "Barbell Hip Thrust",
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.QUADS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BARBELL,
            defaultSettings = "Bench 16\" • Pad Centered on Hip Crease",
            difficulty = "Intermediate",
            animationType = ExerciseAnimationType.HIP_THRUST,
            cues = listOf(
                "Position shoulder blades on bench edge with shins vertical at the top.",
                "Tuck chin slightly and ribs down to avoid lower-back arching.",
                "Drive through heels and hold peak glute squeeze for 1 second."
            ),
            defaultSets = 4,
            defaultReps = 10,
            defaultWeightKg = 90.0
        ),
        ExerciseDefinition(
            id = "glute_bridge",
            name = "Bodyweight Glute Bridge",
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CORE_ABS),
            equipment = EquipmentType.BODYWEIGHT,
            defaultSettings = "Yoga Mat • Heels 6\" from Glutes",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.HIP_THRUST,
            cues = listOf(
                "Press lower back flat into the mat before lifting hips.",
                "Squeeze glutes hard at the top until torso and thighs align.",
                "Lower slowly without resting completely between reps."
            ),
            defaultSets = 3,
            defaultReps = 15,
            defaultWeightKg = 20.0
        ),

        // CALVES
        ExerciseDefinition(
            id = "standing_calf_raise",
            name = "Standing Machine Calf Raise",
            primaryMuscle = MuscleGroup.CALVES,
            secondaryMuscles = emptyList(),
            equipment = EquipmentType.PIN_LOADED_MACHINE,
            defaultSettings = "Pad Height 5 • Balls of Feet on Edge",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.CALF_RAISE,
            cues = listOf(
                "Drop heels fully for a 2-second stretch at the bottom of every rep.",
                "Drive straight up onto your big toes without bouncing.",
                "Hold peak contraction at the top for 1 full second."
            ),
            defaultSets = 4,
            defaultReps = 15,
            defaultWeightKg = 60.0
        ),

        // CORE & ABS
        ExerciseDefinition(
            id = "cable_crunch",
            name = "Kneeling Cable Crunch",
            primaryMuscle = MuscleGroup.CORE_ABS,
            secondaryMuscles = emptyList(),
            equipment = EquipmentType.CABLE,
            defaultSettings = "High Pulley • Rope Behind Neck",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.CRUNCH_PLANK,
            cues = listOf(
                "Keep hips stationary; flex only the spine to curl ribs toward pelvis.",
                "Exhale forcefully as you crunch down and hold for 1 second.",
                "Control the return upward until abs are fully lengthened."
            ),
            defaultSets = 3,
            defaultReps = 15,
            defaultWeightKg = 35.0
        ),
        ExerciseDefinition(
            id = "hanging_leg_raise",
            name = "Ab Mat Crunch / Leg Raise",
            primaryMuscle = MuscleGroup.CORE_ABS,
            secondaryMuscles = listOf(MuscleGroup.QUADS),
            equipment = EquipmentType.BODYWEIGHT,
            defaultSettings = "Yoga Mat • Controlled Tempo",
            difficulty = "Beginner",
            animationType = ExerciseAnimationType.CRUNCH_PLANK,
            cues = listOf(
                "Press lower back into the mat and tuck pelvis upward.",
                "Avoid using momentum; initiate every rep from the lower abs.",
                "Lower slowly to maintain constant core tension."
            ),
            defaultSets = 3,
            defaultReps = 15,
            defaultWeightKg = 0.0
        )
    )

    fun forMuscle(muscle: MuscleGroup?): List<ExerciseDefinition> {
        if (muscle == null) return allExercises
        val primary = allExercises.filter { it.primaryMuscle == muscle }
        val secondary = allExercises.filter { it.primaryMuscle != muscle && muscle in it.secondaryMuscles }
        return primary + secondary
    }

    fun findByName(name: String): ExerciseDefinition? =
        allExercises.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: allExercises.firstOrNull { name.contains(it.name.split(" ").last(), ignoreCase = true) }
}
