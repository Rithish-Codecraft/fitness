import React, { useEffect, useState } from 'react';
import {
  Activity,
  BarChart3,
  Copy,
  Droplets,
  Dumbbell,
  Flame,
  Home,
  LogOut,
  Palette,
  Plus,
  Settings,
  Trash2,
  Utensils
} from 'lucide-react';
import {
  ActivityLevel,
  FoodItem,
  GoalType,
  LifestyleSleepAnalysis,
  MealType,
  ThemePalette,
  UserProfile,
  WorkoutSession
} from './types/models';
import {
  ACTIVITY_LABELS,
  GOAL_LABELS,
  calculateTargets,
  estimate1RmEpley
} from './engine/calculators';
import { useWebTelemetry } from './hooks/useWebTelemetry';
import { TelemetryCard } from './components/TelemetryCard';
import { LifestyleSleepCard } from './components/StorageAndLifestyleCards';
import { VersionSyncModal } from './components/VersionSyncModal';
import { AuthScreen } from './components/AuthScreen';
import { PersonalWizard } from './components/PersonalWizard';
import { LegalPage } from './components/LegalPages';
import {
  AuthAccount,
  UserDataBundle,
  completePersonalWizard,
  createMultiDeviceSyncKey,
  deleteAccountData,
  getActiveSession,
  loadUserData,
  logoutAccount,
  saveUserData
} from './auth/authManager';
import {
  derivePulseForgeId,
  loadCloudUserState,
  saveCloudUserState,
  supabase
} from './auth/supabaseClient';
import type { User as SupabaseUser } from '@supabase/supabase-js';

type TabId = 'dashboard' | 'nutrition' | 'workout' | 'progress' | 'settings';

function makeDefaultBundle(account: AuthAccount): UserDataBundle {
  return {
    profile: {
      id: account.userId,
      name: account.fullName,
      age: 25,
      gender: 'MALE',
      heightCm: 175,
      currentWeightKg: 72,
      targetWeightKg: 76,
      targetTimelineWeeks: 12,
      primaryGoal: 'MUSCLE_GAIN',
      activityLevel: 'MODERATE',
      tdeeKcal: 2350,
      targetKcal: 2650,
      targetProteinG: 160,
      targetCarbsG: 320,
      targetFatG: 75,
      waterTargetMl: 3000,
      waterConsumedMl: 0,
      activeTheme: 'POWER',
      isDarkMode: true
    },
    foods: [],
    workouts: [],
    lifestyle: {
      dailyScreenTimeMinutes: 0,
      nightScreenTimeMinutes: 0,
      mostUsedNightApp: '-',
      mostUsedNightAppMinutes: 0,
      estimatedBedtime: '-',
      estimatedWakeTime: '-',
      estimatedSleepDurationHours: null,
      sleepQualityScore: null,
      sleepDisruptionFeedback: '',
      sourceNote: 'Web'
    },
    lastSync: '-'
  };
}

