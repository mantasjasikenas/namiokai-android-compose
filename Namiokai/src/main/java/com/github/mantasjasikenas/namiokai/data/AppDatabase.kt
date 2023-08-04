package com.github.mantasjasikenas.namiokai.data

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Notification::class, AccentColor::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    abstract fun accentColorDao(): AccentColorDao
}