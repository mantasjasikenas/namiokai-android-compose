package com.github.mantasjasikenas.core.domain.model.debts

import com.github.mantasjasikenas.core.common.util.UserUid

interface DebtsMap {
    fun getDebts(fromUser: UserUid, toUser: UserUid): List<DebtBill>
    fun getAllDebts(): Map<String, Map<String, List<DebtBill>>>
    fun getUserDebts(userUid: UserUid): Map<String, List<DebtBill>>
    fun getTotalDebt(userUid: UserUid): Double
    fun getTotalDebtsCount(userUid: UserUid): Int
    fun getTotalOwedToYou(userUid: UserUid): Double
    fun isEmpty(): Boolean
}

class MutableDebtsMap : DebtsMap {
    private val debts: MutableMap<String, MutableMap<String, MutableList<DebtBill>>> =
        mutableMapOf()

    fun addDebt(fromUser: UserUid, toUser: UserUid, debtBill: DebtBill) {
        debts.computeIfAbsent(fromUser) { mutableMapOf() }
            .computeIfAbsent(toUser) { mutableListOf() }
            .add(debtBill)
    }

    fun removeDebt(fromUser: String, toUser: String) {
        debts[fromUser]?.remove(toUser)
    }

    override fun getDebts(fromUser: UserUid, toUser: UserUid): List<DebtBill> {
        return debts[fromUser]?.get(toUser) ?: emptyList()
    }

    override fun getAllDebts(): Map<String, Map<String, List<DebtBill>>> {
        return debts
    }

    override fun getUserDebts(userUid: UserUid): Map<String, List<DebtBill>> {
        return debts[userUid] ?: emptyMap()
    }

    override fun getTotalDebt(userUid: UserUid): Double {
        return debts[userUid]
            ?.values
            ?.flatten()
            ?.sumOf { it.amount } ?: 0.0
    }

    override fun getTotalDebtsCount(userUid: UserUid): Int {
        return debts[userUid]?.size ?: 0
    }

    override fun getTotalOwedToYou(userUid: UserUid): Double {
        return debts
            .values
            .sumOf {
                it[userUid]
                    ?.sumOf { debtBill -> debtBill.amount } ?: 0.0
            }
    }

    override fun isEmpty(): Boolean {
        return debts.isEmpty()
    }
}