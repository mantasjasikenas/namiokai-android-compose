package com.example.namiokai.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.namiokai.ui.screens.summary.SummaryUiState
import com.example.namiokai.ui.theme.NamiokaiTheme

@Composable
fun SummaryScreen(
    summaryUiState: SummaryUiState, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(summaryUiState.users) { user ->
            UserCard(user)
        }
    }
}

@Composable
private fun UserCard(user: String, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SizedIcon(
                imageVector = Icons.Outlined.Person2
            )
            SizedIcon(
                imageVector = Icons.Outlined.Wallet
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                SizedIcon(
                    imageVector = Icons.Outlined.Person
                )
                SizedIcon(
                    imageVector = Icons.Outlined.Person
                )
            }
        }

    }
}

@Composable
private fun SizedIcon(modifier: Modifier = Modifier, imageVector: ImageVector, size: Dp = 35.dp) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(size)
    )
}


@Preview
@Composable
fun CardPreview() {
    NamiokaiTheme {
        UserCard(user = "Lolikas")
    }
}