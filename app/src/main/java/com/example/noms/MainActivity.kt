package com.example.noms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noms.data.model.Recipe
import com.example.noms.ui.screens.FavoritesScreen
import com.example.noms.ui.screens.RecipeDetailScreen
import com.example.noms.ui.screens.RecipeListScreen
import com.example.noms.ui.screens.SplashScreen
import com.example.noms.ui.theme.NomsTheme
import com.example.noms.ui.viewmodel.FavoritesViewModel
import com.example.noms.ui.viewmodel.RecipeListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NomsTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    NomsApp()
                }
            }
        }
    }
}

@Composable
fun NomsApp(
    recipeListViewModel: RecipeListViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel()
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedRecipe by rememberSaveable { mutableStateOf<Recipe?>(null) }

    val listUiState by recipeListViewModel.uiState.collectAsState()

    // handle back button in detail screen
    if (selectedRecipe != null) {
        BackHandler {
            selectedRecipe = null
            recipeListViewModel.refreshFavorites()
            favoritesViewModel.loadFavorites()
        }

        val isFav = listUiState.favoriteIds.contains(selectedRecipe!!.id)

        RecipeDetailScreen(
            recipe = selectedRecipe!!,
            isFavorite = isFav,
            onFavoriteClick = { recipe ->
                recipeListViewModel.toggleFavorite(recipe)
                favoritesViewModel.loadFavorites()
            },
            onBackClick = {
                selectedRecipe = null
                recipeListViewModel.refreshFavorites()
                favoritesViewModel.loadFavorites()
            }
        )
    } else {
        // main Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = {
                    val slideDirection = if (targetState == AppDestinations.HOME) {
                        AnimatedTransitionDirection.LEFT
                    } else {
                        AnimatedTransitionDirection.RIGHT
                    }
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> slideDirection * fullWidth },
                        animationSpec = tween(350)
                    ) + fadeIn(animationSpec = tween(350)) togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -slideDirection * fullWidth },
                                animationSpec = tween(350)
                            ) + fadeOut(animationSpec = tween(350))
                },
                label = "NavigationTransition"
            ) { destination ->
                when (destination) {
                    AppDestinations.HOME -> {
                        RecipeListScreen(
                            viewModel = recipeListViewModel,
                            onRecipeClick = { recipe ->
                                selectedRecipe = recipe
                            }
                        )
                    }
                    AppDestinations.FAVORITES -> {
                        FavoritesScreen(
                            viewModel = favoritesViewModel,
                            onRecipeClick = { recipe ->
                                selectedRecipe = recipe
                            },
                            onNavigateToHome = {
                                currentDestination = AppDestinations.HOME
                            }
                        )
                    }
                }
            }

            // bottom navbar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    //.padding(bottom = 12.dp)
            ) {
                CustomBottomNavBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        if (currentDestination != destination) {
                            if (destination == AppDestinations.FAVORITES) {
                                favoritesViewModel.loadFavorites()
                            }
                            currentDestination = destination
                        }
                    }
                )
            }
        }
    }
}

enum class AppDestinations {
    HOME, FAVORITES
}

object AnimatedTransitionDirection {
    const val LEFT = -1
    const val RIGHT = 1
}

@Composable
fun CustomBottomNavBar(
    currentDestination: AppDestinations,
    onDestinationSelected: (AppDestinations) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(6.dp)
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentDestination == AppDestinations.HOME,
                onClick = { onDestinationSelected(AppDestinations.HOME) }
            )
            NavItem(
                icon = Icons.Default.Bookmark,
                label = "Saved",
                isSelected = currentDestination == AppDestinations.FAVORITES,
                onClick = { onDestinationSelected(AppDestinations.FAVORITES) }
            )
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 400),
        label = "NavBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 400),
        label = "NavContentColor"
    )

    val widthAnim by animateDpAsState(
        targetValue = if (isSelected) 185.dp else 76.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NavWidthAnim"
    )

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        modifier = Modifier
            .height(52.dp)
            .width(widthAnim)
            .clip(RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(200, delayMillis = 100)) + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut(animationSpec = tween(150)) + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
