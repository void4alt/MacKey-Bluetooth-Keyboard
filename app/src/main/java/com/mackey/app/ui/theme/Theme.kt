package com.mackey.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MacBlueAccent,
    background = MacDarkBackground,
    surface = MacDarkSurface,
    surfaceVariant = MacDarkSurfaceVariant,
    onPrimary = MacDarkTextPrimary,
    onBackground = MacDarkTextPrimary,
    onSurface = MacDarkTextPrimary,
    onSurfaceVariant = MacDarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = MacBlueAccent,
    background = MacLightBackground,
    surface = MacLightSurface,
    surfaceVariant = MacLightSurfaceVariant,
    onPrimary = MacLightTextPrimary,
    onBackground = MacLightTextPrimary,
    onSurface = MacLightTextPrimary,
    onSurfaceVariant = MacLightTextSecondary
)

@Composable
fun MacKeyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

