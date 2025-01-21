package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.database.AccentColor
import com.github.mantasjasikenas.core.database.AccentColorDao
import com.github.mantasjasikenas.core.domain.repository.AccentColorRepository
import kotlinx.coroutines.flow.Flow

class AccentColorRepositoryImpl(private val accentColorDao: AccentColorDao) :
    AccentColorRepository {
    override fun getAllAccentColorsStream(): Flow<List<AccentColor>> {
        return accentColorDao.getAllCustomColors()
    }

    override fun getAccentColorStream(id: Int): Flow<AccentColor?> {
        return accentColorDao.getCustomColor(id)
    }

    override suspend fun insertAccentColor(accentColor: AccentColor) {
        accentColorDao.insert(accentColor)
    }

    override suspend fun deleteAccentColor(accentColor: AccentColor) {
        accentColorDao.delete(accentColor)
    }

    override suspend fun deleteAllAccentColors() {
        accentColorDao.deleteAll()
    }

    override suspend fun deleteUnpinnedAccentColors() {
        accentColorDao.deleteUnpinned()
    }

    override suspend fun updateAccentColor(accentColor: AccentColor) {
        accentColorDao.update(accentColor)
    }

    override suspend fun updateAccentColorPinned(
        id: Int,
        pinned: Boolean
    ) {
        accentColorDao.updatePinned(id, pinned)
    }
}