package com.github.mantasjasikenas.core.domain.model

data class SignInResult(
    val user: User?,
    val errorMessage: String?
)
