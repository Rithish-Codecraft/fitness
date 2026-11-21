import React, { useRef } from 'react';
import { BedDouble, Download, FolderCheck, Moon, Smartphone, Upload } from 'lucide-react';
import { LifestyleSleepAnalysis } from '../types/models';

interface StorageCardProps {
  lastSync: string;
  onExportAllJson: () => void;
  onImportJson: (file: File) => void;
}

export const StorageSyncCard: React.FC<StorageCardProps> = ({
  lastSync,
  onExportAllJson,
  onImportJson
}) => {
  const inputRef = useRef<HTMLInputElement | null>(null);

  return (
    <div className="card">
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 12
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <span
            className="icon-badge"
            style={{ background: '#2ed57326', color: '#2ed573', width: 34, height: 34 }}
          >
            <FolderCheck size={18} />
          </span>
          <div>
            <div style={{ fontSize: 10, fontWeight: 900, color: '#2ed573', letterSpacing: 0.8 }}>
              PULSEFORGE AI STORAGE SYNC
            </div>
            <div style={{ fontSize: 13, fontWeight: 700 }}>
              Local Browser Storage + Android JSON Bridge
            </div>
          </div>
        </div>
        <span
          style={{
            fontSize: 11,
            fontWeight: 700,
            padding: '4px 9px',
            borderRadius: 10,
            background: '#2ed57322',
            color: '#2ed573'
          }}
        >
          Ready
        </span>
      </div>

      <div className="sensor-tile" style={{ marginBottom: 12 }}>
        <div style={{ fontSize: 10, fontWeight: 700 }} className="muted">
          COMPATIBLE DIRECTORY FORMAT
        </div>
        <div style={{ fontSize: 12, fontWeight: 600, marginTop: 4 }}>
          Documents/PulseForge AI/*.json
        </div>
        <div style={{ fontSize: 11, marginTop: 4 }} className="muted">
          Last sync: {lastSync}
        </div>
      </div>

      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        <button className="btn" onClick={onExportAllJson} style={{ flex: 1 }}>
          <Download size={14} color="#2ed573" />
          Export JSON Backup
        </button>
        <button
          className="btn"
          onClick={() => inputRef.current?.click()}
          style={{ flex: 1 }}
        >
          <Upload size={14} color="#38bdf8" />
          Import Android JSON
        </button>
        <input
          ref={inputRef}
          type="file"
          accept=".json,application/json"
          style={{ display: 'none' }}
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) onImportJson(file);
            e.target.value = '';
          }}
        />
      </div>
    </div>
  );
};

export const LifestyleSleepCard: React.FC<{ analysis: LifestyleSleepAnalysis }> = ({
  analysis
}) => {
  const hasReal = analysis.dailyScreenTimeMinutes > 0 || analysis.nightScreenTimeMinutes > 0;

  return (
    <div className="card">
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 12 }}>
        <span
          className="icon-badge"
          style={{ background: '#a29bfe26', color: '#a29bfe', width: 34, height: 34 }}
        >
          <BedDouble size={18} />
        </span>
        <div>
          <div style={{ fontSize: 10, fontWeight: 900, color: '#a29bfe', letterSpacing: 0.8 }}>
            LIFESTYLE &amp; SLEEP BEHAVIOR
          </div>
          <div style={{ fontSize: 13, fontWeight: 700 }}>
            Nighttime Usage &amp; Circadian Rhythm
          </div>
        </div>
      </div>

      <div className="grid-2" style={{ marginBottom: 12 }}>
        <div className="sensor-tile">
          <div className="tile-header">
            <span className="icon-badge" style={{ background: '#ffd16626', color: '#ffd166' }}>
              <Moon size={14} />
            </span>
            <span style={{ fontSize: 10, fontWeight: 800, color: '#ffd166' }}>NIGHT USAGE</span>
          </div>
          <div className="tile-value-row">
            <span>{analysis.nightScreenTimeMinutes > 0 ? `${analysis.nightScreenTimeMinutes} min` : '-'}</span>
          </div>
          <div style={{ fontSize: 10, marginTop: 4 }} className="muted">
            10 PM – 6 AM window
          </div>
        </div>

        <div className="sensor-tile">
          <div className="tile-header">
            <span className="icon-badge" style={{ background: '#ef577726', color: '#ef5777' }}>
              <Smartphone size={14} />
            </span>
            <span style={{ fontSize: 10, fontWeight: 800, color: '#ef5777' }}>TOP NIGHT APP</span>
          </div>
          <div className="tile-value-row" style={{ fontSize: 15 }}>
            <span>{analysis.mostUsedNightApp || '-'}</span>
          </div>
          <div style={{ fontSize: 10, marginTop: 4 }} className="muted">
            {analysis.mostUsedNightAppMinutes > 0
              ? `${analysis.mostUsedNightAppMinutes} min before sleep`
              : '-'}
          </div>
        </div>
      </div>

      <div className="sensor-tile">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: 10, fontWeight: 800, color: '#00cec9' }}>
              OBJECTIVE SLEEP WINDOW
            </div>
            <div style={{ fontSize: 14, fontWeight: 800, marginTop: 2 }}>
              {hasReal
                ? `${analysis.estimatedBedtime} → ${analysis.estimatedWakeTime}`
                : '-'}
            </div>
          </div>
          <span
            style={{
              fontSize: 12,
              fontWeight: 800,
              color: '#00cec9',
              background: '#00cec922',
              padding: '4px 10px',
              borderRadius: 10
            }}
          >
            {analysis.estimatedSleepDurationHours != null
              ? `${analysis.estimatedSleepDurationHours} hrs`
              : '-'}
          </span>
        </div>
        {analysis.sleepDisruptionFeedback && (
          <p style={{ fontSize: 11, marginTop: 8, lineHeight: 1.45 }} className="muted">
            {analysis.sleepDisruptionFeedback}
          </p>
        )}
      </div>
    </div>
  );
};
