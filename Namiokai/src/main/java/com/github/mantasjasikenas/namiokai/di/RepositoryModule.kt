package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.data.repository.FirebaseRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.UsersRepositoryImpl
import com.github.mantasjasikenas.namiokai.data.repository.preferences.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseRepository(): FirebaseRepository =
        FirebaseRepositoryImpl()


    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideNamiokaiRepository(
        firebaseRepository: FirebaseRepository
    ): UsersRepository =
        UsersRepositoryImpl(firebaseRepository = firebaseRepository)
}