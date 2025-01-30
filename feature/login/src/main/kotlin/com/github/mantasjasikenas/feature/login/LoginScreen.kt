package com.github.mantasjasikenas.feature.login

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.core.common.util.Constants

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onSuccessfulLogin: () -> Unit
) {
    LoginScreen(
        viewModel = viewModel,
        onSuccessfulLogin = onSuccessfulLogin
    )
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onSuccessfulLogin: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            onSuccessfulLogin()
        }
    }

    SignInContent(
        loginInProgress = state.isLoading,
        onGoogleSignInClick = {
            viewModel.loginWithGoogle(context)
        }
    )
}

@Composable
fun SignInContent(
    loginInProgress: Boolean,
    onGoogleSignInClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 80.dp)
                    .align(Alignment.TopCenter)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(120.dp)

                )
                Text(
                    text = stringResource(R.string.sign_in_to_continue),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }


            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(
                    6.dp,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GoogleLoginButton(
                    onClick = onGoogleSignInClick,
                    loginInProgress = loginInProgress,
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.clickable {
                        uriHandler.openUri(Constants.GITHUB_URL)

                    },
                    text = stringResource(R.string.namiokai_corporation),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.all_rights_reserved),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }

        }
    }
}

@Composable
private fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    loginInProgress: Boolean = false,
) {
    ElevatedButton(
        modifier = modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing,
            ),
        ),
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        border = ButtonDefaults.outlinedButtonBorder(),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = stringResource(R.string.sign_in_with_google))

        if (loginInProgress) {
            Spacer(modifier = Modifier.width(16.dp))

            CircularProgressIndicator(
                modifier = Modifier
                    .height(16.dp)
                    .width(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}