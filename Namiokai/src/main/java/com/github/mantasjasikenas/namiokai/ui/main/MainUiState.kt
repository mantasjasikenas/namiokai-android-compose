package com.github.mantasjasikenas.namiokai.ui.main

import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.User

typealias UsersMap = Map<UserUid, User>

data class MainUiState(
    val usersMap: UsersMap = emptyMap(),
    val currentUser: User = User(),
)
