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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mantasjasikenas.core.common.localization.Language
import com.github.mantasjasikenas.core.common.util.Constants
import com.github.mantasjasikenas.core.common.util.toHex
import com.github.mantasjasikenas.core.database.AccentColor
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.theme.Theme
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemeType
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
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

    when (settingsUiState) {
        SettingsUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is SettingsUiState.Success -> {
            SettingsScreen(
                uiState = settingsUiState as SettingsUiState.Success,
                onAccentColorPin = viewModel::updateAccentColorPinStatus,
                onClearAccentColorsClick = viewModel::clearUnpinnedAccentColors,
                onSaveAccentColor = viewModel::insertAccentColor,
                onUpdateThemePreferences = viewModel::updateThemePreferences,
                onImageAddToStorage = viewModel::addImageToStorage,
                onUpdateDisplayName = viewModel::updateDisplayName,
                validateNewDisplayName = viewModel::validateDisplayName,
                onLogoutClick = viewModel::logout,
                currentLanguageIso = viewModel.currentLanguageIso,
                onLanguageUpdate = viewModel::updateLanguage
            )
        }
    }
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState.Success,
    onAccentColorPin: (Int, Boolean) -> Unit,
    onClearAccentColorsClick: () -> Unit,
    onSaveAccentColor: (AccentColor) -> Unit,
    onUpdateThemePreferences: (ThemePreferences) -> Unit,
    onImageAddToStorage: (Uri) -> Unit,
    onUpdateDisplayName: (String) -> Unit,
    validateNewDisplayName: (String) -> Boolean,
    onLogoutClick: () -> Unit,
    currentLanguageIso: String,
    onLanguageUpdate: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding()
    ) {
        SettingsGroupSpacer()

        GeneralSettingsGroup(
            currentLanguageIso = currentLanguageIso,
            onLanguageUpdate = onLanguageUpdate
        )

        AppearanceSettingsGroup(
            themePreferences = uiState.userData.themePreferences,
            accentColors = uiState.accentColors,
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
            SettingsEntryGroupText(title = stringResource(R.string.feature_settings_profile_group_title))
            SettingsEntry(
                title = stringResource(R.string.feature_settings_change_profile_picture),
                text = stringResource(R.string.feature_settings_update_your_profile_picture),
                onClick = {
                    galleryLauncher.launch(Constants.IMAGES_TYPE)
                })
            SettingsEntry(
                title = stringResource(R.string.feature_settings_change_display_name),
                text = stringResource(R.string.feature_settings_update_display_name),
                onClick = {
                    setUpdateNameDialogState(true)
                })
            SettingsGroupSpacer()
            SettingsEntryGroupText(title = stringResource(R.string.feature_settings_account))
            SettingsEntry(
                title = stringResource(R.string.feature_settings_email),
                text = currentUser.email.ifEmpty { stringResource(R.string.feature_settings_not_logged_in) },
                onClick = { })
            SettingsEntry(
                title = stringResource(R.string.feature_settings_log_out),
                text = stringResource(
                    R.string.feature_settings_you_are_logged_in_as,
                    currentUser.displayName
                ),
                onClick = onLogoutClick
            )
        }
    }

    if (updateDisplayNameDialogState) {
        ChangeDisplayNameDialog(
            onSaveClick = {
                val name = it.trim()
                val isValid = validateNewDisplayName(name)

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
private fun GeneralSettingsGroup(
    currentLanguageIso: String,
    onLanguageUpdate: (String) -> Unit
) {
    val languages = Language.entries.toList()
    var selectedLanguageIndex by remember {
        mutableIntStateOf(
            languages.indexOfFirst { it.iso == currentLanguageIso }.coerceAtLeast(0)
        )
    }

    SettingsEntryGroupText(title = stringResource(R.string.feature_settings_general))

    Column(
        modifier = Modifier.padding(
            horizontal = 32.dp,
            vertical = 16.dp
        )
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(R.string.feature_settings_language),
            style = typography.titleMedium,
        )

        FancyIndicatorTabs(
            values = languages,
            selectedIndex = selectedLanguageIndex,
            onValueChange = { index ->
                selectedLanguageIndex = index

                onLanguageUpdate(
                    languages.getOrNull(index)?.iso ?: Language.English.iso
                )
            },
            textForValue = { stringResource(it.titleResourceId) }
        )
    }

    SettingsGroupSpacer()
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

    SettingsEntryGroupText(title = stringResource(R.string.feature_settings_appearance))

    Column(
        modifier = Modifier.padding(
            horizontal = 32.dp,
            vertical = 16.dp
        )
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(R.string.feature_settings_mode),
            style = typography.titleMedium,
        )

        val themes = ThemeType.entries.toList()

        FancyIndicatorTabs(
            values = themes,
            selectedIndex = themes.indexOf(themePreferences.themeType),
            onValueChange = {
                val themeType = ThemeType.entries[it]

                onUpdateThemePreferences(
                    themePreferences.copy(
                        themeType = themeType
                    )
                )
            },
            textForValue = { stringResource(it.titleResourceId) }
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
                text = stringResource(R.string.feature_settings_colour),
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

                    Column(
                        verticalArrangement = Arrangement.Center,
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
                            text = stringResource(theme.titleResId),
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
                    title = stringResource(R.string.feature_settings_change_accent_color),
                    text = stringResource(R.string.feature_settings_update_custom_theme_accent_color),
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
        title = stringResource(R.string.feature_settings_pick_a_color),
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
                        text = stringResource(R.string.feature_settings_recent_colors),
                        style = typography.labelLarge,
                    )

                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.feature_settings_more),
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.feature_settings_pin_all)) },
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
                                text = { Text(stringResource(R.string.feature_settings_unpin_all)) },
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
                                text = { Text(stringResource(R.string.feature_settings_clear_unpinned)) },
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
        title = stringResource(R.string.feature_settings_type_your_new_display_name),
        selectedValue = newDisplayName.value,
        onSaveClick = onSaveClick,
        onDismiss = onDismiss
    ) {
        NamiokaiTextField(
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 30.dp
            ),
            label = stringResource(R.string.feature_settings_display_name),
            onValueChange = { newDisplayName.value = it })
    }
}