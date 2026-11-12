# Product Requirements Document (PRD)
## Project Name: PulseForge AI (Comprehensive Fitness, Nutrition & Muscle Ecosystem)
**Version:** 1.0.0  
**Target Platform:** Android (Native APK via Kotlin Jetpack Compose / React Native Native Build)  
**Date:** November 2026  
**Status:** Approved for Implementation

---

## 1. Executive Summary & Vision
**PulseForge AI** is an all-in-one, intelligent mobile fitness and nutrition companion engineered to replace multiple fragmented applications (calorie counters, workout loggers, recipe planners, and personal training coaches). 

By uniting **computer vision-driven food and label scanning**, **muscle-targeted strength logging**, **mathematically grounded physiological goal feasibility modeling**, **adaptive UI layouts**, and an **expressive multi-theme design system**, PulseForge empowers users to achieve sustainable body transformations with zero guesswork.

---

## 2. Core Value Propositions
1. **Frictionless Nutrition Intake:** Log full meals in seconds via dual-engine AI Vision (plate recognition + FDA/FSSAI nutrition label OCR) with one-tap portion verification across 4 meals and snacks.
2. **Physiologically Rigorous Goal Planning:** Realistic, science-backed feasibility checks prevent dangerous or impossible weight/muscle expectations, instantly calculating the maximum healthy rate achievable in the user's chosen timeframe.
3. **Hypertrophy & Equipment-Centric Training:** Muscle-specific tracking across free weights, cables, and gym machines, visualizing volume load, estimated 1RM, and caloric burn.
4. **Algorithmic AI Progress Reviews:** Automated weekly check-ins that evaluate adherence, detect plateaus, and recalibrate calorie, macro, and volume prescriptions based on user feedback and recovery.
5. **Adaptive Contextual Interface & Theming:** A UI layout that dynamically reorganizes based on the user's immediate goal and daily usage patterns, packaged in 7 curated atmospheric themes (Power, Fire, Calm, Peace, Warm, Mood, Light/Dark).

---

## 3. User Personas & Target Segments

| Persona | Core Motivation | Pain Points | Primary Features Used |
| :--- | :--- | :--- | :--- |
| **Alex (Hypertrophy Builder)** | Focused on growing specific muscle groups (Upper Chest & Lats) for physique aesthetics. | Tracking machine pin weights, plate calculations, and meeting 160g+ daily protein. | Muscle heatmaps, 1RM tracker, gym machine logger, protein shake recommendations. |
| **Priya (Weight Loss & Mobility)** | Sustained 8kg weight loss, incorporating regional home meals and Yoga/Calisthenics. | Hard to track complex local recipes and ingredient labels; easily demotivated by rigid plans. | Food camera scanner, locality-aware meal suggestions, calorie burn graph, Yoga timer. |
| **Marcus (Busy Professional)** | Maintaining general fitness and conditioning with 45-min gym windows. | Manually entering every single food item takes too long; app layouts feel cluttered. | Label OCR scan, adaptive dynamic dashboard (morning breakfast -> evening gym), weekly review sheet. |

---

## 4. User Onboarding & Anthropometric Profiling

### 4.1 Demographic Data Collection
* **Basic Metrics:** Age (years), Biological Sex (for BMR/hormonal basal calculations), Height (cm/ft-in), Body Weight (kg/lbs), Target Body Fat % (optional).
* **Locality & Cultural Diet Context:**
  * Region selection (e.g., South Asian / Indian, Mediterranean, East Asian, Western, Latin America, Middle Eastern).
  * Dietary Classifications: Omnivore, Vegetarian, Vegan, Pescatarian, Halal, Kosher, Keto, Lactose-Free, Gluten-Free.
  * Local Pantry Context: Seeds, regional lentils, cottage cheese (paneer), tempeh, tofu, local grains (millet, quinoa, oats), and regional protein staples.
* **Activity Baseline:** Sedentary (1.2), Lightly Active (1.375), Moderately Active (1.55), Very Active (1.725), Extremely Active (1.9).

---

## 5. Goal Setting & Physiological Feasibility Engine

### 5.1 Goal Categories
* **Muscle Hypertrophy:** Target specific muscle groups (Chest, Lats/Back, Shoulders/Delts, Biceps, Triceps, Quads, Hamstrings, Glutes, Calves, Abs/Core).
* **Weight Gain / Bulking:** Lean bulk (+200-300 kcal) or Aggressive bulk (+500 kcal).
* **Weight Loss / Cutting:** Moderate deficit (-300-500 kcal) or Aggressive cut (-750 kcal).
* **Body Recomposition:** Simultaneous fat loss and muscle gain for beginners/detrained individuals.
* **Functional Fitness, Yoga & Flexibility:** Cardio endurance, Vinyasa flows, core stability.

