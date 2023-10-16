package com.github.mantasjasikenas.core.domain.model

data class SharedState(
    val currentUser: User = User(),
    val usersMap: UsersMap = emptyMap(),
    val periodState: PeriodState = PeriodState(),
)