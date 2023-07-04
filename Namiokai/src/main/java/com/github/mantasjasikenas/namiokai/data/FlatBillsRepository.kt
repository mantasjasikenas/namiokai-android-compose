package com.github.mantasjasikenas.namiokai.data

import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.bills.FlatBill
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