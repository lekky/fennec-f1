package com.f1calendar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = F1Red,
    secondary = F1Gold,
    tertiary = Color(0xFF6DD3CE),
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF1E1E2E),
    surfaceVariant = Color(0xFF2A2A3A),
    onPrimary = F1White,
    onSecondary = F1Black,
    onBackground = F1White,
    onSurface = F1White,
    onSurfaceVariant = Color(0xFFE0E0E0),
    primaryContainer = Color(0xFF8B0000),
    onPrimaryContainer = F1White,
    secondaryContainer = Color(0xFFB8860B),
    onSecondaryContainer = F1Black
)

private val LightColorScheme = lightColorScheme(
    primary = F1Red,
    secondary = F1Gold,
    tertiary = Color(0xFF1B9AAA),
    background = Color(0xFFFCFCFC),
    surface = F1White,
    surfaceVariant = Color(0xFFF5F5F5),
    onPrimary = F1White,
    onSecondary = F1Black,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF5F5F5F),
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondaryContainer = Color(0xFFFFE8B3),
    onSecondaryContainer = Color(0xFF3D2E00)
)

@Composable
fun F1CalendarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
        shapes = Shapes,
        content = content
    )
}
