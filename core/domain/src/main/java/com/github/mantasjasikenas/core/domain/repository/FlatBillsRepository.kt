package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.period.Period
import kotlinx.coroutines.flow.Flow

interface FlatBillsRepository {
    fun getFlatBills(): Flow<List<FlatBill>>
    fun getFlatBills(spaceIds: List<String>): Flow<List<FlatBill>>
    fun getFlatBills(period: Period): Flow<List<FlatBill>>
    fun getFlatBills(period: Period, spaceId: String): Flow<List<FlatBill>>
    fun getFlatBills(period: Period, spaceIds: List<String>): Flow<List<FlatBill>>
    fun getFlatBill(id: String): Flow<FlatBill?>
    suspend fun insertFlatBill(flatBill: FlatBill)
    suspend fun updateFlatBill(flatBill: FlatBill)
    suspend fun deleteFlatBill(flatBill: FlatBill)
    suspend fun clearFlatBills()
    suspend fun backupCollection(fileName: String)
}