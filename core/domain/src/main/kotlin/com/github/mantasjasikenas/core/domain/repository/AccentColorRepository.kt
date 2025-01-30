package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.database.AccentColor
import kotlinx.coroutines.flow.Flow

interface AccentColorRepository {
    /**
     * Retrieve all the AccentColors from the the given data source.
     */
    fun getAllAccentColorsStream(): Flow<List<AccentColor>>

    /**
     * Retrieve an AccentColor from the given data source that matches with the [id].
     */
    fun getAccentColorStream(id: Int): Flow<AccentColor?>

    /**
     * Insert AccentColor in the data source
     */
    suspend fun insertAccentColor(accentColor: AccentColor)

    /**
     * Delete AccentColor from the data source
     */
    suspend fun deleteAccentColor(accentColor: AccentColor)

    /**
     * Update AccentColor in the data source
     */
    suspend fun updateAccentColor(accentColor: AccentColor)

    /**
     * Update AccentColor pinned state in the data source
     */
    suspend fun updateAccentColorPinned(id: Int, pinned: Boolean)

    /**
     * Delete all AccentColors from the data source
     */
    suspend fun deleteAllAccentColors()

    /**
     * Delete all unpinned AccentColors from the data source
     */
    suspend fun deleteUnpinnedAccentColors()
}