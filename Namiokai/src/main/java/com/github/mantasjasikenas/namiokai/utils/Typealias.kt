package com.github.mantasjasikenas.namiokai.utils

import com.github.mantasjasikenas.namiokai.model.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult

typealias SignOutResponse = Response<Boolean>
typealias OneTapSignInResponse = Response<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<Boolean>