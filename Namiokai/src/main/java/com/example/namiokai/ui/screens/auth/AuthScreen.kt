package com.example.namiokai.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.namiokai.R
import com.example.namiokai.model.Response
import com.example.namiokai.ui.main.MainViewModel
import com.example.namiokai.ui.navigation.Screen
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    navController: NavHostController
) {

    val isVisible = remember {
        mutableStateOf(viewModel.isUserAuthenticated)
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

        AnimatedVisibility(visible = isVisible.value.not()) {
            Button(onClick = {
                viewModel.oneTapSignIn()
            }, shape = RoundedCornerShape(4.dp)) {
                Icon(imageVector = Icons.Outlined.Login, contentDescription = null)
                Text(
                    text = stringResource(R.string.sign_in_with_google),
                    modifier = Modifier.padding(6.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedVisibility(visible = isVisible.value) {
            Button(onClick = {
                viewModel.signOut()
            }, shape = RoundedCornerShape(4.dp)) {
                Icon(imageVector = Icons.Outlined.Logout, contentDescription = null)
                Text(
                    text = stringResource(R.string.sign_out),
                    modifier = Modifier.padding(6.dp),
                )
            }
        }


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


        when (val oneTapSignInResponse = viewModel.oneTapSignInResponse) {
            is Response.Loading -> {}
            is Response.Success -> oneTapSignInResponse.data?.let {
                LaunchedEffect(it) {
                    val intent =
                        IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
                    launcher.launch(intent)
                }
            }

            is Response.Failure -> LaunchedEffect(Unit) {
                print(oneTapSignInResponse.e)
            }
        }

        when (val signInWithGoogleResponse = viewModel.signInWithGoogleResponse) {
            is Response.Loading -> {}
            is Response.Success -> signInWithGoogleResponse.data?.let { signedIn ->
                LaunchedEffect(signedIn) {
                    if (signedIn) {
                        navController.navigate(Screen.Debts.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        mainViewModel.getCurrentUserDetails()

                    }
                }
            }

            is Response.Failure -> LaunchedEffect(Unit) {
                print(signInWithGoogleResponse.e)
            }
        }
    }

}

