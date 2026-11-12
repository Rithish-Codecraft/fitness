package com.pulseforge.app.domain.model

enum class Gender(val label: String) {
    MALE("Male"),
    FEMALE("Female")
}

enum class GoalType(val label: String, val description: String) {
    MUSCLE_GAIN("Muscle Hypertrophy", "Focus on building lean muscle mass and targeting weak points"),
    WEIGHT_GAIN("Weight Gain / Bulk", "Caloric surplus for total mass and strength acquisition"),
    WEIGHT_LOSS("Fat Loss / Cut", "Caloric deficit designed to shed fat while preserving muscle"),
    BODY_RECOMP("Body Recomposition", "Simultaneously burn fat and build muscle tone"),
    YOGA("Yoga & Mobility", "Flexibility, core stabilization, and joint recovery"),
    GENERAL_FITNESS("General Fitness & Conditioning", "Cardiovascular health, stamina, and functional strength")
}

enum class MuscleGroup(val label: String, val category: String) {
    CHEST("Chest / Pectorals", "Upper Body"),
    BACK_LATS("Lats & Upper Back", "Upper Body"),
    SHOULDERS("Shoulders / Deltoids", "Upper Body"),
    BICEPS("Biceps", "Arms"),
    TRICEPS("Triceps", "Arms"),
    QUADS("Quadriceps", "Lower Body"),
    HAMSTRINGS("Hamstrings", "Lower Body"),
    GLUTES("Glutes", "Lower Body"),
    CALVES("Calves", "Lower Body"),
    CORE_ABS("Abs & Core", "Core")
}

enum class Locality(val label: String, val dietaryContext: String) {
    SOUTH_ASIAN("South Asian / Indian", "Lentils (dal), paneer, sattu, millets, local legumes & spices"),
    MEDITERRANEAN("Mediterranean", "Olive oil, fish, legumes, Greek yogurt, feta, fresh greens"),
    EAST_ASIAN("East Asian", "Tofu, edamame, rice, lean meats, soy, broth-based meals"),
    WESTERN("Western", "Chicken breast, oats, whey protein, eggs, potatoes, peanut butter"),
    LATIN_AMERICAN("Latin American", "Black beans, plantains, maize, lean beef, avocado, queso fresco"),
    MIDDLE_EASTERN("Middle Eastern", "Chickpeas, hummus, tahini, lamb, grilled chicken, labneh")
}

enum class DietPreference(val label: String) {
    VEGETARIAN("Vegetarian (Lacto-Ovo)"),
    NON_VEGETARIAN("Non-Vegetarian"),
    VEGAN("100% Plant-Based (Vegan)"),
    HALAL("Halal Certified"),
    KETO("Low-Carb / Ketogenic")
}

enum class ActivityLevel(val label: String, val multiplier: Double) {
    SEDENTARY("Sedentary (Desk job, little exercise)", 1.20),
    LIGHT("Lightly Active (1-3 workout days/week)", 1.375),
    MODERATE("Moderately Active (3-5 workout days/week)", 1.55),
    VERY_ACTIVE("Very Active (6-7 intense days/week)", 1.725),
    EXTRA_ACTIVE("Extremely Active (Athletic training/physical labor)", 1.90)
}

enum class ExperienceLevel(val label: String) {
    BEGINNER("Beginner (< 1 year consistent training)"),
    INTERMEDIATE("Intermediate (1 - 3 years training)"),
    ADVANCED("Advanced (3+ years serious lifting)")
}

enum class ThemePalette(val label: String, val subtitle: String) {
    POWER("Pulse Minimal", "Coral Terracotta & Soft Alabaster (Signature UI)"),
    FIRE("MacroFactor Amber", "Obsidian & Warm Coral (MacroFactor-Style)"),
    CALM("Oura Emerald", "Nordic Spruce & Champagne (Oura-Style)"),
    PEACE("Hevy Cobalt", "Electric Cobalt & Gym Slate (Hevy-Style)"),
    WARM("Apple Activity", "Activity Crimson & Solar Glow (Apple-Style)"),
    MOOD("Gymshark Onyx", "Titanium Carbon & Monochrome Minimal"),
    LIGHT("Studio Porcelain", "Crisp Clean Athletic Day (MacroFactor Light)")
}

enum class MealType(val label: String, val defaultCaloriePercent: Double) {
    BREAKFAST("Breakfast", 0.25),
    LUNCH("Lunch", 0.35),
    DINNER("Dinner", 0.25),
    SNACK("Snacks & Pre/Post Shake", 0.15)
}

enum class EquipmentType(val label: String) {
    BARBELL("Barbell"),
    DUMBBELL("Dumbbells"),
    CABLE("Cable Pulley Machine"),
    PIN_LOADED_MACHINE("Pin-Selected Machine"),
    PLATE_LOADED_MACHINE("Plate-Loaded / Smith Machine"),
    BODYWEIGHT("Bodyweight / Calisthenics"),
    CARDIO_MACHINE("Treadmill / Bike / Rower"),
    YOGA_MAT("Yoga Mat / Mobility")
}

