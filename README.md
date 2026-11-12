# ⚡ PulseForge AI — Android Application

PulseForge AI is a comprehensive, production-ready Android fitness and nutrition ecosystem engineered with **Kotlin**, **Jetpack Compose**, **Clean Architecture**, **Room SQLite**, and **On-Device Vision OCR**.

---

## 📱 Generated APK Files

| Build Variant | Path | File Size | Status |
| :--- | :--- | :--- | :--- |
| **Debug APK** | `app/build/outputs/apk/debug/app-debug.apk` | ~56.2 MB | ✅ Built & Verified |
| **Signed Release APK** | `app/build/outputs/apk/release/app-release.apk` | ~50.3 MB | ✅ Built & Signed with Web OTA Auto-Updater |

### Direct File Links
* [app-debug.apk](file:///c:/Users/RITHISH/OneDrive/Desktop/projects/fitness/app/build/outputs/apk/debug/app-debug.apk)
* [app-release.apk](file:///c:/Users/RITHISH/OneDrive/Desktop/projects/fitness/app/build/outputs/apk/release/app-release.apk)

---

## 🌐 Web OTA Self-Update System (How it Works)

PulseForge AI comes with a built-in **Over-The-Air (OTA) In-App Auto-Update System**. When you release a new APK, you do **not** need the Google Play Store—the app can detect, download, and install updates directly from your web server or GitHub repository.

### 1. Update Architecture
1. **Remote Manifest (`version.json`):** You host a simple JSON file (template provided at `version.json` in this repo):
   ```json
   {
     "versionCode": 2,
     "versionName": "1.1.0",
     "apkUrl": "https://yourdomain.com/downloads/pulseforge-v1.1.0.apk",
     "releaseNotes": "• New AI food recognition model\n• Added custom theme presets\n• Progressive overload enhancements",
     "isMandatory": false,
     "fileSizeBytes": 52753699
   }
   ```
2. **Automated & Manual Checks:** The app compares its local `BuildConfig.VERSION_CODE` (currently `1`) with the remote `versionCode`.
3. **In-App Download Dialog:** If an update is detected, a native modal displays release notes and a real-time progress bar (`%` and MB downloaded).
4. **Seamless Package Installation:** Uses Android `FileProvider` (`content://...`) and `ACTION_VIEW` package archive intent with `android.permission.REQUEST_INSTALL_PACKAGES` to launch Android's native installer dialog without opening a browser.
5. **Configurable Endpoint:** Users or developers can set or change the update manifest URL at runtime in the **Settings / Themes** screen, or tap **Test Update** to preview the dialog.

---

## 🎯 Implemented Features Checklist

### 1. User Profiling & Anthropometrics
- [x] Comprehensive onboarding collecting Age, Gender, Height, Weight, Activity Level, Experience Level.
- [x] **Locality & Cultural Diet Intelligence:** Supports South Asian (Indian), Mediterranean, East Asian, Western, Latin American, and Middle Eastern pantries and dietary preferences (Non-veg, Vegetarian, Vegan, Halal, Keto).

### 2. Goal Feasibility Calculation Engine
- [x] **Alan Aragon & Eric Helms Physiological Hypertrophy Bounds:** Evaluates target muscle/weight gain rate against the natural maximum human protein synthesis limits (0.5%–1.5% body weight/month).
- [x] **Safe Fat Loss Guardrails:** Restricts fat loss to $\le 1.0\%$ body weight/week to prevent muscle catabolism and metabolic downregulation.
- [x] **Smart Recalibration:** If a target timeline is unrealistic, the engine calculates the mathematically maximum safe change in that timeframe and provides the recommended sustainable timeline.

### 3. Smart Nutrition & Dual-Engine Food Scanner
- [x] **4 Structured Daily Meals + Snacks:** Breakfast, Lunch, Dinner, Evening/Pre-Workout, and Hydration Tracker.
- [x] **AI Food Plate Recognition:** Detects food items, segments portions, and estimates calories and macros.
- [x] **Interactive User Verification BottomSheet:** Users verify and edit AI-scanned quantities before committing them to the meal log.
- [x] **Nutrition Facts Label OCR:** On-device Google ML Kit text recognition parses Calories, Total Fat, Protein, Carbohydrates, Dietary Fiber, and Serving Size.
- [x] **Manual Macro Entry:** Quick entry dialog for custom foods and portion sizes.

### 4. High-Protein Shake & Food Recommender
- [x] Evaluates remaining daily macro deficits on-the-fly.
- [x] Recommends categorized protein shakes (Hypertrophy Mass, Lean Cutting, Bedtime Casein, Plant-Based) matching regional ingredients (Desi Sattu & Almond Elixir, Peanut Butter Whey Fusion, Greek Yogurt Fig Cream, etc.).

### 5. Advanced Gym & Equipment Logger
- [x] Detailed equipment tracking: Barbell, Dumbbell, Cable pulley, Pin-loaded machine, Plate-loaded/Smith machine, Bodyweight, and Cardio.
- [x] Equipment specifics: Pin weights, plate counts, seat notch settings, and cable heights.
- [x] Multi-set tracking: Weight (kg), Reps, RPE (Rate of Perceived Exertion).
- [x] **Estimated 1RM Engine:** Brzycki & Wathan equations calculated in real-time.
- [x] **Caloric Burn Engine:** MET-based expenditure plus resistance volume tonnage EPOC factor.

### 6. Interactive Performance Analytics & Graphs
- [x] **Multi-Horizon Filtering:** Daily, Weekly, Monthly, and All-Time Personal Records (PRs).
- [x] Hardware-accelerated Canvas Bar Charts for Kcal Burned per session.
- [x] 1RM Strength Progression line curves.
- [x] **Muscle Volume Heatmap / Distribution:** Tracks weekly set volumes across Pectorals, Lats, Quads, Hamstrings, Deltoids, Arms, and Core to ensure balanced hypertrophy (10–20 optimal sets/week).
- [x] Gym Equipment Tonnage Breakdown.

### 7. Structured Weekly Review Sheet & Algorithmic AI Coach
- [x] 7-day adherence tracking: Calorie budget compliance %, Protein hit rate %, Workouts completed vs target, and Volume tonnage.
- [x] Subjective fatigue and joint stress slider (1 to 5).
- [x] **Algorithmic Recalibration:** Automatically detects plateaus, adjusts calories ($\pm 100\text{--}175\text{ kcal}$), or prescribes structured deload weeks and diet breaks when fatigue spikes.

### 8. 7-Atmospheric Theme System & Adaptive Bento Layout
- [x] **Dark & Light Modes:** Supported across all themes.
- [x] **7 Curated Visual Themes:**
  1. ⚡ **Power:** Cyber Neon Green (`#00FF87`) & Hyper Volt (`#FFEB3B`)
  2. 🔥 **Fire:** Blaze Crimson (`#FF3D00`) & Solar Amber (`#FFB300`)
  3. 🌿 **Calm:** Sage Emerald (`#10B981`) & Frosted Teal (`#06B6D4`)
  4. 🔮 **Peace:** Serene Lavender (`#A855F7`) & Twilight Indigo (`#6366F1`)
  5. ☀️ **Warm:** Terracotta Amber (`#F59E0B`) & Sunset Peach (`#FB923C`)
  6. 🌑 **Mood:** Titanium Silver (`#94A3B8`) & Deep Obsidian (`#07080A`)
  7. ⚪ **Studio Light:** Clean Athletic White & Cobalt Blue (`#2563EB`)
- [x] **Dynamic Adaptive Layout Dispatcher:** Automatically rearranges home dashboard cards based on **Time of Day** (Morning breakfast $\rightarrow$ Midday workout $\rightarrow$ Night review) and **Primary User Goal**.

---

## 🛠️ Build & Installation Instructions

### Prerequisites
* JDK 17 or JDK 21
* Android SDK (API 34)

### Building the APKs
```powershell
# Set environment variables
$env:JAVA_HOME = "C:\Program Files\Android\openjdk\jdk-21.0.8"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"

# Run Unit Tests
& "C:\gradle\gradle-8.7\bin\gradle.bat" :app:testDebugUnitTest --no-daemon

# Build Debug APK
& "C:\gradle\gradle-8.7\bin\gradle.bat" :app:assembleDebug --no-daemon

# Build Signed Release APK
& "C:\gradle\gradle-8.7\bin\gradle.bat" :app:assembleRelease --no-daemon
```

### Installing onto an Android Device or Emulator
```powershell
# Using ADB
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
# Or release
adb install -r "app\build\outputs\apk\release\app-release.apk"
```

