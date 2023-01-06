package com.example.namiokai.data

import com.example.namiokai.model.Bill
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getFuel(): Flow<List<Fuel>>
    suspend fun insertBill(bill: Bill)
    suspend fun insertFuel(fuel: Fuel)
    suspend fun insertUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getUser(uid: String): Flow<User>

}