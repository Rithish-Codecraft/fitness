/// <reference types="vite/client" />

interface VersionManifest {
  versionCode: number;
  versionName: string;
  apkUrl: string;
  releaseNotes: string;
  isMandatory: boolean;
  minSupportedVersion: number;
  fileSizeBytes: number;
  sha256: string;
}

declare const __APP_VERSION_MANIFEST__: VersionManifest;
