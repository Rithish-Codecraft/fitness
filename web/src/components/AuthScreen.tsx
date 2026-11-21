import React, { useState } from 'react';
import { Eye, EyeOff, KeyRound, Lock, Mail, Smartphone, User, UserPlus } from 'lucide-react';
import {
  AuthAccount,
  UserDataBundle,
  importMultiDeviceSyncKey,
  loginAccount,
  signUpAccount
} from '../auth/authManager';
import {
  derivePulseForgeId,
  signInWithGoogleOAuth,
  supabase
} from '../auth/supabaseClient';

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
  const [acceptedPolicy, setAcceptedPolicy] = useState(false);
  const [syncKey, setSyncKey] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [showPolicyModal, setShowPolicyModal] = useState(false);

  const handleGoogleSignIn = async () => {
    setError(null);
    setBusy(true);
    const err = await signInWithGoogleOAuth();
    if (err) {
      setError(err);
      setBusy(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      if (mode === 'LOGIN') {
        const cleanId = identifier.trim();
        if (cleanId.includes('@')) {
          const { data, error: sbErr } = await supabase.auth.signInWithPassword({
            email: cleanId,
            password
          });
          if (!sbErr && data.user) {
            const pfId = derivePulseForgeId(data.user.id);
            const metaName =
              (data.user.user_metadata?.full_name as string | undefined) ||
              cleanId.split('@')[0];
            onAuthenticated(
              {
                userId: pfId,
                fullName: metaName,
                email: data.user.email || cleanId,
                passwordHash: '',
                createdAt: data.user.created_at.slice(0, 16).replace('T', ' ')
              },
              false
            );
            return;
          }
          if (sbErr && !sbErr.message.toLowerCase().includes('fetch')) {
            // Fall through to local PF-XXXXXX check only if offline or PF-ID
            const localRes = await loginAccount(identifier, password);
            if (localRes.ok) {
              onAuthenticated(localRes.account, false);
              return;
            }
            setError(sbErr.message);
            return;
          }
        }
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
        const cleanEmail = identifier.trim().toLowerCase();
        if (cleanEmail.includes('@')) {
          const { data, error: sbErr } = await supabase.auth.signUp({
            email: cleanEmail,
            password,
            options: {
              data: { full_name: fullName.trim() }
            }
          });
          if (sbErr) {
            setError(sbErr.message);
            return;
          }
          if (data.user) {
            const pfId = derivePulseForgeId(data.user.id);
            onAuthenticated(
              {
                userId: pfId,
                fullName: fullName.trim() || cleanEmail.split('@')[0],
                email: cleanEmail,
                passwordHash: '',
                createdAt: new Date().toISOString().slice(0, 16).replace('T', ' ')
              },
              true
            );
            return;
          }
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

        {/* Sign In with Google (Supabase OAuth) */}
        <button
          type="button"
          onClick={handleGoogleSignIn}
          disabled={busy}
          style={{
            width: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 10,
            padding: '11px 14px',
            borderRadius: 12,
            border: '1px solid #dadce0',
            background: '#ffffff',
            color: '#1f1f1f',
            fontWeight: 700,
            fontSize: 13,
            cursor: 'pointer',
            marginBottom: 14
          }}
        >
          <svg width="18" height="18" viewBox="0 0 24 24" aria-hidden="true">
            <path
              fill="#4285F4"
              d="M23.745 12.27c0-.7-.06-1.4-.19-2.07H12v4.51h6.6c-.29 1.52-1.14 2.82-2.4 3.68v3.05h3.88c2.27-2.09 3.665-5.17 3.665-9.17Z"
            />
            <path
              fill="#34A853"
              d="M12 24c3.24 0 5.95-1.08 7.93-2.91l-3.88-3.05c-1.08.72-2.45 1.16-4.05 1.16-3.12 0-5.77-2.11-6.72-4.96H1.29v3.14C3.26 21.3 7.31 24 12 24Z"
            />
            <path
              fill="#FBBC05"
              d="M5.28 14.24c-.24-.72-.38-1.49-.38-2.24s.14-1.52.38-2.24V6.62H1.29C.47 8.24 0 10.06 0 12s.47 3.76 1.29 5.38l3.99-3.14Z"
            />
            <path
              fill="#EA4335"
              d="M12 4.75c1.77 0 3.35.61 4.6 1.8l3.42-3.42C17.95 1.19 15.24 0 12 0 7.31 0 3.26 2.7 1.29 6.62l3.99 3.14c.95-2.85 3.6-4.96 6.72-4.96Z"
            />
          </svg>
          Continue with Google
        </button>

        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            marginBottom: 14,
            fontSize: 11
          }}
          className="muted"
        >
          <div style={{ flex: 1, height: 1, background: 'var(--border)' }} />
          <span>or continue with email / PF-ID</span>
          <div style={{ flex: 1, height: 1, background: 'var(--border)' }} />
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
            style={{ border: 'none', padding: '10px 8px', fontSize: 12, minHeight: 40 }}
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
            style={{ border: 'none', padding: '10px 8px', fontSize: 12, minHeight: 40 }}
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
            style={{ border: 'none', padding: '10px 8px', fontSize: 12, minHeight: 40 }}
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
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                    onClick={() => setShowPassword((v) => !v)}
                    style={{
                      position: 'absolute',
                      right: 6,
                      top: 4,
                      width: 36,
                      height: 36,
                      display: 'inline-flex',
                      alignItems: 'center',
                      justifyContent: 'center',
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
              <label style={{ fontSize: 14, fontWeight: 600, display: 'block', marginBottom: 6 }}>
                Multi-Device Sync Key (PFKEY:...) or Android Account JSON
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
