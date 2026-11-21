import { ActivityLevel, Gender, GoalType, UserProfile } from '../types/models';

const ACTIVITY_MULTIPLIERS: Record<ActivityLevel, number> = {
  SEDENTARY: 1.2,
  LIGHT: 1.375,
  MODERATE: 1.55,
  ACTIVE: 1.725,
  ATHLETE: 1.9
};

export const GOAL_LABELS: Record<GoalType, string> = {
  FAT_LOSS: 'Fat Loss',
  MUSCLE_GAIN: 'Muscle Gain',
  MAINTENANCE: 'Maintenance',
  RECOMPOSITION: 'Body Recomposition',
  ATHLETIC_PERFORMANCE: 'Athletic Performance'
};

export const ACTIVITY_LABELS: Record<ActivityLevel, string> = {
  SEDENTARY: 'Sedentary (Desk Job)',
  LIGHT: 'Light (1-2 days/wk)',
  MODERATE: 'Moderate (3-5 days/wk)',
  ACTIVE: 'Active (6-7 days/wk)',
  ATHLETE: 'Athlete (2x daily)'
};

export function calculateBmr(weightKg: number, heightCm: number, age: number, gender: Gender): number {
  const base = 10 * weightKg + 6.25 * heightCm - 5 * age;
  return Math.round(gender === 'MALE' ? base + 5 : base - 161);
}

export function calculateTdee(profile: Pick<UserProfile, 'currentWeightKg' | 'heightCm' | 'age' | 'gender' | 'activityLevel'>): number {
  const bmr = calculateBmr(profile.currentWeightKg, profile.heightCm, profile.age, profile.gender);
  return Math.round(bmr * ACTIVITY_MULTIPLIERS[profile.activityLevel]);
}

export function calculateTargets(profile: UserProfile): {
  tdeeKcal: number;
  targetKcal: number;
  targetProteinG: number;
  targetCarbsG: number;
  targetFatG: number;
  feasibilityNote: string;
  isFeasible: boolean;
} {
  const tdee = calculateTdee(profile);
  const weightDiff = profile.targetWeightKg - profile.currentWeightKg;
  const weeks = Math.max(1, profile.targetTimelineWeeks);
  const weeklyPaceKg = weightDiff / weeks;

  let calorieDelta = Math.round((weeklyPaceKg * 7700) / 7);
  if (profile.primaryGoal === 'FAT_LOSS' && calorieDelta > -300) calorieDelta = -450;
  if (profile.primaryGoal === 'MUSCLE_GAIN' && calorieDelta < 200) calorieDelta = 300;
  if (profile.primaryGoal === 'MAINTENANCE' || profile.primaryGoal === 'RECOMPOSITION') calorieDelta = 0;

  const minSafe = profile.gender === 'MALE' ? 1500 : 1200;
  const targetKcal = Math.max(minSafe, tdee + calorieDelta);

  const proteinPerKg = profile.primaryGoal === 'FAT_LOSS' || profile.primaryGoal === 'RECOMPOSITION' ? 2.2 : 2.0;
  const targetProteinG = Math.round(profile.currentWeightKg * proteinPerKg);
  const targetFatG = Math.round((targetKcal * 0.25) / 9);
  const proteinAndFatKcal = targetProteinG * 4 + targetFatG * 9;
  const targetCarbsG = Math.max(50, Math.round((targetKcal - proteinAndFatKcal) / 4));

  const maxSafeWeekly = profile.primaryGoal === 'FAT_LOSS' ? -1.0 : 0.5;
  const isFeasible =
    profile.primaryGoal === 'FAT_LOSS'
      ? weeklyPaceKg >= maxSafeWeekly
      : profile.primaryGoal === 'MUSCLE_GAIN'
        ? weeklyPaceKg <= maxSafeWeekly
        : true;

  const feasibilityNote = isFeasible
    ? `Safe pace (${weeklyPaceKg >= 0 ? '+' : ''}${weeklyPaceKg.toFixed(2)} kg/wk over ${weeks} wks)`
    : `Aggressive pace (${weeklyPaceKg.toFixed(2)} kg/wk) — consider extending timeline for lean retention.`;

  return {
    tdeeKcal: tdee,
    targetKcal,
    targetProteinG,
    targetCarbsG,
    targetFatG,
    feasibilityNote,
    isFeasible
  };
}

export function estimate1RmEpley(weightKg: number, reps: number): number {
  if (reps <= 0 || weightKg <= 0) return 0;
  if (reps === 1) return weightKg;
  return Math.round(weightKg * (1 + reps / 30) * 10) / 10;
}
