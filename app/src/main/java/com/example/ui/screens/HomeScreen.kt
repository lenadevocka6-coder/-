package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.LauncherSettings
import com.example.model.SearchPlacement
import com.example.ui.components.AppIconGraphic
import com.example.ui.components.AtAGlanceWidget
import com.example.ui.components.MaterialDock
import com.example.ui.components.MaterialWallpaperBackground
import com.example.ui.components.PixelSearchPill
import com.example.ui.components.detectLauncherGestures
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    uiState: LauncherUiState,
    settings: LauncherSettings,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val accent = settings.accent.color

    var showGestureToast by remember { mutableStateOf(false) }
    var gestureToastText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.lastDetectedGesture) {
        val gesture = uiState.lastDetectedGesture
        if (!gesture.isNullOrBlank()) {
            gestureToastText = gesture
            showGestureToast = true
            delay(1600)
            showGestureToast = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .detectLauncherGestures(
                onGesture = { gestureType ->
                    if (gestureType == com.example.model.GestureType.LONG_PRESS) {
                        viewModel.openHomeContextMenu()
                    } else {
                        viewModel.handleGesture(gestureType)
                    }
                }
            )
    ) {
        // 1. Android 8-11 Curated Wallpaper Background
        MaterialWallpaperBackground(wallpaper = settings.wallpaper)

        // 2. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // At a Glance Widget (Android Pixel style)
            if (settings.showAtAGlance) {
                Spacer(modifier = Modifier.height(10.dp))
                AtAGlanceWidget(
                    weatherCity = settings.weatherCity,
                    weatherTemp = settings.weatherTemp,
                    weatherCondition = settings.weatherCondition,
                    isDark = isDark,
                    onWeatherClick = { viewModel.openWeatherDialog() }
                )
            }

            // Top Search Bar (if placement is TOP)
            if (settings.searchPlacement == SearchPlacement.TOP) {
                Spacer(modifier = Modifier.height(10.dp))
                PixelSearchPill(
                    isDark = isDark,
                    onClick = { viewModel.openSearch() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Home Apps Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(settings.gridColumns),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.homeApps, key = { it.packageName }) { app ->
                        HomeAppGridItem(
                            app = app,
                            settings = settings,
                            isDark = isDark,
                            onClick = { viewModel.launchApp(app) },
                            onLongClick = { viewModel.showAppMenu(app) }
                        )
                    }
                }
            }

            // Material Pixel Dock (at bottom with Search Pill)
            MaterialDock(
                apps = uiState.dockApps,
                iconShape = settings.iconShape,
                iconScale = settings.iconScale,
                showSearchInDock = settings.searchPlacement == SearchPlacement.DOCK,
                isDark = isDark,
                onAppClick = { viewModel.launchApp(it) },
                onAppLongClick = { viewModel.showAppMenu(it) },
                onSearchClick = { viewModel.openSearch() }
            )
        }

        // Gesture Detection Feedback Toast
        AnimatedVisibility(
            visible = showGestureToast,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 48.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = if (isDark) Color(0xFF323232) else Color(0xFF202124),
                shadowElevation = 4.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = gestureToastText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Home Long Press Menu (Classic Android 8-11: Wallpapers, Widgets, Home Settings)
        if (uiState.isHomeContextMenuOpen) {
            HomeContextMenuDialog(
                isDark = isDark,
                accent = accent,
                onDismiss = { viewModel.closeHomeContextMenu() },
                onOpenSettings = { viewModel.openSettings() },
                onOpenWallpapers = {
                    viewModel.openSettings()
                },
                onOpenWeather = {
                    viewModel.closeHomeContextMenu()
                    viewModel.openWeatherDialog()
                }
            )
        }

        // Individual App Long-Press Context Dialog
        if (uiState.selectedAppForMenu != null) {
            val app = uiState.selectedAppForMenu
            AppContextDialog(
                app = app,
                settings = settings,
                isDark = isDark,
                accent = accent,
                onDismiss = { viewModel.closeAppMenu() },
                onLaunch = { viewModel.launchApp(app) },
                onTogglePin = { viewModel.togglePinApp(app) },
                onHide = { viewModel.hideApp(app) },
                onInfo = { viewModel.openAppDetails(app) }
            )
        }

        // Screen Lock / Sleep Overlay
        if (uiState.isSleepModeActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { viewModel.wakeFromSleep() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Блокировка",
                        tint = accent,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Экран заблокирован",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = "Нажмите в любом месте для разблокировки",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeAppGridItem(
    app: AppInfo,
    settings: LauncherSettings,
    isDark: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(settings.iconScale)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(52.dp),
            contentAlignment = Alignment.Center
        ) {
            AppIconGraphic(
                drawable = app.icon,
                label = app.label,
                shape = settings.iconShape,
                size = 50.dp
            )

            if (app.isPinned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(settings.accent.color)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        }

        if (settings.showIconLabels) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = app.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun HomeContextMenuDialog(
    isDark: Boolean,
    accent: Color,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenWallpapers: () -> Unit,
    onOpenWeather: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Главный экран",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFE8EAED) else Color(0xFF202124)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ContextMenuItem(
                    icon = Icons.Default.Palette,
                    title = "Стили и обои",
                    accent = accent,
                    onClick = onOpenWallpapers
                )
                ContextMenuItem(
                    icon = Icons.Default.Widgets,
                    title = "Виджет «Вкратце» и погода",
                    accent = accent,
                    onClick = onOpenWeather
                )
                ContextMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Настройки лаунчера",
                    accent = accent,
                    onClick = onOpenSettings
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть", color = accent)
            }
        },
        containerColor = if (isDark) Color(0xFF2D2E30) else Color(0xFFFFFFFF),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun AppContextDialog(
    app: AppInfo,
    settings: LauncherSettings,
    isDark: Boolean,
    accent: Color,
    onDismiss: () -> Unit,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit,
    onHide: () -> Unit,
    onInfo: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppIconGraphic(
                    drawable = app.icon,
                    label = app.label,
                    shape = settings.iconShape,
                    size = 38.dp
                )
                Text(
                    text = app.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFE8EAED) else Color(0xFF202124)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ContextMenuItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Запустить",
                    accent = accent,
                    onClick = onLaunch
                )
                ContextMenuItem(
                    icon = Icons.Default.PushPin,
                    title = if (app.isPinned) "Убрать с главного экрана" else "Закрепить на главном экране",
                    accent = accent,
                    onClick = onTogglePin
                )
                ContextMenuItem(
                    icon = Icons.Default.Info,
                    title = "О приложении",
                    accent = accent,
                    onClick = onInfo
                )
                ContextMenuItem(
                    icon = Icons.Default.VisibilityOff,
                    title = "Скрыть приложение",
                    accent = Color(0xFFD93025),
                    onClick = onHide
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Готово", color = accent)
            }
        },
        containerColor = if (isDark) Color(0xFF2D2E30) else Color(0xFFFFFFFF),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ContextMenuItem(
    icon: ImageVector,
    title: String,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = accent,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Unspecified
        )
    }
}
