package com.github.mantasjasikenas.feature.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.database.Notification
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.noRippleClickable
import com.github.mantasjasikenas.core.ui.component.NoResultsFound
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
) {
    val notificationsUiState by notificationsViewModel.notificationsUiState.collectAsState()

    if (notificationsUiState.notificationList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            NoResultsFound(
                label = "No notifications was found.",
            )
        }
        return
    }


    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(notificationsUiState.notificationList) { notification ->
            NotificationItem(
                notification = notification,
                onClick = {
                    //navController.navigate("notification/${notification.id}")
                }
            )
        }


    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val instant = Instant.fromEpochMilliseconds(notification.date)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val date = if (dateTime.date == Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
    ) {
        // show 18:07 instead of 18:7
        "${dateTime.hour}:${
            dateTime.minute.toString()
                .padStart(
                    2,
                    '0'
                )
        }"
    }
    else {
        "${
            dateTime.month.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
        } ${dateTime.dayOfMonth}"
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
            .noRippleClickable { onClick() },
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.ic_launcher_foreground)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                loading = {
                    CircularProgressIndicator()
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    //.background(MaterialTheme.colorScheme.primary)
                    .padding(5.dp)
                    .size(25.dp)
            )
            NamiokaiSpacer(width = 20)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = notification.text,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W400
                )
            }
            NamiokaiSpacer(width = 40)

            Column(
                verticalArrangement = Arrangement.Top,
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W400
                )
            }

        }

    }
}


