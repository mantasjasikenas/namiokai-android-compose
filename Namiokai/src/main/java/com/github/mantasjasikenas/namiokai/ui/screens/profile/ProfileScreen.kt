package com.github.mantasjasikenas.namiokai.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.ui.common.NamiokaiSpacer
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel

@Composable
fun ProfileScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val mainUiState by mainViewModel.mainUiState.collectAsState()
    val currentUser = mainUiState.currentUser

    ProfileScreenContent(user = currentUser)


}

@Composable
private fun ProfileScreenContent(
    user: User
) {
    val context = LocalContext.current
    val showToast = {
        Toast.makeText(context, "ARE YOU CRAZY!?", Toast.LENGTH_SHORT).show()
        Toast.makeText(context, "Under construction!", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
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

        Text(text = user.displayName, style = MaterialTheme.typography.titleLarge)
        Text(text = user.email)
        NamiokaiSpacer(height = 32)

        Button(onClick = { showToast() }) {
            Text(text = "Change display name")
        }
        Button(onClick = { showToast() }) {
            Text(text = "Change profile picture")
        }
        Button(onClick = { showToast() }) {
            Text(text = "Sign out")
        }


        NamiokaiSpacer(height = 64)
    }

}