package com.example.noms.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.noms.data.model.Recipe
import com.example.noms.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FavoritesUiState(
    val favoriteRecipes: List<Recipe> = emptyList(),
    val filteredFavorites: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedMealType: String = "All",
    val availableMealTypes: List<String> = listOf("All")
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(application)

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        val list = repository.getFavoriteRecipes()

        val extractedMealTypes = list
            .flatMap { it.mealType }
            .map { if (it.equals("Snacks", ignoreCase = true)) "Snack" else it }
            .distinct()
            .sorted()
        val allMealTypes = listOf("All") + extractedMealTypes

        val newState = _uiState.value.copy(
            favoriteRecipes = list,
            availableMealTypes = allMealTypes
        )

        _uiState.value = applyFilters(newState)
    }

    fun onSearchQueryChanged(query: String) {
        val newState = _uiState.value.copy(searchQuery = query)
        _uiState.value = applyFilters(newState)
    }

    fun onMealTypeSelected(mealType: String) {
        val newState = _uiState.value.copy(selectedMealType = mealType)
        _uiState.value = applyFilters(newState)
    }

    fun removeFavorite(recipeId: Int) {
        repository.removeFavorite(recipeId)
        loadFavorites()
    }

    private fun applyFilters(state: FavoritesUiState): FavoritesUiState {
        var list = state.favoriteRecipes

        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.trim()
            list = list.filter {
                it.name.contains(q, ignoreCase = true) ||
                it.cuisine.contains(q, ignoreCase = true) ||
                it.ingredients.any { ing -> ing.contains(q, ignoreCase = true) }
            }
        }

        if (!state.selectedMealType.equals("All", ignoreCase = true)) {
            val filterMeal = if (state.selectedMealType.equals("Snacks", ignoreCase = true)) "Snack" else state.selectedMealType
            list = list.filter { recipe ->
                recipe.mealType.any {
                    val recipeMeal = if (it.equals("Snacks", ignoreCase = true)) "Snack" else it
                    recipeMeal.equals(filterMeal, ignoreCase = true)
                }
            }
        }

        return state.copy(filteredFavorites = list)
    }
}
