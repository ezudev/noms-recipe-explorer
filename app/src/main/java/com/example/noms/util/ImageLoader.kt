package com.example.noms.util

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageLoader {
    suspend fun loadBitmap(context: Context, urlString: String): Bitmap? = withContext(Dispatchers.IO) {
        if (urlString.isBlank()) return@withContext null
        try {
            Glide.with(context)
                .asBitmap()
                .load(urlString)
                .submit()
                .get()
        } catch (_: Exception) {
            null
        }
    }
}
