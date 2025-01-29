package com.github.mantasjasikenas.feature.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.ui.common.NamiokaiCircularProgressIndicator
import com.github.mantasjasikenas.core.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.core.ui.common.NamiokaiUiTokens

@Composable
fun ProfileRoute() {
    ProfileScreen()
}

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileUiState by profileViewModel.profileUiState.collectAsStateWithLifecycle()

    when (profileUiState) {
        ProfileUiState.Loading -> {
            NamiokaiCircularProgressIndicator()
        }

        is ProfileUiState.Success -> {
            ProfileScreenContent(
                user = (profileUiState as ProfileUiState.Success).currentUser,
                onLogout = profileViewModel::logout
            )
        }
    }
}

@Composable
private fun ProfileScreenContent(
    user: User,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NamiokaiUiTokens.PageContentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                .size(160.dp)
                .border(
                    Dp.Hairline,
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
        )
        NamiokaiSpacer(height = 32)

        Text(
            text = user.displayName,
            style = MaterialTheme.typography.titleLarge
        )
        Text(text = user.email)

        NamiokaiSpacer(height = 32)

        Button(onClick = onLogout) {
            Text(text = stringResource(R.string.sign_out))
        }

        NamiokaiSpacer(height = 64)
    }

}