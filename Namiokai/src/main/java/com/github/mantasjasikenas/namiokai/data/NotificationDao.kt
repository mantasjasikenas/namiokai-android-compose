package com.github.mantasjasikenas.namiokai.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * from notifications ORDER BY date DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * from notifications WHERE id = :id")
    fun getNotification(id: Int): Flow<Notification>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification)

    @Update
    suspend fun update(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)
}

