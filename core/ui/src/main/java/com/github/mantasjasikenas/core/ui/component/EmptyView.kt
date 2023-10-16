package com.github.mantasjasikenas.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.ui.R
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer

@Composable
fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_data_available),
            style = MaterialTheme.typography.headlineSmall
        )
    }

}

@Composable
fun NoResultsFound(
    modifier: Modifier = Modifier,
    label: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size((144).dp),
            tint = MaterialTheme.colorScheme.primary
        )
        NamiokaiSpacer(height = 16)
        Text(
            text = "Whoops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        NamiokaiSpacer(height = 8)
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        NamiokaiSpacer(height = 8)
    }
}