package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ThemeMode
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.AppSearchOverlay
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WeatherEditDialog
import com.example.ui.theme.MaterialLauncherTheme
import com.example.ui.viewmodel.LauncherUiState
import com.example.ui.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val systemDark = isSystemInDarkTheme()
            val isDark = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK_AMOLED, ThemeMode.DARK_SURFACE -> true
                ThemeMode.SYSTEM -> systemDark
            }

            MaterialLauncherTheme(
                themeMode = settings.themeMode,
                accent = settings.accent
            ) {
                LauncherRoot(
                    viewModel = viewModel,
                    uiState = uiState,
                    settings = settings,
                    isDark = isDark
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadApps()
    }
}

@Composable
fun LauncherRoot(
    viewModel: LauncherViewModel,
    uiState: LauncherUiState,
    settings: com.example.model.LauncherSettings,
    isDark: Boolean
) {
    BackHandler(
        enabled = uiState.isSettingsOpen || uiState.isSearchOpen || uiState.isDrawerOpen || uiState.isSleepModeActive || uiState.isHomeContextMenuOpen
    ) {
        when {
            uiState.isSleepModeActive -> viewModel.wakeFromSleep()
            uiState.isHomeContextMenuOpen -> viewModel.closeHomeContextMenu()
            uiState.isSettingsOpen -> viewModel.closeSettings()
            uiState.isSearchOpen -> viewModel.closeSearch()
            uiState.isDrawerOpen -> viewModel.closeDrawer()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Home Screen
        HomeScreen(
            viewModel = viewModel,
            uiState = uiState,
            settings = settings,
            isDark = isDark
        )

        // Slide-up App Drawer
        AppDrawerScreen(
            isOpen = uiState.isDrawerOpen,
            viewModel = viewModel,
            uiState = uiState,
            settings = settings,
            isDark = isDark,
            onClose = { viewModel.closeDrawer() }
        )

        // Google Pixel Search Overlay
        AppSearchOverlay(
            isOpen = uiState.isSearchOpen,
            viewModel = viewModel,
            uiState = uiState,
            settings = settings,
            isDark = isDark,
            onClose = { viewModel.closeSearch() }
        )

        // Launcher Settings Screen
        SettingsScreen(
            isOpen = uiState.isSettingsOpen,
            viewModel = viewModel,
            uiState = uiState,
            settings = settings,
            isDark = isDark,
            onClose = { viewModel.closeSettings() }
        )

        // Weather Location Dialog
        WeatherEditDialog(
            isOpen = uiState.isWeatherDialogOpen,
            settings = settings,
            isDark = isDark,
            viewModel = viewModel,
            onDismiss = { viewModel.closeWeatherDialog() }
        )
    }
}
