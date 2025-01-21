package com.github.mantasjasikenas.namiokai.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.common.util.Constants.GITHUB_URL
import com.github.mantasjasikenas.core.ui.common.rememberState
import com.github.mantasjasikenas.core.ui.component.ReportBugDialog
import com.github.mantasjasikenas.feature.admin.navigation.AdminPanelRoute
import com.github.mantasjasikenas.feature.notifications.navigation.NotificationsRoute
import com.github.mantasjasikenas.feature.profile.navigation.ProfileRoute
import com.github.mantasjasikenas.feature.settings.navigation.SettingsRoute
import com.github.mantasjasikenas.feature.test.navigation.TestRoute
import com.github.mantasjasikenas.namiokai.R

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    modifier: Modifier = Modifier,
    showTopBar: Boolean,
    adminModeEnabled: Boolean,
    title: String? = null,
    canNavigateBack: Boolean,
    photoUrl: String,
    navigateUp: () -> Unit,
    navigateScreen: (Any) -> Unit
) {
    Surface {
        AnimatedVisibility(
            visible = showTopBar,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors()
                    .copy(
                        containerColor = Color.Transparent,
                    ),
                title = {
                    title?.let {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                modifier = modifier,
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_button)
                            )
                        }
                    }
                },
                actions = {
                    TopBarDropdownMenu(
                        navigateScreen = navigateScreen,
                        adminModeEnabled = adminModeEnabled,
                        photoUrl = photoUrl
                    )
                })
        }
    }
}

@Composable
private fun TopBarDropdownMenu(
    navigateScreen: (Any) -> Unit,
    adminModeEnabled: Boolean = false,
    photoUrl: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    var reportBugDialog by rememberState { false }
    val context = LocalContext.current
    val urlIntent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(GITHUB_URL)
        )
    }


    IconButton(onClick = { expanded = true }) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photoUrl.ifEmpty { R.drawable.profile })
                .crossfade(true)
                .build(),
            contentDescription = null,
            loading = {
                CircularProgressIndicator()
            },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(28.dp) // 31.dp
                .border(
                    Dp.Hairline,
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }) {

        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings_menu_label)) },
            onClick = {
                navigateScreen(SettingsRoute)
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = null
                )
            })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.profile_label)) },
            onClick = {
                navigateScreen(ProfileRoute)
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = null
                )
            })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.notifications_menu_label)) },
            onClick = {
                navigateScreen(NotificationsRoute)
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = null
                )
            })
        DropdownMenuItem(
            text = { Text(text = "New issue") },
            onClick = {
                reportBugDialog = true
                expanded = false
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.BugReport,
                    contentDescription = null
                )
            })

        AnimatedVisibility(visible = adminModeEnabled) {
            Column {
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.admin_panel_menu_label)) },
                    onClick = {
                        navigateScreen(AdminPanelRoute)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.AdminPanelSettings,
                            contentDescription = null
                        )
                    })
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.github)) },
                    onClick = {
                        expanded = false
                        context.startActivity(urlIntent)

                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Update,
                            contentDescription = null
                        )
                    })
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.debug_menu_label)) },
                    onClick = {
                        navigateScreen(TestRoute)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.BugReport,
                            contentDescription = null
                        )
                    })
            }
        }
    }

    ReportBugDialog(
        dialogState = reportBugDialog,
        onDismiss = { reportBugDialog = false })
}