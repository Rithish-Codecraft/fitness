# Technical Requirements Document (TRD)
## Project Name: PulseForge AI (Android Application)
**Version:** 1.0.0  
**Target OS:** Android 8.0 (API Level 26) through Android 15 (API Level 35)  
**Architecture:** Offline-First Clean Architecture + MVVM (Model-View-ViewModel) + Dynamic Composition  
**Date:** November 2026

---

## 1. System Architecture Overview

PulseForge AI follows **Modern Android Clean Architecture** with **Offline-First Reactive Data Flow**.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │     Jetpack Compose / Reactive UI Engine                         │  │
│  │     - Dynamic Adaptive Layout Dispatcher (Time & Goal Aware)     │  │
│  │     - Multi-Theme Token Provider (Power, Fire, Calm, Peace, etc) │  │
│  │     - Vico Charts / Canvas Graphing Engine                       │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
│                                     │ StateFlow / Events               │
│  ┌──────────────────────────────────▼───────────────────────────────┐  │
│  │     ViewModels (State Holders & Business Presenters)             │  │
│  │     - NutritionViewModel, WorkoutViewModel, AnalyticsViewModel    │  │
│  │     - AIReviewViewModel, FeasibilityViewModel, ThemeViewModel     │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
└─────────────────────────────────────┼──────────────────────────────────┘
                                      │ Invokes UseCases
┌─────────────────────────────────────▼──────────────────────────────────┐
│                           DOMAIN LAYER                                 │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │     Use Cases & Physiological Calculation Engines                │  │
│  │     - CalculateTDEEUseCase (Mifflin-St Jeor + Activity Factor)   │  │
│  │     - ValidateGoalFeasibilityUseCase (Aragon/Helms Boundary)     │  │
│  │     - Estimate1RMUseCase (Brzycki / Epley Formulas)              │  │
│  │     - ComputeCaloricBurnUseCase (MET x Bodyweight x Duration)    │  │
│  │     - AdaptiveCoachRecommendationEngine                          │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
└─────────────────────────────────────┼──────────────────────────────────┘
                                      │ Implements Repositories
┌─────────────────────────────────────▼──────────────────────────────────┐
│                            DATA LAYER                                  │
│  ┌───────────────────────────────┐  ┌────────────────────────────────┐ │
│  │      Local Persistence        │  │       Remote / AI Services     │ │
│  │  - Room Database (SQLite)     │  │  - Gemini Multimodal Vision API│ │
│  │  - EncryptedDataStore (Pref)  │  │  - ML Kit Text OCR (Offline)   │ │
│  │  - Cache / WorkManager Sync   │  │  - OpenFoodFacts / Custom API  │ │
│  └───────────────────────────────┘  └────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Technology Stack & Framework Selection

| Layer | Technology | Justification |
| :--- | :--- | :--- |
| **Language & Runtime** | Kotlin 2.0+ / Java 17 | Type safety, Coroutines, Flow, null-safety, standard for modern Android. |
| **UI Toolkit** | Jetpack Compose + Material 3 | Declarative UI, frictionless dynamic theming, smooth animation transitions. |
| **State Management** | StateFlow, SharedFlow, MVI/MVVM | Unidirectional Data Flow (UDF), thread-safe asynchronous updates. |
| **Local Database** | Room Persistence Library 2.6+ (SQLite) | Robust ORM, compile-time query verification, native Flow observation. |
| **Dependency Injection** | Dagger-Hilt / Koin | Modular, clean architecture lifecycle management. |
| **Vision & AI Parsing** | Google Gemini 2.5/Flash Vision API + Google ML Kit Text Recognition | High-accuracy food plate parsing + zero-latency offline nutrition label OCR. |
| **Graphing & Charts** | Vico / MPAndroidChart / Compose Canvas | High-performance hardware-accelerated rendering for volume & 1RM charts. |
| **Camera & Media** | CameraX (Preview, ImageCapture, ImageAnalysis) | Robust cross-device camera lifecycle integration. |
| **Async & Background** | Kotlin Coroutines + WorkManager | Battery-conscious background sync, daily check-in generation, offline queue. |

---

## 3. Physiological & Algorithmic Engines

