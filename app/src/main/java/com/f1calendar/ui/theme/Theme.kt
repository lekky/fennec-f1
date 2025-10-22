package com.f1calendar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = F1Red,
    secondary = F1Gold,
    background = F1Black,
    surface = Color(0xFF1E1E2E),
    onPrimary = F1White,
    onSecondary = F1Black,
    onBackground = F1White,
    onSurface = F1White
)

private val LightColorScheme = lightColorScheme(
    primary = F1Red,
    secondary = F1Gold,
    background = Color(0xFFF5F5F5),
    surface = F1White,
    onPrimary = F1White,
    onSecondary = F1Black,
    onBackground = F1Black,
    onSurface = F1Black
)

@Composable
fun F1CalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
