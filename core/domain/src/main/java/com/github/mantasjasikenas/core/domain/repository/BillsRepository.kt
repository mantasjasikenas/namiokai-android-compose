package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import kotlinx.coroutines.flow.Flow

interface BillsRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getBills(period: Period): Flow<List<Bill>>
}