### 3.1 Energy Expenditure & TDEE (Mifflin-St Jeor)
$$\text{BMR}_{\text{men}} = (10 \times W) + (6.25 \times H) - (5 \times A) + 5$$
$$\text{BMR}_{\text{women}} = (10 \times W) + (6.25 \times H) - (5 \times A) - 161$$
$$\text{TDEE} = \text{BMR} \times \text{Activity Factor}$$
* Activity Multipliers: Sedentary: 1.20 | Lightly active: 1.375 | Moderately active: 1.55 | Very active: 1.725 | Extremely active: 1.90.

### 3.2 Goal Feasibility & Maximum Safe Rate Engine
```kotlin
data class FeasibilityReport(
    val isFeasible: Boolean,
    val targetWeeks: Int,
    val requestedChangeKg: Double,
    val maxSafeChangeKg: Double,
    val recommendedWeeks: Int,
    val warningMessage: String?,
    val dailyCalorieTarget: Int
)

class GoalFeasibilityCalculator {
    fun calculate(
        currentWeightKg: Double,
        targetWeightKg: Double,
        weeks: Int,
        tdee: Int,
        goalType: GoalType,
        experienceLevel: ExperienceLevel
    ): FeasibilityReport {
        val deltaKg = targetWeightKg - currentWeightKg
        val weeklyRate = kotlin.math.abs(deltaKg) / weeks

        return when (goalType) {
            GoalType.MUSCLE_GAIN, GoalType.WEIGHT_GAIN -> {
                // Alan Aragon Hypertrophy Caps (% of bodyweight per month)
                val maxMonthlyRatePercent = when (experienceLevel) {
                    ExperienceLevel.BEGINNER -> 0.015
                    ExperienceLevel.INTERMEDIATE -> 0.010
                    ExperienceLevel.ADVANCED -> 0.005
                }
                val maxWeeklySafeKg = (currentWeightKg * maxMonthlyRatePercent) / 4.33
                val maxSafeChangeInWeeks = maxWeeklySafeKg * weeks
                val isFeasible = deltaKg <= maxSafeChangeInWeeks

                val recommendedWeeks = if (isFeasible) weeks 
                    else kotlin.math.ceil(deltaKg / maxWeeklySafeKg).toInt()
                
                // Surplus: 350-500 kcal for lean muscle building
                val surplus = if (experienceLevel == ExperienceLevel.BEGINNER) 400 else 250
                
                FeasibilityReport(
                    isFeasible = isFeasible,
                    targetWeeks = weeks,
                    requestedChangeKg = deltaKg,
                    maxSafeChangeKg = maxSafeChangeInWeeks,
                    recommendedWeeks = recommendedWeeks,
                    warningMessage = if (!isFeasible) 
                        "Natural muscle synthesis caps at ~${String.format("%.2f", maxWeeklySafeKg)}kg/week. Higher gains will result in excess adipose tissue." 
                        else null,
                    dailyCalorieTarget = tdee + surplus
                )
            }
            GoalType.WEIGHT_LOSS -> {
                // Max safe fat loss: 1.0% body weight/week
                val maxWeeklyLossKg = currentWeightKg * 0.010
                val minWeeklyLossKg = currentWeightKg * 0.005
                val targetLossKg = kotlin.math.abs(deltaKg)
                val isFeasible = weeklyRate <= maxWeeklyLossKg

                val recommendedWeeks = if (isFeasible) weeks 
                    else kotlin.math.ceil(targetLossKg / maxWeeklyLossKg).toInt()
                
                // 1 kg fat ~ 7700 kcal -> Daily Deficit = (weeklyRate * 7700) / 7
                val rawDeficit = ((targetLossKg / weeks) * 7700) / 7
                val safeDeficit = rawDeficit.coerceIn(300.0, (tdee * 0.25))

                FeasibilityReport(
                    isFeasible = isFeasible,
                    targetWeeks = weeks,
                    requestedChangeKg = deltaKg,
                    maxSafeChangeKg = maxWeeklyLossKg * weeks,
                    recommendedWeeks = recommendedWeeks,
                    warningMessage = if (!isFeasible) 
                        "Losing more than 1% body weight per week triggers metabolic slowdown and muscle wasting." 
                        else null,
                    dailyCalorieTarget = (tdee - safeDeficit).toInt()
                )
            }
            else -> FeasibilityReport(true, weeks, 0.0, 0.0, weeks, null, tdee)
        }
    }
}
```

