package com.github.mantasjasikenas.namiokai.utils

import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round

fun <K, V> Map<K, V>.toMutableStateMap() = SnapshotStateMap<K, V>().also { it.putAll(this) }

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun Double.format(digits: Int) = String.Companion.format(
    "%#,.${digits}f",
    this
)

fun LocalDateTime.Companion.tryParse(isoString: String) = try {
    parse(isoString)
} catch (e: Exception) {
    null
}

fun String.parseLocalDateTime() = LocalDateTime.tryParse(this)

fun LocalDateTime.format(format: String = Constants.DATE_TIME_DISPLAY_FORMAT): String =
    DateTimeFormatter.ofPattern(format).format(this.toJavaLocalDateTime())


