package com.globant.pretatit.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Schema de culori pentru tema deschisa (light)
private val LightColorScheme = lightColorScheme(
    primary = CorporateBlue,
    secondary = LightBlue,
    background = LightGray,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkGray,
    onSurface = DarkGray,
    error = AccentRed
)

// Vom folosi aceeasi tema si pentru dark mode pentru un aspect consistent
private val DarkColorScheme = lightColorScheme(
    primary = CorporateBlue,
    secondary = LightBlue,
    background = LightGray,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = DarkGray,
    onSurface = DarkGray,
    error = AccentRed
)


@Composable
fun PretatitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Am setat schema noastra de culori
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}