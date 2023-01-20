package com.example.namiokai.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.namiokai.data.repository.preferences.PreferenceKeys
import com.example.namiokai.data.repository.preferences.PreferencesRepository
import com.example.namiokai.data.repository.preferences.rememberPreference
import com.example.namiokai.ui.screens.NamiokaiApp
import com.example.namiokai.ui.screens.common.PermissionsHandler
import com.example.namiokai.ui.theme.NamiokaiTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            //setKeepOnScreenCondition { true }
        }
        setContent {
//            val themeState = remember {
//                preferencesRepository.getPreference(PreferenceKeys.IS_DARK_MODE_ENABLED, true)
//                    .map { it }
//            }.collectAsState(initial = true)

            val state by rememberPreference(key = PreferenceKeys.IS_DARK_MODE_ENABLED, defaultValue = true)

            NamiokaiTheme(darkTheme = state) {
                PermissionsHandler()
                NamiokaiApp()
            }
        }
    }
}


