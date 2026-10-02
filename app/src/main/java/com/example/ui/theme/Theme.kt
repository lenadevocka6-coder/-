package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.model.MaterialAccent
import com.example.model.ThemeMode

private fun createDarkColorScheme(accent: Color, isAmoled: Boolean) = darkColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent.copy(alpha = 0.25f),
    onPrimaryContainer = Color.White,
    secondary = accent,
    onSecondary = Color.White,
    background = if (isAmoled) AmoledBackground else MatDarkBackground,
    onBackground = MatDarkTextPrimary,
    surface = if (isAmoled) AmoledSurface else MatDarkSurface,
    onSurface = MatDarkTextPrimary,
    surfaceVariant = MatDarkSurfaceVariant,
    onSurfaceVariant = MatDarkTextSecondary,
    outline = MatDarkBorder
)

private fun createLightColorScheme(accent: Color) = lightColorScheme(
    primary = accent,
    onPrimary = Color.White,
    primaryContainer = accent.copy(alpha = 0.15f),
    onPrimaryContainer = accent,
    secondary = accent,
    onSecondary = Color.White,
    background = MatLightBackground,
    onBackground = MatLightTextPrimary,
    surface = MatLightSurface,
    onSurface = MatLightTextPrimary,
    surfaceVariant = MatLightSurfaceVariant,
    onSurfaceVariant = MatLightTextSecondary,
    outline = MatLightBorder
)

@Composable
fun MaterialLauncherTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accent: MaterialAccent = MaterialAccent.PIXEL_BLUE,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK_AMOLED, ThemeMode.DARK_SURFACE -> true
        ThemeMode.SYSTEM -> systemDark
    }
    val isAmoled = themeMode == ThemeMode.DARK_AMOLED

    val colorScheme = if (isDark) {
        createDarkColorScheme(accent.color, isAmoled)
    } else {
        createLightColorScheme(accent.color)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
