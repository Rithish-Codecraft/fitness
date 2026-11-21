import React, { useState } from 'react';
import { ArrowRight, CheckCircle2 } from 'lucide-react';
import { AuthAccount } from '../auth/authManager';
import { ActivityLevel, Gender, GoalType, UserProfile } from '../types/models';
import { ACTIVITY_LABELS, GOAL_LABELS, calculateTargets } from '../engine/calculators';

interface PersonalWizardProps {
  account: AuthAccount;
  initialProfile: UserProfile;
  onComplete: (profile: UserProfile) => void;
}

export const PersonalWizard: React.FC<PersonalWizardProps> = ({
  account,
  initialProfile,
  onComplete
}) => {
  const [age, setAge] = useState(String(initialProfile.age));
  const [gender, setGender] = useState<Gender>(initialProfile.gender);
  const [heightCm, setHeightCm] = useState(String(initialProfile.heightCm));
  const [currentWeightKg, setCurrentWeightKg] = useState(String(initialProfile.currentWeightKg));
  const [targetWeightKg, setTargetWeightKg] = useState(String(initialProfile.targetWeightKg));
  const [goal, setGoal] = useState<GoalType>(initialProfile.primaryGoal);
  const [activity, setActivity] = useState<ActivityLevel>(initialProfile.activityLevel);

  const handleFinish = (e: React.FormEvent) => {
    e.preventDefault();
    const base: UserProfile = {
      ...initialProfile,
      id: account.userId,
      name: account.fullName,
      age: Number(age) || 25,
      gender,
      heightCm: Number(heightCm) || 175,
      currentWeightKg: Number(currentWeightKg) || 72,
      targetWeightKg: Number(targetWeightKg) || 76,
      primaryGoal: goal,
      activityLevel: activity
    };
    const targets = calculateTargets(base);
    onComplete({
      ...base,
      tdeeKcal: targets.tdeeKcal,
      targetKcal: targets.targetKcal,
      targetProteinG: targets.targetProteinG,
      targetCarbsG: targets.targetCarbsG,
      targetFatG: targets.targetFatG
    });
  };

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
      <form
        className="card"
        onSubmit={handleFinish}
        style={{ width: '100%', maxWidth: 480, padding: 26, display: 'flex', flexDirection: 'column', gap: 12 }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <CheckCircle2 size={22} color="#10b981" />
          <div>
            <strong style={{ fontSize: 17 }}>Welcome, {account.fullName}</strong>
            <div style={{ fontSize: 12, color: '#10b981', fontWeight: 800 }}>
              Assigned PulseForge ID: {account.userId}
            </div>
          </div>
        </div>
        <div className="grid-2">
          <div>
            <label style={{ fontSize: 11 }} className="muted">Age</label>
            <input className="input" type="number" value={age} onChange={(e) => setAge(e.target.value)} />
          </div>
          <div>
            <label style={{ fontSize: 11 }} className="muted">Sex</label>
            <select className="input" value={gender} onChange={(e) => setGender(e.target.value as Gender)}>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
          </div>
          <div>
            <label style={{ fontSize: 11 }} className="muted">Height (cm)</label>
            <input className="input" type="number" value={heightCm} onChange={(e) => setHeightCm(e.target.value)} />
          </div>
          <div>
            <label style={{ fontSize: 11 }} className="muted">Current Weight (kg)</label>
            <input className="input" type="number" value={currentWeightKg} onChange={(e) => setCurrentWeightKg(e.target.value)} />
          </div>
        </div>

        <div>
          <label style={{ fontSize: 11 }} className="muted">Target Weight (kg)</label>
          <input className="input" type="number" value={targetWeightKg} onChange={(e) => setTargetWeightKg(e.target.value)} />
        </div>

        <div>
          <label style={{ fontSize: 11 }} className="muted">Primary Goal</label>
          <select className="input" value={goal} onChange={(e) => setGoal(e.target.value as GoalType)}>
            {Object.entries(GOAL_LABELS).map(([k, label]) => (
              <option key={k} value={k}>{label}</option>
            ))}
          </select>
        </div>

        <div>
          <label style={{ fontSize: 11 }} className="muted">Activity Level</label>
          <select className="input" value={activity} onChange={(e) => setActivity(e.target.value as ActivityLevel)}>
            {Object.entries(ACTIVITY_LABELS).map(([k, label]) => (
              <option key={k} value={k}>{label}</option>
            ))}
          </select>
        </div>

        <button className="btn btn-primary" type="submit" style={{ padding: 12, marginTop: 6 }}>
          Calculate Targets &amp; Enter Dashboard <ArrowRight size={15} />
        </button>
      </form>
    </div>
  );
};
