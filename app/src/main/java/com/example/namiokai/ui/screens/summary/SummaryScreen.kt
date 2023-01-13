package com.example.namiokai.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.namiokai.R
import com.example.namiokai.model.User
import com.example.namiokai.ui.main.MainViewModel
import com.example.namiokai.ui.screens.common.CardText
import com.example.namiokai.ui.screens.common.CustomSpacer
import com.example.namiokai.ui.screens.common.EmptyView
import com.example.namiokai.ui.screens.summary.SummaryViewModel

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    summaryViewModel: SummaryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    val summaryUiState by summaryViewModel.uiState.collectAsState()


    /*  Lazy column displays the list of users in a vertical list.
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(mainUiState.users) { user ->
                UserCard(user)
            }
            item {
                CustomSpacer(height = 40)
            }
            items(summaryUiState.debts.count()) { index ->
                val user = summaryUiState.debts.keys.elementAtOrNull(index)
                val debts = summaryUiState.debts.values.elementAtOrNull(index)

                if (user != null || debts != null) {
                    DebtCard(user!!, debts!!)
                }
            }

        }*/

    if (summaryUiState.debts.isEmpty()) {
        EmptyView()
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(20.dp),
    ) {
        items(summaryUiState.debts.count()) { index ->
            val user = summaryUiState.debts.keys.elementAtOrNull(index)
            val debts = summaryUiState.debts.values.elementAtOrNull(index)

            if (user != null || debts != null) {
                DebtCard(user!!, debts!!)
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
            Text(text = stringResource(R.string.photo), style = MaterialTheme.typography.labelMedium)
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

