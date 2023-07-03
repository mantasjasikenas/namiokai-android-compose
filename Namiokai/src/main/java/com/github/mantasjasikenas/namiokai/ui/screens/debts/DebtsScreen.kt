package com.github.mantasjasikenas.namiokai.ui.screens.debts

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserDebtsMap
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.CardText
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiDialog
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiElevatedCard
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.ui.screens.home.NamiokaiDateRangePicker
import com.github.mantasjasikenas.namiokai.utils.format
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DebtsScreen(
    modifier: Modifier = Modifier,
    debtsViewModel: DebtsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val debtsUiState by debtsViewModel.debtsUiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val usersMap = mainUiState.usersMap
    val period by mainViewModel.periodState.collectAsState()
    var openDatePicker by rememberState {
        false
    }
    val usersDebts by debtsViewModel.getDebts(period)
        .collectAsState(initial = emptyMap())

    if (usersDebts.isEmpty()) {
        EmptyView()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                20.dp,
                10.dp,
                20.dp,
                5.dp
            )
    ) {

        CustomSpacer(height = 8)
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                NamiokaiOutlinedCard(modifier = Modifier.height(80.dp)) {
                    Text(
                        text = "Period",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$period",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = { openDatePicker = true })
                    )
                }
            }
            items(usersDebts.toList()) { (user, debts) ->
                if (debts.isEmpty() || usersMap[user] == null) return@items

                DebtorCard(
                    debtorUser = usersMap[user]!!,
                    userDebts = debts,
                    usersMap = usersMap
                )
            }
        }
    }

    if (openDatePicker) {
        NamiokaiDateRangePicker(
            onDismissRequest = { openDatePicker = false },
            onSaveRequest = {
                mainViewModel.updatePeriodState(it)
                openDatePicker = false
            },
            onResetRequest = {
                mainViewModel.resetPeriodState()
                openDatePicker = false
            },
            initialSelectedStartDateMillis = period.start.atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(1.days)
                .toEpochMilliseconds(),
            initialSelectedEndDateMillis = period.end.atStartOfDayIn(TimeZone.currentSystemDefault())
                .plus(1.days)
                .toEpochMilliseconds()
        )
    }
}

@Composable
private fun DebtorCard(
    debtorUser: User,
    userDebts: UserDebtsMap,
    usersMap: UsersMap
) {

    var expandedState by remember { mutableStateOf(false) }

    NamiokaiElevatedCard(modifier = Modifier
        .animateContentSize(),
        onClick = { expandedState = !expandedState }) {

        Column(
            modifier = Modifier
                .padding(5.dp),
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


                CustomSpacer(width = 10)
                CardTextColumn(
                    label = stringResource(R.string.debtor),
                    value = debtorUser.displayName,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

    }


    if (expandedState) {
        NamiokaiDialog(
            title = "Debts details",
            buttonsVisible = false,
            onSaveClick = { expandedState = false },
            onDismiss = { expandedState = false })
        {
            Column {
                CustomSpacer(height = 10)
                if (userDebts.isEmpty()) {
                    Text(
                        text = "No debts",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    return@NamiokaiDialog
                }

                CardText(
                    label = "Debtor",
                    value = debtorUser.displayName,
                )
                //CustomSpacer(height = 10)
                Text(
                    text = "Pays to",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                /*Text(
                    text = "Pays to",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    //modifier = Modifier.align(Alignment.Start)
                )*/
                //Divider(modifier = Modifier.padding(vertical = 10.dp))


                var total = 0.0
                userDebts.forEach { (key, value) ->
                    EuroIconTextRow(
                        label = usersMap[key]!!.displayName,
                        value = value.format(2)
                    )
                    total += value
                }

                if (userDebts.size > 1) {
                    Divider(modifier = Modifier.padding(vertical = 7.dp))
                    EuroIconTextRow(
                        label = "",
                        value = total.format(2),
                        //modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}



