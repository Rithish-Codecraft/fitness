import React, { useEffect, useRef, useState } from 'react';
import { Activity, Dumbbell, Pause, Play } from 'lucide-react';
import {
  ExerciseMediaEntry,
  findLocalExerciseMedia,
  searchExerciseDbMedia
} from '../engine/exerciseMediaMap';

interface WorkoutAnimationClipProps {
  exerciseName: string;
  muscleGroup: string;
  compact?: boolean;
}

export const WorkoutAnimationClip: React.FC<WorkoutAnimationClipProps> = ({
  exerciseName,
  muscleGroup,
  compact = false
}) => {
  const [playing, setPlaying] = useState(true);
  const [speed, setSpeed] = useState<number>(1);
  const [media, setMedia] = useState<ExerciseMediaEntry | null>(() =>
    findLocalExerciseMedia(exerciseName, muscleGroup)
  );
  const [loadingApi, setLoadingApi] = useState(false);
  const [imgError, setImgError] = useState(false);
  const imgRef = useRef<HTMLImageElement | null>(null);
  const freezeCanvasRef = useRef<HTMLCanvasElement | null>(null);

  // Instant local match + debounced ExerciseDB API fallback for custom searched exercises
  useEffect(() => {
    setImgError(false);
    const local = findLocalExerciseMedia(exerciseName, muscleGroup);
    if (local) {
      setMedia(local);
      setLoadingApi(false);
      return;
    }

    const clean = exerciseName.trim();
    if (clean.length < 3) {
      setMedia(null);
      setLoadingApi(false);
      return;
    }

    let cancelled = false;
    setLoadingApi(true);
    const timer = setTimeout(async () => {
      const apiMatch = await searchExerciseDbMedia(clean);
      if (!cancelled) {
        setMedia(apiMatch);
        setLoadingApi(false);
      }
    }, 380);

    return () => {
      cancelled = true;
      clearTimeout(timer);
    };
  }, [exerciseName, muscleGroup]);

  // Capture current GIF frame when paused so Play/Pause freezes cleanly
  useEffect(() => {
    if (playing) return;
    const img = imgRef.current;
    const canvas = freezeCanvasRef.current;
    if (!img || !canvas || !img.complete || img.naturalWidth === 0) return;
    try {
      canvas.width = img.naturalWidth;
      canvas.height = img.naturalHeight;
      const ctx = canvas.getContext('2d');
      ctx?.drawImage(img, 0, 0);
    } catch {
      // Cross-origin frame capture fallback handled gracefully
    }
  }, [playing]);

  const cycleSpeed = () => {
    setSpeed((s) => (s === 1 ? 0.5 : s === 0.5 ? 1.5 : 1));
  };

  const targetLabel = media?.targetMuscle || muscleGroup;
  const activationPct = media?.activationPercent ?? 88;
  const formCue =
    media?.formCue ||
    'Maintain strict tempo: 2s controlled eccentric, pause, then explosive concentric drive.';

  const boxHeight = compact ? 150 : 210;

  return (
    <div
      style={{
        background: '#141414',
        border: '1px solid #262626',
        borderRadius: 16,
        padding: compact ? 10 : 14,
        color: '#f8fafc'
      }}
    >
      {/* Header */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: 8,
          marginBottom: 10
        }}
      >
        <div style={{ minWidth: 0 }}>
          <div
            style={{
              fontSize: 10,
              fontWeight: 800,
              letterSpacing: 0.9,
              color: '#94a3b8',
              display: 'flex',
              alignItems: 'center',
              gap: 6
            }}
          >
            <span>3D ANATOMICAL DEMO</span>
            {speed !== 1 && (
              <span style={{ color: '#FF5722' }}>• {speed}x TEMPO</span>
            )}
          </div>
          <div
            style={{
              fontSize: 12,
              fontWeight: 800,
              color: '#FF5722',
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis'
            }}
          >
            Target: {targetLabel} ({activationPct}% active)
          </div>
        </div>

        <div style={{ display: 'flex', gap: 6, alignItems: 'center', flexShrink: 0 }}>
          <button
            type="button"
            onClick={cycleSpeed}
            style={{
              background: '#1E1E1E',
              border: '1px solid #303030',
              color: '#e2e8f0',
              borderRadius: 8,
              padding: '4px 8px',
              fontSize: 10,
              fontWeight: 700,
              cursor: 'pointer'
            }}
            title="Cycle playback tempo (1x / 0.5x / 1.5x)"
          >
            {speed}x
          </button>
          <button
            type="button"
            onClick={() => setPlaying((p) => !p)}
            style={{
              background: '#FF5722',
              border: 'none',
              color: '#ffffff',
              borderRadius: 8,
              padding: '5px 8px',
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

      {/* Responsive Looping Media Container (zero layout shift) */}
      <div
        style={{
          position: 'relative',
          width: '100%',
          height: boxHeight,
          background: '#141414',
          borderRadius: 12,
          border: '1px solid #222222',
          overflow: 'hidden',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}
      >
        {media && !imgError ? (
          <>
            <img
              ref={imgRef}
              key={media.animationUrl}
              src={media.animationUrl}
              alt={`${media.name} anatomical demonstration`}
              crossOrigin="anonymous"
              onError={() => setImgError(true)}
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'contain',
                display: playing ? 'block' : 'none',
                filter: 'invert(0.92) hue-rotate(180deg) contrast(1.08) saturate(1.25)',
                transition: 'opacity 0.2s ease'
              }}
            />
            {!playing && (
              <canvas
                ref={freezeCanvasRef}
                style={{
                  width: '100%',
                  height: '100%',
                  objectFit: 'contain',
                  filter: 'invert(0.92) hue-rotate(180deg) contrast(1.08) saturate(1.25)'
                }}
              />
            )}
            {!playing && (
              <div
                style={{
                  position: 'absolute',
                  inset: 0,
                  background: 'rgba(20, 20, 20, 0.45)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  pointerEvents: 'none'
                }}
              >
                <span
                  style={{
                    background: '#1E1E1E',
                    border: '1px solid #FF5722',
                    color: '#FF5722',
                    padding: '4px 10px',
                    borderRadius: 999,
                    fontSize: 11,
                    fontWeight: 800
                  }}
                >
                  PAUSED
                </span>
              </div>
            )}
            {/* Subtle muscle legend pill */}
            {media.secondaryMuscles.length > 0 && !compact && (
              <div
                style={{
                  position: 'absolute',
                  bottom: 8,
                  left: 8,
                  background: 'rgba(30, 30, 30, 0.88)',
                  border: '1px solid #2c2c2c',
                  borderRadius: 8,
                  padding: '3px 8px',
                  fontSize: 10,
                  color: '#cbd5e1'
                }}
              >
                Secondary: {media.secondaryMuscles.slice(0, 2).join(', ')}
              </div>
            )}
          </>
        ) : (
          <div
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 8,
              padding: 16,
              textAlign: 'center',
              background: '#1E1E1E',
              width: '100%',
              height: '100%'
            }}
          >
            {loadingApi ? (
              <>
                <Activity size={22} color="#FF5722" />
                <span style={{ fontSize: 12, color: '#cbd5e1', fontWeight: 600 }}>
                  Loading anatomical demonstration...
                </span>
              </>
            ) : (
              <>
                <Dumbbell size={22} color="#FF5722" />
                <span style={{ fontSize: 12, color: '#cbd5e1', fontWeight: 600 }}>
                  {exerciseName.trim()
                    ? `Custom Exercise: ${exerciseName}`
                    : 'Select or search an exercise'}
                </span>
                <span style={{ fontSize: 10, color: '#94a3b8' }}>
                  Targeting {targetLabel} • Full 3D clip unavailable offline
                </span>
              </>
            )}
          </div>
        )}
      </div>

      {/* Bottom Form Cue */}
      {!compact && (
        <div style={{ fontSize: 11, marginTop: 9, color: '#cbd5e1', lineHeight: 1.45 }}>
          <strong style={{ color: '#FF5722' }}>Form Cue:</strong> {formCue}
        </div>
      )}
    </div>
  );
};
