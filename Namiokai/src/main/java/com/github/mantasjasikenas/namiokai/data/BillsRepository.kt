package com.github.mantasjasikenas.namiokai.data

import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.bills.Bill
import kotlinx.coroutines.flow.Flow

interface BillsRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getBills(period: Period): Flow<List<Bill>>
}