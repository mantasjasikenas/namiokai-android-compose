@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.namiokai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.namiokai.model.Filter
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.model.filter
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days


@Preview(showBackground = true)
@Composable
fun FiltersPreview() {
    val bills by rememberState {
        listOf(
            PurchaseBill(
                total = 100.0,
                paymasterUid = "1",
                date = "2021-01-01T00:00:00"
            ),
            PurchaseBill(
                total = 200.0,
                paymasterUid = "2",
                date = "2021-01-02T00:00:00"
            ),
            PurchaseBill(
                total = 300.0,
                paymasterUid = "3",
                date = "2021-01-03T00:00:00"
            )
        )
    }

    val periods =
        listOf(
            Period(
                start = LocalDate(
                    2021,
                    1,
                    1
                ),
                end = LocalDate(
                    2021,
                    1,
                    31
                )
            ),
            Period(
                start = LocalDate(
                    2021,
                    2,
                    1
                ),
                end = LocalDate(
                    2021,
                    2,
                    28
                )
            ),
            Period(
                start = LocalDate(
                    2021,
                    3,
                    1
                ),
                end = LocalDate(
                    2021,
                    3,
                    31
                )
            ),
        )


    val filters1 = remember {
        listOf<Filter<PurchaseBill, Any>>(
            Filter(
                displayLabel = "Total",
                filterName = "total",
                values = bills.map { it.total },
                predicate = { bill, value -> bill.total == value }
            ),
            Filter(
                displayLabel = "Paymaster",
                filterName = "paymaster",
                values = bills.map { it.paymasterUid },
                predicate = { bill, value -> bill.paymasterUid == value }
            ),
            Filter(
                displayLabel = "Period",
                filterName = "period",
                values = periods,
                predicate = { bill, value ->
                    val period = value as Period
                    val date = bill.date.toLocalDateTime()
                    date.date >= period.start && date.date <= period.end
                }
            )


        )

    }
    var filters by rememberState {
        filters1.toMutableStateList()
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FiltersRow(
            filters = filters,
            onFilterChanged = {
                filters = it.toMutableStateList()
            },
        )

        Column {
            val filteredBills = bills.filter(filters)

            filteredBills.forEach {
                Text(
                    text = "Total: ${it.total}  Paymaster: ${it.paymasterUid}",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }


}


@Composable
fun <T> FiltersRow(
    modifier: Modifier = Modifier,
    filters: List<Filter<T, Any>>,
    onFilterChanged: (List<Filter<T, Any>>) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ResetFilterChip(onReset = {
                filters.forEach { it.selectedValue = null }
                onFilterChanged(filters)
            })
        }
        items(filters) { filter: Filter<T, Any> ->
            FilterItem(
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

    FilterChip(modifier = modifier,
        selected = filter.selectedValue != null,
        onClick = onClickRequest,
        label = {
            val label = if (filter.selectedValue == null) filter.displayLabel else filter.selectedValue.toString()
            Text(text = label)
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


    FilterChip(modifier = modifier,
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
    isSelected: Boolean,
) {
    Box(modifier = Modifier
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
            text = value.toString(),
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

