package com.example.namiokai.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.namiokai.R
import com.example.namiokai.data.repository.debts.UserDebtsHashMap
import com.example.namiokai.model.User
import com.example.namiokai.ui.main.MainViewModel
import com.example.namiokai.ui.screens.common.CardText
import com.example.namiokai.ui.screens.common.CustomSpacer
import com.example.namiokai.ui.screens.common.EmptyView
import com.example.namiokai.ui.screens.debts.DebtsViewModel
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

@Composable
fun DebtsScreen(
    modifier: Modifier = Modifier,
    debtsViewModel: DebtsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    //val mainUiState by mainViewModel.uiState.collectAsState()
    val summaryUiState by debtsViewModel.uiState.collectAsState()


    if (summaryUiState.debts.isEmpty()) {
        EmptyView()
        return
    }

    /*LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 5.dp),
    ) {
        item(span = { GridItemSpan(2) }) { CustomSpacer(height = 15) }
        items(summaryUiState.debts.count()) { index ->
            val user = summaryUiState.debts.keys.elementAtOrNull(index)
            val debts = summaryUiState.debts.values.elementAtOrNull(index)

            if (user != null && debts != null) {
                ExpandableAvatarCard(
                    debtorUser = user,
                    userDebts = debts
                )
            }
        }
    }*/

    FlowRow(
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = 8.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Start,
        crossAxisSpacing = 8.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        summaryUiState.debts.forEach { (user, debts) ->
            ExpandableAvatarCard(
                debtorUser = user,
                userDebts = debts
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableAvatarCard(debtorUser: User, userDebts: UserDebtsHashMap) {

    var expandedState by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier
        .animateContentSize(),
        onClick = { expandedState = !expandedState }) {

        Column(
            modifier = Modifier
                //.fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                AsyncImage(
                    model = debtorUser.photoUrl.ifEmpty { R.drawable.profile },
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { expandedState = !expandedState }
                        /*.border(
                            BorderStroke(0.5.dp, MaterialTheme.colorScheme.surfaceTint),
                            RoundedCornerShape(50)
                        )*/
                        .size(50.dp),
                    //.background(MaterialTheme.colorScheme.surfaceTint),
                    contentScale = ContentScale.FillBounds,
                )
                CustomSpacer(height = 10)
                Text(
                    text = debtorUser.displayName,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            AnimatedVisibility(visible = expandedState) {
                Column {
                    CustomSpacer(height = 10)
                    CustomSpacer(height = 10)
                    CardText(
                        label = stringResource(R.string.debtor),
                        value = debtorUser.displayName
                    )
                    userDebts.forEach { (key, value) ->
                        CardText(label = key.displayName, value = "$value €")
                    }
                }
            }
        }

    }
}

@Composable
private fun UserCard(user: User, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            CardText(label = stringResource(R.string.display_name), value = user.displayName)
            CardText(label = stringResource(R.string.email), value = user.email)
            Text(
                text = stringResource(R.string.photo),
                style = MaterialTheme.typography.labelMedium
            )
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
            )
            CustomSpacer(height = 10)
            CardText(label = stringResource(R.string.uid), value = user.uid)


        }
    }

}

@Composable
private fun DebtCard(user: User, userDebts: HashMap<User, Double>) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            CardText(label = stringResource(R.string.debtor), value = user.displayName)
            CustomSpacer(height = 10)
            userDebts.forEach { (key, value) ->
                CardText(label = key.displayName, value = "$value €")
            }
        }
    }
}

