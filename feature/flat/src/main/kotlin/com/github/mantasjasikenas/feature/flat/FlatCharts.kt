package com.github.mantasjasikenas.feature.flat

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.common.chart.GenericChart
import com.github.mantasjasikenas.core.ui.common.chart.rememberHorizontalLine
import com.github.mantasjasikenas.core.ui.common.chart.rememberIndicatorMarker
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.datetime.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun FlatBillsChart(
    modifier: Modifier, chartModelProducer: CartesianChartModelProducer, flatBills: List<FlatBill>
) {
    val context = LocalContext.current
    val indicatorMarker = rememberSmallIndicatorMarker()
    val avgLineColor = avgLineColor()

    GenericChart(
        modifier = modifier,
        chartModelProducer = chartModelProducer,
        items = flatBills,
        xAxisValueFormatter = { x -> formatFlatBillXAxisValue(flatBills[x.toInt()].date) },
        yAxisValueFormatter = { y -> "${y.toInt()}€" },
        fieldsProvider = { selectedBill ->
            listOf(
                Triple(
                    context.getString(R.string.date),
                    selectedBill?.date?.split("T")?.firstOrNull(),
                    null
                ),
                Triple(context.getString(R.string.taxes), selectedBill?.taxesTotal?.format(2), "€"),
                Triple(context.getString(R.string.total), selectedBill?.total?.format(2), "€"),
                Triple(
                    context.getString(R.string.total_split),
                    selectedBill?.splitPricePerUser()?.format(2),
                    "€"
                )
            )
        },
        legendItems = { colors ->
            listOf(
                (stringResource(R.string.total_split) to colors.first()),
                (stringResource(R.string.avg_split) to avgLineColor)
            )
        },
        decorations = listOf(
            rememberHorizontalLine(
                lineColor = avgLineColor,
                lineThickness = 1.dp,
                y = {
                    it[FlatViewModel.FLAT_EXTRA_STORE].avgTotal
                },
            ),
        ),
        persistentMarkers = { extraStore ->
            listOf(
                indicatorMarker to extraStore[FlatViewModel.FLAT_EXTRA_STORE].maxTotalIndex,
                indicatorMarker to extraStore[FlatViewModel.FLAT_EXTRA_STORE].minTotalIndex,
            )
        },
        rangeProvider = rememberCartesianLayerRangeProvider()
    )
}

@Composable
internal fun ElectricityChart(
    modifier: Modifier,
    chartModelProducer: CartesianChartModelProducer,
    electricity: List<BillDifference>
) {
    val context = LocalContext.current
    val indicatorMarker = rememberSmallIndicatorMarker()
    val avgLineColor = avgLineColor()

    GenericChart(
        modifier = modifier,
        chartModelProducer = chartModelProducer,
        items = electricity,
        xAxisValueFormatter = { x ->
            val bill = electricity[x.toInt()]

            bill.firstBillDate.split("T").firstOrNull() ?: "-"
        },
        yAxisValueFormatter = { y -> "${y.toInt()}kWh" },
        fieldsProvider = { selected ->
            listOf(
                Triple(
                    context.getString(R.string.start_date),
                    selected?.firstBillDate?.split("T")?.firstOrNull(),
                    null
                ),
                Triple(
                    context.getString(R.string.end_date),
                    selected?.secondBillDate?.split("T")?.firstOrNull(),
                    null
                ),
                Triple(context.getString(R.string.amount), selected?.difference?.format(2), "kWh"),
            )
        },
        legendItems = { colors ->
            listOf(
                (stringResource(R.string.electricity) to colors.first()),
                (stringResource(R.string.avg) to avgLineColor)
            )
        },
        decorations = listOf(
            rememberHorizontalLine(
                lineColor = avgLineColor,
                lineThickness = 1.dp,
                y = {
                    it[FlatViewModel.ELECTRICITY_EXTRA_STORE].averageDifference
                },
            ),
        ),
        persistentMarkers = { extraStore ->
            listOf(
                indicatorMarker to extraStore[FlatViewModel.ELECTRICITY_EXTRA_STORE].maxDifferenceIndex,
                indicatorMarker to extraStore[FlatViewModel.ELECTRICITY_EXTRA_STORE].minDifferenceIndex,
            )
        },
        rangeProvider = rememberCartesianLayerRangeProvider()
    )
}

@Composable
private fun rememberCartesianLayerRangeProvider() = remember {
    object : CartesianLayerRangeProvider {
        override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
            return minY
        }

        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
            return maxY
        }
    }
}

@Composable
private fun avgLineColor() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

@Composable
private fun rememberSmallIndicatorMarker() = rememberIndicatorMarker(
    sizeFraction = 0.5f,
    indicatorColor = MaterialTheme.colorScheme.outline,
)

private fun formatFlatBillXAxisValue(date: String): String {
    @Suppress("NAME_SHADOWING")
    val date = date.split("T").firstOrNull() ?: return " "
    val values = date.split("-")

    val yearLastTwoDigits = values[0].takeLast(2).toInt()
    val month = values[1].toInt()

    val monthName = Month(month).getDisplayName(
        TextStyle.SHORT, Locale.getDefault()
    )

    return "$monthName'$yearLastTwoDigits"
}