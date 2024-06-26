package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.repository.BaseFirebaseRepository
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val BILLS_COLLECTION = "bills"
private const val BACKUP_BILLS_PATH = "backup/bills"
private const val ORDER_BY_FIELD = "date"

const val BILL_IMPORT_FILE_NAME = "bills.json"

class PurchaseBillsRepositoryImpl @Inject constructor(
    private val baseFirebaseRepository: BaseFirebaseRepository,
    private val db: FirebaseFirestore
) :
    PurchaseBillsRepository {

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

    override suspend fun insertBill(purchaseBill: PurchaseBill) {
        db.collection(BILLS_COLLECTION)
            .add(purchaseBill)
    }

    override suspend fun updateBill(purchaseBill: PurchaseBill) {
        if (purchaseBill.documentId.isNotEmpty()) {
            db.collection(BILLS_COLLECTION)
                .document(purchaseBill.documentId)
                .set(purchaseBill)
        }
    }

    override suspend fun deleteBill(purchaseBill: PurchaseBill) {
        if (purchaseBill.documentId.isNotEmpty()) {
            db.collection(BILLS_COLLECTION)
                .document(purchaseBill.documentId)
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

    override suspend fun loadBillsFromStorage(fileName: String): Response<Boolean> {
        return try {
            val billsJson =
                baseFirebaseRepository.getFileFromStorage("$BACKUP_BILLS_PATH/$fileName")
            val purchaseBills = Json.decodeFromString<List<PurchaseBill>>(billsJson)
            purchaseBills.forEach { insertBill(it) }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun backupCollection(fileName: String) {
        baseFirebaseRepository.backupCollection(BILLS_COLLECTION, BACKUP_BILLS_PATH, fileName)
    }
}