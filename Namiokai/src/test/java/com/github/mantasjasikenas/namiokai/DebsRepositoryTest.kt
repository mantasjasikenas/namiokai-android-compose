package com.github.mantasjasikenas.namiokai

import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsRepository
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.model.User


class DebsRepositoryTest {

    private val repo = DebtsRepository()
    private lateinit var purchaseBills: MutableList<PurchaseBill>

    private val sigisUser = User(displayName = "Sigis", uid = "1")
    private val mantelisUser = User(displayName = "Mantelis", uid = "2")


    /*    @Before
        fun setup() {
            bills = mutableListOf(
                Bill(
                    paymasterUid = mantelisUser,
                    total = 15.0,
                    splitUsersUid = listOf(sigisUser, mantelisUser)
                ),
                Bill(
                    paymasterUid = sigisUser,
                    total = 15.0,
                    splitUsersUid = listOf(sigisUser, mantelisUser)
                ),
                Bill(paymasterUid = sigisUser, total = 15.0, splitUsersUid = listOf(mantelisUser)),
                Bill(
                    paymasterUid = mantelisUser,
                    total = 15.0,
                    splitUsersUid = listOf(sigisUser, mantelisUser)
                ),
            )
        }

        @Test
        fun `test calculateDebts`() {
            repo.calculateBillDebts(bills)

            assert(
                repo.getDebt(
                    userWhoPays = sigisUser,
                    payToUser = mantelisUser
                ) == 0.0
            ) { "Sigis should not owe Mantelis" }
            assert(
                repo.getDebt(
                    userWhoPays = mantelisUser,
                    payToUser = sigisUser
                ) == 7.5
            ) { "Mantelis should owe Sigis 7.5" }

        }*/


}