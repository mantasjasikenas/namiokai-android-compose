package com.github.mantasjasikenas.core.domain.model

import com.github.mantasjasikenas.core.domain.model.space.Space

data class SharedState(
    val currentUser: User = User(),
    val spaceUsers: UsersMap = emptyMap(),
    val spaces: List<Space> = emptyList(),
)