### 5.2 Algorithmic Feasibility Engine
When the user specifies:
* Current Weight: $W_0$
* Target Weight: $W_t$
* Target Timeline: $T$ (weeks)
* Goal Type: Muscle Gain vs Fat Loss

#### Science-Based Guardrails (Alan Aragon / Helms Models):
1. **Maximum Natural Muscle Gain Rates:**
   * Beginner: $1.0\% - 1.5\%$ of body weight per month.
   * Intermediate: $0.5\% - 1.0\%$ of body weight per month.
   * Advanced: $0.25\% - 0.5\%$ of body weight per month.
2. **Safe Fat Loss Rates:**
   * Maximum healthy sustainable loss: $0.5\% - 1.0\%$ of total body weight per week ($0.5 - 1.0\text{ kg/week}$ for an $80\text{ kg}$ individual).
   * Calorie deficit capped to avoid metabolic downregulation and muscle catabolism ($>20\%$ below TDEE requires cautionary warning).

#### Feasibility Evaluation Pipeline:
```
User inputs: Delta = W_t - W_0 over T weeks
   │
   ├── Compute required weekly rate = |Delta| / T
   │
   ├── IF required rate > Max_Healthy_Rate:
   │     ├─ Status: UNREALISTIC / UNSAFE
   │     ├─ Trigger UI Dialogue: "This rate may cause muscle loss or metabolic fatigue."
   │     ├─ Compute Max Realistic Delta in T weeks: Max_Delta = Max_Healthy_Rate * T
   │     ├─ Compute Recommended Safe Timeline: T_rec = |Delta| / Max_Healthy_Rate
   │     └─ Prompt User: "Adopt recommended timeline (T_rec weeks) OR adjust target to (Max_Delta kg)?"
   │
   └── ELSE:
         ├─ Status: REALISTIC & OPTIMAL
         └─ Generate customized caloric surplus/deficit schedule.
```

---

## 6. Smart Nutrition & Calorie Tracking

### 6.1 Meal Architecture
Tracks **4 Structured Daily Meals + Snacks + Hydration**:
1. **Breakfast** (Target: 20-25% daily kcal, 25-35g protein)
2. **Lunch** (Target: 30-35% daily kcal, 30-45g protein)
3. **Dinner** (Target: 25-30% daily kcal, 30-40g protein)
4. **Pre/Post Workout Meal or Evening Snack** (Target: 10-15% daily kcal)
5. **Snacks & Liquid Hydration** (Water intake tracker in ml with hourly reminders)

### 6.2 Dual-Engine Input Modalities
1. **Manual Search & Macro Entry:**
   * Database of 100,000+ regional and global items.
   * Custom food creation (Name, Calories, Protein, Carbs, Fat, Fiber, Serving Unit).
2. **AI Vision Camera Meal Scanner:**
   * User snaps a photo of their plate.
   * Multimodal vision model segments food items (e.g., "Grilled Chicken Breast 150g, White Rice 1 cup, Steamed Broccoli 80g").
   * Interactive Verification Card: Presents items, quantities, and macronutrient totals. User can adjust slider weights or approve with a single tap.
3. **Packaged Product Nutrition Label OCR:**
   * User snaps a photo of the "Nutrition Facts" panel on any snack, bar, or drink.
   * Computer vision parses Serving Size, Calories, Total Fat, Saturated Fat, Carbohydrates, Dietary Fiber, Sugars, and Protein.
   * One-tap addition directly into the active meal slot.

---

## 7. Intelligent Diet & High-Protein Shake Recommendation Engine

### 7.1 Dynamic Meal & Shake Recommendations
* Dynamically evaluates remaining daily macros:
  $$\text{Deficit}_{\text{protein}} = \text{Goal}_{\text{protein}} - \text{Consumed}_{\text{protein}}$$
  $$\text{Deficit}_{\text{kcal}} = \text{Goal}_{\text{kcal}} - \text{Consumed}_{\text{kcal}}$$
* If at 4:00 PM, user has a $40\text{g}$ protein deficit and $350\text{ kcal}$ remaining:
  * Generates instant Shake Suggestions tailored to their regional pantry (e.g., *Peanut Butter Whey Oat Shake* or *Almond Sattu Cinnamon Shake*).
