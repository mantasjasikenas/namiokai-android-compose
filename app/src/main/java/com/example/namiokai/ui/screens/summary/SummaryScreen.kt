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
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.model.User
import com.example.namiokai.ui.screens.summary.SummaryViewModel

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    viewModel: SummaryViewModel = hiltViewModel(),
) {
    val summaryUiState by viewModel.uiState.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(summaryUiState.users) { user ->
            UserCard(user)
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

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = user.displayName)
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
