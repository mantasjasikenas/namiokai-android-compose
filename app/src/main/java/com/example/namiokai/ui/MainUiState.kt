package com.example.namiokai.ui

import com.example.namiokai.model.User

data class MainUiState(val users: List<User> = emptyList(), val user: User = User())
