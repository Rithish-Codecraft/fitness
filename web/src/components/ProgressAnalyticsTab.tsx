import React from 'react';
import {
  DailyRecording,
  PhoneSensorTelemetry,
  UserProfile,
  WorkoutSession
} from '../types/models';

export const MUSCLE_GROUPS = [
  'Chest / Pectorals',
  'Lats & Upper Back',
  'Shoulders / Deltoids',
  'Biceps',
  'Triceps',
  'Quadriceps',
  'Hamstrings',
  'Glutes',
  'Calves',
  'Abs & Core'
];

export const EQUIPMENT_TYPES = [
  'Barbell',
  'Dumbbells',
  'Cable Pulley Machine',
  'Pin-Selected Machine',
  'Plate-Loaded / Smith Machine',
  'Bodyweight / Calisthenics',
  'Treadmill / Bike / Rower',
  'Yoga Mat / Mobility'
];

interface ProgressAnalyticsTabProps {
  profile: UserProfile;
  workouts: WorkoutSession[];
  telemetry: PhoneSensorTelemetry;
  dailyRecordings: DailyRecording[];
}

export const ProgressAnalyticsTab: React.FC<ProgressAnalyticsTabProps> = ({
  profile,
  workouts,
  telemetry,
  dailyRecordings
}) => {
  // 1. Steps & Distance
  const steps = telemetry.stepCount ?? 0;
  const distKm =
    telemetry.distanceMeters != null && telemetry.distanceMeters > 0
      ? (telemetry.distanceMeters / 1000).toFixed(2)
      : null;

  // 2. Training Summary (Weekly / Logged)
  const totalKcalBurned = workouts.reduce(
    (sum, w) => sum + (w.kcalBurned ?? Math.round(w.durationMinutes * 6.5)),
    0
  );
  const totalTonnageKg = workouts.reduce(
    (sum, w) =>
      sum +
      w.exercises.reduce(
        (es, ex) => es + ex.sets.reduce((ss, s) => ss + s.weightKg * s.reps, 0),
        0
      ),
    0
  );
  const tonnageDisplay =
    totalTonnageKg >= 1000
      ? `${(totalTonnageKg / 1000).toFixed(1)}t`
      : `${Math.round(totalTonnageKg)} kg`;
  const totalMinutes = workouts.reduce((sum, w) => sum + w.durationMinutes, 0);

  // 3. BMI Spectrum
  const heightM = Math.max(0.5, profile.heightCm / 100);
  const bmi = Math.round((profile.currentWeightKg / (heightM * heightM)) * 10) / 10;
  const bmiBadge =
    bmi < 18.5
      ? { label: 'Underweight', rating: 'Low', color: '#38bdf8' }
      : bmi < 25
        ? { label: 'Healthy', rating: 'Good', color: '#10b981' }
        : bmi < 30
          ? { label: 'Overweight', rating: 'Elevated', color: '#f59e0b' }
          : { label: 'Obese', rating: 'High', color: '#ef4444' };
  const bmiPct = Math.min(96, Math.max(4, ((bmi - 15) / (35 - 15)) * 100));

  // 4. Kcal Burn per Session (chronological)
  const sessionBurns = [...workouts].reverse().slice(-7).map((w) => ({
    title: w.title,
    kcal: w.kcalBurned ?? Math.round(w.durationMinutes * 6.5)
  }));
  const maxBurn = Math.max(1, ...sessionBurns.map((s) => s.kcal));

  // 5. Muscle Group Volume (sets per group, optimal 10-20)
  const setsByMuscle: Record<string, number> = {};
  MUSCLE_GROUPS.forEach((m) => {
    setsByMuscle[m] = 0;
  });
  workouts.forEach((w) => {
    w.exercises.forEach((ex) => {
      const key =
        MUSCLE_GROUPS.find(
          (m) => m.toLowerCase() === ex.muscleGroup.toLowerCase()
        ) ?? ex.muscleGroup;
      setsByMuscle[key] = (setsByMuscle[key] ?? 0) + ex.sets.length;
    });
  });

  // 6. Equipment Tonnage Breakdown
  const tonnageByEquip: Record<string, number> = {};
  EQUIPMENT_TYPES.forEach((eq) => {
    tonnageByEquip[eq] = 0;
  });
  workouts.forEach((w) => {
    w.exercises.forEach((ex) => {
      const eq = ex.equipmentType || 'Barbell';
      const vol = ex.sets.reduce((s, set) => s + set.weightKg * set.reps, 0);
      tonnageByEquip[eq] = (tonnageByEquip[eq] ?? 0) + vol;
    });
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      {/* Row 1: Steps & Distance Sparklines */}
      <div className="grid-2">
        <div className="card">
          <div style={{ fontSize: 12 }} className="muted">
            Steps
          </div>
          <div style={{ fontSize: 22, fontWeight: 900, margin: '4px 0 12px' }}>
            {steps > 0 ? steps : '-'}
          </div>
          <div
            style={{
              display: 'flex',
              alignItems: 'flex-end',
              gap: 6,
              height: 40
            }}
          >
            {[0.25, 0.35, 0.25, 0.9, 0.65, 0.25, 0.18, 0.18].map((h, i) => (
              <div
                key={i}
                style={{
                  flex: 1,
                  height: `${steps > 0 ? Math.round(h * 100) : 14}%`,
                  borderRadius: 99,
                  background:
                    steps > 0 && (i === 3 || i === 4)
                      ? 'var(--text)'
                      : 'var(--border)'
                }}
              />
            ))}
          </div>
        </div>

        <div className="card">
          <div style={{ fontSize: 12 }} className="muted">
            Distance
          </div>
          <div style={{ fontSize: 22, fontWeight: 900, margin: '4px 0 12px' }}>
            {distKm != null ? `${distKm} km` : '-'}
          </div>
          <svg viewBox="0 0 160 40" width="100%" height="40">
            <path
              d={
                distKm != null
                  ? 'M 4 30 Q 25 14, 48 22 T 92 16 T 136 22 T 156 12'
                  : 'M 4 28 L 156 28'
              }
              fill="none"
              stroke={distKm != null ? '#e85b37' : 'var(--border)'}
              strokeWidth="3.5"
              strokeLinecap="round"
            />
          </svg>
        </div>
      </div>

      {/* Row 2: Training Summary (Weekly) */}
      <div className="card">
        <strong style={{ fontSize: 15, display: 'block', marginBottom: 12 }}>
          Training Summary (Weekly)
        </strong>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(4, minmax(0, 1fr))',
            gap: 8
          }}
        >
          <div className="sensor-tile" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 17, fontWeight: 900, color: '#e85b37' }}>
              {workouts.length > 0 ? totalKcalBurned : '-'}
            </div>
            <div style={{ fontSize: 9, fontWeight: 800, marginTop: 4 }} className="muted">
              KCAL BURNED
            </div>
          </div>
          <div className="sensor-tile" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 17, fontWeight: 900, color: '#e85b37' }}>
              {workouts.length > 0 ? tonnageDisplay : '-'}
            </div>
            <div style={{ fontSize: 9, fontWeight: 800, marginTop: 4 }} className="muted">
              TONNAGE
            </div>
          </div>
          <div className="sensor-tile" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 17, fontWeight: 900, color: '#e85b37' }}>
              {workouts.length > 0 ? `${totalMinutes}m` : '-'}
            </div>
            <div style={{ fontSize: 9, fontWeight: 800, marginTop: 4 }} className="muted">
              MIN TRAINED
            </div>
          </div>
          <div className="sensor-tile" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 17, fontWeight: 900, color: '#f59e0b' }}>
              {workouts.length > 0 ? workouts.length : '-'}
            </div>
            <div style={{ fontSize: 9, fontWeight: 800, marginTop: 4 }} className="muted">
              SESSIONS
            </div>
          </div>
        </div>
      </div>

      {/* Row 3: BMI Spectrum Card */}
      <div className="card">
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6 }}>
          <span style={{ fontSize: 14, fontWeight: 600 }}>Your Weight is</span>
          <span
            style={{
              background: '#16171b',
              color: '#ffffff',
              padding: '3px 10px',
              borderRadius: 999,
              fontSize: 12,
              fontWeight: 800
            }}
          >
            {bmiBadge.label}
          </span>
        </div>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginBottom: 12 }}>
          <span style={{ fontSize: 28, fontWeight: 900 }}>{bmi}</span>
          <span style={{ fontSize: 13, fontWeight: 800, color: bmiBadge.color }}>
            {bmiBadge.rating}
          </span>
        </div>
        <div style={{ position: 'relative', height: 10, borderRadius: 999, background: 'linear-gradient(90deg, #38bdf8 0%, #10b981 38%, #f59e0b 68%, #ef4444 100%)' }}>
          <div
            style={{
              position: 'absolute',
              top: '50%',
              left: `${bmiPct}%`,
              transform: 'translate(-50%, -50%)',
              width: 16,
              height: 16,
              borderRadius: 999,
              background: '#ffffff',
              border: `3px solid ${bmiBadge.color}`,
              boxShadow: '0 2px 6px rgba(0,0,0,0.35)'
            }}
          />
        </div>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            fontSize: 10,
            marginTop: 8
          }}
          className="muted"
        >
          <span>• Underweight</span>
          <span>• Healthy</span>
          <span>• Overweight</span>
          <span>• Obese</span>
        </div>
      </div>

      {/* Row 4: Kcal Burn per Session */}
      <div className="card">
        <strong style={{ fontSize: 15 }}>Kcal Burn per Session</strong>
        <div style={{ fontSize: 11, marginBottom: 14 }} className="muted">
          Calculated from logged workouts
        </div>
        {sessionBurns.length === 0 ? (
          <div className="sensor-tile" style={{ textAlign: 'center', fontSize: 12 }} >
            <span className="muted">No workouts logged yet — log a session to render burn bars.</span>
          </div>
        ) : (
          <div>
            <div
              style={{
                display: 'flex',
                alignItems: 'flex-end',
                gap: 14,
                height: 110,
                padding: '0 8px'
              }}
            >
              {sessionBurns.map((s, idx) => (
                <div
                  key={idx}
                  style={{
                    flex: 1,
                    height: `${Math.max(20, Math.round((s.kcal / maxBurn) * 100))}%`,
                    background: '#e85b37',
                    borderRadius: 10
                  }}
                  title={`${s.title}: ${s.kcal} kcal`}
                />
              ))}
            </div>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                fontSize: 11,
                marginTop: 8,
                fontWeight: 600
              }}
              className="muted"
            >
              <span>Earliest</span>
              <span>Latest ({sessionBurns[sessionBurns.length - 1].kcal} kcal)</span>
            </div>
          </div>
        )}
      </div>

      {/* Row 5 & 6: Muscle Group Volume + Equipment Tonnage Breakdown */}
      <div className="grid-main">
        <div className="card">
          <strong style={{ fontSize: 15 }}>Muscle Group Volume</strong>
          <div style={{ fontSize: 11, marginBottom: 14 }} className="muted">
            Logged working sets per target group (Optimal: 10–20 sets)
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {MUSCLE_GROUPS.map((group) => {
              const setsCount = setsByMuscle[group] ?? 0;
              return (
                <div key={group}>
                  <div
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      fontSize: 12,
                      marginBottom: 4
                    }}
                  >
                    <span style={{ fontWeight: 700 }}>{group}</span>
                    <span className="muted">{setsCount} Sets</span>
                  </div>
                  <div className="progress-track" style={{ height: 6 }}>
                    <div
                      className="progress-fill"
                      style={{
                        width: `${Math.min(100, (setsCount / 15) * 100)}%`,
                        background: setsCount >= 10 ? '#10b981' : 'var(--accent)'
                      }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="card">
            <strong style={{ fontSize: 15, display: 'block', marginBottom: 12 }}>
              Equipment Tonnage Breakdown
            </strong>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {EQUIPMENT_TYPES.map((eq) => (
                <div
                  key={eq}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    fontSize: 12
                  }}
                >
                  <span className="muted">{eq}</span>
                  <strong>{(tonnageByEquip[eq] ?? 0).toLocaleString()} kg</strong>
                </div>
              ))}
            </div>
          </div>

          {/* Row 7: Multi-Day Recordings */}
          <div className="card">
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: 10
              }}
            >
              <strong style={{ fontSize: 15 }}>Multi-Day Recordings</strong>
              <span style={{ fontSize: 12, fontWeight: 800, color: '#e85b37' }}>
                {dailyRecordings.length} {dailyRecordings.length === 1 ? 'day' : 'days'}
              </span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {dailyRecordings.slice(0, 7).map((rec, i) => (
                <div
                  key={`${rec.date}-${i}`}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    fontSize: 12
                  }}
                >
                  <strong>{rec.date}</strong>
                  <span className="muted">
                    {rec.steps} steps • {Math.round(rec.caloriesConsumed)} kcal • {rec.waterMl} ml
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
