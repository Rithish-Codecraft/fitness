export type AnimationPattern =
  | 'BENCH_PRESS'
  | 'CABLE_FLY'
  | 'LAT_PULLDOWN'
  | 'BARBELL_ROW'
  | 'OVERHEAD_PRESS'
  | 'LATERAL_RAISE'
  | 'BICEP_CURL'
  | 'TRICEP_PUSHDOWN'
  | 'SQUAT'
  | 'LEG_PRESS'
  | 'ROMANIAN_DEADLIFT'
  | 'HIP_THRUST'
  | 'CALF_RAISE'
  | 'CRUNCH_PLANK';

export interface ExercisePreset {
  id: string;
  name: string;
  primaryMuscle: string;
  secondaryMuscles: string[];
  equipment: string;
  animationType: AnimationPattern;
  defaultWeightKg: number;
  defaultReps: number;
  cue: string;
}

export const EXERCISE_PRESETS: ExercisePreset[] = [
  {
    id: 'incline_bench_press',
    name: 'Incline Barbell Bench Press',
    primaryMuscle: 'Chest / Pectorals',
    secondaryMuscles: ['Shoulders / Deltoids', 'Triceps'],
    equipment: 'Barbell',
    animationType: 'BENCH_PRESS',
    defaultWeightKg: 60,
    defaultReps: 8,
    cue: 'Retract shoulder blades, lower under control to upper chest, drive up smoothly.'
  },
  {
    id: 'cable_chest_fly',
    name: 'Standing Cable Chest Fly',
    primaryMuscle: 'Chest / Pectorals',
    secondaryMuscles: ['Shoulders / Deltoids', 'Abs & Core'],
    equipment: 'Cable Pulley Machine',
    animationType: 'CABLE_FLY',
    defaultWeightKg: 15,
    defaultReps: 12,
    cue: 'Keep a soft 15° elbow bend and squeeze pecs together at center.'
  },
  {
    id: 'lat_pulldown',
    name: 'Seated Lat Pulldown',
    primaryMuscle: 'Lats & Upper Back',
    secondaryMuscles: ['Biceps', 'Shoulders / Deltoids'],
    equipment: 'Cable Pulley Machine',
    animationType: 'LAT_PULLDOWN',
    defaultWeightKg: 55,
    defaultReps: 10,
    cue: 'Depress shoulders first, drive elbows toward hips to upper chest.'
  },
  {
    id: 'barbell_row',
    name: 'Bent-Over Barbell Row',
    primaryMuscle: 'Lats & Upper Back',
    secondaryMuscles: ['Biceps', 'Hamstrings', 'Abs & Core'],
    equipment: 'Barbell',
    animationType: 'BARBELL_ROW',
    defaultWeightKg: 60,
    defaultReps: 10,
    cue: 'Hinge at 45° with flat spine, pull bar to lower ribs and squeeze.'
  },
  {
    id: 'overhead_press',
    name: 'Standing Overhead Barbell Press',
    primaryMuscle: 'Shoulders / Deltoids',
    secondaryMuscles: ['Triceps', 'Abs & Core'],
    equipment: 'Barbell',
    animationType: 'OVERHEAD_PRESS',
    defaultWeightKg: 40,
    defaultReps: 8,
    cue: 'Brace core and glutes, press vertically overhead past the face.'
  },
  {
    id: 'lateral_raise',
    name: 'Dumbbell Lateral Raise',
    primaryMuscle: 'Shoulders / Deltoids',
    secondaryMuscles: ['Abs & Core'],
    equipment: 'Dumbbells',
    animationType: 'LATERAL_RAISE',
    defaultWeightKg: 10,
    defaultReps: 15,
    cue: 'Lead with elbows in the scapular plane to shoulder height.'
  },
  {
    id: 'bicep_curl',
    name: 'Standing Dumbbell Bicep Curl',
    primaryMuscle: 'Biceps',
    secondaryMuscles: ['Abs & Core'],
    equipment: 'Dumbbells',
    animationType: 'BICEP_CURL',
    defaultWeightKg: 14,
    defaultReps: 12,
    cue: 'Pin elbows to ribs, supinate wrists as you curl without swinging.'
  },
  {
    id: 'tricep_pushdown',
    name: 'Cable Rope Tricep Pushdown',
    primaryMuscle: 'Triceps',
    secondaryMuscles: ['Shoulders / Deltoids'],
    equipment: 'Cable Pulley Machine',
    animationType: 'TRICEP_PUSHDOWN',
    defaultWeightKg: 25,
    defaultReps: 12,
    cue: 'Lock upper arms at your sides and extend forearms fully downward.'
  },
  {
    id: 'barbell_squat',
    name: 'Barbell Back Squat',
    primaryMuscle: 'Quadriceps',
    secondaryMuscles: ['Glutes', 'Hamstrings', 'Abs & Core'],
    equipment: 'Barbell',
    animationType: 'SQUAT',
    defaultWeightKg: 80,
    defaultReps: 8,
    cue: 'Break at hips and knees together, sit to parallel, drive through mid-foot.'
  },
  {
    id: 'leg_press',
    name: '45° Plate-Loaded Leg Press',
    primaryMuscle: 'Quadriceps',
    secondaryMuscles: ['Glutes', 'Hamstrings'],
    equipment: 'Plate-Loaded / Smith Machine',
    animationType: 'LEG_PRESS',
    defaultWeightKg: 140,
    defaultReps: 10,
    cue: 'Keep lower back glued to the pad and press smoothly without locking knees.'
  },
  {
    id: 'romanian_deadlift',
    name: 'Romanian Deadlift (RDL)',
    primaryMuscle: 'Hamstrings',
    secondaryMuscles: ['Glutes', 'Lats & Upper Back'],
    equipment: 'Barbell',
    animationType: 'ROMANIAN_DEADLIFT',
    defaultWeightKg: 70,
    defaultReps: 10,
    cue: 'Push hips straight back with soft knees until deep hamstring stretch.'
  },
  {
    id: 'hip_thrust',
    name: 'Barbell Hip Thrust',
    primaryMuscle: 'Glutes',
    secondaryMuscles: ['Hamstrings', 'Abs & Core'],
    equipment: 'Barbell',
    animationType: 'HIP_THRUST',
    defaultWeightKg: 90,
    defaultReps: 10,
    cue: 'Drive through heels to full hip extension and squeeze glutes at top.'
  },
  {
    id: 'calf_raise',
    name: 'Standing Calf Raise',
    primaryMuscle: 'Calves',
    secondaryMuscles: [],
    equipment: 'Pin-Selected Machine',
    animationType: 'CALF_RAISE',
    defaultWeightKg: 50,
    defaultReps: 15,
    cue: 'Pause at the bottom stretch for 1s, then press high onto big toes.'
  },
  {
    id: 'hanging_crunch',
    name: 'Weighted Core Crunch / Plank',
    primaryMuscle: 'Abs & Core',
    secondaryMuscles: ['Shoulders / Deltoids'],
    equipment: 'Bodyweight / Calisthenics',
    animationType: 'CRUNCH_PLANK',
    defaultWeightKg: 0,
    defaultReps: 15,
    cue: 'Exhale hard and curl ribcage toward pelvis with braced core.'
  }
];

