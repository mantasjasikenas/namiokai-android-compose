package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import kotlinx.coroutines.flow.Flow

interface TripBillsRepository {
    suspend fun getTripBills(): Flow<List<TripBill>>
    suspend fun getTripBills(period: Period): Flow<List<TripBill>>
    suspend fun getTripBill(id: String): Flow<TripBill>
    suspend fun clearTripBills()
    fun getDestinations(): Flow<List<Destination>>
    suspend fun insertTripBill(tripBill: TripBill)
    suspend fun updateTripBill(tripBill: TripBill)
    suspend fun deleteTripBill(tripBill: TripBill)
    suspend fun loadTripsFromStorage(fileName: String): Response<Boolean>
    suspend fun backupCollection(fileName: String)
}