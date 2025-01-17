package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import kotlinx.coroutines.flow.Flow

interface BillsRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getBills(period: Period): Flow<List<Bill>>
    fun getBill(id: String, type: BillType): Flow<Bill>
    suspend fun updateBill(bill: Bill)
    suspend fun insertBill(bill: Bill)
}