package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import kotlinx.coroutines.flow.Flow

interface PurchaseBillsRepository {
    suspend fun getPurchaseBills(): Flow<List<PurchaseBill>>
    suspend fun getPurchaseBills(period: Period): Flow<List<PurchaseBill>>
    suspend fun getPurchaseBill(id: String): Flow<PurchaseBill>
    suspend fun insertPurchaseBill(purchaseBill: PurchaseBill)
    suspend fun updatePurchaseBill(purchaseBill: PurchaseBill)
    suspend fun deletePurchaseBill(purchaseBill: PurchaseBill)
    suspend fun clearPurchaseBills()
    suspend fun loadPurchaseBillsFromStorage(fileName: String): Response<Boolean>
    suspend fun backupCollection(fileName: String)
}