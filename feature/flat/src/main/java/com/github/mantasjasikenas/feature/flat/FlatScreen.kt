package com.github.mantasjasikenas.feature.flat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Flood
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.common.util.tryParse
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.resolveBillCost
import com.github.mantasjasikenas.core.ui.common.CardTextColumn
import com.github.mantasjasikenas.core.ui.common.DateTimeCardColumn
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.VerticalDivider
import com.github.mantasjasikenas.core.ui.component.NamiokaiConfirmDialog
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import com.github.mantasjasikenas.core.ui.component.ProgressGraph
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun FlatScreen(
    modifier: Modifier = Modifier,
    flatViewModel: FlatViewModel = hiltViewModel(),
    sharedState: SharedState,
    onNavigateToFlatBill: () -> Unit,
) {
    val flatUiState by flatViewModel.flatUiState.collectAsStateWithLifecycle()
    val flatBills = flatUiState.flatBills.reversed()

    if (flatUiState.isLoading()) {
        NamiokaiCircularProgressIndicator()
        return
    }

    val currentUser = sharedState.currentUser
    val usersMap = sharedState.usersMap

    if (flatUiState.flatBills.isEmpty()) {
        NoResultsFound(label = "No flat bills found.")
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 5.dp, bottom = 80.dp, start = 16.dp, end = 16.dp)
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
                )
            }

            item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                FlatStatisticsContainer(
                    data = flatBills,
                    title = "Total rent and taxes",
                    subtitle = "Rent and taxes statistics",
                    xAxisLabels = flatBills
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
                        },
                    selectedValueTitle = { bill ->
                        bill.date.split("T")
                            .firstOrNull() ?: ""
                    }
                )
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
        title = "Latest bill"
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
                    text = flatBill.date.toLocalDateTime().format("yyyy-MM-dd"),
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
        title = "Last two bills comparison",
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
fun FlatBillSummary(
    modifier: Modifier = Modifier,
    bills: List<FlatBill>,
    onSeeAllClick: () -> Unit,
    usersMap: UsersMap,
    currentUser: User,
    flatViewModel: FlatViewModel
) {
    val visibleBills = bills.take(3)

    ElevatedCardContainer(
        modifier = modifier,
        title = "Flat bills",
    ) {
        Column {
            visibleBills.forEach { flatBill ->
                CompactFlatCard(
                    flatBill = flatBill,
                    isAllowedModification = (currentUser.admin || flatBill.createdByUid == currentUser.uid),
                    usersMap = usersMap,
                    viewModel = flatViewModel,
                    currentUser = currentUser
                )
            }
        }

        Text(
            text = "See all",
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .clickable { onSeeAllClick() },
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
private fun CompactFlatCard(
    flatBill: FlatBill,
    isAllowedModification: Boolean,
    usersMap: UsersMap,
    viewModel: FlatViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val dateTime = LocalDateTime.tryParse(flatBill.date) ?: Clock.System.now()
        .toLocalDateTime(
            TimeZone.currentSystemDefault()
        )
    val modifyPopupState = remember {
        mutableStateOf(false)
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var confirmDialog by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(CardDefaults.elevatedShape)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .clickable {
                openBottomSheet = !openBottomSheet
            }
            .padding(
                vertical = 6.dp
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            NamiokaiSpacer(width = 10)
            DateTimeCardColumn(
                day = dateTime.date.dayOfMonth.toString(),
                month = dateTime.month.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                )
            )

            NamiokaiSpacer(width = 20)
            VerticalDivider(modifier = Modifier.height(60.dp))
            NamiokaiSpacer(width = 20)

            Column(modifier = Modifier.weight(1f)) {
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

            NamiokaiSpacer(width = 30)
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = flatBill.resolveBillCost(currentUser),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 18.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Outlined.EuroSymbol,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            NamiokaiSpacer(width = 10)

        }

    }


    if (openBottomSheet) {
        NamiokaiBottomSheet(
            title = stringResource(id = R.string.flat_bill_details),
            onDismiss = { openBottomSheet = false },
            bottomSheetState = bottomSheetState
        ) {
            NamiokaiSpacer(height = 10)
            CardTextColumn(
                label = stringResource(R.string.paid_by),
                value = usersMap[flatBill.paymasterUid]?.displayName ?: "-"
            )
            NamiokaiSpacer(height = 10)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardTextColumn(
                    label = stringResource(R.string.rent_total),
                    value = "€${flatBill.rentTotal.format(2)}"
                )
                NamiokaiSpacer(width = 30)
                CardTextColumn(
                    label = "Taxes",
                    value = "€${flatBill.taxesTotal.format(2)}"
                )
            }
            NamiokaiSpacer(height = 10)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CardTextColumn(
                    label = "Total",
                    value = "€${flatBill.total.format(2)}"
                )
                NamiokaiSpacer(width = 30)
                CardTextColumn(
                    label = stringResource(R.string.price_per_person),
                    value = "€${
                        flatBill.splitPricePerUser()
                            .format(2)
                    }"
                )
            }

            NamiokaiSpacer(height = 10)
            CardTextColumn(
                label = stringResource(R.string.flat_bill_date),
                value = dateTime.format()
            )

            NamiokaiSpacer(height = 10)
            Text(
                text = stringResource(R.string.split_bill_with),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            NamiokaiSpacer(height = 7)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                usersMap.filter { flatBill.splitUsersUid.contains(it.key) }.values.forEach {
                    OutlinedCard(shape = RoundedCornerShape(25)) {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.padding(7.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            NamiokaiSpacer(height = 30)
            AnimatedVisibility(visible = isAllowedModification) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(
                        onClick = { modifyPopupState.value = true }) {
                        Text(text = "Edit")
                    }
                    TextButton(
                        onClick = {
                            confirmDialog = true
                        }) {
                        Text(text = "Delete")
                    }
                }
            }

            if (confirmDialog) {
                NamiokaiConfirmDialog(
                    onConfirm = {
                        scope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                            }
                        viewModel.deleteFlatBill(flatBill)
                        confirmDialog = false
                    },
                    onDismiss = { confirmDialog = false }
                )
            }
        }
    }

    if (modifyPopupState.value) {
        FlatBillPopup(
            initialFlatBill = flatBill.copy(),
            onSaveClick = { viewModel.updateFlatBill(it) },
            onDismiss = { modifyPopupState.value = false },
            usersMap = usersMap
        )
    }
}

@Composable
internal fun FlatStatisticsContainer(
    data: List<FlatBill>,
    title: String,
    subtitle: String,
    xAxisLabels: List<String> = emptyList(),
    selectedValueTitle: (FlatBill) -> String,
    selectedInitial: FlatBill? = data.lastOrNull(),
) {
    var selectedRecord by remember {
        mutableStateOf(selectedInitial)
    }

    ElevatedCardContainer(
        modifier = Modifier,
        title = title,
    ) {
        TextLabelWithDivider(
            data = listOf(
                "Rent" to "${(selectedRecord?.rentTotal ?: 0.0).format(2)}€",
                "Taxes" to "${(selectedRecord?.taxesTotal ?: 0.0).format(2)}€",
                "Total" to "${(selectedRecord?.total ?: 0.0).format(2)}€",
            ),
            horizontalArrangement = Arrangement.Start,
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextLabelWithDivider(
            data = listOf(
                "Date" to (selectedRecord?.date?.split("T")?.firstOrNull() ?: "-"),
            ),
            horizontalArrangement = Arrangement.Start,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProgressGraph(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            data = data.map { it.total }
                .ifEmpty {
                    List(xAxisLabels.size) { 0 }
                },
            xAxisLabels = xAxisLabels,
            onSelectedIndexChange = { index ->
                selectedRecord = data.getOrNull(index)
            },
            selected = data.indexOf(selectedRecord),
            dataUnit = "€"
        )
    }
}


@Composable
internal fun ElevatedCardContainer(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            if (subtitle != null) {
                Text(
                    modifier = Modifier.padding(bottom = 6.dp),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Light
                )
            } else {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }



            content()
        }
    }
}


@Composable
fun <T> TextLabelWithDivider(
    data: List<Pair<String, T>>,
    dividerVisible: Boolean = true,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold
    ),
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelMedium,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
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
                        .padding(horizontal = 16.dp),
                    thickness = 1.dp,
                )
            }

            if (!isLast && !dividerVisible) {
                Spacer(modifier = Modifier.width(16.dp))
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
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
    ),
    labelStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelMedium,
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