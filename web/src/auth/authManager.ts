import {
  DailyRecording,
  FoodItem,
  LifestyleSleepAnalysis,
  UserProfile,
  WorkoutSession
} from '../types/models';

export interface AuthAccount {
  userId: string;
  fullName: string;
  email: string;
  passwordHash: string;
  createdAt: string;
}

export interface UserDataBundle {
  profile: UserProfile;
  foods: FoodItem[];
  workouts: WorkoutSession[];
  lifestyle: LifestyleSleepAnalysis;
  dailyRecordings?: DailyRecording[];
  lastSync: string;
}

export interface MultiDeviceSyncPayload {
  format: 'pulseforge-multi-device-v1';
  account: AuthAccount;
  data?: Partial<UserDataBundle>;
}

const ACCOUNTS_KEY = 'pulseforge_registered_accounts_v1';
const ACTIVE_USER_KEY = 'pulseforge_active_user_id_v1';
const WIZARD_FLAG_PREFIX = 'pulseforge_needs_wizard_';
const DATA_PREFIX = 'pulseforge_user_data_v1_';

export async function sha256Hex(input: string): Promise<string> {
  const bytes = new TextEncoder().encode(input);
  const hashBuffer = await crypto.subtle.digest('SHA-256', bytes);
  return Array.from(new Uint8Array(hashBuffer))
    .map((b) => b.toString(16).padStart(2, '0'))
    .join('');
}

export function getRegisteredAccounts(): AuthAccount[] {
  try {
    const raw = localStorage.getItem(ACCOUNTS_KEY);
    return raw ? (JSON.parse(raw) as AuthAccount[]) : [];
  } catch {
    return [];
  }
}

function saveRegisteredAccounts(accounts: AuthAccount[]): void {
  localStorage.setItem(ACCOUNTS_KEY, JSON.stringify(accounts));
}

export function getActiveSession(): {
  account: AuthAccount | null;
  needsWizard: boolean;
} {
  const activeId = localStorage.getItem(ACTIVE_USER_KEY);
  if (!activeId) return { account: null, needsWizard: false };
  const account = getRegisteredAccounts().find((a) => a.userId === activeId) ?? null;
  if (!account) return { account: null, needsWizard: false };
  const needsWizard = localStorage.getItem(`${WIZARD_FLAG_PREFIX}${account.userId}`) === 'true';
  return { account, needsWizard };
}

export async function signUpAccount(
  fullName: String,
  email: string,
  password: string
): Promise<{ ok: true; account: AuthAccount } | { ok: false; error: string }> {
  const cleanName = fullName.trim();
  const cleanEmail = email.trim().toLowerCase();
  if (cleanName.length < 2) {
    return { ok: false, error: 'Please enter your full name (at least 2 characters).' };
  }
  if (!cleanEmail || (!cleanEmail.includes('@') && cleanEmail.length < 6)) {
    return { ok: false, error: 'Please enter a valid email address or phone number.' };
  }
  if (password.length < 6) {
    return { ok: false, error: 'Password must be at least 6 characters.' };
  }

  const accounts = getRegisteredAccounts();
  if (accounts.some((a) => a.email.toLowerCase() === cleanEmail)) {
    return { ok: false, error: 'An account with this email already exists. Please sign in.' };
  }

  const existingIds = new Set(accounts.map((a) => a.userId));
  let newId = '';
  do {
    newId = `PF-${Math.floor(100000 + Math.random() * 900000)}`;
  } while (existingIds.has(newId));

  const account: AuthAccount = {
    userId: newId,
    fullName: cleanName,
    email: cleanEmail,
    passwordHash: await sha256Hex(password),
    createdAt: new Date().toISOString().slice(0, 16).replace('T', ' ')
  };

  saveRegisteredAccounts([...accounts, account]);
  localStorage.setItem(ACTIVE_USER_KEY, account.userId);
  localStorage.setItem(`${WIZARD_FLAG_PREFIX}${account.userId}`, 'true');

  return { ok: true, account };
}

