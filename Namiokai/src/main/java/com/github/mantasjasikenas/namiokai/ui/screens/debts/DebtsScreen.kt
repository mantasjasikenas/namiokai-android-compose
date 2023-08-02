package com.github.mantasjasikenas.namiokai.ui.screens.debts

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserDebtsMap
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.CardText
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiDialog
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiElevatedCard
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiElevatedOutlinedCard
import com.github.mantasjasikenas.namiokai.ui.components.SwipePeriod
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.ui.screens.home.NoDebtsFound
import com.github.mantasjasikenas.namiokai.utils.format

@Composable
fun DebtsScreen(
    debtsViewModel: DebtsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val usersMap = mainUiState.usersMap
    val periodUiState by mainViewModel.periodState.collectAsState()
    val usersDebts by debtsViewModel.getDebts(periodUiState.userSelectedPeriod)
        .collectAsState(initial = emptyMap())

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
                periods = mainViewModel.getPeriods(),
                userSelectedPeriod = periodUiState.userSelectedPeriod,
                currentPeriod = periodUiState.currentPeriod,
                onPeriodReset = {
                    mainViewModel.resetPeriodState()
                },
                onPeriodUpdate = {
                    mainViewModel.updateUserSelectedPeriodState(it)
                },
            )
        }

        if (usersDebts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                NoDebtsFound()
            }
            return@Column
        }
        else {
            NamiokaiSpacer(height = 20) // looks good with 8?
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
                if (userDebts.isEmpty()) {
                    Text(
                        text = "No debts",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    return@NamiokaiDialog
                }


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CardText(
                        label = "Debtor",
                        value = debtorUser.displayName
                    )
                    /* CardText(
                         label = "Period",
                         value = "$debtsPeriod"
                     )*/
                }

                //CustomSpacer(height = 8) // 16
                Text(
                    text = "Debts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )

                var total = 0.0
                userDebts.forEach { (key, value) ->
                    total += value
                    EuroIconTextRow(
                        label = usersMap[key]!!.displayName,
                        value = value.format(2)
                    )
                }

                if (userDebts.size > 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 3.dp),
                        thickness = 2.dp,
                    )
                    EuroIconTextRow(
                        label = "Total",
                        value = total.format(2),
                    )
                }
                NamiokaiSpacer(height = 8)
            }
        }
    }
}



