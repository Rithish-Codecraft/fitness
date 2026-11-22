import React, { useEffect, useState } from 'react';
import { Check, KeyRound, Play, Search, X } from 'lucide-react';
import {
  EXERCISEDB_BODY_PARTS,
  ExerciseDbItem,
  getSavedExerciseDbKey,
  mapBodyPartToMuscleGroup,
  mapEquipmentType,
  saveExerciseDbKey,
  searchExerciseDb
} from '../services/exerciseDbService';

interface ExerciseDbExplorerProps {
  onSelectExercise: (exercise: {
    name: string;
    muscleGroup: string;
    equipmentType: string;
  }) => void;
}

export const ExerciseDbExplorer: React.FC<ExerciseDbExplorerProps> = ({ onSelectExercise }) => {
  const [apiKey, setApiKey] = useState<string>(() => getSavedExerciseDbKey());
  const [keyInput, setKeyInput] = useState<string>(() => getSavedExerciseDbKey());
  const [showKeyForm, setShowKeyForm] = useState<boolean>(() => !getSavedExerciseDbKey());

  const [query, setQuery] = useState('');
  const [bodyPart, setBodyPart] = useState<string>('all');
  const [items, setItems] = useState<ExerciseDbItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [activeExercise, setActiveExercise] = useState<ExerciseDbItem | null>(null);

  const loadExercises = async (q: string, bp: string, key: string) => {
    if (!key.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const list = await searchExerciseDb(q, bp, key);
      setItems(list);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load ExerciseDB');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (apiKey) {
      loadExercises(query, bodyPart, apiKey);
    }
  }, [apiKey, bodyPart]);

  const handleSaveKey = (e: React.FormEvent) => {
    e.preventDefault();
    const clean = keyInput.trim();
    saveExerciseDbKey(clean);
    setApiKey(clean);
    setShowKeyForm(!clean);
    if (clean) {
      loadExercises(query, bodyPart, clean);
    }
  };

  return (
    <div className="card pf-animate-in" style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 16 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 8 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <strong style={{ fontSize: 16 }}>ExerciseDB Library (1,300+ GIFs)</strong>
        </div>
        <button
          type="button"
          className="btn"
          onClick={() => setShowKeyForm((v) => !v)}
          style={{ padding: '6px 10px', fontSize: 11 }}
        >
          <KeyRound size={13} color={apiKey ? '#10b981' : 'var(--accent)'} />
          {apiKey ? 'API Connected' : 'Connect Key'}
        </button>
      </div>

      {showKeyForm && (
        <form onSubmit={handleSaveKey} style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          <input
            className="input"
            type="password"
            placeholder="Paste RapidAPI ExerciseDB Key"
            value={keyInput}
            onChange={(e) => setKeyInput(e.target.value)}
            style={{ flex: '1 1 200px' }}
          />
          <button className="btn btn-primary" type="submit">
            <Check size={14} /> Save Key
          </button>
        </form>
      )}

      {apiKey && (
        <>
          <form
            onSubmit={(e) => {
              e.preventDefault();
              loadExercises(query, bodyPart, apiKey);
            }}
            style={{ display: 'flex', gap: 8 }}
          >
            <div style={{ position: 'relative', flex: 1 }}>
              <Search size={15} style={{ position: 'absolute', left: 12, top: 14, opacity: 0.55 }} />
              <input
                className="input"
                style={{ paddingLeft: 38 }}
                placeholder="Search exercises (e.g. bench, squat, curl)"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
            <button className="btn btn-primary" type="submit" disabled={loading}>
              {loading ? '...' : 'Search'}
            </button>
          </form>

          <div style={{ display: 'flex', gap: 6, overflowX: 'auto', paddingBottom: 4 }}>
            {EXERCISEDB_BODY_PARTS.map((bp) => (
              <button
                key={bp}
                type="button"
                className={`btn ${bodyPart === bp ? 'btn-primary' : ''}`}
                onClick={() => setBodyPart(bp)}
                style={{ padding: '6px 12px', fontSize: 11, textTransform: 'capitalize' }}
              >
                {bp}
              </button>
            ))}
          </div>

          {error && (
            <div style={{ fontSize: 12, color: '#ef4444', fontWeight: 600 }}>{error}</div>
          )}

          {!loading && items.length > 0 && (
            <div className="grid-2">
              {items.slice(0, 8).map((ex) => (
                <div
                  key={ex.id}
                  className="sensor-tile pf-tap-card"
                  style={{ display: 'flex', flexDirection: 'column', gap: 8, padding: 10 }}
                >
                  {ex.gifUrl && (
                    <img
                      src={ex.gifUrl}
                      alt={ex.name}
                      loading="lazy"
                      style={{
                        width: '100%',
                        height: 128,
                        objectFit: 'cover',
                        borderRadius: 12,
                        background: '#ffffff'
                      }}
                    />
                  )}
                  <div style={{ fontSize: 13, fontWeight: 800, textTransform: 'capitalize' }}>
                    {ex.name}
                  </div>
                  <div style={{ fontSize: 11, textTransform: 'capitalize' }} className="muted">
                    {ex.target} • {ex.equipment}
                  </div>
                  <div style={{ display: 'flex', gap: 6 }}>
                    <button
                      type="button"
                      className="btn btn-primary"
                      onClick={() =>
                        onSelectExercise({
                          name: ex.name.replace(/\b\w/g, (c) => c.toUpperCase()),
                          muscleGroup: mapBodyPartToMuscleGroup(ex.bodyPart, ex.target),
                          equipmentType: mapEquipmentType(ex.equipment)
                        })
                      }
                      style={{ flex: 1, padding: '7px 10px', fontSize: 11 }}
                    >
                      Use
                    </button>
                    <button
                      type="button"
                      className="btn"
                      aria-label="Preview GIF"
                      onClick={() => setActiveExercise(ex)}
                      style={{ padding: '7px 10px', fontSize: 11 }}
                    >
                      <Play size={13} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {activeExercise && (
        <div className="pf-sheet-backdrop" onClick={() => setActiveExercise(null)}>
          <div className="card pf-sheet-panel" onClick={(e) => e.stopPropagation()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 10 }}>
              <strong style={{ fontSize: 16, textTransform: 'capitalize' }}>{activeExercise.name}</strong>
              <button className="btn" aria-label="Close" onClick={() => setActiveExercise(null)} style={{ padding: 6 }}>
                <X size={16} />
              </button>
            </div>
            {activeExercise.gifUrl && (
              <img
                src={activeExercise.gifUrl}
                alt={activeExercise.name}
                style={{
                  width: '100%',
                  maxHeight: 240,
                  objectFit: 'contain',
                  borderRadius: 14,
                  background: '#ffffff',
                  marginBottom: 10
                }}
              />
            )}
            <div style={{ fontSize: 12, fontWeight: 700, textTransform: 'capitalize', marginBottom: 6 }}>
              Target: {activeExercise.target} • Equipment: {activeExercise.equipment}
            </div>
            {activeExercise.instructions?.slice(0, 4).map((step, i) => (
              <div key={i} style={{ fontSize: 12, marginBottom: 4 }} className="muted">
                {i + 1}. {step}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
