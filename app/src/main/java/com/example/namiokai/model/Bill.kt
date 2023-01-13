package com.example.namiokai.model

data class Bill(
    var date: String = "",
    var paymaster: User = User(),
    var shoppingList: String = "",
    var total: Double = 0.0,
    var splitUsers: List<User> = emptyList()
)

fun Bill.isValid(): Boolean {
    return paymaster.uid.isNotEmpty() && shoppingList.isNotEmpty() && total > 0.0 && splitUsers.isNotEmpty()
}

fun Bill.splitPricePerUser(): Double {
    return total / splitUsers.count()
}



