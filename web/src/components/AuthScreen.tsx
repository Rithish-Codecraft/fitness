import React, { useState } from 'react';
import { Eye, EyeOff, KeyRound, Lock, Mail, Smartphone, User, UserPlus } from 'lucide-react';
import {
  AuthAccount,
  UserDataBundle,
  importMultiDeviceSyncKey,
  loginAccount,
  signUpAccount
} from '../auth/authManager';

interface AuthScreenProps {
  versionName: string;
  versionCode: number;
  onAuthenticated: (
    account: AuthAccount,
    isNewSignUp: boolean,
    importedData?: Partial<UserDataBundle>
  ) => void;
}

type Mode = 'LOGIN' | 'SIGNUP' | 'SYNC';

export const AuthScreen: React.FC<AuthScreenProps> = ({
  onAuthenticated
}) => {
  const [mode, setMode] = useState<Mode>('LOGIN');
  const [fullName, setFullName] = useState('');
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [acceptedPolicy, setAcceptedPolicy] = useState(true);
  const [syncKey, setSyncKey] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [showPolicyModal, setShowPolicyModal] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      if (mode === 'LOGIN') {
        const res = await loginAccount(identifier, password);
        if (!res.ok) {
          setError(res.error);
          return;
        }
        onAuthenticated(res.account, false);
      } else if (mode === 'SIGNUP') {
        if (!acceptedPolicy) {
          setError('Please accept the Terms of Service & Privacy Policy to continue.');
          return;
        }
        const res = await signUpAccount(fullName, identifier, password);
        if (!res.ok) {
          setError(res.error);
          return;
        }
        onAuthenticated(res.account, true);
      } else {
        const res = importMultiDeviceSyncKey(syncKey);
        if (!res.ok) {
          setError(res.error);
          return;
        }
        onAuthenticated(res.account, false, res.data);
      }
    } finally {
      setBusy(false);
    }
  };

  const passwordStrength =
    password.length === 0
      ? null
      : password.length < 6
        ? { label: 'Too short', color: '#ef4444' }
        : password.length < 10
          ? { label: 'Good', color: '#f59e0b' }
          : { label: 'Strong', color: '#10b981' };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 20,
        background: 'radial-gradient(circle at top, #1f1635 0%, #0c0e14 60%)'
      }}
    >
      <div className="card" style={{ width: '100%', maxWidth: 440, padding: 26 }}>
        {/* Official Brand Hero */}
        <div style={{ textAlign: 'center', marginBottom: 20 }}>
          <img
            src="/app_logo.png"
            alt="PulseForge AI"
            style={{
              width: 72,
              height: 72,
              borderRadius: 18,
              border: '1px solid var(--border)',
              marginBottom: 10
            }}
          />
          <h1 style={{ fontSize: 22, fontWeight: 900, letterSpacing: 0.5 }}>PULSEFORGE AI</h1>
        </div>

        {/* Mode Switcher */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(3, 1fr)',
            gap: 6,
            padding: 4,
            background: 'var(--surface-2)',
            borderRadius: 14,
            marginBottom: 18
          }}
        >
          <button
            type="button"
            className={`btn ${mode === 'LOGIN' ? 'btn-primary' : ''}`}
            style={{ border: 'none', padding: '8px 6px', fontSize: 11 }}
            onClick={() => {
              setMode('LOGIN');
              setError(null);
            }}
          >
            <Lock size={13} /> Sign In
          </button>
          <button
            type="button"
            className={`btn ${mode === 'SIGNUP' ? 'btn-primary' : ''}`}
            style={{ border: 'none', padding: '8px 6px', fontSize: 11 }}
            onClick={() => {
              setMode('SIGNUP');
              setError(null);
            }}
          >
            <UserPlus size={13} /> Sign Up
          </button>
          <button
            type="button"
            className={`btn ${mode === 'SYNC' ? 'btn-primary' : ''}`}
            style={{ border: 'none', padding: '8px 6px', fontSize: 11 }}
            onClick={() => {
              setMode('SYNC');
              setError(null);
            }}
          >
            <Smartphone size={13} /> Sync Device
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {mode === 'SIGNUP' && (
            <div>
              <label style={{ fontSize: 14, fontWeight: 600, display: 'block', marginBottom: 6 }}>
                Full Name
              </label>
              <div style={{ position: 'relative' }}>
                <User
                  size={15}
                  style={{ position: 'absolute', left: 12, top: 13, opacity: 0.55 }}
                />
                <input
                  className="input"
                  style={{ paddingLeft: 40, fontSize: 14 }}
                  placeholder="Enter your full name"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />
              </div>
            </div>
          )}

          {mode !== 'SYNC' ? (
            <>
              <div>
                <label style={{ fontSize: 14, fontWeight: 600, display: 'block', marginBottom: 6 }}>
                  {mode === 'SIGNUP' ? 'Email or Phone' : 'Email or PulseForge ID (PF-XXXXXX)'}
                </label>
                <div style={{ position: 'relative' }}>
                  <Mail
                    size={15}
                    style={{ position: 'absolute', left: 12, top: 13, opacity: 0.55 }}
                  />
                  <input
                    className="input"
                    style={{ paddingLeft: 40, fontSize: 14 }}
                    placeholder={mode === 'SIGNUP' ? 'athlete@domain.com' : ''}
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                  />
                </div>
              </div>

              <div>
                <label style={{ fontSize: 14, fontWeight: 600, display: 'block', marginBottom: 6 }}>
                  Password
                </label>
                <div style={{ position: 'relative' }}>
                  <Lock
                    size={15}
                    style={{ position: 'absolute', left: 12, top: 13, opacity: 0.55 }}
                  />
                  <input
                    className="input"
                    type={showPassword ? 'text' : 'password'}
                    style={{ paddingLeft: 40, paddingRight: 38, fontSize: 14 }}
                    placeholder="Minimum 6 characters"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword((v) => !v)}
                    style={{
                      position: 'absolute',
                      right: 10,
                      top: 9,
                      background: 'none',
                      border: 'none',
                      color: 'var(--text-muted)',
                      cursor: 'pointer'
                    }}
                  >
                    {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                {mode === 'SIGNUP' && passwordStrength && (
                  <div style={{ fontSize: 11, marginTop: 4, color: passwordStrength.color, fontWeight: 700 }}>
                    Strength: {passwordStrength.label}
                  </div>
                )}
              </div>
            </>
          ) : (
            <div>
              <label style={{ fontSize: 11, fontWeight: 700, display: 'block', marginBottom: 4 }} className="muted">
                Multi-Device Sync Key (`PFKEY:...`) or Android Account JSON
              </label>
              <textarea
                className="input"
                rows={4}
                placeholder="Paste your PFKEY:... token exported from your Android APK or another browser"
                value={syncKey}
                onChange={(e) => setSyncKey(e.target.value)}
                style={{ resize: 'vertical', fontFamily: 'monospace', fontSize: 11 }}
              />
            </div>
          )}

          {mode === 'SIGNUP' && (
            <label
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: 8,
                fontSize: 11,
                cursor: 'pointer'
              }}
              className="muted"
            >
              <input
                type="checkbox"
                checked={acceptedPolicy}
                onChange={(e) => setAcceptedPolicy(e.target.checked)}
              />
              <span>
                I accept the{' '}
                <button
                  type="button"
                  onClick={() => setShowPolicyModal(true)}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: 'var(--accent)',
                    textDecoration: 'underline',
                    cursor: 'pointer',
                    fontSize: 11,
                    fontWeight: 700
                  }}
                >
                  Terms of Service &amp; Privacy Policy
                </button>
              </span>
            </label>
          )}

          {error && (
            <div
              style={{
                padding: '10px 12px',
                borderRadius: 12,
                background: '#ef444422',
                border: '1px solid #ef444455',
                color: '#fca5a5',
                fontSize: 12,
                fontWeight: 600
              }}
            >
              {error}
            </div>
          )}

          <button className="btn btn-primary" type="submit" disabled={busy} style={{ padding: 12, fontSize: 14 }}>
            <KeyRound size={15} />
            {mode === 'LOGIN'
              ? 'Sign In to My Account'
              : mode === 'SIGNUP'
                ? 'Create PulseForge ID & Continue'
                : 'Restore & Sign In on This Device'}
          </button>
        </form>
      </div>

      {showPolicyModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            background: 'rgba(0,0,0,0.72)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 16,
            zIndex: 70
          }}
        >
          <div className="card" style={{ maxWidth: 460, width: '100%' }}>
            <strong style={{ fontSize: 16 }}>PulseForge AI Privacy &amp; Data Policy</strong>
            <p style={{ fontSize: 12, marginTop: 10, lineHeight: 1.55 }} className="muted">
              1. Local-First Privacy: Your biometrics, nutrition logs, workouts, and telemetry are isolated under your unique PulseForge ID (`PF-XXXXXX`).<br />
              2. Multi-Device Portability: You control cross-device transfer using encrypted/encoded Sync Keys or JSON backups.<br />
              3. Medical Disclaimer: BMR, TDEE, and calorie targets are educational fitness estimates, not medical advice.
            </p>
            <button
              className="btn btn-primary"
              style={{ marginTop: 14, width: '100%' }}
              onClick={() => setShowPolicyModal(false)}
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
