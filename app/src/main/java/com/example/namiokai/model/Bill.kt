package com.example.namiokai.model

data class Bill(
    val date: String = "",
    val paymaster: User = User(),
    val shoppingList: String = "",
    val total: Double = 0.0,

    )


/*
public string Date { get; set; }
public string WhoPaid { get; set; }
public string ShoppingList { get; set; }
public bool MantelisSplit { get; set; }
public string TotalPrice { get; set; }
public bool KlaidasSplit { get; set; }
public bool KlaidelisSplit { get; set; }
public double? TotalPerson { get; set; }*/

