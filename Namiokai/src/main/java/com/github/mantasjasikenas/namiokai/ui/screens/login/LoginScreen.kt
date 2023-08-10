package com.github.mantasjasikenas.namiokai.ui.screens.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mantasjasikenas.namiokai.presentation.sign_in.SignInContent
import com.github.mantasjasikenas.namiokai.presentation.sign_in.SignInViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onSuccessfulLogin: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val lifecycleScope = rememberCoroutineScope()
    val googleAuthUiClient = viewModel.googleAuthUiClient


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            Toast.makeText(
                context,
                "Sign in successful",
                Toast.LENGTH_LONG
            ).show()
            onSuccessfulLogin()
        }
    }

    SignInContent(
        state = state,
        onSignInClick = {
            lifecycleScope.launch {
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }
    )
}