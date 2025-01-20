package com.github.mantasjasikenas.feature.debts.pages

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import com.github.mantasjasikenas.feature.debts.DebtsDetailsSheet
import com.github.mantasjasikenas.feature.debts.R

@Composable
internal fun DebtsPage(
    spacesDebts: List<SpaceDebts>,
    usersMap: UsersMap,
    onPeriodReset: () -> Unit,
    onPeriodUpdate: (Period) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp)
            .verticalScroll(rememberScrollState()), // remove if lazy column is used
    ) {
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