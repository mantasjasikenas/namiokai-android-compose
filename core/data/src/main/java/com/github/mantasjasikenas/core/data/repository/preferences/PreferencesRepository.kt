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


private const val TAG = "UserPreferencesRepo"
const val DATASTORE_NAME = "preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)


class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    suspend fun <T> putPreference(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun <T> getPreference(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: default
        }
    }

    suspend fun updateThemePreferences(themePreferences: ThemePreferences) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_TYPE] = themePreferences.themeType.name
            preferences[PreferenceKeys.THEME] = themePreferences.theme.name
            preferences[PreferenceKeys.ACCENT_COLOR] = themePreferences.accentColor.toArgb()
        }
    }

    val themePreferences: Flow<ThemePreferences> =
        dataStore.data.map { preferences ->
            ThemePreferences(
                themeType = ThemeType.valueOf(
                    preferences[PreferenceKeys.THEME_TYPE]
                        ?: ThemeType.AUTOMATIC.name
                ),
                theme = Theme.valueOf(
                    preferences[PreferenceKeys.THEME] ?: Theme.DEFAULT.name
                ),
                accentColor = Color(preferences[PreferenceKeys.ACCENT_COLOR] ?: Color.Unspecified.toArgb())
            )
        }

}

//@Composable
//fun <T> rememberPreference(
//    key: Preferences.Key<T>,
//    defaultValue: T,
//): MutableState<T> {
//    val coroutineScope = rememberCoroutineScope()
//    val context = LocalContext.current
//    val state = remember {
//        context.dataStore.data
//            .map {
//                it[key] ?: defaultValue
//            }
//    }.collectAsState(initial = defaultValue)
//
//    return remember {
//        object : MutableState<T> {
//            override var value: T
//                get() = state.value
//                set(value) {
//                    coroutineScope.launch {
//                        context.dataStore.edit {
//                            it[key] = value
//                        }
//                    }
//                }
//
//            override fun component1() = value
//            override fun component2(): (T) -> Unit = { value = it }
//        }
//    }
//}
//
//@Composable
//fun rememberThemePreferences(): MutableState<ThemePreferences> {
//    val coroutineScope = rememberCoroutineScope()
//    val context = LocalContext.current
//    val state = remember {
//        context.dataStore.data
//            .map { preferences ->
//                ThemePreferences(
//                    themeType = ThemeType.valueOf(
//                        preferences[PreferenceKeys.THEME_TYPE]
//                            ?: ThemeType.AUTOMATIC.name
//                    ),
//                    theme = Theme.valueOf(
//                        preferences[PreferenceKeys.THEME] ?: Theme.DEFAULT.name
//                    ),
//                    accentColor = Color(preferences[PreferenceKeys.ACCENT_COLOR] ?: Color.Unspecified.toArgb())
//                )
//            }
//    }.collectAsState(initial = ThemePreferences())
//
//    return remember {
//        object : MutableState<ThemePreferences> {
//            override var value: ThemePreferences
//                get() = state.value
//                set(value) {
//                    coroutineScope.launch {
//                        context.dataStore.edit {
//                            it[PreferenceKeys.THEME_TYPE] = value.themeType.name
//                            it[PreferenceKeys.THEME] = value.theme.name
//                            it[PreferenceKeys.ACCENT_COLOR] = value.accentColor.toArgb()
//                        }
//                    }
//                }
//
//            override fun component1() = value
//            override fun component2(): (ThemePreferences) -> Unit =
//                { value = it }
//        }
//    }
//}





