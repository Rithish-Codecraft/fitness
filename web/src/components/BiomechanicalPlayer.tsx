import React, { useEffect, useRef, useState } from 'react';
import { Gauge, Pause, Play } from 'lucide-react';
import { findRegistryEntry, resolveExerciseMediaUrl } from '../data/exerciseMediaRegistry';
import { AnimationPattern, resolveAnimationForWorkout } from '../engine/exerciseCatalog';

interface BiomechanicalPlayerProps {
  exerciseName: string;
  muscleGroup: string;
  compact?: boolean;
}

export const BiomechanicalPlayer: React.FC<BiomechanicalPlayerProps> = ({
  exerciseName,
  muscleGroup,
  compact = false
}) => {
  const entry = findRegistryEntry(exerciseName);
  const activeMuscle = entry?.primaryMuscle || muscleGroup || 'Chest / Pectorals';
  const pattern = resolveAnimationForWorkout(exerciseName, activeMuscle).animationType;

  const [mediaUrl, setMediaUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [imgFailed, setImgFailed] = useState(false);
  const [isPlaying, setIsPlaying] = useState(true);
  const [speed, setSpeed] = useState<0.75 | 1 | 1.25>(1);
  const [fadeKey, setFadeKey] = useState(0);

  const videoRef = useRef<HTMLVideoElement | null>(null);

  useEffect(() => {
    let cancelled = false;
    setImgFailed(false);
    setFadeKey((k) => k + 1);

    const trimmed = exerciseName.trim();
    if (!trimmed) {
      setMediaUrl(null);
      setLoading(false);
      return;
    }

    setLoading(true);
    resolveExerciseMediaUrl(trimmed)
      .then((url) => {
        if (!cancelled) {
          setMediaUrl(url);
          setLoading(false);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setMediaUrl(null);
          setLoading(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [exerciseName]);

  useEffect(() => {
    const video = videoRef.current;
    if (!video) return;
    video.playbackRate = speed;
    if (isPlaying) {
      video.play().catch(() => {});
    } else {
      video.pause();
    }
  }, [isPlaying, speed, mediaUrl]);

  const isVideo = Boolean(mediaUrl && /\.(mp4|webm)(\?|$)/i.test(mediaUrl));
  const showExternalMedia = Boolean(mediaUrl && !imgFailed);

  return (
    <div
      style={{
        position: 'relative',
        width: '100%',
        aspectRatio: compact ? '16 / 6' : '16 / 10',
        minHeight: compact ? 88 : 190,
        borderRadius: 16,
        overflow: 'hidden',
        background: 'radial-gradient(circle at 50% 35%, #1d2233 0%, #0e1017 100%)',
        border: '1px solid var(--border)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}
    >
      {loading && <div className="pf-skeleton-shimmer" style={{ position: 'absolute', inset: 0 }} />}

      {!loading && showExternalMedia && (
        <div
          key={fadeKey}
          className="pf-crossfade"
          style={{
            width: '100%',
            height: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: '#0e1017'
          }}
        >
          {isVideo ? (
            <video
              ref={videoRef}
              src={mediaUrl!}
              autoPlay
              loop
              muted
              playsInline
              onError={() => setImgFailed(true)}
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'contain',
                mixBlendMode: 'screen'
              }}
            />
          ) : (
            <img
              src={mediaUrl!}
              alt={exerciseName || activeMuscle}
              onError={() => setImgFailed(true)}
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'contain',
                filter: 'invert(0.92) hue-rotate(180deg) contrast(1.08)',
                mixBlendMode: 'screen'
              }}
            />
          )}
        </div>
      )}

      {!loading && !showExternalMedia && (
        <AnatomicalMotionCanvas
          pattern={pattern}
          muscleGroup={activeMuscle}
          isPlaying={isPlaying}
          speed={speed}
        />
      )}

      {/* Compact HUD Overlay */}
      <div
        style={{
          position: 'absolute',
          left: 10,
          right: 10,
          bottom: 8,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: 8,
          pointerEvents: 'none'
        }}
      >
        <span
          style={{
            fontSize: 10,
            fontWeight: 800,
            letterSpacing: 0.6,
            padding: '4px 9px',
            borderRadius: 999,
            background: 'rgba(12, 14, 20, 0.78)',
            color: '#f8fafc',
            border: '1px solid rgba(255,255,255,0.12)',
            maxWidth: '65%',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap'
          }}
        >
          {activeMuscle}
        </span>

        {!compact && (
          <div style={{ display: 'flex', gap: 6, pointerEvents: 'auto' }}>
            <button
              type="button"
              aria-label={isPlaying ? 'Pause' : 'Play'}
              onClick={() => setIsPlaying((p) => !p)}
              style={{
                width: 28,
                height: 28,
                borderRadius: 999,
                border: '1px solid rgba(255,255,255,0.16)',
                background: 'rgba(12, 14, 20, 0.8)',
                color: '#fff',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
            >
              {isPlaying ? <Pause size={12} /> : <Play size={12} />}
            </button>
            <button
              type="button"
              aria-label="Playback speed"
              onClick={() => setSpeed((s) => (s === 1 ? 1.25 : s === 1.25 ? 0.75 : 1))}
              style={{
                height: 28,
                padding: '0 8px',
                borderRadius: 999,
                border: '1px solid rgba(255,255,255,0.16)',
                background: 'rgba(12, 14, 20, 0.8)',
                color: '#fff',
                fontSize: 10,
                fontWeight: 800,
                display: 'inline-flex',
                alignItems: 'center',
                gap: 4,
                cursor: 'pointer'
              }}
            >
              <Gauge size={11} /> {speed}x
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

/**
 * 60fps Hardware-Accelerated Anatomical Exercise Motion Renderer.
 * Renders monochrome anatomical musculature + equipment silhouette with glowing
 * coral-red primary working muscle contraction synchronized to each movement pattern.
 */
const AnatomicalMotionCanvas: React.FC<{
  pattern: AnimationPattern;
  muscleGroup: string;
  isPlaying: boolean;
  speed: number;
}> = ({ pattern, muscleGroup, isPlaying, speed }) => {
  const [phase, setPhase] = useState(0);

  useEffect(() => {
    if (!isPlaying) return;
    let rafId = 0;
    let last = performance.now();
    let acc = 0;

    const tick = (now: number) => {
      const dt = (now - last) / 1000;
      last = now;
      acc += dt * speed * 1.9;
      setPhase(0.5 - 0.5 * Math.cos(acc * Math.PI));
      rafId = requestAnimationFrame(tick);
    };

    rafId = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(rafId);
  }, [isPlaying, speed]);

  const m = muscleGroup.toLowerCase();
  const bodyBase = '#cbd5e1';
  const bodyShade = '#64748b';
  const equipStroke = '#475569';
  const equipFill = '#334155';
  const activeMuscle = '#ef4444';
  const activeGlow = `rgba(239, 68, 68, ${0.35 + phase * 0.55})`;

  // Compute dynamic limb & equipment coordinates per pattern (phase 0 = eccentric, 1 = peak contraction)
  const renderExerciseScene = () => {
    switch (pattern) {
      case 'BENCH_PRESS': {
        const barY = 76 - phase * 30;
        const elbowY = 82 - phase * 18;
        return (
          <g>
            {/* Incline Bench */}
            <rect x="65" y="96" width="130" height="8" rx="3" fill={equipFill} />
            <line x1="85" y1="104" x2="80" y2="128" stroke={equipStroke} strokeWidth="5" strokeLinecap="round" />
            <line x1="175" y1="104" x2="180" y2="128" stroke={equipStroke} strokeWidth="5" strokeLinecap="round" />
            {/* Athlete supine */}
            <circle cx="82" cy="84" r="9" fill={bodyBase} />
            <rect x="92" y="80" width="58" height="16" rx="8" fill={bodyBase} />
            {/* Highlighted Chest */}
            <ellipse cx="112" cy="84" rx="14" ry="7" fill={activeMuscle} />
            <ellipse cx="112" cy="84" rx="18" ry="10" fill={activeGlow} />
            {/* Legs */}
            <path d="M148 88 L178 96 L184 124" stroke={bodyShade} strokeWidth="11" strokeLinecap="round" fill="none" />
            {/* Arms pressing barbell */}
            <path d={`M108 82 L114 ${elbowY} L112 ${barY}`} stroke={bodyBase} strokeWidth="9" strokeLinecap="round" fill="none" />
            {/* Barbell */}
            <line x1="84" y1={barY} x2="140" y2={barY} stroke="#e2e8f0" strokeWidth="3.5" strokeLinecap="round" />
            <rect x="80" y={barY - 10} width="7" height="20" rx="2" fill="#e85b37" />
            <rect x="137" y={barY - 10} width="7" height="20" rx="2" fill="#e85b37" />
          </g>
        );
      }

      case 'TRICEP_PUSHDOWN':
      case 'CABLE_FLY': {
        const handX = pattern === 'CABLE_FLY' ? 132 + phase * 22 : 128 + phase * 18;
        const handY = pattern === 'CABLE_FLY' ? 68 + phase * 10 : 54 + phase * 32;
        return (
          <g>
            {/* Cable Pulley Tower (matching video layout!) */}
            <rect x="44" y="16" width="8" height="114" rx="2" fill={equipFill} />
            <rect x="34" y="126" width="36" height="5" rx="2" fill={equipStroke} />
            <rect x="40" y={54 - phase * 16} width="16" height="26" rx="2" fill="#1e293b" stroke={equipStroke} strokeWidth="1.5" />
            {/* Cable line */}
            <line x1="52" y1="26" x2={handX} y2={handY} stroke="#94a3b8" strokeWidth="1.8" />
            {/* Incline Bench / Seated Athlete */}
            <line x1="112" y1="100" x2="132" y2="56" stroke={equipStroke} strokeWidth="7" strokeLinecap="round" />
            <line x1="120" y1="100" x2="112" y2="128" stroke={equipStroke} strokeWidth="4.5" strokeLinecap="round" />
            <line x1="146" y1="100" x2="152" y2="128" stroke={equipStroke} strokeWidth="4.5" strokeLinecap="round" />
            {/* Athlete Torso & Head */}
            <circle cx="136" cy="44" r="8.5" fill={bodyBase} />
            <path d="M132 54 L118 94" stroke={bodyBase} strokeWidth="16" strokeLinecap="round" />
            {/* Highlighted Working Muscle (Triceps / Chest) */}
            <ellipse
              cx={pattern === 'CABLE_FLY' ? 132 : 136}
              cy={pattern === 'CABLE_FLY' ? 62 : 66}
              rx="8"
              ry="5"
              fill={activeMuscle}
            />
            <ellipse
              cx={pattern === 'CABLE_FLY' ? 132 : 136}
              cy={pattern === 'CABLE_FLY' ? 62 : 66}
              rx="12"
              ry="8"
              fill={activeGlow}
            />
            {/* Legs */}
            <path d="M118 94 L152 96 L160 126" stroke={bodyShade} strokeWidth="10" strokeLinecap="round" fill="none" />
            {/* Articulated Arm pulling cable */}
            <path d={`M132 58 L142 74 L${handX} ${handY}`} stroke={bodyBase} strokeWidth="8" strokeLinecap="round" fill="none" />
          </g>
        );
      }

      case 'LAT_PULLDOWN':
      case 'OVERHEAD_PRESS': {
        const isPulldown = pattern === 'LAT_PULLDOWN';
        const barY = isPulldown ? 28 + phase * 34 : 58 - phase * 32;
        return (
          <g>
            {/* Frame / Tower */}
            {isPulldown && (
              <>
                <line x1="130" y1="14" x2="130" y2={barY} stroke="#94a3b8" strokeWidth="2" />
                <rect x="108" y="96" width="44" height="8" rx="3" fill={equipFill} />
              </>
            )}
            {/* Torso */}
            <circle cx="130" cy="48" r="9" fill={bodyBase} />
            <rect x="118" y="58" width="24" height="40" rx="8" fill={bodyBase} />
            {/* Highlighted Lats / Shoulders */}
            <ellipse cx="118" cy="65" rx="7" ry="11" fill={activeMuscle} />
            <ellipse cx="142" cy="65" rx="7" ry="11" fill={activeMuscle} />
            <ellipse cx="130" cy="66" rx="18" ry="12" fill={activeGlow} />
            {/* Arms */}
            <path d={`M118 62 L106 ${barY + 12} L106 ${barY}`} stroke={bodyBase} strokeWidth="8" strokeLinecap="round" fill="none" />
            <path d={`M142 62 L154 ${barY + 12} L154 ${barY}`} stroke={bodyBase} strokeWidth="8" strokeLinecap="round" fill="none" />
            {/* Barbell / Bar */}
            <line x1="92" y1={barY} x2="168" y2={barY} stroke="#e2e8f0" strokeWidth="3.5" strokeLinecap="round" />
            {!isPulldown && (
              <>
                <rect x="88" y={barY - 9} width="6" height="18" rx="2" fill="#e85b37" />
                <rect x="166" y={barY - 9} width="6" height="18" rx="2" fill="#e85b37" />
                <path d="M122 98 L120 130 M138 98 L140 130" stroke={bodyShade} strokeWidth="10" strokeLinecap="round" />
              </>
            )}
          </g>
        );
      }

      case 'SQUAT':
      case 'LEG_PRESS':
      case 'ROMANIAN_DEADLIFT':
      case 'HIP_THRUST':
      case 'CALF_RAISE': {
        const drop = phase * 26;
        const hipY = 76 + (pattern === 'HIP_THRUST' ? -phase * 22 : drop);
        const kneeX = 148 + (pattern === 'SQUAT' ? phase * 10 : 0);
        return (
          <g>
            {/* Floor */}
            <line x1="56" y1="130" x2="204" y2="130" stroke={equipStroke} strokeWidth="3" strokeLinecap="round" />
            {/* Legs */}
            <path d={`M122 ${hipY} L${kneeX} 102 L126 128`} stroke={bodyBase} strokeWidth="12" strokeLinecap="round" fill="none" />
            {/* Highlighted Lower Body (Quads / Glutes / Hamstrings / Calves) */}
            <ellipse
              cx={pattern === 'CALF_RAISE' ? 136 : pattern === 'HIP_THRUST' ? 124 : 136}
              cy={pattern === 'CALF_RAISE' ? 116 : pattern === 'HIP_THRUST' ? hipY : (hipY + 102) / 2}
              rx="10"
              ry="14"
              fill={activeMuscle}
            />
            <ellipse
              cx={pattern === 'CALF_RAISE' ? 136 : 134}
              cy={pattern === 'CALF_RAISE' ? 116 : (hipY + 102) / 2}
              rx="15"
              ry="18"
              fill={activeGlow}
            />
            {/* Torso & Head */}
            <path
              d={`M122 ${hipY} L${pattern === 'ROMANIAN_DEADLIFT' ? 148 : 114} ${hipY - 34}`}
              stroke={bodyBase}
              strokeWidth="15"
              strokeLinecap="round"
            />
            <circle cx={pattern === 'ROMANIAN_DEADLIFT' ? 154 : 114} cy={hipY - 46} r="8.5" fill={bodyBase} />
            {/* Barbell */}
            <line
              x1="96"
              y1={pattern === 'ROMANIAN_DEADLIFT' ? 104 - phase * 22 : hipY - 34}
              x2="148"
              y2={pattern === 'ROMANIAN_DEADLIFT' ? 104 - phase * 22 : hipY - 34}
              stroke="#e2e8f0"
              strokeWidth="3.5"
              strokeLinecap="round"
            />
          </g>
        );
      }

      case 'BICEP_CURL':
      case 'LATERAL_RAISE':
      case 'BARBELL_ROW':
      default: {
        const angle = pattern === 'LATERAL_RAISE' ? phase * 1.3 : phase * 1.9;
        const handX =
          pattern === 'LATERAL_RAISE'
            ? 144 + Math.sin(angle) * 28
            : pattern === 'BICEP_CURL'
              ? 142 + Math.cos(angle) * 18
              : 146 - phase * 14;
        const handY =
          pattern === 'LATERAL_RAISE'
            ? 84 - Math.sin(angle) * 28
            : pattern === 'BICEP_CURL'
              ? 88 - Math.sin(angle) * 26
              : 94 - phase * 22;

        return (
          <g>
            {/* Torso & Head */}
            <circle cx="128" cy="36" r="9" fill={bodyBase} />
            <rect x="116" y="47" width="24" height="44" rx="8" fill={bodyBase} />
            {/* Highlighted Upper Muscle */}
            <ellipse
              cx={pattern === 'BARBELL_ROW' ? 128 : 142}
              cy={pattern === 'LATERAL_RAISE' ? 52 : 66}
              rx="9"
              ry="7"
              fill={activeMuscle}
            />
            <ellipse
              cx={pattern === 'BARBELL_ROW' ? 128 : 142}
              cy={pattern === 'LATERAL_RAISE' ? 52 : 66}
              rx="14"
              ry="11"
              fill={activeGlow}
            />
            {/* Legs */}
            <path d="M122 90 L120 128 M134 90 L136 128" stroke={bodyShade} strokeWidth="10" strokeLinecap="round" />
            {/* Arm & Dumbbell */}
            <path d={`M138 52 L144 72 L${handX} ${handY}`} stroke={bodyBase} strokeWidth="8" strokeLinecap="round" fill="none" />
            <circle cx={handX} cy={handY} r="6.5" fill="#e85b37" />
            {m.includes('back') && (
              <circle cx="114" cy="76" r="5" fill={activeGlow} />
            )}
          </g>
        );
      }
    }
  };

  return (
    <svg
      viewBox="0 0 260 145"
      style={{ width: '100%', height: '100%', maxHeight: 220 }}
      aria-label={`Animated exercise demonstration for ${muscleGroup}`}
    >
      {renderExerciseScene()}
    </svg>
  );
};
