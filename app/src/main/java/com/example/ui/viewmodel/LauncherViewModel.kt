package com.example.ui.viewmodel

import android.app.Application
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.PreferencesRepository
import com.example.model.AppCategory
import com.example.model.AppInfo
import com.example.model.GestureAction
import com.example.model.GestureType
import com.example.model.IconShape
import com.example.model.LauncherSettings
import com.example.model.MaterialAccent
import com.example.model.SearchPlacement
import com.example.model.ThemeMode
import com.example.model.WallpaperPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LauncherUiState(
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val suggestedApps: List<AppInfo> = emptyList(),
    val homeApps: List<AppInfo> = emptyList(),
    val dockApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: AppCategory = AppCategory.ALL,
    val isDrawerOpen: Boolean = false,
    val isSearchOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isHomeContextMenuOpen: Boolean = false,
    val isWeatherDialogOpen: Boolean = false,
    val isSleepModeActive: Boolean = false,
    val selectedAppForMenu: AppInfo? = null,
    val lastDetectedGesture: String? = null,
    val isLoading: Boolean = true
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val appRepo = AppRepository(application)
    private val prefsRepo = PreferencesRepository(application)
    private val vibrator = application.getSystemService(Vibrator::class.java)

    val settings: StateFlow<LauncherSettings> = prefsRepo.settingsFlow

    private val _uiState = MutableStateFlow(LauncherUiState())
    val uiState: StateFlow<LauncherUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val apps = appRepo.getInstalledApps()
            updateAppsState(apps)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun updateAppsState(apps: List<AppInfo>) {
        val s = settings.value
        val hidden = s.hiddenPackages
        val pinned = s.pinnedPackages

        val visibleApps = apps.filterNot { hidden.contains(it.packageName) }.map {
            it.copy(isPinned = pinned.contains(it.packageName))
        }

        // Dock apps: Pick 5 apps for the dock (e.g. Phone, Message, Chrome, Camera, Music)
        val dockList = if (s.dockPackages.isNotEmpty()) {
            val list = mutableListOf<AppInfo>()
            for (pkg in s.dockPackages) {
                visibleApps.find { it.packageName == pkg }?.let { list.add(it) }
            }
            if (list.size < 5) {
                val extras = visibleApps.filterNot { list.contains(it) }.take(5 - list.size)
                list.addAll(extras)
            }
            list
        } else {
            visibleApps.take(5)
        }

        // Home grid apps: Pinned apps first, then next non-dock apps
        val homeList = if (pinned.isNotEmpty()) {
            visibleApps.filter { it.isPinned }
        } else {
            visibleApps.drop(5).take(12)
        }

        // Suggested apps for drawer: top launched apps
        val suggested = visibleApps.sortedByDescending { it.launchCount }.take(5)

        _uiState.value = _uiState.value.copy(
            allApps = visibleApps,
            filteredApps = filterApps(visibleApps, _uiState.value.searchQuery, _uiState.value.selectedCategory),
            suggestedApps = suggested,
            homeApps = homeList,
            dockApps = dockList
        )
    }

    private fun filterApps(
        apps: List<AppInfo>,
        query: String,
        category: AppCategory
    ): List<AppInfo> {
        val q = query.trim().lowercase()
        return apps.filter { app ->
            val matchesQuery = q.isEmpty() ||
                app.label.lowercase().contains(q) ||
                app.packageName.lowercase().contains(q)

            val matchesCategory = when (category) {
                AppCategory.ALL -> true
                AppCategory.FAVORITES -> app.isPinned
                AppCategory.RECENT -> app.launchCount > 0
                else -> app.category == category
            }

            matchesQuery && matchesCategory
        }.sortedWith(
            compareByDescending<AppInfo> { it.isPinned }
                .thenBy { it.label.lowercase() }
        )
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = newQuery,
            filteredApps = filterApps(_uiState.value.allApps, newQuery, _uiState.value.selectedCategory)
        )
    }

    fun onCategorySelected(category: AppCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredApps = filterApps(_uiState.value.allApps, _uiState.value.searchQuery, category)
        )
    }

    fun openDrawer() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(isDrawerOpen = true)
    }

    fun closeDrawer() {
        _uiState.value = _uiState.value.copy(isDrawerOpen = false, searchQuery = "")
        _uiState.value = _uiState.value.copy(
            filteredApps = filterApps(_uiState.value.allApps, "", _uiState.value.selectedCategory)
        )
    }

    fun openSearch() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(isSearchOpen = true)
    }

    fun closeSearch() {
        _uiState.value = _uiState.value.copy(isSearchOpen = false, searchQuery = "")
    }

    fun openSettings() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(isSettingsOpen = true, isHomeContextMenuOpen = false)
    }

    fun closeSettings() {
        _uiState.value = _uiState.value.copy(isSettingsOpen = false)
        updateAppsState(_uiState.value.allApps)
    }

    fun openHomeContextMenu() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(isHomeContextMenuOpen = true)
    }

    fun closeHomeContextMenu() {
        _uiState.value = _uiState.value.copy(isHomeContextMenuOpen = false)
    }

    fun openWeatherDialog() {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(isWeatherDialogOpen = true)
    }

    fun closeWeatherDialog() {
        _uiState.value = _uiState.value.copy(isWeatherDialogOpen = false)
    }

    fun showAppMenu(app: AppInfo) {
        triggerHaptic()
        _uiState.value = _uiState.value.copy(selectedAppForMenu = app)
    }

    fun closeAppMenu() {
        _uiState.value = _uiState.value.copy(selectedAppForMenu = null)
    }

    fun wakeFromSleep() {
        _uiState.value = _uiState.value.copy(isSleepModeActive = false)
    }

    fun launchApp(app: AppInfo) {
        triggerHaptic()
        appRepo.launchApp(app)
        closeAppMenu()
        closeDrawer()
        closeSearch()
    }

    fun openAppDetails(app: AppInfo) {
        appRepo.openAppDetails(app.packageName)
        closeAppMenu()
    }

    fun togglePinApp(app: AppInfo) {
        prefsRepo.togglePinApp(app.packageName)
        updateAppsState(_uiState.value.allApps)
        closeAppMenu()
    }

    fun hideApp(app: AppInfo) {
        prefsRepo.toggleHideApp(app.packageName)
        updateAppsState(_uiState.value.allApps)
        closeAppMenu()
    }

    fun unhideApp(packageName: String) {
        prefsRepo.unhideApp(packageName)
        updateAppsState(_uiState.value.allApps)
    }

    fun openHomeSettings() {
        appRepo.openHomeSettings()
    }

    fun handleGesture(gestureType: GestureType) {
        val action = settings.value.gestures[gestureType] ?: GestureAction.NONE
        _uiState.value = _uiState.value.copy(
            lastDetectedGesture = "${gestureType.title} -> ${action.title}"
        )
        triggerHaptic()

        when (action) {
            GestureAction.OPEN_APP_DRAWER -> openDrawer()
            GestureAction.OPEN_SEARCH -> openSearch()
            GestureAction.OPEN_SETTINGS -> openSettings()
            GestureAction.EXPAND_NOTIFICATIONS -> appRepo.expandNotifications()
            GestureAction.LOCK_SCREEN -> {
                _uiState.value = _uiState.value.copy(isSleepModeActive = true)
            }
            GestureAction.SWITCH_WALLPAPER -> {
                prefsRepo.cycleNextWallpaper()
            }
            GestureAction.NONE -> Unit
        }
    }

    fun updateThemeMode(mode: ThemeMode) = prefsRepo.updateThemeMode(mode)
    fun updateAccent(accent: MaterialAccent) = prefsRepo.updateAccent(accent)
    fun updateWallpaper(wallpaper: WallpaperPreset) = prefsRepo.updateWallpaper(wallpaper)
    fun updateIconShape(shape: IconShape) = prefsRepo.updateIconShape(shape)
    fun updateSearchPlacement(placement: SearchPlacement) = prefsRepo.updateSearchPlacement(placement)
    fun updateIconScale(scale: Float) = prefsRepo.updateIconScale(scale)
    fun updateShowLabels(show: Boolean) = prefsRepo.updateShowLabels(show)
    fun updateGridColumns(cols: Int) = prefsRepo.updateGridColumns(cols)
    fun updateShowAtAGlance(show: Boolean) = prefsRepo.updateShowAtAGlance(show)
    fun updateGesture(type: GestureType, action: GestureAction) = prefsRepo.updateGesture(type, action)
    fun updateWeather(city: String, temp: Int, condition: String) = prefsRepo.updateWeather(city, temp, condition)

    private fun triggerHaptic() {
        try {
            vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (ignored: Exception) {}
    }
}
