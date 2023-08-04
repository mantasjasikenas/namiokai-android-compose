@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.namiokai.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberRichTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.data.AccentColor
import com.github.mantasjasikenas.namiokai.model.theme.Theme
import com.github.mantasjasikenas.namiokai.model.theme.ThemePreferences
import com.github.mantasjasikenas.namiokai.model.theme.ThemeType
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.common.conditional
import com.github.mantasjasikenas.namiokai.ui.common.noRippleClickable
import com.github.mantasjasikenas.namiokai.ui.common.rememberState
import com.github.mantasjasikenas.namiokai.ui.components.FancyIndicatorTabs
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiConfirmDialog
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiDialog
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiTextField
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.theme.getColorScheme
import com.github.mantasjasikenas.namiokai.ui.theme.md_theme_dark_primary
import com.github.mantasjasikenas.namiokai.utils.Constants
import com.github.mantasjasikenas.namiokai.utils.toHex
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val settingsUiState by settingsViewModel.settingsUiState.collectAsState()
    val themePreferences = mainUiState.themePreferences


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {
        SettingsGroupSpacer()
        AppearanceSettingsGroup(
            themePreferences = themePreferences,
            updateThemePreferences = mainViewModel::updateThemePreferences,
            accentColors = settingsUiState.accentColors,
            onSaveAccentColor = settingsViewModel::insertAccentColor,
            onClearAccentColorsClick = settingsViewModel::clearUnpinnedAccentColors,
            onAccentColorPin = settingsViewModel::updateAccentColorPin
        )
        ProfileSettingsGroup(
            settingsViewModel = settingsViewModel,
            mainViewModel = mainViewModel,
            mainUiState = mainUiState
        )
    }
}

@Composable
private fun ProfileSettingsGroup(
    settingsViewModel: SettingsViewModel,
    mainViewModel: MainViewModel,
    mainUiState: MainUiState
) {
    val currentUser = mainUiState.currentUser
    val (updateDisplayNameDialogState, setUpdateNameDialogState) = remember { mutableStateOf(false) }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageUri?.let {
            settingsViewModel.addImageToStorage(imageUri)
        }
    }

    AnimatedVisibility(visible = currentUser.uid.isNotEmpty()) {
        Column {
            SettingsEntryGroupText(title = "Profile")
            SettingsEntry(title = "Change profile picture",
                text = "Update your profile picture",
                onClick = {
                    galleryLauncher.launch(Constants.IMAGES_TYPE)
                })
            SettingsEntry(title = "Change display name",
                text = "Update display name",
                onClick = {
                    setUpdateNameDialogState(true)
                })
            SettingsGroupSpacer()
            SettingsEntryGroupText(title = "Account")
            SettingsEntry(title = "Email",
                text = currentUser.email.ifEmpty { "Not logged in" },
                onClick = { })
            SettingsEntry(title = "Log out",
                text = "You are logged in as ${currentUser.displayName}",
                onClick = {
                    settingsViewModel.logout()
                    mainViewModel.resetCurrentUser()
                })
        }
    }

    if (updateDisplayNameDialogState) {
        ChangeDisplayNameDialog(onSaveClick = {
            val name = it.trim()
            val isValid = settingsViewModel.validateDisplayName(
                mainUiState,
                name
            )
            if (!isValid) {
                return@ChangeDisplayNameDialog
            }

            settingsViewModel.updateDisplayName(name)
            setUpdateNameDialogState(false)
        },
            onDismiss = { setUpdateNameDialogState(false) })
    }
}


