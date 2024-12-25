package com.github.mantasjasikenas.feature.settings

import android.net.Uri
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.util.Constants
import com.github.mantasjasikenas.core.common.util.toHex
import com.github.mantasjasikenas.core.database.AccentColor
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.theme.Theme
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemeType
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.conditional
import com.github.mantasjasikenas.core.ui.common.noRippleClickable
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.FancyIndicatorTabs
import com.github.mantasjasikenas.core.ui.component.NamiokaiDialog
import com.github.mantasjasikenas.core.ui.component.NamiokaiTextField
import com.github.mantasjasikenas.core.ui.component.SettingsEntry
import com.github.mantasjasikenas.core.ui.component.SettingsEntryGroupText
import com.github.mantasjasikenas.core.ui.component.SettingsGroupSpacer
import com.github.mantasjasikenas.core.ui.theme.getColorScheme
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun SettingsRoute() {
    SettingsScreen()
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsUiState = settingsUiState,
        onAccentColorPin = viewModel::updateAccentColorPinStatus,
        onClearAccentColorsClick = viewModel::clearUnpinnedAccentColors,
        onSaveAccentColor = viewModel::insertAccentColor,
        onUpdateThemePreferences = viewModel::updateThemePreferences,
        onImageAddToStorage = viewModel::addImageToStorage,
        onUpdateDisplayName = viewModel::updateDisplayName,
        validateNewDisplayName = viewModel::validateDisplayName,
        onLogoutClick = {
            viewModel.logout()
        },
    )

}


@Composable
fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onAccentColorPin: (Int, Boolean) -> Unit,
    onClearAccentColorsClick: () -> Unit,
    onSaveAccentColor: (AccentColor) -> Unit,
    onUpdateThemePreferences: (ThemePreferences) -> Unit,
    onImageAddToStorage: (Uri) -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    validateNewDisplayName: (String) -> Boolean,
    onLogoutClick: () -> Unit,

    ) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {
        val uiState = when (settingsUiState) {
            is SettingsUiState.Success -> settingsUiState
            else -> {
//                Text(text = "Loading...")
                return
            }
        }

        SettingsGroupSpacer()

        AppearanceSettingsGroup(
            themePreferences = uiState.userData.themePreferences,
            accentColors = settingsUiState.accentColors,
            onUpdateThemePreferences = onUpdateThemePreferences,
            onSaveAccentColor = onSaveAccentColor,
            onClearAccentColorsClick = onClearAccentColorsClick,
            onAccentColorPin = onAccentColorPin,
        )

        ProfileSettingsGroup(
            onImageAddToStorage = onImageAddToStorage,
            onLogoutClick = onLogoutClick,
            onUpdateDisplayName = onUpdateDisplayName,
            validateNewDisplayName = validateNewDisplayName,
            currentUser = uiState.userData.user
        )
    }
}

@Composable
private fun ProfileSettingsGroup(
    currentUser: User,
    onImageAddToStorage: (Uri) -> Unit,
    onLogoutClick: () -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    validateNewDisplayName: (String) -> Boolean,
) {
    val (updateDisplayNameDialogState, setUpdateNameDialogState) = remember { mutableStateOf(false) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            imageUri?.let {
                onImageAddToStorage(it)
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
            SettingsEntry(
                title = "Log out",
                text = "You are logged in as ${currentUser.displayName}",
                onClick = onLogoutClick
            )
        }
    }

    if (updateDisplayNameDialogState) {
        ChangeDisplayNameDialog(onSaveClick = {
            val name = it.trim()
            val isValid = validateNewDisplayName(name)
//            settingsViewModel.validateDisplayName(
//                mainUiState,
//                name
//            )
            if (!isValid) {
                return@ChangeDisplayNameDialog
            }

            onUpdateDisplayName(name)
            setUpdateNameDialogState(false)
        },
            onDismiss = { setUpdateNameDialogState(false) })
    }
}


@Composable
private fun ColumnScope.AppearanceSettingsGroup(
    accentColors: List<AccentColor>,
    themePreferences: ThemePreferences,
    onUpdateThemePreferences: (ThemePreferences) -> Unit,
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
            onUpdateThemePreferences(
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
        onUpdateThemePreferences(
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
            text = "Mode",
            style = typography.titleMedium,
        )
        val values = ThemeType.entries
            .map { it.title }

        FancyIndicatorTabs(
            values = values,
            selectedIndex = values.indexOf(themePreferences.themeType.title),
            onValueChange = {
                val themeType = ThemeType.entries[it]

                onUpdateThemePreferences(
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
                text = "Colour",
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
                                onUpdateThemePreferences(
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
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
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
                                                selectedColor.value = it.toColor()
                                                controller.selectByColor(
                                                    it.toColor(),
                                                    true
                                                )
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
                                Icon(
                                    modifier = Modifier
                                        .padding(1.dp)
                                        .size(16.dp)
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
            label = "Display name",
            onValueChange = { newDisplayName.value = it })
    }
}