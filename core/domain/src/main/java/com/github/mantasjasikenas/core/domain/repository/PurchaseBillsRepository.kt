package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import kotlinx.coroutines.flow.Flow

interface PurchaseBillsRepository {
    suspend fun getPurchaseBills(): Flow<List<PurchaseBill>>
    suspend fun getPurchaseBills(period: Period): Flow<List<PurchaseBill>>
    suspend fun insertBill(purchaseBill: PurchaseBill)
    suspend fun updateBill(purchaseBill: PurchaseBill)
    suspend fun deleteBill(purchaseBill: PurchaseBill)
    suspend fun clearBills()
    suspend fun loadBillsFromStorage(fileName: String): Response<Boolean>
    suspend fun backupCollection(fileName: String)

}