package com.github.mantasjasikenas.namiokai.data.repository

import com.github.mantasjasikenas.namiokai.data.BaseFirebaseRepository
import com.github.mantasjasikenas.namiokai.data.TripBillsRepository
import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.bills.TripBill
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val FUEL_COLLECTION = "fuel"
private const val BACKUP_FUEL_PATH = "backup/fuel"
private const val DESTINATIONS_COLLECTION = "destinations"
private const val ORDER_BY_FIELD = "date"

internal const val FUEL_IMPORT_FILE_NAME = "fuel.json"

class TripBillsRepositoryImpl @Inject constructor(
    private val baseFirebaseRepository: BaseFirebaseRepository,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) :
    TripBillsRepository {

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

    override suspend fun insertFuel(tripBill: TripBill) {
        db.collection(FUEL_COLLECTION)
            .add(tripBill)
    }

    override suspend fun updateFuel(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            db.collection(FUEL_COLLECTION)
                .document(tripBill.documentId)
                .set(tripBill)
        }
    }

    override suspend fun deleteFuel(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            db.collection(FUEL_COLLECTION)
                .document(tripBill.documentId)
                .delete()
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


    override suspend fun getDestinations(): Flow<List<Destination>> =
        db.collection(DESTINATIONS_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Destination>()!!
                }
            }

    override suspend fun loadFuelFromStorage(fileName: String): Response<Boolean> {
        return try {
            val fuelJson = baseFirebaseRepository.getFileFromStorage("$BACKUP_FUEL_PATH/$fileName")
            val tripBill = Json.decodeFromString<List<TripBill>>(fuelJson)
            tripBill.forEach { insertFuel(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }


}