package com.example.namiokai.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.unit.dp
import com.example.namiokai.data.repository.preferences.PreferenceKeys
import com.example.namiokai.data.repository.preferences.rememberPreference


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var isDarkModeEnabled by rememberPreference(
        PreferenceKeys.IS_DARK_MODE_ENABLED,
        isSystemInDarkTheme()
    )
    var useSystemTheme by rememberPreference(
        PreferenceKeys.USE_SYSTEM_DEFAULT_THEME,
        false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {
        SettingsGroupSpacer()
        SettingsEntryGroupText(title = "General")
        SwitchSettingEntry(
            title = "Dark mode",
            text = "Toggles theme",
            isChecked = isDarkModeEnabled,
            onCheckedChange = { isDarkModeEnabled = !isDarkModeEnabled },
            isEnabled = !useSystemTheme
        )
        SwitchSettingEntry(
            title = "Use system theme",
            text = "Toggles system default theme",
            isChecked = useSystemTheme,
            onCheckedChange = { useSystemTheme = !useSystemTheme }
        )
        SettingsGroupSpacer()
    }
}

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

    /*if (isShowingDialog) {
        ValueSelectorDialog(
            onDismiss = { isShowingDialog = false },
            title = title,
            selectedValue = selectedValue,
            values = values,
            onValueSelected = onValueSelected,
            valueText = valueText
        )
    }*/

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
            Switch(
                enabled = isEnabled,
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
    isEnabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(enabled = isEnabled, onClick = onClick)
            .alpha(if (isEnabled) 1f else 0.5f)
            .padding(start = 16.dp, end = 16.dp)
            .padding(all = 16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = title,
                style = typography.titleMedium,
            )

            Text(
                text = text,
                style = typography.labelMedium,
            )
        }

        trailingContent?.invoke()
    }
}

@Composable
fun SettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ImportantSettingsDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = typography.bodyMedium,
        modifier = modifier
            .padding(start = 16.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
        style = typography.bodyMedium,
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
        modifier = modifier
            .height(24.dp)
    )
}

/*@Composable
inline fun <T> ValueSelectorDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    crossinline valueText: (T) -> String = { it.toString() }
) {

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 48.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = title,
                style = typography.bodySmall,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                values.forEach { value ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onDismiss()
                                    onValueSelected(value)
                                }
                            )
                            .padding(vertical = 12.dp, horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        if (selectedValue == value) {
                            Canvas(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    //.background()
                            ) {
                                drawCircle(
                                    color = ,
                                    radius = 4.dp.toPx(),
                                    center = size.center,
//                                    shadow = Shadow(
//                                        color = Color.Black.copy(alpha = 0.4f),
//                                        blurRadius = 4.dp.toPx(),
//                                        offset = Offset(x = 0f, y = 1.dp.toPx())
//                                    )
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(
                                        width = 1.dp,
                                        color = colorPalette.textDisabled,
                                        shape = CircleShape
                                    )
                            )
                        }

                        Text(
                            text = valueText(value),
                            style = typography.labelMedium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
            ) {
                DialogTextButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }
        }
    }
}*/

