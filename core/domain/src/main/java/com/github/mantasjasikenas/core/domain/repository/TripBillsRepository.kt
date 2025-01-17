package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import kotlinx.coroutines.flow.Flow

interface TripBillsRepository {
    fun getTripBills(): Flow<List<TripBill>>
    fun getTripBills(spaceIds: List<String>): Flow<List<TripBill>>
    fun getTripBills(period: Period): Flow<List<TripBill>>
    fun getTripBill(id: String): Flow<TripBill>
    suspend fun clearTripBills()
    suspend fun insertTripBill(tripBill: TripBill)
    suspend fun updateTripBill(tripBill: TripBill)
    suspend fun deleteTripBill(tripBill: TripBill)
    suspend fun loadTripsFromStorage(fileName: String): Response<Boolean>
    suspend fun backupCollection(fileName: String)
}