### 3.3 Strength One Rep Max (1RM) & Calorie Burn
* **Brzycki Formula:** $\text{1RM} = \frac{W}{1.0278 - (0.0278 \times R)}$ (for $R \le 10$)
* **Wathan Formula:** $\text{1RM} = \frac{100 \times W}{48.8 + (53.8 \times e^{-0.075 \times R})}$ (for $R > 10$)
* **Resistance Calorie Burn:**
  $$E_{\text{strength}} = (\text{MET}_{\text{lift}} \times W_{\text{kg}} \times t_{\text{hr}}) + (V_{\text{tonnage}} \times 0.0125)$$
  * Where $\text{MET}_{\text{lift}} = 6.0$ for moderate resistance, $8.0$ for high-intensity powerlifting, and $V_{\text{tonnage}} = \sum (\text{sets} \times \text{reps} \times \text{weight in kg}) / 1000$.

---

## 4. AI Vision & Optical Recognition Pipeline

### 4.1 Plate & Meal Recognition Architecture
```
User Captures Plate Photo (CameraX)
   │
   ├── Step 1: Pre-process (1080p, Exif orientation correction, JPG compress ~300KB)
   │
   ├── Step 2: Dispatch to Gemini Multimodal Vision API
   │     Prompt: "Analyze this meal plate. Output strict JSON with:
   │              - List of detected foods
   │              - Estimated weight in grams or portion size
   │              - Estimated Calories, Protein(g), Carbs(g), Fat(g)
   │              - Confidence score (0.0-1.0)"
   │
   ├── Step 3: Parse Structured Response into Verification DTO
   │
   └── Step 4: Present Interactive Compose BottomSheet:
         ┌──────────────────────────────────────────────┐
         │  AI Food Identification                      │
         │  - Grilled Chicken Breast [160g] (265 kcal)  │
         │  - Steamed Jasmine Rice   [150g] (195 kcal)  │
         │  - Sauteed Broccoli       [80g]  (45 kcal)   │
         │  ------------------------------------------  │
         │  Total: 505 kcal | 44g P | 45g C | 12g F     │
         │  [Edit Portions]        [Confirm & Log Meal] │
         └──────────────────────────────────────────────┘
```

### 4.2 Nutrition Facts Label OCR (On-Device & Cloud Hybrid)
* **Local First:** Google ML Kit Text Recognition scans the Nutrition Facts table offline in $<300\text{ms}$.
* **Regex / NLP Parser:**
  * Regex filters extract `Calories`, `Total Fat \d+g`, `Protein \d+g`, `Carbohydrate \d+g`, `Serving Size`.
  * Fallback to Vision API if label format is non-standard or blurry.

---

## 5. Local Database Schema (Room SQLite)

