package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext
        context: Context,
        oneTapClient: SignInClient,
        usersRepository: UsersRepository
    ) = GoogleAuthUiClient(
        context = context,
        oneTapClient = oneTapClient,
        usersRepository = usersRepository
    )

    @Provides
    fun provideSignInClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)


}