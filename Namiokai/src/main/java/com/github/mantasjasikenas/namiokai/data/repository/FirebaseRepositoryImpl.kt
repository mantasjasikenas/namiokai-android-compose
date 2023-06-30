package com.github.mantasjasikenas.namiokai.data.repository

import android.net.Uri
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.bills.Bill
import com.github.mantasjasikenas.namiokai.model.bills.FlatBill
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.model.bills.TripBill
import com.github.mantasjasikenas.namiokai.utils.JsonBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

private const val TAG = "FirebaseRepository"

// Database paths
private const val BILLS_COLLECTION = "bills"
private const val FUEL_COLLECTION = "fuel"
private const val USERS_COLLECTION = "users"
private const val FLAT_BILLS_COLLECTION = "flatBills"
private const val DESTINATIONS_COLLECTION = "destinations"

// Backup paths
private const val BASE_PATH = "backup"
private const val USERS_PATH = "$BASE_PATH/users"
private const val BILLS_PATH = "$BASE_PATH/bills"
private const val FUEL_PATH = "$BASE_PATH/fuel"

private const val ORDER_BY_FIELD = "date"

// User profile picture path
private const val IMAGES_STORAGE_PATH = "images"
private const val PHOTO_URL_FIELD = "photoUrl"
private const val USERNAME_FIELD = "displayName"

const val BILL_IMPORT_FILE_NAME = "bills.json"
const val FUEL_IMPORT_FILE_NAME = "fuel.json"
const val USERS_IMPORT_FILE_NAME = "users.json"


