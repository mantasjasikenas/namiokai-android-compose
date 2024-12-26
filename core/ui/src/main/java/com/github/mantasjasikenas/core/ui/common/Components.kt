package com.github.mantasjasikenas.core.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.github.mantasjasikenas.core.common.util.Uid
import com.github.mantasjasikenas.core.domain.model.UsersMap
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun PagesFlowRow(
    pages: List<String>,
    currentPage: Int,
    onPageClick: (Int) -> Unit = {}
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(
            0.dp,
            Alignment.Start
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(pages) {
            FilterChip(
                selected = it == pages[currentPage],
                border = null,
                shape = CircleShape,
                onClick = { onPageClick(pages.indexOf(it)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer

                ),
                label = { Text(text = it) })
        }
    }
}

@Composable
fun FloatingAddButton(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.End,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = horizontalAlignment
    ) {
        FloatingActionButton(
            modifier = Modifier.padding(all = 15.dp),
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
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
    } else {
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

@Composable
fun EuroIconTextRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onLongClick: (() -> Unit)? = null,
    labelTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    valueTextStyle: TextStyle = LocalTextStyle.current,
    iconSize: Dp = 16.dp,
) {
    IconTextRow(
        modifier = modifier,
        label = label,
        value = value,
        onLongClick = onLongClick,
        labelTextStyle = labelTextStyle,
        valueTextStyle = valueTextStyle,
        iconSize = iconSize,
        icon = Icons.Outlined.EuroSymbol
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconTextRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onLongClick: (() -> Unit)? = null,
    labelTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    valueTextStyle: TextStyle = LocalTextStyle.current,
    iconSize: Dp = 16.dp,
    icon: ImageVector
) {
    TextRow(
        modifier = modifier,
        label = label,
        value = value,
        onLongClick = onLongClick,
        labelTextStyle = labelTextStyle,
        valueTextStyle = valueTextStyle,
        endContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .fillMaxWidth(),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onLongClick: (() -> Unit)? = null,
    labelTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    valueTextStyle: TextStyle = LocalTextStyle.current,
    endContent: (@Composable () -> Unit)? = null
) {
    val haptics = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .conditional(onLongClick != null) {
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
            style = labelTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.padding(start = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = value,
                style = valueTextStyle,
            )

            endContent?.invoke()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UsersPicker(
    usersMap: UsersMap,
    usersPickup: SnapshotStateMap<Uid, Boolean>,
    isMultipleSelectEnabled: Boolean = true
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            7.dp,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.Center
    ) {
        usersPickup.forEach { (uid, selected) ->
            FlowRowItemCard(
                usersMap[uid]?.displayName ?: "Missing display name",
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

@Composable
private fun FlowRowItemCard(
    text: String,
    selectedStatus: Boolean,
    onItemSelected: (status: Boolean) -> Unit,
) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = if (selectedStatus) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.surface),
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
        contentWindowInsets = { BottomAppBarDefaults.windowInsets },
        modifier = Modifier
            .padding(
                start = 10.dp,
                top = 0.dp,
                end = 10.dp,
                bottom = 10.dp
            ),
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 25.dp,
                    top = 0.dp,
                    end = 25.dp,
                    bottom = 25.dp
                )
                .verticalScroll(rememberScrollState())
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
fun NamiokaiDateRangePicker(
    initialSelectedStartDateMillis: Long? = null,
    initialSelectedEndDateMillis: Long? = null,
    onDismissRequest: () -> Unit = {},
    onSaveRequest: (com.github.mantasjasikenas.core.domain.model.Period) -> Unit = { _ -> },
    onResetRequest: () -> Unit = {}
) {

    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialSelectedStartDateMillis,
        initialSelectedEndDateMillis = initialSelectedEndDateMillis
    )

    DatePickerDialog(
        modifier = Modifier.fillMaxSize(),
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
                        com.github.mantasjasikenas.core.domain.model.Period(
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
fun NamiokaiCircularProgressIndicator(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 22.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(45.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = 5.dp
        )
    }
}

@Composable
inline fun <T> rememberState(crossinline producer: @DisallowComposableCalls () -> T) =
    remember { mutableStateOf(producer()) }

@Composable
inline fun <T> rememberState(
    key: Any?,
    crossinline producer: @DisallowComposableCalls () -> T
) = remember(key) { mutableStateOf(producer()) }

