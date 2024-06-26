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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Flood
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.CardDefaults
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
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun FlatScreen(
    modifier: Modifier = Modifier,
    flatViewModel: FlatViewModel = hiltViewModel(),
    sharedState: SharedState,
    onNavigateToFlatBill: () -> Unit,
) {
    val flatUiState by flatViewModel.flatUiState.collectAsStateWithLifecycle()

    if (flatUiState.isLoading()) {
        NamiokaiCircularProgressIndicator()
        return
    }

    val currentUser = sharedState.currentUser
    val usersMap = sharedState.usersMap

    val scrollState = rememberScrollState()

    if (flatUiState.flatBills.isEmpty()) {
        NoResultsFound(label = "No flat bills found.")
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FlatBillSummary(
                bills = flatUiState.flatBills,
                usersMap = usersMap,
                currentUser = currentUser,
                flatViewModel = flatViewModel,
                onSeeAllClick = {
                    onNavigateToFlatBill()
                },
            )

            FlatStatisticsContainer(
                data = flatUiState.flatBills.reversed(), // TODO remove reversed
                title = "Total rent and taxes",
                subtitle = "Rent and taxes statistics",
                xAxisLabels = flatUiState.flatBills.mapNotNull {
                    it.date.split("T")
                        .firstOrNull()
                }
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
                    .reversed(),
                selectedValueTitle = { bill ->
                    bill.date.split("T")
                        .firstOrNull() ?: ""
                }
            )

            MonthlyComparisonCard(
                bills = flatUiState.flatBills,
                usersMap = usersMap,
                currentUser = currentUser,
                flatViewModel = flatViewModel
            )

            Spacer(modifier = Modifier.height(6.dp))
        }
    }


}

@Composable
fun MonthlyComparisonCard(
    modifier: Modifier = Modifier,
    bills: List<FlatBill>,
    usersMap: UsersMap,
    currentUser: User,
    flatViewModel: FlatViewModel,
) {
    ElevatedCardContainer(
        modifier = modifier,
        title = "Monthly comparison",
    ) {

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