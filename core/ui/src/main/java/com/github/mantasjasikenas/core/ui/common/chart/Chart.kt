package com.github.mantasjasikenas.core.ui.common.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mantasjasikenas.core.ui.common.TextRow
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.decoration.Decoration
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarkerVisibilityListener
import com.patrykandpatrick.vico.core.common.Legend
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun <T> GenericChart(
    modifier: Modifier,
    chartModelProducer: CartesianChartModelProducer,
    items: List<T>,
    xAxisValueFormatter: (x: Double) -> String,
    yAxisValueFormatter: (y: Double) -> String,
    fieldsProvider: (T?) -> List<Triple<String, String?, String?>>,
    legendItems: @Composable ((List<Color>) -> List<Pair<String, Color>>)? = null,
    lineColors: List<Color>? = null,
    decorations: List<Decoration> = emptyList(),
    persistentMarkers: (ExtraStore) -> List<Pair<CartesianMarker?, Double>> = { emptyList() }
) {
    val selectedXIndex = remember { mutableStateOf<Double?>((items.size - 1).toDouble()) }
    val selectedItem = remember(selectedXIndex.value) {
        items.getOrNull((selectedXIndex.value ?: -1).toInt())
    }

    val fields = remember(selectedItem) {
        fieldsProvider(selectedItem)
    }

    fields.forEach { (label, value, endContent) ->
        TextRow(
            label = label,
            value = value ?: "-",
            labelTextStyle = MaterialTheme.typography.labelMedium,
            valueTextStyle = MaterialTheme.typography.labelMedium,
            endContent = {
                if (value != null && endContent != null) {
                    Text(
                        text = endContent,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    ProvideVicoTheme(
        theme = rememberM3VicoTheme(
            lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        )
    ) {
        val lineColors = lineColors ?: vicoTheme.lineCartesianLayerColors

        ComposeChart(
            modelProducer = chartModelProducer,
            modifier = modifier,
            xAxisValueFormatter = CartesianValueFormatter { _, x, _ ->
                xAxisValueFormatter(x)
            },
            yAxisValueFormatter = CartesianValueFormatter { _, y, _ ->
                yAxisValueFormatter(y)
            },
            legend = if (legendItems == null) {
                null
            } else {
                rememberLegend(
                    labelsWithColors = legendItems(lineColors).mapIndexed { index, legendItem ->
                        legendItem.first to lineColors[index % lineColors.size]
                    })
            },
            onMarkerSelected = { selectedXIndex.value = it },
            selectedMarkerX = selectedXIndex.value,
            lineColors = lineColors,
            decorations = decorations,
            persistentMarkers = persistentMarkers
        )
    }
}

@Composable
fun ComposeChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    xAxisValueFormatter: CartesianValueFormatter = remember { CartesianValueFormatter.decimal() },
    yAxisValueFormatter: CartesianValueFormatter = remember { CartesianValueFormatter.decimal() },
    legend: Legend<CartesianMeasuringContext, CartesianDrawingContext>? = null,
    onMarkerSelected: (Double?) -> Unit = {},
    selectedMarkerX: Double? = null,
    marker: CartesianMarker = rememberMarker(),
    lineColors: List<Color> = vicoTheme.lineCartesianLayerColors,
    decorations: List<Decoration> = emptyList(),
    persistentMarkers: (ExtraStore) -> List<Pair<CartesianMarker?, Double>> = { emptyList() }
) {
    CartesianChartHost(
        modifier = modifier,
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(zoomEnabled = false),
        scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    lines = lineColors.map { color ->
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(
                                fill(
                                    color = color
                                )
                            ),
                            pointConnector = remember {
                                LineCartesianLayer.PointConnector.cubic(
                                    curvature = 0f
                                )
                            },
                        )
                    }),
                pointSpacing = 16.dp
            ),
            startAxis = VerticalAxis.rememberStart(
                guideline = null,
                itemPlacer = remember { VerticalAxis.ItemPlacer.count({ 3 }) },
                valueFormatter = yAxisValueFormatter,
                label = rememberAxisLabelComponent(textSize = 10.sp),
            ),
            topAxis = HorizontalAxis.rememberTop(
                guideline = rememberAxisLineComponent(), label = null, tick = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = xAxisValueFormatter,
                itemPlacer = remember {
                    HorizontalAxis.ItemPlacer.aligned(
                        spacing = { 4 }, addExtremeLabelPadding = false
                    )
                },
                label = rememberAxisLabelComponent(
                    textSize = 10.sp
                ),
                guideline = null
            ),
            marker = marker,
            decorations = decorations,
            legend = legend,
            markerVisibilityListener = object : CartesianMarkerVisibilityListener {
                override fun onShown(
                    marker: CartesianMarker,
                    targets: List<CartesianMarker.Target>
                ) {
                    super.onShown(marker, targets)

                    val targetX = targets.firstOrNull()?.x
                    onMarkerSelected(targetX)
                }

                override fun onUpdated(
                    marker: CartesianMarker,
                    targets: List<CartesianMarker.Target>
                ) {
                    super.onUpdated(marker, targets)

                    val targetX = targets.firstOrNull()?.x
                    onMarkerSelected(targetX)
                }
            },
            persistentMarkers = rememberExtraLambda(
                marker,
                selectedMarkerX,
                persistentMarkers
            ) {
                persistentMarkers(it).forEach { (localMarker, x) ->
                    if (selectedMarkerX == x) {
                        return@forEach
                    }

                    if (localMarker != null) {
                        localMarker at x
                    } else {
                        marker at x
                    }
                }

                if (selectedMarkerX != null) {
                    marker at selectedMarkerX
                }
            },
        ),
    )
}

@Composable
private fun rememberLegend(
    labelsWithColors: List<Pair<String, Color>>
): Legend<CartesianMeasuringContext, CartesianDrawingContext> {
    val labelComponent = rememberTextComponent(vicoTheme.textColor)

    return rememberHorizontalLegend(
        items = rememberExtraLambda {
            labelsWithColors.forEachIndexed { index, (label, color) ->
                add(
                    LegendItem(
                        icon = shapeComponent(fill(color), CorneredShape.Pill),
                        labelComponent = labelComponent,
                        label = label
                    )
                )
            }
        },
        padding = dimensions(top = 8.dp),
    )
}