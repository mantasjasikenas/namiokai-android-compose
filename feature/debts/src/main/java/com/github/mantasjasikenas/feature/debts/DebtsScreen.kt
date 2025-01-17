@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.feature.debts

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.UnfoldLess
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.common.util.parseLocalDateTime
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.debts.DebtBill
import com.github.mantasjasikenas.core.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.core.ui.common.NamiokaiBottomSheet
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.PagesFlowRow
import com.github.mantasjasikenas.core.ui.common.noRippleClickable
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedCard
import com.github.mantasjasikenas.core.ui.component.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.core.ui.component.NamiokaiOutlinedCard
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import com.github.mantasjasikenas.core.ui.component.SwipePeriod

@Composable
fun DebtsRoute(
    modifier: Modifier = Modifier,
    viewModel: DebtsViewModel = hiltViewModel()
) {
    DebtsScreen(
        debtsViewModel = viewModel
    )
}

@Composable
fun DebtsScreen(
    debtsViewModel: DebtsViewModel = hiltViewModel(),
) {
    val debtsUiState by debtsViewModel.debtsUiState.collectAsStateWithLifecycle()

    when (debtsUiState) {
        is DebtsUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is DebtsUiState.Success -> {
            DebtsScreenContent(
                debtsUiState = debtsUiState as DebtsUiState.Success,
                onPeriodReset = debtsViewModel::onPeriodReset,
                onPeriodUpdate = debtsViewModel::onPeriodUpdate,
            )
        }
    }
}

@Composable
fun DebtsScreenContent(
    debtsUiState: DebtsUiState.Success,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    val usersMap = remember(debtsUiState.users) {
        debtsUiState.users.associateBy { it.uid }
    }

    val pages = listOf(
        "Personal",
        "All"
    )
    var currentPage by rememberState {
        0
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PagesFlowRow(
                pages = pages,
                currentPage = currentPage,
                onPageClick = {
                    currentPage = it
                }
            )
        }

        AnimatedContent(
            targetState = currentPage,
            label = "",
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> -height } + fadeOut())
                } else {
                    (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> height } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            },
        ) {
            when (it) {
                0 -> {
                    PersonalDebtsPage(
                        periodState = debtsUiState.periodState,
                        currentUserDebts = debtsUiState.currentUserDebts,
                        onPeriodReset = onPeriodReset,
                        onPeriodUpdate = onPeriodUpdate,
                        usersMap = usersMap
                    )
                }

                1 -> {
                    DebtsPage(
                        periodState = debtsUiState.periodState,
                        usersDebts = debtsUiState.debts,
                        usersMap = usersMap,
                        onPeriodReset = onPeriodReset,
                        onPeriodUpdate = onPeriodUpdate,
                    )
                }
            }

        }
    }
}

@Composable
private fun PersonalDebtsPage(
    usersMap: UsersMap,
    periodState: PeriodState,
    currentUserDebts: Map<UserUid, List<DebtBill>>?,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            NamiokaiElevatedOutlinedCard {
                Text(
                    text = "Your debts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                SwipePeriod(
                    periods = periodState.periods,
                    selectedPeriod = periodState.userSelectedPeriod,
                    appPeriod = periodState.currentPeriod,
                    onPeriodReset = onPeriodReset,
                    onPeriodUpdate = onPeriodUpdate,
                )
            }

            if (currentUserDebts.isNullOrEmpty()) {
                NoDebtsFound()
            } else {
                PersonalDebts(
                    currentUserDebts = currentUserDebts,
                    usersMap = usersMap
                )
            }
        }
    }
}

@Composable
private fun DebtsPage(
    periodState: PeriodState,
    usersDebts: List<Pair<String, Map<String, List<DebtBill>>>>,
    usersMap: UsersMap,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                20.dp
            )
    ) {
        NamiokaiElevatedOutlinedCard {
            Text(
                text = "Period",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            SwipePeriod(
                periods = periodState.periods,
                selectedPeriod = periodState.userSelectedPeriod,
                appPeriod = periodState.currentPeriod,
                onPeriodReset = onPeriodReset,
                onPeriodUpdate = onPeriodUpdate,
            )
        }

        if (usersDebts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                NoDebtsFound()
            }

            return@Column
        }

        NamiokaiSpacer(height = 20)

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.Start
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = usersDebts,
                key = { it.first }
            ) { (user, debts) ->
                if (debts.isNotEmpty() && usersMap[user] != null) {
                    DebtorCard(
                        modifier = Modifier.animateItem(),
                        debtorUser = usersMap[user]!!,
                        userDebts = debts,
                        usersMap = usersMap
                    )
                } else {
                    Text(
                        text = "IF YOU SEE THIS, SOMETHING WENT WRONG",
                    )
                }
            }
        }

    }
}

@Composable
private fun PersonalDebts(
    currentUserDebts: Map<UserUid, List<DebtBill>>?,
    usersMap: UsersMap,
) {
    if (currentUserDebts == null) {
        return
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val launchSwedbank = {
        val launchIntent: Intent? =
            context.packageManager.getLaunchIntentForPackage("lt.swedbank.mobile")

        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    }

    NamiokaiSpacer(height = 20)

    NamiokaiOutlinedCard {

        currentUserDebts.forEach { (uid, debtBills) ->
            val value = remember(uid, debtBills) {
                debtBills.sumOf { it.amount }
            }

            EuroIconTextRow(
                label = usersMap[uid]!!.displayName,
                value = value.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(value.format(2)))
                    launchSwedbank()
                }
            )
        }

        if ((currentUserDebts.size) > 1) {
            val total = remember(currentUserDebts) {
                currentUserDebts.values.flatten().sumOf { it.amount }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 3.dp),
                thickness = 2.dp
            )
            EuroIconTextRow(
                label = "Total",
                value = total.format(2),
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(total.format(2)))
                }
            )
        }
    }
}