* Categorized Shake Library:
  * **Anabolic Mass Gainer Shake:** Oats, Peanut Butter, Banana, Whey/Plant Protein, Whole Milk (650 kcal, 48g Protein).
  * **Lean Cutting High-Protein Shake:** Almond Milk, Whey Isolate, Frozen Berries, Chia Seeds (220 kcal, 32g Protein).
  * **Post-Workout Glycogen Replenisher:** Coconut Water, Honey, Whey/Vegan Protein, Banana (310 kcal, 28g Protein).
  * **Bedtime Recovery Shake:** Micellar Casein or Greek Yogurt, Flaxseeds, Cinnamon (200 kcal, 30g Protein).

### 7.2 Schedule Horizons
* **Daily Suggestions:** On-the-fly macro fillers.
* **Weekly Meal Prep Schedules:** Structured 7-day grocery lists and prep batching based on regional recipes.
* **Monthly Nutritional Progression:** Cycling carb intake on workout days vs rest days (Carb Cycling Engine).

---

## 8. Advanced Workout & Gym Equipment Logger

### 8.1 Exercise & Modality Categories
* **Strength & Hypertrophy:** Barbell, Dumbbell, Cable pulley, Weight machines, Bodyweight calisthenics.
* **Gym Equipment Specifics:**
  * Free Weights: Barbell, EZ-Bar, Dumbbells, Kettlebells.
  * Cables: Single cable, Dual adjustable pulley, Tricep pushdown, Lat pulldown, Seated cable row.
  * Pin & Plate Loaded Machines: Smith Machine, Leg Press ($45^\circ$), Hack Squat, Leg Extension, Lying Leg Curl, Pec Deck / Reverse Fly, Chest Press, Preacher Curl.
  * Custom Equipment Settings: Pin weight, Plate count, Seat height notch, Pulley height level.
* **Cardio & Functional:** Treadmill, Stationary Bike, Rowing Machine, Stair Climber, Outdoor Running/Walking (Distance, Pace, Incline, Duration).
* **Yoga & Flexibility:** Vinyasa, Hatha, Yin Yoga, Active Mobility routines with integrated pose timer.

### 8.2 Logging Data Model
For every set:
* Set # (Warmup, Normal, Drop Set, Rest-Pause, Failure)
* Weight Lifted ($\text{kg}$ or $\text{lbs}$)
* Repetitions Completed
* RPE (Rate of Perceived Exertion: 6–10)
* Rest Timer (Auto-starts countdown: 60s, 90s, 120s, 180s)
* Target Muscle Focus tag (e.g., Clavicular Pectoralis vs Sternal Pectoralis)

### 8.3 Caloric Burn Calculation
$$E_{\text{burned}} = \text{MET} \times \text{Weight (kg)} \times \text{Duration (hours)}$$
* Dynamically adjusted for resistance training based on total volume load ($V = \sum \text{Sets} \times \text{Reps} \times \text{Weight}$) and EPOC (Excess Post-Exercise Oxygen Consumption).

---

## 9. Interactive Visualizations & Analytics

### 9.1 Graphing Horizons
* **Daily:** Hourly calorie burn vs consumption, active workout duration, macros ring progress.
* **Weekly:** Total volume load per muscle group, daily caloric surplus/deficit delta, workout adherence rate (e.g., 4/5 planned sessions).
* **Monthly:** Estimated 1RM strength curves across major lifts (Bench Press, Squat, Deadlift, Overhead Press, Lat Pulldown), body weight trendline (7-day exponential moving average).
* **All-Time / Maximums:** Personal records (PRs), peak volume sessions, total tonnage lifted, lifetime calorie burn.

### 9.2 Specialized Visual Analytics
* **3D/2D Muscle Heatmap:** Visual body diagram where muscle groups change color (Gray $\rightarrow$ Amber $\rightarrow$ Emerald) based on weekly set volume (sub-optimal $<10$ sets, optimal $10-20$ sets, overreaching $>22$ sets).
* **Volume Load Progression Graphs:** Line charts tracking progressive overload over 12 weeks.
* **Estimated 1RM Calculator (Brzycki Formula):**
  $$\text{1RM} = \frac{\text{Weight}}{1.0278 - (0.0278 \times \text{Reps})}$$

---

## 10. Structured Progress Sheet & Algorithmic AI Coach

