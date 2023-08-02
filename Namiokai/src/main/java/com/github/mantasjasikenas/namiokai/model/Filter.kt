package com.github.mantasjasikenas.namiokai.model

data class Filter<T, V>(
    val displayLabel: String,
    val filterName: String,
    val values: List<V>,
    var selectedValue: V? = null,
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