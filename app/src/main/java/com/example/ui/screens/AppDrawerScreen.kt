package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.LauncherSettings
import com.example.ui.components.AppIconGraphic
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDrawerScreen(
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
        val sheetBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
        val searchBg = if (isDark) Color(0xFF2D2E30) else Color(0xFFF1F3F4)
        val textColor = if (isDark) Color(0xFFE8EAED) else Color(0xFF202124)
        val subtextColor = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable { onClose() }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {}
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = sheetBg,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag Handle pill
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(subtextColor.copy(alpha = 0.4f))
                            .clickable { onClose() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Material 2 Search Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = CircleShape,
                            color = searchBg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                GoogleLogoIcon(modifier = Modifier.size(20.dp))

                                Spacer(modifier = Modifier.width(10.dp))

                                BasicTextField(
                                    value = uiState.searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = textColor
                                    ),
                                    cursorBrush = SolidColor(accent),
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        if (uiState.searchQuery.isEmpty()) {
                                            Text(
                                                text = "Поиск приложений...",
                                                fontSize = 14.sp,
                                                color = subtextColor
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.onSearchQueryChanged("") },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Очистить",
                                            tint = subtextColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = subtextColor
                            )
                        }
                    }

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppCategory.entries.forEach { category ->
                            val isSelected = uiState.selectedCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) accent.copy(alpha = 0.15f) else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) accent else subtextColor.copy(alpha = 0.3f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.onCategorySelected(category) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = category.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) accent else subtextColor
                                )
                            }
                        }
                    }

                    // Suggested Apps row (Android 8-11 Pixel style) when not searching
                    if (uiState.searchQuery.isEmpty() && uiState.selectedCategory == AppCategory.ALL && uiState.suggestedApps.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Часто используемые",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = subtextColor
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                uiState.suggestedApps.forEach { app ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(62.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { viewModel.launchApp(app) }
                                            .padding(2.dp)
                                    ) {
                                        AppIconGraphic(
                                            drawable = app.icon,
                                            label = app.label,
                                            shape = settings.iconShape,
                                            size = 46.dp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = app.label,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center,
                                            color = textColor
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = subtextColor.copy(alpha = 0.2f),
                                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                            )
                        }
                    }

                    // Apps Grid
                    if (uiState.filteredApps.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = "Ничего не найдено",
                                    tint = subtextColor,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = "Приложения не найдены",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textColor
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(settings.gridColumns),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(uiState.filteredApps, key = { it.packageName }) { app ->
                                DrawerAppItem(
                                    app = app,
                                    settings = settings,
                                    textColor = textColor,
                                    onClick = { viewModel.launchApp(app) },
                                    onLongClick = { viewModel.showAppMenu(app) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawerAppItem(
    app: AppInfo,
    settings: LauncherSettings,
    textColor: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(settings.iconScale)
            .clip(RoundedCornerShape(10.dp))
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = textColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
