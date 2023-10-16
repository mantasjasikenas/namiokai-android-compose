package com.github.mantasjasikenas.core.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SwitchSettingEntry(
    title: String,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    SettingsEntry(
        title = title,
        text = text,
        isEnabled = isEnabled,
        onClick = { onCheckedChange(!isChecked) },
        trailingContent = {
            Switch(enabled = isEnabled,
                checked = isChecked,
                onCheckedChange = { onCheckedChange(!isChecked) })
        },
        modifier = modifier
    )
}

@Composable
fun SettingsEntry(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    confirmClick: Boolean = false,
    isEnabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(enabled = isEnabled,
                onClick = {
                    if (confirmClick) {
                        showDialog = true
                    }
                    else {
                        onClick()
                    }
                })
            .alpha(if (isEnabled) 1f else 0.5f)
            .padding(
                start = 16.dp,
                end = 16.dp
            )
            .padding(all = 16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        trailingContent?.invoke()
    }

    if (showDialog) {
        NamiokaiConfirmDialog(onConfirm = {
            showDialog = false
            onClick()
        },
            onDismiss = { showDialog = false })

    }
}

@Composable
fun SettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    )
}

@Composable
fun SettingsEntryGroupText(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun SettingsGroupSpacer(
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier.height(24.dp)
    )
}

@Composable
fun ImportantSettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    )
}

//region ValueSelector
@Composable
inline fun <reified T : Enum<T>> EnumValueSelectorSettingsEntry(
    title: String,
    selectedValue: T,
    crossinline onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    crossinline valueText: (T) -> String = Enum<T>::name,
    noinline trailingContent: (@Composable () -> Unit)? = null
) {
    ValueSelectorSettingsEntry(
        title = title,
        selectedValue = selectedValue,
        values = enumValues<T>().toList(),
        onValueSelected = onValueSelected,
        modifier = modifier,
        isEnabled = isEnabled,
        valueText = valueText,
        trailingContent = trailingContent,
    )
}

@Composable
inline fun <T> ValueSelectorSettingsEntry(
    title: String,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    crossinline valueText: (T) -> String = { it.toString() },
    noinline trailingContent: (@Composable () -> Unit)? = null
) {
    var isShowingDialog by remember {
        mutableStateOf(false)
    }

    if (isShowingDialog) {
        ValueSelectorDialog(
            onDismiss = { isShowingDialog = false },
            title = title,
            selectedValue = selectedValue,
            values = values,
            onValueSelected = onValueSelected,
            valueText = valueText
        )
    }

    SettingsEntry(
        title = title,
        text = valueText(selectedValue),
        modifier = modifier,
        isEnabled = isEnabled,
        onClick = { isShowingDialog = true },
        trailingContent = trailingContent
    )
}

@Composable
inline fun <T> ValueSelectorDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    crossinline valueText: (T) -> String = { it.toString() }
) {

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp,
            modifier = modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.padding(
                        vertical = 8.dp,
                        horizontal = 24.dp
                    )
                )
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    values.forEach { value ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .clickable(onClick = {
                                    onDismiss()
                                    onValueSelected(value)
                                })
                                .padding(
                                    vertical = 12.dp,
                                    horizontal = 24.dp
                                )
                                .fillMaxWidth()
                        ) {
                            if (selectedValue == value) {
                                Canvas(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                ) {
                                    drawCircle(
                                        color = com.github.mantasjasikenas.core.ui.theme.md_theme_dark_primary,
                                        radius = 4.dp.toPx(),
                                        center = size.center,
                                    )
                                }
                            }
                            else {
                                Spacer(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = CircleShape
                                        )
                                )
                            }

                            Text(
                                text = valueText(value),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}
//endregion