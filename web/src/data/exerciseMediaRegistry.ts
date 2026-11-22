import { getSavedExerciseDbKey, searchExerciseDb } from '../services/exerciseDbService';

export interface ExerciseMediaEntry {
  id: string;
  name: string;
  aliases: string[];
  exerciseDbQuery: string;
  cdnVideoUrl: string;
  primaryMuscle: string;
  secondaryMuscles: string[];
  equipment: string;
  cue: string;
}

export const EXERCISE_MEDIA_REGISTRY: ExerciseMediaEntry[] = [
  {
    id: 'incline_bench_press',
    name: 'Incline Barbell Bench Press',
    aliases: ['bench press', 'incline bench', 'barbell bench press'],
    exerciseDbQuery: 'barbell incline bench press',
    cdnVideoUrl: '/assets/exercises/incline_bench_press.mp4',
    primaryMuscle: 'Chest / Pectorals',
    secondaryMuscles: ['Shoulders / Deltoids', 'Triceps'],
    equipment: 'Barbell',
    cue: 'Retract shoulder blades, lower under control to upper chest, drive up smoothly.'
  },
  {
    id: 'cable_chest_fly',
    name: 'Standing Cable Chest Fly',
    aliases: ['cable fly', 'chest fly', 'cable chest fly'],
    exerciseDbQuery: 'cable standing fly',
    cdnVideoUrl: '/assets/exercises/cable_chest_fly.mp4',
    primaryMuscle: 'Chest / Pectorals',
    secondaryMuscles: ['Shoulders / Deltoids', 'Abs & Core'],
    equipment: 'Cable Pulley Machine',
    cue: 'Keep a soft 15° elbow bend and squeeze pecs together at center.'
  },
  {
    id: 'lat_pulldown',
    name: 'Seated Lat Pulldown',
    aliases: ['lat pulldown', 'pulldown'],
    exerciseDbQuery: 'cable lat pulldown',
    cdnVideoUrl: '/assets/exercises/lat_pulldown.mp4',
    primaryMuscle: 'Lats & Upper Back',
    secondaryMuscles: ['Biceps', 'Shoulders / Deltoids'],
    equipment: 'Cable Pulley Machine',
    cue: 'Depress shoulders first, drive elbows toward hips to upper chest.'
  },
  {
    id: 'barbell_row',
    name: 'Bent-Over Barbell Row',
    aliases: ['bent over barbell row', 'barbell row', 'bent-over row'],
    exerciseDbQuery: 'barbell bent over row',
    cdnVideoUrl: '/assets/exercises/barbell_row.mp4',
    primaryMuscle: 'Lats & Upper Back',
    secondaryMuscles: ['Biceps', 'Hamstrings', 'Abs & Core'],
    equipment: 'Barbell',
    cue: 'Hinge at 45° with flat spine, pull bar to lower ribs and squeeze.'
  },
  {
    id: 'overhead_press',
    name: 'Standing Overhead Barbell Press',
    aliases: ['overhead press', 'shoulder press', 'ohp', 'military press'],
    exerciseDbQuery: 'barbell standing overhead press',
    cdnVideoUrl: '/assets/exercises/overhead_press.mp4',
    primaryMuscle: 'Shoulders / Deltoids',
    secondaryMuscles: ['Triceps', 'Abs & Core'],
    equipment: 'Barbell',
    cue: 'Brace core and glutes, press straight overhead and lock out with head through.'
  },
  {
    id: 'lateral_raise',
    name: 'Dumbbell Lateral Raise',
    aliases: ['lateral raise', 'side raise'],
    exerciseDbQuery: 'dumbbell lateral raise',
    cdnVideoUrl: '/assets/exercises/lateral_raise.mp4',
    primaryMuscle: 'Shoulders / Deltoids',
    secondaryMuscles: ['Lats & Upper Back'],
    equipment: 'Dumbbell',
    cue: 'Lead with elbows slightly forward in scapular plane to shoulder height.'
  },
  {
    id: 'bicep_curl',
    name: 'Standing Dumbbell Bicep Curl',
    aliases: ['bicep curl', 'dumbbell curl', 'curl'],
    exerciseDbQuery: 'dumbbell bicep curl',
    cdnVideoUrl: '/assets/exercises/bicep_curl.mp4',
    primaryMuscle: 'Biceps',
    secondaryMuscles: ['Abs & Core'],
    equipment: 'Dumbbell',
    cue: 'Pin elbows at ribs, supinate wrists as you curl, lower slowly.'
  },
  {
    id: 'tricep_pushdown',
    name: 'Cable Rope Tricep Pushdown',
    aliases: ['tricep pushdown', 'rope pushdown', 'pushdown'],
    exerciseDbQuery: 'cable pushdown',
    cdnVideoUrl: '/assets/exercises/tricep_pushdown.mp4',
    primaryMuscle: 'Triceps',
    secondaryMuscles: ['Abs & Core'],
    equipment: 'Cable Pulley Machine',
    cue: 'Lock upper arms at sides and flare the rope apart at full elbow extension.'
  },
  {
    id: 'barbell_squat',
    name: 'Barbell Back Squat',
    aliases: ['squat', 'back squat', 'barbell squat'],
    exerciseDbQuery: 'barbell full squat',
    cdnVideoUrl: '/assets/exercises/barbell_squat.mp4',
    primaryMuscle: 'Quadriceps',
    secondaryMuscles: ['Glutes', 'Hamstrings', 'Abs & Core'],
    equipment: 'Barbell',
    cue: 'Brace deeply, sit hips back and down below parallel, drive through mid-foot.'
  },
  {
    id: 'leg_press',
    name: '45° Plate-Loaded Leg Press',
    aliases: ['leg press'],
    exerciseDbQuery: 'sled 45 leg press',
    cdnVideoUrl: '/assets/exercises/leg_press.mp4',
    primaryMuscle: 'Quadriceps',
    secondaryMuscles: ['Glutes', 'Hamstrings'],
    equipment: 'Machine',
    cue: 'Lower sled until knees reach 90° without lifting lower back off the pad.'
  },
  {
    id: 'romanian_deadlift',
    name: 'Romanian Deadlift (RDL)',
    aliases: ['romanian deadlift', 'rdl', 'deadlift'],
    exerciseDbQuery: 'barbell romanian deadlift',
    cdnVideoUrl: '/assets/exercises/romanian_deadlift.mp4',
    primaryMuscle: 'Hamstrings',
    secondaryMuscles: ['Glutes', 'Lats & Upper Back'],
    equipment: 'Barbell',
    cue: 'Push hips straight back keeping bar close to shins until deep hamstring stretch.'
  },
  {
    id: 'hip_thrust',
    name: 'Barbell Hip Thrust',
    aliases: ['hip thrust', 'glute bridge'],
    exerciseDbQuery: 'barbell hip thrust',
    cdnVideoUrl: '/assets/exercises/hip_thrust.mp4',
    primaryMuscle: 'Glutes',
    secondaryMuscles: ['Hamstrings', 'Abs & Core'],
    equipment: 'Barbell',
    cue: 'Tuck chin, drive through heels and pause 1 second at full hip extension.'
  },
  {
    id: 'calf_raise',
    name: 'Standing Calf Raise',
    aliases: ['calf raise'],
    exerciseDbQuery: 'standing calf raise',
    cdnVideoUrl: '/assets/exercises/calf_raise.mp4',
    primaryMuscle: 'Calves',
    secondaryMuscles: [],
    equipment: 'Machine',
    cue: 'Pause at full bottom stretch and rise onto big toes with straight knees.'
  },
  {
    id: 'hanging_crunch',
    name: 'Weighted Core Crunch / Plank',
    aliases: ['hanging leg raise', 'crunch', 'plank', 'core'],
    exerciseDbQuery: 'hanging leg raise',
    cdnVideoUrl: '/assets/exercises/hanging_crunch.mp4',
    primaryMuscle: 'Abs & Core',
    secondaryMuscles: [],
    equipment: 'Bodyweight',
    cue: 'Curl pelvis toward ribcage with controlled eccentric lowering.'
  }
];

