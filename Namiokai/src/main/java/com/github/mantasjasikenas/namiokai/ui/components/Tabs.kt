package com.github.mantasjasikenas.namiokai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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

@Composable
fun FancyIndicatorTabs(
    values: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
) {

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        FancyIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }

    Column {
        NamiokaiElevatedCard(padding = 0.dp) {
            TabRow(
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                selectedTabIndex = selectedIndex,
                indicator = indicator,
                divider = {},
            ) {
                values.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = {
                            onValueChange(index)
                        },
                        text = { Text(title) },
                    )
                }
            }
        }
    }
}