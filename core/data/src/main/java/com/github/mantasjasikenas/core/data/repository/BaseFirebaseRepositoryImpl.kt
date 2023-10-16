package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.common.util.JsonBuilder
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BaseFirebaseRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : BaseFirebaseRepository {

    override suspend fun backupCollection(
        collectionPath: String,
        backupPath: String,
        fileName: String
    ) =
        coroutineScope {
            val deferredCollection = async { jsonSerializeCollection(collectionPath) }
            val collectionJson = deferredCollection.await()
            uploadJsonToStorage(
                collectionJson,
                "$backupPath/$fileName.json"
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

    private suspend fun jsonSerializeCollection(collectionPath: String): String {
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

    /*override suspend fun backupCollections() =
        coroutineScope {
            val deferredUsers = async { jsonSerializeCollection(USERS_COLLECTION) }
            val deferredBills = async { jsonSerializeCollection(BILLS_COLLECTION) }
            val deferredFuel = async { jsonSerializeCollection(FUEL_COLLECTION) }
            val deferredFlatBill = async { jsonSerializeCollection(FLAT_BILLS_COLLECTION) }

            val usersJson = deferredUsers.await()
            val billsJson = deferredBills.await()
            val fuelJson = deferredFuel.await()
            val flatBillJson = deferredFlatBill.await()

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
            uploadJsonToStorage(
                flatBillJson,
                "$FLAT_BILLS_COLLECTION/$currentDateTime.json"
            )

        }*/
}