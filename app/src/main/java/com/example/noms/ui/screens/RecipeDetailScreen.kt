package com.example.noms.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noms.data.model.Recipe
import com.example.noms.ui.components.AsyncRecipeImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavoriteClick: (Recipe) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val checkedIngredients = remember { mutableStateMapOf<Int, Boolean>() }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncRecipeImage(
                    url = recipe.image,
                    contentDescription = recipe.name,
                    modifier = Modifier.fillMaxSize()
                )

                // back bookmark button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter)
                ) {

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }


                    IconButton(
                        onClick = { onFavoriteClick(recipe) },
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }


            Surface(
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-28).dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                ) {

                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // title
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (recipe.cuisine.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recipe.cuisine,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // rating
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rating",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f", recipe.rating),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // detail capsul
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val totalTime = recipe.prepTimeMinutes + recipe.cookTimeMinutes
                        CapsuleStatBadge(
                            icon = Icons.Default.Timelapse,
                            value = if (totalTime > 0) "${totalTime}\nmins" else "15\nmins"
                        )
                        CapsuleStatBadge(
                            icon = Icons.Default.PeopleAlt,
                             value = "${recipe.servings}\nServings"

                        )
                        CapsuleStatBadge(
                            icon = Icons.Default.LocalFireDepartment,
                            value = if (recipe.caloriesPerServing > 0) "${recipe.caloriesPerServing}\nCal" else "100\nCal"
                        )
                        CapsuleStatBadge(
                            icon = Icons.Default.Layers,
                            value = recipe.difficulty.ifBlank { "Easy" }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // ingredients
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ingredients",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${recipe.ingredients.size} items",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        recipe.ingredients.forEachIndexed { index, ingredient ->
                            val isChecked = checkedIngredients[index] ?: false
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        checkedIngredients[index] = !isChecked
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checkedIngredients[index] = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        uncheckedColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // directions
                    Text(
                        text = "Directions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        recipe.instructions.forEach { step ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Yellow Bullet Dot
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // tags
                    if (recipe.mealType.isNotEmpty() || recipe.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(28.dp))
                        Text(
                            text = "Categories & Tags",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            recipe.mealType.forEach { meal ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = meal,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            recipe.tags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "#$tag",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun CapsuleStatBadge(
    icon: ImageVector,
    value: String
) {
    Row {
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .width(63.dp)
                .height(110.dp)

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                // Top White Circle with Icon
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(55.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Bottom Label / Value
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}
