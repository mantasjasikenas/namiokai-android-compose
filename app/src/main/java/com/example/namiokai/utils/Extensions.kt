package com.example.namiokai.utils

import androidx.compose.runtime.snapshots.SnapshotStateMap

fun <K, V> Map<K, V>.toMutableStateMap() = SnapshotStateMap<K, V>().also { it.putAll(this) }
