@file:Suppress("KotlinConstantConditions")

package com.github.mantasjasikenas.namiokai.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.namiokai.data.repository.preferences.rememberThemePreferences
import com.github.mantasjasikenas.namiokai.ui.common.PermissionsHandler
import com.github.mantasjasikenas.namiokai.ui.theme.NamiokaiTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity(), AnalyticsLogger by AnalyticsLoggerImpl() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var debtsManager: DebtsManager

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE

    val mainViewModel: MainViewModel by viewModels()


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mainViewModel.init()

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }

        installSplashScreen()
        subscribeToTopics()
        checkForAppUpdates()
        registerLifecycleOwner(this)

        setContent {
            val themePreferences by rememberThemePreferences()

            LaunchedEffect(
                key1 = themePreferences
            ) {
                Log.d(
                    TAG,
                    "Theme changed to $themePreferences"
                )
            }


            NamiokaiTheme(
                themePreferences = themePreferences,
            ) {
                PermissionsHandler()
                NamiokaiApp(mainViewModel = mainViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        this,
                        AppUpdateOptions.defaultOptions(updateType),
                        123
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds.",
                Toast.LENGTH_LONG
            )
                .show()

            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    this,
                    AppUpdateOptions.defaultOptions(updateType),
                    123
                )
            }
        }
    }

    private fun subscribeToTopics() {
        Firebase.messaging.subscribeToTopic("namiokai")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(
                    TAG,
                    msg
                )
            }
    }
}
