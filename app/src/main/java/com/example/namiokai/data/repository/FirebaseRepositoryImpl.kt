package com.example.namiokai.data.repository

import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.model.Bill
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.User
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private const val BILLS_COLLECTION = "bills"
private const val FUEL_COLLECTION = "fuel"
private const val USERS_COLLECTION = "users"

private const val ORDER_BY = "date"

class FirebaseRepositoryImpl : FirebaseRepository {

    private val db = Firebase.firestore

    init {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    override suspend fun getBills(): Flow<List<Bill>> =
        db.collection(BILLS_COLLECTION).orderBy(ORDER_BY, Query.Direction.DESCENDING).snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Bill>()!!
                }
            }

    override suspend fun getFuel(): Flow<List<Fuel>> =
        db.collection(FUEL_COLLECTION).orderBy(ORDER_BY, Query.Direction.DESCENDING).snapshots()
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
        db.collection(USERS_COLLECTION).add(user)
    }

    override suspend fun deleteUser(user: User) {
        // TODO Implement delete user
        //db.collection(USERS_COLLECTION).document("").delete()
    }

    override suspend fun getUsers(): Flow<List<User>> =
        db.collection(USERS_COLLECTION).snapshots().map {
            it.documents.map { document ->
                document.toObject<User>()!!
            }
        }

    override suspend fun getUser(uid: String): Flow<User> = getUsers().map { userList ->
        userList.first { it.uid == uid }
    }


}