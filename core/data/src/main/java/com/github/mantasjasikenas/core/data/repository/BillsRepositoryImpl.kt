package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
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