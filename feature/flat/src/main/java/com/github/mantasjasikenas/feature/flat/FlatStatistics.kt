package com.github.mantasjasikenas.feature.flat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.common.util.format
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.ui.component.ProgressGraph

@Composable
internal fun FlatStatisticsContainer(
    data: List<FlatBill>,
    title: String,
    subtitle: String,
    xAxisLabels: List<String> = emptyList(),
    selectedValueTitle: (FlatBill) -> String,
    selectedInitial: FlatBill? = data.lastOrNull(),
) {
    var selectedRecord by remember {
        mutableStateOf(selectedInitial)
    }

    ElevatedCardContainer(
        modifier = Modifier,
        title = title,
    ) {
        TextLabelWithDivider(
            data = listOf(
                "Total" to "${(selectedRecord?.total ?: 0.0).format(2)}€",
                "Rent" to "${(selectedRecord?.rentTotal ?: 0.0).format(2)}€",
                "Taxes" to "${(selectedRecord?.taxesTotal ?: 0.0).format(2)}€",
            ),
            horizontalArrangement = Arrangement.Start,
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProgressGraph(
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            data = data.map { it.total }
                .ifEmpty {
                    List(xAxisLabels.size) { 0 }
                },
            xAxisLabels = xAxisLabels,
            onSelectedIndexChange = { index ->
                selectedRecord = data.getOrNull(index)
            },
            selected = data.indexOf(selectedRecord),
            dataUnit = "€"
        )
    }
}


@Composable
internal fun ElevatedCardContainer(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            if (subtitle != null) {
                Text(
                    modifier = Modifier.padding(bottom = 6.dp),
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                )

                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Light
                )
            } else {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }



            content()
        }
    }
}


@Composable
fun <T> TextLabelWithDivider(
    data: List<Pair<String, T>>,
    dividerVisible: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
    ) {
        data.forEachIndexed { index, (label, value) ->
            TextWithLabel(
                label = label,
                text = value.toString(),
                textStyle = textStyle,
                labelStyle = labelStyle,
            )

            val isLast = index == data.lastIndex

            if (!isLast && dividerVisible) {
                VerticalDivider(
                    modifier = Modifier
                        .height(18.dp)
                        .padding(horizontal = 16.dp),
                    thickness = 1.dp,
                )
            }

            if (!isLast && !dividerVisible) {
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun TextWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = FontWeight.Bold,
    ),
    labelStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    val textColor = textStyle.color.takeOrElse {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
        Text(
            text = label,
            style = labelStyle,
        )
        Text(
            text = text,
            style = textStyle,
            color = textColor,
        )
    }
}
