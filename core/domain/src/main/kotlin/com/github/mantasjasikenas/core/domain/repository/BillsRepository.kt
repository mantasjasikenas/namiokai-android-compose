package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.period.Period
import kotlinx.coroutines.flow.Flow

interface BillsRepository {
    fun getBills(): Flow<List<Bill>>
    fun getBills(period: Period): Flow<List<Bill>>
    fun getBills(period: Period, spaceId: String): Flow<List<Bill>>
    fun getBills(period: Period, spaces: List<String>): Flow<List<Bill>>
    fun getBill(id: String, type: BillType): Flow<Bill?>
    suspend fun updateBill(bill: Bill)
    suspend fun insertBill(bill: Bill)
}