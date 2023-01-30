package com.example.namiokai.ui.main

import com.example.namiokai.data.repository.debts.UserUid
import com.example.namiokai.model.User

typealias UsersMap = Map<UserUid, User>

data class MainUiState(
    val usersMap: UsersMap = emptyMap(),
    val currentUser: User = User()
)