@Composable
private fun ColumnScope.AppearanceSettingsGroup(
    accentColors: List<AccentColor>,
    themePreferences: ThemePreferences,
    updateThemePreferences: (ThemePreferences) -> Unit,
    onSaveAccentColor: (AccentColor) -> Unit,
    onAccentColorPin: (Int, Boolean) -> Unit,
    onClearAccentColorsClick: () -> Unit
) {
    var colorDialogState by rememberState { false }

    val onColorsDialogDismiss = {
        colorDialogState = false
    }

    val onColorsDialogOpen = {
        colorDialogState = true
    }
    val onColorsSaveClick = { color: Color ->
        if (themePreferences.accentColor != color) {
            updateThemePreferences(
                themePreferences.copy(
                    accentColor = color
                )
            )
            onSaveAccentColor(
                AccentColor(
                    color = color.toArgb(),
                    date = System.currentTimeMillis()
                )
            )
        }

        onColorsDialogDismiss()
    }
    val onAccentColorClick = { accentColor: AccentColor ->
        updateThemePreferences(
            themePreferences.copy(
                accentColor = Color(accentColor.color)
            )
        )
        onColorsDialogDismiss()
    }




    SettingsEntryGroupText(title = "Appearance")
    Column(
        modifier = Modifier.padding(
            horizontal = 32.dp,
            vertical = 16.dp
        )
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "Colors",
            style = typography.titleMedium,
        )
        val values = ThemeType.values()
            .map { it.title }

        FancyIndicatorTabs(
            values = values,
            selectedIndex = values.indexOf(themePreferences.themeType.title),
            onValueChange = {
                val themeType = ThemeType.values()[it]

                updateThemePreferences(
                    themePreferences.copy(
                        themeType = themeType
                    )
                )
            }
        )
    }


    AnimatedVisibility(
        visible = themePreferences.themeType != ThemeType.AUTOMATIC,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        ) {
            Text(
                text = "Theme",
                style = typography.titleMedium,
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    bottom = 8.dp
                ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val themes = when (themePreferences.themeType) {
                    ThemeType.DARK -> Theme.darkColorThemes
                    ThemeType.LIGHT -> Theme.lightColorThemes
                    else -> emptyList()
                }

                items(
                    themes
                ) { theme ->
                    val colorScheme = getColorScheme(
                        ThemePreferences(
                            themeType = themePreferences.themeType,
                            theme = theme,
                            accentColor = themePreferences.accentColor
                        )
                    )

                    Column(verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .noRippleClickable {
                                updateThemePreferences(
                                    themePreferences.copy(
                                        theme = theme
                                    )
                                )
                            }) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(color = colorScheme.primaryContainer)
                                .border(
                                    width = 2.dp,
                                    color = if (themePreferences.theme == theme) colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                        NamiokaiSpacer(height = 8)
                        Text(
                            text = theme.title,
                            style = typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = (themePreferences.theme == Theme.CUSTOM),
            ) {
                SettingsEntry(
                    modifier = Modifier.offset(x = (-32).dp),
                    title = "Change accent color",
                    text = "Update custom theme accent color.",
                    onClick = onColorsDialogOpen
                )

            }
        }
    }



    SettingsGroupSpacer()

    if (colorDialogState) {
        ColorPickerDialog(
            themePreferences = themePreferences,
            onSaveClick = onColorsSaveClick,
            onDismiss = onColorsDialogDismiss,
            accentColors = accentColors,
            onAccentColorClick = onAccentColorClick,
            onClearAccentColorsClick = onClearAccentColorsClick,
            onAccentColorPin = onAccentColorPin
        )
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColorPickerDialog(
    themePreferences: ThemePreferences,
    onSaveClick: (Color) -> Unit,
    onAccentColorClick: (AccentColor) -> Unit,
    onAccentColorPin: (Int, Boolean) -> Unit,
    onClearAccentColorsClick: () -> Unit,
    onDismiss: () -> Unit,
    accentColors: List<AccentColor>
) {
    val controller = rememberColorPickerController()
    val clipboardManager = LocalClipboardManager.current


    NamiokaiDialog(
        title = "Pick a color",
        selectedValue = controller.selectedColor.value,
        onSaveClick = onSaveClick,
        onDismiss = onDismiss
    ) {
        HsvColorPicker(
            modifier = Modifier
                .padding(10.dp)
                .height(250.dp),
            controller = controller,
            drawOnPosSelected = {
                drawColorIndicator(
                    controller.selectedPoint.value,
                    controller.selectedColor.value
                )
            },
            initialColor = themePreferences.accentColor
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
            initialColor = themePreferences.accentColor
        )
        Column(
            modifier = Modifier.noRippleClickable {
                clipboardManager.setText(AnnotatedString(controller.selectedColor.value.toHex()))
            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = controller.selectedColor.value.toHex(),
                style = typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            AlphaTile(
                modifier = Modifier
                    .padding()
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp)),
                controller = controller,
            )
        }

        AnimatedVisibility(accentColors.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                NamiokaiSpacer(height = 20)

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent colors",
                        style = typography.labelLarge,
                    )


                    val tooltipState = rememberRichTooltipState(isPersistent = true)
                    val scope = rememberCoroutineScope()
                    RichTooltipBox(
                        title = { Text("Recent colors") },
                        action = {
                            TextButton(
                                onClick = { scope.launch { tooltipState.dismiss() } }
                            ) { Text("OK") }
                        },
                        text = {
                            Column {
                                Text(text = "Click - selects as accent color")
                                Text(text = "Long click - pins color")
                                NamiokaiSpacer(height = 10)
                                Text(text = "Pinned colors are not cleared.\nThese colors are displayed \nat the top of the list.")
                            }
                        },
                        tooltipState = tooltipState
                    ) {
                        IconButton(
                            onClick = { scope.launch { tooltipState.show() } },
                            modifier = Modifier.tooltipTrigger()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Localized Description"
                            )
                        }
                    }
                }


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(accentColors) {
                        val cornerColor = colorScheme.inverseSurface

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AlphaTile(
                                modifier = Modifier
                                    .padding()
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .conditional(
                                        condition = it.pinned,
                                        modifier = {
                                            this
                                                /*.background(
                                                    color = cornerColor,
                                                    RoundedCornerShape(6.dp)
                                                )*/
                                                .clip(CutCornerShape(topEnd = 16.dp))
                                        }
                                    )

                                    .combinedClickable(
                                        onClick = { onAccentColorClick(it) },
                                        onLongClick = {
                                            onAccentColorPin(
                                                it.id,
                                                it.pinned.not()
                                            )
                                        }
                                    ),
                                selectedColor = it.toColor()
                            )
                        }
                    }
                }

                NamiokaiSpacer(height = 20) // 12

                Row {
                    OutlinedButton(
                        onClick = {
                            // FIXME replace this with a proper function
                            accentColors.forEach {
                                onAccentColorPin(
                                    it.id,
                                    false
                                )
                            }
                        },
                        //contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "Unpin all",
                            style = typography.labelMedium,
                        )
                    }
                    NamiokaiSpacer(width = 10)

                    Button(
                        onClick = onClearAccentColorsClick,
                        //contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            text = "Clear unpinned",
                            style = typography.labelMedium,
                        )
                    }
                }
            }
        }

    }
}


