package com.pulseforge.app.domain.calculator

import com.pulseforge.app.domain.model.Locality
import com.pulseforge.app.domain.model.ShakeRecipe

object ShakeRecommendationEngine {

    private val masterRecipes = listOf(
        // Anabolic Muscle Building
        ShakeRecipe(
            name = "Anabolic Peanut Butter Whey Fusion",
            category = "Hypertrophy Mass",
            purpose = "Post-workout rapid glycogen and amino acid replenishment",
            calories = 620,
            proteinG = 48,
            carbsG = 65,
            fatG = 18,
            ingredients = listOf("2 Scoops Whey Protein", "2 tbsp Peanut Butter", "1 Large Banana", "1/2 Cup Rolled Oats", "350ml Whole Milk"),
            instructions = "Blend oats first into flour, then add milk, whey, banana and peanut butter. Blend on high for 45s.",
            regionalTag = Locality.WESTERN
        ),
        ShakeRecipe(
            name = "Desi Power Sattu & Almond Elixir",
            category = "High-Protein Traditional",
            purpose = "Sustained muscle recovery using regional roasted gram flour & nuts",
            calories = 490,
            proteinG = 38,
            carbsG = 52,
            fatG = 14,
            ingredients = listOf("60g Chana Sattu (Roasted Gram Flour)", "1 Scoop Whey or Paneer Cubes (50g)", "15 Soaked Crushed Almonds", "1 tsp Cardamom & Honey", "350ml Skimmed Milk or Water"),
            instructions = "Whisk sattu thoroughly in warm milk/water with honey, cardamom and crushed almonds.",
            regionalTag = Locality.SOUTH_ASIAN
        ),
        // Lean Cutting / Fat Loss
        ShakeRecipe(
            name = "Lean Berry Isolate Deficit Shield",
            category = "Lean Cutting",
            purpose = "Maximum protein satiety with minimal carbs and fat",
            calories = 230,
            proteinG = 34,
            carbsG = 14,
            fatG = 3,
            ingredients = listOf("1.5 Scoops Whey Isolate", "1 Cup Frozen Blueberries", "1 tbsp Chia Seeds", "300ml Unsweetened Almond Milk"),
            instructions = "Blend frozen berries with almond milk and chia seeds. Pulse in whey isolate until smooth.",
            regionalTag = Locality.WESTERN
        ),
        ShakeRecipe(
            name = "Mediterranean Fig & Greek Yogurt Cream",
            category = "Whole Food Recovery",
            purpose = "Natural bio-active peptides, probiotics, and calcium for bone-tendon strength",
            calories = 380,
            proteinG = 35,
            carbsG = 40,
            fatG = 8,
            ingredients = listOf("200g Authentic Greek Yogurt (0% or 2%)", "2 Fresh Figs or Dates", "1 tbsp Crushed Walnuts", "1 Scoop Vanilla Protein", "Splash of Cold Water"),
            instructions = "Puree figs with cold water and vanilla protein, fold into Greek yogurt and top with walnuts.",
            regionalTag = Locality.MEDITERRANEAN
        ),
        // Bedtime / Slow Digestion
        ShakeRecipe(
            name = "Twilight Casein & Cinnamon Recovery",
            category = "Night Time",
            purpose = "Anti-catabolic slow-digesting amino acids overnight",
            calories = 280,
            proteinG = 36,
            carbsG = 12,
            fatG = 6,
            ingredients = listOf("1.5 Scoops Micellar Casein or 150g Low-Fat Cottage Cheese", "1 tbsp Ground Flaxseed", "1/2 tsp Ceylon Cinnamon", "300ml Warm/Cold Milk"),
            instructions = "Shake or blend casein with flaxseeds and cinnamon 30 minutes before sleep.",
            regionalTag = null
        ),
        // Plant-Based / East Asian
        ShakeRecipe(
            name = "Matcha Soy & Edamame Green Dynamo",
            category = "Plant Hypertrophy",
            purpose = "Antioxidant rich green tea with complete plant amino acids",
            calories = 340,
            proteinG = 32,
            carbsG = 28,
            fatG = 9,
            ingredients = listOf("1 Scoop Soy/Pea Isolate", "1 tsp Ceremonial Matcha Powder", "100g Silken Tofu", "300ml Fortified Soy Milk", "1 tsp Agave Nectar"),
            instructions = "Whisk matcha in warm water, blend with silken tofu and soy protein.",
            regionalTag = Locality.EAST_ASIAN
        )
    )

    fun getRecommendations(
        remainingProteinG: Double,
        remainingCalories: Double,
        locality: Locality? = null
    ): List<ShakeRecipe> {
        // Filter or rank by locality preference, then match macro requirements
        return masterRecipes.sortedWith(
            compareByDescending<ShakeRecipe> { it.regionalTag == locality }
                .thenBy { kotlin.math.abs(it.proteinG - remainingProteinG) }
        )
    }

    fun getAllRecipes(): List<ShakeRecipe> = masterRecipes
}

