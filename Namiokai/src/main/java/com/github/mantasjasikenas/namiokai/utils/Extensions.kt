package com.github.mantasjasikenas.namiokai.utils

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.github.mantasjasikenas.namiokai.model.User
import com.google.firebase.auth.FirebaseUser
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

fun FirebaseUser.toUser(): User = User(
    displayName = displayName ?: "",
    email = email ?: "",
    uid = uid,
    photoUrl = photoUrl?.toString() ?: ""
)

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


