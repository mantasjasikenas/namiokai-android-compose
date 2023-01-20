package com.example.namiokai.ui.main

import com.example.namiokai.model.User

data class MainUiState(val users: List<User> = emptyList(), val currentUser: User = User())