class FirebaseRepositoryImpl : FirebaseRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .build()
            )
        }
        db.firestoreSettings = settings
    }


    override suspend fun getPurchaseBills(): Flow<List<PurchaseBill>> =
        db.collection(BILLS_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.DESCENDING
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<PurchaseBill>()!!
                }
            }

    override suspend fun getPurchaseBills(period: Period): Flow<List<PurchaseBill>> =
        db.collection(BILLS_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.DESCENDING
            )
            .whereGreaterThanOrEqualTo(
                ORDER_BY_FIELD,
                period.start.toString() + "T00:00:00"
            )
            .whereLessThanOrEqualTo(
                ORDER_BY_FIELD,
                period.end.toString() + "T23:59:59"
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<PurchaseBill>()!!
                }
            }

    override suspend fun getTripBills(): Flow<List<TripBill>> =
        db.collection(FUEL_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.DESCENDING
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<TripBill>()!!
                }
            }

    override suspend fun getTripBills(period: Period): Flow<List<TripBill>> =
        db.collection(FUEL_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.DESCENDING
            )
            .whereGreaterThanOrEqualTo(
                ORDER_BY_FIELD,
                period.start.toString() + "T00:00:00"
            )
            .whereLessThanOrEqualTo(
                ORDER_BY_FIELD,
                period.end.toString() + "T23:59:59"
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<TripBill>()!!
                }
            }

    override suspend fun getFlatBills(): Flow<List<FlatBill>> =
        db.collection(FLAT_BILLS_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.DESCENDING
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<FlatBill>()!!
                }
            }

    override suspend fun getFlatBills(period: Period): Flow<List<FlatBill>> =
        db.collection(FLAT_BILLS_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.DESCENDING
            )
            .whereGreaterThanOrEqualTo(
                ORDER_BY_FIELD,
                period.start.toString() + "T00:00:00"
            )
            .whereLessThanOrEqualTo(
                ORDER_BY_FIELD,
                period.end.toString() + "T23:59:59"
            )
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<FlatBill>()!!
                }
            }


    override suspend fun insertBill(purchaseBill: PurchaseBill) {
        db.collection(BILLS_COLLECTION)
            .add(purchaseBill)
    }


    override suspend fun insertFuel(tripBill: TripBill) {
        db.collection(FUEL_COLLECTION)
            .add(tripBill)
    }

    override suspend fun insertUser(user: User) {
        db.collection(USERS_COLLECTION)
            .document(user.uid)
            .set(user)
        // db.collection(USERS_COLLECTION).add(user)
    }

    override suspend fun insertFlatBill(flatBill: FlatBill) {
        db.collection(FLAT_BILLS_COLLECTION)
            .add(flatBill)
    }

    override suspend fun updateBill(purchaseBill: PurchaseBill) {
        if (purchaseBill.documentId.isNotEmpty()) {
            db.collection(BILLS_COLLECTION)
                .document(purchaseBill.documentId)
                .set(purchaseBill)
        }
    }

    override suspend fun updateFuel(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            db.collection(FUEL_COLLECTION)
                .document(tripBill.documentId)
                .set(tripBill)
        }
    }

    override suspend fun updateFlatBill(flatBill: FlatBill) {
        if (flatBill.documentId.isNotEmpty()) {
            db.collection(FLAT_BILLS_COLLECTION)
                .document(flatBill.documentId)
                .set(flatBill)
        }
    }

    override suspend fun deleteBill(purchaseBill: PurchaseBill) {
        if (purchaseBill.documentId.isNotEmpty()) {
            db.collection(BILLS_COLLECTION)
                .document(purchaseBill.documentId)
                .delete()
        }
    }

    override suspend fun deleteFuel(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            db.collection(FUEL_COLLECTION)
                .document(tripBill.documentId)
                .delete()
        }
    }

    override suspend fun deleteFlatBill(flatBill: FlatBill) {
        if (flatBill.documentId.isNotEmpty()) {
            db.collection(FLAT_BILLS_COLLECTION)
                .document(flatBill.documentId)
                .delete()
        }
    }

    override suspend fun clearBills() {
        db.collection(BILLS_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection(BILLS_COLLECTION)
                        .document(document.id)
                        .delete()
                }
            }
    }

    override suspend fun clearFuel() {
        db.collection(FUEL_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection(FUEL_COLLECTION)
                        .document(document.id)
                        .delete()
                }
            }
    }

    override suspend fun clearFlatBills() {
        db.collection(FLAT_BILLS_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection(FLAT_BILLS_COLLECTION)
                        .document(document.id)
                        .delete()
                }
            }
    }

    override suspend fun clearUsers() {
        db.collection(USERS_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection(USERS_COLLECTION)
                        .document(document.id)
                        .delete()
                }
            }
    }

    override suspend fun clearBillsAndFuel() {
        clearBills()
        clearFuel()
    }

    override suspend fun getUsers(): Flow<List<User>> =
        db.collection(USERS_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<User>()!!
                }
            }

    override suspend fun getDestinations(): Flow<List<Destination>> =
        db.collection(DESTINATIONS_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Destination>()!!
                }
            }


    override suspend fun getUser(uid: String): Flow<User> = getUsers().map { userList ->
        userList.first { it.uid == uid }
    }

    override suspend fun getCollections(): Flow<Triple<List<User>, List<PurchaseBill>, List<TripBill>>> =
        combine(
            getUsers(),
            getPurchaseBills(),
            getTripBills()
        ) { users, bills, fuels ->
            Triple(
                users,
                bills,
                fuels
            )
        }

    override suspend fun getBills(): Flow<List<Bill>> {
        return combine(
            getPurchaseBills(),
            getTripBills(),
            getFlatBills()
        ) { purchaseBills, tripBills, flatBills ->
            purchaseBills + tripBills + flatBills
        }
    }

    override suspend fun getBills(period: Period): Flow<List<Bill>> {
        return combine(
            getPurchaseBills(period),
            getTripBills(period),
            getFlatBills(period)
        ) { purchaseBills, tripBills, flatBills ->
            purchaseBills + tripBills + flatBills
        }
    }

    override suspend fun backupCollections() =
        coroutineScope {
            val deferredUsers = async { backupCollection(USERS_COLLECTION) }
            val deferredBills = async { backupCollection(BILLS_COLLECTION) }
            val deferredFuel = async { backupCollection(FUEL_COLLECTION) }

            val usersJson = deferredUsers.await()
            val billsJson = deferredBills.await()
            val fuelJson = deferredFuel.await()

            val currentDateTime = LocalDateTime.now()
                .toString()

            uploadJsonToStorage(
                usersJson,
                "$USERS_PATH/$currentDateTime.json"
            )
            uploadJsonToStorage(
                billsJson,
                "$BILLS_PATH/$currentDateTime.json"
            )
            uploadJsonToStorage(
                fuelJson,
                "$FUEL_PATH/$currentDateTime.json"
            )

        }

    private suspend fun backupCollection(collectionPath: String): String {
        val querySnapshot = db.collection(collectionPath)
            .get()
            .await()
        val jsonBuilder = JsonBuilder()

        querySnapshot.forEach { document -> jsonBuilder.append(document.data) }

        return jsonBuilder.toString()
            .replace(
                "\\/",
                "/"
            )
    }

    override suspend fun uploadJsonToStorage(
        json: String,
        fileName: String
    ) {
        val storageRef = storage.reference
        val fileRef = storageRef.child(fileName)

        val stream = json.byteInputStream()
        fileRef.putStream(stream)
            .await()

        withContext(Dispatchers.IO) {
            stream.close()
        }
    }

    override suspend fun getFileFromStorage(fileName: String): String {
        val storageRef = storage.reference
        val fileRef = storageRef.child(fileName)
        val taskSnapshot = fileRef.stream.await()

        return taskSnapshot.stream.bufferedReader()
            .use { it.readText() }
    }

    override suspend fun loadBillsFromStorage(fileName: String): Response<Boolean> {
        return try {
            val billsJson = getFileFromStorage("$BILLS_PATH/$fileName")
            val purchaseBills = Json.decodeFromString<List<PurchaseBill>>(billsJson)
            purchaseBills.forEach { insertBill(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }

    }

    override suspend fun loadFuelFromStorage(fileName: String): Response<Boolean> {
        return try {
            val fuelJson = getFileFromStorage("$FUEL_PATH/$fileName")
            val tripBill = Json.decodeFromString<List<TripBill>>(fuelJson)
            tripBill.forEach { insertFuel(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun loadUsersFromStorage(fileName: String): Response<Boolean> {
        return try {
            val usersJson = getFileFromStorage("$FUEL_PATH/$fileName")
            val users = Json.decodeFromString<List<TripBill>>(usersJson)
            users.forEach { insertFuel(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri> {
        return try {
            auth.uid?.let { uid ->
                val profileImageName = "$uid.jpg"

                val downloadUrl =
                    storage.reference.child(IMAGES_STORAGE_PATH)
                        .child(profileImageName)
                        .putFile(imageUri)
                        .await()
                        .storage.downloadUrl.await()

                return Response.Success(downloadUrl)
            }

            Response.Failure(Exception("User is not logged in"))

        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun changeCurrentUserImageUrlInFirestore(downloadUrl: Uri): Response<Boolean> {
        return try {
            auth.uid?.let { uid ->
                db.collection(USERS_COLLECTION)
                    .document(uid)
                    .update(
                        PHOTO_URL_FIELD,
                        downloadUrl.toString()
                    )
                    .await()
                return Response.Success(true)
            }
            Response.Failure(Exception("User is not logged in"))
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun changeCurrentUserNameInFirestore(newUserName: String): Response<Boolean> {
        return try {
            auth.uid?.let { uid ->
                db.collection(USERS_COLLECTION)
                    .document(uid)
                    .update(
                        USERNAME_FIELD,
                        newUserName
                    )
                    .await()
                return Response.Success(true)
            }
            Response.Failure(Exception("User is not logged in"))
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

}