package com.example.noms.data.repository

import android.content.Context
import com.example.noms.data.local.FavoritesStorage
import com.example.noms.data.local.RecipeCacheStorage
import com.example.noms.data.model.Recipe
import com.example.noms.data.model.RecipeResponse
import com.example.noms.data.network.ApiClient

class RecipeRepository(context: Context) {
    private val favoritesStorage = FavoritesStorage(context)
    private val recipeCacheStorage = RecipeCacheStorage(context)

    suspend fun getRecipes(limit: Int, skip: Int): FetchResult {
        val networkResult = ApiClient.getRecipes(limit, skip)
        return if (networkResult.isSuccess) {
            val response = networkResult.getOrNull()!!
            if (skip == 0) {
                recipeCacheStorage.saveCachedRecipes(response.recipes)
            }
            FetchResult.Success(response = response, isFromCache = false)
        } else {
            // check offline cache
            if (skip == 0) {
                val cached = recipeCacheStorage.getCachedRecipes()
                if (cached.isNotEmpty()) {
                    FetchResult.Success(
                        response = RecipeResponse(
                            recipes = cached,
                            total = cached.size,
                            skip = 0,
                            limit = cached.size
                        ),
                        isFromCache = true
                    )
                } else {
                    FetchResult.Error(getErrorMessage(networkResult.exceptionOrNull()))
                }
            } else {
                FetchResult.Error(getErrorMessage(networkResult.exceptionOrNull()))
            }
        }
    }

    suspend fun searchRecipes(query: String, limit: Int, skip: Int): FetchResult {
        if (query.isBlank()) {
            return getRecipes(limit, skip)
        }
        val networkResult = ApiClient.searchRecipes(query, limit, skip)
        return if (networkResult.isSuccess) {
            val response = networkResult.getOrNull()!!
            FetchResult.Success(response = response, isFromCache = false)
        } else {
            FetchResult.Error(getErrorMessage(networkResult.exceptionOrNull()))
        }
    }

    private fun getErrorMessage(throwable: Throwable?): String {
        val message = throwable?.message ?: ""
        return when {
            throwable is java.net.UnknownHostException ||
            throwable is java.net.SocketTimeoutException ||
            throwable is java.net.ConnectException ||
            message.contains("Unable to resolve host", ignoreCase = true) ||
            message.contains("Failed to connect", ignoreCase = true) ||
            message.contains("Network is unreachable", ignoreCase = true) -> {
                "No internet connection. Please check your network settings and try again."
            }
            else -> {
                message.ifBlank { "An unexpected error occurred. Please try again." }
            }
        }
    }

    fun saveCachedRecipes(recipes: List<Recipe>) = recipeCacheStorage.saveCachedRecipes(recipes)

    // bookmark data
    fun getFavoriteIds(): Set<Int> = favoritesStorage.getFavoriteIds()

    fun getFavoriteRecipes(): List<Recipe> = favoritesStorage.getFavoriteRecipes()

    fun isFavorite(recipeId: Int): Boolean = favoritesStorage.isFavorite(recipeId)

    fun toggleFavorite(recipe: Recipe): Boolean = favoritesStorage.toggleFavorite(recipe)

    fun removeFavorite(recipeId: Int) = favoritesStorage.removeFavorite(recipeId)
}

sealed class FetchResult {
    data class Success(val response: RecipeResponse, val isFromCache: Boolean) : FetchResult()
    data class Error(val message: String) : FetchResult()
}
