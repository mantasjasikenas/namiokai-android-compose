package com.github.mantasjasikenas.core.ui.common.chart

import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberLayeredComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shadow
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.HorizontalDimensions
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker.LabelPosition
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Corner
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@androidx.compose.runtime.Composable
internal fun rememberMarker(
    labelPosition: LabelPosition = LabelPosition.Top,
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = markerCorneredShape(Corner.Companion.FullyRounded)
    val labelBackground = rememberShapeComponent(
        fill = fill(MaterialTheme.colorScheme.surfaceBright),
        shape = labelBackgroundShape,
        shadow = shadow(
            radius = LABEL_BACKGROUND_SHADOW_RADIUS_DP.dp, dy = LABEL_BACKGROUND_SHADOW_DY_DP.dp
        ),
    )
    val label = rememberTextComponent(
        color = MaterialTheme.colorScheme.onSurface,
        textAlignment = Layout.Alignment.ALIGN_CENTER,
        padding = dimensions(8.dp, 4.dp),
        background = labelBackground,
        minWidth = TextComponent.MinWidth.fixed(40.dp),
    )
    val indicatorFrontComponent = rememberShapeComponent(
        fill(
            MaterialTheme.colorScheme.surface,
        ), CorneredShape.Companion.Pill
    )
    val indicatorCenterComponent = rememberShapeComponent(shape = CorneredShape.Companion.Pill)
    val indicatorRearComponent = rememberShapeComponent(shape = CorneredShape.Companion.Pill)
    val indicator = rememberLayeredComponent(
        rear = indicatorRearComponent,
        front = rememberLayeredComponent(
            rear = indicatorCenterComponent,
            front = indicatorFrontComponent,
            padding = dimensions(5.dp),
        ),
        padding = dimensions(10.dp),
    )
    val guideline = rememberAxisGuidelineComponent()

    return remember(
        label, labelPosition, indicator, showIndicator, guideline
    ) {
        object : DefaultCartesianMarker(
            label = label,
            labelPosition = labelPosition,
            indicator = if (showIndicator) {
                { color ->
                    LayeredComponent(
                        rear = ShapeComponent(
                            Fill(ColorUtils.setAlphaComponent(color, 38)), CorneredShape.Pill
                        ),
                        front = LayeredComponent(
                            rear = ShapeComponent(
                                fill = Fill(color),
                                shape = CorneredShape.Pill,
                                shadow = Shadow(radiusDp = 12f, color = color),
                            ),
                            front = indicatorFrontComponent,
                            padding = dimensions(5.dp),
                        ),
                        padding = dimensions(10.dp),
                    )
                }
            } else {
                null
            },
            indicatorSizeDp = 36f,
            guideline = guideline,
        ) {
            override fun updateInsets(
                context: CartesianMeasuringContext,
                horizontalDimensions: HorizontalDimensions,
                model: CartesianChartModel,
                insets: Insets,
            ) {
                with(context) {
                    val baseShadowInsetDp =
                        CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER * LABEL_BACKGROUND_SHADOW_RADIUS_DP
                    var topInset = (baseShadowInsetDp - LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    var bottomInset = (baseShadowInsetDp + LABEL_BACKGROUND_SHADOW_DY_DP).pixels
                    when (labelPosition) {
                        LabelPosition.Top, LabelPosition.AbovePoint -> topInset += label.getHeight(
                            context
                        ) + tickSizeDp.pixels

                        LabelPosition.Bottom -> bottomInset += label.getHeight(context) + tickSizeDp.pixels
                        LabelPosition.AroundPoint -> {}
                    }
                    insets.ensureValuesAtLeast(top = topInset, bottom = bottomInset)
                }
            }
        }
    }
}

private const val LABEL_BACKGROUND_SHADOW_RADIUS_DP = 4f
private const val LABEL_BACKGROUND_SHADOW_DY_DP = 2f
private const val CLIPPING_FREE_SHADOW_RADIUS_MULTIPLIER = 1.4f
