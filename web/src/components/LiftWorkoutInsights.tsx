import React, { useMemo, useState } from 'react';
import { CheckCircle2, Clock, Dumbbell, Layers, Play, Scale, X } from 'lucide-react';
import { DailyRecording, UserProfile, WorkoutSession } from '../types/models';

interface LiftWorkoutInsightsProps {
  sessions: WorkoutSession[];
  profile: UserProfile;
  dailyRecordings: DailyRecording[];
  onStartWorkout: () => void;
}

const formatCompact = (val: number): string => {
  if (val >= 1000) return `${(val / 1000).toFixed(1)}k`;
  return val > 0 ? `${Math.round(val)}` : '0';
};

export const LiftWorkoutInsights: React.FC<LiftWorkoutInsightsProps> = ({
  sessions,
  profile,
  dailyRecordings,
  onStartWorkout
}) => {
  const todayStr = new Date().toISOString().slice(0, 10);
  const todaySessions = sessions.filter((s) => s.date === todayStr);
  const latest = todaySessions[0] ?? sessions[0];

  const [detailType, setDetailType] = useState<'WORKOUTS' | 'REPS' | 'VOLUME' | null>(null);

  const stats = useMemo(() => {
    let sets = 0;
    let reps = 0;
    let volume = 0;
    let maxWeight = 0;
    let totalMinutes = 0;

    sessions.forEach((s) => {
      totalMinutes += s.durationMinutes || 0;
      s.exercises.forEach((ex) => {
        sets += ex.sets.length;
        ex.sets.forEach((st) => {
          reps += st.reps;
          volume += st.weightKg * st.reps;
          if (st.weightKg > maxWeight) maxWeight = st.weightKg;
        });
      });
    });
    return {
      workouts: sessions.length,
      sets,
      reps,
      volume,
      maxWeight,
      totalMinutes
    };
  }, [sessions]);

  const last30Dates = useMemo(() => {
    const arr: string[] = [];
    const base = new Date();
    for (let i = 29; i >= 0; i--) {
      const d = new Date(base);
      d.setDate(base.getDate() - i);
      arr.push(d.toISOString().slice(0, 10));
    }
    return arr;
  }, []);

  const activeSet = useMemo(() => {
    const set = new Set<string>();
    sessions.forEach((s) => set.add(s.date));
    dailyRecordings.forEach((r) => {
      if (r.workoutCount > 0 || r.steps > 1000) set.add(r.date);
    });
    return set;
  }, [sessions, dailyRecordings]);

  const activeDaysCount = last30Dates.filter((d) => activeSet.has(d)).length;

  const weeklyReps = useMemo(() => {
    const base = new Date();
    const out: { label: string; reps: number }[] = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date(base);
      d.setDate(base.getDate() - i);
      const dStr = d.toISOString().slice(0, 10);
      const dayReps = sessions
        .filter((s) => s.date === dStr)
        .reduce(
          (acc, s) =>
            acc +
            s.exercises.reduce(
              (eAcc, ex) => eAcc + ex.sets.reduce((sAcc, st) => sAcc + st.reps, 0),
              0
            ),
          0
        );
      out.push({
        label: d.toLocaleDateString('en-US', { weekday: 'narrow' }),
        reps: dayReps
      });
    }
    return out;
  }, [sessions]);

  const maxDayReps = Math.max(1, ...weeklyReps.map((w) => w.reps));
  const weekTotalReps = weeklyReps.reduce((acc, w) => acc + w.reps, 0);

  const volumeSeries = useMemo(() => {
    const pts = sessions
      .slice(0, 8)
      .reverse()
      .map((s) =>
        s.exercises.reduce(
          (acc, ex) => acc + ex.sets.reduce((sAcc, st) => sAcc + st.weightKg * st.reps, 0),
          0
        )
      );
    if (pts.length === 1) return [pts[0], pts[0]];
    return pts;
  }, [sessions]);

  const maxVol = Math.max(1, ...volumeSeries);
  const diffKg = Math.abs(profile.targetWeightKg - profile.currentWeightKg);
  const weightPct = Math.max(8, Math.min(100, Math.round((1 - diffKg / 15) * 100)));

  const heroSets = latest
    ? latest.exercises.reduce((acc, ex) => acc + ex.sets.length, 0)
    : 0;
  const heroVol = latest
    ? latest.exercises.reduce(
        (acc, ex) => acc + ex.sets.reduce((sAcc, st) => sAcc + st.weightKg * st.reps, 0),
        0
      )
    : 0;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14, marginBottom: 16 }}>
      {/* 1. Today's Workout Hero Card */}
      <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 12, fontWeight: 700 }} className="muted">Today</span>
          <span
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 5,
              fontSize: 11,
              fontWeight: 800,
              padding: '4px 10px',
              borderRadius: 999,
              background: todaySessions.length > 0 ? '#10b98122' : 'var(--surface-2)',
              color: todaySessions.length > 0 ? '#10b981' : 'var(--text-muted)'
            }}
          >
            {todaySessions.length > 0 ? <CheckCircle2 size={13} /> : <Clock size={13} />}
            {todaySessions.length > 0 ? 'Completed' : 'Ready'}
          </span>
        </div>

        <div>
          <div style={{ fontSize: 24, fontWeight: 900 }}>
            {latest ? latest.title : 'Start Training'}
          </div>
          <div style={{ fontSize: 12 }} className="muted">
            {latest
              ? Array.from(new Set(latest.exercises.map((e) => e.muscleGroup))).join(', ')
              : 'Log your first session'}
          </div>
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <span className="version-pill" style={{ cursor: 'default' }}>{heroSets} sets</span>
          <span className="version-pill" style={{ cursor: 'default' }}>{formatCompact(heroVol)} kg</span>
        </div>

        <button
          type="button"
          className="btn btn-primary"
          onClick={onStartWorkout}
          style={{ borderRadius: 999, padding: '12px 16px', fontSize: 14 }}
        >
          <Play size={15} /> {todaySessions.length > 0 ? 'Start another workout' : 'Start workout'}
        </button>
      </div>

      {/* 2. 3-Column Stat Pills */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, minmax(0, 1fr))', gap: 10 }}>
        <button
          type="button"
          className="card"
          onClick={() => setDetailType('WORKOUTS')}
          style={{
            padding: 12,
            textAlign: 'center',
            cursor: 'pointer',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 4
          }}
        >
          <Dumbbell size={16} color="var(--accent)" />
          <strong style={{ fontSize: 20 }}>{stats.workouts}</strong>
          <span style={{ fontSize: 11 }} className="muted">workouts</span>
        </button>

        <button
          type="button"
          className="card"
          onClick={() => setDetailType('REPS')}
          style={{
            padding: 12,
            textAlign: 'center',
            cursor: 'pointer',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 4
          }}
        >
          <Layers size={16} color="#38bdf8" />
          <strong style={{ fontSize: 20 }}>{stats.sets}</strong>
          <span style={{ fontSize: 11 }} className="muted">{stats.reps} reps</span>
        </button>

        <button
          type="button"
          className="card"
          onClick={() => setDetailType('VOLUME')}
          style={{
            padding: 12,
            textAlign: 'center',
            cursor: 'pointer',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 4
          }}
        >
          <Scale size={16} color="#10b981" />
          <strong style={{ fontSize: 20 }}>{formatCompact(stats.volume)}</strong>
          <span style={{ fontSize: 11 }} className="muted">kg volume</span>
        </button>
      </div>

      {/* 3. Last 30 Days Consistency Heatmap */}
      <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <strong style={{ fontSize: 14 }}>Last 30 days</strong>
          <span style={{ fontSize: 12, fontWeight: 700, color: 'var(--accent)' }}>
            {activeDaysCount} / 30 active
          </span>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(10, minmax(0, 1fr))', gap: 6 }}>
          {last30Dates.map((dStr) => {
            const isToday = dStr === todayStr;
            const isActive = activeSet.has(dStr);
            return (
              <div
                key={dStr}
                title={dStr}
                style={{
                  height: 16,
                  borderRadius: 5,
                  background: isToday
                    ? 'var(--accent)'
                    : isActive
                      ? 'var(--text)'
                      : 'var(--surface-2)',
                  opacity: isToday || isActive ? 0.9 : 0.7
                }}
              />
            );
          })}
        </div>
      </div>

      {/* 4. Volume Trend & This Week Daily Reps */}
      <div className="grid-2">
        <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <span style={{ fontSize: 11, fontWeight: 700 }} className="muted">Volume</span>
          <strong style={{ fontSize: 18 }}>{formatCompact(stats.volume)} kg</strong>
          <svg viewBox="0 0 120 44" style={{ width: '100%', height: 44 }}>
            {volumeSeries.length > 0 && (
              <polyline
                fill="none"
                stroke="var(--accent)"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
                points={volumeSeries
                  .map((v, idx) => {
                    const x = (idx / Math.max(1, volumeSeries.length - 1)) * 116 + 2;
                    const y = 40 - (v / maxVol) * 34;
                    return `${x},${y}`;
                  })
                  .join(' ')}
              />
            )}
          </svg>
        </div>

        <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <span style={{ fontSize: 11, fontWeight: 700 }} className="muted">This week</span>
          <strong style={{ fontSize: 18 }}>{weekTotalReps} reps</strong>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 4, alignItems: 'end', height: 44 }}>
            {weeklyReps.map((d, idx) => {
              const h = d.reps > 0 ? Math.max(6, Math.round((d.reps / maxDayReps) * 30)) : 4;
              return (
                <div key={idx} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2 }}>
                  <div
                    style={{
                      width: '100%',
                      height: h,
                      borderRadius: 3,
                      background: idx === weeklyReps.length - 1 ? 'var(--accent)' : 'var(--text-muted)'
                    }}
                  />
                  <span style={{ fontSize: 9 }} className="muted">{d.label}</span>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* 5. Body Weight Goal Card */}
      <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: 11 }} className="muted">Body weight</div>
            <strong style={{ fontSize: 20 }}>{profile.currentWeightKg} kg</strong>
          </div>
          <span style={{ fontSize: 12, fontWeight: 700, color: 'var(--accent)' }}>
            Goal {profile.targetWeightKg} kg • {diffKg < 0.2 ? 'Reached' : `${diffKg.toFixed(1)} kg to go`}
          </span>
        </div>
        <div className="progress-track">
          <div className="progress-fill" style={{ width: `${weightPct}%`, background: 'var(--accent)' }} />
        </div>
      </div>

      {/* Interactive Stat Detail Modal */}
      {detailType && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.68)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 16,
            zIndex: 60
          }}
        >
          <div className="card" style={{ maxWidth: 380, width: '100%' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
              <strong style={{ fontSize: 16 }}>
                {detailType === 'WORKOUTS'
                  ? 'Workouts Logged'
                  : detailType === 'REPS'
                    ? 'Reps & Sets Logged'
                    : 'Total Volume'}
              </strong>
              <button className="btn" aria-label="Close" onClick={() => setDetailType(null)} style={{ padding: 6 }}>
                <X size={16} />
              </button>
            </div>
            <div style={{ fontSize: 28, fontWeight: 900, marginBottom: 6 }}>
              {detailType === 'WORKOUTS'
                ? `${stats.workouts} sessions`
                : detailType === 'REPS'
                  ? `${stats.reps} reps`
                  : `${formatCompact(stats.volume)} kg`}
            </div>
            <div style={{ fontSize: 13 }} className="muted">
              {detailType === 'WORKOUTS'
                ? `${stats.totalMinutes} total minutes trained.`
                : detailType === 'REPS'
                  ? `${stats.sets} sets completed (avg ${stats.sets > 0 ? (stats.reps / stats.sets).toFixed(1) : '0'} reps/set).`
                  : `Heaviest single set: ${stats.maxWeight} kg.`}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
