package com.github.mantasjasikenas.namiokai.data

import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
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