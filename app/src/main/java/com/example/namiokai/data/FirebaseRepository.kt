package com.example.namiokai.data

import com.example.namiokai.model.Bill
import com.example.namiokai.model.Fuel
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getFuel(): Flow<List<Fuel>>
    suspend fun insertBill(bill: Bill)
    suspend fun insertFuel(fuel: Fuel)
}