package com.github.mantasjasikenas.core.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.github.mantasjasikenas.core.ui.R
import com.github.mantasjasikenas.core.ui.common.CardText
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer


@Composable
fun NamiokaiElevatedCard(
    modifier: Modifier = Modifier,
    padding: Dp = 15.dp,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun NamiokaiElevatedOutlinedCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .border(
                CardDefaults.outlinedCardBorder(),
                shape = MaterialTheme.shapes.medium
            ),
        onClick = onClick,
    ) {

        Column(
            modifier = Modifier
                .padding(contentPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun NamiokaiOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
fun UserCard(
    user: com.github.mantasjasikenas.core.domain.model.User,
    modifier: Modifier = Modifier
) {
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

            CardText(
                label = stringResource(R.string.display_name),
                value = user.displayName
            )
            CardText(
                label = stringResource(R.string.email),
                value = user.email
            )
            Text(
                text = stringResource(R.string.photo),
                style = MaterialTheme.typography.labelMedium
            )
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
            )
            NamiokaiSpacer(height = 10)
            CardText(
                label = stringResource(R.string.uid),
                value = user.uid
            )


        }
    }

}