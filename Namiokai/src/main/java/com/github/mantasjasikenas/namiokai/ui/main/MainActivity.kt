package com.github.mantasjasikenas.namiokai.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferenceKeys
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.namiokai.data.repository.preferences.rememberPreference
import com.github.mantasjasikenas.namiokai.ui.common.PermissionsHandler
import com.github.mantasjasikenas.namiokai.ui.theme.NamiokaiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity(), AnalyticsLogger by AnalyticsLoggerImpl() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLifecycleOwner(this)
        //region Worker
        /*PeriodicWorkRequestBuilder<NotificationWorker>(15, java.util.concurrent.TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()
            .let {
                WorkManager.getInstance(applicationContext).enqueue(it)
            }

        val downloadRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()
        val workManager = WorkManager.getInstance(applicationContext)*/
        //endregion

        installSplashScreen().apply {
            //setKeepOnScreenCondition { true }
        }
        setContent {
            //region Notification
            /*val workInfos = workManager
                .getWorkInfosForUniqueWorkLiveData("download")
                .observeAsState()
                .value*/
            //endregion

            val useSystemThemePref by rememberPreference(
                key = PreferenceKeys.USE_SYSTEM_DEFAULT_THEME,
                defaultValue = false
            )
            val darkThemePref by rememberPreference(
                key = PreferenceKeys.IS_DARK_MODE_ENABLED,
                defaultValue = true
            )
            val amoledThemePref by rememberPreference(
                key = PreferenceKeys.IS_AMOLED_MODE_ENABLED,
                defaultValue = false
            )

            val isDarkThemeEnabled = if (useSystemThemePref) {
                isSystemInDarkTheme()
            } else {
                darkThemePref
            }

            val isAmoledThemeEnabled = if (isDarkThemeEnabled) {
                amoledThemePref
            } else {
                false
            }

            NamiokaiTheme(darkTheme = isDarkThemeEnabled, amoledTheme = isAmoledThemeEnabled) {
                PermissionsHandler()
                NamiokaiApp()
            }
        }
    }
}
