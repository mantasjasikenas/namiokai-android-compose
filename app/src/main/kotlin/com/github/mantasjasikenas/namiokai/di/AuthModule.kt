package com.github.mantasjasikenas.namiokai.di

import android.app.Application
import android.content.Context
import com.github.mantasjasikenas.core.common.util.Constants.SIGN_IN_REQUEST
import com.github.mantasjasikenas.core.common.util.Constants.SIGN_UP_REQUEST
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import com.github.mantasjasikenas.namiokai.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext
        context: Context,
        oneTapClient: SignInClient,
        usersRepository: UsersRepository,
        @Named(SIGN_IN_REQUEST)
        signInRequest: BeginSignInRequest,
    ) = AuthRepository(
        context = context,
        oneTapClient = oneTapClient,
        usersRepository = usersRepository,
        signInRequest = signInRequest
    )

    @Provides
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Named(SIGN_IN_REQUEST)
    fun provideSignInRequest(
        app: Application,
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .setAutoSelectEnabled(true)
        .build()

    @Provides
    @Named(SIGN_UP_REQUEST)
    fun provideSignUpRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    @Provides
    fun provideGoogleSignInOptions(
        app: Application,
    ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(
        app,
        options
    )


}