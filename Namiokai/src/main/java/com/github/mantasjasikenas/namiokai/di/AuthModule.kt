package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.presentation.sign_in.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideGoogleAuthUiClient(
        @ApplicationContext
        context: Context,
        oneTapClient: SignInClient,
        firebaseRepository: FirebaseRepository
    ) = GoogleAuthUiClient(
        context = context,
        oneTapClient = oneTapClient,
        firebaseRepository = firebaseRepository
    )

    @Provides
    fun provideSignInClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)


}