package com.github.mantasjasikenas.namiokai.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.keelar.exprk.Expressions
import com.github.mantasjasikenas.namiokai.utils.format

@Composable
fun NamiokaiTextField(
    modifier: Modifier = Modifier,
    label: String,
    initialTextFieldValue: String = "",
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    var text by remember { mutableStateOf(TextFieldValue(initialTextFieldValue)) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = text,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(onNext = {
            val isMoved = focusManager.moveFocus(FocusDirection.Down)
            if (!isMoved) {
                focusManager.clearFocus()
            }
        }),
        onValueChange = {
            text = it
            onValueChange(it.text)
        },
        singleLine = singleLine,
        modifier = modifier
    )
}

@Composable
fun NamiokaiNumberField(
    modifier: Modifier = Modifier,
    label: String,
    initialTextFieldValue: String = "",
    onValueChange: (Double) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    var text by remember { mutableStateOf(TextFieldValue(initialTextFieldValue)) }
    var number by remember { mutableStateOf(0.0) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = text,
        label = {
            number.takeIf { it == 0.0 }
                ?.let {
                    Text(text = label)
                } ?: Text(text = "$label is ${number.format(2)}")
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(onNext = {
            val isMoved = focusManager.moveFocus(FocusDirection.Down)
            if (!isMoved) {
                focusManager.clearFocus()
            }
        }),
        onValueChange = {
            // TODO review implementation logic
            text = it
            val expr = it.text.replace(",", ".")

            if (expr.isEmpty()) {
                onValueChange(0.0)
                number = 0.0
                return@OutlinedTextField
            }

            try {
                number = Expressions()
                    .eval(expr)
                    .toDouble()

                onValueChange(number)

            } catch (_: Throwable) {
                onValueChange(expr.toDoubleOrNull() ?: 0.0)
            }
        },
        singleLine = singleLine,
        modifier = modifier
    )
}

@Composable
fun NamiokaiTextArea(
    modifier: Modifier = Modifier,
    label: String,
    initialTextFieldValue: String = "",
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var text by remember { mutableStateOf(TextFieldValue(initialTextFieldValue)) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier.height(100.dp),
        value = text,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = keyboardType
        ),
        keyboardActions = KeyboardActions(onNext = {
            val isMoved = focusManager.moveFocus(FocusDirection.Down)
            if (!isMoved) {
                focusManager.clearFocus()
            }
        }),
        onValueChange = {
            text = it
            onValueChange(it.text)
        },
    )
}