@Composable
private fun ChangeDisplayNameDialog(
    onSaveClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val newDisplayName = remember { mutableStateOf("") }

    NamiokaiDialog(
        title = "Type your new display name",
        selectedValue = newDisplayName.value,
        onSaveClick = onSaveClick,
        onDismiss = onDismiss
    ) {
        NamiokaiTextField(modifier = Modifier.padding(
            vertical = 10.dp,
            horizontal = 30.dp
        ),
            label = stringResource(R.string.display_name),
            onValueChange = { newDisplayName.value = it })
    }
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
                style = typography.titleMedium,
            )
            Text(
                text = text,
                style = typography.labelMedium,
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
        style = typography.bodyMedium,
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
        color = colorScheme.primary,
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
        style = typography.bodyMedium,
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
            color = colorScheme.background,
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
                                            color = colorScheme.primary,
                                            shape = CircleShape
                                        )
                                ) {
                                    drawCircle(
                                        color = md_theme_dark_primary,
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
                                            color = colorScheme.primary,
                                            shape = CircleShape
                                        )
                                )
                            }

                            Text(
                                text = valueText(value),
                                style = typography.labelLarge
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

@Preview(
    backgroundColor = 0xFFFFFFFF,
    showBackground = true
)
@Composable
fun SettingsEntryPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AlphaTile(
            modifier = Modifier
                .padding()
                .size(40.dp)
                .background(
                    color = Color.Red,
                    RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .clip(CutCornerShape(topEnd = 16.dp)),
            selectedColor = Color.Magenta
        )
    }
}

