package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.database.Notification
import com.github.mantasjasikenas.core.database.NotificationDao
import com.github.mantasjasikenas.core.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow

class NotificationsRepositoryImpl(private val notificationDao: NotificationDao) :
    NotificationsRepository {
    override fun getAllNotificationsStream(): Flow<List<Notification>> =
        notificationDao.getAllNotifications()

    override fun getNotificationStream(id: Int): Flow<Notification?> =
        notificationDao.getNotification(id)

    override suspend fun insertNotification(notification: Notification) =
        notificationDao.insert(notification)

    override suspend fun deleteNotification(notification: Notification) =
        notificationDao.delete(notification)

    override suspend fun updateNotification(notification: Notification) =
        notificationDao.update(notification)
}