const memoryMediaCache = new Map<string, string>();

export function findRegistryEntry(exerciseName: string): ExerciseMediaEntry | null {
  const clean = exerciseName.trim().toLowerCase();
  if (!clean) return null;
  return (
    EXERCISE_MEDIA_REGISTRY.find(
      (entry) =>
        entry.name.toLowerCase() === clean ||
        entry.aliases.some((alias) => clean.includes(alias) || alias.includes(clean))
    ) ?? null
  );
}

export function getExerciseApiKey(): string {
  const envKey =
    (import.meta.env.VITE_EXERCISE_API_KEY as string | undefined) ||
    (import.meta.env.VITE_RAPIDAPI_EXERCISEDB_KEY as string | undefined) ||
    '';
  if (envKey.trim()) return envKey.trim();
  return getSavedExerciseDbKey();
}

export async function resolveExerciseMediaUrl(exerciseName: string): Promise<string | null> {
  const entry = findRegistryEntry(exerciseName);
  const query = entry?.exerciseDbQuery || exerciseName.trim().toLowerCase();
  if (!query || query.length < 2) return null;

  const cached = memoryMediaCache.get(query);
  if (cached) return cached;

  const apiKey = getExerciseApiKey();
  if (!apiKey) return null;

  try {
    const results = await searchExerciseDb(query, 'all', apiKey);
    const gifUrl = results[0]?.gifUrl;
    if (gifUrl) {
      memoryMediaCache.set(query, gifUrl);
      const img = new Image();
      img.src = gifUrl;
      return gifUrl;
    }
  } catch {
    return null;
  }
  return null;
}

export async function preloadPresetMediaAssets(): Promise<void> {
  const apiKey = getExerciseApiKey();
  if (!apiKey) return;
  const topPresets = EXERCISE_MEDIA_REGISTRY.slice(0, 6);
  await Promise.allSettled(topPresets.map((item) => resolveExerciseMediaUrl(item.name)));
}
