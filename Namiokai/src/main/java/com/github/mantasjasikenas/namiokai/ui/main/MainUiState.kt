package com.github.mantasjasikenas.namiokai.ui.main

import com.github.mantasjasikenas.namiokai.data.repository.debts.UserUid
import com.github.mantasjasikenas.namiokai.model.Period
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.model.theme.ThemePreferences

typealias UsersMap = Map<UserUid, User>

data class MainUiState(
    val usersMap: UsersMap = emptyMap(),
    val currentUser: User = User(),
    val isLoading: Boolean = true,
    val themePreferences: ThemePreferences = ThemePreferences(),
)

data class PeriodUiState(
    val currentPeriod: Period,
    val userSelectedPeriod: Period,
)
