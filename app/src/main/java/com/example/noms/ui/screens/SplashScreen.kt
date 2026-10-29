package com.example.noms.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.noms.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1600)
        onTimeout()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(R.drawable.noms_cookie_logo),
            contentDescription = "Noms Splash Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(140.dp)
                .padding(horizontal = 32.dp)
                .alpha(alphaAnim)
                .scale(scaleAnim)
        )
    }
}
