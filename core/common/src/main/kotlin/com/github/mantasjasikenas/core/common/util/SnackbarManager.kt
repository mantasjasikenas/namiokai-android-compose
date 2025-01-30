package com.github.mantasjasikenas.core.common.util

import androidx.annotation.StringRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


object SnackbarController {
    private val _events = Channel<BaseSnackbarEvent>()
    val events = _events.receiveAsFlow()

    private suspend fun sendEvent(event: BaseSnackbarEvent) {
        _events.send(event)
    }

    @Suppress("unused")
    suspend fun sendEvent(message: String, action: SnackbarAction? = null) {
        sendEvent(MessageSnackbarEvent(message = message, action = action))
    }

    suspend fun sendEvent(@StringRes resId: Int, action: SnackbarAction? = null) {
        sendEvent(ResourceSnackbarEvent(resId = resId, action = action))
    }
}

open class BaseSnackbarEvent(
    open val action: SnackbarAction? = null
)

data class MessageSnackbarEvent(
    val message: String,
    override val action: SnackbarAction? = null
) : BaseSnackbarEvent(action)

data class ResourceSnackbarEvent(
    @StringRes val resId: Int,
    override val action: SnackbarAction? = null
) : BaseSnackbarEvent(action)

data class SnackbarAction(
    val name: String,
    val action: suspend () -> Unit
)