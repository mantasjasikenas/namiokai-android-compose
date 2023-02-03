package com.github.mantasjasikenas.namiokai.ui.common

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsHandler() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }

    val permissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    permissionState.launchPermissionRequest()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })

    when {
        permissionState.status.isGranted -> {
            Log.d("PERMISSIONS", "POST_NOTIFICATIONS permission is granted")
        }

        permissionState.status.shouldShowRationale -> {
            Log.d("PERMISSIONS", "POST_NOTIFICATIONS permission is required by this app")
        }

        !permissionState.status.isGranted && !permissionState.status.shouldShowRationale -> {
            Log.d(
                "PERMISSIONS",
                "POST_NOTIFICATIONS permission fully denied. Go to settings to enable"
            )
        }
    }
}