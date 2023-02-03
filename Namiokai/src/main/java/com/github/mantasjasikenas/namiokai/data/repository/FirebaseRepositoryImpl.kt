package com.github.mantasjasikenas.namiokai.data.repository

import android.net.Uri
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Fuel
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.utils.JsonBuilder
import com.google.firebase.auth.ktx.auth
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
import java.time.LocalDateTime

private const val TAG = "FirebaseRepository"

// Database paths
private const val BILLS_COLLECTION = "bills"
private const val FUEL_COLLECTION = "fuel"
private const val USERS_COLLECTION = "users"
private const val DESTINATIONS_COLLECTION = "destinations"

// Backup paths
private const val BASE_PATH = "backup"
private const val USERS_PATH = "$BASE_PATH/users"
private const val BILLS_PATH = "$BASE_PATH/bills"
private const val FUEL_PATH = "$BASE_PATH/fuel"

private const val ORDER_BY = "date"

// User profile picture path
private const val IMAGES_STORAGE_PATH = "images"
private const val PHOTO_URL_FIELD = "photoUrl"
private const val USERNAME_FIELD = "displayName"


class FirebaseRepositoryImpl : FirebaseRepository {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    init {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }


    override suspend fun getBills(): Flow<List<Bill>> =
        db.collection(BILLS_COLLECTION).orderBy(
            ORDER_BY, Query.Direction.DESCENDING).snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Bill>()!!
                }
            }

    override suspend fun getFuel(): Flow<List<Fuel>> =
        db.collection(FUEL_COLLECTION).orderBy(
            ORDER_BY, Query.Direction.DESCENDING).snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Fuel>()!!
                }
            }


    override suspend fun insertBill(bill: Bill) {
        db.collection(BILLS_COLLECTION).add(bill)
    }


    override suspend fun insertFuel(fuel: Fuel) {
        db.collection(FUEL_COLLECTION).add(fuel)
    }

    override suspend fun insertUser(user: User) {
        db.collection(USERS_COLLECTION).document(user.uid).set(user)
        // db.collection(USERS_COLLECTION).add(user)
    }


    override suspend fun clearBills() {
        db.collection(BILLS_COLLECTION).get().addOnSuccessListener { documents ->
            for (document in documents) {
                db.collection(BILLS_COLLECTION).document(document.id).delete()
            }
        }
    }

    override suspend fun clearFuel() {
        db.collection(FUEL_COLLECTION).get().addOnSuccessListener { documents ->
            for (document in documents) {
                db.collection(FUEL_COLLECTION).document(document.id).delete()
            }
        }
    }

    override suspend fun clearUsers() {
        db.collection(USERS_COLLECTION).get().addOnSuccessListener { documents ->
            for (document in documents) {
                db.collection(USERS_COLLECTION).document(document.id).delete()
            }
        }
    }

    override suspend fun clearAll() {
        clearBills()
        clearFuel()
        clearUsers()
    }

    override suspend fun getUsers(): Flow<List<User>> =
        db.collection(USERS_COLLECTION).snapshots().map {
            it.documents.map { document ->
                document.toObject<User>()!!
            }
        }

    override suspend fun getDestinations(): Flow<List<Destination>> =
        db.collection(DESTINATIONS_COLLECTION).snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Destination>()!!
                }
            }


    override suspend fun getUser(uid: String): Flow<User> = getUsers().map { userList ->
        userList.first { it.uid == uid }
    }

    override suspend fun getCollections(): Flow<Triple<List<User>, List<Bill>, List<Fuel>>> =
        combine(
            getUsers(),
            getBills(),
            getFuel()
        ) { users, bills, fuels ->
            Triple(users, bills, fuels)
        }

    override suspend fun getBillsAndFuel(): Flow<Pair<List<Bill>, List<Fuel>>> =
        combine(
            getBills(),
            getFuel()
        ) { bills, fuels ->
            Pair(bills, fuels)
        }

    override suspend fun clearAndBackupCollections() =
        coroutineScope {
            val deferredUsers = async { backupCollection(USERS_COLLECTION) }
            val deferredBills = async { backupCollection(BILLS_COLLECTION) }
            val deferredFuel = async { backupCollection(FUEL_COLLECTION) }

            val usersJson = deferredUsers.await()
            val billsJson = deferredBills.await()
            val fuelJson = deferredFuel.await()

            val currentDateTime = LocalDateTime.now().toString()

            uploadJsonToStorage(usersJson, "$USERS_PATH/$currentDateTime.json")
            uploadJsonToStorage(billsJson, "$BILLS_PATH/$currentDateTime.json")
            uploadJsonToStorage(fuelJson, "$FUEL_PATH/$currentDateTime.json")

        }

    private suspend fun backupCollection(collectionPath: String): String {
        val querySnapshot = db.collection(collectionPath).get().await()
        val jsonBuilder = JsonBuilder()

        querySnapshot.forEach { document -> jsonBuilder.append(document.data) }

        return jsonBuilder.toString().replace("\\/", "/")
    }

    override suspend fun uploadJsonToStorage(json: String, fileName: String) {
        val storageRef = storage.reference
        val fileRef = storageRef.child(fileName)

        val stream = json.byteInputStream()
        fileRef.putStream(stream).await()

        withContext(Dispatchers.IO) {
            stream.close()
        }
    }

    override suspend fun getFileFromStorage(fileName: String): String {
        val storageRef = storage.reference
        val fileRef = storageRef.child(fileName)
        val taskSnapshot = fileRef.stream.await()

        return taskSnapshot.stream.bufferedReader().use { it.readText() }
    }

    override suspend fun addImageToFirebaseStorage(imageUri: Uri): Response<Uri> {
        return try {
            auth.uid?.let { uid ->
                val profileImageName = "$uid.jpg"

                val downloadUrl =
                    storage.reference.child(IMAGES_STORAGE_PATH).child(profileImageName)
                        .putFile(imageUri).await()
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
                db.collection(USERS_COLLECTION).document(uid)
                    .update(PHOTO_URL_FIELD, downloadUrl.toString()).await()
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
                db.collection(USERS_COLLECTION).document(uid)
                    .update(USERNAME_FIELD, newUserName).await()
                return Response.Success(true)
            }
            Response.Failure(Exception("User is not logged in"))
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

}