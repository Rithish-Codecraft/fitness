export interface ExerciseMediaEntry {
  exerciseId: string;
  name: string;
  animationUrl: string;
  targetMuscle: string;
  secondaryMuscles: string[];
  activationPercent: number;
  formCue: string;
}

/**
 * Structured Exercise Media Map (exerciseId -> anatomical 3D GIF + muscle metadata).
 * Powered by verified ExerciseDB v1 media assets.
 */
export const exerciseMediaMap: Record<string, ExerciseMediaEntry> = {
  incline_bench_press: {
    exerciseId: '3TZduzM',
    name: 'Incline Barbell Bench Press',
    animationUrl: 'https://static.exercisedb.dev/media/3TZduzM.gif',
    targetMuscle: 'Chest / Pectorals',
    secondaryMuscles: ['Shoulders / Deltoids', 'Triceps'],
    activationPercent: 92,
    formCue: 'Retract shoulder blades, lower under control to upper chest, drive up smoothly.'
  },
  cable_chest_fly: {
    exerciseId: '0CXGHya',
    name: 'Standing Cable Chest Fly',
    animationUrl: 'https://static.exercisedb.dev/media/0CXGHya.gif',
    targetMuscle: 'Chest / Pectorals',
    secondaryMuscles: ['Shoulders / Deltoids', 'Abs & Core'],
    activationPercent: 88,
    formCue: 'Keep a soft 15° elbow bend and squeeze pecs together at center.'
  },
  lat_pulldown: {
    exerciseId: '0MlxeMn',
    name: 'Seated Lat Pulldown',
    animationUrl: 'https://static.exercisedb.dev/media/0MlxeMn.gif',
    targetMuscle: 'Lats & Upper Back',
    secondaryMuscles: ['Biceps', 'Shoulders / Deltoids'],
    activationPercent: 90,
    formCue: 'Depress shoulders first, drive elbows toward hips to upper chest.'
  },
  barbell_row: {
    exerciseId: 'eZyBC3j',
    name: 'Bent-Over Barbell Row',
    animationUrl: 'https://static.exercisedb.dev/media/eZyBC3j.gif',
    targetMuscle: 'Lats & Upper Back',
    secondaryMuscles: ['Biceps', 'Hamstrings', 'Abs & Core'],
    activationPercent: 91,
    formCue: 'Hinge at 45° with flat spine, pull bar to lower ribs and squeeze.'
  },
  overhead_press: {
    exerciseId: 'A6wtbuL',
    name: 'Standing Overhead Barbell Press',
    animationUrl: 'https://static.exercisedb.dev/media/A6wtbuL.gif',
    targetMuscle: 'Shoulders / Deltoids',
    secondaryMuscles: ['Triceps', 'Abs & Core'],
    activationPercent: 89,
    formCue: 'Brace core and glutes, press vertically overhead past the face.'
  },
  lateral_raise: {
    exerciseId: 'DsgkuIt',
    name: 'Dumbbell Lateral Raise',
    animationUrl: 'https://static.exercisedb.dev/media/DsgkuIt.gif',
    targetMuscle: 'Shoulders / Deltoids',
    secondaryMuscles: ['Abs & Core'],
    activationPercent: 86,
    formCue: 'Lead with elbows in the scapular plane to shoulder height.'
  },
  bicep_curl: {
    exerciseId: 'ae9UoXQ',
    name: 'Standing Dumbbell Bicep Curl',
    animationUrl: 'https://static.exercisedb.dev/media/ae9UoXQ.gif',
    targetMuscle: 'Biceps',
    secondaryMuscles: ['Abs & Core'],
    activationPercent: 88,
    formCue: 'Pin elbows to ribs, supinate wrists as you curl without swinging.'
  },
  tricep_pushdown: {
    exerciseId: '9tvVVM9',
    name: 'Cable Rope Tricep Pushdown',
    animationUrl: 'https://static.exercisedb.dev/media/9tvVVM9.gif',
    targetMuscle: 'Triceps',
    secondaryMuscles: ['Shoulders / Deltoids'],
    activationPercent: 87,
    formCue: 'Lock upper arms at your sides and extend forearms fully downward.'
  },
  barbell_squat: {
    exerciseId: 'DhMl549',
    name: 'Barbell Back Squat',
    animationUrl: 'https://static.exercisedb.dev/media/DhMl549.gif',
    targetMuscle: 'Quadriceps',
    secondaryMuscles: ['Glutes', 'Hamstrings', 'Abs & Core'],
    activationPercent: 95,
    formCue: 'Break at hips and knees together, sit to parallel, drive through mid-foot.'
  },
  leg_press: {
    exerciseId: '10Z2DXU',
    name: '45° Plate-Loaded Leg Press',
    animationUrl: 'https://static.exercisedb.dev/media/10Z2DXU.gif',
    targetMuscle: 'Quadriceps',
    secondaryMuscles: ['Glutes', 'Hamstrings'],
    activationPercent: 90,
    formCue: 'Keep lower back glued to the pad and press smoothly without locking knees.'
  },
  romanian_deadlift: {
    exerciseId: 'wQ2c4XD',
    name: 'Romanian Deadlift (RDL)',
    animationUrl: 'https://static.exercisedb.dev/media/wQ2c4XD.gif',
    targetMuscle: 'Hamstrings',
    secondaryMuscles: ['Glutes', 'Lats & Upper Back'],
    activationPercent: 92,
    formCue: 'Push hips straight back with soft knees until deep hamstring stretch.'
  },
  hip_thrust: {
    exerciseId: 'Pjbc0Kt',
    name: 'Barbell Hip Thrust',
    animationUrl: 'https://static.exercisedb.dev/media/Pjbc0Kt.gif',
    targetMuscle: 'Glutes',
    secondaryMuscles: ['Hamstrings', 'Abs & Core'],
    activationPercent: 94,
    formCue: 'Drive through heels to full hip extension and squeeze glutes at top.'
  },
  calf_raise: {
    exerciseId: '2IHEa2T',
    name: 'Standing Calf Raise',
    animationUrl: 'https://static.exercisedb.dev/media/2IHEa2T.gif',
    targetMuscle: 'Calves',
    secondaryMuscles: [],
    activationPercent: 85,
    formCue: 'Pause at the bottom stretch for 1s, then press high onto big toes.'
  },
  hanging_crunch: {
    exerciseId: 'I3tsCnC',
    name: 'Weighted Core Crunch / Plank',
    animationUrl: 'https://static.exercisedb.dev/media/I3tsCnC.gif',
    targetMuscle: 'Abs & Core',
    secondaryMuscles: ['Shoulders / Deltoids'],
    activationPercent: 89,
    formCue: 'Exhale hard and curl ribcage toward pelvis with braced core.'
  }
};

