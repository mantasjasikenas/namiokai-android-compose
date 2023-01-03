package com.example.namiokai.model

data class Bill(
    val date: String = "",
    var paymaster: String = "",
    var shoppingList: String = "",
    var total: Double = 0.0,
    val splitUsers: List<User> = emptyList()
)

