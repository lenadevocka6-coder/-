package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.WallpaperPreset
import com.example.ui.theme.WpAmoledEnd
import com.example.ui.theme.WpAmoledStart
import com.example.ui.theme.WpAndroid10End
import com.example.ui.theme.WpAndroid10Start
import com.example.ui.theme.WpAndroid11End
import com.example.ui.theme.WpAndroid11Start
import com.example.ui.theme.WpAndroid9End
import com.example.ui.theme.WpAndroid9Start
import com.example.ui.theme.WpPixelEnd
import com.example.ui.theme.WpPixelStart

@Composable
fun MaterialWallpaperBackground(
    wallpaper: WallpaperPreset,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        when (wallpaper) {
            WallpaperPreset.PIXEL_CLEAN -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A5298),
                            Color(0xFF1E3C72),
                            Color(0xFF0F1E36)
                        ),
                        startY = 0f,
                        endY = height
                    ),
                    size = size
                )
                // Subtle soft radial highlight
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x334285F4), Color.Transparent),
                        center = Offset(width * 0.5f, height * 0.3f),
                        radius = width * 0.7f
                    ),
                    radius = width * 0.7f,
                    center = Offset(width * 0.5f, height * 0.3f)
                )
            }
            WallpaperPreset.ANDROID_10_DARK -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1D263B),
                            Color(0xFF121824),
                            Color(0xFF0B0E14)
                        ),
                        startY = 0f,
                        endY = height
                    ),
                    size = size
                )
            }
            WallpaperPreset.ANDROID_11_MINIMAL -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1F3160),
                            Color(0xFF111D38),
                            Color(0xFF080D1A)
                        ),
                        startY = 0f,
                        endY = height
                    ),
                    size = size
                )
            }
            WallpaperPreset.ANDROID_9_PIE -> {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A3052),
                            Color(0xFF2C223B),
                            Color(0xFF161224)
                        ),
                        startY = 0f,
                        endY = height
                    ),
                    size = size
                )
            }
            WallpaperPreset.AMOLED_PURE -> {
                drawRect(
                    color = Color.Black,
                    size = size
                )
            }
        }
    }
}
