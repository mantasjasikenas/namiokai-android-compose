@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.feature.flat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Flood
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.common.ElevatedCardContainer
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.NamiokaiUiTokens
import com.github.mantasjasikenas.core.ui.common.TextRow
import com.github.mantasjasikenas.core.ui.common.VerticalDivider
import com.github.mantasjasikenas.core.ui.common.bill.BillCard
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import com.github.mantasjasikenas.core.ui.component.ProgressGraph
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.datetime.LocalDateTime
import kotlin.math.absoluteValue

@Composable
fun FlatRoute(
    flatViewModel: FlatViewModel = hiltViewModel(),
    sharedState: SharedState,
    onNavigateToFlatBill: () -> Unit,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    FlatScreen(
        flatViewModel = flatViewModel,
        sharedState = sharedState,
        onNavigateToFlatBill = onNavigateToFlatBill,
        onNavigateToCreateBill = onNavigateToCreateBill
    )
}


@Composable
fun FlatScreen(
    flatViewModel: FlatViewModel = hiltViewModel(),
    sharedState: SharedState,
    onNavigateToFlatBill: () -> Unit,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    val flatUiState by flatViewModel.flatUiState.collectAsStateWithLifecycle()
    val flatBills = flatUiState.flatBills

    if (flatUiState.isLoading) {
        NamiokaiCircularProgressIndicator()
        return
    }

    if (flatUiState.flatBills.isEmpty()) {
        NoResultsFound(label = stringResource(R.string.no_flat_bills_found))
        return
    }

    FlatScreenContent(
        flatUiState = flatUiState,
        flatViewModel = flatViewModel,
        flatBills = flatBills,
        currentUser = sharedState.currentUser,
        usersMap = sharedState.spaceUsers,
        onNavigateToFlatBill = onNavigateToFlatBill,
        onNavigateToCreateBill = onNavigateToCreateBill
    )
}

@Composable
fun FlatScreenContent(
    flatUiState: FlatUiState,
    flatViewModel: FlatViewModel,
    flatBills: List<FlatBill>,
    currentUser: User,
    usersMap: UsersMap,
    onNavigateToFlatBill: () -> Unit,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(NamiokaiUiTokens.ItemSpacing),
        verticalArrangement = Arrangement.spacedBy(NamiokaiUiTokens.ItemSpacing),
        contentPadding = NamiokaiUiTokens.PageContentPaddingWithFab
    ) {
        item {
            LatestTwoBillsComparisonCard(
                modifier = Modifier.fillMaxWidth(),
                bills = flatBills
            )
        }

        item {
            LatestBillCard(
                flatBill = flatBills.last()
            )
        }

        item(span = {
            GridItemSpan(maxLineSpan)
        }) {
            FlatBillSummary(
                bills = flatUiState.flatBills,
                usersMap = usersMap,
                currentUser = currentUser,
                flatViewModel = flatViewModel,
                onSeeAllClick = {
                    onNavigateToFlatBill()
                },
                onNavigateToCreateBill = onNavigateToCreateBill
            )
        }

        if (flatBills.size > 1) {
            item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                FlatBillsChartContainer(
                    flatBills = flatBills,
                    chartModelProducer = flatViewModel.flatBillsChartModelProducer
                )
            }

            if (flatUiState.electricitySummary != null) {
                if (flatUiState.electricitySummary.electricityDifference.size > 1) {
                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        ElectricityChartContainer(
                            electricity = flatUiState.electricitySummary.electricityDifference,
                            chartModelProducer = flatViewModel.electricityChartModelProducer
                        )
                    }
                }

                item(span = {
                    GridItemSpan(maxLineSpan)
                }) {
                    ElectricitySummaryContainer(
                        electricitySummary = flatUiState.electricitySummary,
                    )
                }
            }
        }
    }
}

