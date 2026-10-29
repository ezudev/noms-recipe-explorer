package com.example.noms.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AsyncRecipeImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val lightGreyBg = Color(0xFFEEEEEE)

    GlideImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = placeholder {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightGreyBg),
                contentAlignment = Alignment.Center
            ) {
                GifLoadingView(
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        failure = placeholder {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(lightGreyBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Failed to load image",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    )
}