### 10.1 Structured Weekly Review Sheet
Every 7 days, PulseForge compiles a structured check-in summary:
* **Metric Trajectory:** Starting weight vs Current weight vs Target pace.
* **Nutritional Adherence:** % of days within $\pm 100\text{ kcal}$ of goal, average daily protein hit rate.
* **Workout Volume & Frequency:** Completed sessions vs target, total volume tonnage, PRs broken.
* **User Subjective Feedback:** Energy levels (1–5), Sleep quality (1–5), Soreness/Fatigue score (1–5).

### 10.2 Adaptive AI Algorithm
* **Scenario A (Weight loss stalled for 2 weeks, adherence $>90\%$):** 
  * AI reduces daily calories by $125\text{ kcal}$ or increases daily step goal by 1,500 steps.
* **Scenario B (Weight loss faster than safety limit $>1.2\%/\text{week}$):** 
  * AI warns of muscle catabolism; increases daily calories by $+150\text{ kcal}$ (mostly carbs/protein).
* **Scenario C (High fatigue & joint soreness):** 
  * AI automatically schedules a **Deload Week** (volume cut by $40\%$, intensity maintained).

---

## 11. Multi-Theme Design System & Atmospheric Aesthetics

PulseForge supports **Dynamic Dark and Light modes** across **7 distinctive visual themes**:

| Theme Key | Name | Color Palette & Vibe | Target Psychological State |
| :--- | :--- | :--- | :--- |
| `THEME_POWER` | **Power** | Cyber Neon Green (`#00FF87`), Deep Charcoal (`#0B0E14`), Hyper Yellow accents | High-intensity strength sessions, PR attempts, intense lifting. |
| `THEME_FIRE` | **Fire** | Flame Orange (`#FF5722`), Crimson Blaze (`#FF1744`), Molten Gold | HIIT, fast-paced cardio, aggressive motivation. |
| `THEME_CALM` | **Calm** | Sage Mint (`#2ECC71`), Frosted Teal (`#00B4D8`), Deep Spruce (`#122322`) | Recovery days, balanced lifestyle, mindful eating. |
| `THEME_PEACE` | **Peace** | Serene Lavender (`#A594F9`), Soft Slate (`#6C5CE7`), Twilight Navy | Yoga sessions, evening reflection, restorative sleep. |
| `THEME_WARM` | **Warm** | Terracotta Amber (`#FFB300`), Sunset Peach (`#FF8A65`), Sand Dune | Gentle, friendly, holistic wellness tracking. |
| `THEME_MOOD` | **Mood** | Monochromatic Titanium (`#718096`), Pure Obsidian (`#050505`), Ice Silver | Minimalist, distraction-free aesthetic for pure focus. |
| `THEME_LIGHT` | **Light / Studio** | Crisp Athletic White (`#FFFFFF`), Cobalt Blue (`#2563EB`), Slate Gray | Clean, bright daylight usage and clinical clarity. |

---

## 12. Dynamic Adaptive UI Layout Engine

The layout is not static; it dynamically alters its widget hierarchy based on **User Archetype**, **Time of Day**, and **Activity Recency**:

```
Morning (06:00 - 11:00)
├── Hydration Ring & Morning Weight Log
├── Breakfast Nutrition & Meal Scan Widget
└── Today's Planned Workout Preview

Workout Window (Active or Imminent Session)
├── Quick-Launch Active Gym Logger (Machine & Set Tracker)
├── Rest Timer & Music Controller Overlay
└── Pre/Post-Workout Shake Formulation

Evening (18:00 - 23:00)
├── Remaining Daily Macros & Dinner Log
├── Recovery Score & Sleep Prep Checklist
└── Weekly Progress Sheet Quick View
```

* **Goal-Driven Reordering:**
  * *Hypertrophy Goal:* Muscle heatmap, 1RM graph, and Workout Log pinned to Top.
  * *Weight Loss Goal:* Calorie Deficit Bar, Food Scanner, and Step Counter pinned to Top.
  * *Yoga / Mobility:* Flexibility streak, Guided sessions, and Hydration pinned to Top.

---

## 13. Non-Functional Requirements
* **Performance:** Cold start $<1.2\text{s}$, camera scan processing time $<2.5\text{s}$.
* **Offline First:** Complete logging capability for workouts and meals without active cellular/Wi-Fi connection; background sync upon reconnection.
* **Privacy & Security:** Biometric lock option, local encryption for user health telemetry, GDPR/HIPAA compliant storage practices.
* **Storage Footprint:** Base APK package size $<35\text{ MB}$.

