package com.example.namiokai.data

import com.example.namiokai.model.Bill
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getFuel(): Flow<List<Fuel>>
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getUser(uid: String): Flow<User>
    suspend fun getCollections(): Flow<Triple<List<User>, List<Bill>, List<Fuel>>>
    suspend fun insertBill(bill: Bill)
    suspend fun insertFuel(fuel: Fuel)
    suspend fun insertUser(user: User)
    suspend fun clearBills()
    suspend fun clearFuel()
    suspend fun clearUsers()
    suspend fun clearAll()
    suspend fun clearAndBackupCollections()
    suspend fun getFileFromStorage(fileName: String): String
    suspend fun uploadJsonToStorage(json: String, fileName: String)

}