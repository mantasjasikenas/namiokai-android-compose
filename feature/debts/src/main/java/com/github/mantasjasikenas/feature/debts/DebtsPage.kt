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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlin.math.absoluteValue

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
internal fun DebtsPage(
    spacesDebts: List<SpaceDebts>,
    usersMap: UsersMap,
    periodOffset: Int,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
    onPeriodOffsetUpdate: (Int) -> Unit,
) {
    if (spacesDebts.isEmpty()) {
        NoResultsFound(label = "No spaces found.\nPlease create a space first to add a bill.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp)
            .verticalScroll(rememberScrollState()), // remove if lazy column is used
    ) {
        PeriodSelection(
            modifier = Modifier.padding(bottom = 24.dp),
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
            Column {
                spacesDebts.forEach { spaceDebt ->
                    val usersDebts = spaceDebt.debts

                    SpaceDebtHeader(spaceDebt = spaceDebt)

                    if (usersDebts.isEmpty()) {
                        NoDebtsFound()
                        return@forEach
                    }

                    SpaceDebtsContainer(usersDebts = usersDebts, usersMap = usersMap)
                }
            }
        }
    }
}

@Composable
private fun PeriodSelection(
    modifier: Modifier = Modifier,
    onPeriodOffsetUpdate: (Int) -> Unit,
    periodOffset: Int
) {
    Card(
        modifier = modifier,
        border = CardDefaults.outlinedCardBorder(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            IconButton(
                onClick = { onPeriodOffsetUpdate(-1) },
            ) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.AutoMirrored.Outlined.ArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selected period",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (periodOffset != 0) {
                        Icon(
                            imageVector = when {
                                periodOffset > 0 -> Icons.Outlined.Add
                                else -> Icons.Outlined.Remove
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${periodOffset.absoluteValue} periods",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Current period",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.clickable(
                        enabled = periodOffset != 0,
                    ) { onPeriodOffsetUpdate(periodOffset * -1) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Restore",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            IconButton(
                onClick = { onPeriodOffsetUpdate(1) },
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
private fun SpaceDebtsContainer(
    usersDebts: List<Pair<String, Map<String, List<DebtBill>>>>,
    usersMap: UsersMap
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        usersDebts.forEach { (user, debts) ->
            if (debts.isEmpty() || usersMap[user] == null) {
                return@forEach
            }

            DebtorCard(
                debtorUser = usersMap[user]!!,
                userDebts = debts,
                usersMap = usersMap
            )

        }
    }
}

@Composable
private fun SpaceDebtHeader(spaceDebt: SpaceDebts) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            val spaceText = buildAnnotatedString {
                append("Space ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append(spaceDebt.space.spaceName)
                }
            }

            Text(
                text = spaceText,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )

            Text(
                text = "${spaceDebt.period}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
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
) {
    val expandedState = remember { mutableStateOf(false) }
    val expandAll = remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.animateContentSize(),
        onClick = { expandedState.value = !expandedState.value },
    ) {
        Row(
            modifier = Modifier
                .sizeIn(minHeight = 48.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(debtorUser.photoUrl.ifEmpty { R.drawable.profile })
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
            )

            Row(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.debtor),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = debtorUser.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp),
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
                text = "No debts was found. You are all good!",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}