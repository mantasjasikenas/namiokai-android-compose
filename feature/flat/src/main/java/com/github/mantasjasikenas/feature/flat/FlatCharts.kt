package com.github.mantasjasikenas.feature.flat

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.common.chart.GenericChart
import com.github.mantasjasikenas.core.ui.common.chart.rememberHorizontalLine
import com.github.mantasjasikenas.core.ui.common.chart.rememberIndicatorMarker
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.datetime.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun FlatBillsChart(
    modifier: Modifier,
    chartModelProducer: CartesianChartModelProducer,
    flatBills: List<FlatBill>
) {
    val indicatorMarker = rememberIndicatorMarker(
        sizeFraction = 0.5f,
        indicatorColor = MaterialTheme.colorScheme.outline,
    )

    GenericChart(
        modifier = modifier,
        chartModelProducer = chartModelProducer,
        items = flatBills,
        xAxisValueFormatter = { x -> formatFlatBillXAxisValue(flatBills[x.toInt()].date) },
        yAxisValueFormatter = { y -> "${y.toInt()}€" },
        fieldsProvider = { selectedBill ->
            listOf(
                Triple("Date", selectedBill?.date?.split("T")?.firstOrNull(), null),
                Triple("Taxes", selectedBill?.taxesTotal?.format(2), "€"),
                Triple("Total", selectedBill?.total?.format(2), "€"),
                Triple("Total split", selectedBill?.splitPricePerUser()?.format(2), "€")
            )
        },
        legendItems = { colors ->
            listOf(
                ("Total split" to colors.first()),
                ("Avg split" to MaterialTheme.colorScheme.secondary)
            )
        },

        decorations = listOf(
            rememberHorizontalLine(
                lineColor = MaterialTheme.colorScheme.secondary,
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
        }
    )
}

@Composable
internal fun ElectricityChart(
    modifier: Modifier,
    chartModelProducer: CartesianChartModelProducer,
    electricity: List<BillDifference>
) {
    GenericChart(
        modifier = modifier,
        chartModelProducer = chartModelProducer,
        items = electricity,
        xAxisValueFormatter = { x ->
            val bill = electricity[x.toInt()]

            bill.firstBillDate.split("T")
                .firstOrNull() ?: "-"
        },
        yAxisValueFormatter = { y -> "${y.toInt()}kWh" },
        fieldsProvider = { selected ->
            listOf(
                Triple("Start date", selected?.firstBillDate?.split("T")?.firstOrNull(), null),
                Triple("End date", selected?.secondBillDate?.split("T")?.firstOrNull(), null),
                Triple("Amount", selected?.difference?.format(2), "kWh"),
            )
        },
        legendItems = { colors -> listOf("Electricity").zip(colors) },
    )
}

private fun formatFlatBillXAxisValue(date: String): String {
    val date = date.split("T").firstOrNull() ?: return ""
    val values = date.split("-")

    val yearLastTwoDigits = values[0].takeLast(2).toInt()
    val month = values[1].toInt()

    val monthName = Month(month).getDisplayName(
        TextStyle.SHORT, Locale.getDefault()
    )

    return "$monthName'$yearLastTwoDigits"
}