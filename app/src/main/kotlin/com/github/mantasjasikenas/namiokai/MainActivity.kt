@file:Suppress("KotlinConstantConditions")

package com.github.mantasjasikenas.namiokai

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.PermissionsHandler
import com.github.mantasjasikenas.core.ui.theme.NamiokaiTheme
import com.github.mantasjasikenas.namiokai.services.AnalyticsLogger
import com.github.mantasjasikenas.namiokai.services.AnalyticsLoggerImpl
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
import kotlin.time.Duration.Companion.seconds

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity(), AnalyticsLogger by AnalyticsLoggerImpl() {

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE

    private val mainActivityViewModel: MainActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }

        installSplashScreen().setKeepOnScreenCondition {
            mainActivityViewModel.userDataUiState.value is UserDataUiState.Loading ||
                    mainActivityViewModel.sharedUiState.value is SharedUiState.Loading
        }


        subscribeToTopics()
        checkForAppUpdates()
        registerLifecycleOwner(this)

        setContent {
            val userDataUiState by mainActivityViewModel.userDataUiState.collectAsState()

            when (userDataUiState) {
                is UserDataUiState.Loading -> {
                    NamiokaiCircularProgressIndicator()
                }

                is UserDataUiState.Success -> {
                    val userData = (userDataUiState as UserDataUiState.Success).userData

                    NamiokaiTheme(
                        themePreferences = userData.themePreferences,
                    ) {
                        PermissionsHandler()
                        NamiokaiApp(mainActivityViewModel = mainActivityViewModel)
                    }
                }
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
                if (!task.isSuccessful) {
                    Log.d(
                        TAG,
                        "Failed to subscribe to topic: ${task.exception}"
                    )
                }
            }
    }
}
