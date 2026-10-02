package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GestureAction
import com.example.model.GestureType
import com.example.model.IconShape
import com.example.model.LauncherSettings
import com.example.model.MaterialAccent
import com.example.model.SearchPlacement
import com.example.model.ThemeMode
import com.example.model.WallpaperPreset
import com.example.ui.components.detectLauncherGestures
import com.example.ui.components.getShapeForIcon
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun SettingsScreen(
    isOpen: Boolean,
    viewModel: LauncherViewModel,
    uiState: LauncherUiState,
    settings: LauncherSettings,
    isDark: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isOpen) {
        BackHandler {
            onClose()
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        val accent = settings.accent.color
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = listOf("Стиль и значки", "Жесты", "Обои", "Приложения")

        val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF8F9FA)
        val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
        val textColor = if (isDark) Color(0xFFE8EAED) else Color(0xFF202124)
        val subtextColor = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Header (Material 2 style)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = textColor
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = "Настройки лаунчера",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                        Text(
                            text = "Material Design 2 • Android 8–11",
                            fontSize = 12.sp,
                            color = subtextColor
                        )
                    }
                }

                // Material Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = accent
                        )
                    },
                    divider = {
                        HorizontalDivider(color = subtextColor.copy(alpha = 0.2f))
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) accent else subtextColor
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> StyleAndIconsTab(
                            settings = settings,
                            isDark = isDark,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            subtextColor = subtextColor,
                            accent = accent,
                            viewModel = viewModel
                        )
                        1 -> GesturesTab(
                            settings = settings,
                            isDark = isDark,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            subtextColor = subtextColor,
                            accent = accent,
                            viewModel = viewModel
                        )
                        2 -> WallpapersTab(
                            settings = settings,
                            isDark = isDark,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            subtextColor = subtextColor,
                            accent = accent,
                            viewModel = viewModel
                        )
                        3 -> AppsAndSystemTab(
                            uiState = uiState,
                            settings = settings,
                            isDark = isDark,
                            surfaceColor = surfaceColor,
                            textColor = textColor,
                            subtextColor = subtextColor,
                            accent = accent,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StyleAndIconsTab(
    settings: LauncherSettings,
    isDark: Boolean,
    surfaceColor: Color,
    textColor: Color,
    subtextColor: Color,
    accent: Color,
    viewModel: LauncherViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Icon Shape Selector (Signature Android 8-11 feature)
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Форма значков приложений",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Text(
                    text = "Стилизация адаптивных иконок (как в Android 8–11)",
                    fontSize = 12.sp,
                    color = subtextColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconShape.entries.forEach { shape ->
                        val isSelected = settings.iconShape == shape
                        val shapeImpl = getShapeForIcon(shape)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateIconShape(shape) }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(shapeImpl)
                                    .background(if (isSelected) accent else subtextColor.copy(alpha = 0.2f))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) accent else Color.Transparent,
                                        shape = shapeImpl
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Выбрано",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = shape.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) accent else subtextColor
                            )
                        }
                    }
                }
            }
        }

        // Theme Mode Selector
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Тема интерфейса",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accent.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) accent else subtextColor.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateThemeMode(mode) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (mode) {
                                    ThemeMode.SYSTEM -> "Системная"
                                    ThemeMode.LIGHT -> "Светлая"
                                    ThemeMode.DARK_AMOLED -> "AMOLED"
                                    ThemeMode.DARK_SURFACE -> "Темно-серая"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) accent else subtextColor
                            )
                        }
                    }
                }
            }
        }

        // Material Accent Colors
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Цветовой акцент Material",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MaterialAccent.entries.forEach { accentOption ->
                        val isSelected = settings.accent == accentOption
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(accentOption.color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.updateAccent(accentOption) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Выбрано",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Bar Placement
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Панель поиска Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                SearchPlacement.entries.forEach { placement ->
                    val isSelected = settings.searchPlacement == placement
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.updateSearchPlacement(placement) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = placement.title,
                            fontSize = 14.sp,
                            color = textColor
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Выбрано",
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // At a Glance toggle
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Виджет «Вкратце» (At a Glance)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Text(
                        text = "Отображает дату, день недели и текущую погоду",
                        fontSize = 12.sp,
                        color = subtextColor
                    )
                }
                Switch(
                    checked = settings.showAtAGlance,
                    onCheckedChange = { viewModel.updateShowAtAGlance(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = accent, checkedTrackColor = accent.copy(alpha = 0.5f))
                )
            }
        }

        // Icon Size & Labels
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Размер значков: ${(settings.iconScale * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Slider(
                    value = settings.iconScale,
                    onValueChange = { viewModel.updateIconScale(it) },
                    valueRange = 0.8f..1.2f,
                    colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Подписи под значками",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Switch(
                        checked = settings.showIconLabels,
                        onCheckedChange = { viewModel.updateShowLabels(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = accent, checkedTrackColor = accent.copy(alpha = 0.5f))
                    )
                }
            }
        }

        // Grid columns (4 or 5)
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Сетка рабочего стола",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(4, 5).forEach { col ->
                        val isColSelected = settings.gridColumns == col
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isColSelected) accent else Color.Transparent)
                                .border(1.dp, accent, RoundedCornerShape(6.dp))
                                .clickable { viewModel.updateGridColumns(col) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${col}x${col + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isColSelected) Color.White else textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GesturesTab(
    settings: LauncherSettings,
    isDark: Boolean,
    surfaceColor: Color,
    textColor: Color,
    subtextColor: Color,
    accent: Color,
    viewModel: LauncherViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Interactive Gesture Test Pad
        var detectedInTestPad by remember { mutableStateOf<String?>(null) }

        MaterialSectionCard(surfaceColor = surfaceColor) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .detectLauncherGestures(
                        onGesture = { type ->
                            detectedInTestPad = type.title
                        }
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Тест жестов",
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Интерактивная панель проверки жестов",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Text(
                        text = if (detectedInTestPad != null) "Распознан: $detectedInTestPad" else "Проведите пальцем или нажмите дважды",
                        fontSize = 12.sp,
                        color = if (detectedInTestPad != null) accent else subtextColor
                    )
                }
            }
        }

        Text(
            text = "Настройка действий жестов",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = subtextColor,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        GestureType.entries.forEach { gestureType ->
            val currentAction = settings.gestures[gestureType] ?: GestureAction.NONE
            var isMenuOpen by remember { mutableStateOf(false) }

            MaterialSectionCard(surfaceColor = surfaceColor) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isMenuOpen = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = gestureType.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                        Text(
                            text = gestureType.subtitle,
                            fontSize = 11.sp,
                            color = subtextColor
                        )
                    }

                    Box {
                        Text(
                            text = currentAction.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = accent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accent.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )

                        DropdownMenu(
                            expanded = isMenuOpen,
                            onDismissRequest = { isMenuOpen = false }
                        ) {
                            GestureAction.entries.forEach { action ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = action.title, fontWeight = FontWeight.Medium)
                                            Text(text = action.description, fontSize = 11.sp, color = subtextColor)
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateGesture(gestureType, action)
                                        isMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WallpapersTab(
    settings: LauncherSettings,
    isDark: Boolean,
    surfaceColor: Color,
    textColor: Color,
    subtextColor: Color,
    accent: Color,
    viewModel: LauncherViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Обои в стиле Android 8–11 и Pixel",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = subtextColor,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        WallpaperPreset.entries.forEach { preset ->
            val isSelected = settings.wallpaper == preset
            MaterialSectionCard(surfaceColor = surfaceColor) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateWallpaper(preset) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (preset) {
                                    WallpaperPreset.PIXEL_CLEAN -> Color(0xFF1E3C72)
                                    WallpaperPreset.ANDROID_10_DARK -> Color(0xFF121824)
                                    WallpaperPreset.ANDROID_11_MINIMAL -> Color(0xFF111D38)
                                    WallpaperPreset.ANDROID_9_PIE -> Color(0xFF4A3052)
                                    WallpaperPreset.AMOLED_PURE -> Color(0xFF000000)
                                }
                            )
                            .border(1.dp, subtextColor.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Выбрано",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = preset.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                        Text(
                            text = preset.description,
                            fontSize = 11.sp,
                            color = subtextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppsAndSystemTab(
    uiState: LauncherUiState,
    settings: LauncherSettings,
    isDark: Boolean,
    surfaceColor: Color,
    textColor: Color,
    subtextColor: Color,
    accent: Color,
    viewModel: LauncherViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Set Default Launcher Button
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.openHomeSettings() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Главный экран",
                    tint = accent,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Сделать лаунчером по умолчанию",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Text(
                        text = "Открыть системные настройки главного экрана",
                        fontSize = 12.sp,
                        color = subtextColor
                    )
                }
            }
        }

        // Hidden Apps Management
        Text(
            text = "Скрытые приложения",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = subtextColor,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        if (settings.hiddenPackages.isEmpty()) {
            MaterialSectionCard(surfaceColor = surfaceColor) {
                Text(
                    text = "Скрытых приложений нет. Чтобы скрыть приложение, зажмите его и выберите «Скрыть».",
                    fontSize = 13.sp,
                    color = subtextColor,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            settings.hiddenPackages.forEach { pkg ->
                MaterialSectionCard(surfaceColor = surfaceColor) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = pkg,
                            fontSize = 13.sp,
                            color = textColor,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.unhideApp(pkg) }) {
                            Text("Показать", color = accent)
                        }
                    }
                }
            }
        }

        // Stats
        MaterialSectionCard(surfaceColor = surfaceColor) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Информация",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Всего приложений в системе: ${uiState.allApps.size}",
                    fontSize = 13.sp,
                    color = subtextColor
                )
                Text(
                    text = "Закреплено на рабочем столе: ${settings.pinnedPackages.size}",
                    fontSize = 13.sp,
                    color = subtextColor
                )
            }
        }
    }
}

@Composable
fun MaterialSectionCard(
    surfaceColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = surfaceColor,
        shadowElevation = 1.dp
    ) {
        content()
    }
}
