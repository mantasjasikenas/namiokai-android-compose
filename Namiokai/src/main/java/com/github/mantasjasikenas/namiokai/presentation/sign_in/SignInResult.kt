package com.github.mantasjasikenas.namiokai.presentation.sign_in

import com.github.mantasjasikenas.namiokai.model.User

data class SignInResult(
    val data: User?,
    val errorMessage: String?
)
