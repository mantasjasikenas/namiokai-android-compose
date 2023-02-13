package com.github.mantasjasikenas.namiokai.utils

import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlin.math.round

fun <K, V> Map<K, V>.toMutableStateMap() = SnapshotStateMap<K, V>().also { it.putAll(this) }

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Double.format(digits:Int) = String.Companion.format(
    "%#,.${digits}f",
    this
)