export const App: React.FC = () => {
  const initialSession = getActiveSession();
  const [account, setAccount] = useState<AuthAccount | null>(initialSession.account);
  const [needsWizard, setNeedsWizard] = useState<boolean>(initialSession.needsWizard);

  const initialBundle: UserDataBundle | null = initialSession.account
    ? loadUserData(initialSession.account.userId, makeDefaultBundle(initialSession.account))
    : null;

  const [tab, setTab] = useState<TabId>('dashboard');
  const [profile, setProfile] = useState<UserProfile>(
    initialBundle?.profile ??
      makeDefaultBundle({
        userId: 'PF-000000',
        fullName: '',
        email: '',
        passwordHash: '',
        createdAt: ''
      }).profile
  );
  const [foods, setFoods] = useState<FoodItem[]>(initialBundle?.foods ?? []);
  const [workouts, setWorkouts] = useState<WorkoutSession[]>(initialBundle?.workouts ?? []);
  const [lifestyle, setLifestyle] = useState<LifestyleSleepAnalysis>(
    initialBundle?.lifestyle ??
      makeDefaultBundle({
        userId: 'PF-000000',
        fullName: '',
        email: '',
        passwordHash: '',
        createdAt: ''
      }).lifestyle
  );
  const [lastSync, setLastSync] = useState<string>(initialBundle?.lastSync ?? '-');
  const [versionModalOpen, setVersionModalOpen] = useState(false);
  const [syncCopiedMsg, setSyncCopiedMsg] = useState<string | null>(null);
  const [supabaseUser, setSupabaseUser] = useState<SupabaseUser | null>(null);

  const { telemetry, gpsActive, gpsError, enableBrowserGps, importTelemetrySnapshot } =
    useWebTelemetry();

  const today = new Date().toISOString().slice(0, 10);

  // Listen for Supabase Auth (Google OAuth & Email/Password) sessions
  useEffect(() => {
    const applySupabaseSession = async (user: SupabaseUser | null) => {
      setSupabaseUser(user);
      if (!user) return;
      const cloud = await loadCloudUserState(user);
      if (cloud.telemetry) {
        importTelemetrySnapshot(cloud.telemetry);
      }
      const displayName =
        (user.user_metadata?.full_name as string | undefined) ||
        (user.user_metadata?.name as string | undefined) ||
        user.email?.split('@')[0] ||
        'Athlete';
      const cloudAccount: AuthAccount = {
        userId: cloud.pulseforgeId || derivePulseForgeId(user.id),
        fullName: displayName,
        email: user.email || '',
        passwordHash: '',
        createdAt: user.created_at.slice(0, 16).replace('T', ' ')
      };
      handleAuthenticated(cloudAccount, cloud.isNewCloudProfile, cloud.bundle ?? undefined);
    };

    supabase.auth.getSession().then(({ data }) => {
      if (data.session?.user) {
        applySupabaseSession(data.session.user);
      }
    });

    const { data: sub } = supabase.auth.onAuthStateChange((_event, session) => {
      if (session?.user) {
        applySupabaseSession(session.user);
      }
    });

    return () => sub.subscription.unsubscribe();
  }, []);

  // Sync theme attribute to <html>
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', profile.activeTheme);
  }, [profile.activeTheme]);

  // Persist strictly isolated per-user data locally + to Supabase Cloud when signed in
  useEffect(() => {
    if (!account) return;
    const bundle: UserDataBundle = {
      profile,
      foods,
      workouts,
      lifestyle,
      lastSync
    };
    saveUserData(account.userId, bundle);

    if (supabaseUser && !needsWizard) {
      const timer = setTimeout(() => {
        saveCloudUserState(supabaseUser, account.userId, bundle, telemetry).catch(() => {});
      }, 800);
      return () => clearTimeout(timer);
    }
  }, [account, supabaseUser, needsWizard, profile, foods, workouts, lifestyle, lastSync, telemetry]);

  const handleAuthenticated = (
    nextAccount: AuthAccount,
    isNewSignUp: boolean,
    importedData?: Partial<UserDataBundle>
  ) => {
    const defaults = makeDefaultBundle(nextAccount);
    const loaded = loadUserData(nextAccount.userId, defaults);
    const mergedProfile: UserProfile = {
      ...loaded.profile,
      ...(importedData?.profile ?? {}),
      id: nextAccount.userId,
      name: nextAccount.fullName
    };
    setProfile(mergedProfile);
    setFoods(importedData?.foods ?? loaded.foods);
    setWorkouts(importedData?.workouts ?? loaded.workouts);
    setLifestyle(importedData?.lifestyle ?? loaded.lifestyle);
    setLastSync(importedData?.lastSync ?? loaded.lastSync);
    setAccount(nextAccount);
    setNeedsWizard(isNewSignUp);
    setTab('dashboard');
  };

  const handleLogout = async () => {
    await supabase.auth.signOut().catch(() => {});
    logoutAccount();
    setSupabaseUser(null);
    setAccount(null);
    setNeedsWizard(false);
  };

  // Manual food form state
  const [mealType, setMealType] = useState<MealType>('LUNCH');
  const [foodName, setFoodName] = useState('');
  const [foodKcal, setFoodKcal] = useState('');
  const [foodP, setFoodP] = useState('');
  const [foodC, setFoodC] = useState('');
  const [foodF, setFoodF] = useState('');

  // Manual workout form state
  const [workoutTitle, setWorkoutTitle] = useState('');
  const [exerciseName, setExerciseName] = useState('');
  const [weightKg, setWeightKg] = useState('');
  const [reps, setReps] = useState('');
  const [durationMin, setDurationMin] = useState('45');

  const pathname = typeof window !== 'undefined' ? window.location.pathname : '/';
  if (pathname === '/privacy' || pathname === '/terms') {
    return (
      <LegalPage
        kind={pathname === '/privacy' ? 'privacy' : 'terms'}
        onBack={() => {
          window.history.pushState({}, '', '/');
          window.location.reload();
        }}
      />
    );
  }

  // Gate 1: Always require Sign In / Sign Up if no active session
  if (!account) {
    return (
      <AuthScreen
        versionName={__APP_VERSION_MANIFEST__.versionName}
        versionCode={__APP_VERSION_MANIFEST__.versionCode}
        onAuthenticated={handleAuthenticated}
      />
    );
  }

  // Gate 2: Complete Personal Details Wizard on brand-new Sign Up
  if (needsWizard) {
    return (
      <PersonalWizard
        account={account}
        initialProfile={profile}
        onComplete={(completedProfile) => {
          setProfile(completedProfile);
          completePersonalWizard(account.userId);
          setNeedsWizard(false);
        }}
      />
    );
  }

  const todayFoods = foods.filter((f) => f.date === today);
  const consumedKcal = todayFoods.reduce((acc, f) => acc + f.calories, 0);
  const consumedP = todayFoods.reduce((acc, f) => acc + f.proteinG, 0);
  const consumedC = todayFoods.reduce((acc, f) => acc + f.carbsG, 0);
  const consumedF = todayFoods.reduce((acc, f) => acc + f.fatG, 0);

  const addFood = (e: React.FormEvent) => {
    e.preventDefault();
    if (!foodName.trim()) return;
    const item: FoodItem = {
      id: crypto.randomUUID(),
      mealType,
      name: foodName.trim(),
      quantity: 1,
      unit: 'serving',
      calories: Number(foodKcal) || 0,
      proteinG: Number(foodP) || 0,
      carbsG: Number(foodC) || 0,
      fatG: Number(foodF) || 0,
      date: today
    };
    setFoods((prev) => [item, ...prev]);
    setFoodName('');
    setFoodKcal('');
    setFoodP('');
    setFoodC('');
    setFoodF('');
  };

  const logWorkout = (e: React.FormEvent) => {
    e.preventDefault();
    if (!exerciseName.trim()) return;
    const w = Number(weightKg) || 0;
    const r = Number(reps) || 0;
    const session: WorkoutSession = {
      id: crypto.randomUUID(),
      date: today,
      title: workoutTitle.trim() || `${exerciseName.trim()} Session`,
      durationMinutes: Number(durationMin) || 45,
      notes: '',
      exercises: [
        {
          id: crypto.randomUUID(),
          exerciseName: exerciseName.trim(),
          muscleGroup: 'Strength',
          sets: [
            {
              id: crypto.randomUUID(),
              setNumber: 1,
              weightKg: w,
              reps: r,
              rpe: 8,
              estimated1RmKg: estimate1RmEpley(w, r)
            }
          ]
        }
      ]
    };
    setWorkouts((prev) => [session, ...prev]);
    setWorkoutTitle('');
    setExerciseName('');
    setWeightKg('');
    setReps('');
  };

  const addWater = (ml: number) => {
    setProfile((p) => ({ ...p, waterConsumedMl: p.waterConsumedMl + ml }));
  };

  const exportAllJson = () => {
    const bundle = {
      exportedAt: new Date().toISOString(),
      version: __APP_VERSION_MANIFEST__,
      authAccount: account,
      userProfile: profile,
      dailyNutrition: {
        date: today,
        foods: todayFoods,
        consumedCalories: consumedKcal,
        waterConsumedMl: profile.waterConsumedMl
      },
      workoutSessions: workouts,
      sensorTelemetry: telemetry,
      lifestyleSleepAnalysis: lifestyle
    };
    const blob = new Blob([JSON.stringify(bundle, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `pulseforge_${account.userId}_${today}.json`;
    a.click();
    URL.revokeObjectURL(url);
    setLastSync(new Date().toLocaleTimeString());
  };

  const importAndroidJson = async (file: File) => {
    try {
      const text = await file.text();
      const parsed = JSON.parse(text);
      if (parsed.userProfile) setProfile((prev) => ({ ...prev, ...parsed.userProfile, id: account.userId }));
      if (parsed.dailyScreenTimeMinutes !== undefined || parsed.nightScreenTimeMinutes !== undefined) {
        setLifestyle((prev) => ({ ...prev, ...parsed }));
      }
      if (parsed.lifestyleSleepAnalysis) {
        setLifestyle((prev) => ({ ...prev, ...parsed.lifestyleSleepAnalysis }));
      }
      if (parsed.stepCount !== undefined || parsed.distanceMeters !== undefined) {
        importTelemetrySnapshot({
          stepCount: parsed.stepCount ?? null,
          stepCadenceSpm: parsed.stepCadenceSpm ?? null,
          distanceMeters: parsed.distanceMeters ?? null,
          speedKmh: parsed.speedKmh ?? null,
          paceMinPerKm: parsed.paceMinPerKm ?? null,
          elevationGainMeters: parsed.elevationGainMeters ?? null,
          floorsClimbed: parsed.floorsClimbed ?? null,
          activeCaloriesBurned: parsed.activeCaloriesBurned ?? null,
          gpsRoute: parsed.gpsRoute ?? [],
          sleepStartTime: parsed.sleepEstimate?.sleepStartTime ?? '-',
          wakeTime: parsed.sleepEstimate?.wakeTime ?? '-',
          sleepTotalHours: parsed.sleepEstimate?.totalHours ?? null
        });
      }
      if (parsed.sensorTelemetry) {
        importTelemetrySnapshot(parsed.sensorTelemetry);
      }
      setLastSync(`Imported ${file.name}`);
    } catch {
      setLastSync('Invalid JSON file');
    }
  };

  const copySyncKey = async () => {
    const key = createMultiDeviceSyncKey(account, {
      profile,
      foods,
      workouts,
      lifestyle,
      lastSync
    });
    await navigator.clipboard.writeText(key);
    setSyncCopiedMsg('Multi-Device Sync Key copied! Paste it under "Sync Device" on any browser.');
  };

  const targetsCalc = calculateTargets(profile);

  return (
    <div className="app-shell">
      {/* Top Bar */}
      <header className="top-header">
        <div className="brand-group">
          <img src="/app_logo.png" alt="PulseForge AI" className="brand-logo" />
          <div>
            <div style={{ fontSize: 16, fontWeight: 900 }}>
              Good day, {account.fullName}
            </div>
            <div style={{ fontSize: 12 }} className="muted">
              {account.userId} • {GOAL_LABELS[profile.primaryGoal]} • {profile.currentWeightKg} kg
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <button
            className="version-pill"
            onClick={() => setVersionModalOpen(true)}
            title="Unified APK & Web Version Control"
          >
            <Activity size={14} color="#10b981" />
            v{__APP_VERSION_MANIFEST__.versionName} (Build {__APP_VERSION_MANIFEST__.versionCode})
          </button>
          <button className="btn" onClick={() => setTab('settings')} title="Themes & Profile">
            <Palette size={15} />
          </button>
          <button className="btn" onClick={handleLogout} title="Sign Out">
            <LogOut size={15} color="#ef4444" />
          </button>
        </div>
      </header>

      {/* TAB 1: DASHBOARD */}
      {tab === 'dashboard' && (
        <div className="grid-main">
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <TelemetryCard
              telemetry={telemetry}
              gpsActive={gpsActive}
              gpsError={gpsError}
              onEnableGps={enableBrowserGps}
            />
            <LifestyleSleepCard analysis={lifestyle} />
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {/* Daily Macros Card */}
            <div className="card">
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  marginBottom: 12
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Flame size={18} color="#e85b37" />
                  <strong style={{ fontSize: 15 }}>Daily Nutrition Budget</strong>
                </div>
                <button className="btn" onClick={() => setTab('nutrition')}>
                  + Log Meal
                </button>
              </div>
              <div style={{ fontSize: 24, fontWeight: 900 }}>
                {Math.round(consumedKcal)} / {profile.targetKcal}{' '}
                <span style={{ fontSize: 12 }} className="muted">
                  kcal
                </span>
              </div>
              <div className="progress-track" style={{ margin: '10px 0 12px' }}>
                <div
                  className="progress-fill"
                  style={{
                    width: `${Math.min(100, (consumedKcal / Math.max(1, profile.targetKcal)) * 100)}%`,
                    background: 'var(--accent)'
                  }}
                />
              </div>
              <div className="grid-2" style={{ gap: 8 }}>
                <div className="sensor-tile">
                  <div style={{ fontSize: 10 }} className="muted">Protein</div>
                  <div style={{ fontSize: 14, fontWeight: 800 }}>
                    {Math.round(consumedP)} / {profile.targetProteinG}g
                  </div>
                </div>
                <div className="sensor-tile">
                  <div style={{ fontSize: 10 }} className="muted">Carbs / Fat</div>
                  <div style={{ fontSize: 14, fontWeight: 800 }}>
                    {Math.round(consumedC)}g / {Math.round(consumedF)}g
                  </div>
                </div>
              </div>
            </div>

            {/* Hydration Card */}
            <div className="card">
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  marginBottom: 10
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Droplets size={18} color="#38bdf8" />
                  <strong style={{ fontSize: 15 }}>Hydration</strong>
                </div>
                <span style={{ fontSize: 13, fontWeight: 800, color: '#38bdf8' }}>
                  {profile.waterConsumedMl} / {profile.waterTargetMl} ml
                </span>
              </div>
              <div className="progress-track" style={{ marginBottom: 12 }}>
                <div
                  className="progress-fill"
                  style={{
                    width: `${Math.min(100, (profile.waterConsumedMl / Math.max(1, profile.waterTargetMl)) * 100)}%`,
                    background: '#38bdf8'
                  }}
                />
              </div>
              <div style={{ display: 'flex', gap: 8 }}>
                <button className="btn" onClick={() => addWater(250)} style={{ flex: 1 }}>
                  <Plus size={14} /> 250 ml
                </button>
                <button className="btn" onClick={() => addWater(500)} style={{ flex: 1 }}>
                  <Plus size={14} /> 500 ml
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: NUTRITION */}
      {tab === 'nutrition' && (
        <div className="grid-main">
          <form className="card" onSubmit={addFood} style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <strong style={{ fontSize: 16 }}>Log Food Item</strong>
            <select
              className="input"
              value={mealType}
              onChange={(e) => setMealType(e.target.value as MealType)}
            >
              <option value="BREAKFAST">Breakfast</option>
              <option value="LUNCH">Lunch</option>
              <option value="DINNER">Dinner</option>
              <option value="SNACK">Snack</option>
            </select>
            <input
              className="input"
              placeholder="Food name (e.g. Chicken Breast 200g)"
              value={foodName}
              onChange={(e) => setFoodName(e.target.value)}
            />
            <div className="grid-2">
              <input
                className="input"
                type="number"
                placeholder="Calories (kcal)"
                value={foodKcal}
                onChange={(e) => setFoodKcal(e.target.value)}
              />
              <input
                className="input"
                type="number"
                placeholder="Protein (g)"
                value={foodP}
                onChange={(e) => setFoodP(e.target.value)}
              />
              <input
                className="input"
                type="number"
                placeholder="Carbs (g)"
                value={foodC}
                onChange={(e) => setFoodC(e.target.value)}
              />
              <input
                className="input"
                type="number"
                placeholder="Fat (g)"
                value={foodF}
                onChange={(e) => setFoodF(e.target.value)}
              />
            </div>
            <button className="btn btn-primary" type="submit">
              <Plus size={15} /> Add to Today&apos;s Log
            </button>
          </form>

          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <strong style={{ fontSize: 16 }}>Today&apos;s Meals ({todayFoods.length})</strong>
            {todayFoods.length === 0 ? (
              <p className="muted" style={{ fontSize: 13 }}>
                No meals logged yet.
              </p>
            ) : (
              todayFoods.map((item) => (
                <div
                  key={item.id}
                  className="sensor-tile"
                  style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
                >
                  <div>
                    <div style={{ fontSize: 13, fontWeight: 700 }}>{item.name}</div>
                    <div style={{ fontSize: 11 }} className="muted">
                      {item.mealType} • {item.calories} kcal • P:{item.proteinG}g C:{item.carbsG}g F:{item.fatG}g
                    </div>
                  </div>
                  <button
                    className="btn"
                    aria-label="Delete food item"
                    onClick={() => setFoods((prev) => prev.filter((f) => f.id !== item.id))}
                    style={{ padding: 6 }}
                  >
                    <Trash2 size={14} color="#ef4444" />
                  </button>
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* TAB 3: WORKOUT */}
      {tab === 'workout' && (
        <div className="grid-main">
          <form className="card" onSubmit={logWorkout} style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <strong style={{ fontSize: 16 }}>Log Strength / Workout Set</strong>
            <input
              className="input"
              placeholder="Session title (e.g. Upper Body Push)"
              value={workoutTitle}
              onChange={(e) => setWorkoutTitle(e.target.value)}
            />
            <input
              className="input"
              placeholder="Exercise (e.g. Bench Press)"
              value={exerciseName}
              onChange={(e) => setExerciseName(e.target.value)}
            />
            <div className="grid-2">
              <input
                className="input"
                type="number"
                placeholder="Weight (kg)"
                value={weightKg}
                onChange={(e) => setWeightKg(e.target.value)}
              />
              <input
                className="input"
                type="number"
                placeholder="Reps"
                value={reps}
                onChange={(e) => setReps(e.target.value)}
              />
            </div>
            <input
              className="input"
              type="number"
              placeholder="Duration (min)"
              value={durationMin}
              onChange={(e) => setDurationMin(e.target.value)}
            />
            <button className="btn btn-primary" type="submit">
              <Dumbbell size={15} /> Save Workout Session
            </button>
          </form>

          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <strong style={{ fontSize: 16 }}>Logged Sessions ({workouts.length})</strong>
            {workouts.length === 0 ? (
              <p className="muted" style={{ fontSize: 13 }}>
                No workouts logged yet.
              </p>
            ) : (
              workouts.map((w) => {
                const vol = w.exercises.reduce(
                  (acc, ex) => acc + ex.sets.reduce((sAcc, s) => sAcc + s.weightKg * s.reps, 0),
                  0
                );
                const best1Rm = Math.max(
                  0,
                  ...w.exercises.flatMap((ex) => ex.sets.map((s) => s.estimated1RmKg))
                );
                return (
                  <div key={w.id} className="sensor-tile">
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                      <strong style={{ fontSize: 13 }}>{w.title}</strong>
                      <span style={{ fontSize: 11 }} className="muted">{w.date}</span>
                    </div>
                    <div style={{ fontSize: 11, marginTop: 4 }} className="muted">
                      {w.durationMinutes} min • Volume: {vol} kg • Est 1RM: {best1Rm || '-'} kg
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}

      {/* TAB 4: PROGRESS */}
      {tab === 'progress' && (
        <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          <strong style={{ fontSize: 16 }}>Real Progress &amp; Adherence Analytics</strong>
          <div className="grid-2">
            <div className="sensor-tile">
              <div style={{ fontSize: 11 }} className="muted">Calorie Target Adherence</div>
              <div style={{ fontSize: 22, fontWeight: 900, marginTop: 4 }}>
                {consumedKcal > 0
                  ? `${Math.round((consumedKcal / Math.max(1, profile.targetKcal)) * 100)}%`
                  : '-'}
              </div>
            </div>
            <div className="sensor-tile">
              <div style={{ fontSize: 11 }} className="muted">Workouts Completed</div>
              <div style={{ fontSize: 22, fontWeight: 900, marginTop: 4 }}>
                {workouts.length > 0 ? workouts.length : '-'}
              </div>
            </div>
            <div className="sensor-tile">
              <div style={{ fontSize: 11 }} className="muted">Total Volume Lifted</div>
              <div style={{ fontSize: 22, fontWeight: 900, marginTop: 4 }}>
                {workouts.length > 0
                  ? `${workouts.reduce(
                      (a, w) =>
                        a +
                        w.exercises.reduce(
                          (ea, ex) => ea + ex.sets.reduce((sa, s) => sa + s.weightKg * s.reps, 0),
                          0
                        ),
                      0
                    )} kg`
                  : '-'}
              </div>
            </div>
            <div className="sensor-tile">
              <div style={{ fontSize: 11 }} className="muted">Goal Feasibility</div>
              <div style={{ fontSize: 13, fontWeight: 700, marginTop: 6 }}>
                {targetsCalc.feasibilityNote}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TAB 5: SETTINGS, ACCOUNT & THEMES */}
      {tab === 'settings' && (
        <div className="grid-main">
          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            <strong style={{ fontSize: 16 }}>Biometric Profile &amp; TDEE Engine</strong>
            <div className="grid-2">
              <input
                className="input"
                value={profile.name}
                onChange={(e) => setProfile({ ...profile, name: e.target.value })}
                placeholder="Name"
              />
              <input
                className="input"
                type="number"
                value={profile.age}
                onChange={(e) => setProfile({ ...profile, age: Number(e.target.value) || 25 })}
                placeholder="Age"
              />
              <input
                className="input"
                type="number"
                value={profile.currentWeightKg}
                onChange={(e) =>
                  setProfile({ ...profile, currentWeightKg: Number(e.target.value) || 70 })
                }
                placeholder="Current Weight (kg)"
              />
              <input
                className="input"
                type="number"
                value={profile.targetWeightKg}
                onChange={(e) =>
                  setProfile({ ...profile, targetWeightKg: Number(e.target.value) || 70 })
                }
                placeholder="Target Weight (kg)"
              />
            </div>

            <select
              className="input"
              value={profile.primaryGoal}
              onChange={(e) =>
                setProfile({ ...profile, primaryGoal: e.target.value as GoalType })
              }
            >
              {Object.entries(GOAL_LABELS).map(([k, label]) => (
                <option key={k} value={k}>
                  {label}
                </option>
              ))}
            </select>

            <select
              className="input"
              value={profile.activityLevel}
              onChange={(e) =>
                setProfile({ ...profile, activityLevel: e.target.value as ActivityLevel })
              }
            >
              {Object.entries(ACTIVITY_LABELS).map(([k, label]) => (
                <option key={k} value={k}>
                  {label}
                </option>
              ))}
            </select>

            <button
              className="btn btn-primary"
              onClick={() => {
                const next = calculateTargets(profile);
                setProfile((p) => ({
                  ...p,
                  tdeeKcal: next.tdeeKcal,
                  targetKcal: next.targetKcal,
                  targetProteinG: next.targetProteinG,
                  targetCarbsG: next.targetCarbsG,
                  targetFatG: next.targetFatG
                }));
              }}
            >
              Recalculate TDEE &amp; Macro Targets ({targetsCalc.targetKcal} kcal)
            </button>
          </div>

          <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            <strong style={{ fontSize: 16 }}>Account Security, Multi-Device Sync &amp; Themes</strong>
            <div className="sensor-tile">
              <div style={{ fontSize: 12, fontWeight: 800 }}>
                Signed in as {account.fullName} ({account.userId})
              </div>
              <div style={{ fontSize: 11 }} className="muted">
                {account.email} • Created {account.createdAt}
              </div>
            </div>

            <button className="btn" onClick={copySyncKey}>
              <Copy size={14} color="#38bdf8" /> Copy Multi-Device Sync Key (PFKEY)
            </button>
            {syncCopiedMsg && (
              <div style={{ fontSize: 11, color: '#10b981', fontWeight: 600 }}>{syncCopiedMsg}</div>
            )}

            <div className="grid-2">
              {(['POWER', 'LAVENDER', 'LONGEVITY', 'WELLNESS'] as ThemePalette[]).map((t) => (
                <button
                  key={t}
                  className={`btn ${profile.activeTheme === t ? 'btn-primary' : ''}`}
                  onClick={() => setProfile({ ...profile, activeTheme: t })}
                >
                  {t}
                </button>
              ))}
            </div>

            <button className="btn" onClick={() => setVersionModalOpen(true)}>
              Open Version Control (v{__APP_VERSION_MANIFEST__.versionName})
            </button>

            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn" onClick={handleLogout} style={{ flex: 1 }}>
                <LogOut size={14} color="#f59e0b" /> Sign Out
              </button>
              <button
                className="btn"
                style={{ flex: 1 }}
                onClick={() => {
                  deleteAccountData(account.userId);
                  setAccount(null);
                }}
              >
                <Trash2 size={14} color="#ef4444" /> Delete Account
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Bottom Floating Capsule Navigation */}
      <nav className="bottom-nav">
        <button
          aria-label="Home"
          className={`nav-item ${tab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setTab('dashboard')}
        >
          <Home size={18} />
          {tab === 'dashboard' && <span>Home</span>}
        </button>
        <button
          aria-label="Nutrition"
          className={`nav-item ${tab === 'nutrition' ? 'active' : ''}`}
          onClick={() => setTab('nutrition')}
        >
          <Utensils size={18} />
          {tab === 'nutrition' && <span>Nutrition</span>}
        </button>
        <button
          aria-label="Workout"
          className={`nav-item ${tab === 'workout' ? 'active' : ''}`}
          onClick={() => setTab('workout')}
        >
          <Dumbbell size={18} />
          {tab === 'workout' && <span>Workout</span>}
        </button>
        <button
          aria-label="Progress"
          className={`nav-item ${tab === 'progress' ? 'active' : ''}`}
          onClick={() => setTab('progress')}
        >
          <BarChart3 size={18} />
          {tab === 'progress' && <span>Progress</span>}
        </button>
        <button
          aria-label="Settings"
          className={`nav-item ${tab === 'settings' ? 'active' : ''}`}
          onClick={() => setTab('settings')}
        >
          <Settings size={18} />
          {tab === 'settings' && <span>Settings</span>}
        </button>
      </nav>

      <VersionSyncModal
        open={versionModalOpen}
        currentVersion={__APP_VERSION_MANIFEST__}
        onClose={() => setVersionModalOpen(false)}
      />
    </div>
  );
};