export function resolveAnimationForWorkout(
  exerciseName: string,
  muscleGroup: string
): {
  animationType: AnimationPattern;
  primaryMuscle: string;
  secondaryMuscles: string[];
  cue: string;
} {
  const cleanName = exerciseName.toLowerCase();
  const byName = EXERCISE_PRESETS.find(
    (p) =>
      cleanName &&
      (p.name.toLowerCase().includes(cleanName) || cleanName.includes(p.name.toLowerCase()))
  );
  if (byName) return byName;

  if (cleanName.includes('fly')) return EXERCISE_PRESETS[1];
  if (cleanName.includes('pulldown')) return EXERCISE_PRESETS[2];
  if (cleanName.includes('row')) return EXERCISE_PRESETS[3];
  if (cleanName.includes('overhead') || cleanName.includes('ohp')) return EXERCISE_PRESETS[4];
  if (cleanName.includes('lateral')) return EXERCISE_PRESETS[5];
  if (cleanName.includes('curl')) return EXERCISE_PRESETS[6];
  if (cleanName.includes('pushdown') || cleanName.includes('tricep')) return EXERCISE_PRESETS[7];
  if (cleanName.includes('squat')) return EXERCISE_PRESETS[8];
  if (cleanName.includes('leg press')) return EXERCISE_PRESETS[9];
  if (cleanName.includes('deadlift') || cleanName.includes('rdl')) return EXERCISE_PRESETS[10];
  if (cleanName.includes('thrust') || cleanName.includes('bridge')) return EXERCISE_PRESETS[11];
  if (cleanName.includes('calf')) return EXERCISE_PRESETS[12];
  if (cleanName.includes('plank') || cleanName.includes('crunch')) return EXERCISE_PRESETS[13];

  const byMuscle = EXERCISE_PRESETS.find(
    (p) => p.primaryMuscle.toLowerCase() === muscleGroup.toLowerCase()
  );
  return byMuscle ?? EXERCISE_PRESETS[0];
}
