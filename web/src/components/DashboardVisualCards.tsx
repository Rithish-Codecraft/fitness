import React from 'react';
import { Calendar, CloudSync, Droplets, Dumbbell, Flame, Footprints, Timer, Utensils, Zap } from 'lucide-react';

interface DailyGoalHeroProps {
  consumedKcal: number;
  targetKcal: number;
  onBoltClick: () => void;
}

export const DailyGoalHeroCard: React.FC<DailyGoalHeroProps> = ({
  consumedKcal,
  targetKcal,
  onBoltClick
}) => {
  const pct = targetKcal > 0 ? Math.min(100, Math.round((consumedKcal / targetKcal) * 100)) : 0;

  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12 }}>
        <strong style={{ fontSize: 17 }}>Daily Goal: {pct}%</strong>
        <button
          type="button"
          aria-label="Log Workout"
          onClick={onBoltClick}
          style={{
            width: 42,
            height: 42,
            borderRadius: 999,
            border: 'none',
            background: 'var(--accent)',
            color: '#fff',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            flexShrink: 0
          }}
        >
          <Zap size={18} />
        </button>
      </div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <div className="progress-track" style={{ flex: 1 }}>
          <div className="progress-fill" style={{ width: `${pct}%`, background: 'var(--accent)' }} />
        </div>
        <span style={{ fontSize: 12, fontWeight: 700 }} className="muted">
          {Math.round(consumedKcal)}/{targetKcal}
        </span>
      </div>
    </div>
  );
};

interface QuickActionsRowProps {
  onWorkout: () => void;
  onMeal: () => void;
  onWater: () => void;
  onSync: () => void;
}

export const QuickActionsRow: React.FC<QuickActionsRowProps> = ({
  onWorkout,
  onMeal,
  onWater,
  onSync
}) => {
  const actions = [
    { label: 'Workout', icon: <Dumbbell size={18} />, color: 'var(--text)', onClick: onWorkout },
    { label: 'Meal', icon: <Utensils size={18} />, color: 'var(--text)', onClick: onMeal },
    { label: 'Water', icon: <Droplets size={18} />, color: '#38bdf8', onClick: onWater },
    { label: 'Sync', icon: <CloudSync size={18} />, color: 'var(--accent)', onClick: onSync }
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, minmax(0, 1fr))', gap: 10 }}>
      {actions.map((act) => (
        <button
          key={act.label}
          type="button"
          onClick={act.onClick}
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 6,
            background: 'none',
            border: 'none',
            color: 'var(--text)',
            cursor: 'pointer',
            padding: '4px 0'
          }}
        >
          <span
            style={{
              width: 52,
              height: 52,
              borderRadius: 999,
              background: 'var(--surface)',
              border: '1px solid var(--border)',
              color: act.color,
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            {act.icon}
          </span>
          <span style={{ fontSize: 11, fontWeight: 700 }} className="muted">
            {act.label}
          </span>
        </button>
      ))}
    </div>
  );
};

interface DailySummaryCardProps {
  steps: number | null;
  activeBurnKcal: number | null;
  consumedKcal: number;
  targetKcal: number;
  proteinG: number;
  targetProteinG: number;
  waterMl: number;
}

export const DailySummaryCard: React.FC<DailySummaryCardProps> = ({
  steps,
  activeBurnKcal,
  consumedKcal,
  targetKcal,
  proteinG,
  targetProteinG,
  waterMl
}) => {
  const remaining = Math.max(0, Math.round(targetKcal - consumedKcal));
  const pct = targetKcal > 0 ? Math.min(100, Math.round((consumedKcal / targetKcal) * 100)) : 0;

  return (
    <div className="card">
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: '1fr auto 1fr',
          alignItems: 'center',
          gap: 12
        }}
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <div>
            <div style={{ fontSize: 11 }} className="muted">Steps</div>
            <div style={{ fontSize: 18, fontWeight: 900 }}>{steps != null && steps > 0 ? steps : '-'}</div>
          </div>
          <div>
            <div style={{ fontSize: 11 }} className="muted">Protein</div>
            <div style={{ fontSize: 13, fontWeight: 800, color: 'var(--accent)' }}>
              {Math.round(proteinG)} / {targetProteinG}g
            </div>
          </div>
        </div>

        <div
          style={{
            width: 104,
            height: 104,
            borderRadius: 999,
            background: `conic-gradient(var(--accent) ${pct * 3.6}deg, var(--surface-2) 0deg)`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 9
          }}
        >
          <div
            style={{
              width: '100%',
              height: '100%',
              borderRadius: 999,
              background: 'var(--surface)',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              textAlign: 'center'
            }}
          >
            <span style={{ fontSize: 18, fontWeight: 900, lineHeight: 1.1 }}>{remaining}</span>
            <span style={{ fontSize: 9, fontWeight: 800, color: 'var(--accent)' }}>KCAL</span>
            <span style={{ fontSize: 9 }} className="muted">LEFT</span>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, textAlign: 'right' }}>
          <div>
            <div style={{ fontSize: 11 }} className="muted">Active Burn</div>
            <div style={{ fontSize: 18, fontWeight: 900 }}>
              {activeBurnKcal != null && activeBurnKcal > 0 ? `${Math.round(activeBurnKcal)} kcal` : '-'}
            </div>
          </div>
          <div>
            <div style={{ fontSize: 11 }} className="muted">Water</div>
            <div style={{ fontSize: 13, fontWeight: 800, color: '#38bdf8' }}>{waterMl} ml</div>
          </div>
        </div>
      </div>
    </div>
  );
};

interface ScheduledDietTachometerProps {
  currentKcal: number;
  targetKcal: number;
  proteinG: number;
  targetProteinG: number;
  fatG: number;
  targetFatG: number;
  carbsG: number;
  targetCarbsG: number;
  onCalendarClick: () => void;
}

