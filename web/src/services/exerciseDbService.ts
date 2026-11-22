export interface ExerciseDbItem {
  id: string;
  name: string;
  bodyPart: string;
  target: string;
  equipment: string;
  gifUrl: string;
  secondaryMuscles: string[];
  instructions: string[];
}

const RAPIDAPI_HOST = 'exercisedb.p.rapidapi.com';
const BASE_URL = `https://${RAPIDAPI_HOST}`;
const STORAGE_KEY = 'pulseforge_exercisedb_key_v1';
const CACHE_PREFIX = 'pulseforge_exercisedb_cache_v1_';

export const EXERCISEDB_BODY_PARTS = [
  'all',
  'chest',
  'back',
  'shoulders',
  'upper arms',
  'upper legs',
  'lower legs',
  'waist',
  'cardio'
] as const;

export function getSavedExerciseDbKey(): string {
  const envKey = (import.meta.env.VITE_RAPIDAPI_EXERCISEDB_KEY as string | undefined) || '';
  if (envKey.trim()) return envKey.trim();
  try {
    return localStorage.getItem(STORAGE_KEY) || '';
  } catch {
    return '';
  }
}

export function saveExerciseDbKey(key: string): void {
  try {
    const clean = key.trim();
    if (clean) {
      localStorage.setItem(STORAGE_KEY, clean);
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  } catch {}
}

async function fetchExerciseDbEndpoint(path: string, apiKey: string): Promise<ExerciseDbItem[]> {
  const cacheKey = `${CACHE_PREFIX}${path}`;
  try {
    const cached = localStorage.getItem(cacheKey);
    if (cached) {
      const parsed = JSON.parse(cached) as { ts: number; data: ExerciseDbItem[] };
      if (Date.now() - parsed.ts < 1000 * 60 * 60 * 12 && Array.isArray(parsed.data)) {
        return parsed.data;
      }
    }
  } catch {}

  const res = await fetch(`${BASE_URL}${path}`, {
    method: 'GET',
    headers: {
      'x-rapidapi-key': apiKey,
      'x-rapidapi-host': RAPIDAPI_HOST
    }
  });

  if (!res.ok) {
    if (res.status === 401 || res.status === 403) {
      throw new Error('Invalid or unauthorized RapidAPI ExerciseDB key.');
    }
    throw new Error(`ExerciseDB error (${res.status})`);
  }

  const data = (await res.json()) as ExerciseDbItem[];
  const list = Array.isArray(data) ? data : [];
  try {
    localStorage.setItem(cacheKey, JSON.stringify({ ts: Date.now(), data: list }));
  } catch {}
  return list;
}

export async function searchExerciseDb(
  query: string,
  bodyPart: string,
  apiKey: string
): Promise<ExerciseDbItem[]> {
  const cleanQuery = query.trim().toLowerCase();
  if (cleanQuery.length >= 2) {
    return fetchExerciseDbEndpoint(`/exercises/name/${encodeURIComponent(cleanQuery)}?limit=24&offset=0`, apiKey);
  }
  if (bodyPart && bodyPart !== 'all') {
    return fetchExerciseDbEndpoint(`/exercises/bodyPart/${encodeURIComponent(bodyPart)}?limit=24&offset=0`, apiKey);
  }
  return fetchExerciseDbEndpoint('/exercises?limit=24&offset=0', apiKey);
}

export function mapBodyPartToMuscleGroup(bodyPart: string, target: string): string {
  const bp = `${bodyPart} ${target}`.toLowerCase();
  if (bp.includes('chest') || bp.includes('pectoral')) return 'CHEST';
  if (bp.includes('back') || bp.includes('lat') || bp.includes('trap')) return 'BACK_LATS';
  if (bp.includes('shoulder') || bp.includes('delt')) return 'SHOULDERS';
  if (bp.includes('bicep')) return 'BICEPS';
  if (bp.includes('tricep')) return 'TRICEPS';
  if (bp.includes('quad')) return 'QUADS';
  if (bp.includes('hamstring')) return 'HAMSTRINGS';
  if (bp.includes('glute')) return 'GLUTES';
  if (bp.includes('calf') || bp.includes('calves')) return 'CALVES';
  if (bp.includes('waist') || bp.includes('abs')) return 'CORE_ABS';
  return 'CHEST';
}

export function mapEquipmentType(equipment: string): string {
  const eq = equipment.toLowerCase();
  if (eq.includes('barbell') || eq.includes('ez')) return 'BARBELL';
  if (eq.includes('dumbbell')) return 'DUMBBELL';
  if (eq.includes('cable')) return 'CABLE';
  if (eq.includes('leverage') || eq.includes('machine') || eq.includes('smith')) return 'MACHINE';
  return 'BODYWEIGHT';
}
