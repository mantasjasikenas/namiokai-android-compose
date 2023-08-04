package com.github.mantasjasikenas.namiokai.data

import kotlinx.coroutines.flow.Flow

class AccentColorRepositoryImpl (private val accentColorDao: AccentColorDao) : AccentColorRepository {
    override fun getAllAccentColorsStream(): Flow<List<AccentColor>> = accentColorDao.getAllCustomColors()

    override fun getAccentColorStream(id: Int): Flow<AccentColor?> = accentColorDao.getCustomColor(id)

    override suspend fun insertAccentColor(accentColor: AccentColor) = accentColorDao.insert(accentColor)

    override suspend fun deleteAccentColor(accentColor: AccentColor) = accentColorDao.delete(accentColor)

    override suspend fun deleteAllAccentColors() = accentColorDao.deleteAll()
    override suspend fun deleteUnpinnedAccentColors() = accentColorDao.deleteUnpinned()

    override suspend fun updateAccentColor(accentColor: AccentColor) = accentColorDao.update(accentColor)
}