package com.github.mantasjasikenas.namiokai.services

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

private const val TAG = "AnalyticsLogger"

interface AnalyticsLogger {
    fun registerLifecycleOwner(owner: LifecycleOwner)
}

class AnalyticsLoggerImpl : AnalyticsLogger, LifecycleEventObserver {
    override fun registerLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> Log.d(TAG, "ON_RESUME event")
            Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "ON_PAUSE event")
            Lifecycle.Event.ON_CREATE -> Log.d(TAG, "ON_CREATE event")
            Lifecycle.Event.ON_START -> Log.d(TAG, "ON_START event")
            Lifecycle.Event.ON_STOP -> Log.d(TAG, "ON_STOP event")
            Lifecycle.Event.ON_DESTROY -> Log.d(TAG, "ON_DESTROY event")
            else -> Unit
        }
    }
}