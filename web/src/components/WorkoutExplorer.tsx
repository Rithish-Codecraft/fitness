import React, { useMemo, useState } from 'react';
import { ArrowRight, Calendar, ChevronRight, Dumbbell, Flame, Timer } from 'lucide-react';

export interface WorkoutPlanTemplate {
  id: string;
  category: string;
  title: string;
  durationMin: number;
  daysLabel: string;
  exerciseName: string;
  muscleGroup: string;
  equipmentType: string;
  accentGradient: string;
}

const PLAN_TEMPLATES: WorkoutPlanTemplate[] = [
  {
    id: 'lower-body',
    category: 'STRENGTH SESSION',
    title: 'Lower Body Exercises',
    durationMin: 45,
    daysLabel: '45 Days',
    exerciseName: 'Barbell Back Squat',
    muscleGroup: 'QUADS',
    equipmentType: 'BARBELL',
    accentGradient: 'linear-gradient(135deg, #1b2232 0%, #0f131d 100%)'
  },
  {
    id: 'upper-power',
    category: 'HYPERTROPHY',
    title: 'Upper Body Power',
    durationMin: 30,
    daysLabel: '45 Days',
    exerciseName: 'Incline Barbell Bench Press',
    muscleGroup: 'CHEST',
    equipmentType: 'BARBELL',
    accentGradient: 'linear-gradient(135deg, #252038 0%, #12101d 100%)'
  },
  {
    id: 'deep-stabilizer',
    category: 'CORE & MOBILITY',
    title: 'Deep Stabilizer',
    durationMin: 45,
    daysLabel: '25 Days',
    exerciseName: 'Hanging Leg Raise',
    muscleGroup: 'CORE_ABS',
    equipmentType: 'BODYWEIGHT',
    accentGradient: 'linear-gradient(135deg, #182b29 0%, #0e1615 100%)'
  }
];

interface WorkoutExplorerProps {
  onSelectTemplate: (template: WorkoutPlanTemplate) => void;
}

export const WorkoutExplorer: React.FC<WorkoutExplorerProps> = ({ onSelectTemplate }) => {
  const [activeIndex, setActiveIndex] = useState(0);

  const weekDays = useMemo(() => {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0=Sun..6=Sat
    const mondayOffset = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    const labels = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return labels.map((label, i) => {
      const d = new Date(today);
      d.setDate(today.getDate() + mondayOffset + i);
      return {
        label,
        dayNum: d.getDate(),
        isToday: d.toDateString() === today.toDateString()
      };
    });
  }, []);

  const [selectedDay, setSelectedDay] = useState<number>(() => {
    const todayItem = weekDays.find((d) => d.isToday);
    return todayItem ? todayItem.dayNum : weekDays[0].dayNum;
  });

  const activePlan = PLAN_TEMPLATES[activeIndex % PLAN_TEMPLATES.length];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16, marginBottom: 16 }}>
      {/* Training Plan Week Strip */}
      <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <strong style={{ fontSize: 16 }}>Training Plan</strong>
          <Calendar size={16} className="muted" />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, minmax(0, 1fr))', gap: 6 }}>
          {weekDays.map((d) => {
            const isSelected = selectedDay === d.dayNum;
            return (
              <button
                key={d.label}
                type="button"
                onClick={() => setSelectedDay(d.dayNum)}
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  gap: 6,
                  padding: '8px 4px',
                  borderRadius: 16,
                  border: isSelected ? '1px solid var(--accent)' : '1px solid var(--border)',
                  background: isSelected ? 'var(--surface-2)' : 'transparent',
                  color: 'var(--text)',
                  cursor: 'pointer'
                }}
              >
                <span style={{ fontSize: 10, fontWeight: 700 }} className="muted">
                  {d.label}
                </span>
                <span
                  style={{
                    width: 28,
                    height: 28,
                    borderRadius: 999,
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: 12,
                    fontWeight: 800,
                    background: isSelected ? 'var(--accent)' : 'var(--surface-2)',
                    color: isSelected ? '#ffffff' : 'var(--text)'
                  }}
                >
                  {d.dayNum}
                </span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Exercise Process Hero Card */}
      <div
        key={activePlan.id}
        className="card pf-animate-in"
        style={{
          background: activePlan.accentGradient,
          color: '#ffffff',
          display: 'flex',
          flexDirection: 'column',
          gap: 14
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 8 }}>
          <span style={{ fontSize: 13, fontWeight: 700, opacity: 0.85 }}>Exercise Process</span>
          <span
            style={{
              fontSize: 10,
              fontWeight: 800,
              letterSpacing: 0.8,
              padding: '4px 10px',
              borderRadius: 999,
              background: 'rgba(255,255,255,0.14)',
              color: '#ffffff'
            }}
          >
            {activePlan.category}
          </span>
        </div>

        <div style={{ fontSize: 22, fontWeight: 900, lineHeight: 1.2 }}>{activePlan.title}</div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
          <span
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 5,
              fontSize: 11,
              fontWeight: 700,
              padding: '5px 10px',
              borderRadius: 999,
              background: 'rgba(255,255,255,0.1)'
            }}
          >
            <Timer size={13} /> {activePlan.durationMin} Min
          </span>
          <span
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 5,
              fontSize: 11,
              fontWeight: 700,
              padding: '5px 10px',
              borderRadius: 999,
              background: 'rgba(255,255,255,0.1)'
            }}
          >
            <Flame size={13} /> {activePlan.daysLabel}
          </span>
        </div>

        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
          <button
            type="button"
            className="btn"
            onClick={() => onSelectTemplate(activePlan)}
            style={{
              flex: 1,
              background: '#ffffff',
              color: '#111827',
              borderColor: 'transparent',
              padding: '10px 14px',
              borderRadius: 999
            }}
          >
            <ArrowRight size={15} /> Start Workout
          </button>
          <button
            type="button"
            className="btn"
            onClick={() => setActiveIndex((i) => (i + 1) % PLAN_TEMPLATES.length)}
            style={{
              background: 'rgba(255,255,255,0.14)',
              color: '#ffffff',
              borderColor: 'rgba(255,255,255,0.2)',
              padding: '10px 16px',
              borderRadius: 999
            }}
          >
            Next <ChevronRight size={15} />
          </button>
        </div>
      </div>

      {/* Activities for Beginners / Quick Templates */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <strong style={{ fontSize: 15 }}>Activities for Beginners</strong>
        </div>

        <div className="grid-2">
          {PLAN_TEMPLATES.slice(1).map((item) => (
            <button
              key={item.id}
              type="button"
              onClick={() => onSelectTemplate(item)}
              className="card pf-tap-card"
              style={{
                textAlign: 'left',
                cursor: 'pointer',
                display: 'flex',
                flexDirection: 'column',
                gap: 8,
                padding: 14,
                background: item.accentGradient,
                color: '#ffffff'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 10, fontWeight: 700, opacity: 0.85 }}>{item.durationMin} Min</span>
                <Dumbbell size={14} />
              </div>
              <div style={{ fontSize: 14, fontWeight: 800 }}>{item.title}</div>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
