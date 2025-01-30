package com.github.mantasjasikenas.core.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.github.mantasjasikenas.core.data.repository.preferences.PreferenceKeys
import com.github.mantasjasikenas.core.data.repository.preferences.dataStore
import com.github.mantasjasikenas.core.domain.model.theme.Theme
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemeType
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    defaultValue: T,
): MutableState<T> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = remember {
        context.dataStore.data
            .map {
                it[key] ?: defaultValue
            }
    }.collectAsState(initial = defaultValue)

    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Composable
fun rememberThemePreferences(): MutableState<ThemePreferences> {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = remember {
        context.dataStore.data
            .map { preferences ->
                ThemePreferences(
                    themeType = ThemeType.valueOf(
                        preferences[PreferenceKeys.THEME_TYPE]
                            ?: ThemeType.AUTOMATIC.name
                    ),
                    theme = Theme.valueOf(
                        preferences[PreferenceKeys.THEME] ?: Theme.DEFAULT.name
                    ),
                    accentColor = Color(
                        preferences[PreferenceKeys.ACCENT_COLOR] ?: Color.Unspecified.toArgb()
                    )
                )
            }
    }.collectAsState(initial = ThemePreferences())

    return remember {
        object : MutableState<ThemePreferences> {
            override var value: ThemePreferences
                get() = state.value
                set(value) {
                    coroutineScope.launch {
                        context.dataStore.edit {
                            it[PreferenceKeys.THEME_TYPE] = value.themeType.name
                            it[PreferenceKeys.THEME] = value.theme.name
                            it[PreferenceKeys.ACCENT_COLOR] = value.accentColor.toArgb()
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (ThemePreferences) -> Unit =
                { value = it }
        }
    }
}