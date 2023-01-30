package com.example.namiokai.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.namiokai.data.repository.preferences.PreferenceKeys
import com.example.namiokai.data.repository.preferences.PreferencesRepository
import com.example.namiokai.data.repository.preferences.rememberPreference
import com.example.namiokai.ui.common.PermissionsHandler
import com.example.namiokai.ui.theme.NamiokaiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            val useSystemTheme by rememberPreference(
                key = PreferenceKeys.USE_SYSTEM_DEFAULT_THEME,
                defaultValue = false
            )
            val darkTheme by rememberPreference(
                key = PreferenceKeys.IS_DARK_MODE_ENABLED,
                defaultValue = true
            )

            val currentTheme = if (useSystemTheme) {
                isSystemInDarkTheme()
            } else {
                darkTheme
            }

            NamiokaiTheme(darkTheme = currentTheme) {
                PermissionsHandler()
                NamiokaiApp()
            }
        }
    }
}


