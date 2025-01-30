package com.github.mantasjasikenas.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.ui.common.NamiokaiDateRangePicker
import com.github.mantasjasikenas.core.ui.common.rememberState
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days

@Composable
fun SwipePeriod(
    periods: List<Period>,
    selectedPeriod: Period,
    appPeriod: Period,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    ),
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = periods.indexOf(selectedPeriod),
        pageCount = {
            periods.size
        })
    var datePickerState by rememberState {
        false
    }
    val onPeriodClick = {
        datePickerState = true
    }

    if (!periods.contains(selectedPeriod)) {
        Text(text = "$selectedPeriod",
            style = textStyle,
            modifier = modifier.clickable {
                onPeriodClick()
            }
        )
    } else {
        PeriodsHorizontalPager(
            periods = periods,
            pagerState = pagerState,
            onPeriodClick = onPeriodClick,
            onPeriodUpdate = onPeriodUpdate,
            selectedPeriod = selectedPeriod,
        )
    }

    if (datePickerState) {
        NamiokaiDateRangePicker(
            onDismissRequest = { datePickerState = false },
            onSaveRequest = {
                onPeriodUpdate(it)
                datePickerState = false
            },
            onResetRequest = {
                onPeriodReset()
                coroutineScope.launch {
                    val index = periods.indexOf(appPeriod)
                    pagerState.scrollToPage(index)
                }
                datePickerState = false
            },
            initialSelectedStartDateMillis = selectedPeriod.start.atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(1.days)
                .toEpochMilliseconds(),
            initialSelectedEndDateMillis = selectedPeriod.end.atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(1.days)
                .toEpochMilliseconds()
        )
    }
}

@Composable
fun PeriodsHorizontalPager(
    selectedPeriod: Period,
    periods: List<Period>,
    pagerState: PagerState,
    onPeriodClick: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    ),
) {

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onPeriodUpdate(periods[page])
        }
    }

    LaunchedEffect(
        key1 = selectedPeriod,
    ) {
        val index = periods.indexOf(selectedPeriod)

        if (index != pagerState.currentPage && index != -1) {
            pagerState.scrollToPage(index)
        }
    }

    HorizontalPager(
        modifier = Modifier
            .width(180.dp)
            .clickable { onPeriodClick() },
        state = pagerState,
        pageSpacing = 8.dp,
    ) { page ->
        Text(
            text = "${periods[page]}",
            style = textStyle,
        )
    }
}