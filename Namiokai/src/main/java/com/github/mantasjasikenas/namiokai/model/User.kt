package com.github.mantasjasikenas.namiokai.model

import kotlinx.serialization.Serializable

typealias Uid = String
typealias DisplayName = String

@Serializable
data class User(
    val displayName: String = "",
    val email: String = "",
    val uid: String = "",
    val photoUrl: String = "",
    val admin: Boolean = false
)

fun User.toUidAndDisplayNamePair(): Pair<Uid, DisplayName> {
    return Pair(uid, displayName)
}




