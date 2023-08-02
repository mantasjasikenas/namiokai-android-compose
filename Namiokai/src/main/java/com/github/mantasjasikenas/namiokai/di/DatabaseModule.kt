package com.github.mantasjasikenas.namiokai.di

import android.content.Context
import androidx.room.Room
import com.github.mantasjasikenas.namiokai.data.NotificationDao
import com.github.mantasjasikenas.namiokai.data.NotificationsDatabase
import com.github.mantasjasikenas.namiokai.data.NotificationsRepository
import com.github.mantasjasikenas.namiokai.data.NotificationsRepositoryImpl
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
    fun provideNotificationsDao(appDatabase: NotificationsDatabase): NotificationDao {
        return appDatabase.notificationDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context):
            NotificationsDatabase {
        return Room.databaseBuilder(
            appContext,
            NotificationsDatabase::class.java,
            "notification_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesNotificationsRepository(notificationDao: NotificationDao): NotificationsRepository =
        NotificationsRepositoryImpl(notificationDao)
}