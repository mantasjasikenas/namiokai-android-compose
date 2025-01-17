package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.period.Period
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

private const val TRIP_BILL_COLLECTION = "fuel"
private const val BACKUP_FUEL_PATH = "backup/fuel"
private const val ORDER_BY_FIELD = "date"
private const val SPACE_ID_FIELD = "spaceId"

const val FUEL_IMPORT_FILE_NAME = "fuel.json"

class TripBillsRepositoryImpl @Inject constructor(
    private val baseFirebaseRepository: BaseFirebaseRepository,
    db: FirebaseFirestore,
) : TripBillsRepository {

    private val tripBillCollection = db.collection(TRIP_BILL_COLLECTION)

    override fun getTripBills(): Flow<List<TripBill>> =
        tripBillCollection
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

    override fun getTripBills(spaceIds: List<String>): Flow<List<TripBill>> =
        tripBillCollection
            .whereIn(SPACE_ID_FIELD, spaceIds)
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


    override fun getTripBills(period: Period): Flow<List<TripBill>> =
        tripBillCollection
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

    override fun getTripBill(id: String): Flow<TripBill> =
        tripBillCollection
            .document(id)
            .snapshots()
            .map {
                it.toObject<TripBill>()!!
            }

    override suspend fun insertTripBill(tripBill: TripBill) {
        tripBillCollection
            .add(tripBill)
    }

    override suspend fun updateTripBill(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            tripBillCollection
                .document(tripBill.documentId)
                .set(tripBill)
        }
    }

    override suspend fun deleteTripBill(tripBill: TripBill) {
        if (tripBill.documentId.isNotEmpty()) {
            tripBillCollection
                .document(tripBill.documentId)
                .delete()
        }
    }

    override suspend fun clearTripBills() {
        tripBillCollection
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    tripBillCollection
                        .document(document.id)
                        .delete()
                }
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
            TRIP_BILL_COLLECTION,
            BACKUP_FUEL_PATH,
            fileName
        )
    }


}