package com.github.mantasjasikenas.namiokai.data

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
     * Delete all AccentColors from the data source
     */
    suspend fun deleteAllAccentColors()

    /**
     * Delete all unpinned AccentColors from the data source
     */
    suspend fun deleteUnpinnedAccentColors()
}