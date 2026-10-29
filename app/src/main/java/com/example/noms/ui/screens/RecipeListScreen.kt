package com.example.noms.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tapas
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noms.data.model.Recipe
import com.example.noms.ui.components.ErrorView
import com.example.noms.ui.components.FilterBar
import com.example.noms.ui.components.GifLoadingView
import com.example.noms.ui.components.OfflineCacheBanner
import com.example.noms.ui.components.RecipeCard
import com.example.noms.ui.components.TopBarComponent
import com.example.noms.ui.viewmodel.RecipeListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel,
    onRecipeClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showFilters by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshFavorites()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= state.filteredRecipes.size - 2) {
                    viewModel.loadNextPage()
                }
            }
    }

    // scroll to top after filter changes and search
    LaunchedEffect(state.selectedDifficulty, state.selectedMealType, state.searchQuery) {
        if (state.filteredRecipes.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 25 && listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 && !state.isRefreshing) {
                            viewModel.refresh()
                        }
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))


                TopBarComponent()

                Spacer(modifier = Modifier.height(18.dp))

                // headline
                Text(
                    text = buildAnnotatedString {
                        append("Create Your ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Cravings")
                        }
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 36.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // top
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = {
                            Text(
                                text = "Search any recipe",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // filter button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (showFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(52.dp)
                    ) {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = "Toggle Filters",
                                tint = if (showFilters) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // filter view
                AnimatedVisibility(visible = showFilters) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        FilterBar(
                            selectedDifficulty = state.selectedDifficulty,
                            onDifficultySelected = { viewModel.onDifficultySelected(it) },
                            selectedMealType = state.selectedMealType,
                            onMealTypeSelected = { viewModel.onMealTypeSelected(it) },
                            availableMealTypes = state.availableMealTypes,
                            onClearFilters = { viewModel.clearFilters() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // list title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (state.selectedMealType.equals("All", ignoreCase = true)) "All Recipes" else "${state.selectedMealType} Recipes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    AnimatedVisibility(visible = showScrollToTop) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Back to top",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Top",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // offline banner
                AnimatedVisibility(visible = state.isFromOfflineCache) {
                    OfflineCacheBanner(onRefresh = { viewModel.refresh() })
                }

                // list recipe
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoadingInitial -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    GifLoadingView(modifier = Modifier.size(72.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Fetching delicious recipes...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        state.errorMessage != null && state.filteredRecipes.isEmpty() -> {
                            ErrorView(
                                message = state.errorMessage!!,
                                onRetry = { viewModel.loadInitialRecipes() }
                            )
                        }

                        state.filteredRecipes.isEmpty() -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize().padding(32.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Empty",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No recipes found",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Try adjusting your search query or filters",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedButton(onClick = { viewModel.clearFilters() }) {
                                        Text("Reset Filters")
                                    }
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 110.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.filteredRecipes,
                                    key = { it.id }
                                ) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        isFavorite = state.favoriteIds.contains(recipe.id),
                                        onRecipeClick = onRecipeClick,
                                        onFavoriteClick = { viewModel.toggleFavorite(it) }
                                    )
                                }

                                // loading ui
                                item {
                                    when {
                                        state.isLoadingMore -> {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    GifLoadingView(modifier = Modifier.size(24.dp))
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        text = "Loading more recipes...",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                        state.errorMessage != null && state.filteredRecipes.isNotEmpty() -> {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                    modifier = Modifier.clickable { viewModel.loadNextPage() }
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                                    ) {
                                                        Text(
                                                            text = "Connection lost. Tap to retry loading more.",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.Medium,
                                                            color = MaterialTheme.colorScheme.onErrorContainer
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
