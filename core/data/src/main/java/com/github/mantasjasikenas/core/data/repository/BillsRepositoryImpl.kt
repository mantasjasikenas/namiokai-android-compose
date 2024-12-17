package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.bills.BillType
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BillsRepositoryImpl @Inject constructor(
    private val purchaseBillsRepository: PurchaseBillsRepository,
    private val tripBillsRepository: TripBillsRepository,
    private val flatBillsRepository: FlatBillsRepository
) : BillsRepository {

    override suspend fun getBills(): Flow<List<Bill>> {
        return combine(
            purchaseBillsRepository.getPurchaseBills(),
            tripBillsRepository.getTripBills(),
            flatBillsRepository.getFlatBills()
        ) { purchaseBills, tripBills, flatBills ->
            purchaseBills + tripBills + flatBills
        }
    }

    override suspend fun getBills(period: Period): Flow<List<Bill>> {
        return combine(
            purchaseBillsRepository.getPurchaseBills(period),
            tripBillsRepository.getTripBills(period),
            flatBillsRepository.getFlatBills(period)
        ) { purchaseBills, tripBills, flatBills ->
            purchaseBills + tripBills + flatBills
        }
    }

    override fun getBill(id: String, type: BillType): Flow<Bill> = flow {
        val billFlow = when (type) {
            BillType.Purchase -> purchaseBillsRepository.getPurchaseBill(id)
            BillType.Trip -> tripBillsRepository.getTripBill(id)
            BillType.Flat -> flatBillsRepository.getFlatBill(id)
        }

        emitAll(billFlow)
    }

    override suspend fun updateBill(bill: Bill) {
        when (bill) {
            is PurchaseBill -> purchaseBillsRepository.updatePurchaseBill(bill)
            is TripBill -> tripBillsRepository.updateTripBill(bill)
            is FlatBill -> flatBillsRepository.updateFlatBill(bill)
        }
    }

    override suspend fun insertBill(bill: Bill) {
        when (bill) {
            is PurchaseBill -> purchaseBillsRepository.insertPurchaseBill(bill)
            is TripBill -> tripBillsRepository.insertTripBill(bill)
            is FlatBill -> flatBillsRepository.insertFlatBill(bill)
        }
    }
}