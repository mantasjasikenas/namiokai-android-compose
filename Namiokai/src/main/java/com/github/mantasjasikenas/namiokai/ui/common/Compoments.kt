package com.github.mantasjasikenas.namiokai.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.Uid
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.main.UsersMap
import com.github.mantasjasikenas.namiokai.ui.theme.NamiokaiTheme
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun FloatingAddButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LargeFloatingActionButton(
            modifier = Modifier.padding(all = 15.dp),
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
            )
        }
    }
}

@Composable
fun NamiokaiSpacer(
    height: Int = 0,
    width: Int = 0
) {
    Spacer(
        modifier = Modifier
            .height(height.dp)
            .width(width.dp)
    )
}


@Composable
fun CardText(
    label: String,
    value: String
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(text = value)
    NamiokaiSpacer(height = 10)
}

@Composable
fun CardTextColumn(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(text = value)
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) {
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    }
    else {
        thickness
    }
    Box(
        modifier
            .fillMaxHeight()
            .width(targetThickness)
            .background(color = color)
    )
}

@Composable
fun DateTimeCardColumn(
    modifier: Modifier = Modifier,
    day: String,
    month: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Text(text = month)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EuroIconTextRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onLongClick: (() -> Unit)? = null
) {
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .conditional(onLongClick != null) {
                //this.noRippleClickable { onClick?.invoke() }
                this.combinedClickable(
                    onClick = { },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick?.invoke()
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = value)
            Icon(
                imageVector = Icons.Outlined.EuroSymbol,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .fillMaxWidth(),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/*
> OLD VERSION
@Composable
fun UsersPicker(
    usersPickup: SnapshotStateMap<Pair<Uid, DisplayName>, Boolean>,
    isMultipleSelectEnabled: Boolean = true
) {
    FlowRow(
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = 7.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        usersPickup.forEach { (pair, selected) ->
            FlowRowItemCard(pair.second, selected, onItemSelected = { status ->
                if (!isMultipleSelectEnabled) {
                    usersPickup.forEach { (user, _) -> usersPickup[user] = false }
                }
                usersPickup[pair] = status.not()
            })
        }
    }
}*/

@Composable
fun UsersPicker(
    usersMap: UsersMap,
    usersPickup: SnapshotStateMap<Uid, Boolean>,
    isMultipleSelectEnabled: Boolean = true
) {
    FlowRow(
        mainAxisAlignment = MainAxisAlignment.Center,
        mainAxisSpacing = 7.dp,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
    ) {
        usersPickup.forEach { (uid, selected) ->
            FlowRowItemCard(usersMap[uid]?.displayName ?: "Missing display name",
                selected,
                onItemSelected = { status ->
                    if (!isMultipleSelectEnabled) {
                        usersPickup.forEach { (uid, _) -> usersPickup[uid] = false }
                    }
                    usersPickup[uid] = status.not()
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlowRowItemCard(
    text: String,
    selectedStatus: Boolean,
    onItemSelected: (status: Boolean) -> Unit,
) {
    OutlinedCard(colors = CardDefaults.outlinedCardColors(containerColor = if (selectedStatus) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surface),
        onClick = { onItemSelected(selectedStatus) }) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SizedIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    size: Dp = 35.dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(size)
    )
}

@Composable
fun UserCard(
    user: User,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            CardText(
                label = stringResource(R.string.display_name),
                value = user.displayName
            )
            CardText(
                label = stringResource(R.string.email),
                value = user.email
            )
            Text(
                text = stringResource(R.string.photo),
                style = MaterialTheme.typography.labelMedium
            )
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
            )
            NamiokaiSpacer(height = 10)
            CardText(
                label = stringResource(R.string.uid),
                value = user.uid
            )


        }
    }

}

@Composable
fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_data_available),
            style = MaterialTheme.typography.headlineSmall
        )
    }

}

@Composable
fun NoResultsFound(
    modifier: Modifier = Modifier,
    label: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            modifier = Modifier.size((144).dp),
            tint = MaterialTheme.colorScheme.primary
        )
        NamiokaiSpacer(height = 16)
        Text(
            text = "Whoops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        NamiokaiSpacer(height = 8)
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        NamiokaiSpacer(height = 8)
    }
}

