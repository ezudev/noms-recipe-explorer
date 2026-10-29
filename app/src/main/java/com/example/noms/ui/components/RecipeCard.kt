package com.example.noms.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noms.data.model.Recipe


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean,
    onRecipeClick: (Recipe) -> Unit,
    onFavoriteClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRecipeClick(recipe) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncRecipeImage(
                    url = recipe.image,
                    contentDescription = recipe.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .shadow(
                            elevation = 5.dp, // Controls how dramatic and blurry the shadow looks
                            shape = RoundedCornerShape(16.dp), // Must match your clip shape exactly
                            clip = false // Prevents the shadow itself from being aggressively cut off
                        )
                        .clip(RoundedCornerShape(16.dp)) // Clips the actual image content
                )


                // cuisine tag
                if (recipe.cuisine.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = recipe.cuisine,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // bookmark button
                IconButton(
                    onClick = { onFavoriteClick(recipe) },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // title
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", recipe.rating),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${recipe.reviewCount})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // difficulty
                    val (difficultyBg, difficultyText) = when (recipe.difficulty.lowercase()) {
                        "easy" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                        "medium" -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                        "hard" -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = difficultyBg
                    ) {
                        Text(
                            text = recipe.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = difficultyText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (recipe.prepTimeMinutes + recipe.cookTimeMinutes > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timelapse,
                                contentDescription = "Time",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.prepTimeMinutes + recipe.cookTimeMinutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (recipe.caloriesPerServing > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Calories",
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.caloriesPerServing} kcal",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PeopleAlt,
                            contentDescription = "Servings",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.servings} servings",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // meal type tag
                if (recipe.mealType.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        recipe.mealType.take(3).forEach { meal ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = meal,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