const apiLookupCache = new Map<string, ExerciseMediaEntry | null>();

export function findLocalExerciseMedia(
  exerciseName: string,
  muscleGroup: string
): ExerciseMediaEntry | null {
  const clean = exerciseName.trim().toLowerCase();
  const entries = Object.values(exerciseMediaMap);

  if (clean) {
    const exact = entries.find(
      (e) =>
        e.name.toLowerCase() === clean ||
        e.name.toLowerCase().includes(clean) ||
        clean.includes(e.name.toLowerCase())
    );
    if (exact) return exact;

    if (clean.includes('bench') || clean.includes('chest press')) return exerciseMediaMap.incline_bench_press;
    if (clean.includes('fly') || clean.includes('crossover')) return exerciseMediaMap.cable_chest_fly;
    if (clean.includes('pulldown')) return exerciseMediaMap.lat_pulldown;
    if (clean.includes('row')) return exerciseMediaMap.barbell_row;
    if (clean.includes('overhead') || clean.includes('shoulder press') || clean.includes('ohp')) {
      return exerciseMediaMap.overhead_press;
    }
    if (clean.includes('lateral')) return exerciseMediaMap.lateral_raise;
    if (clean.includes('curl')) return exerciseMediaMap.bicep_curl;
    if (clean.includes('pushdown') || clean.includes('tricep')) return exerciseMediaMap.tricep_pushdown;
    if (clean.includes('leg press')) return exerciseMediaMap.leg_press;
    if (clean.includes('squat')) return exerciseMediaMap.barbell_squat;
    if (clean.includes('deadlift') || clean.includes('rdl')) return exerciseMediaMap.romanian_deadlift;
    if (clean.includes('thrust') || clean.includes('bridge')) return exerciseMediaMap.hip_thrust;
    if (clean.includes('calf')) return exerciseMediaMap.calf_raise;
    if (clean.includes('plank') || clean.includes('crunch') || clean.includes('ab')) {
      return exerciseMediaMap.hanging_crunch;
    }
    return null;
  }

  return (
    entries.find((e) => e.targetMuscle.toLowerCase() === muscleGroup.toLowerCase()) ??
    exerciseMediaMap.incline_bench_press
  );
}

export async function searchExerciseDbMedia(query: string): Promise<ExerciseMediaEntry | null> {
  const clean = query.trim().toLowerCase();
  if (clean.length < 3) return null;
  if (apiLookupCache.has(clean)) return apiLookupCache.get(clean) ?? null;

  try {
    const url = `https://oss.exercisedb.dev/api/v1/exercises?name=${encodeURIComponent(clean)}&limit=1`;
    const res = await fetch(url);
    if (!res.ok) {
      apiLookupCache.set(clean, null);
      return null;
    }
    const json = await res.json();
    const first = json?.data?.[0];
    if (!first?.gifUrl) {
      apiLookupCache.set(clean, null);
      return null;
    }
    const entry: ExerciseMediaEntry = {
      exerciseId: first.exerciseId,
      name: first.name,
      animationUrl: first.gifUrl,
      targetMuscle: Array.isArray(first.targetMuscles) && first.targetMuscles[0]
        ? first.targetMuscles[0]
        : 'Full Body',
      secondaryMuscles: Array.isArray(first.secondaryMuscles) ? first.secondaryMuscles : [],
      activationPercent: 88,
      formCue:
        Array.isArray(first.instructions) && first.instructions[0]
          ? String(first.instructions[0]).replace(/^Step:\d+\s*/i, '')
          : 'Control both the concentric drive and eccentric lowering phase.'
    };
    apiLookupCache.set(clean, entry);
    return entry;
  } catch {
    return null;
  }
}
