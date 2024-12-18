package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val FLAT_BILLS_COLLECTION = "flatBills"
private const val BACKUP_FLAT_BILLS_PATH = "backup/flatBills"
private const val ORDER_BY_FIELD = "date"

class FlatBillsRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val baseFirebaseRepository: BaseFirebaseRepository
) :
    FlatBillsRepository {

    override suspend fun getFlatBills(): Flow<List<FlatBill>> =
        db.collection(FLAT_BILLS_COLLECTION)
            .orderBy(
                ORDER_BY_FIELD,
                Query.Direction.ASCENDING
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
                Query.Direction.ASCENDING
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

    override suspend fun getFlatBill(id: String): Flow<FlatBill> =
        db.collection(FLAT_BILLS_COLLECTION)
            .document(id)
            .snapshots()
            .map {
                it.toObject<FlatBill>()!!
            }

    override suspend fun insertFlatBill(flatBill: FlatBill) {
        db.collection(FLAT_BILLS_COLLECTION)
            .add(flatBill)
    }


    override suspend fun updateFlatBill(flatBill: FlatBill) {
        if (flatBill.documentId.isNotEmpty()) {
            db.collection(FLAT_BILLS_COLLECTION)
                .document(flatBill.documentId)
                .set(flatBill)
        }
    }


    override suspend fun deleteFlatBill(flatBill: FlatBill) {
        if (flatBill.documentId.isNotEmpty()) {
            db.collection(FLAT_BILLS_COLLECTION)
                .document(flatBill.documentId)
                .delete()
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

    override suspend fun backupCollection(fileName: String) {
        baseFirebaseRepository.backupCollection(
            FLAT_BILLS_COLLECTION,
            BACKUP_FLAT_BILLS_PATH,
            fileName
        )
    }
}