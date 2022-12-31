package com.example.namiokai.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.namiokai.R
import com.example.namiokai.model.Response
import com.example.namiokai.utils.Constants.SIGN_IN_WITH_GOOGLE
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {

    Column() {
        AuthContent(
            oneTapSignIn = {
                viewModel.oneTapSignIn()
            }
        )

        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    try {
                        val credentials =
                            viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                        val googleIdToken = credentials.googleIdToken
                        val googleCredentials =
                            GoogleAuthProvider.getCredential(googleIdToken, null)
                        viewModel.signInWithGoogle(googleCredentials)
                    } catch (it: ApiException) {
                        print(it)
                    }
                }
            }

        fun launch(signInResult: BeginSignInResult) {
            val intent =
                IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
            launcher.launch(intent)
        }

        OneTapSignIn(
            launch = {
                launch(it)
            }
        )

        SignInWithGoogle(
            navigateToHomeScreen = { signedIn ->
                if (signedIn) {

                }
            }
        )
    }
}

@Composable
fun AuthContent(
    oneTapSignIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SignInButton(
            onClick = oneTapSignIn
        )
    }
}

@Composable
fun OneTapSignIn(
    viewModel: AuthViewModel = hiltViewModel(),
    launch: (result: BeginSignInResult) -> Unit
) {
    when (val oneTapSignInResponse = viewModel.oneTapSignInResponse) {
        is Response.Loading -> {}
        is Response.Success -> oneTapSignInResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }

        is Response.Failure -> LaunchedEffect(Unit) {
            print(oneTapSignInResponse.e)
        }
    }
}

@Composable
fun SignInButton(
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.padding(bottom = 48.dp),
        shape = RoundedCornerShape(6.dp),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.ic_launcher_foreground
            ),
            contentDescription = null
        )
        Text(
            text = SIGN_IN_WITH_GOOGLE,
            modifier = Modifier.padding(6.dp),
            fontSize = 18.sp
        )
    }
}

@Composable
fun SignInWithGoogle(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToHomeScreen: (signedIn: Boolean) -> Unit
) {
    when (val signInWithGoogleResponse = viewModel.signInWithGoogleResponse) {
        is Response.Loading -> {}
        is Response.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToHomeScreen(signedIn)
            }
        }

        is Response.Failure -> LaunchedEffect(Unit) {
            print(signInWithGoogleResponse.e)
        }
    }
}