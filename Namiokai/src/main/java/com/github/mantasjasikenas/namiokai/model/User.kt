package com.github.mantasjasikenas.namiokai.model

typealias Uid = String
typealias DisplayName = String

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




