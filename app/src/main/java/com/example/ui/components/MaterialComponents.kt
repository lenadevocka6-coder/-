package com.example.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.BatteryManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.IconShape
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

fun getShapeForIcon(shape: IconShape): Shape {
    return when (shape) {
        IconShape.CIRCLE -> CircleShape
        IconShape.SQUIRCLE -> RoundedCornerShape(percent = 32)
        IconShape.ROUNDED_SQUARE -> RoundedCornerShape(percent = 18)
        IconShape.TEARDROP -> RoundedCornerShape(
            topStartPercent = 50,
            topEndPercent = 50,
            bottomEndPercent = 50,
            bottomStartPercent = 12
        )
        IconShape.PEBBLE -> RoundedCornerShape(percent = 38)
    }
}

@Composable
fun AtAGlanceWidget(
    weatherCity: String,
    weatherTemp: Int,
    weatherCondition: String,
    isDark: Boolean,
    onWeatherClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Date()) }
    val context = LocalContext.current
    var batteryPct by remember { mutableIntStateOf(85) }
    var isCharging by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000)
        }
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                if (level >= 0 && scale > 0) {
                    batteryPct = (level * 100) / scale
                }
                isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL)
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale("ru")) }
    val formattedDate = dateFormat.format(currentTime).replaceFirstChar { it.uppercase() }

    val textColor = Color.White
    val subtextColor = Color.White.copy(alpha = 0.85f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        // Date Header
        Text(
            text = formattedDate,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.shadow(elevation = 2.dp, shape = CircleShape)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Weather and Battery row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable { onWeatherClick() }
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Погода",
                tint = Color(0xFFFFCC00),
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "$weatherTemp°C • $weatherCondition, $weatherCity",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = subtextColor
            )

            if (isCharging) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Зарядка",
                    tint = Color(0xFF66BB6A),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "$batteryPct%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtextColor
                )
            }
        }
    }
}

@Composable
fun PixelSearchPill(
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillBg = if (isDark) Color(0xFF303134) else Color(0xFFFFFFFF)
    val pillBorder = if (isDark) Color(0xFF404245) else Color(0xFFE0E0E0)
    val placeholderColor = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368)

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = pillBg,
        shadowElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(0.8.dp, pillBorder, CircleShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Google 'G' Emblem (Google 4-color)
            GoogleLogoIcon(modifier = Modifier.size(22.dp))

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "Поиск приложений...",
                fontSize = 15.sp,
                color = placeholderColor,
                modifier = Modifier.weight(1f)
            )

            // Assistant Mic Icon
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Голосовой поиск",
                tint = if (isDark) Color(0xFF8AB4F8) else Color(0xFF1A73E8),
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onClick() }
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Lens Icon
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Объектив",
                tint = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368),
                modifier = Modifier
                    .size(19.dp)
                    .clickable { onClick() }
            )
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val strokeW = w * 0.22f
        val r = (w - strokeW) / 2f

        // Red arc (top)
        drawArc(
            color = GoogleRed,
            startAngle = 195f,
            sweepAngle = 100f,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2, r * 2)
        )

        // Yellow arc (left)
        drawArc(
            color = GoogleYellow,
            startAngle = 125f,
            sweepAngle = 80f,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2, r * 2)
        )

        // Green arc (bottom)
        drawArc(
            color = GoogleGreen,
            startAngle = 45f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2, r * 2)
        )

        // Blue arc (right + horizontal bar)
        drawArc(
            color = GoogleBlue,
            startAngle = -20f,
            sweepAngle = 70f,
            useCenter = false,
            style = Stroke(width = strokeW),
            topLeft = Offset(cx - r, cy - r),
            size = Size(r * 2, r * 2)
        )

        // Center cross bar for 'G'
        drawRect(
            color = GoogleBlue,
            topLeft = Offset(cx - strokeW * 0.2f, cy - strokeW / 2f),
            size = Size(r + strokeW * 0.2f, strokeW)
        )
    }
}

@Composable
fun MaterialDock(
    apps: List<AppInfo>,
    iconShape: IconShape,
    iconScale: Float,
    showSearchInDock: Boolean,
    isDark: Boolean,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dock icons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (app in apps.take(5)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(iconScale)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAppClick(app) }
                        .padding(4.dp)
                ) {
                    AppIconGraphic(
                        drawable = app.icon,
                        label = app.label,
                        shape = iconShape,
                        size = 50.dp
                    )
                }
            }
        }

        if (showSearchInDock) {
            Spacer(modifier = Modifier.height(6.dp))
            PixelSearchPill(
                isDark = isDark,
                onClick = onSearchClick
            )
        }
    }
}

@Composable
fun AppIconGraphic(
    drawable: Drawable?,
    label: String,
    shape: IconShape = IconShape.CIRCLE,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(drawable) {
        if (drawable != null) {
            drawableToBitmap(drawable)
        } else {
            null
        }
    }

    val iconShapeImpl = getShapeForIcon(shape)

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = label,
            modifier = modifier
                .size(size)
                .clip(iconShapeImpl)
        )
    } else {
        // Clean Material initial letter badge
        val char = label.firstOrNull()?.uppercase() ?: "A"
        val bgColor = remember(label) {
            val colors = listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen, Color(0xFF673AB7), Color(0xFF009688))
            val hash = kotlin.math.abs(label.hashCode())
            colors[hash % colors.size]
        }

        Box(
            modifier = modifier
                .size(size)
                .clip(iconShapeImpl)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = char,
                fontSize = (size.value * 0.45f).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
