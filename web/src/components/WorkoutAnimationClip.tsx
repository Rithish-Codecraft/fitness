import React, { useEffect, useState } from 'react';
import { Pause, Play } from 'lucide-react';
import { AnimationPattern, resolveAnimationForWorkout } from '../engine/exerciseCatalog';

interface WorkoutAnimationClipProps {
  exerciseName: string;
  muscleGroup: string;
  compact?: boolean;
}

function getLimbPose(pattern: AnimationPattern, c: number) {
  // Returns normalized coordinates inside 160x130 viewport
  switch (pattern) {
    case 'BENCH_PRESS':
      return {
        hip: [78, 92],
        shoulder: [54, 92],
        head: [38, 86],
        elbow: [58, 72 - c * 18],
        hand: [56, 56 - c * 28],
        knee: [104, 92],
        foot: [112, 114],
        barbell: true,
        bench: true
      };
    case 'CABLE_FLY':
      return {
        hip: [80, 74],
        shoulder: [80, 44],
        head: [80, 26],
        elbow: [54 + c * 18, 48 + c * 4],
        hand: [40 + c * 36, 52 + c * 4],
        knee: [74, 96],
        foot: [72, 116],
        cable: true
      };
    case 'LAT_PULLDOWN':
      return {
        hip: [80, 88],
        shoulder: [80, 56],
        head: [80, 38],
        elbow: [52 + c * 12, 34 + c * 26],
        hand: [60 + c * 10, 20 + c * 32],
        knee: [102, 88],
        foot: [104, 114],
        cable: true
      };
    case 'BARBELL_ROW':
      return {
        hip: [94, 72],
        shoulder: [58, 56],
        head: [44, 46],
        elbow: [58 + c * 8, 74 - c * 16],
        hand: [56, 94 - c * 22],
        knee: [98, 94],
        foot: [96, 116],
        barbell: true
      };
    case 'OVERHEAD_PRESS':
      return {
        hip: [80, 74],
        shoulder: [80, 44],
        head: [80, 26],
        elbow: [62, 52 - c * 20],
        hand: [68, 40 - c * 26],
        knee: [80, 96],
        foot: [80, 116],
        barbell: true
      };
    case 'LATERAL_RAISE':
      return {
        hip: [80, 74],
        shoulder: [80, 44],
        head: [80, 26],
        elbow: [66 - c * 22, 62 - c * 18],
        hand: [62 - c * 32, 76 - c * 32],
        knee: [80, 96],
        foot: [80, 116]
      };
    case 'BICEP_CURL':
      return {
        hip: [80, 74],
        shoulder: [80, 44],
        head: [80, 26],
        elbow: [78, 66],
        hand: [78 + c * 10, 86 - c * 36],
        knee: [80, 96],
        foot: [80, 116]
      };
    case 'TRICEP_PUSHDOWN':
      return {
        hip: [80, 74],
        shoulder: [80, 44],
        head: [80, 26],
        elbow: [72, 64],
        hand: [62, 54 + c * 28],
        knee: [80, 96],
        foot: [80, 116],
        cable: true
      };
    case 'SQUAT':
      return {
        hip: [82 + c * 14, 70 + c * 24],
        shoulder: [74 + c * 4, 42 + c * 20],
        head: [72 + c * 4, 24 + c * 20],
        elbow: [82, 52 + c * 20],
        hand: [78, 40 + c * 20],
        knee: [66 - c * 12, 94 + c * 4],
        foot: [78, 116],
        barbell: true
      };
    case 'LEG_PRESS':
      return {
        hip: [52, 92],
        shoulder: [40, 70],
        head: [34, 54],
        elbow: [48, 80],
        hand: [56, 86],
        knee: [78 - c * 10, 68 + c * 10],
        foot: [104 + c * 18, 54 - c * 10]
      };
    case 'ROMANIAN_DEADLIFT':
      return {
        hip: [84 + c * 14, 72 + c * 4],
        shoulder: [74 - c * 22, 44 + c * 18],
        head: [74 - c * 24, 26 + c * 18],
        elbow: [70 - c * 18, 60 + c * 14],
        hand: [68 - c * 16, 78 + c * 18],
        knee: [84, 96],
        foot: [84, 116],
        barbell: true
      };
    case 'HIP_THRUST':
      return {
        hip: [74, 96 - c * 22],
        shoulder: [46, 76],
        head: [36, 64],
        elbow: [58, 86],
        hand: [72, 92 - c * 20],
        knee: [104, 80 - c * 6],
        foot: [106, 112],
        barbell: true,
        bench: true
      };
    case 'CALF_RAISE':
      return {
        hip: [80, 72 - c * 8],
        shoulder: [80, 42 - c * 8],
        head: [80, 24 - c * 8],
        elbow: [72, 58 - c * 8],
        hand: [72, 72 - c * 8],
        knee: [80, 94 - c * 8],
        foot: [80, 116 - c * 8]
      };
    case 'CRUNCH_PLANK':
    default:
      return {
        hip: [82, 98],
        shoulder: [54 + c * 12, 94 - c * 20],
        head: [40 + c * 16, 88 - c * 22],
        elbow: [52 + c * 14, 82 - c * 16],
        hand: [46 + c * 14, 82 - c * 16],
        knee: [104, 84],
        foot: [118, 104]
      };
  }
}

