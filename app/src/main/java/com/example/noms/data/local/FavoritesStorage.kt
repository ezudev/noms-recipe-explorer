package com.example.noms.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.noms.data.model.Recipe
import org.json.JSONArray

class FavoritesStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("noms_favorites_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITE_IDS = "key_favorite_ids"
        private const val KEY_FAVORITE_RECIPES_JSON = "key_favorite_recipes_json"
    }

    fun getFavoriteIds(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_FAVORITE_IDS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun getFavoriteRecipes(): List<Recipe> {
        val jsonString = prefs.getString(KEY_FAVORITE_RECIPES_JSON, "[]") ?: "[]"
        val recipeList = mutableListOf<Recipe>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                jsonArray.optJSONObject(i)?.let {
                    recipeList.add(Recipe.fromJsonObject(it))
                }
            }
        } catch (_: Exception) {}
        return recipeList
    }

    fun isFavorite(recipeId: Int): Boolean {
        return getFavoriteIds().contains(recipeId)
    }

    fun addFavorite(recipe: Recipe) {
        val currentIds = getFavoriteIds().toMutableSet()
        currentIds.add(recipe.id)

        val currentRecipes = getFavoriteRecipes().toMutableList()
        if (currentRecipes.none { it.id == recipe.id }) {
            currentRecipes.add(0, recipe)
        }

        saveToPrefs(currentIds, currentRecipes)
    }

    fun removeFavorite(recipeId: Int) {
        val currentIds = getFavoriteIds().toMutableSet()
        currentIds.remove(recipeId)

        val currentRecipes = getFavoriteRecipes().filter { it.id != recipeId }

        saveToPrefs(currentIds, currentRecipes)
    }

    fun toggleFavorite(recipe: Recipe): Boolean {
        return if (isFavorite(recipe.id)) {
            removeFavorite(recipe.id)
            false
        } else {
            addFavorite(recipe)
            true
        }
    }

    private fun saveToPrefs(ids: Set<Int>, recipes: List<Recipe>) {
        val jsonArray = JSONArray()
        recipes.forEach { jsonArray.put(it.toJsonObject()) }

        prefs.edit()
            .putStringSet(KEY_FAVORITE_IDS, ids.map { it.toString() }.toSet())
            .putString(KEY_FAVORITE_RECIPES_JSON, jsonArray.toString())
            .apply()
    }
}
