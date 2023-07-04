package com.github.mantasjasikenas.namiokai.data

import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.bills.TripBill
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