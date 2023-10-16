package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import androidx.room.Room
import com.github.mantasjasikenas.core.data.repository.AccentColorRepositoryImpl
import com.github.mantasjasikenas.core.data.repository.NotificationsRepositoryImpl
import com.github.mantasjasikenas.core.database.AccentColorDao
import com.github.mantasjasikenas.core.database.AppDatabase
import com.github.mantasjasikenas.core.database.NotificationDao
import com.github.mantasjasikenas.core.domain.repository.AccentColorRepository
import com.github.mantasjasikenas.core.domain.repository.NotificationsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideNotificationsDao(appDatabase: AppDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Provides
    fun provideAccentColorDao(appDatabase: AppDatabase): AccentColorDao {
        return appDatabase.accentColorDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context):
            AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesNotificationsRepository(notificationDao: NotificationDao): NotificationsRepository =
        NotificationsRepositoryImpl(notificationDao)

    @Provides
    fun providesAccentColorRepository(accentColorDao: AccentColorDao): AccentColorRepository =
        AccentColorRepositoryImpl(accentColorDao)
}