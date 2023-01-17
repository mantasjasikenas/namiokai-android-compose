package com.example.namiokai.data.repository.debts

import com.example.namiokai.model.Bill
import com.example.namiokai.model.Fuel
import com.example.namiokai.model.User
import com.example.namiokai.model.tripPricePerUser
import kotlin.math.min

typealias DebtsHashMap = HashMap<User, HashMap<User, Double>>
typealias UserDebtsHashMap = HashMap<User, Double>

class DebtsRepository {

    private val debts = DebtsHashMap()


    private fun clear() {
        this.debts.clear()
    }

    fun getDebts(): DebtsHashMap {
        return debts
    }

    fun getUserDebts(user: User): UserDebtsHashMap {
        return debts[user] ?: HashMap()
    }

    fun getDebt(userWhoPays: User, payToUser: User): Double {
        return debts[userWhoPays]?.get(payToUser) ?: 0.0
    }

    fun calculateDebts(
        users: List<User>,
        bills: List<Bill>,
        fuels: List<Fuel>
    ): DebtsHashMap {

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
    ): DebtsHashMap {

        bills.forEach { bill ->
            val splitUsers = bill.splitUsers
            val paymaster = bill.paymaster
            val total = bill.total
            val amountPerUser = total / splitUsers.count()

            splitUsers.forEach loop@{ splitUser ->
                if (splitUser.uid == paymaster.uid)
                    return@loop

                val currentDebt = debts[splitUser]?.get(paymaster) ?: 0.0
                val calculatedDebt = currentDebt + amountPerUser

                debts.putIfAbsent(splitUser, HashMap())
                debts[splitUser]!![paymaster] = calculatedDebt

                val currentDebtPaymaster = debts[paymaster]?.get(splitUser) ?: 0.0
                val minValue = min(calculatedDebt, currentDebtPaymaster)

                if (minValue > 0) {
                    debts[splitUser]!![paymaster] = calculatedDebt - minValue
                    debts[paymaster]!![splitUser] = currentDebtPaymaster - minValue
                }
            }
        }

        return debts
    }

    /**
     * Calculates debts for fuels
     */
    private fun calculateFuelDebts(fuels: List<Fuel>): DebtsHashMap {

        fuels.forEach { fuel ->
            val driver = fuel.driver
            val passengers = fuel.passengers
            val tripPricePerUser = fuel.tripPricePerUser()

            passengers.forEach loop@{ passenger ->
                if (passenger.uid == driver.uid)
                    return@loop

                val currentDebt = debts[passenger]?.get(driver) ?: 0.0
                val calculatedDebt = currentDebt + tripPricePerUser

                debts.putIfAbsent(passenger, HashMap())
                debts[passenger]!![driver] = calculatedDebt

                val currentDebtPaymaster = debts[driver]?.get(passenger) ?: 0.0
                val minValue = min(calculatedDebt, currentDebtPaymaster)

                if (minValue > 0) {
                    debts[passenger]!![driver] = calculatedDebt - minValue
                    debts[driver]!![passenger] = currentDebtPaymaster - minValue
                }
            }
        }

        return debts
    }


}