package com.github.mantasjasikenas.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FancyIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .padding(5.dp)
            .fillMaxSize()
            .border(
                BorderStroke(
                    2.dp,
                    color
                ),
                MaterialTheme.shapes.small
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FancyIndicatorTabs(
    values: List<T>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
    textForValue: @Composable (T) -> String
) {
    PrimaryTabRow(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small),
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        divider = {},
    ) {
        values.forEachIndexed { index, value ->
            Tab(
                selected = selectedIndex == index,
                onClick = {
                    onValueChange(index)
                },
                text = {
                    Text(
                        text = textForValue(value),
                    )
                },
            )
        }
    }
}