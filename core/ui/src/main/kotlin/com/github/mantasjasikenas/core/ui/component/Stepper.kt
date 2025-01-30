package com.github.mantasjasikenas.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mantasjasikenas.core.ui.R

@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE
) {
    Stepper(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        minValue = range.first,
        maxValue = range.last
    )
}

@Composable
fun Stepper(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = Int.MIN_VALUE,
    maxValue: Int = Int.MAX_VALUE,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(
            onClick = {
                if (value > minValue) {
                    onValueChange(value - 1)
                }
            },
            enabled = value > minValue
        ) {
            Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = stringResource(R.string.decrease)
            )
        }

        Text(
            modifier = Modifier
                .width(48.dp),
            text = value.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 18.sp
        )

        StepperButton(
            modifier = Modifier.clip(RectangleShape),
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            enabled = value < maxValue
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(R.string.increase)
            )
        }
    }


//    TextField(
//        value = value.toString(),
//        onValueChange = {
//            val newValue = it.toIntOrNull() ?: value
//            if (newValue in minValue..maxValue) {
//                onValueChange(newValue)
//            }
//        },
//        label = { Text(text = "Value") },
//        keyboardOptions = KeyboardOptions(
//            keyboardType = KeyboardType.Number,
//            imeAction = ImeAction.Done
//        ),
//        trailingIcon = {
//            IconButton(onClick = { onValueChange(value + 1) }) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Increase"
//                )
//            }
//        }
//    )
}

@Composable
internal fun StepperButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    colors: IconButtonColors = IconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ),
    shape: Shape = MaterialTheme.shapes.medium,
    size: Dp = 40.0.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier =
        modifier
            .minimumInteractiveComponentSize()
            .size(size)
            .clip(shape)
            .background(color = if (enabled) colors.containerColor else colors.disabledContainerColor)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun StepperPreview() {
    val value = remember {
        mutableIntStateOf(100)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Stepper(
            value = value.intValue,
            onValueChange = { value.intValue = it },
            range = 1..200
        )
    }
}