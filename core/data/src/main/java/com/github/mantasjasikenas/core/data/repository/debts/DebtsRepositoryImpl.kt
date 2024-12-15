package com.github.mantasjasikenas.core.data.repository.debts

import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.debts.DebtBill
import com.github.mantasjasikenas.core.domain.model.debts.DebtsMap
import com.github.mantasjasikenas.core.domain.model.debts.MutableDebtsMap
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository

// TODO: validate logic
class DebtsRepositoryImpl : DebtsRepository {
    override fun calculateDebts(bills: List<Bill>): DebtsMap {
        val debtsMap = MutableDebtsMap()

        bills.forEach { bill ->
            processBill(bill, debtsMap)
        }

        return debtsMap
    }

    private fun processBill(bill: Bill, debtsMap: MutableDebtsMap) {
        val splitUsersUid = bill.splitUsersUid
        val paymasterUid = bill.paymasterUid
        val amountPerUser = bill.splitPricePerUser()

        splitUsersUid.forEach { splitUserUid ->
            if (splitUserUid != paymasterUid) {
                addDebt(splitUserUid, paymasterUid, amountPerUser, bill, debtsMap)
                balanceDebts(splitUserUid, paymasterUid, debtsMap)
            }
        }
    }

    private fun addDebt(
        fromUser: UserUid, toUser: UserUid, amount: Double, bill: Bill, debtsMap: MutableDebtsMap
    ) {
        debtsMap.addDebt(fromUser, toUser, DebtBill(amount, bill))
    }

    private fun balanceDebts(fromUser: UserUid, toUser: UserUid, debtsMap: MutableDebtsMap) {
        val fromUserDebts = debtsMap.getDebts(fromUser, toUser).toMutableList()
        val toUserDebts = debtsMap.getDebts(toUser, fromUser).toMutableList()
        val currentDebt = fromUserDebts.sumOf { it.amount }
        val currentDebtPaymaster = toUserDebts.sumOf { it.amount }
        val minValue = min(currentDebt, currentDebtPaymaster)

        if (minValue > 0) {
            var remainingValue = minValue

            while (remainingValue > 0 && fromUserDebts.isNotEmpty() && toUserDebts.isNotEmpty()) {
                val fromDebt = fromUserDebts.removeAt(0)
                val toDebt = toUserDebts.removeAt(0)
                val debtAmount = min(fromDebt.amount, toDebt.amount, remainingValue)

                debtsMap.addDebt(fromUser, toUser, DebtBill(-debtAmount, toDebt.bill))
                debtsMap.addDebt(toUser, fromUser, DebtBill(debtAmount, fromDebt.bill))

                remainingValue -= debtAmount
            }

            if (currentDebt - minValue == 0.0) debtsMap.removeDebt(fromUser, toUser)
            if (currentDebtPaymaster - minValue == 0.0) debtsMap.removeDebt(toUser, fromUser)
        }
    }

    private fun min(vararg values: Double): Double {
        return values.minOrNull() ?: 0.0
    }
}