@Composable
fun LatestBillCard(
    modifier: Modifier = Modifier,
    flatBill: FlatBill
) {
    ElevatedCardContainer(
        modifier = modifier,
        title = stringResource(R.string.latest_bill)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                NamiokaiSpacer(width = 7)

                Text(
                    text = LocalDateTime.parse(flatBill.date).format("yyyy-MM-dd"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            NamiokaiSpacer(height = 10)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                NamiokaiSpacer(width = 7)

                Text(
                    text = "€${flatBill.taxesTotal.format(2)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            NamiokaiSpacer(height = 10)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Flood,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                NamiokaiSpacer(width = 7)

                Text(
                    text = "€${flatBill.rentTotal.format(2)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}

@Composable
fun LatestTwoBillsComparisonCard(
    modifier: Modifier = Modifier,
    bills: List<FlatBill>
) {
    val lastTwoBills = bills.takeLast(2)
    val previous = lastTwoBills.first()
    val latest = lastTwoBills.last()

    val change = latest.total - previous.total
    val changePercentage = change / previous.total * 100

    val changeColor = when {
        change < 0 -> MaterialTheme.colorScheme.primary
        change > 0 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    ElevatedCardContainer(
        modifier = modifier,
        title = stringResource(R.string.last_two_bills_comparison),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start)
            ) {
                Icon(
                    imageVector = if (change > 0) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = changeColor
                )

                Text(
                    text = "€${change.absoluteValue.format(2)}",
                    color = changeColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Text(
                text = "${changePercentage.absoluteValue.format(2)}%",
                style = MaterialTheme.typography.bodyLarge,
                color = changeColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        NamiokaiSpacer(height = 10)
    }
}


@Composable
fun ElectricitySummaryContainer(
    modifier: Modifier = Modifier,
    electricitySummary: ElectricitySummary,
) {
    val fields = listOf(
        stringResource(R.string.average) to electricitySummary.averageDifference,
        stringResource(R.string.minimum) to electricitySummary.minDifference,
        stringResource(R.string.maximum) to electricitySummary.maxDifference
    )

    val expanded = remember { mutableStateOf(false) }

    ElevatedCardContainer(
        modifier = modifier,
        title = stringResource(R.string.electricity_consumption),
    ) {
        Column {
            fields.forEach { (label, value) ->
                TextRow(
                    label = label,
                    value = value.format(2),
                    labelTextStyle = MaterialTheme.typography.labelMedium,
                    valueTextStyle = MaterialTheme.typography.labelMedium,
                    endContent = {
                        Text(
                            text = "kWh",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AnimatedVisibility(visible = expanded.value) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )

                        electricitySummary.electricityDifference.forEach {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    4.dp,
                                    Alignment.Start
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ElectricalServices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                TextRow(
                                    label = it.firstBillDate.split('T')
                                        .first() + " - " + it.secondBillDate.split('T').first(),
                                    value = it.difference.format(2),
                                    labelTextStyle = MaterialTheme.typography.labelMedium,
                                    valueTextStyle = MaterialTheme.typography.labelMedium,
                                    endContent = {
                                        Text(
                                            text = "kWh",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

            }
            Text(
                text = if (expanded.value) stringResource(R.string.show_less) else stringResource(R.string.show_more),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .clickable { expanded.value = !expanded.value },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FlatBillSummary(
    modifier: Modifier = Modifier,
    bills: List<FlatBill>,
    onSeeAllClick: () -> Unit,
    usersMap: UsersMap,
    currentUser: User,
    flatViewModel: FlatViewModel,
    onNavigateToCreateBill: (BillFormArgs) -> Unit
) {
    val visibleBills = remember(bills) {
        bills.takeLast(3).reversed()
    }
    var selectedFlatBill by rememberSaveable {
        mutableStateOf<FlatBill?>(null)
    }

    val onEdit: (flatBill: FlatBill) -> Unit = { flatBill ->
        onNavigateToCreateBill(
            BillFormArgs(
                billId = flatBill.documentId,
                billType = BillType.Flat
            )
        )
    }

    ElevatedCardContainer(
        modifier = modifier,
        title = stringResource(R.string.flat_bills),
    ) {
        Column {
            visibleBills.forEach { flatBill ->
                CompactFlatCard(
                    flatBill = flatBill,
                    isAllowedModification = (currentUser.admin || flatBill.createdByUid == currentUser.uid),
                    usersMap = usersMap,
                    currentUser = currentUser,
                    onEdit = {
                        onEdit(flatBill)
                    },
                    onSelect = {
                        selectedFlatBill = flatBill
                    }
                )
            }
        }

        Text(
            text = stringResource(R.string.see_all),
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .clickable { onSeeAllClick() },
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        selectedFlatBill?.let { flatBill ->
            FlatBillBottomSheet(
                flatBill = flatBill,
                usersMap = usersMap,
                isAllowedModification = (currentUser.admin || flatBill.createdByUid == currentUser.uid),
                onEdit = {
                    onEdit(flatBill)
                },
                onDismiss = {
                    selectedFlatBill = null
                },
                onDelete = {
                    flatViewModel.deleteFlatBill(flatBill)
                    selectedFlatBill = null
                }
            )
        }
    }
}

@Composable
private fun CompactFlatCard(
    modifier: Modifier = Modifier,
    flatBill: FlatBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    currentUser: User,
    onEdit: () -> Unit,
    onSelect: () -> Unit,
) {
    BillCard(
        modifier = modifier.padding(0.dp),
        bill = flatBill,
        currentUserUid = currentUser.uid,
        onClick = {
            onSelect()
        },
        elevatedCardPadding = PaddingValues(0.dp),
        innerPadding = PaddingValues(vertical = 6.dp),
        elevated = false
    ) {
        FlatBillCardContent(flatBill = flatBill)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FlatStatisticsContainer(
    flatBills: List<FlatBill>,
    title: String,
    xAxisLabels: List<String> = emptyList(),
    selectedInitial: FlatBill? = flatBills.lastOrNull(),
) {
    @Suppress("NAME_SHADOWING")
    val xAxisLabels = xAxisLabels.ifEmpty {
        flatBills
            .let { dates ->
                if (dates.size >= 3) {
                    listOf(
                        dates.first(),
                        dates[dates.size / 2],
                        dates.last()
                    )
                } else {
                    dates
                }
            }
            .mapNotNull {
                it.date.split("T")
                    .firstOrNull()
            }
    }

    var selectedRecord by remember {
        mutableStateOf(selectedInitial)
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf(
        stringResource(R.string.total),
        stringResource(R.string.split),
    )
    var data by remember {
        mutableStateOf(
            flatBills.map { it.total }
                .ifEmpty {
                    List(xAxisLabels.size) { 0 }
                },
        )
    }

    val selectedRecordFields = listOf(
        stringResource(R.string.date) to (selectedRecord?.date?.split("T")?.firstOrNull() ?: "-"),
        stringResource(R.string.rent) to "${(selectedRecord?.rentTotal ?: 0.0).format(2)}€",
        stringResource(R.string.taxes) to "${(selectedRecord?.taxesTotal ?: 0.0).format(2)}€",
        stringResource(R.string.total) to "${(selectedRecord?.total ?: 0.0).format(2)}€",
        stringResource(R.string.split) to "${(selectedRecord?.splitPricePerUser() ?: 0.0).format(2)}€",
    )

    ElevatedCardContainer(
        modifier = Modifier,
        title = title,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            selectedRecordFields.forEach { (label, value) ->
                TextWithLabel(
                    label = label,
                    text = value,
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ProgressGraph(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            data = data,
            xAxisLabels = xAxisLabels,
            onSelectedIndexChange = { index ->
                selectedRecord = flatBills.getOrNull(index)
            },
            selected = flatBills.indexOf(selectedRecord),
            dataUnit = "€"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SingleChoiceSegmentedButtonRow {
                options.forEachIndexed { index, value ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            selectedIndex = index
                            data = flatBills.map {
                                when (index) {
                                    0 -> it.total
                                    1 -> it.splitPricePerUser()
                                    else -> 0.0
                                }
                            }
                        },
                        selected = index == selectedIndex
                    ) {
                        Text(text = value)
                    }
                }
            }
        }
    }
}

@Composable
internal fun ElectricityStatisticsContainer(
    electricity: List<BillDifference>,
    title: String,
    xAxisLabels: List<String> = emptyList(),
    selectedInitial: BillDifference? = electricity.lastOrNull(),
) {
    val xLabels = xAxisLabels.ifEmpty {
        electricity
            .let { dates ->
                if (dates.size >= 3) {
                    listOf(
                        dates.first(),
                        dates[dates.size / 2],
                        dates.last()
                    )
                } else {
                    dates
                }
            }
            .mapNotNull {
                it.firstBillDate.split("T")
                    .firstOrNull()
            }
    }

    var selectedRecord by remember {
        mutableStateOf(selectedInitial)
    }

    val data by remember {
        mutableStateOf(
            electricity.map { it.difference }
                .ifEmpty {
                    List(xLabels.size) { 0 }
                },
        )
    }

    val statisticsFields = listOf(
        stringResource(R.string.start_date) to (selectedRecord?.firstBillDate?.split("T")
            ?.firstOrNull() ?: "-"),
        stringResource(R.string.end_date) to (selectedRecord?.secondBillDate?.split("T")
            ?.firstOrNull() ?: "-"),
        stringResource(R.string.amount) to (selectedRecord?.difference ?: 0.0).format(2),
    )

    ElevatedCardContainer(
        modifier = Modifier,
        title = title,
    ) {
        statisticsFields.forEach { (label, value) ->
            TextRow(
                label = label,
                value = value.format(2),
                labelTextStyle = MaterialTheme.typography.labelMedium,
                valueTextStyle = MaterialTheme.typography.labelMedium,
                endContent = if (label == stringResource(R.string.amount)) {
                    {
                        Text(
                            text = "kWh",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    null
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        ProgressGraph(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            data = data,
            xAxisLabels = xLabels,
            onSelectedIndexChange = { index ->
                selectedRecord = electricity.getOrNull(index)
            },
            selected = electricity.indexOf(selectedRecord),
            dataUnit = "kWh"
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FlatBillsChartContainer(
    flatBills: List<FlatBill>,
    chartModelProducer: CartesianChartModelProducer
) {
    if (flatBills.size < 2) {
        return
    }

    ElevatedCardContainer(
        title = stringResource(R.string.flat_bills_chart),
    ) {
        FlatBillsChart(
            modifier = Modifier.fillMaxWidth(),
            flatBills = flatBills,
            chartModelProducer = chartModelProducer
        )
    }
}

@Composable
private fun ElectricityChartContainer(
    electricity: List<BillDifference>,
    chartModelProducer: CartesianChartModelProducer,
) {
    if (electricity.size < 2) {
        return
    }

    ElevatedCardContainer(
        title = stringResource(R.string.electricity_consumption),
    ) {
        ElectricityChart(
            modifier = Modifier.fillMaxWidth(),
            chartModelProducer = chartModelProducer,
            electricity = electricity,
        )
    }
}


@Composable
fun <T> TextLabelWithDivider(
    data: List<Pair<String, T>>,
    dividerVisible: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold
    ),
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
    space: Dp = 16.dp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
    ) {
        data.forEachIndexed { index, (label, value) ->
            TextWithLabel(
                label = label,
                text = value.toString(),
                textStyle = textStyle,
                labelStyle = labelStyle,
            )

            val isLast = index == data.lastIndex

            if (!isLast && dividerVisible) {
                VerticalDivider(
                    modifier = Modifier
                        .height(18.dp)
                        .padding(horizontal = space),
                    thickness = 1.dp,
                )
            }

            if (!isLast && !dividerVisible) {
                Spacer(modifier = Modifier.width(space))
            }
        }
    }
}

@Composable
fun TextWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
    ),
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    val textColor = textStyle.color.takeOrElse {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
        Text(
            text = label,
            style = labelStyle,
        )
        Text(
            text = text,
            style = textStyle,
            color = textColor,
        )
    }
}