package com.example.namiokai.model

data class Bill(
    var date: String = "",
    var paymaster: User = User(),
    var shoppingList: String = "",
    var total: Double = 0.0,
    var splitUsers: List<User> = emptyList()
)

