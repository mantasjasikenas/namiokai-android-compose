package com.github.mantasjasikenas.core.domain.model

import com.github.mantasjasikenas.core.common.util.UserUid
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val displayName: String = "",
    val email: String = "",
    val uid: String = "",
    val photoUrl: String = "",
    val admin: Boolean = false
) {
    @Exclude
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf(
            displayName,
            email,
            uid,
        )

        return matchingCombinations.any { combination ->
            combination.contains(query, ignoreCase = true)
        }
    }
}

typealias UsersMap = Map<UserUid, User>

fun User.isNotLoggedIn(): Boolean {
    return uid == ""
}

fun FirebaseUser.toUser(): User = User(
    displayName = displayName ?: "",
    email = email ?: "",
    uid = uid,
    photoUrl = photoUrl?.toString() ?: ""
)






