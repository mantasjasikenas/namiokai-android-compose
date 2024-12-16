package com.github.mantasjasikenas.core.data.repository.debts

import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.debts.DebtBill
import com.github.mantasjasikenas.core.domain.model.debts.DebtsMap
import com.github.mantasjasikenas.core.domain.model.debts.MutableDebtsMap
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository

class DebtsRepositoryImpl : DebtsRepository {
    override fun calculateDebts(bills: List<Bill>): DebtsMap {
        val debtsMap = MutableDebtsMap()

        bills.forEach { bill ->
            processBill(bill, debtsMap)
        }

        debtsMap.keys.forEach { fromUser ->
            debtsMap.getUserDebts(fromUser).keys.forEach { toUser ->
                balanceDebts(fromUser, toUser, debtsMap)
            }
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
            }
        }
    }

    private fun addDebt(
        fromUser: UserUid, toUser: UserUid, amount: Double, bill: Bill, debtsMap: MutableDebtsMap
    ) {
        debtsMap.addDebt(fromUser, toUser, DebtBill(amount, bill))
    }

    private fun balanceDebts(firstUser: UserUid, secondUser: UserUid, debtsMap: MutableDebtsMap) {
        val firstUserDebts = debtsMap.getDebts(firstUser, secondUser).toMutableList()
        val secondUserDebts = debtsMap.getDebts(secondUser, firstUser).toMutableList()

        val totalFirstUserDebt = firstUserDebts.sumOf { it.amount }
        val totalSecondUserDebt = secondUserDebts.sumOf { it.amount }

        if (totalFirstUserDebt <= 0 || totalSecondUserDebt <= 0) return

        // Debts are equal
        if (totalFirstUserDebt == totalSecondUserDebt) {
            debtsMap.removeDebt(firstUser, secondUser)
            debtsMap.removeDebt(secondUser, firstUser)

            return
        }

        // Debts are not equal, need to reduce the debt
        val (fromUser, toUser, debts) = if (totalFirstUserDebt < totalSecondUserDebt) {
            Triple(firstUser, secondUser, firstUserDebts)
        } else {
            Triple(secondUser, firstUser, secondUserDebts)
        }

        transferDebts(fromUser, toUser, debts, debtsMap)
    }

    private fun transferDebts(
        fromUser: UserUid,
        toUser: UserUid,
        debts: List<DebtBill>,
        debtsMap: MutableDebtsMap
    ) {
        debts.forEach { debtBill ->
            debtsMap.addDebt(toUser, fromUser, reverseDebt(debtBill))
        }

        debtsMap.removeDebt(fromUser, toUser)
    }

    private fun reverseDebt(debtBill: DebtBill): DebtBill {
        return DebtBill(-debtBill.amount, debtBill.bill)
    }
}



