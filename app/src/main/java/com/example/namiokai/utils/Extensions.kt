package com.example.namiokai.utils

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier

fun <K, V> Map<K, V>.toMutableStateMap() = SnapshotStateMap<K, V>().also { it.putAll(this) }

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: (Modifier.() -> Modifier)? = null
): Modifier {
    return if (condition) {
        then(ifTrue(Modifier))
    } else if (ifFalse != null) {
        then(ifFalse(Modifier))
    } else {
        this
    }
}