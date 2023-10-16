package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.UserData
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    usersRepository: UsersRepository,
    periodRepository: PeriodRepository
) : UserDataRepository {

    override val userData: Flow<UserData> = combine(
        usersRepository.currentUser,
        preferencesRepository.themePreferences
    ) { user, themePreferences ->
        UserData(
            user = user,
            themePreferences = themePreferences
        )
    }

    override val sharedState: Flow<SharedState> = combine(
        usersRepository.currentUser,
        usersRepository.getUsers(),
        periodRepository.getPeriodState()
    ) { currentUser, users, periodState ->
        SharedState(
            currentUser = currentUser,
            usersMap = users.associateBy { it.uid },
            periodState = periodState
        )
    }


    override suspend fun updateThemePreferences(themePreferences: ThemePreferences) {
        preferencesRepository.updateThemePreferences(themePreferences)
    }
}