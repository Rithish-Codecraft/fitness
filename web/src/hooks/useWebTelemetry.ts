import { useState, useEffect, useCallback } from 'react';
import { GpsWaypoint, PhoneSensorTelemetry } from '../types/models';

const EMPTY_TELEMETRY: PhoneSensorTelemetry = {
  stepCount: null,
  stepCadenceSpm: null,
  activityType: 'Still / Sedentary',
  distanceMeters: null,
  speedKmh: null,
  paceMinPerKm: null,
  gpsRoute: [],
  elevationGainMeters: null,
  floorsClimbed: null,
  activeCaloriesBurned: null,
  sleepStartTime: '-',
  wakeTime: '-',
  sleepTotalHours: null
};

function haversineMeters(a: GpsWaypoint, b: GpsWaypoint): number {
  const R = 6371000;
  const toRad = (deg: number) => (deg * Math.PI) / 180;
  const dLat = toRad(b.latitude - a.latitude);
  const dLon = toRad(b.longitude - a.longitude);
  const lat1 = toRad(a.latitude);
  const lat2 = toRad(b.latitude);
  const h =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) ** 2;
  return 2 * R * Math.asin(Math.sqrt(h));
}

export function useWebTelemetry(initial?: PhoneSensorTelemetry) {
  const [telemetry, setTelemetry] = useState<PhoneSensorTelemetry>(
    initial ?? EMPTY_TELEMETRY
  );
  const [gpsActive, setGpsActive] = useState(false);
  const [gpsError, setGpsError] = useState<string | null>(null);

  const enableBrowserGps = useCallback(() => {
    if (!('geolocation' in navigator)) {
      setGpsError('Geolocation API unavailable in this browser');
      return;
    }
    setGpsError(null);
    setGpsActive(true);
  }, []);

  useEffect(() => {
    if (!gpsActive || !('geolocation' in navigator)) return;

    const watchId = navigator.geolocation.watchPosition(
      (pos) => {
        const wp: GpsWaypoint = {
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
          altitudeMeters: pos.coords.altitude ?? 0,
          timestampMillis: pos.timestamp
        };

        setTelemetry((prev) => {
          const last = prev.gpsRoute[prev.gpsRoute.length - 1];
          const delta = last ? haversineMeters(last, wp) : 0;
          const nextRoute = !last || delta >= 2 ? [...prev.gpsRoute, wp] : prev.gpsRoute;
          const nextDist =
            delta >= 2 ? (prev.distanceMeters ?? 0) + delta : prev.distanceMeters;
          const speedKmh =
            pos.coords.speed != null && pos.coords.speed > 0
              ? pos.coords.speed * 3.6
              : prev.speedKmh;
          const paceMinPerKm =
            speedKmh != null && speedKmh > 0.5 ? 60 / speedKmh : prev.paceMinPerKm;
          const altGain =
            last && wp.altitudeMeters - last.altitudeMeters > 0.5
              ? (prev.elevationGainMeters ?? 0) + (wp.altitudeMeters - last.altitudeMeters)
              : prev.elevationGainMeters;

          return {
            ...prev,
            gpsRoute: nextRoute,
            distanceMeters: nextDist,
            speedKmh,
            paceMinPerKm,
            elevationGainMeters: altGain,
            floorsClimbed: altGain != null ? Math.floor(altGain / 3) : prev.floorsClimbed,
            activityType:
              speedKmh != null && speedKmh > 25
                ? 'In Vehicle'
                : speedKmh != null && speedKmh > 10
                  ? 'Cycling'
                  : speedKmh != null && speedKmh > 6
                    ? 'Running'
                    : speedKmh != null && speedKmh > 1.2
                      ? 'Walking'
                      : prev.activityType
          };
        });
      },
      (err) => {
        setGpsError(err.message);
        setGpsActive(false);
      },
      { enableHighAccuracy: true, maximumAge: 3000, timeout: 10000 }
    );

    return () => navigator.geolocation.clearWatch(watchId);
  }, [gpsActive]);

  const importTelemetrySnapshot = useCallback((incoming: Partial<PhoneSensorTelemetry>) => {
    setTelemetry((prev) => ({
      ...prev,
      ...incoming
    }));
  }, []);

  return {
    telemetry,
    gpsActive,
    gpsError,
    enableBrowserGps,
    importTelemetrySnapshot
  };
}
