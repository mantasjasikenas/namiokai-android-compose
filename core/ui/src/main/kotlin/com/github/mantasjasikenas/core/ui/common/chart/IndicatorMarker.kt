package com.github.mantasjasikenas.core.ui.common.chart

import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.marker.CandlestickCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.CacheStore

open class IndicatorCartesianMarker(
    protected val indicator: ((Int) -> Component),
    protected val indicatorSizeDp: Float = 16f,
    private val guideline: LineComponent? = null,
) : CartesianMarker {
    override fun drawOverLayers(
        context: CartesianDrawingContext,
        targets: List<CartesianMarker.Target>,
    ) {
        with(context) {
            drawGuideline(targets)
            val halfIndicatorSize = (indicatorSizeDp / 2).pixels

            targets.forEach { target ->
                when (target) {
                    is CandlestickCartesianLayerMarkerTarget -> {
                        drawIndicator(
                            target.canvasX,
                            target.openingCanvasY,
                            target.openingColor,
                            halfIndicatorSize,
                        )
                        drawIndicator(
                            target.canvasX,
                            target.closingCanvasY,
                            target.closingColor,
                            halfIndicatorSize,
                        )
                        drawIndicator(
                            target.canvasX,
                            target.lowCanvasY,
                            target.lowColor,
                            halfIndicatorSize
                        )
                        drawIndicator(
                            target.canvasX,
                            target.highCanvasY,
                            target.highColor,
                            halfIndicatorSize
                        )
                    }

                    is ColumnCartesianLayerMarkerTarget -> {
                        target.columns.forEach { column ->
                            drawIndicator(
                                target.canvasX,
                                column.canvasY,
                                column.color,
                                halfIndicatorSize
                            )
                        }
                    }

                    is LineCartesianLayerMarkerTarget -> {
                        target.points.forEach { point ->
                            drawIndicator(
                                target.canvasX,
                                point.canvasY,
                                point.color,
                                halfIndicatorSize
                            )
                        }
                    }
                }
            }
        }
    }

    protected open fun CartesianDrawingContext.drawIndicator(
        x: Float,
        y: Float,
        color: Int,
        halfIndicatorSize: Float,
    ) {
        cacheStore
            .getOrSet(keyNamespace, indicator, color) { indicator.invoke(color) }
            .draw(
                this,
                x - halfIndicatorSize,
                y - halfIndicatorSize,
                x + halfIndicatorSize,
                y + halfIndicatorSize,
            )
    }

    private fun CartesianDrawingContext.drawGuideline(targets: List<CartesianMarker.Target>) {
        targets
            .map { it.canvasX }
            .toSet()
            .forEach { x -> guideline?.drawVertical(this, layerBounds.top, layerBounds.bottom, x) }
    }

    protected companion object {
        val keyNamespace: CacheStore.KeyNamespace = CacheStore.KeyNamespace()
    }
}