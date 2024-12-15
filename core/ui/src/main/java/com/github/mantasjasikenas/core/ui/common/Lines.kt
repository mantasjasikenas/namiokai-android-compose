package com.github.mantasjasikenas.core.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StampedPathEffectStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.ui.theme.NamiokaiTheme

@Composable
fun SolidLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
    cap: StrokeCap = Stroke.DefaultCap
) {
    Canvas(modifier) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
            cap = cap
        )
    }
}

@Composable
fun DashedLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
    phaseDivider: Int
) {
    val density = LocalDensity.current

    with(density) {
        val dashOnInterval = (thickness * 4).toPx()
        val dashOffInterval = (thickness * 4).toPx()

        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashOnInterval, dashOffInterval),
            phase = if (phaseDivider == 0) 0f else dashOnInterval / phaseDivider
        )

        Canvas(modifier) {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = thickness.toPx(),
                cap = StrokeCap.Butt,
                pathEffect = pathEffect,
            )
        }
    }
}

@Composable
fun MultiDashedLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val density = LocalDensity.current
    with(density) {
        val dashOnInterval1 = (thickness * 4).toPx()
        val dashOffInterval1 = (thickness * 2).toPx()
        val dashOnInterval2 = (thickness / 4).toPx()
        val dashOffInterval2 = (thickness * 2).toPx()

        val pathEffect =
            PathEffect.dashPathEffect(
                intervals = floatArrayOf(
                    dashOnInterval1,
                    dashOffInterval1,
                    dashOnInterval2,
                    dashOffInterval2
                ),
                phase = 0f
            )
        Canvas(modifier) {
            drawLine(
                color = color,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = thickness.toPx(),
                cap = StrokeCap.Round,
                pathEffect = pathEffect,
            )
        }
    }
}

@Composable
fun DottedLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val dotRadius = thickness / 2
    val dotSpacing = dotRadius * 2

    Canvas(modifier) {
        val circle = Path()

        circle.addOval(Rect(center = Offset.Zero, radius = dotRadius.toPx()))

        val pathEffect = PathEffect.stampedPathEffect(
            shape = circle,
            advance = dotSpacing.toPx(),
            phase = 0f,
            style = StampedPathEffectStyle.Translate
        )
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            cap = StrokeCap.Round,
            strokeWidth = dotRadius.toPx()
        )
    }
}

@Composable
fun DottedSeparatedLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val dotRadius = thickness / 2
    val dotSpacing = dotRadius * 4

    Canvas(modifier) {
        val circle = Path()

        circle.addOval(Rect(center = Offset.Zero, radius = dotRadius.toPx()))

        val pathEffect = PathEffect.stampedPathEffect(
            shape = circle,
            advance = dotSpacing.toPx(),
            phase = 0f,
            style = StampedPathEffectStyle.Translate
        )

        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            cap = StrokeCap.Round,
            strokeWidth = dotRadius.toPx()
        )
    }
}

@Composable
fun HeartLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val shapeRadius = thickness / 2
    val dotSpacing = shapeRadius * 4
    val density = LocalDensity.current

    val heartPath = remember {
        with(density) {
            Path().apply {
                val width = (shapeRadius * 2).toPx()
                val height = (shapeRadius * 2).toPx()

                moveTo(width / 2, height / 4)
                cubicTo(width / 4, 0f, 0f, height / 3, width / 4, height / 2)
                lineTo(width / 2, height * 3 / 4)
                lineTo(width * 3 / 4, height / 2)
                cubicTo(width, height / 3, width * 3 / 4, 0f, width / 2, height / 4)
            }
        }
    }
    Canvas(modifier) {
        val pathEffect = PathEffect.stampedPathEffect(
            shape = heartPath,
            advance = dotSpacing.toPx(),
            phase = 0f,
            style = StampedPathEffectStyle.Translate
        )

        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = shapeRadius.toPx()
        )
    }
}

