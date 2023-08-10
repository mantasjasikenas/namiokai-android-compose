package com.github.mantasjasikenas.namiokai.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiDialog
import com.github.mantasjasikenas.namiokai.ui.components.NamiokaiTextField
import com.github.mantasjasikenas.namiokai.ui.components.SettingsEntry
import com.github.mantasjasikenas.namiokai.ui.components.SettingsEntryGroupText
import com.github.mantasjasikenas.namiokai.ui.components.SettingsGroupSpacer
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.theme.getColorScheme
import com.github.mantasjasikenas.namiokai.utils.Constants
import com.github.mantasjasikenas.namiokai.utils.toHex
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
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
    @Suppress("UNUSED_PARAMETER")
    onAccentColorClick: (AccentColor) -> Unit,
    onAccentColorPin: (Int, Boolean) -> Unit,
    onClearAccentColorsClick: () -> Unit,
    onDismiss: () -> Unit,
    accentColors: List<AccentColor>
) {
    var expanded by rememberState { false }
    val controller = rememberColorPickerController()
    val clipboardManager = LocalClipboardManager.current
    val initialColor = rememberState {
        themePreferences.accentColor
    }
    val selectedColor = rememberState {
        themePreferences.accentColor
    }


    NamiokaiDialog(
        title = "Pick a color",
        selectedValue = selectedColor.value,
        onSaveClick = onSaveClick,
        onDismiss = onDismiss
    ) {
        HsvColorPicker(
            modifier = Modifier
                .padding(10.dp)
                .height(250.dp),
            onColorChanged = {
                selectedColor.value = it.color
            },
            controller = controller,
            drawOnPosSelected = {
                drawColorIndicator(
                    controller.selectedPoint.value,
                    controller.selectedColor.value
                )
            },
            initialColor = initialColor.value
        )
        /*BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
            initialColor = initialColor.value
        )*/
        Column(
            modifier = Modifier.noRippleClickable {
                clipboardManager.setText(AnnotatedString(controller.selectedColor.value.toHex()))
            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = selectedColor.value.toHex(),
                style = typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            AlphaTile(
                modifier = Modifier
                    .padding()
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp)),
                selectedColor = selectedColor.value,
                //controller = controller,
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

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Pin all") },
                                onClick = {
                                    expanded = false
                                    accentColors.forEach {
                                        onAccentColorPin(
                                            it.id,
                                            true
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Unpin all") },
                                onClick = {
                                    expanded = false
                                    accentColors.forEach {
                                        onAccentColorPin(
                                            it.id,
                                            false
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear unpinned") },
                                onClick = {
                                    expanded = false
                                    onClearAccentColorsClick()
                                }
                            )
                        }
                    }
                }


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        4.dp,
                        Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(
                        items = accentColors,
                        key = { it.id }) {
                        Column(
                            modifier = Modifier.animateItemPlacement(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box {
                                AlphaTile(
                                    modifier = Modifier
                                        .padding()
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .combinedClickable(
                                            onClick = {
                                                //onAccentColorClick(it)
                                                selectedColor.value = it.toColor()
                                            },
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
                            Row(
                                modifier = Modifier.heightIn(14.dp),
                            ) {
                                //if (it.pinned) {
                                Icon(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .size(16.dp) // 12
                                        .conditional(
                                            condition = it.pinned,
                                            modifier = {
                                                this.rotate(45f)
                                            })
                                        .noRippleClickable {
                                            onAccentColorPin(
                                                it.id,
                                                it.pinned.not()
                                            )
                                        },
                                    tint = if (it.pinned) it.toColor() else colorScheme.onSurface,
                                    imageVector = if (it.pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = null,
                                )
                                // }
                                /*Text(
                                    modifier = Modifier.combinedClickable(
                                        onClick = {

                                        },
                                        onLongClick = {
                                            clipboardManager.setText(AnnotatedString(it.toHex()))
                                        }
                                    ),
                                    text = it.toHex(),
                                    style = typography.bodySmall, // bodySmall
                                    fontWeight = FontWeight.SemiBold
                                )*/
                            }
                        }
                    }
                }

                NamiokaiSpacer(height = 20)
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