@Composable
fun NoDebtsFound(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NoResultsFound(
            label = "No debts was found.\nYou are all good!",
            modifier = modifier
        )
    }
}

@Composable
private fun DebtorCard(
    modifier: Modifier = Modifier,
    debtorUser: User,
    userDebts: Map<UserUid, List<DebtBill>>,
    usersMap: UsersMap
) {
    val expandedState = remember { mutableStateOf(false) }
    val expandAll = remember { mutableStateOf(false) }

    NamiokaiElevatedCard(
        modifier = Modifier.animateContentSize(),
        onClick = { expandedState.value = !expandedState.value }) {

        Column(
            modifier = modifier
                .padding(0.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(debtorUser.photoUrl.ifEmpty { R.drawable.profile })
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    loading = {
                        CircularProgressIndicator()
                    },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(31.dp)
                )

                NamiokaiSpacer(width = 10)

                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = stringResource(R.string.debtor),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = debtorUser.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

    }

    if (expandedState.value) {
        DebtorBottomSheet(
            debtorUser = debtorUser,
            userDebts = userDebts,
            usersMap = usersMap,
            expandedState = expandedState,
            expandAll = expandAll
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebtorBottomSheet(
    debtorUser: User,
    userDebts: Map<UserUid, List<DebtBill>>,
    usersMap: UsersMap,
    expandedState: MutableState<Boolean>,
    expandAll: MutableState<Boolean>
) {
    NamiokaiBottomSheet(
        title = "Debts details",
        onDismiss = { expandedState.value = false },
        bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column {
            if (userDebts.isEmpty()) {
                Text(
                    text = "No debts",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )

                return@NamiokaiBottomSheet
            }

            DebtsDetailsHeader(
                displayName = debtorUser.displayName,
                onExpandAll = { expandAll.value = true },
                onCollapseAll = { expandAll.value = false }
            )

            userDebts.forEach { (key, debtBills) ->
                ExpandableDebtDetailsRow(
                    expandAll = expandAll,
                    usersMap = usersMap,
                    userUid = key,
                    value = remember(debtBills) {
                        debtBills.sumOf { it.amount }
                    },
                    debtBills = debtBills
                )
            }

            if (userDebts.size > 1) {
                DebtsDetailsFooter(
                    totalDebt = remember(userDebts) {
                        userDebts.values.flatten().sumOf { it.amount }
                    }
                )
            }

            NamiokaiSpacer(height = 8)
        }

    }
}

@Composable
private fun ExpandableDebtDetailsRow(
    expandAll: MutableState<Boolean>,
    usersMap: UsersMap,
    userUid: UserUid,
    value: Double,
    debtBills: List<DebtBill>
) {
    ExpandableSection(
        initialExpandState = expandAll.value,
        header = {
            EuroIconTextRow(
                modifier = Modifier.padding(horizontal = 8.dp),
                label = usersMap[userUid]?.displayName ?: "",
                value = value.format(2),
            )
        }
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                debtBills.forEach { debtBill ->
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
                            imageVector = debtBill.bill.getIcon(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        EuroIconTextRow(
                            label = debtBill.bill.getBillDescription(),
                            value = debtBill.amount.format(2),
                            labelTextStyle = MaterialTheme.typography.labelMedium,
                            valueTextStyle = MaterialTheme.typography.labelMedium,
                            iconSize = 12.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtsDetailsHeader(
    displayName: String,
    onExpandAll: () -> Unit,
    onCollapseAll: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.padding(bottom = 16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.End
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val debtorText = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(displayName)
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(" owes")
                }
            }

            Text(
                modifier = Modifier
                    .weight(1f),
                text = debtorText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )

            Icon(
                imageVector = Icons.Outlined.UnfoldLess,
                contentDescription = null,
                modifier = Modifier.noRippleClickable {
                    onCollapseAll()
                }
            )

            Icon(
                imageVector = Icons.Outlined.UnfoldMore,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onExpandAll()
                }
            )
        }
    }
}

@Composable
private fun DebtsDetailsFooter(
    totalDebt: Double
) {
    OutlinedCard(
        modifier = Modifier.padding(top = 16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        EuroIconTextRow(
            modifier = Modifier.padding(8.dp),
            label = "Total",
            value = totalDebt.format(2),
            labelTextStyle = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun ExpandableSection(
    modifier: Modifier = Modifier,
    initialExpandState: Boolean = false,
    header: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    var isExpanded by remember(initialExpandState) { mutableStateOf(initialExpandState) }

    Column(
        modifier = modifier
            .noRippleClickable {
                isExpanded = !isExpanded
            }
            .fillMaxWidth()
    ) {
        header()

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth(),
            visible = isExpanded
        ) {
            content()
        }
    }
}

private fun Bill.getBillDescription(): String {
    return when (this) {
        is TripBill -> {
            "Trip to $tripDestination"
        }

        is PurchaseBill -> {
            shoppingList
        }

        is FlatBill -> {
            date.parseLocalDateTime()?.format() ?: "Flat bill"
        }

        else -> {
            "Bill"
        }
    }
}

private fun Bill.getIcon(): ImageVector {
    return when (this) {
        is TripBill -> Icons.Outlined.LocalGasStation
        is PurchaseBill -> Icons.Outlined.ShoppingBag
        is FlatBill -> Icons.Outlined.Cottage
        else -> Icons.Outlined.ShoppingCart
    }
}