```sql
-- User Profile & Goals
CREATE TABLE user_profile (
    id TEXT PRIMARY KEY NOT NULL,
    age INTEGER NOT NULL,
    gender TEXT NOT NULL,
    height_cm REAL NOT NULL,
    current_weight_kg REAL NOT NULL,
    target_weight_kg REAL NOT NULL,
    target_timeline_weeks INTEGER NOT NULL,
    primary_goal TEXT NOT NULL,          -- HYPERTROPHY, WEIGHT_LOSS, YOGA, etc.
    target_muscle_groups TEXT NOT NULL,   -- JSON array: ["CHEST", "LATS"]
    locality TEXT NOT NULL,              -- e.g. "SOUTH_ASIAN", "WESTERN", etc.
    diet_preference TEXT NOT NULL,       -- VEG, NON_VEG, VEGAN, HALAL
    tdee_kcal INTEGER NOT NULL,
    target_kcal INTEGER NOT NULL,
    target_protein_g INTEGER NOT NULL,
    target_carbs_g INTEGER NOT NULL,
    target_fat_g INTEGER NOT NULL,
    water_target_ml INTEGER NOT NULL,
    active_theme TEXT NOT NULL DEFAULT 'THEME_POWER',
    is_dark_mode INTEGER NOT NULL DEFAULT 1
);

-- Daily Food Consumption
CREATE TABLE meal_log (
    id TEXT PRIMARY KEY NOT NULL,
    date TEXT NOT NULL,                  -- YYYY-MM-DD
    meal_type TEXT NOT NULL,             -- BREAKFAST, LUNCH, DINNER, SNACK
    created_at INTEGER NOT NULL
);

CREATE TABLE food_item (
    id TEXT PRIMARY KEY NOT NULL,
    meal_log_id TEXT NOT NULL,
    name TEXT NOT NULL,
    quantity REAL NOT NULL,
    unit TEXT NOT NULL,                  -- g, ml, cup, piece
    calories REAL NOT NULL,
    protein_g REAL NOT NULL,
    carbs_g REAL NOT NULL,
    fat_g REAL NOT NULL,
    fiber_g REAL DEFAULT 0.0,
    photo_uri TEXT,
    is_ai_scanned INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY(meal_log_id) REFERENCES meal_log(id) ON DELETE CASCADE
);

-- Workout Sessions & Exercises
CREATE TABLE workout_session (
    id TEXT PRIMARY KEY NOT NULL,
    date TEXT NOT NULL,                  -- YYYY-MM-DD
    title TEXT NOT NULL,
    duration_minutes INTEGER NOT NULL,
    total_kcal_burned REAL NOT NULL,
    total_volume_kg REAL NOT NULL,
    notes TEXT,
    created_at INTEGER NOT NULL
);

CREATE TABLE exercise_log (
    id TEXT PRIMARY KEY NOT NULL,
    workout_session_id TEXT NOT NULL,
    exercise_name TEXT NOT NULL,
    muscle_group TEXT NOT NULL,          -- CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE
    equipment_type TEXT NOT NULL,        -- BARBELL, DUMBBELL, CABLE, MACHINE, BODYWEIGHT
    machine_name TEXT,                   -- e.g. "Seated Cable Row - Pin 14"
    equipment_settings TEXT,             -- e.g. "Seat Notch 4, Cable Pin 50kg"
    distance_km REAL,                    -- For cardio
    duration_seconds INTEGER,
    FOREIGN KEY(workout_session_id) REFERENCES workout_session(id) ON DELETE CASCADE
);

CREATE TABLE exercise_set (
    id TEXT PRIMARY KEY NOT NULL,
    exercise_log_id TEXT NOT NULL,
    set_number INTEGER NOT NULL,
    weight_kg REAL NOT NULL,
    reps INTEGER NOT NULL,
    rpe REAL,                            -- 6.0 - 10.0
    is_personal_record INTEGER NOT NULL DEFAULT 0,
    estimated_1rm_kg REAL NOT NULL,
    FOREIGN KEY(exercise_log_id) REFERENCES exercise_log(id) ON DELETE CASCADE
);

-- Weekly AI Structured Review
CREATE TABLE progress_review (
    id TEXT PRIMARY KEY NOT NULL,
    week_start_date TEXT NOT NULL,
    avg_weight_kg REAL NOT NULL,
    calorie_adherence_percent REAL NOT NULL,
    protein_adherence_percent REAL NOT NULL,
    total_volume_tonnage REAL NOT NULL,
    workouts_completed INTEGER NOT NULL,
    user_fatigue_score INTEGER NOT NULL, -- 1 to 5
    ai_feedback_summary TEXT NOT NULL,
    ai_calorie_adjustment INTEGER NOT NULL, -- e.g. -150 or +200
    ai_recommended_action TEXT NOT NULL
);
```

---

## 6. Multi-Theme Engine Architecture

The theming system utilizes **custom Compose Color Schemes** keyed by `ThemePalette`:

```kotlin
enum class AppThemePalette {
    POWER,   // High-voltage Neon Green / Charcoal
    FIRE,    // Flame Red / Solar Amber
    CALM,    // Sage Emerald / Frosted Teal
    PEACE,   // Soft Lavender / Deep Indigo
    WARM,    // Golden Terracotta / Honey
    MOOD,    // Minimalist Titanium / Monochromatic
    LIGHT    // Pure Crisp Daylight Athletic
}

data class FitnessColors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val accentGlow: Color,
    val proteinColor: Color,
    val carbsColor: Color,
    val fatColor: Color,
    val calorieRing: Color
)

val LocalFitnessColors = staticCompositionLocalOf<FitnessColors> { 
    error("No FitnessColors provided") 
}
```

