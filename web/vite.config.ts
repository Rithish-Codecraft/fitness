import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import fs from 'fs';
import path from 'path';

const rootVersionPath = path.resolve(__dirname, '../version.json');
const versionManifest = fs.existsSync(rootVersionPath)
  ? JSON.parse(fs.readFileSync(rootVersionPath, 'utf-8'))
  : {
      versionCode: 12,
      versionName: '1.7.1',
      apkUrl: 'https://raw.githubusercontent.com/Rithish-Codecraft/fitness/main/releases/app-release.apk',
      releaseNotes: '',
      sha256: ''
    };

export default defineConfig({
  plugins: [react()],
  define: {
    __APP_VERSION_MANIFEST__: JSON.stringify(versionManifest)
  }
});
