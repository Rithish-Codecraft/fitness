import React from 'react';
import { ArrowLeft, ShieldCheck } from 'lucide-react';

export const LegalPage: React.FC<{
  kind: 'privacy' | 'terms';
  onBack: () => void;
}> = ({ kind, onBack }) => {
  const isPrivacy = kind === 'privacy';

  return (
    <div
      style={{
        minHeight: '100vh',
        padding: '32px 18px',
        background: 'radial-gradient(circle at top, #1f1635 0%, #0c0e14 60%)'
      }}
    >
      <div className="card" style={{ maxWidth: 740, margin: '0 auto', padding: 28 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <img src="/app_logo.png" alt="PulseForge AI" style={{ width: 40, height: 40, borderRadius: 10 }} />
            <div>
              <h1 style={{ fontSize: 20, fontWeight: 900 }}>
                {isPrivacy ? 'PulseForge AI — Privacy Policy' : 'PulseForge AI — Terms of Service'}
              </h1>
              <div style={{ fontSize: 11 }} className="muted">
                Effective Date: November 22, 2026 • Domain: pulseforge.feenixs.com
              </div>
            </div>
          </div>
          <button className="btn" onClick={onBack}>
            <ArrowLeft size={14} /> Back to App
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 14, fontSize: 13, lineHeight: 1.65 }} className="muted">
          {isPrivacy ? (
            <>
              <p>
                <strong style={{ color: 'var(--text)' }}>1. Information We Collect:</strong> When you sign in with Google or email, PulseForge AI stores your account identifier, email address, display name, biometric fitness goals, nutrition logs, workout sessions, and optional device sensor/lifestyle telemetry.
              </p>
              <p>
                <strong style={{ color: 'var(--text)' }}>2. Data Isolation &amp; Storage:</strong> All user records are isolated per authenticated user ID using Supabase Postgres Row-Level Security (`auth.uid() = id`). On Android, local backups are stored inside your device&apos;s `Documents/pulseforce/` directory.
              </p>
              <p>
                <strong style={{ color: 'var(--text)' }}>3. Data Sharing &amp; Deletion:</strong> PulseForge AI does not sell your personal or health data to third parties. You may export or permanently delete your account data at any time from Settings.
              </p>
              <p>
                <strong style={{ color: 'var(--text)' }}>4. Contact:</strong> For privacy inquiries, contact rithish.codecraft@gmail.com.
              </p>
            </>
          ) : (
            <>
              <p>
                <strong style={{ color: 'var(--text)' }}>1. Acceptance of Terms:</strong> By creating an account or signing into PulseForge AI on Web (`pulseforge.feenixs.com`) or Android, you agree to these Terms of Service.
              </p>
              <p>
                <strong style={{ color: 'var(--text)' }}>2. Fitness &amp; Health Disclaimer:</strong> BMR, TDEE, macro targets, and sleep estimates provided by PulseForge AI are informational fitness tools and do not constitute medical advice. Consult a qualified healthcare professional before starting any new diet or exercise program.
              </p>
              <p>
                <strong style={{ color: 'var(--text)' }}>3. Account Responsibility:</strong> You are responsible for safeguarding your account credentials and ensuring accurate biometric inputs.
              </p>
              <p>
                <strong style={{ color: 'var(--text)' }}>4. Contact:</strong> Support is available at rithish.codecraft@gmail.com.
              </p>
            </>
          )}
        </div>

        <div style={{ marginTop: 20, paddingTop: 14, borderTop: '1px solid var(--border)', display: 'flex', alignItems: 'center', gap: 8, fontSize: 11 }} className="muted">
          <ShieldCheck size={16} color="#10b981" />
          <span>PulseForge AI • Fitness • Nutrition • Progress</span>
        </div>
      </div>
    </div>
  );
};
