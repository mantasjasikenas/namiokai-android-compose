package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.database.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {
    /**
     * Retrieve all the Notifications from the the given data source.
     */
    fun getAllNotificationsStream(): Flow<List<Notification>>

    /**
     * Retrieve an Notification from the given data source that matches with the [id].
     */
    fun getNotificationStream(id: Int): Flow<Notification?>

    /**
     * Insert Notification in the data source
     */
    suspend fun insertNotification(notification: Notification)

    /**
     * Delete Notification from the data source
     */
    suspend fun deleteNotification(notification: Notification)

    /**
     * Update Notification in the data source
     */
    suspend fun updateNotification(notification: Notification)
}

