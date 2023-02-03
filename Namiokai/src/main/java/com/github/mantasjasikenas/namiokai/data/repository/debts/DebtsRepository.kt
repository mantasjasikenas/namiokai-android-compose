package com.github.mantasjasikenas.namiokai.data.repository.debts

import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.model.Fuel
import com.github.mantasjasikenas.namiokai.model.splitPricePerUser
import kotlin.math.min

typealias UserUid = String
typealias DebtsMap = MutableMap<UserUid, MutableMap<UserUid, Double>>
typealias UserDebtsMap = MutableMap<UserUid, Double>

class DebtsRepository {

    private val debts: DebtsMap = mutableMapOf()

    private fun clear() {
        this.debts.clear()
    }

    fun calculateDebts(
        bills: List<Bill>,
        fuels: List<Fuel>
    ): DebtsMap {

        clear()
        calculateBillDebts(bills)
        calculateFuelDebts(fuels)

        return debts
    }

    /**
     * Calculates debts for bills
     */
    private fun calculateBillDebts(
        bills: List<Bill>
    ): DebtsMap {

        bills.forEach { bill ->
            val splitUsersUid = bill.splitUsersUid
            val paymasterUid = bill.paymasterUid
            val amountPerUser = bill.splitPricePerUser()

            splitUsersUid.forEach loop@{ splitUserUid ->
                if (splitUserUid == paymasterUid)
                    return@loop

                val currentDebt = debts[splitUserUid]?.get(paymasterUid) ?: 0.0
                val calculatedDebt = currentDebt + amountPerUser

                debts.putIfAbsent(splitUserUid, HashMap())
                debts[splitUserUid]!![paymasterUid] = calculatedDebt

                val currentDebtPaymaster = debts[paymasterUid]?.get(splitUserUid) ?: 0.0
                val minValue = min(calculatedDebt, currentDebtPaymaster)

                if (minValue > 0) {
                    val recalculatedPaymasterDebt = currentDebtPaymaster - minValue
                    val recalculatedSplitUserDebt = calculatedDebt - minValue

                    debts[splitUserUid]!![paymasterUid] = recalculatedSplitUserDebt
                    debts[paymasterUid]!![splitUserUid] = recalculatedPaymasterDebt

                    if (recalculatedSplitUserDebt == 0.0)
                        debts[splitUserUid]?.remove(paymasterUid)

                    if (recalculatedPaymasterDebt == 0.0)
                        debts[paymasterUid]?.remove(splitUserUid)
                }
            }
        }

        return debts
    }

    /**
     * Calculates debts for fuels
     */
    private fun calculateFuelDebts(fuels: List<Fuel>): DebtsMap {

        fuels.forEach { fuel ->
            val driverUid = fuel.driverUid
            val passengersUid = fuel.passengersUid
            val tripPricePerUser = fuel.tripPricePerUser

            passengersUid.forEach loop@{ passengerUid ->
                if (passengerUid == driverUid)
                    return@loop

                val currentDebt = debts[passengerUid]?.get(driverUid) ?: 0.0
                val calculatedDebt = currentDebt + tripPricePerUser

                debts.putIfAbsent(passengerUid, HashMap())
                debts[passengerUid]!![driverUid] = calculatedDebt

                val currentDebtPaymaster = debts[driverUid]?.get(passengerUid) ?: 0.0
                val minValue = min(calculatedDebt, currentDebtPaymaster)

                if (minValue > 0) {
                    val recalculatedDriverDebt = currentDebtPaymaster - minValue
                    val recalculatedPassengerDebt = calculatedDebt - minValue

                    debts[passengerUid]!![driverUid] = recalculatedPassengerDebt
                    debts[driverUid]!![passengerUid] = recalculatedDriverDebt

                    if (recalculatedPassengerDebt == 0.0)
                        debts[passengerUid]?.remove(driverUid)

                    if (recalculatedDriverDebt == 0.0)
                        debts[driverUid]?.remove(passengerUid)
                }
            }
        }
        return debts
    }

}