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
        minHeight: compact ? 88 : 200,
        borderRadius: 16,
        overflow: 'hidden',
        background: 'radial-gradient(circle at 50% 42%, #1e2536 0%, #0d1018 100%)',
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
          exerciseName={exerciseName}
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
 * 3D-Shaded Anatomical Exercise Motion Renderer.
 * Renders sculpted monochrome musculature with striation shading, realistic gym
 * equipment (cable tower, adjustable bench, barbell/plates), and glowing coral-red
 * primary muscle contraction synchronized at 60fps.
 */
const AnatomicalMotionCanvas: React.FC<{
  pattern: AnimationPattern;
  exerciseName: string;
  muscleGroup: string;
  isPlaying: boolean;
  speed: number;
}> = ({ pattern, exerciseName, muscleGroup, isPlaying, speed }) => {
  const [phase, setPhase] = useState(0);

  useEffect(() => {
    if (!isPlaying) return;
    let rafId = 0;
    let last = performance.now();
    let acc = 0;

    const tick = (now: number) => {
      const dt = (now - last) / 1000;
      last = now;
      acc += dt * speed * 1.85;
      setPhase(0.5 - 0.5 * Math.cos(acc * Math.PI));
      rafId = requestAnimationFrame(tick);
    };

    rafId = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(rafId);
  }, [isPlaying, speed]);

  const m = muscleGroup.toLowerCase();
  const cleanName = exerciseName.toLowerCase();
  const glowOpacity = 0.35 + phase * 0.55;

  const renderCableTower = (x: number, stackOffset: number, pulleyY: number) => (
    <g>
      {/* Base foot */}
      <rect x={x - 10} y="152" width="44" height="5" rx="2" fill="url(#metalGrad)" />
      {/* Dual upright columns */}
      <rect x={x} y="16" width="5" height="138" rx="1.5" fill="url(#metalGrad)" />
      <rect x={x + 18} y="16" width="5" height="138" rx="1.5" fill="url(#metalGrad)" />
      <rect x={x - 2} y="14" width="27" height="6" rx="2" fill="#64748b" />
      {/* Guide rods */}
      <line x1={x + 9} y1="20" x2={x + 9} y2="152" stroke="#475569" strokeWidth="1" />
      <line x1={x + 14} y1="20" x2={x + 14} y2="152" stroke="#475569" strokeWidth="1" />
      {/* Weight stack */}
      <g transform={`translate(0, ${-stackOffset})`}>
        <rect x={x + 4} y="92" width="15" height="36" rx="2" fill="#0f172a" stroke="#475569" strokeWidth="1" />
        {[0, 5, 10, 15, 20, 25, 30].map((dy) => (
          <line key={dy} x1={x + 5} y1={96 + dy} x2={x + 18} y2={96 + dy} stroke="#334155" strokeWidth="1" />
        ))}
      </g>
      {/* Pulley arm & wheel */}
      <rect x={x + 22} y={pulleyY - 3} width="10" height="6" rx="2" fill="#64748b" />
      <circle cx={x + 32} cy={pulleyY} r="3.5" fill="#94a3b8" stroke="#1e293b" strokeWidth="1" />
    </g>
  );

  const renderInclineBench = () => (
    <g>
      {/* Rear & front legs */}
      <line x1="138" y1="112" x2="112" y2="154" stroke="url(#metalGrad)" strokeWidth="4.5" strokeLinecap="round" />
      <line x1="176" y1="114" x2="194" y2="154" stroke="url(#metalGrad)" strokeWidth="4.5" strokeLinecap="round" />
      <line x1="146" y1="120" x2="180" y2="120" stroke="#475569" strokeWidth="3" />
      {/* Backrest & seat pad */}
      <line x1="126" y1="68" x2="160" y2="110" stroke="url(#padGrad)" strokeWidth="9" strokeLinecap="round" />
      <line x1="160" y1="110" x2="186" y2="114" stroke="url(#padGrad)" strokeWidth="9" strokeLinecap="round" />
    </g>
  );

  const renderFlatBench = () => (
    <g>
      <line x1="98" y1="110" x2="92" y2="154" stroke="url(#metalGrad)" strokeWidth="5" strokeLinecap="round" />
      <line x1="202" y1="110" x2="208" y2="154" stroke="url(#metalGrad)" strokeWidth="5" strokeLinecap="round" />
      <rect x="86" y="102" width="128" height="10" rx="4" fill="url(#padGrad)" />
    </g>
  );

  const renderSculptedHead = (cx: number, cy: number, tiltDeg = 0) => (
    <g transform={`rotate(${tiltDeg} ${cx} ${cy})`}>
      {/* Neck */}
      <path d={`M${cx - 4} ${cy + 6} L${cx + 4} ${cy + 6} L${cx + 5} ${cy + 15} L${cx - 5} ${cy + 15} Z`} fill="url(#muscleGrad)" />
      {/* Cranium + jaw */}
      <ellipse cx={cx} cy={cy} rx="8.5" ry="10" fill="url(#muscleGrad)" />
      <path d={`M${cx - 5} ${cy + 3} Q${cx} ${cy + 11} ${cx + 6} ${cy + 5}`} fill="none" stroke="#475569" strokeWidth="1.2" />
    </g>
  );

  const renderExerciseScene = () => {
    switch (pattern) {
      case 'BICEP_CURL': {
        // Matches the uploaded GymVisual incline cable bicep curl composition
        const handX = 160 + phase * 6;
        const handY = 92 - phase * 30;
        const elbowX = 156;
        const elbowY = 92;
        const shoulderX = 144;
        const shoulderY = 74;
        return (
          <g>
            {renderCableTower(48, phase * 18, 52)}
            {renderInclineBench()}
            {/* Cable */}
            <line x1="80" y1="52" x2={handX} y2={handY} stroke="#cbd5e1" strokeWidth="1.4" />
            {/* Athlete seated reclined on incline bench */}
            {renderSculptedHead(128, 56, -32)}
            {/* Torso */}
            <path d="M134 66 Q148 68 156 88 L168 106 L152 112 L132 82 Z" fill="url(#muscleGrad)" />
            {/* Shorts */}
            <path d="M154 102 L176 105 L178 118 L156 116 Z" fill="#0f172a" />
            {/* Sculpted Legs & Calves */}
            <path d="M174 108 Q196 110 202 126 L194 128 Q186 118 172 116 Z" fill="url(#muscleGrad)" />
            <path d="M196 125 L200 150 L212 152 L202 153 L192 150 L191 126 Z" fill="url(#muscleGrad)" />
            {/* Upper Arm + Highlighted Bicep */}
            <path d={`M${shoulderX} ${shoulderY} Q${shoulderX + 10} ${shoulderY + 8} ${elbowX} ${elbowY}`} stroke="url(#muscleGrad)" strokeWidth="11" strokeLinecap="round" fill="none" />
            {/* Glowing Coral-Red Bicep Contraction */}
            <ellipse
              cx={(shoulderX + elbowX) / 2 + 2}
              cy={(shoulderY + elbowY) / 2 - 1}
              rx={6 + phase * 1.5}
              ry={9 + phase * 1}
              transform={`rotate(-35 ${(shoulderX + elbowX) / 2 + 2} ${(shoulderY + elbowY) / 2 - 1})`}
              fill="url(#activeMuscleGrad)"
            />
            <ellipse
              cx={(shoulderX + elbowX) / 2 + 2}
              cy={(shoulderY + elbowY) / 2 - 1}
              rx="12"
              ry="14"
              fill={`rgba(239, 68, 68, ${glowOpacity})`}
              filter="url(#muscleGlow)"
            />
            {/* Forearm & Cable Handle */}
            <path d={`M${elbowX} ${elbowY} L${handX} ${handY}`} stroke="url(#muscleGrad)" strokeWidth="8.5" strokeLinecap="round" fill="none" />
            <rect x={handX - 5} y={handY - 3} width="10" height="5" rx="2" fill="#e2e8f0" />
          </g>
        );
      }

      case 'TRICEP_PUSHDOWN':
      case 'CABLE_FLY': {
        const isFly = pattern === 'CABLE_FLY';
        const handX = isFly ? 168 + phase * 26 : 168 + phase * 12;
        const handY = isFly ? 84 + phase * 12 : 68 + phase * 38;
        const shoulderX = 148;
        const shoulderY = 72;
        const elbowX = isFly ? 158 + phase * 12 : 156;
        const elbowY = isFly ? 82 + phase * 4 : 88;
        return (
          <g>
            {renderCableTower(48, phase * 20, isFly ? 64 : 36)}
            {isFly ? null : renderInclineBench()}
            <line x1="80" y1={isFly ? 64 : 36} x2={handX} y2={handY} stroke="#cbd5e1" strokeWidth="1.4" />
            {renderSculptedHead(isFly ? 148 : 134, isFly ? 46 : 56, isFly ? 0 : -24)}
            {/* Torso */}
            <path
              d={isFly ? 'M138 62 L162 62 L158 106 L142 106 Z' : 'M134 66 Q148 68 156 88 L168 106 L152 112 L132 82 Z'}
              fill="url(#muscleGrad)"
            />
            {/* Highlighted Muscle (Chest or Triceps) */}
            <ellipse
              cx={isFly ? 156 : 149}
              cy={isFly ? 74 : 81}
              rx={isFly ? 11 : 6.5}
              ry={isFly ? 7.5 : 9}
              fill="url(#activeMuscleGrad)"
            />
            <ellipse
              cx={isFly ? 156 : 149}
              cy={isFly ? 74 : 81}
              rx="15"
              ry="13"
              fill={`rgba(239, 68, 68, ${glowOpacity})`}
              filter="url(#muscleGlow)"
            />
            {/* Shorts & Legs */}
            {isFly ? (
              <>
                <rect x="141" y="102" width="18" height="16" rx="3" fill="#0f172a" />
                <path d="M145 118 L142 152 M156 118 L162 152" stroke="url(#muscleGrad)" strokeWidth="10" strokeLinecap="round" />
              </>
            ) : (
              <>
                <path d="M154 102 L176 105 L178 118 L156 116 Z" fill="#0f172a" />
                <path d="M174 108 Q196 110 202 126 L194 128 Q186 118 172 116 Z" fill="url(#muscleGrad)" />
                <path d="M196 125 L200 150 L212 152 L202 153 L192 150 L191 126 Z" fill="url(#muscleGrad)" />
              </>
            )}
            {/* Arm */}
            <path d={`M${shoulderX} ${shoulderY} L${elbowX} ${elbowY} L${handX} ${handY}`} stroke="url(#muscleGrad)" strokeWidth="9" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            <circle cx={handX} cy={handY} r="3.5" fill="#e2e8f0" />
          </g>
        );
      }

      case 'BENCH_PRESS': {
        const barY = 78 - phase * 32;
        const elbowY = 88 - phase * 20;
        return (
          <g>
            {renderFlatBench()}
            {renderSculptedHead(96, 90, -85)}
            {/* Torso */}
            <path d="M110 88 L170 90 L172 104 L110 104 Z" fill="url(#muscleGrad)" />
            {/* Highlighted Pectorals */}
            <ellipse cx="128" cy="92" rx="15" ry="7" fill="url(#activeMuscleGrad)" />
            <ellipse cx="128" cy="92" rx="20" ry="11" fill={`rgba(239, 68, 68, ${glowOpacity})`} filter="url(#muscleGlow)" />
            {/* Shorts & Legs */}
            <rect x="166" y="90" width="18" height="16" rx="3" fill="#0f172a" />
            <path d="M180 96 L206 108 L212 150" stroke="url(#muscleGrad)" strokeWidth="11" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            {/* Arm pressing barbell */}
            <path d={`M124 90 L132 ${elbowY} L126 ${barY}`} stroke="url(#muscleGrad)" strokeWidth="9.5" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            {/* Barbell & Plates */}
            <line x1="94" y1={barY} x2="158" y2={barY} stroke="#e2e8f0" strokeWidth="3.5" strokeLinecap="round" />
            <rect x="90" y={barY - 13} width="7" height="26" rx="2" fill="#1e293b" stroke="#94a3b8" strokeWidth="1" />
            <rect x="155" y={barY - 13} width="7" height="26" rx="2" fill="#1e293b" stroke="#94a3b8" strokeWidth="1" />
          </g>
        );
      }

      case 'LAT_PULLDOWN':
      case 'OVERHEAD_PRESS': {
        const isPulldown = pattern === 'LAT_PULLDOWN';
        const barY = isPulldown ? 30 + phase * 36 : 62 - phase * 34;
        return (
          <g>
            {isPulldown && (
              <>
                <rect x="147" y="10" width="6" height="24" fill="url(#metalGrad)" />
                <line x1="150" y1="22" x2="150" y2={barY} stroke="#cbd5e1" strokeWidth="1.6" />
                <rect x="128" y="114" width="44" height="9" rx="4" fill="url(#padGrad)" />
                <line x1="150" y1="123" x2="150" y2="154" stroke="url(#metalGrad)" strokeWidth="5" />
              </>
            )}
            {renderSculptedHead(150, 52)}
            {/* Sculpted V-taper Torso */}
            <path d="M132 66 L168 66 L160 110 L140 110 Z" fill="url(#muscleGrad)" />
            {/* Highlighted Lats / Shoulders */}
            <ellipse cx={isPulldown ? 136 : 133} cy={isPulldown ? 80 : 70} rx="7" ry={isPulldown ? 13 : 8} fill="url(#activeMuscleGrad)" />
            <ellipse cx={isPulldown ? 164 : 167} cy={isPulldown ? 80 : 70} rx="7" ry={isPulldown ? 13 : 8} fill="url(#activeMuscleGrad)" />
            <ellipse cx="150" cy={isPulldown ? 80 : 70} rx="22" ry="13" fill={`rgba(239, 68, 68, ${glowOpacity})`} filter="url(#muscleGlow)" />
            {/* Shorts & Legs */}
            <rect x="139" y="104" width="22" height="14" rx="3" fill="#0f172a" />
            {!isPulldown && (
              <path d="M144 118 L142 152 M156 118 L158 152" stroke="url(#muscleGrad)" strokeWidth="10" strokeLinecap="round" />
            )}
            {/* Arms */}
            <path d={`M134 68 L118 ${barY + 14} L118 ${barY}`} stroke="url(#muscleGrad)" strokeWidth="8.5" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            <path d={`M166 68 L182 ${barY + 14} L182 ${barY}`} stroke="url(#muscleGrad)" strokeWidth="8.5" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            {/* Bar */}
            <line x1="104" y1={barY} x2="196" y2={barY} stroke="#e2e8f0" strokeWidth="3.5" strokeLinecap="round" />
            {!isPulldown && (
              <>
                <rect x="98" y={barY - 11} width="7" height="22" rx="2" fill="#1e293b" stroke="#94a3b8" strokeWidth="1" />
                <rect x="195" y={barY - 11} width="7" height="22" rx="2" fill="#1e293b" stroke="#94a3b8" strokeWidth="1" />
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
        const hipY = pattern === 'HIP_THRUST' ? 112 - phase * 24 : 88 + (pattern === 'CALF_RAISE' ? -phase * 8 : drop);
        const kneeX = 166 + (pattern === 'SQUAT' ? phase * 10 : 0);
        const kneeY = pattern === 'HIP_THRUST' ? 96 : 118;
        const activeY =
          pattern === 'CALF_RAISE'
            ? 136
            : pattern === 'HIP_THRUST'
              ? hipY
              : (hipY + kneeY) / 2;

        return (
          <g>
            <line x1="70" y1="154" x2="230" y2="154" stroke="#334155" strokeWidth="3" strokeLinecap="round" />
            {/* Sculpted Legs */}
            <path d={`M140 ${hipY} L${kneeX} ${kneeY} L144 150`} stroke="url(#muscleGrad)" strokeWidth="13" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            {/* Highlighted Lower Body */}
            <ellipse
              cx={pattern === 'CALF_RAISE' ? 154 : pattern === 'HIP_THRUST' ? 142 : 152}
              cy={activeY}
              rx="10"
              ry="14"
              fill="url(#activeMuscleGrad)"
            />
            <ellipse
              cx={pattern === 'CALF_RAISE' ? 154 : 150}
              cy={activeY}
              rx="16"
              ry="19"
              fill={`rgba(239, 68, 68, ${glowOpacity})`}
              filter="url(#muscleGlow)"
            />
            {/* Shorts */}
            <ellipse cx="140" cy={hipY} rx="11" ry="8" fill="#0f172a" />
            {/* Torso & Head */}
            <path
              d={`M140 ${hipY} L${pattern === 'ROMANIAN_DEADLIFT' ? 172 : 132} ${hipY - 38}`}
              stroke="url(#muscleGrad)"
              strokeWidth="15"
              strokeLinecap="round"
            />
            {renderSculptedHead(pattern === 'ROMANIAN_DEADLIFT' ? 180 : 132, hipY - 52)}
            {/* Barbell */}
            <line
              x1="112"
              y1={pattern === 'ROMANIAN_DEADLIFT' ? 116 - phase * 22 : hipY - 38}
              x2="168"
              y2={pattern === 'ROMANIAN_DEADLIFT' ? 116 - phase * 22 : hipY - 38}
              stroke="#e2e8f0"
              strokeWidth="3.5"
              strokeLinecap="round"
            />
            <rect
              x="108"
              y={(pattern === 'ROMANIAN_DEADLIFT' ? 116 - phase * 22 : hipY - 38) - 11}
              width="6"
              height="22"
              rx="2"
              fill="#1e293b"
              stroke="#94a3b8"
              strokeWidth="1"
            />
            <rect
              x="166"
              y={(pattern === 'ROMANIAN_DEADLIFT' ? 116 - phase * 22 : hipY - 38) - 11}
              width="6"
              height="22"
              rx="2"
              fill="#1e293b"
              stroke="#94a3b8"
              strokeWidth="1"
            />
          </g>
        );
      }

      case 'LATERAL_RAISE':
      case 'BARBELL_ROW':
      case 'CRUNCH_PLANK':
      default: {
        const isLat = pattern === 'LATERAL_RAISE';
        const isCore = pattern === 'CRUNCH_PLANK' || m.includes('core') || m.includes('abs');
        const angle = isLat ? phase * 1.35 : phase * 1.85;
        const handX = isLat ? 166 + Math.sin(angle) * 32 : 166 - phase * 14;
        const handY = isLat ? 94 - Math.sin(angle) * 30 : 108 - phase * 24;

        if (isCore) {
          return (
            <g>
              <line x1="70" y1="146" x2="230" y2="146" stroke="#334155" strokeWidth="3" strokeLinecap="round" />
              <path d={`M100 138 Q144 ${132 - phase * 18} 194 138`} stroke="url(#muscleGrad)" strokeWidth="14" strokeLinecap="round" fill="none" />
              <ellipse cx="146" cy={132 - phase * 14} rx="18" ry="8" fill="url(#activeMuscleGrad)" />
              <ellipse cx="146" cy={132 - phase * 14} rx="24" ry="12" fill={`rgba(239, 68, 68, ${glowOpacity})`} filter="url(#muscleGlow)" />
              {renderSculptedHead(96, 128 - phase * 10)}
            </g>
          );
        }

        return (
          <g>
            {renderSculptedHead(148, 44)}
            {/* Sculpted Torso */}
            <path d="M132 58 L164 58 L158 104 L138 104 Z" fill="url(#muscleGrad)" />
            {/* Highlighted Muscle */}
            <ellipse
              cx={pattern === 'BARBELL_ROW' ? 148 : 162}
              cy={isLat ? 62 : 76}
              rx="10"
              ry="8"
              fill="url(#activeMuscleGrad)"
            />
            <ellipse
              cx={pattern === 'BARBELL_ROW' ? 148 : 162}
              cy={isLat ? 62 : 76}
              rx="16"
              ry="12"
              fill={`rgba(239, 68, 68, ${glowOpacity})`}
              filter="url(#muscleGlow)"
            />
            {/* Shorts & Legs */}
            <rect x="137" y="100" width="22" height="16" rx="3" fill="#0f172a" />
            <path d="M142 116 L140 152 M154 116 L156 152" stroke="url(#muscleGrad)" strokeWidth="10" strokeLinecap="round" />
            {/* Arm & Dumbbell */}
            <path d={`M160 62 L166 84 L${handX} ${handY}`} stroke="url(#muscleGrad)" strokeWidth="8.5" strokeLinecap="round" strokeLinejoin="round" fill="none" />
            <rect x={handX - 7} y={handY - 4} width="14" height="8" rx="2.5" fill="#1e293b" stroke="#94a3b8" strokeWidth="1" />
            {(m.includes('back') || cleanName.includes('row')) && (
              <circle cx="136" cy="78" r="6" fill={`rgba(239, 68, 68, ${glowOpacity})`} />
            )}
          </g>
        );
      }
    }
  };

  return (
    <svg
      viewBox="0 0 300 170"
      style={{ width: '100%', height: '100%', maxHeight: 230 }}
      aria-label={`Animated anatomical exercise demonstration for ${muscleGroup}`}
    >
      <defs>
        <linearGradient id="muscleGrad" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stopColor="#f1f5f9" />
          <stop offset="52%" stopColor="#94a3b8" />
          <stop offset="100%" stopColor="#475569" />
        </linearGradient>
        <linearGradient id="activeMuscleGrad" x1="0" y1="0" x2="1" y2="1">
          <stop offset="0%" stopColor="#ff7a59" />
          <stop offset="55%" stopColor="#ef4444" />
          <stop offset="100%" stopColor="#991b1b" />
        </linearGradient>
        <linearGradient id="metalGrad" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0%" stopColor="#94a3b8" />
          <stop offset="50%" stopColor="#64748b" />
          <stop offset="100%" stopColor="#334155" />
        </linearGradient>
        <linearGradient id="padGrad" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#334155" />
          <stop offset="100%" stopColor="#0f172a" />
        </linearGradient>
        <radialGradient id="floorShadow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="rgba(0,0,0,0.65)" />
          <stop offset="100%" stopColor="rgba(0,0,0,0)" />
        </radialGradient>
        <filter id="muscleGlow" x="-40%" y="-40%" width="180%" height="180%">
          <feGaussianBlur stdDeviation="3.5" />
        </filter>
      </defs>

      {/* Studio floor shadow */}
      <ellipse cx="150" cy="155" rx="108" ry="8" fill="url(#floorShadow)" />

      {renderExerciseScene()}
    </svg>
  );
};
