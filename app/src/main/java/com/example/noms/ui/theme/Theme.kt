package com.example.noms.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryYellow,
    background = TextDark,
    surface = Color(0xFF2C2C2C),
    onPrimary = TextDark,
    onBackground = SurfaceLight,
    onSurface = SurfaceLight,
    tertiaryContainer = LightYellowTag,
    onTertiaryContainer = OnLightYellowTag
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryYellow,
    primaryContainer = PrimaryYellow,
    secondary = PrimaryYellowDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextDark,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextGray,
    tertiaryContainer = LightYellowTag,
    onTertiaryContainer = OnLightYellowTag
)

@Composable
fun NomsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
