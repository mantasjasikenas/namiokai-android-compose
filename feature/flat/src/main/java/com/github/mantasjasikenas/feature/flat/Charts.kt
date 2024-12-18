package com.github.mantasjasikenas.feature.flat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.common.chart.GenericChart
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
        legendItems = listOf("Total", "Taxes"),
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
        legendItems = listOf("Electricity consumption"),
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