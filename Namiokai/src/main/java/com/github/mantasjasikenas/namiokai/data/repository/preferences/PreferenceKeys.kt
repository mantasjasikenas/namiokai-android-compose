package com.github.mantasjasikenas.namiokai.data.repository.preferences

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val ACCENT_COLOR = intPreferencesKey("custom_color")
    val THEME_TYPE = stringPreferencesKey("theme_type")
    val THEME = stringPreferencesKey("theme")
}