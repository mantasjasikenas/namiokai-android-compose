@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.github.mantasjasikenas.feature.search_users

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults.InputFieldHeight
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.ui.R
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.component.NoResultsFound

@Composable
fun SearchUsersRoute(
    onNavigateBack: (users: List<String>) -> Unit,
) {
    SearchUsersScreen(
        onNavigateBack = onNavigateBack,
    )
}


@Composable
fun SearchUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchUsersViewModel = hiltViewModel(),
    onNavigateBack: (users: List<String>) -> Unit,
) {
    val searchText by viewModel.searchText.collectAsState()
    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    val selectedUsers by viewModel.selectedUsers.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            SearchBox(
                searchText = searchText,
                onSearchTextChange = viewModel::onSearchTextChange
            )

            if (isSearching) {
                NamiokaiCircularProgressIndicator()
                return
            }

            FilteredUsersColumn(
                filteredUsers = filteredUsers,
                selectedUsers = selectedUsers,
                onSelectedUserUpdate = viewModel::onSelectedUserUpdate,
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            visible = selectedUsers.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            FilledTonalButton(
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    )
                    .fillMaxWidth(),
                onClick = {
                    onNavigateBack(selectedUsers.map { it.uid })
                },
                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
            ) {
                Text(text = "Continue • ${selectedUsers.size}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBox(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .sizeIn(
                minWidth = 360.dp,
                maxWidth = 720.dp,
                minHeight = InputFieldHeight,
            )
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        value = searchText,
        shape = ShapeDefaults.ExtraLarge,
        onValueChange = onSearchTextChange,
        placeholder = { Text("Search and select users") },
        singleLine = true,
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    )
}

@Composable
private fun SelectedUsersFlowRow(
    modifier: Modifier = Modifier,
    selectedUsers: Set<User>,
    onUserRemoved: (User) -> Unit
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        selectedUsers.forEach { user ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(48.dp)
                            .clickable {
                                onUserRemoved(user)
                            },
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.photoUrl.ifEmpty { R.drawable.profile })
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        loading = {
                            CircularProgressIndicator()
                        },
                        contentScale = ContentScale.Crop,
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(4.dp)
                            .size(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier
                                .size(12.dp)
                        )
                    }

                }
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
private fun FilteredUsersColumn(
    filteredUsers: List<User>,
    selectedUsers: Set<User>,
    onSelectedUserUpdate: (User, Boolean) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        if (selectedUsers.isNotEmpty()) {
            item(key = "selectedUsers") {
                SelectedUsersFlowRow(
                    modifier = Modifier.animateItem(),
                    selectedUsers = selectedUsers,
                    onUserRemoved = { user ->
                        onSelectedUserUpdate(user, false)
                    }
                )
            }
        }

        if (filteredUsers.isEmpty()) {
            item(key = "noResultsFound") {
                NoResultsFound(label = "No users found")
            }
        }

        items(items = filteredUsers, key = { it.uid }) { user ->
            val isSelected = selectedUsers.any { it.uid == user.uid }

            ListItem(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable {
                        onSelectedUserUpdate(user, !isSelected)
                    },
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                headlineContent = { Text(user.displayName) },
                supportingContent = { Text(user.email) },
                leadingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { selected ->
                                onSelectedUserUpdate(user, selected)
                            },
                        )

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.photoUrl.ifEmpty { R.drawable.profile })
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            loading = {
                                CircularProgressIndicator()
                            },
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(36.dp)
                        )
                    }
                },
            )
        }
    }
}