### Color Palette Matrix

| Theme | Primary Accent | Secondary Accent | Dark Surface | Light Surface |
| :--- | :--- | :--- | :--- | :--- |
| **Power** | `#00FF87` (Neon Green) | `#FFEB3B` (Volt) | `#0E1318` | `#F4F8F5` |
| **Fire** | `#FF3D00` (Blaze Orange) | `#FFD600` (Solar) | `#1A0D0A` | `#FFF5F2` |
| **Calm** | `#10B981` (Emerald Mint)| `#06B6D4` (Teal) | `#0B1714` | `#F0FDF8` |
| **Peace** | `#8B5CF6` (Lavender) | `#6366F1` (Indigo) | `#11101E` | `#F6F5FF` |
| **Warm** | `#F59E0B` (Amber Gold) | `#FB923C` (Peach) | `#19140C` | `#FFFBF2` |
| **Mood** | `#94A3B8` (Slate Silver)| `#E2E8F0` (Ice) | `#0B0C0E` | `#F8FAFC` |
| **Light** | `#2563EB` (Athletic Blue)| `#38BDF8` (Sky) | `#1E293B` | `#FFFFFF` |

---

## 7. Dynamic Adaptive UI Layout Engine

The main dashboard is implemented as a **Composable Staggered/Bento Grid** governed by a `DashboardLayoutDispatcher`:

```kotlin
data class DashboardWidget(
    val id: String,
    val type: WidgetType,
    val priorityScore: Float,
    val isVisible: Boolean
)

enum class WidgetType {
    NUTRITION_RINGS,
    FOOD_CAMERA_QUICK_ACTION,
    WORKOUT_LOGGER_ACTIVE,
    WORKOUT_PLANNED_PREVIEW,
    HIGH_PROTEIN_SHAKE_SUGGESTION,
    MUSCLE_VOLUME_HEATMAP,
    STRENGTH_1RM_PROGRESSION,
    HYDRATION_TRACKER,
    WEEKLY_AI_COACH_INSIGHTS
}

class DashboardLayoutDispatcher {
    fun prioritizeWidgets(
        userGoal: GoalType,
        timeOfDay: LocalTime,
        isWorkoutActive: Boolean,
        frequentActionFrequency: Map<WidgetType, Int>
    ): List<DashboardWidget> {
        // Priority Scoring Calculation
        // 1. Time-of-day weighting:
        //    06:00-11:00 -> Morning Nutrition & Hydration +30
        //    16:00-20:00 -> Workout Active Logger & Pre-workout +50
        //    20:00-23:00 -> Bedtime Shake & Daily Review +40
        // 2. Goal weighting:
        //    Hypertrophy -> Muscle Heatmap & 1RM +40
        //    Weight Loss -> Calorie Deficit Ring & Food Scan +40
        // 3. User usage frequency bonus (+0 to +25)
        ...
    }
}
```

---

## 8. APK Build, Packaging & Signing Strategy

### 8.1 Android Gradle Setup
* **Min SDK:** 26 (Android 8.0 Oreo)
* **Target SDK:** 35 (Android 15)
* **Build Variants:**
  * `debug`: Enabled logging, test mock AI responses.
  * `release`: Proguard/R8 code shrinking, resource obfuscation, signed with release keystore.
* **ProGuard / R8 Optimization:**
  * Removes unused Compose runtime symbols.
  * Strips debug reflection.
  * Target APK Size: $\le 28\text{ MB}$.

### 8.2 Permissions Matrix in `AndroidManifest.xml`
```xml
<manifest ...>
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
    
    <uses-feature android:name="android.hardware.camera" android:required="false" />
</manifest>
```

---

## 9. Security, Privacy & Reliability

1. **Local Privacy:** User anthropometrics, weights, and daily logs are stored locally in encrypted Room SQLite (`SQLCipher` / Android Keystore).
2. **Offline Resilience:** If network connectivity drops:
   * Label OCR runs 100% on-device via ML Kit.
   * Workouts and meals save instantly to local Room DB.
   * Photos queue locally and sync when connection returns.
3. **Data Export & Portability:** Full JSON and CSV export option for user workout spreadsheets and nutritional history.

