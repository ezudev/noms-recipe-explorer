package com.example.noms.data.model

import org.json.JSONArray
import org.json.JSONObject

data class Recipe(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val tags: List<String>,
    val userId: Int,
    val image: String,
    val rating: Double,
    val reviewCount: Int,
    val mealType: List<String>
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            put("ingredients", JSONArray(ingredients))
            put("instructions", JSONArray(instructions))
            put("prepTimeMinutes", prepTimeMinutes)
            put("cookTimeMinutes", cookTimeMinutes)
            put("servings", servings)
            put("difficulty", difficulty)
            put("cuisine", cuisine)
            put("caloriesPerServing", caloriesPerServing)
            put("tags", JSONArray(tags))
            put("userId", userId)
            put("image", image)
            put("rating", rating)
            put("reviewCount", reviewCount)
            put("mealType", JSONArray(mealType))
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): Recipe {
            val ingredientsList = mutableListOf<String>()
            val ingredientsArray = json.optJSONArray("ingredients")
            if (ingredientsArray != null) {
                for (i in 0 until ingredientsArray.length()) {
                    ingredientsList.add(ingredientsArray.optString(i, ""))
                }
            }

            val instructionsList = mutableListOf<String>()
            val instructionsArray = json.optJSONArray("instructions")
            if (instructionsArray != null) {
                for (i in 0 until instructionsArray.length()) {
                    instructionsList.add(instructionsArray.optString(i, ""))
                }
            }

            val tagsList = mutableListOf<String>()
            val tagsArray = json.optJSONArray("tags")
            if (tagsArray != null) {
                for (i in 0 until tagsArray.length()) {
                    tagsList.add(tagsArray.optString(i, ""))
                }
            }

            val mealTypeList = mutableListOf<String>()
            val mealTypeArray = json.optJSONArray("mealType")
            if (mealTypeArray != null) {
                for (i in 0 until mealTypeArray.length()) {
                    val rawType = mealTypeArray.optString(i, "")
                    val normalizedType = if (rawType.equals("Snacks", ignoreCase = true)) "Snack" else rawType
                    if (normalizedType.isNotBlank() && !mealTypeList.contains(normalizedType)) {
                        mealTypeList.add(normalizedType)
                    }
                }
            }

            return Recipe(
                id = json.optInt("id", 0),
                name = json.optString("name", "Unknown Recipe"),
                ingredients = ingredientsList,
                instructions = instructionsList,
                prepTimeMinutes = json.optInt("prepTimeMinutes", 0),
                cookTimeMinutes = json.optInt("cookTimeMinutes", 0),
                servings = json.optInt("servings", 1),
                difficulty = json.optString("difficulty", "Easy"),
                cuisine = json.optString("cuisine", "General"),
                caloriesPerServing = json.optInt("caloriesPerServing", 0),
                tags = tagsList,
                userId = json.optInt("userId", 0),
                image = json.optString("image", ""),
                rating = json.optDouble("rating", 0.0),
                reviewCount = json.optInt("reviewCount", 0),
                mealType = mealTypeList
            )
        }
    }
}

data class RecipeResponse(
    val recipes: List<Recipe>,
    val total: Int,
    val skip: Int,
    val limit: Int
) {
    fun toJsonObject(): JSONObject {
        val recipesArray = JSONArray()
        recipes.forEach { recipesArray.put(it.toJsonObject()) }
        return JSONObject().apply {
            put("recipes", recipesArray)
            put("total", total)
            put("skip", skip)
            put("limit", limit)
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject): RecipeResponse {
            val recipesList = mutableListOf<Recipe>()
            val recipesArray = json.optJSONArray("recipes")
            if (recipesArray != null) {
                for (i in 0 until recipesArray.length()) {
                    recipesArray.optJSONObject(i)?.let { recipeJson ->
                        recipesList.add(Recipe.fromJsonObject(recipeJson))
                    }
                }
            }

            return RecipeResponse(
                recipes = recipesList,
                total = json.optInt("total", recipesList.size),
                skip = json.optInt("skip", 0),
                limit = json.optInt("limit", 10)
            )
        }
    }
}
