package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val FUEL_COLLECTION = "fuel"
private const val BACKUP_FUEL_PATH = "backup/fuel"
private const val DESTINATIONS_COLLECTION = "destinations"
private const val ORDER_BY_FIELD = "date"

const val FUEL_IMPORT_FILE_NAME = "fuel.json"

class TripBillsRepositoryImpl @Inject constructor(
    private val baseFirebaseRepository: BaseFirebaseRepository,
    private val db: FirebaseFirestore,
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

    override suspend fun getTripBill(id: String): Flow<TripBill> =
        db.collection(FUEL_COLLECTION)
            .document(id)
            .snapshots()
            .map {
                it.toObject<TripBill>()!!
            }

    override suspend fun insertTripBill(tripBill: TripBill) {
        db.collection(FUEL_COLLECTION)
            .add(tripBill)
    }

    override suspend fun updateTripBill(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            db.collection(FUEL_COLLECTION)
                .document(tripBill.documentId)
                .set(tripBill)
        }
    }

    override suspend fun deleteTripBill(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            db.collection(FUEL_COLLECTION)
                .document(tripBill.documentId)
                .delete()
        }
    }

    override suspend fun clearTripBills() {
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


    override fun getDestinations(): Flow<List<Destination>> =
        db.collection(DESTINATIONS_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Destination>()!!
                }
            }

    override suspend fun loadTripsFromStorage(fileName: String): Response<Boolean> {
        return try {
            val fuelJson = baseFirebaseRepository.getFileFromStorage("$BACKUP_FUEL_PATH/$fileName")
            val tripBill = Json.decodeFromString<List<TripBill>>(fuelJson)
            tripBill.forEach { insertTripBill(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun backupCollection(fileName: String) {
        baseFirebaseRepository.backupCollection(
            FUEL_COLLECTION,
            BACKUP_FUEL_PATH,
            fileName
        )
    }


}