package com.example.namiokai

import com.example.namiokai.data.repository.debts.DebtsRepository
import com.example.namiokai.model.Bill
import com.example.namiokai.model.User
import org.junit.Before
import org.junit.Test


class DebsRepositoryTest {

    private val repo = DebtsRepository()
    private lateinit var bills: MutableList<Bill>

    private val sigisUser = User(displayName = "Sigis", uid = "1")
    private val mantelisUser = User(displayName = "Mantelis", uid = "2")


    @Before
    fun setup() {
        bills = mutableListOf(
            Bill(
                paymaster = mantelisUser,
                total = 15.0,
                splitUsers = listOf(sigisUser, mantelisUser)
            ),
            Bill(paymaster = sigisUser, total = 15.0, splitUsers = listOf(sigisUser, mantelisUser)),
            Bill(paymaster = sigisUser, total = 15.0, splitUsers = listOf(mantelisUser)),
            Bill(paymaster = mantelisUser, total = 15.0, splitUsers = listOf(sigisUser, mantelisUser)),
        )
    }

    @Test
    fun `test calculateDebts`() {
        repo.calculateBillDebts(bills)

        assert(repo.getDebt(userWhoPays = sigisUser, payToUser = mantelisUser) == 0.0) { "Sigis should not owe Mantelis" }
        assert(repo.getDebt(userWhoPays = mantelisUser, payToUser = sigisUser) == 7.5) { "Mantelis should owe Sigis 7.5" }

    }


}