package com.example.namiokai.data.repository.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val IS_DARK_MODE_ENABLED = booleanPreferencesKey("is_dark_mode_enabled")
    val USE_SYSTEM_DEFAULT_THEME = booleanPreferencesKey("use_system_default_theme")
}