package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.period.Period
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
private const val SPACE_ID_FIELD = "spaceId"

class FlatBillsRepositoryImpl @Inject constructor(
    db: FirebaseFirestore,
    private val baseFirebaseRepository: BaseFirebaseRepository
) : FlatBillsRepository {

    private val flatBillsCollection = db.collection(FLAT_BILLS_COLLECTION)

    override fun getFlatBills(): Flow<List<FlatBill>> =
        flatBillsCollection
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

    override fun getFlatBills(spaceIds: List<String>): Flow<List<FlatBill>> =
        flatBillsCollection
            .whereIn(SPACE_ID_FIELD, spaceIds)
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


    override fun getFlatBills(period: Period): Flow<List<FlatBill>> =
        flatBillsCollection
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

    override fun getFlatBill(id: String): Flow<FlatBill> =
        flatBillsCollection
            .document(id)
            .snapshots()
            .map {
                it.toObject<FlatBill>()!!
            }

    override suspend fun insertFlatBill(flatBill: FlatBill) {
        flatBillsCollection
            .add(flatBill)
    }


    override suspend fun updateFlatBill(flatBill: FlatBill) {
        if (flatBill.documentId.isNotEmpty()) {
            flatBillsCollection
                .document(flatBill.documentId)
                .set(flatBill)
        }
    }


    override suspend fun deleteFlatBill(flatBill: FlatBill) {
        if (flatBill.documentId.isNotEmpty()) {
            flatBillsCollection
                .document(flatBill.documentId)
                .delete()
        }
    }

    override suspend fun clearFlatBills() {
        flatBillsCollection
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    flatBillsCollection
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