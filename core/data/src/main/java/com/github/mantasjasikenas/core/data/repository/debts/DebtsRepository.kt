package com.github.mantasjasikenas.core.data.repository.debts

import com.github.mantasjasikenas.core.common.util.DebtsMap
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import kotlin.math.min


class DebtsRepository {

    //private val cachedDebts: DebtsMap = mutableMapOf()

    /**
     * Calculates debts for Bills
     */
    fun calculateDebts(
        bills: List<Bill>
    ): DebtsMap {
        val debts: DebtsMap = mutableMapOf()

        bills.forEach { bill ->
            val splitUsersUid = bill.splitUsersUid
            val paymasterUid = bill.paymasterUid
            val amountPerUser = bill.splitPricePerUser()

            splitUsersUid.forEach loop@{ splitUserUid ->
                if (splitUserUid == paymasterUid) return@loop

                val currentDebt = debts[splitUserUid]?.get(paymasterUid) ?: 0.0
                val calculatedDebt = currentDebt + amountPerUser

                debts.putIfAbsent(
                    splitUserUid,
                    HashMap()
                )
                debts[splitUserUid]!![paymasterUid] = calculatedDebt

                val currentDebtPaymaster = debts[paymasterUid]?.get(splitUserUid) ?: 0.0
                val minValue = min(
                    calculatedDebt,
                    currentDebtPaymaster
                )

                if (minValue > 0) {
                    val recalculatedPaymasterDebt = currentDebtPaymaster - minValue
                    val recalculatedSplitUserDebt = calculatedDebt - minValue

                    debts[splitUserUid]!![paymasterUid] = recalculatedSplitUserDebt
                    debts[paymasterUid]!![splitUserUid] = recalculatedPaymasterDebt

                    if (recalculatedSplitUserDebt == 0.0) debts[splitUserUid]?.remove(paymasterUid)

                    if (recalculatedPaymasterDebt == 0.0) debts[paymasterUid]?.remove(splitUserUid)
                }
            }
        }

        return debts
    }
}