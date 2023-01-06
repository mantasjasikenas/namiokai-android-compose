package com.example.namiokai.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.namiokai.model.User
import com.example.namiokai.ui.MainViewModel
import com.example.namiokai.ui.screens.common.CardText
import com.example.namiokai.ui.screens.common.CustomSpacer
import com.example.namiokai.ui.screens.summary.SummaryViewModel

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    summaryViewModel: SummaryViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    val summaryUiState by summaryViewModel.uiState.collectAsState()


    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(mainUiState.users) { user ->
            UserCard(user)
        }
        item {
            CustomSpacer(height = 40)
        }
        items(summaryUiState.reducedDebts.count()) { index ->
            val user = summaryUiState.reducedDebts.keys.elementAtOrNull(index)
            val debts = summaryUiState.reducedDebts.values.elementAtOrNull(index)

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

            CardText(label = "Display name", value = user.displayName)
            CardText(label = "Email", value = user.email)
            Text(text = "Photo", style = MaterialTheme.typography.labelMedium)
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
            )
            CustomSpacer(height = 10)
            CardText(label = "Uid", value = user.uid)


        }
    }

}

@Composable
private fun DebtCard(user: User, userDebts: HashMap<User, Double>) {
    ElevatedCard(
        modifier = Modifier
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

            CardText(label = "Display name", value = user.displayName + " pays to")
            userDebts.forEach { (key, value) ->
                CardText(label = key.displayName, value = value.toString())
            }
            CustomSpacer(height = 10)

        }
    }
}

@Composable
private fun SizedIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    size: Dp = 35.dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(size)
    )
}
