export type Gender = 'MALE' | 'FEMALE';
export type GoalType = 'FAT_LOSS' | 'MUSCLE_GAIN' | 'MAINTENANCE' | 'RECOMPOSITION' | 'ATHLETIC_PERFORMANCE';
export type ActivityLevel = 'SEDENTARY' | 'LIGHT' | 'MODERATE' | 'ACTIVE' | 'ATHLETE';
export type ThemePalette = 'POWER' | 'LAVENDER' | 'LONGEVITY' | 'WELLNESS';
export type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER' | 'SNACK';
export type DetectedActivityType = 'Still / Sedentary' | 'Walking' | 'Running' | 'Cycling' | 'In Vehicle';

export interface UserProfile {
  id: string;
  name: string;
  age: number;
  gender: Gender;
  heightCm: number;
  currentWeightKg: number;
  targetWeightKg: number;
  targetTimelineWeeks: number;
  primaryGoal: GoalType;
  activityLevel: ActivityLevel;
  tdeeKcal: number;
  targetKcal: number;
  targetProteinG: number;
  targetCarbsG: number;
  targetFatG: number;
  waterTargetMl: number;
  waterConsumedMl: number;
  activeTheme: ThemePalette;
  isDarkMode: boolean;
}

export interface FoodItem {
  id: string;
  mealType: MealType;
  name: string;
  quantity: number;
  unit: string;
  calories: number;
  proteinG: number;
  carbsG: number;
  fatG: number;
  date: string;
}

export interface ExerciseSet {
  id: string;
  setNumber: number;
  weightKg: number;
  reps: number;
  rpe: number;
  estimated1RmKg: number;
}

export interface ExerciseLog {
  id: string;
  exerciseName: string;
  muscleGroup: string;
  equipmentType?: string;
  sets: ExerciseSet[];
}

export interface WorkoutSession {
  id: string;
  date: string;
  title: string;
  durationMinutes: number;
  kcalBurned?: number;
  exercises: ExerciseLog[];
  notes: string;
}

export interface DailyRecording {
  date: string;
  steps: number;
  caloriesConsumed: number;
  waterMl: number;
}

export interface GpsWaypoint {
  latitude: number;
  longitude: number;
  altitudeMeters: number;
  timestampMillis: number;
}

export interface PhoneSensorTelemetry {
  stepCount: number | null;
  stepCadenceSpm: number | null;
  activityType: DetectedActivityType;
  distanceMeters: number | null;
  speedKmh: number | null;
  paceMinPerKm: number | null;
  gpsRoute: GpsWaypoint[];
  elevationGainMeters: number | null;
  floorsClimbed: number | null;
  activeCaloriesBurned: number | null;
  sleepStartTime: string;
  wakeTime: string;
  sleepTotalHours: number | null;
}

export interface LifestyleSleepAnalysis {
  dailyScreenTimeMinutes: number;
  nightScreenTimeMinutes: number;
  mostUsedNightApp: string;
  mostUsedNightAppMinutes: number;
  estimatedBedtime: string;
  estimatedWakeTime: string;
  estimatedSleepDurationHours: number | null;
  sleepQualityScore: number | null;
  sleepDisruptionFeedback: string;
  sourceNote: string;
}

export interface VersionInfo {
  versionCode: number;
  versionName: string;
  apkUrl: string;
  releaseNotes: string;
  fileSizeBytes: number;
  sha256: string;
}
