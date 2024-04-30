package com.github.mantasjasikenas.namiokai.di

import android.app.Application
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import com.github.mantasjasikenas.namiokai.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
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
    fun provideAuthRepository(
        usersRepository: UsersRepository,
        auth: FirebaseAuth,
        credentialManager: CredentialManager,
        getCredentialRequest: GetCredentialRequest,
        @ApplicationContext context: Context,
    ) = AuthRepository(
        usersRepository = usersRepository,
        auth = auth,
        credentialManager = credentialManager,
        getCredentialRequest = getCredentialRequest,
        context = context,
    )

    @Provides
    @Singleton
    fun provideGoogleIdOption(
        app: Application
    ): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(app.getString(R.string.default_web_client_id))
            .build()
    }

    @Provides
    @Singleton
    fun provideGetCredentialRequest(googleIdOption: GetGoogleIdOption): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }
}