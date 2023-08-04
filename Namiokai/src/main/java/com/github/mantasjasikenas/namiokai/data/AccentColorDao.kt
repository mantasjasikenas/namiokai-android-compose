package com.github.mantasjasikenas.namiokai.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccentColorDao {

    @Query("SELECT * from accent_colors ORDER BY pinned DESC, date DESC")
    fun getAllCustomColors(): Flow<List<AccentColor>>

    @Query("SELECT * from accent_colors WHERE id = :id")
    fun getCustomColor(id: Int): Flow<AccentColor>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(accentColor: AccentColor)

    @Update
    suspend fun update(accentColor: AccentColor)

    @Delete
    suspend fun delete(accentColor: AccentColor)

    @Query("DELETE FROM accent_colors")
    suspend fun deleteAll()

    @Query("DELETE FROM accent_colors WHERE pinned = 0")
    suspend fun deleteUnpinned()
}