package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import kotlinx.coroutines.flow.Flow

interface TripBillsRepository {
    suspend fun getTripBills(): Flow<List<TripBill>>
    suspend fun getTripBills(period: Period): Flow<List<TripBill>>
    suspend fun clearFuel()
    suspend fun getDestinations(): Flow<List<Destination>>
    suspend fun insertFuel(tripBill: TripBill)
    suspend fun updateFuel(tripBill: TripBill)
    suspend fun deleteFuel(tripBill: TripBill)
    suspend fun loadFuelFromStorage(fileName: String): Response<Boolean>
    suspend fun backupCollection(fileName: String)
}