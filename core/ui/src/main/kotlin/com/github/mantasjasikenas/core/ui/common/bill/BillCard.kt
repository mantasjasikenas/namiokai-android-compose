package com.github.mantasjasikenas.core.ui.common.bill

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReadMore
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.common.util.tryParse
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.resolveBillCost
import com.github.mantasjasikenas.core.ui.R
import com.github.mantasjasikenas.core.ui.common.DateTimeCardColumn
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.VerticalDivider
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SwipeBillCard(
    subtext: String,
    subtextIcon: ImageVector,
    bill: Bill,
    currentUser: User,
    usersMap: Map<String, User>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onStartToEndSwipe: () -> Unit = {},
    onEndToStartSwipe: () -> Unit = {},
    enableDismissFromEndToStart: Boolean = false,
    enableDismissFromStartToEnd: Boolean = true,
    elevated: Boolean = true,
) {
    SwipeBillCardCommon(
        modifier = modifier,
        onStartToEndSwipe = onStartToEndSwipe,
        onEndToStartSwipe = onEndToStartSwipe,
        enableDismissFromEndToStart = enableDismissFromEndToStart,
        enableDismissFromStartToEnd = enableDismissFromStartToEnd,
        content = {
            BillCard(
                subtext = subtext,
                subtextIcon = subtextIcon,
                bill = bill,
                currentUser = currentUser,
                usersMap = usersMap,
                onClick = onClick,
                elevated = elevated
            )
        }
    )
}

@Composable
fun SwipeBillCard(
    bill: Bill,
    currentUser: User,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onStartToEndSwipe: () -> Unit = {},
    onEndToStartSwipe: () -> Unit = {},
    enableDismissFromEndToStart: Boolean = false,
    enableDismissFromStartToEnd: Boolean = true,
    elevated: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    SwipeBillCardCommon(
        modifier = modifier,
        onStartToEndSwipe = onStartToEndSwipe,
        onEndToStartSwipe = onEndToStartSwipe,
        enableDismissFromEndToStart = enableDismissFromEndToStart,
        enableDismissFromStartToEnd = enableDismissFromStartToEnd,
    ) {
        BillCard(
            bill = bill,
            currentUserUid = currentUser.uid,
            onClick = onClick,
            elevated = elevated,
            content = content
        )
    }
}

@Composable
private fun SwipeBillCardCommon(
    modifier: Modifier,
    onStartToEndSwipe: () -> Unit,
    onEndToStartSwipe: () -> Unit,
    enableDismissFromEndToStart: Boolean,
    enableDismissFromStartToEnd: Boolean,
    content: @Composable (RowScope.() -> Unit)
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onStartToEndSwipe()
                    false
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onEndToStartSwipe()
                    false
                }

                else -> {
                    false
                }
            }
        },
    )

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled, SwipeToDismissBoxValue.EndToStart -> Color.Transparent
            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.secondaryContainer
        },
        label = ""
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxSize()
                    .clip(CardDefaults.elevatedShape)
                    .background(color),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReadMore,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterStart)
                )
            }
        },
        enableDismissFromEndToStart = enableDismissFromEndToStart,
        enableDismissFromStartToEnd = enableDismissFromStartToEnd,
        content = content
    )
}

@Composable
fun BillCard(
    modifier: Modifier = Modifier,
    subtext: String,
    subtextIcon: ImageVector,
    bill: Bill,
    currentUser: User,
    usersMap: Map<String, User>,
    onClick: () -> Unit,
    elevatedCardPadding: PaddingValues = PaddingValues(),
    innerPadding: PaddingValues = PaddingValues(15.dp),
    elevated: Boolean = true,
) {
    BillCard(
        bill = bill,
        currentUserUid = currentUser.uid,
        elevatedCardPadding = elevatedCardPadding,
        innerPadding = innerPadding,
        onClick = onClick,
        modifier = modifier,
        elevated = elevated
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(usersMap[bill.paymasterUid]?.photoUrl?.ifEmpty { R.drawable.profile })
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(18.dp)
            )

            NamiokaiSpacer(width = 6)

            Text(
                text = usersMap[bill.paymasterUid]?.displayName ?: "-",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        NamiokaiSpacer(height = 5)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = subtextIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            NamiokaiSpacer(width = 7)

            Text(
                text = subtext,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BillCard(
    bill: Bill,
    currentUserUid: UserUid,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevatedCardPadding: PaddingValues = PaddingValues(),
    innerPadding: PaddingValues = PaddingValues(15.dp),
    elevated: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val billCreationDateTime = remember(bill.date) {
        LocalDateTime.tryParse(bill.date) ?: Clock.System.now()
            .toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
    }

    ElevatedCard(
        modifier = modifier
            .padding(
                elevatedCardPadding
            )
            .fillMaxSize(),
        onClick = onClick,
        elevation = if (elevated) CardDefaults.elevatedCardElevation() else CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxSize()
            ) {
                NamiokaiSpacer(width = 10)

                DateTimeCardColumn(
                    day = billCreationDateTime.date.dayOfMonth.toString(),
                    month = billCreationDateTime.month.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                )

                NamiokaiSpacer(width = 20)
                VerticalDivider(modifier = Modifier.height(60.dp))
                NamiokaiSpacer(width = 20)

                Column(modifier = Modifier.weight(1f)) {
                    content()
                }

                NamiokaiSpacer(width = 30)

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = bill.resolveBillCost(currentUserUid),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 18.sp
                            )
                        )
                        Icon(
                            imageVector = Icons.Outlined.EuroSymbol,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                NamiokaiSpacer(width = 10)
            }
        }
    }
}
