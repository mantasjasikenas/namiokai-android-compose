package com.github.mantasjasikenas.namiokai.data.repository

import com.github.mantasjasikenas.namiokai.data.BillsRepository
import com.github.mantasjasikenas.namiokai.data.FlatBillsRepository
import com.github.mantasjasikenas.namiokai.data.PurchaseBillsRepository
import com.github.mantasjasikenas.namiokai.data.TripBillsRepository
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.bills.Bill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
}