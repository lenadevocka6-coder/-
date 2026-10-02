package com.example.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

enum class AppCategory(val title: String) {
    ALL("Все"),
    FAVORITES("Избранное"),
    RECENT("Недавние"),
    TOOLS("Инструменты"),
    COMMUNICATION("Связь"),
    MEDIA("Медиа"),
    SYSTEM("Система")
}

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Drawable? = null,
    val category: AppCategory = AppCategory.TOOLS,
    val isPinned: Boolean = false,
    val isHidden: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunched: Long = 0L
)

enum class IconShape(val title: String, val cornerPercent: Int) {
    CIRCLE("Круг", 50),
    SQUIRCLE("Сквиркл", 30),
    ROUNDED_SQUARE("Квадрат", 18),
    TEARDROP("Капля", 40),
    PEBBLE("Галька", 35)
}

enum class SearchPlacement(val title: String) {
    DOCK("В доке (стиль Pixel)", ),
    TOP("Вверху экрана"),
    DRAWER_ONLY("Только в меню приложений")
}

enum class ThemeMode(val title: String) {
    SYSTEM("Системная"),
    LIGHT("Светлая (Material White)"),
    DARK_AMOLED("Тёмная (AMOLED Black)"),
    DARK_SURFACE("Тёмная (Material Charcoal)")
}

enum class MaterialAccent(val title: String, val color: Color) {
    PIXEL_BLUE("Pixel Blue", Color(0xFF1A73E8)),
    EMERALD_GREEN("Android Green", Color(0xFF0F9D58)),
    CORAL_ORANGE("Coral", Color(0xFFE37400)),
    GOOGLE_RED("Google Red", Color(0xFFD93025)),
    ROYAL_PURPLE("Material Purple", Color(0xFF8430CE)),
    TEAL_ACCENT("Teal", Color(0xFF00796B))
}

enum class WallpaperPreset(val title: String, val description: String) {
    PIXEL_CLEAN("Pixel Horizon", "Классический чистый градиент в стиле Google Pixel"),
    ANDROID_10_DARK("Android 10 Q Dark", "Глубокая ночная палитра с контрастными акцентами"),
    ANDROID_11_MINIMAL("Android 11 Geometric", "Минималистичные строгие геометрические формы"),
    ANDROID_9_PIE("Android 9 Pie Sunset", "Мягкий закатный градиент Android Pie"),
    AMOLED_PURE("AMOLED Black", "Чистый глубокий черный цвет для максимальной экономии батареи")
}

enum class GestureType(val title: String, val subtitle: String) {
    SWIPE_UP("Свайп вверх", "Проведите снизу вверх по экрану"),
    SWIPE_DOWN("Свайп вниз", "Проведите сверху вниз по экрану"),
    DOUBLE_TAP("Двойное нажатие", "Дважды коснитесь пустого места экрана"),
    LONG_PRESS("Долгое нажатие", "Удерживайте палец на рабочем столе"),
    PINCH_IN("Щипок внутрь", "Сведите два пальца на экране"),
    PINCH_OUT("Щипок наружу", "Разведите два пальца на экране")
}

enum class GestureAction(val title: String, val description: String) {
    OPEN_APP_DRAWER("Меню всех приложений", "Открыть список всех приложений"),
    OPEN_SEARCH("Быстрый поиск приложений", "Мгновенно открыть строку поиска"),
    OPEN_SETTINGS("Настройки лаунчера", "Открыть персонализацию и параметры экрана"),
    EXPAND_NOTIFICATIONS("Шторка уведомлений", "Опустить системную панель уведомлений"),
    LOCK_SCREEN("Блокировка экрана", "Перевести экран в режим сна"),
    SWITCH_WALLPAPER("Сменить обои", "Переключить следующий пресет обоев"),
    NONE("Не задано", "Ничего не делать")
}

data class LauncherSettings(
    val iconShape: IconShape = IconShape.CIRCLE,
    val searchPlacement: SearchPlacement = SearchPlacement.DOCK,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accent: MaterialAccent = MaterialAccent.PIXEL_BLUE,
    val wallpaper: WallpaperPreset = WallpaperPreset.PIXEL_CLEAN,
    val iconScale: Float = 1.0f,
    val showIconLabels: Boolean = true,
    val gridColumns: Int = 4,
    val showAtAGlance: Boolean = true,
    val gestures: Map<GestureType, GestureAction> = mapOf(
        GestureType.SWIPE_UP to GestureAction.OPEN_APP_DRAWER,
        GestureType.SWIPE_DOWN to GestureAction.EXPAND_NOTIFICATIONS,
        GestureType.DOUBLE_TAP to GestureAction.LOCK_SCREEN,
        GestureType.LONG_PRESS to GestureAction.OPEN_SETTINGS,
        GestureType.PINCH_IN to GestureAction.OPEN_SEARCH,
        GestureType.PINCH_OUT to GestureAction.SWITCH_WALLPAPER
    ),
    val pinnedPackages: Set<String> = emptySet(),
    val hiddenPackages: Set<String> = emptySet(),
    val dockPackages: List<String> = emptyList(),
    val weatherCity: String = "Москва",
    val weatherTemp: Int = 21,
    val weatherCondition: String = "Ясно"
)
