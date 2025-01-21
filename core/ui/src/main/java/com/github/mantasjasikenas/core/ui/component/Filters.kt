@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.core.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days


@Composable
fun <T> FiltersRow(
    modifier: Modifier = Modifier,
    filters: List<Filter<T, Any>>,
    onFilterChanged: (List<Filter<T, Any>>) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "reset") {
            ResetFilterChip(
                modifier = Modifier
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                    .animateContentSize(),
                onReset = {
                    filters.forEach { it.selectedValue = null }
                    onFilterChanged(filters)
                })
        }
        items(
            items = filters.sortedBy { it.selectedValue == null },
            key = { it.filterName }
        ) { filter: Filter<T, Any> ->
            FilterItem(
                modifier = Modifier
                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                    .animateContentSize(),
                filter = filter,
                onValueSelected = {
                    filter.selectedValue = it
                    onFilterChanged(filters)
                },
                onClear = {
                    filter.selectedValue = null
                    onFilterChanged(filters)
                },
                trailingIcon = Icons.Outlined.ArrowDropDown
            )
        }
    }
}

// TODO Implement this someday :)
@Composable
fun <T> FiltersDialog(
    modifier: Modifier = Modifier,
    filters: List<Filter<T, Any>>,
    onFilterChanged: (List<Filter<T, Any>>) -> Unit = {}
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            Text(text = filter.displayLabel)
            AvailableValueCard(
                onValueSelected = {
//                    onClear()
//                    onDismissRequest()
                },
                value = "All",
                isSelected = filter.selectedValue == null,
            )
            filter.values.forEach { value ->
                AvailableValueCard(
                    onValueSelected = {
                        //val filters = filters.toMutableList()
                        filter.selectedValue = value
                        onFilterChanged(filters)
                    },
                    value = value.toString(),
                    isSelected = filter.selectedValue == value,
                )
            }
            NamiokaiSpacer(height = 20)


        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T, V> FilterItem(
    modifier: Modifier = Modifier,
    filter: Filter<T, V>,
    onValueSelected: (V?) -> Unit,
    onClear: () -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = Icons.Outlined.ArrowDropDown,
) {
    val expandedSheet = remember { mutableStateOf(false) }
    val onClickRequest = { expandedSheet.value = true }
    val onDismissRequest = { expandedSheet.value = false }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val onFilterSelected: (V?) -> Unit = {
        onDismissRequest()
        onValueSelected(it)
    }

    FilterChip(
        modifier = modifier,
        selected = filter.selectedValue != null,
        onClick = onClickRequest,
        label = {
            Text(
                text = if (filter.selectedValue == null) {
                    filter.displayLabel
                } else {
                    filter.displayValue(filter.selectedValue!!)
                }
            )
        },
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        })

    if (expandedSheet.value) {
        NamiokaiBottomSheet(
            onDismiss = onDismissRequest,
            title = filter.displayLabel,
            bottomSheetState = bottomSheetState
        ) {
            AvailableValueCard(
                onValueSelected = {
                    onClear()
                    onDismissRequest()
                },
                value = "All",
                isSelected = filter.selectedValue == null,
            )

            filter.values.forEach { value ->
                AvailableValueCard(
                    onValueSelected = {
                        onFilterSelected(value)
                    },
                    value = value,
                    isSelected = filter.selectedValue == value,
                    displayValue = filter.displayValue
                )
            }
            NamiokaiSpacer(height = 20)
        }
    }
}


@Composable
fun PeriodFilterChip(
    modifier: Modifier = Modifier,
    label: String,
    currentValue: Period?,
    defaultValue: Period?,
    onValueSelected: (Period?) -> Unit,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = Icons.Outlined.ArrowDropDown,
) {
    val datePickerState = remember { mutableStateOf(false) }

    val onClickRequest = { datePickerState.value = true }
    val onDismissRequest = { datePickerState.value = false }

    val onSaveRequest = { sort: Period ->
        onDismissRequest()
        onValueSelected(sort)
    }
    val onResetRequest = {
        onDismissRequest()
        onValueSelected(null)
    }


    FilterChip(
        modifier = modifier,
        selected = currentValue != defaultValue,
        onClick = onClickRequest,
        label = { if (currentValue == defaultValue) Text(label) else Text(currentValue.toString()) },
        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        })

    if (datePickerState.value) {
        NamiokaiDateRangePicker(
            onDismissRequest = onDismissRequest,
            onSaveRequest = onSaveRequest,
            onResetRequest = onResetRequest,
            initialSelectedStartDateMillis = currentValue?.start?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.plus(1.days)
                ?.toEpochMilliseconds(),
            initialSelectedEndDateMillis = currentValue?.end?.atStartOfDayIn(TimeZone.currentSystemDefault())
                ?.plus(1.days)
                ?.toEpochMilliseconds()
        )
    }
}

@Composable
private fun <V> AvailableValueCard(
    onValueSelected: (V) -> Unit,
    value: V,
    displayValue: (V) -> String = { it.toString() },
    isSelected: Boolean,
) {
    Box(
        modifier = Modifier
            .padding(vertical = 2.dp) // 4
            .fillMaxWidth()
            .clickable {
                onValueSelected(value)
            }
            .border(
                width = 1.dp,
                color = DividerDefaults.color,
                shape = MaterialTheme.shapes.small
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .padding(10.dp)) {
        Text(
            text = displayValue(value),
            color = if (isSelected) DividerDefaults.color else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ResetFilterChip(
    modifier: Modifier = Modifier,
    onReset: () -> Unit
) {
    FilterChip(
        modifier = modifier,
        onClick = onReset,
        selected = false,
        label = {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
    )
}