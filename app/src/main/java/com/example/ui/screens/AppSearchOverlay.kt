package com.example.ui.screens

import android.app.SearchManager
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppInfo
import com.example.model.LauncherSettings
import com.example.ui.components.AppIconGraphic
import com.example.ui.components.GoogleLogoIcon
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

@Composable
fun AppSearchOverlay(
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
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        val accent = settings.accent.color
        val context = LocalContext.current
        val focusRequester = remember { FocusRequester() }

        val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFFAFAFA)
        val searchBoxBg = if (isDark) Color(0xFF242424) else Color(0xFFFFFFFF)
        val textColor = if (isDark) Color(0xFFE8EAED) else Color(0xFF202124)
        val subtextColor = if (isDark) Color(0xFF9AA0A6) else Color(0xFF5F6368)

        LaunchedEffect(isOpen) {
            if (isOpen) {
                try {
                    focusRequester.requestFocus()
                } catch (ignored: Exception) {}
            }
        }

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
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Top Search Bar (Google Pixel Search Box)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = CircleShape,
                    color = searchBoxBg,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = subtextColor
                            )
                        }

                        GoogleLogoIcon(modifier = Modifier.size(20.dp))

                        Spacer(modifier = Modifier.width(10.dp))

                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChanged(it) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = textColor
                            ),
                            cursorBrush = SolidColor(accent),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            decorationBox = { innerTextField ->
                                if (uiState.searchQuery.isEmpty()) {
                                    Text(
                                        text = "Поиск приложений...",
                                        fontSize = 15.sp,
                                        color = subtextColor
                                    )
                                }
                                innerTextField()
                            }
                        )

                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onSearchQueryChanged("") },
                                modifier = Modifier.size(32.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                // Web Search Shortcut Option (if query not empty)
                if (uiState.searchQuery.isNotBlank()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                        putExtra(SearchManager.QUERY, uiState.searchQuery)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Fallback to browser
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = searchBoxBg,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Поиск в Интернете",
                                tint = accent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Искать в Google «${uiState.searchQuery}»",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = accent
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "В браузере",
                                tint = subtextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Section header
                Text(
                    text = if (uiState.searchQuery.isEmpty()) "Все приложения (${uiState.filteredApps.size})" else "Найдено (${uiState.filteredApps.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtextColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )

                // Results list
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredApps, key = { it.packageName }) { app ->
                        SearchAppItemRow(
                            app = app,
                            settings = settings,
                            textColor = textColor,
                            subtextColor = subtextColor,
                            accent = accent,
                            onClick = { viewModel.launchApp(app) },
                            onTogglePin = { viewModel.togglePinApp(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAppItemRow(
    app: AppInfo,
    settings: LauncherSettings,
    textColor: Color,
    subtextColor: Color,
    accent: Color,
    onClick: () -> Unit,
    onTogglePin: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconGraphic(
                drawable = app.icon,
                label = app.label,
                shape = settings.iconShape,
                size = 42.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                Text(
                    text = app.category.title,
                    fontSize = 12.sp,
                    color = subtextColor
                )
            }

            IconButton(
                onClick = onTogglePin,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Закрепить",
                    tint = if (app.isPinned) accent else subtextColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
