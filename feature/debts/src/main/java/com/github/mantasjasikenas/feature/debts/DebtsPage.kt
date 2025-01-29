package com.github.mantasjasikenas.feature.debts

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowLeft
import androidx.compose.material.icons.automirrored.outlined.ArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.UsersMap
import com.github.mantasjasikenas.core.domain.model.debts.DebtBill
import com.github.mantasjasikenas.core.domain.model.debts.SpaceDebts
import com.github.mantasjasikenas.core.ui.common.NamiokaiUiTokens
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlin.math.absoluteValue

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
internal fun DebtsPage(
    spacesDebts: List<SpaceDebts>,
    usersMap: UsersMap,
    periodOffset: Int,
    onPeriodOffsetUpdate: (Int) -> Unit,
) {
    if (spacesDebts.isEmpty()) {
        NoResultsFound(label = stringResource(R.string.no_spaces_found))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NamiokaiUiTokens.PageContentPadding)
            .verticalScroll(rememberScrollState()), // remove if lazy column is used
    ) {
        PeriodSelection(
            modifier = Modifier.padding(bottom = 16.dp),
            onPeriodOffsetUpdate = onPeriodOffsetUpdate,
            periodOffset = periodOffset
        )

        AnimatedContent(
            targetState = periodOffset,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> -height } + fadeOut())
                } else {
                    (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> height } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                spacesDebts.forEach { spaceDebt ->
                    SpaceDebtColumn(spaceDebt = spaceDebt, usersMap = usersMap)
                }
            }
        }
    }
}

@Composable
private fun PeriodSelection(
    modifier: Modifier = Modifier, onPeriodOffsetUpdate: (Int) -> Unit, periodOffset: Int
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            IconButton(
                onClick = { onPeriodOffsetUpdate(-1) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.AutoMirrored.Outlined.ArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (periodOffset != 0) {
                        Icon(
                            imageVector = when {
                                periodOffset > 0 -> Icons.Default.Add
                                else -> Icons.Default.Remove
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.periods, periodOffset.absoluteValue),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.current_period),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val enabledReset = periodOffset != 0

                Row(
                    modifier = Modifier.clickable(
                        enabled = enabledReset,
                    ) { onPeriodOffsetUpdate(periodOffset * -1) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Outlined.Restore,
                        contentDescription = null,
                        tint = if (enabledReset) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        }
                    )
                    Text(
                        color = if (enabledReset) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        },
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            IconButton(
                onClick = { onPeriodOffsetUpdate(1) }, colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.AutoMirrored.Outlined.ArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SpaceDebtColumn(
    spaceDebt: SpaceDebts, usersMap: UsersMap
) {
    val usersDebts = spaceDebt.debts.toList()

    val header = buildAnnotatedString {
        append(spaceDebt.space.spaceName)
        append("\n")
        withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
            append("${spaceDebt.period}")
        }
    }

    Column {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            text = header,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
        )

        ElevatedCard {
            if (usersDebts.isEmpty()) {
                NoDebtsFound()
            } else {
                SpaceDebtsContainer(usersDebts = usersDebts, usersMap = usersMap)
            }
        }
    }
}

@Composable
private fun SpaceDebtsContainer(
    usersDebts: List<Pair<String, Map<String, List<DebtBill>>>>, usersMap: UsersMap
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
    ) {
        usersDebts.forEachIndexed { index, (user, debts) ->
            if (debts.isEmpty() || usersMap[user] == null) {
                return@forEachIndexed
            }

            DebtorCard(
                debtorUser = usersMap[user]!!,
                userDebts = debts,
                usersMap = usersMap,
                bottomDividerVisible = index != usersDebts.size - 1
            )
        }
    }
}

@Composable
private fun DebtorCard(
    modifier: Modifier = Modifier,
    debtorUser: User,
    userDebts: Map<UserUid, List<DebtBill>>,
    usersMap: UsersMap,
    bottomDividerVisible: Boolean = true
) {
    val expandedState = remember { mutableStateOf(false) }
    val expandAll = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .animateContentSize()
            .clickable { expandedState.value = !expandedState.value },
    ) {
        Row(
            modifier = Modifier
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(debtorUser.photoUrl.ifEmpty { R.drawable.profile }).crossfade(true)
                    .build(),
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(22.dp)
            )

            Text(
                text = debtorUser.displayName,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (bottomDividerVisible) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }

    if (expandedState.value) {
        DebtsDetailsSheet(
            debtorUser = debtorUser,
            userDebts = userDebts,
            usersMap = usersMap,
            expandedState = expandedState,
            expandAll = expandAll
        )
    }
}

@Composable
private fun NoDebtsFound() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )

            Text(
                text = stringResource(R.string.no_debts_you_are_all_good),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}