package com.github.mantasjasikenas.core.data.repository.preferences

import androidx.datastore.preferences.core.Preferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val themePreferences: Flow<ThemePreferences>
    fun <T> getPreference(key: Preferences.Key<T>, default: T): Flow<T>
    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)
    suspend fun updateThemePreferences(themePreferences: ThemePreferences)
}