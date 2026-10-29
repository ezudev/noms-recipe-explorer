package com.example.noms.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.noms.data.model.Recipe
import org.json.JSONArray

class RecipeCacheStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("noms_recipe_cache_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CACHED_RECIPES_JSON = "key_cached_recipes_json"
        private const val KEY_LAST_UPDATE_TIME = "key_last_update_time"
    }

    fun saveCachedRecipes(recipes: List<Recipe>) {
        val jsonArray = JSONArray()
        recipes.forEach { jsonArray.put(it.toJsonObject()) }

        prefs.edit()
            .putString(KEY_CACHED_RECIPES_JSON, jsonArray.toString())
            .putLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getCachedRecipes(): List<Recipe> {
        val jsonString = prefs.getString(KEY_CACHED_RECIPES_JSON, "[]") ?: "[]"
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

    fun getLastUpdateTime(): Long {
        return prefs.getLong(KEY_LAST_UPDATE_TIME, 0L)
    }
}
