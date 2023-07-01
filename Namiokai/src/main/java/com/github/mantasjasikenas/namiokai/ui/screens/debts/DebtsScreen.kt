package com.github.mantasjasikenas.namiokai.ui.screens.debts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.common.EuroIconTextRow
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.utils.format

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
    val usersDebts by debtsViewModel.getDebts(period)
        .collectAsState(initial = emptyMap())

    if (usersDebts.isEmpty()) {
        EmptyView()
        return
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        items(usersDebts.toList()) { (user, debts) ->
            if (debts.isEmpty() || usersMap[user] == null) return@items


            ExpandableAvatarCard(
                debtorUser = usersMap[user]!!,
                userDebts = debts,
                usersMap = usersMap
            )

        }
    }

    /*FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(vertScrollState)
    ) {
        summaryUiState.debts.forEach { (user, debts) ->
            if (debts.isEmpty() || usersMap[user] == null) return@forEach

            ExpandableAvatarCard(
                debtorUser = usersMap[user]!!,
                userDebts = debts,
                usersMap = usersMap
            )
        }
    }*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableAvatarCard(
    debtorUser: User,
    userDebts: UserDebtsMap,
    usersMap: UsersMap
) {

    var expandedState by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier
        .animateContentSize(),
        onClick = { expandedState = !expandedState }) {

        Column(
            modifier = Modifier
                .padding(20.dp),
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

            AnimatedVisibility(visible = expandedState) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CustomSpacer(height = 10)

                    if (userDebts.isEmpty()) {
                        Text(
                            text = "No debts",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        return@AnimatedVisibility
                    }

                    Text(
                        text = "Pays to",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp))


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
                            label = "Total",
                            value = total.format(2),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }


                }
            }
        }

    }
}



