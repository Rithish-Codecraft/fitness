import { createClient, User } from '@supabase/supabase-js';
import { UserDataBundle } from './authManager';
import { PhoneSensorTelemetry } from '../types/models';

const SUPABASE_URL =
  import.meta.env.VITE_SUPABASE_URL || 'https://qngiwslqsgnkdpwkxjza.supabase.co';
const SUPABASE_ANON_KEY =
  import.meta.env.VITE_SUPABASE_ANON_KEY ||
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFuZ2l3c2xxc2dua2Rwd2t4anphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTUyODY0MDUsImV4cCI6MjExMDg2MjQwNX0.sUZtpVgyPc7xR_sRbu4m7h6YURukwdYWPp1YJMpKF9s';

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY, {
  auth: {
    autoRefreshToken: true,
    persistSession: true,
    detectSessionInUrl: true
  }
});

export function derivePulseForgeId(uid: string): string {
  let hash = 0;
  for (let i = 0; i < uid.length; i++) {
    hash = (hash * 31 + uid.charCodeAt(i)) >>> 0;
  }
  const sixDigits = 100000 + (hash % 900000);
  return `PF-${sixDigits}`;
}

export async function signInWithGoogleOAuth(): Promise<string | null> {
  const { error } = await supabase.auth.signInWithOAuth({
    provider: 'google',
    options: {
      redirectTo: window.location.origin
    }
  });
  return error ? error.message : null;
}

export async function loadCloudUserState(user: User): Promise<{
  pulseforgeId: string;
  bundle: Partial<UserDataBundle> | null;
  telemetry: Partial<PhoneSensorTelemetry> | null;
  isNewCloudProfile: boolean;
}> {
  const fallbackId = derivePulseForgeId(user.id);
  const { data: profRow } = await supabase
    .from('user_profiles')
    .select('pulseforge_id, full_name, email, profile_json')
    .eq('id', user.id)
    .maybeSingle();

  const { data: syncRow } = await supabase
    .from('user_sync_state')
    .select('foods_json, workouts_json, telemetry_json, lifestyle_json, updated_at')
    .eq('id', user.id)
    .maybeSingle();

  if (!profRow || !profRow.profile_json) {
    return {
      pulseforgeId: fallbackId,
      bundle: null,
      telemetry: null,
      isNewCloudProfile: true
    };
  }

  const profileJson = profRow.profile_json as UserDataBundle['profile'];
  const hasBaseline = Boolean(
    profileJson &&
      (profileJson.isOnboarded === true ||
        (profileJson.age > 0 && profileJson.heightCm > 0 && profileJson.currentWeightKg > 0))
  );

  return {
    pulseforgeId: profRow.pulseforge_id || fallbackId,
    bundle: {
      profile: { ...profileJson, isOnboarded: hasBaseline },
      foods: Array.isArray(syncRow?.foods_json) ? syncRow.foods_json : undefined,
      workouts: Array.isArray(syncRow?.workouts_json) ? syncRow.workouts_json : undefined,
      lifestyle: syncRow?.lifestyle_json || undefined,
      lastSync: syncRow?.updated_at
        ? new Date(syncRow.updated_at).toLocaleTimeString()
        : 'Cloud Synced'
    },
    telemetry: syncRow?.telemetry_json || null,
    isNewCloudProfile: !hasBaseline
  };
}

export async function saveCloudUserState(
  user: User,
  pulseforgeId: string,
  bundle: UserDataBundle,
  telemetry: PhoneSensorTelemetry
): Promise<void> {
  const { error: profErr } = await supabase.from('user_profiles').upsert(
    {
      id: user.id,
      pulseforge_id: pulseforgeId,
      full_name: bundle.profile.name,
      email: user.email || '',
      profile_json: bundle.profile,
      updated_at: new Date().toISOString()
    },
    { onConflict: 'id' }
  );
  if (profErr) console.warn('Cloud profile sync warning:', profErr.message);

  const { error: syncErr } = await supabase.from('user_sync_state').upsert(
    {
      id: user.id,
      foods_json: bundle.foods,
      workouts_json: bundle.workouts,
      telemetry_json: telemetry,
      lifestyle_json: bundle.lifestyle,
      updated_at: new Date().toISOString()
    },
    { onConflict: 'id' }
  );
  if (syncErr) console.warn('Cloud sync_state warning:', syncErr.message);
}
