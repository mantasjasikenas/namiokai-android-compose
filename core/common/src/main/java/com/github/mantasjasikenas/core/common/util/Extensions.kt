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

/**
 * Converts a [Map] to a [SnapshotStateMap].
 */
fun <K, V> Map<K, V>.toMutableStateMap(): SnapshotStateMap<K, V> {
    return SnapshotStateMap<K, V>().also { it.putAll(this) }
}

/**
 * Rounds a [Double] to a specified number of decimal places.
 */
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

/**
 * Formats a [Double] to a string with a specified number of decimal places.
 */
fun Double.format(digits: Int): String {
    return String.Companion.format(
        "%#,.${digits}f",
        this
    )
}

/**
 * Try to parse a [LocalDateTime] from an ISO string.
 */
fun LocalDateTime.Companion.tryParse(isoString: String): LocalDateTime? {
    return try {
        parse(isoString)
    } catch (e: Exception) {
        null
    }
}

/**
 * Try to parse a [LocalDateTime] from a string.
 */
fun String.parseLocalDateTime(): LocalDateTime? {
    return LocalDateTime.tryParse(this)
}

/**
 * Formats a [LocalDateTime] to a string.
 */
fun LocalDateTime.format(format: String = Constants.DATE_TIME_DISPLAY_FORMAT): String {
    return DateTimeFormatter.ofPattern(format)
        .format(this.toJavaLocalDateTime())
}

/**
 * Filters a list of items by multiple predicates. Returns a list of items that satisfy all predicates.
 */
fun <T> Iterable<T>.filterAll(vararg predicates: (T) -> Boolean): List<T> {
    return filter { candidate ->
        predicates.all { it(candidate) }
    }
}

/**
 * Filters a collection of items by multiple predicates. Returns a list of items that satisfy all predicates.
 */
fun <T> Iterable<T>.filterAll(predicates: List<(T) -> Boolean>): List<T> {
    return filter { candidate ->
        predicates.all { it(candidate) }
    }
}

/**
 * Filters a collection of items by multiple predicates. Returns a list of items that satisfy all predicates.
 */
fun <T> Collection<T>.filterAll(predicates: MutableCollection<(T) -> Boolean>): List<T> {
    return filter { candidate ->
        predicates.all { it(candidate) }
    }
}

/**
 * Applies a block to an object if a predicate is true. Returns the object.
 */
inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> T): T {
    return if (predicate()) {
        block(this)
    } else {
        this
    }
}

/**
 * Converts a [Color] to a hex string.
 */
fun Color.toHex(): String {
    return String.format(
        "#%06X",
        0xFFFFFF and this.toArgb()
    )
}

/**
 * Converts an integer to a hex string.
 */
fun Int.toHex(): String {
    return String.format(
        "#%06X",
        0xFFFFFF and this
    )
}

/**
 * Converts string to a pair of year and month. Example: "2021-01" -> ("2021", "January")
 */
fun String.toYearMonthPair(): Pair<String, String> {
    val year = this.substring(0, 4)
    val month = Month(this.substring(5, 7).toInt())
        .getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )

    return Pair(year, month)
}

