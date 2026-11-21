import React, { useState } from 'react';
import { CheckCircle2, Download, RefreshCw, X } from 'lucide-react';
import { VersionInfo } from '../types/models';

interface VersionSyncModalProps {
  open: boolean;
  currentVersion: VersionInfo;
  onClose: () => void;
}

export const VersionSyncModal: React.FC<VersionSyncModalProps> = ({
  open,
  currentVersion,
  onClose
}) => {
  const [remoteManifest, setRemoteManifest] = useState<VersionInfo | null>(null);
  const [checking, setChecking] = useState(false);
  const [checkMsg, setCheckMsg] = useState<string | null>(null);

  if (!open) return null;

  const checkLiveManifest = async () => {
    setChecking(true);
    setCheckMsg(null);
    try {
      const res = await fetch('/version.json', { cache: 'no-store' });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = (await res.json()) as VersionInfo;
      setRemoteManifest(data);
      setCheckMsg(
        data.versionCode > currentVersion.versionCode
          ? `New version available: v${data.versionName} (Build ${data.versionCode})`
          : `Synced with APK manifest: v${data.versionName} (Build ${data.versionCode})`
      );
    } catch (e) {
      setCheckMsg(`Could not reach version.json: ${(e as Error).message}`);
    } finally {
      setChecking(false);
    }
  };

  const active = remoteManifest ?? currentVersion;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(0,0,0,0.65)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 16,
        zIndex: 60
      }}
    >
      <div className="card" style={{ width: '100%', maxWidth: 480 }}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: 12
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <CheckCircle2 size={18} color="#10b981" />
            <strong style={{ fontSize: 16 }}>Unified APK &amp; Web Version Control</strong>
          </div>
          <button className="btn" onClick={onClose} style={{ padding: 6 }}>
            <X size={16} />
          </button>
        </div>

        <div className="sensor-tile" style={{ marginBottom: 12 }}>
          <div style={{ fontSize: 12 }} className="muted">
            Shared Manifest (version.json)
          </div>
          <div style={{ fontSize: 20, fontWeight: 900, marginTop: 4 }}>
            v{active.versionName} (versionCode {active.versionCode})
          </div>
          <div style={{ fontSize: 11, marginTop: 6, wordBreak: 'break-all' }} className="muted">
            APK SHA-256: {active.sha256 || '-'}
          </div>
        </div>

        <div
          style={{
            fontSize: 12,
            whiteSpace: 'pre-line',
            lineHeight: 1.5,
            marginBottom: 14,
            maxHeight: 160,
            overflowY: 'auto'
          }}
          className="muted"
        >
          {active.releaseNotes}
        </div>

        {checkMsg && (
          <div style={{ fontSize: 12, color: '#10b981', marginBottom: 10, fontWeight: 600 }}>
            {checkMsg}
          </div>
        )}

        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
          <button className="btn" onClick={checkLiveManifest} disabled={checking}>
            <RefreshCw size={14} />
            {checking ? 'Checking...' : 'Check version.json'}
          </button>
          <a
            href={active.apkUrl}
            target="_blank"
            rel="noreferrer"
            className="btn btn-primary"
            style={{ textDecoration: 'none' }}
          >
            <Download size={14} />
            Download Android APK (v{active.versionName})
          </a>
        </div>
      </div>
    </div>
  );
};
