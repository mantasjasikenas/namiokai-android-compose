package com.github.mantasjasikenas.core.domain.model

data class SharedState(
    val currentUser: User = User(),
    val spaceUsers: UsersMap = emptyMap(),
    val spaces: List<Space> = emptyList(),
)