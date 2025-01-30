package com.github.mantasjasikenas.core.data.repository.preferences

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.github.mantasjasikenas.core.domain.model.theme.Theme
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


const val DATASTORE_NAME = "preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferencesRepository {

    private val dataStore = context.dataStore

    override val themePreferences: Flow<ThemePreferences> =
        dataStore.data.map { preferences ->
            ThemePreferences(
                themeType = ThemeType.valueOf(
                    preferences[PreferenceKeys.THEME_TYPE]
                        ?: ThemeType.AUTOMATIC.name
                ),
                theme = Theme.valueOf(preferences[PreferenceKeys.THEME] ?: Theme.DEFAULT.name),
                accentColor = Color(
                    preferences[PreferenceKeys.ACCENT_COLOR] ?: Color.Unspecified.toArgb()
                )
            )
        }

    override suspend fun <T> putPreference(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    override fun <T> getPreference(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: default
        }
    }

    override suspend fun updateThemePreferences(themePreferences: ThemePreferences) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_TYPE] = themePreferences.themeType.name
            preferences[PreferenceKeys.THEME] = themePreferences.theme.name
            preferences[PreferenceKeys.ACCENT_COLOR] = themePreferences.accentColor.toArgb()
        }
    }
}