export const ScheduledDietTachometerCard: React.FC<ScheduledDietTachometerProps> = ({
  currentKcal,
  targetKcal,
  proteinG,
  targetProteinG,
  fatG,
  targetFatG,
  carbsG,
  targetCarbsG,
  onCalendarClick
}) => {
  const totalTicks = 24;
  const pct = targetKcal > 0 ? Math.min(1, currentKcal / targetKcal) : 0;
  const filledTicks = Math.round(pct * totalTicks);

  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
        <strong style={{ fontSize: 16 }}>Scheduled Your Diet</strong>
        <button
          type="button"
          className="btn"
          onClick={onCalendarClick}
          style={{ padding: '6px 10px', fontSize: 11 }}
        >
          <Calendar size={13} /> Calendar
        </button>
      </div>

      {/* Semi-circular segmented gauge */}
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative', paddingTop: 6 }}>
        <svg viewBox="0 0 220 120" style={{ width: '100%', maxWidth: 240, overflow: 'visible' }}>
          {Array.from({ length: totalTicks }).map((_, i) => {
            const angle = Math.PI + (i / (totalTicks - 1)) * Math.PI;
            const r1 = 76;
            const r2 = 94;
            const x1 = 110 + r1 * Math.cos(angle);
            const y1 = 110 + r1 * Math.sin(angle);
            const x2 = 110 + r2 * Math.cos(angle);
            const y2 = 110 + r2 * Math.sin(angle);
            const active = i < filledTicks;
            return (
              <line
                key={i}
                x1={x1}
                y1={y1}
                x2={x2}
                y2={y2}
                stroke={active ? (i < totalTicks * 0.4 ? '#e85b37' : '#f59e0b') : 'var(--border)'}
                strokeWidth={5}
                strokeLinecap="round"
              />
            );
          })}
        </svg>
        <div style={{ marginTop: -46, textAlign: 'center', marginBottom: 8 }}>
          <Flame size={18} color="#f97316" />
          <div style={{ fontSize: 20, fontWeight: 900 }}>
            {Math.round(currentKcal)}/{targetKcal}
          </div>
          <div style={{ fontSize: 10, fontWeight: 800 }} className="muted">KCAL</div>
        </div>
      </div>

      {/* 3 Macro Bars */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, minmax(0, 1fr))', gap: 10 }}>
        <div>
          <div style={{ fontSize: 11 }} className="muted">Protein</div>
          <div style={{ fontSize: 12, fontWeight: 800, marginBottom: 4 }}>
            {Math.round(proteinG)}/{targetProteinG}g
          </div>
          <div className="progress-track">
            <div
              className="progress-fill"
              style={{
                width: `${targetProteinG > 0 ? Math.min(100, (proteinG / targetProteinG) * 100) : 0}%`,
                background: '#3b82f6'
              }}
            />
          </div>
        </div>
        <div>
          <div style={{ fontSize: 11 }} className="muted">Fat</div>
          <div style={{ fontSize: 12, fontWeight: 800, marginBottom: 4 }}>
            {Math.round(fatG)}/{targetFatG}g
          </div>
          <div className="progress-track">
            <div
              className="progress-fill"
              style={{
                width: `${targetFatG > 0 ? Math.min(100, (fatG / targetFatG) * 100) : 0}%`,
                background: '#10b981'
              }}
            />
          </div>
        </div>
        <div>
          <div style={{ fontSize: 11 }} className="muted">Carbs</div>
          <div style={{ fontSize: 12, fontWeight: 800, marginBottom: 4 }}>
            {Math.round(carbsG)}/{targetCarbsG}g
          </div>
          <div className="progress-track">
            <div
              className="progress-fill"
              style={{
                width: `${targetCarbsG > 0 ? Math.min(100, (carbsG / targetCarbsG) * 100) : 0}%`,
                background: '#a855f7'
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

interface ArchVaultTrioProps {
  caloriesKcal: number | null;
  timeMinutes: number | null;
  distanceMiles: string;
}

export const ArchVaultTrioCard: React.FC<ArchVaultTrioProps> = ({
  caloriesKcal,
  timeMinutes,
  distanceMiles
}) => {
  const pillars = [
    {
      label: 'Calories',
      value: caloriesKcal != null && caloriesKcal > 0 ? `${Math.round(caloriesKcal)}` : '-',
      unit: 'kcal',
      bg: '#64a6bd',
      icon: <Flame size={16} />
    },
    {
      label: 'Time',
      value: timeMinutes != null && timeMinutes > 0 ? `${timeMinutes}` : '-',
      unit: 'Min',
      bg: '#69b394',
      icon: <Timer size={16} />
    },
    {
      label: 'Distance',
      value: distanceMiles,
      unit: 'Mile',
      bg: '#dfbc5e',
      icon: <Footprints size={16} />
    }
  ];

  return (
    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, minmax(0, 1fr))', gap: 10 }}>
      {pillars.map((p) => (
        <div
          key={p.label}
          style={{
            background: p.bg,
            color: '#0f172a',
            borderRadius: '44px 44px 18px 18px',
            padding: '14px 10px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 8,
            textAlign: 'center'
          }}
        >
          <span
            style={{
              width: 36,
              height: 36,
              borderRadius: 999,
              background: '#0f172a',
              color: '#ffffff',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            {p.icon}
          </span>
          <span style={{ fontSize: 12, fontWeight: 700 }}>{p.label}</span>
          <span style={{ fontSize: 18, fontWeight: 900 }}>{p.value}</span>
          <span style={{ fontSize: 11, fontWeight: 600, opacity: 0.78 }}>{p.unit}</span>
        </div>
      ))}
    </div>
  );
};
