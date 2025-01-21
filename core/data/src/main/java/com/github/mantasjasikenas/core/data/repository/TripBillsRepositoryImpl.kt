package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    private fun CollectionReference.orderByDate(direction: Query.Direction = Query.Direction.DESCENDING): Query {
        return this.orderBy(ORDER_BY_FIELD, direction)
    }

    private fun Query.orderByDate(direction: Query.Direction = Query.Direction.DESCENDING): Query {
        return this.orderBy(ORDER_BY_FIELD, direction)
    }

    private fun Query.whereInPeriod(period: Period): Query {
        return this
            .whereGreaterThanOrEqualTo(
                ORDER_BY_FIELD, period.start.toString() + "T00:00:00"
            )
            .whereLessThanOrEqualTo(
                ORDER_BY_FIELD, period.end.toString() + "T23:59:59"
            )
    }

    override fun getTripBills(): Flow<List<TripBill>> {
        return tripBillCollection
            .orderByDate()
            .dataObjects<TripBill>()
    }

    override fun getTripBills(spaceIds: List<String>): Flow<List<TripBill>> {
        if (spaceIds.isEmpty()) {
            return flowOf(emptyList())
        }

        return tripBillCollection
            .whereIn(SPACE_ID_FIELD, spaceIds)
            .orderByDate()
            .dataObjects<TripBill>()
    }

    override fun getTripBills(period: Period, spaceId: String): Flow<List<TripBill>> {
        return tripBillCollection
            .orderByDate()
            .whereEqualTo(SPACE_ID_FIELD, spaceId)
            .whereInPeriod(period)
            .dataObjects<TripBill>()
    }

    override fun getTripBills(period: Period, spaceIds: List<String>): Flow<List<TripBill>> {
        if (spaceIds.isEmpty()) {
            return flowOf(emptyList())
        }

        return tripBillCollection
            .orderByDate()
            .whereIn(SPACE_ID_FIELD, spaceIds)
            .whereInPeriod(period)
            .dataObjects<TripBill>()
    }

    override fun getTripBills(period: Period): Flow<List<TripBill>> {
        return tripBillCollection
            .orderByDate()
            .whereInPeriod(period)
            .dataObjects<TripBill>()
    }

    override fun getTripBill(id: String): Flow<TripBill?> {
        return tripBillCollection
            .document(id)
            .dataObjects<TripBill>()
    }

    override suspend fun insertTripBill(tripBill: TripBill) {
        tripBillCollection.add(tripBill)
    }

    override suspend fun updateTripBill(tripBill: TripBill) {
        if (tripBill.documentId.isEmpty()) {
            return
        }

        tripBillCollection
            .document(tripBill.documentId)
            .set(tripBill)
    }

    override suspend fun deleteTripBill(tripBill: TripBill) {
        if (tripBill.documentId.isEmpty()) {
            return
        }

        tripBillCollection
            .document(tripBill.documentId)
            .delete()
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