import React, { useEffect, useRef, useState } from 'react';
import { Gauge, Pause, Play } from 'lucide-react';
import { findRegistryEntry, resolveExerciseMediaUrl } from '../data/exerciseMediaRegistry';

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
  const showMedia = Boolean(mediaUrl && !imgFailed);

  return (
    <div
      style={{
        position: 'relative',
        width: '100%',
        aspectRatio: compact ? '16 / 6' : '16 / 10',
        minHeight: compact ? 84 : 184,
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

      {!loading && showMedia && (
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

      {!loading && !showMedia && (
        <AnatomicalMuscleFallback muscleGroup={activeMuscle} compact={compact} />
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

        {!compact && isVideo && showMedia && (
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

const AnatomicalMuscleFallback: React.FC<{ muscleGroup: string; compact: boolean }> = ({
  muscleGroup,
  compact
}) => {
  const m = muscleGroup.toLowerCase();
  const isChest = m.includes('chest') || m.includes('pectoral');
  const isBack = m.includes('back') || m.includes('lat');
  const isShoulders = m.includes('shoulder') || m.includes('delt');
  const isBiceps = m.includes('bicep');
  const isTriceps = m.includes('tricep');
  const isCore = m.includes('core') || m.includes('abs');
  const isQuads = m.includes('quad');
  const isHamstrings = m.includes('hamstring');
  const isGlutes = m.includes('glute');
  const isCalves = m.includes('calf') || m.includes('calves');

  const activeFill = '#e85b37';
  const baseFill = 'rgba(248, 250, 252, 0.14)';

  return (
    <svg
      viewBox="0 0 180 140"
      style={{
        width: compact ? 84 : 136,
        height: compact ? 68 : 116,
        opacity: 0.95
      }}
      aria-label={`Anatomical diagram highlighting ${muscleGroup}`}
    >
      {/* Head & Neck */}
      <circle cx="90" cy="18" r="9" fill={baseFill} />
      <rect x="86" y="27" width="8" height="6" rx="2" fill={baseFill} />

      {/* Shoulders / Deltoids */}
      <ellipse cx="64" cy="40" rx="10" ry="7" fill={isShoulders ? activeFill : baseFill} />
      <ellipse cx="116" cy="40" rx="10" ry="7" fill={isShoulders ? activeFill : baseFill} />

      {/* Chest / Pectorals or Upper Back */}
      <path
        d="M72 34 L108 34 L105 52 L75 52 Z"
        fill={isChest || isBack ? activeFill : baseFill}
      />

      {/* Arms (Biceps / Triceps) */}
      <rect
        x="53"
        y="46"
        width="9"
        height="26"
        rx="4"
        fill={isBiceps || isTriceps ? activeFill : baseFill}
      />
      <rect
        x="118"
        y="46"
        width="9"
        height="26"
        rx="4"
        fill={isBiceps || isTriceps ? activeFill : baseFill}
      />

      {/* Core / Abs */}
      <rect x="76" y="54" width="28" height="24" rx="5" fill={isCore ? activeFill : baseFill} />

      {/* Glutes */}
      <ellipse cx="82" cy="83" rx="9" ry="6" fill={isGlutes ? activeFill : baseFill} />
      <ellipse cx="98" cy="83" rx="9" ry="6" fill={isGlutes ? activeFill : baseFill} />

      {/* Quads / Hamstrings */}
      <rect
        x="73"
        y="88"
        width="13"
        height="26"
        rx="5"
        fill={isQuads || isHamstrings ? activeFill : baseFill}
      />
      <rect
        x="94"
        y="88"
        width="13"
        height="26"
        rx="5"
        fill={isQuads || isHamstrings ? activeFill : baseFill}
      />

      {/* Calves */}
      <rect x="75" y="116" width="10" height="18" rx="4" fill={isCalves ? activeFill : baseFill} />
      <rect x="95" y="116" width="10" height="18" rx="4" fill={isCalves ? activeFill : baseFill} />
    </svg>
  );
};