@Composable
fun ZigZagLineTriangles(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val shapeWidth = thickness
    val density = LocalDensity.current

    val zigZagPath = remember {
        with(density) {
            Path().apply {
                val zigZagWidth = shapeWidth.toPx()
                val zigZagHeight = shapeWidth.toPx()

                moveTo(0f, 0f)
                lineTo(zigZagWidth / 2, zigZagHeight / 2)
                lineTo(zigZagWidth, 0f)
            }
        }
    }
    Canvas(modifier) {
        val pathEffect = PathEffect.stampedPathEffect(
            shape = zigZagPath,
            advance = shapeWidth.toPx(),
            phase = 0f,
            style = StampedPathEffectStyle.Translate
        )
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = shapeWidth.toPx()
        )
    }
}

@Composable
fun ZigZagLineBaseline(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val shapeWidth = thickness
    val density = LocalDensity.current

    val zigZagPath = remember {
        with(density) {
            Path().apply {
                val zigZagWidth = shapeWidth.toPx()
                val zigZagHeight = shapeWidth.toPx()
                val zigZagLineWidth = (1.dp).toPx()

                moveTo(0f, 0f)

                lineTo(zigZagWidth / 2, zigZagHeight / 2)
                lineTo(zigZagWidth, 0f)
                lineTo(zigZagWidth, 0f + zigZagLineWidth)
                lineTo(zigZagWidth / 2, zigZagHeight / 2 + zigZagLineWidth)
                lineTo(0f, 0f + zigZagLineWidth)
            }
        }
    }

    Canvas(modifier) {
        val pathEffect = PathEffect.stampedPathEffect(
            shape = zigZagPath,
            advance = shapeWidth.toPx(),
            phase = 0f,
            style = StampedPathEffectStyle.Translate
        )

        drawLine(
            color = Color.Magenta,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = (1.dp).toPx()
        )
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = shapeWidth.toPx()
        )
    }
}

@Composable
fun ZigZagLine(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color
) {
    val shapeWidth = thickness
    val density = LocalDensity.current

    val zigZagPath = remember {
        with(density) {
            Path().apply {
                val zigZagWidth = shapeWidth.toPx()
                val zigZagHeight = shapeWidth.toPx()
                val zigZagLineWidth = (1.dp).toPx()
                val shapeVerticalOffset = (zigZagHeight / 2) / 2
                val shapeHorizontalOffset = (zigZagHeight / 2) / 2

                moveTo(0f, 0f)

                lineTo(zigZagWidth / 2, zigZagHeight / 2)
                lineTo(zigZagWidth, 0f)
                lineTo(zigZagWidth, 0f + zigZagLineWidth)
                lineTo(zigZagWidth / 2, zigZagHeight / 2 + zigZagLineWidth)
                lineTo(0f, 0f + zigZagLineWidth)
                translate(Offset(-shapeHorizontalOffset, -shapeVerticalOffset))
            }
        }
    }

    Canvas(modifier) {
        val pathEffect = PathEffect.stampedPathEffect(
            shape = zigZagPath,
            advance = shapeWidth.toPx(),
            phase = 0f,
            style = StampedPathEffectStyle.Translate
        )

        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = shapeWidth.toPx()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LinePreview() {
    NamiokaiTheme(
        themePreferences = ThemePreferences()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            SolidLine(color = Color.Black, modifier = Modifier.fillMaxWidth())

            DashedLine(modifier = Modifier.fillMaxWidth(), color = Color.Yellow, phaseDivider = 0)
            DashedLine(modifier = Modifier.fillMaxWidth(), color = Color.Yellow, phaseDivider = 2)
            DashedLine(modifier = Modifier.fillMaxWidth(), color = Color.Yellow, phaseDivider = 1)

            MultiDashedLine(modifier = Modifier.fillMaxWidth(), color = Color.LightGray)

            DottedLine(modifier = Modifier.fillMaxWidth(), color = Color.Green)
            DottedSeparatedLine(modifier = Modifier.fillMaxWidth(), color = Color.Cyan)
            HeartLine(modifier = Modifier.fillMaxWidth(), color = Color.Magenta)

            ZigZagLineTriangles(modifier = Modifier.fillMaxWidth(), color = Color.Blue)
            ZigZagLineBaseline(modifier = Modifier.fillMaxWidth(), color = Color.Blue)
            ZigZagLine(modifier = Modifier.fillMaxWidth(), color = Color.Blue)
        }
    }
}