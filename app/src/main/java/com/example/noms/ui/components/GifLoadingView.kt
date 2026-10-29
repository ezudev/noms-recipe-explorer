package com.example.noms.ui.components

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.noms.R

@Composable
fun GifLoadingView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                Glide.with(context)
                    .asGif()
                    .load(R.drawable.food_carousel)
                    .into(this)
            }
        }
    )
}