export async function loginAccount(
  identifier: string,
  password: string
): Promise<{ ok: true; account: AuthAccount } | { ok: false; error: string }> {
  const cleanId = identifier.trim();
  if (!cleanId || !password) {
    return { ok: false, error: 'Enter your Email / User ID (PF-XXXXXX) and password.' };
  }

  const account = getRegisteredAccounts().find(
    (a) =>
      a.email.toLowerCase() === cleanId.toLowerCase() ||
      a.userId.toLowerCase() === cleanId.toLowerCase()
  );

  if (!account) {
    return {
      ok: false,
      error: `No account found for "${cleanId}" on this device. Sign up or use Multi-Device Sync.`
    };
  }

  const hash = await sha256Hex(password);
  if (account.passwordHash !== hash) {
    return { ok: false, error: 'Incorrect password. Please try again.' };
  }

  localStorage.setItem(ACTIVE_USER_KEY, account.userId);
  localStorage.setItem(`${WIZARD_FLAG_PREFIX}${account.userId}`, 'false');
  return { ok: true, account };
}

export function completePersonalWizard(userId: string): void {
  localStorage.setItem(`${WIZARD_FLAG_PREFIX}${userId}`, 'false');
}

export function logoutAccount(): void {
  localStorage.removeItem(ACTIVE_USER_KEY);
}

export function deleteAccountData(userId: string): void {
  const remaining = getRegisteredAccounts().filter((a) => a.userId !== userId);
  saveRegisteredAccounts(remaining);
  localStorage.removeItem(`${DATA_PREFIX}${userId}`);
  localStorage.removeItem(`${WIZARD_FLAG_PREFIX}${userId}`);
  localStorage.removeItem(ACTIVE_USER_KEY);
}

export function loadUserData(userId: string, fallback: UserDataBundle): UserDataBundle {
  try {
    const raw = localStorage.getItem(`${DATA_PREFIX}${userId}`);
    if (raw) {
      return JSON.parse(raw) as UserDataBundle;
    }
  } catch {
    // ignore parse failure
  }
  return fallback;
}

export function saveUserData(userId: string, bundle: UserDataBundle): void {
  localStorage.setItem(`${DATA_PREFIX}${userId}`, JSON.stringify(bundle));
}

export function createMultiDeviceSyncKey(
  account: AuthAccount,
  data?: UserDataBundle
): string {
  const payload: MultiDeviceSyncPayload = {
    format: 'pulseforge-multi-device-v1',
    account,
    data
  };
  const json = JSON.stringify(payload);
  const bytes = new TextEncoder().encode(json);
  let bin = '';
  bytes.forEach((b) => {
    bin += String.fromCharCode(b);
  });
  return `PFKEY:${btoa(bin)}`;
}

export function importMultiDeviceSyncKey(
  rawInput: string
): { ok: true; account: AuthAccount; data?: Partial<UserDataBundle> } | { ok: false; error: string } {
  try {
    const trimmed = rawInput.trim();
    const json = trimmed.startsWith('PFKEY:')
      ? new TextDecoder().decode(
          Uint8Array.from(atob(trimmed.slice(6)), (c) => c.charCodeAt(0))
        )
      : trimmed;

    const parsed = JSON.parse(json) as Partial<MultiDeviceSyncPayload> & {
      authAccount?: AuthAccount;
      userProfile?: UserProfile;
    };

    const account = parsed.account ?? parsed.authAccount;
    if (!account || !account.userId || !account.email || !account.passwordHash) {
      return { ok: false, error: 'Invalid Multi-Device Sync Key or Account JSON.' };
    }

    const accounts = getRegisteredAccounts().filter((a) => a.userId !== account.userId);
    saveRegisteredAccounts([...accounts, account]);
    localStorage.setItem(ACTIVE_USER_KEY, account.userId);
    localStorage.setItem(`${WIZARD_FLAG_PREFIX}${account.userId}`, 'false');

    const incomingData = parsed.data ?? (parsed.userProfile ? { profile: parsed.userProfile } : undefined);
    return { ok: true, account, data: incomingData };
  } catch {
    return { ok: false, error: 'Could not decode Multi-Device Sync Key. Check the pasted text.' };
  }
}
