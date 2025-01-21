package com.github.mantasjasikenas.core.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun NamiokaiElevatedCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
    val cardModifier = modifier
        .fillMaxSize()
        .animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        )

    if (onClick != null) {
        ElevatedCard(
            modifier = cardModifier,
            onClick = onClick,
            content = cardContent
        )
    } else {
        ElevatedCard(
            modifier = cardModifier,
            content = cardContent
        )
    }
}

@Composable
fun NamiokaiElevatedOutlinedCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .padding(contentPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
    val cardModifier = modifier
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
        )

    if (onClick != null) {
        ElevatedCard(
            modifier = cardModifier,
            onClick = onClick,
            content = cardContent
        )
    } else {
        ElevatedCard(
            modifier = cardModifier,
            content = cardContent
        )
    }
}

@Composable
fun NamiokaiOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .padding(contentPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
    val cardModifier = modifier
        .fillMaxWidth()

    if (onClick != null) {
        OutlinedCard(
            modifier = cardModifier,
            onClick = onClick,
            content = cardContent
        )
    } else {
        OutlinedCard(
            modifier = cardModifier,
            content = cardContent
        )
    }
}