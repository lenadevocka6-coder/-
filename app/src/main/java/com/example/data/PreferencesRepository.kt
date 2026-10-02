package com.example.data

import android.content.Context
import android.content.SharedPreferences
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

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("material_launcher_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<LauncherSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): LauncherSettings {
        val themeModeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr) }.getOrDefault(ThemeMode.SYSTEM)

        val accentStr = prefs.getString(KEY_ACCENT, MaterialAccent.PIXEL_BLUE.name) ?: MaterialAccent.PIXEL_BLUE.name
        val accent = runCatching { MaterialAccent.valueOf(accentStr) }.getOrDefault(MaterialAccent.PIXEL_BLUE)

        val wallpaperStr = prefs.getString(KEY_WALLPAPER, WallpaperPreset.PIXEL_CLEAN.name) ?: WallpaperPreset.PIXEL_CLEAN.name
        val wallpaper = runCatching { WallpaperPreset.valueOf(wallpaperStr) }.getOrDefault(WallpaperPreset.PIXEL_CLEAN)

        val shapeStr = prefs.getString(KEY_ICON_SHAPE, IconShape.CIRCLE.name) ?: IconShape.CIRCLE.name
        val iconShape = runCatching { IconShape.valueOf(shapeStr) }.getOrDefault(IconShape.CIRCLE)

        val searchPosStr = prefs.getString(KEY_SEARCH_PLACEMENT, SearchPlacement.DOCK.name) ?: SearchPlacement.DOCK.name
        val searchPlacement = runCatching { SearchPlacement.valueOf(searchPosStr) }.getOrDefault(SearchPlacement.DOCK)

        val iconScale = prefs.getFloat(KEY_ICON_SCALE, 1.0f)
        val showLabels = prefs.getBoolean(KEY_SHOW_LABELS, true)
        val gridCols = prefs.getInt(KEY_GRID_COLUMNS, 4)
        val showAtAGlance = prefs.getBoolean(KEY_SHOW_GLANCE, true)

        // Load gestures
        val gestureMap = mutableMapOf<GestureType, GestureAction>()
        GestureType.entries.forEach { type ->
            val defaultAction = when (type) {
                GestureType.SWIPE_UP -> GestureAction.OPEN_APP_DRAWER
                GestureType.SWIPE_DOWN -> GestureAction.EXPAND_NOTIFICATIONS
                GestureType.DOUBLE_TAP -> GestureAction.LOCK_SCREEN
                GestureType.LONG_PRESS -> GestureAction.OPEN_SETTINGS
                GestureType.PINCH_IN -> GestureAction.OPEN_SEARCH
                GestureType.PINCH_OUT -> GestureAction.SWITCH_WALLPAPER
            }
            val actionStr = prefs.getString("gesture_${type.name}", defaultAction.name) ?: defaultAction.name
            val action = runCatching { GestureAction.valueOf(actionStr) }.getOrDefault(defaultAction)
            gestureMap[type] = action
        }

        val pinnedSet = prefs.getStringSet(KEY_PINNED_APPS, emptySet()) ?: emptySet()
        val hiddenSet = prefs.getStringSet(KEY_HIDDEN_APPS, emptySet()) ?: emptySet()
        val dockString = prefs.getString(KEY_DOCK_APPS, "") ?: ""
        val dockList = if (dockString.isNotBlank()) dockString.split(",") else emptyList()

        val weatherCity = prefs.getString(KEY_WEATHER_CITY, "Москва") ?: "Москва"
        val weatherTemp = prefs.getInt(KEY_WEATHER_TEMP, 21)
        val weatherCondition = prefs.getString(KEY_WEATHER_COND, "Ясно") ?: "Ясно"

        return LauncherSettings(
            iconShape = iconShape,
            searchPlacement = searchPlacement,
            themeMode = themeMode,
            accent = accent,
            wallpaper = wallpaper,
            iconScale = iconScale,
            showIconLabels = showLabels,
            gridColumns = gridCols,
            showAtAGlance = showAtAGlance,
            gestures = gestureMap,
            pinnedPackages = pinnedSet,
            hiddenPackages = hiddenSet,
            dockPackages = dockList,
            weatherCity = weatherCity,
            weatherTemp = weatherTemp,
            weatherCondition = weatherCondition
        )
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, themeMode.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(themeMode = themeMode)
    }

    fun updateAccent(accent: MaterialAccent) {
        prefs.edit().putString(KEY_ACCENT, accent.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(accent = accent)
    }

    fun updateWallpaper(wallpaper: WallpaperPreset) {
        prefs.edit().putString(KEY_WALLPAPER, wallpaper.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(wallpaper = wallpaper)
    }

    fun cycleNextWallpaper() {
        val current = _settingsFlow.value.wallpaper
        val all = WallpaperPreset.entries
        val nextIdx = (all.indexOf(current) + 1) % all.size
        updateWallpaper(all[nextIdx])
    }

    fun updateIconShape(shape: IconShape) {
        prefs.edit().putString(KEY_ICON_SHAPE, shape.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(iconShape = shape)
    }

    fun updateSearchPlacement(placement: SearchPlacement) {
        prefs.edit().putString(KEY_SEARCH_PLACEMENT, placement.name).apply()
        _settingsFlow.value = _settingsFlow.value.copy(searchPlacement = placement)
    }

    fun updateIconScale(scale: Float) {
        prefs.edit().putFloat(KEY_ICON_SCALE, scale).apply()
        _settingsFlow.value = _settingsFlow.value.copy(iconScale = scale)
    }

    fun updateShowLabels(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_LABELS, show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showIconLabels = show)
    }

    fun updateGridColumns(cols: Int) {
        prefs.edit().putInt(KEY_GRID_COLUMNS, cols).apply()
        _settingsFlow.value = _settingsFlow.value.copy(gridColumns = cols)
    }

    fun updateShowAtAGlance(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_GLANCE, show).apply()
        _settingsFlow.value = _settingsFlow.value.copy(showAtAGlance = show)
    }

    fun updateGesture(type: GestureType, action: GestureAction) {
        prefs.edit().putString("gesture_${type.name}", action.name).apply()
        val currentGestures = _settingsFlow.value.gestures.toMutableMap()
        currentGestures[type] = action
        _settingsFlow.value = _settingsFlow.value.copy(gestures = currentGestures)
    }

    fun togglePinApp(packageName: String) {
        val current = _settingsFlow.value.pinnedPackages.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        prefs.edit().putStringSet(KEY_PINNED_APPS, current).apply()
        _settingsFlow.value = _settingsFlow.value.copy(pinnedPackages = current)
    }

    fun toggleHideApp(packageName: String) {
        val current = _settingsFlow.value.hiddenPackages.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        prefs.edit().putStringSet(KEY_HIDDEN_APPS, current).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hiddenPackages = current)
    }

    fun unhideApp(packageName: String) {
        val current = _settingsFlow.value.hiddenPackages.toMutableSet()
        current.remove(packageName)
        prefs.edit().putStringSet(KEY_HIDDEN_APPS, current).apply()
        _settingsFlow.value = _settingsFlow.value.copy(hiddenPackages = current)
    }

    fun updateWeather(city: String, temp: Int, condition: String) {
        prefs.edit()
            .putString(KEY_WEATHER_CITY, city)
            .putInt(KEY_WEATHER_TEMP, temp)
            .putString(KEY_WEATHER_COND, condition)
            .apply()
        _settingsFlow.value = _settingsFlow.value.copy(
            weatherCity = city,
            weatherTemp = temp,
            weatherCondition = condition
        )
    }

    companion object {
        private const val KEY_THEME_MODE = "mat_theme_mode"
        private const val KEY_ACCENT = "mat_accent"
        private const val KEY_WALLPAPER = "mat_wallpaper"
        private const val KEY_ICON_SHAPE = "mat_icon_shape"
        private const val KEY_SEARCH_PLACEMENT = "mat_search_placement"
        private const val KEY_ICON_SCALE = "mat_icon_scale"
        private const val KEY_SHOW_LABELS = "mat_show_labels"
        private const val KEY_GRID_COLUMNS = "mat_grid_columns"
        private const val KEY_SHOW_GLANCE = "mat_show_glance"
        private const val KEY_PINNED_APPS = "mat_pinned_apps"
        private const val KEY_HIDDEN_APPS = "mat_hidden_apps"
        private const val KEY_DOCK_APPS = "mat_dock_apps"
        private const val KEY_WEATHER_CITY = "mat_weather_city"
        private const val KEY_WEATHER_TEMP = "mat_weather_temp"
        private const val KEY_WEATHER_COND = "mat_weather_cond"
    }
}