export const WorkoutAnimationClip: React.FC<WorkoutAnimationClipProps> = ({
  exerciseName,
  muscleGroup,
  compact = false
}) => {
  const [playing, setPlaying] = useState(true);
  const [speed, setSpeed] = useState<number>(1);
  const [phase, setPhase] = useState(0);

  const info = resolveAnimationForWorkout(exerciseName, muscleGroup);

  useEffect(() => {
    if (!playing) return;
    let raf = 0;
    let last = performance.now();
    const tick = (now: number) => {
      const dt = (now - last) / 1000;
      last = now;
      setPhase((p) => (p + dt * speed * 0.55) % 1);
      raf = requestAnimationFrame(tick);
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [playing, speed]);

  const contraction = (1 - Math.cos(phase * Math.PI * 2)) * 0.5;
  const engagementPct = Math.round(24 + contraction * 76);
  const phaseLabel =
    contraction > 0.85
      ? 'PEAK CONTRACTION'
      : phase < 0.5
        ? 'CONCENTRIC • DRIVE'
        : 'ECCENTRIC • CONTROL';

  const pose = getLimbPose(info.animationType, contraction);
  const primaryGlow = `rgba(232, 91, 55, ${0.45 + contraction * 0.55})`;
  const isMuscle = (name: string) => info.primaryMuscle === name;
  const isSecondary = (name: string) => info.secondaryMuscles.includes(name);
  const fillFor = (name: string) =>
    isMuscle(name)
      ? primaryGlow
      : isSecondary(name)
        ? 'rgba(245, 158, 11, 0.55)'
        : 'rgba(148, 163, 184, 0.22)';

  return (
    <div
      style={{
        background: '#11131a',
        border: '1px solid #262a38',
        borderRadius: 16,
        padding: compact ? 10 : 14,
        color: '#f8fafc'
      }}
    >
      {/* Top Bar */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: 8,
          marginBottom: 8
        }}
      >
        <div>
          <div style={{ fontSize: 10, fontWeight: 800, letterSpacing: 0.9, color: '#94a3b8' }}>
            BIOMECHANICAL CLIP • {phaseLabel}
          </div>
          <div style={{ fontSize: 12, fontWeight: 800, color: '#e85b37' }}>
            Target: {info.primaryMuscle} ({engagementPct}% active)
          </div>
        </div>

        <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
          <button
            type="button"
            onClick={() => setSpeed((s) => (s === 1 ? 1.5 : s === 1.5 ? 0.75 : 1))}
            style={{
              background: '#1e2230',
              border: '1px solid #2e3447',
              color: '#e2e8f0',
              borderRadius: 8,
              padding: '3px 7px',
              fontSize: 10,
              fontWeight: 700,
              cursor: 'pointer'
            }}
          >
            {speed}x
          </button>
          <button
            type="button"
            onClick={() => setPlaying((p) => !p)}
            style={{
              background: '#e85b37',
              border: 'none',
              color: '#ffffff',
              borderRadius: 8,
              padding: '4px 7px',
              display: 'inline-flex',
              alignItems: 'center',
              cursor: 'pointer'
            }}
            aria-label={playing ? 'Pause animation' : 'Play animation'}
          >
            {playing ? <Pause size={12} /> : <Play size={12} />}
          </button>
        </div>
      </div>

      {/* Dual Canvas: Live Exercise Animation + Highlighted Muscle Body Map */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: '1.35fr 0.65fr',
          gap: 10,
          alignItems: 'center'
        }}
      >
        {/* Left: Animated Athlete Movement */}
        <svg
          viewBox="0 0 160 130"
          style={{
            width: '100%',
            height: compact ? 110 : 140,
            background: '#0a0c12',
            borderRadius: 12,
            border: '1px solid #1f2433'
          }}
        >
          {/* Floor */}
          <line x1="12" y1="118" x2="148" y2="118" stroke="#283044" strokeWidth="2" />
          {/* Bench if applicable */}
          {pose.bench && (
            <rect x="28" y="94" width="66" height="6" rx="3" fill="#334155" />
          )}
          {/* Cable line if applicable */}
          {pose.cable && (
            <line
              x1={pose.hand[0]}
              y1="6"
              x2={pose.hand[0]}
              y2={pose.hand[1]}
              stroke="#38bdf8"
              strokeWidth="1.5"
              strokeDasharray="3 2"
            />
          )}
          {/* Legs */}
          <line
            x1={pose.hip[0]}
            y1={pose.hip[1]}
            x2={pose.knee[0]}
            y2={pose.knee[1]}
            stroke={
              isMuscle('Quadriceps') || isMuscle('Hamstrings') || isMuscle('Glutes')
                ? '#e85b37'
                : '#64748b'
            }
            strokeWidth="7"
            strokeLinecap="round"
          />
          <line
            x1={pose.knee[0]}
            y1={pose.knee[1]}
            x2={pose.foot[0]}
            y2={pose.foot[1]}
            stroke={isMuscle('Calves') ? '#e85b37' : '#475569'}
            strokeWidth="5.5"
            strokeLinecap="round"
          />
          {/* Torso */}
          <line
            x1={pose.hip[0]}
            y1={pose.hip[1]}
            x2={pose.shoulder[0]}
            y2={pose.shoulder[1]}
            stroke={
              isMuscle('Chest / Pectorals') ||
              isMuscle('Lats & Upper Back') ||
              isMuscle('Abs & Core')
                ? '#e85b37'
                : '#94a3b8'
            }
            strokeWidth="9"
            strokeLinecap="round"
          />
          {/* Arm */}
          <line
            x1={pose.shoulder[0]}
            y1={pose.shoulder[1]}
            x2={pose.elbow[0]}
            y2={pose.elbow[1]}
            stroke={
              isMuscle('Shoulders / Deltoids') ||
              isMuscle('Biceps') ||
              isMuscle('Triceps')
                ? '#e85b37'
                : '#cbd5e1'
            }
            strokeWidth="5.5"
            strokeLinecap="round"
          />
          <line
            x1={pose.elbow[0]}
            y1={pose.elbow[1]}
            x2={pose.hand[0]}
            y2={pose.hand[1]}
            stroke="#e2e8f0"
            strokeWidth="4.5"
            strokeLinecap="round"
          />
          {/* Head */}
          <circle cx={pose.head[0]} cy={pose.head[1]} r="8" fill="#cbd5e1" />
          {/* Active Muscle Pulse Orb */}
          <circle
            cx={
              isMuscle('Quadriceps') || isMuscle('Hamstrings')
                ? pose.knee[0]
                : isMuscle('Calves')
                  ? pose.foot[0]
                  : isMuscle('Glutes')
                    ? pose.hip[0]
                    : isMuscle('Biceps') || isMuscle('Triceps')
                      ? pose.elbow[0]
                      : pose.shoulder[0]
            }
            cy={
              isMuscle('Quadriceps') || isMuscle('Hamstrings')
                ? (pose.hip[1] + pose.knee[1]) / 2
                : isMuscle('Calves')
                  ? (pose.knee[1] + pose.foot[1]) / 2
                  : isMuscle('Glutes')
                    ? pose.hip[1]
                    : isMuscle('Biceps') || isMuscle('Triceps')
                      ? (pose.shoulder[1] + pose.elbow[1]) / 2
                      : pose.shoulder[1]
            }
            r={6 + contraction * 6}
            fill={primaryGlow}
          />
          {/* Barbell / Weight */}
          {pose.barbell && (
            <>
              <line
                x1={pose.hand[0] - 18}
                y1={pose.hand[1]}
                x2={pose.hand[0] + 18}
                y2={pose.hand[1]}
                stroke="#d4fb44"
                strokeWidth="3"
                strokeLinecap="round"
              />
              <circle cx={pose.hand[0] - 18} cy={pose.hand[1]} r="5" fill="#d4fb44" />
              <circle cx={pose.hand[0] + 18} cy={pose.hand[1]} r="5" fill="#d4fb44" />
            </>
          )}
        </svg>

        {/* Right: Highlighted Anatomical Muscle Map */}
        <svg
          viewBox="0 0 80 130"
          style={{
            width: '100%',
            height: compact ? 110 : 140,
            background: '#0a0c12',
            borderRadius: 12,
            border: '1px solid #1f2433'
          }}
        >
          {/* Head */}
          <circle cx="40" cy="14" r="7" fill="rgba(148,163,184,0.28)" />
          {/* Shoulders */}
          <circle cx="24" cy="29" r="6" fill={fillFor('Shoulders / Deltoids')} />
          <circle cx="56" cy="29" r="6" fill={fillFor('Shoulders / Deltoids')} />
          {/* Chest */}
          <rect x="28" y="25" width="24" height="14" rx="4" fill={fillFor('Chest / Pectorals')} />
          {/* Lats / Back Halo */}
          <path
            d="M 24 30 L 30 48 L 50 48 L 56 30 Z"
            fill={fillFor('Lats & Upper Back')}
            opacity="0.7"
          />
          {/* Abs & Core */}
          <rect x="32" y="40" width="16" height="22" rx="4" fill={fillFor('Abs & Core')} />
          {/* Biceps / Triceps */}
          <rect x="17" y="34" width="6" height="16" rx="3" fill={fillFor('Biceps')} />
          <rect x="57" y="34" width="6" height="16" rx="3" fill={fillFor('Triceps')} />
          {/* Glutes */}
          <rect x="28" y="62" width="24" height="10" rx="4" fill={fillFor('Glutes')} />
          {/* Quads & Hamstrings */}
          <rect x="28" y="73" width="10" height="25" rx="4" fill={fillFor('Quadriceps')} />
          <rect x="42" y="73" width="10" height="25" rx="4" fill={fillFor('Hamstrings')} />
          {/* Calves */}
          <rect x="29" y="100" width="8" height="20" rx="3" fill={fillFor('Calves')} />
          <rect x="43" y="100" width="8" height="20" rx="3" fill={fillFor('Calves')} />
        </svg>
      </div>

      {!compact && (
        <div style={{ fontSize: 11, marginTop: 8, color: '#cbd5e1' }}>
          <strong style={{ color: '#f59e0b' }}>Form Cue:</strong> {info.cue}
        </div>
      )}
    </div>
  );
};
