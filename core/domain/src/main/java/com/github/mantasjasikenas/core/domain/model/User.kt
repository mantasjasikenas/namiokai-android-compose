package com.github.mantasjasikenas.core.domain.model

import com.github.mantasjasikenas.core.common.util.DisplayName
import com.github.mantasjasikenas.core.common.util.Uid
import com.github.mantasjasikenas.core.common.util.UserUid
import com.google.firebase.auth.FirebaseUser
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val displayName: String = "",
    val email: String = "",
    val uid: String = "",
    val photoUrl: String = "",
    val admin: Boolean = false
)

typealias UsersMap = Map<UserUid, User>

fun User.toUidAndDisplayNamePair(): Pair<Uid, DisplayName> {
    return Pair(uid, displayName)
}

fun User.isNotLoggedIn(): Boolean {
    return uid == ""
}

fun FirebaseUser.toUser(): User = User(
    displayName = displayName ?: "",
    email = email ?: "",
    uid = uid,
    photoUrl = photoUrl?.toString() ?: ""
)






