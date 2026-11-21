import React from 'react';
import {
  Activity,
  BedDouble,
  Bike,
  Car,
  Flame,
  Footprints,
  Gauge,
  MapPin,
  Mountain,
  Route,
  Timer
} from 'lucide-react';
import { PhoneSensorTelemetry } from '../types/models';

interface TelemetryCardProps {
  telemetry: PhoneSensorTelemetry;
  gpsActive: boolean;
  gpsError: string | null;
  onEnableGps: () => void;
}

interface TileProps {
  icon: React.ReactNode;
  label: string;
  value: string;
  unit: string;
  accent: string;
}

const SensorTile: React.FC<TileProps> = ({ icon, label, value, unit, accent }) => (
  <div className="sensor-tile">
    <div className="tile-header">
      <span
        className="icon-badge"
        style={{ background: `${accent}26`, color: accent }}
      >
        {icon}
      </span>
      <span style={{ fontSize: 11, fontWeight: 700 }} className="muted">
        {label}
      </span>
    </div>
    <div className="tile-value-row">
      <span style={{ color: accent, display: 'inline-flex' }}>{icon}</span>
      <span style={{ opacity: value === '-' ? 0.4 : 1 }}>{value}</span>
    </div>
    <div style={{ fontSize: 10, marginTop: 4 }} className="muted">
      {unit}
    </div>
  </div>
);

export const TelemetryCard: React.FC<TelemetryCardProps> = ({
  telemetry,
  gpsActive,
  gpsError,
  onEnableGps
}) => {
  const activityIcon =
    telemetry.activityType === 'Running' || telemetry.activityType === 'Walking' ? (
      <Footprints size={15} />
    ) : telemetry.activityType === 'Cycling' ? (
      <Bike size={15} />
    ) : telemetry.activityType === 'In Vehicle' ? (
      <Car size={15} />
    ) : (
      <Activity size={15} />
    );

  const stepsVal =
    telemetry.stepCount != null && telemetry.stepCount > 0
      ? String(telemetry.stepCount)
      : '-';
  const cadenceVal =
    telemetry.stepCadenceSpm != null && telemetry.stepCadenceSpm > 0
      ? String(Math.round(telemetry.stepCadenceSpm))
      : '-';
  const distVal =
    telemetry.distanceMeters != null && telemetry.distanceMeters > 0
      ? (telemetry.distanceMeters / 1000).toFixed(2)
      : '-';
  const speedVal =
    telemetry.speedKmh != null && telemetry.speedKmh > 0
      ? `${telemetry.speedKmh.toFixed(1)} km/h`
      : '-';
  const paceUnit =
    telemetry.paceMinPerKm != null && telemetry.paceMinPerKm > 0
      ? `pace: ${telemetry.paceMinPerKm.toFixed(1)} min/km`
      : 'pace: -';
  const elevVal =
    telemetry.elevationGainMeters != null && telemetry.elevationGainMeters > 0
      ? `${Math.round(telemetry.elevationGainMeters)}m`
      : '-';
  const floorsUnit =
    telemetry.floorsClimbed != null && telemetry.floorsClimbed > 0
      ? `${telemetry.floorsClimbed} floors`
      : 'barometer: -';
  const calVal =
    telemetry.activeCaloriesBurned != null && telemetry.activeCaloriesBurned > 0
      ? String(Math.round(telemetry.activeCaloriesBurned))
      : '-';
  const sleepVal =
    telemetry.sleepTotalHours != null && telemetry.sleepTotalHours > 0
      ? `${telemetry.sleepTotalHours}h`
      : '-';
  const sleepUnit =
    telemetry.sleepStartTime && telemetry.sleepStartTime !== '-'
      ? `${telemetry.sleepStartTime} – ${telemetry.wakeTime}`
      : 'Android Fit / ambient';

  return (
    <div className="card">
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          gap: 10,
          marginBottom: 6
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span
            style={{
              width: 10,
              height: 10,
              borderRadius: 999,
              background: '#10b981',
              display: 'inline-block'
            }}
          />
          <strong style={{ fontSize: 15 }}>Live Telemetry &amp; Android Fit Sync</strong>
        </div>
        <button className="btn" onClick={onEnableGps} style={{ fontSize: 11 }}>
          <MapPin size={13} color={gpsActive ? '#10b981' : '#38bdf8'} />
          {gpsActive ? 'Web GPS Active' : 'Enable Web GPS'}
        </button>
      </div>
      {gpsError && (
        <p style={{ fontSize: 11, color: '#ef4444', marginBottom: 10 }}>
          GPS: {gpsError}
        </p>
      )}

      <div className="grid-2">
        <SensorTile
          icon={<Footprints size={15} />}
          label="Step Count"
          value={stepsVal}
          unit="total steps"
          accent="#38bdf8"
        />
        <SensorTile
          icon={<Gauge size={15} />}
          label="Step Cadence"
          value={cadenceVal}
          unit="steps / min"
          accent="#f59e0b"
        />
        <SensorTile
          icon={activityIcon}
          label="Activity Type"
          value={telemetry.activityType}
          unit="auto-detected"
          accent="#10b981"
        />
        <SensorTile
          icon={<Route size={15} />}
          label="Distance Traveled"
          value={distVal}
          unit="km"
          accent="#a855f7"
        />
        <SensorTile
          icon={<Timer size={15} />}
          label="Speed & Pace"
          value={speedVal}
          unit={paceUnit}
          accent="#ef4444"
        />
        <SensorTile
          icon={<Mountain size={15} />}
          label="Elevation & Floors"
          value={elevVal}
          unit={floorsUnit}
          accent="#14b8a6"
        />
        <SensorTile
          icon={<Flame size={15} />}
          label="Active Calories"
          value={calVal}
          unit="kcal burned"
          accent="#ff6b35"
        />
        <SensorTile
          icon={<BedDouble size={15} />}
          label="Sleep Estimate"
          value={sleepVal}
          unit={sleepUnit}
          accent="#6366f1"
        />
      </div>

      <div
        className="sensor-tile"
        style={{
          marginTop: 12,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <span
            className="icon-badge"
            style={{ background: '#38bdf826', color: '#38bdf8', width: 32, height: 32 }}
          >
            <MapPin size={16} />
          </span>
          <div>
            <div style={{ fontSize: 11 }} className="muted">
              GPS Workout Route
            </div>
            <div style={{ fontSize: 14, fontWeight: 800 }}>
              {telemetry.gpsRoute.length > 0
                ? `${telemetry.gpsRoute.length} waypoints recorded`
                : '-'}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