@Composable
fun CircleIndicatorsRow(
    count: Int,
    current: Int
) {
    Row(
        Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { iteration ->
            val color = if (current == iteration) Color.LightGray else Color.DarkGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(7.dp)

            )
        }
    }
}

@Composable
fun NamiokaiConfirmDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(onDismissRequest = onDismiss,
        title = {
            Text(text = "Are you sure?")
        },
        text = {
            Text(text = "This action cannot be undone")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        })
}

@Composable
fun NamiokaiDialog(
    title: String,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit,
    buttonsVisible: Boolean = true,
    content: @Composable () -> Unit
) {
    NamiokaiDialog(
        title = title,
        buttonsVisible = buttonsVisible,
        selectedValue = null,
        onSaveClick = { onSaveClick() },
        onDismiss = onDismiss,
    ) {
        content()
    }
}

@Composable
fun <T> NamiokaiDialog(
    title: String,
    buttonsVisible: Boolean = true,
    selectedValue: T,
    onSaveClick: (T) -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(30.dp))
                content()
                Spacer(modifier = Modifier.height(30.dp))

                if (buttonsVisible) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text(text = "Cancel")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        TextButton(onClick = { onSaveClick(selectedValue) }) {
                            Text(text = "OK")
                        }
                    }
                }
            }
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersRow(
    sourceData: List<String> = listOf(
        "All",
        "Active",
        "Inactive",
        "Blocked"
    )
) {
    LazyRow {
        items(sourceData) { item ->

            val selected = remember { mutableStateOf(false) }
            FilterChip(selected = selected.value,
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { },
                label = { Text(item) })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FiltersRowPreview() {
    NamiokaiTheme(useDarkTheme = true) {
        FiltersRow()
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NamiokaiDialogPreview() {
    NamiokaiTheme(useDarkTheme = true) {
        val status = remember { mutableStateOf(true) }

        if (status.value) {
            NamiokaiDialog(title = "Select username",
                onDismiss = { status.value = false },
                onSaveClick = { status.value = false }) {
                NamiokaiSpacer(height = 30)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    bottomSheetState: SheetState,
    content: @Composable () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        windowInsets = BottomAppBarDefaults.windowInsets,
        modifier = Modifier.padding(
            start = 10.dp,
            top = 0.dp,
            end = 10.dp,
            bottom = 10.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(
                start = 25.dp,
                top = 0.dp,
                end = 25.dp,
                bottom = 25.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge, // previous: headlineSmall
                    fontWeight = FontWeight.Bold,
                )
                IconButton(
                    onClick = onDismiss,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiElevatedCard(
    modifier: Modifier = Modifier,
    padding: Dp = 15.dp,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        onClick = onClick,
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiElevatedOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .border(
                CardDefaults.outlinedCardBorder(),
                shape = MaterialTheme.shapes.medium
            ),
        onClick = onClick,
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamiokaiDateRangePicker(
    initialSelectedStartDateMillis: Long? = null,
    initialSelectedEndDateMillis: Long? = null,
    onDismissRequest: () -> Unit = {},
    onSaveRequest: (Period) -> Unit = { _ -> },
    onResetRequest: () -> Unit = {}
) {

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialSelectedStartDateMillis,
        initialSelectedEndDateMillis = initialSelectedEndDateMillis
    )

    DatePickerDialog(modifier = Modifier.fillMaxSize(),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = true),
        dismissButton = {
            TextButton(onClick = onResetRequest) {
                Text(text = "Reset")
            }
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onSaveRequest(
                        Period(
                            Instant.fromEpochMilliseconds(state.selectedStartDateMillis ?: 0)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date,

                            Instant.fromEpochMilliseconds(state.selectedEndDateMillis ?: 0)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        )
                    )
                },
                enabled = state.selectedEndDateMillis != null
            ) {
                Text(text = "Save")
            }
        }) {

        NamiokaiSpacer(height = 25)
        DateRangePicker(
            state = state,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
inline fun <T> rememberState(crossinline producer: @DisallowComposableCalls () -> T) = remember { mutableStateOf(producer()) }

@Composable
inline fun <T> rememberState(
    key: Any?,
    crossinline producer: @DisallowComposableCalls () -> T
) = remember(key) { mutableStateOf(producer()) }

