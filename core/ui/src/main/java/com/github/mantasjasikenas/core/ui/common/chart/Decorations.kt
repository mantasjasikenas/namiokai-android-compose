package com.github.mantasjasikenas.core.ui.common.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.data.ExtraStore

@Composable
fun rememberHorizontalLine(
    y: (ExtraStore) -> Double,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    lineThickness: Dp = 2.dp
): HorizontalLine {
    val fill = Fill(color = lineColor.toArgb())
    val line = rememberLineComponent(fill = fill, thickness = lineThickness)

    return remember { HorizontalLine(y = y, line = line) }
}