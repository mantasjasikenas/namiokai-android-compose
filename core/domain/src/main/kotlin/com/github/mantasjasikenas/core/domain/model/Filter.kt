package com.github.mantasjasikenas.core.domain.model

import androidx.annotation.StringRes

data class Filter<T, V>(
    @StringRes val displayLabelResId: Int,
    val filterName: String,
    val values: List<V>,
    var selectedValue: V? = null,
    val displayValue: (V) -> String = { it.toString() },
    val predicate: ((T, V) -> Boolean)? = null,
)

fun <T, V> List<T>.filter(filters: List<Filter<T, V>>): List<T> {
    return this.filter { item ->
        filters.all {
            it.selectedValue == null || it.predicate?.invoke(
                item,
                it.selectedValue!!
            ) == true
        }
    }
}