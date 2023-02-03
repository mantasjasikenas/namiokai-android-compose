package com.github.mantasjasikenas.namiokai.di

import android.app.Application
import android.content.Context
import com.github.mantasjasikenas.namiokai.R
import com.github.mantasjasikenas.namiokai.data.repository.UsersRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.auth.AuthRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.auth.UserRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.namiokai.utils.Constants.SIGN_IN_REQUEST
import com.github.mantasjasikenas.namiokai.utils.Constants.SIGN_UP_REQUEST
import com.github.mantasjasikenas.namiokai.utils.ToastManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

    @Provides
    fun provideFirebaseAuth() = Firebase.auth


    @Provides
    fun provideFirebaseRepository(): com.github.mantasjasikenas.namiokai.data.FirebaseRepository =
        com.github.mantasjasikenas.namiokai.data.repository.FirebaseRepositoryImpl()

    @Provides
    fun provideDebtsManager(
        firebaseRepository: com.github.mantasjasikenas.namiokai.data.FirebaseRepository,
    ): DebtsManager = DebtsManager(firebaseRepository)


    @Provides
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)


    @Provides
    @Named(SIGN_IN_REQUEST)
    fun provideSignInRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(true)
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
        app: Application
    ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()


    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)


    @Provides
    fun provideNamiokaiRepository(
        firebaseRepository: com.github.mantasjasikenas.namiokai.data.FirebaseRepository
    ): com.github.mantasjasikenas.namiokai.data.UsersRepository =
        UsersRepositoryImpl(firebaseRepository = firebaseRepository)


    @Provides
    fun provideAuthRepository(
        firebaseRepository: com.github.mantasjasikenas.namiokai.data.FirebaseRepository,
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        @Named(SIGN_IN_REQUEST)
        signInRequest: BeginSignInRequest,
        @Named(SIGN_UP_REQUEST)
        signUpRequest: BeginSignInRequest,
    ): com.github.mantasjasikenas.namiokai.data.AuthRepository = AuthRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        firebaseRepository = firebaseRepository
    )


    @Provides
    fun provideUserRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient
    ): com.github.mantasjasikenas.namiokai.data.UserRepository = UserRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient
    )

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    fun provideToastManager(@ApplicationContext context: Context): ToastManager {
        return ToastManager(context)
    }


}