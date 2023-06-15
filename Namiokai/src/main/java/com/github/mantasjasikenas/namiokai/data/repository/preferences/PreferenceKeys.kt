package com.github.mantasjasikenas.namiokai.data.repository.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

object PreferenceKeys {
    val IS_DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("is_dynamic_color_enabled")
    val IS_AMOLED_MODE_ENABLED = booleanPreferencesKey("is_amoled_mode_enabled")
    val IS_DARK_MODE_ENABLED = booleanPreferencesKey("is_dark_mode_enabled")
    val USE_SYSTEM_DEFAULT_THEME = booleanPreferencesKey("use_system_default_theme")
}