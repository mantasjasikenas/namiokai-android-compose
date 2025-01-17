package com.github.mantasjasikenas.core.common.util

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
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
    DateTimeFormatter.ofPattern(format)
        .format(this.toJavaLocalDateTime())

fun <T> Iterable<T>.filterAll(vararg predicates: (T) -> Boolean): List<T> {
    return filter { candidate ->
        predicates.all { it(candidate) }
    }
}

fun <T> Iterable<T>.filterAll(predicates: List<(T) -> Boolean>): List<T> {
    return filter { candidate ->
        predicates.all { it(candidate) }
    }
}

fun <T> Collection<T>.filterAll(predicates: MutableCollection<(T) -> Boolean>): List<T> {
    return filter { candidate ->
        predicates.all { it(candidate) }
    }
}

inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> T): T =
    if (predicate()) {
        block(this)
    } else {
        this
    }

fun Color.toHex(): String {
    return String.format(
        "#%06X",
        0xFFFFFF and this.toArgb()
    )
}

fun Int.toHex(): String {
    return String.format(
        "#%06X",
        0xFFFFFF and this
    )
}

fun String.toYearMonthPair(): Pair<String, String> {
    val year = this.substring(
        0,
        4
    )
    val month = Month(
        this.substring(
            5,
            7
        )
            .toInt()
    ).getDisplayName(
        TextStyle.FULL,
        Locale.getDefault()
    )

    return Pair(
        year,
        month
    )
}

