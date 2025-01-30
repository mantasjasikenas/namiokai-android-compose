package com.github.mantasjasikenas.core.domain.model.space

import androidx.annotation.StringRes
import com.github.mantasjasikenas.core.domain.R
import kotlinx.datetime.DateTimeUnit
import kotlinx.serialization.Serializable

@Serializable
enum class RecurrenceUnit(@StringRes val titleResId: Int, val unit: DateTimeUnit) {
    DAILY(R.string.daily, DateTimeUnit.DAY),
    WEEKLY(R.string.weekly, DateTimeUnit.WEEK),
    MONTHLY(R.string.monthly, DateTimeUnit.MONTH),
}

fun RecurrenceUnit.allowedStartValues(): IntRange {
    return when (this) {
        RecurrenceUnit.DAILY -> 1..1
        RecurrenceUnit.WEEKLY -> 1..7
        RecurrenceUnit.MONTHLY -> 1..31
    }
}