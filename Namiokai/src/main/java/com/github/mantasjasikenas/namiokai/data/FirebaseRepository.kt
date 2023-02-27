package com.github.mantasjasikenas.namiokai.data

import android.net.Uri
import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.FlatBill
import com.github.mantasjasikenas.namiokai.model.Fuel
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getFuel(): Flow<List<Fuel>>
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getDestinations(): Flow<List<Destination>>
    suspend fun getUser(uid: String): Flow<User>
    suspend fun getFlatBills(): Flow<List<FlatBill>>
    suspend fun getCollections(): Flow<Triple<List<User>, List<Bill>, List<Fuel>>>
    suspend fun getBillsAndFuel(): Flow<Pair<List<Bill>, List<Fuel>>>
    suspend fun insertBill(bill: Bill)
    suspend fun insertFuel(fuel: Fuel)
    suspend fun insertUser(user: User)
    suspend fun insertFlatBill(flatBill: FlatBill)
    suspend fun updateBill(bill: Bill)
    suspend fun updateFuel(fuel: Fuel)
    suspend fun updateFlatBill(flatBill: FlatBill)
    suspend fun deleteBill(bill: Bill)
    suspend fun deleteFuel(fuel: Fuel)
    suspend fun deleteFlatBill(flatBill: FlatBill)
    suspend fun clearBills()
    suspend fun clearFuel()
    suspend fun clearUsers()
    suspend fun clearFlatBills()
    suspend fun clearBillsAndFuel()
    suspend fun backupCollections()
    suspend fun getFileFromStorage(fileName: String): String
    suspend fun uploadJsonToStorage(json: String, fileName: String)
    suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri>
    suspend fun changeCurrentUserImageUrlInFirestore(downloadUrl: Uri): Response<Boolean>
    suspend fun changeCurrentUserNameInFirestore(newUserName: String): Response<Boolean>
    suspend fun loadBillsFromStorage(fileName: String): Response<Boolean>
    suspend fun loadFuelFromStorage(fileName: String): Response<Boolean>
    suspend fun loadUsersFromStorage(fileName: String): Response<Boolean>


}