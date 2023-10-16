package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import kotlinx.coroutines.flow.Flow

interface FlatBillsRepository {
    suspend fun getFlatBills(): Flow<List<FlatBill>>
    suspend fun getFlatBills(period: Period): Flow<List<FlatBill>>
    suspend fun insertFlatBill(flatBill: FlatBill)
    suspend fun updateFlatBill(flatBill: FlatBill)
    suspend fun deleteFlatBill(flatBill: FlatBill)
    suspend fun clearFlatBills()
    suspend fun backupCollection(fileName: String)
}