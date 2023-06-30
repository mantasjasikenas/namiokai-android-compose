package com.github.mantasjasikenas.namiokai.data

import android.net.Uri
import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.bills.Bill
import com.github.mantasjasikenas.namiokai.model.bills.FlatBill
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.model.bills.TripBill
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getPurchaseBills(): Flow<List<PurchaseBill>>
    suspend fun getPurchaseBills(period: Period): Flow<List<PurchaseBill>>
    suspend fun getTripBills(): Flow<List<TripBill>>
    suspend fun getTripBills(period: Period): Flow<List<TripBill>>
    suspend fun getFlatBills(): Flow<List<FlatBill>>
    suspend fun getFlatBills(period: Period): Flow<List<FlatBill>>
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getDestinations(): Flow<List<Destination>>
    suspend fun getUser(uid: String): Flow<User>
    suspend fun getCollections(): Flow<Triple<List<User>, List<PurchaseBill>, List<TripBill>>>
    suspend fun getBills(): Flow<List<Bill>>
    suspend fun getBills(period: Period): Flow<List<Bill>>
    suspend fun insertBill(purchaseBill: PurchaseBill)
    suspend fun insertFuel(tripBill: TripBill)
    suspend fun insertUser(user: User)
    suspend fun insertFlatBill(flatBill: FlatBill)
    suspend fun updateBill(purchaseBill: PurchaseBill)
    suspend fun updateFuel(tripBill: TripBill)
    suspend fun updateFlatBill(flatBill: FlatBill)
    suspend fun deleteBill(purchaseBill: PurchaseBill)
    suspend fun deleteFuel(tripBill: TripBill)
    suspend fun deleteFlatBill(flatBill: FlatBill)
    suspend fun clearBills()
    suspend fun clearFuel()
    suspend fun clearUsers()
    suspend fun clearFlatBills()
    suspend fun clearBillsAndFuel()
    suspend fun backupCollections()
    suspend fun getFileFromStorage(fileName: String): String
    suspend fun uploadJsonToStorage(
        json: String,
        fileName: String
    )

    suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri>
    suspend fun changeCurrentUserImageUrlInFirestore(downloadUrl: Uri): Response<Boolean>
    suspend fun changeCurrentUserNameInFirestore(newUserName: String): Response<Boolean>
    suspend fun loadBillsFromStorage(fileName: String): Response<Boolean>
    suspend fun loadFuelFromStorage(fileName: String): Response<Boolean>
    suspend fun loadUsersFromStorage(fileName: String): Response<Boolean>


}