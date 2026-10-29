package com.example.noms.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noms.data.model.Recipe
import com.example.noms.data.repository.FetchResult
import com.example.noms.data.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val rawRecipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val searchQuery: String = "",
    val selectedDifficulty: String = "All",
    val selectedMealType: String = "All",
    val availableMealTypes: List<String> = listOf("All"),
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isFromOfflineCache: Boolean = false,
    val hasMorePages: Boolean = true,
    val skip: Int = 0,
    val limit: Int = 10,
    val total: Int = 0
)

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(application)

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadInitialRecipes()
        refreshFavorites()
    }

    fun refreshFavorites() {
        val favIds = repository.getFavoriteIds()
        _uiState.value = _uiState.value.copy(favoriteIds = favIds)
    }

    fun loadInitialRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingInitial = true,
                errorMessage = null,
                skip = 0
            )

            val query = _uiState.value.searchQuery.trim()
            val fetchResult = if (query.isEmpty()) {
                repository.getRecipes(limit = 10, skip = 0)
            } else {
                repository.searchRecipes(query = query, limit = 10, skip = 0)
            }

            handleFetchResult(fetchResult, isInitialOrRefresh = true, isRefresh = false)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val currentCount = maxOf(10, _uiState.value.rawRecipes.size)
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                errorMessage = null,
                skip = 0
            )

            val query = _uiState.value.searchQuery.trim()
            val fetchResult = if (query.isEmpty()) {
                repository.getRecipes(limit = currentCount, skip = 0)
            } else {
                repository.searchRecipes(query = query, limit = currentCount, skip = 0)
            }

            handleFetchResult(fetchResult, isInitialOrRefresh = true, isRefresh = true)
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoadingInitial || currentState.isLoadingMore || !currentState.hasMorePages) {
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoadingMore = true)

            val nextSkip = currentState.rawRecipes.size
            val query = currentState.searchQuery.trim()

            val fetchResult = if (query.isEmpty()) {
                repository.getRecipes(limit = currentState.limit, skip = nextSkip)
            } else {
                repository.searchRecipes(query = query, limit = currentState.limit, skip = nextSkip)
            }

            handleFetchResult(fetchResult, isInitialOrRefresh = false, isRefresh = false)
        }
    }

    private fun handleFetchResult(
        fetchResult: FetchResult,
        isInitialOrRefresh: Boolean,
        isRefresh: Boolean
    ) {
        val currentState = _uiState.value
        when (fetchResult) {
            is FetchResult.Success -> {
                val newRecipes = fetchResult.response.recipes
                val totalCount = fetchResult.response.total
                val skipCount = fetchResult.response.skip

                val updatedRawList = if (isInitialOrRefresh) {
                    newRecipes
                } else {
                    // check duplicate recipes
                    val existingIds = currentState.rawRecipes.map { it.id }.toSet()
                    currentState.rawRecipes + newRecipes.filterNot { existingIds.contains(it.id) }
                }

                // save recipe cache
                if (!fetchResult.isFromCache && updatedRawList.isNotEmpty()) {
                    repository.saveCachedRecipes(updatedRawList)
                }

                val hasMore = updatedRawList.size < totalCount && newRecipes.isNotEmpty()

                // get meal type from loaded recipe
                val extractedMealTypes = updatedRawList
                    .flatMap { it.mealType }
                    .map { if (it.equals("Snacks", ignoreCase = true)) "Snack" else it }
                    .distinct()
                    .filterNot { it.isBlank() }
                    .sorted()

                val allMealTypes = listOf("All") + extractedMealTypes

                val favIds = repository.getFavoriteIds()

                val newState = currentState.copy(
                    rawRecipes = updatedRawList,
                    favoriteIds = favIds,
                    availableMealTypes = allMealTypes,
                    isLoadingInitial = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    errorMessage = null,
                    isFromOfflineCache = fetchResult.isFromCache,
                    hasMorePages = hasMore,
                    skip = skipCount,
                    total = totalCount
                )

                _uiState.value = applyClientFilters(newState)
            }
            is FetchResult.Error -> {
                _uiState.value = currentState.copy(
                    isLoadingInitial = false,
                    isLoadingMore = false,
                    isRefreshing = false,
                    errorMessage = fetchResult.message
                )
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // Debounce network search requests
            loadInitialRecipes()
        }
    }

    fun onDifficultySelected(difficulty: String) {
        val newState = _uiState.value.copy(selectedDifficulty = difficulty)
        _uiState.value = applyClientFilters(newState)
    }

    fun onMealTypeSelected(mealType: String) {
        val newState = _uiState.value.copy(selectedMealType = mealType)
        _uiState.value = applyClientFilters(newState)
    }

    fun toggleFavorite(recipe: Recipe) {
        repository.toggleFavorite(recipe)
        refreshFavorites()
    }

    fun clearFilters() {
        val newState = _uiState.value.copy(
            selectedDifficulty = "All",
            selectedMealType = "All"
        )
        _uiState.value = applyClientFilters(newState)
    }

    private fun applyClientFilters(state: RecipeListUiState): RecipeListUiState {
        var list = state.rawRecipes

        // difficulty filter
        if (!state.selectedDifficulty.equals("All", ignoreCase = true)) {
            list = list.filter { it.difficulty.equals(state.selectedDifficulty, ignoreCase = true) }
        }

        // mealbtype filter
        if (!state.selectedMealType.equals("All", ignoreCase = true)) {
            val filterMeal = if (state.selectedMealType.equals("Snacks", ignoreCase = true)) "Snack" else state.selectedMealType
            list = list.filter { recipe ->
                recipe.mealType.any {
                    val recipeMeal = if (it.equals("Snacks", ignoreCase = true)) "Snack" else it
                    recipeMeal.equals(filterMeal, ignoreCase = true)
                }
            }
        }

        return state.copy(filteredRecipes = list)
    }
}
