package com.example.noms.ui.screens

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noms.data.model.Recipe
import com.example.noms.ui.components.RecipeCard
import com.example.noms.ui.components.SearchBarComponent
import com.example.noms.ui.components.TopBarComponent
import com.example.noms.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onRecipeClick: (Recipe) -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var isRefreshing by remember { mutableStateOf(false) }

    // reload list
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    // scroll to top after search
    LaunchedEffect(state.selectedMealType, state.searchQuery) {
        if (state.filteredFavorites.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadFavorites()
                isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 25 && listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0 && !isRefreshing) {
                            isRefreshing = true
                            viewModel.loadFavorites()
                            isRefreshing = false
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

                Text(
                    text = "Bookmarked Recipes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (state.favoriteRecipes.isNotEmpty()) {

                    SearchBarComponent(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                        placeholderText = "Search recipes..."
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.filteredFavorites.size} saved recipes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.favoriteRecipes.isEmpty() -> {
                            // list empty
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize().padding(24.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FavoriteBorder,
                                        contentDescription = "No Favorites",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No Bookmarks Saved",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Explore delicious recipes and tap the heart icon on any card to save it here for quick offline access!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = onNavigateToHome,
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text("Browse Recipes", fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        state.filteredFavorites.isEmpty() -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize().padding(24.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "No Match",
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No matching favorites",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Try clearing your search query",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            // list bookmsrk
                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 110.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.filteredFavorites,
                                    key = { it.id }
                                ) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        isFavorite = true,
                                        onRecipeClick = onRecipeClick,
                                        onFavoriteClick = { viewModel.removeFavorite(it.id) }
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
