package com.github.mantasjasikenas.namiokai.ui.screens.debts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.data.repository.debts.UserDebtsMap
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.CardTextColumn
import com.github.mantasjasikenas.namiokai.ui.common.CardTextRow
import com.github.mantasjasikenas.namiokai.ui.common.CustomSpacer
import com.github.mantasjasikenas.namiokai.ui.common.EmptyView
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.utils.format
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment

@Composable
fun DebtsScreen(
    modifier: Modifier = Modifier,
    debtsViewModel: DebtsViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val summaryUiState by debtsViewModel.debtsUiState.collectAsState()
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val vertScrollState = rememberScrollState()
    val usersMap = mainUiState.usersMap

    if (summaryUiState.debts.isEmpty()) {
        EmptyView()
        return
    }

    FlowRow(
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = 8.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Start,
        crossAxisSpacing = 8.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(vertScrollState)
    ) {
        summaryUiState.debts.forEach { (user, debts) ->
            if (debts.isEmpty()) return@forEach

            ExpandableAvatarCard(
                debtorUser = usersMap[user]!!,
                userDebts = debts,
                usersMap = usersMap
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableAvatarCard(debtorUser: User, userDebts: UserDebtsMap, usersMap: UsersMap) {

    var expandedState by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier
        .animateContentSize(),
        onClick = { expandedState = !expandedState }) {

        Column(
            modifier = Modifier
                .padding(20.dp)
                .width(IntrinsicSize.Max),
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
                        .size(40.dp),
                    contentScale = ContentScale.FillBounds,
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
                    CustomSpacer(height = 7)
                    Text(
                        text = "pays to",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    //CustomSpacer(height = 15)
                    Divider(modifier = Modifier.padding(vertical = 7.dp))
                    //CustomSpacer(height = 10)


                    var total = 0.0
                    userDebts.forEach { (key, value) ->
                        CardTextRow(
                            label = usersMap[key]!!.displayName,
                            value = "???${value.format(2)}"
                        )
                        total += value
                    }
                    Divider(modifier = Modifier.padding(vertical = 7.dp))
                    CardTextRow(
                        label = "Total",
                        value = "???${total.format(2)}",
                        modifier = Modifier.align(Alignment.End)
                    )


                }
            }
        }

    }
}



