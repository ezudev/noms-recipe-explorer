package com.example.noms.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Tapas
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterBar(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
    selectedMealType: String,
    onMealTypeSelected: (String) -> Unit,
    availableMealTypes: List<String>,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficulties = listOf("All", "Easy", "Medium", "Hard")
    val isFilterActive = !selectedDifficulty.equals("All", ignoreCase = true) ||
            !selectedMealType.equals("All", ignoreCase = true)

    val dynamicMealTypes = listOf("All Meals") + availableMealTypes.filterNot { it.equals("All", ignoreCase = true) }

    Column(modifier = modifier.fillMaxWidth()) {
        // header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Filter Recipes",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isFilterActive) {
                TextButton(onClick = onClearFilters) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // difficulty
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Text(
                text = "Difficulty: ",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )

            difficulties.forEach { diff ->
                val isSelected = selectedDifficulty.equals(diff, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onDifficultySelected(diff) },
                    label = {
                        Text(
                            text = diff,
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // meal type
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            dynamicMealTypes.forEach { mealType ->
                val isSelected = if (mealType == "All Meals") {
                    selectedMealType.equals("All", ignoreCase = true)
                } else {
                    selectedMealType.equals(mealType, ignoreCase = true)
                }

                val tileIcon: ImageVector = when (mealType.lowercase()) {
                    "all meals", "popular" -> Icons.Default.LocalFireDepartment
                    "dinner" -> Icons.Default.DinnerDining
                    "lunch" -> Icons.Default.LunchDining
                    "breakfast" -> Icons.Default.BreakfastDining
                    "snack" -> Icons.Default.Cookie
                    "dessert" -> Icons.Default.Cake
                    "beverage", "drinks" -> Icons.Default.LocalCafe
                    "appetizer" -> Icons.Default.Tapas
                    "pizza" -> Icons.Default.LocalPizza
                    "asian" -> Icons.Default.RamenDining
                    else -> Icons.Default.RestaurantMenu
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (mealType == "All Meals") {
                            onMealTypeSelected("All")
                        } else {
                            onMealTypeSelected(mealType)
                        }
                    }
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = tileIcon,
                                contentDescription = mealType,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mealType,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
