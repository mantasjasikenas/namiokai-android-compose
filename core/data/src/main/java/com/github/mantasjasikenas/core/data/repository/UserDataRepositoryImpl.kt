@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.data.repository.preferences.PreferencesRepository
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.UserData
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    spaceRepository: SpaceRepository,
    usersRepository: UsersRepository,
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
        spaceRepository.getCurrentUserSpaces()
    ) { currentUser, spaces ->
        SharedState(
            currentUser = currentUser,
            spaces = spaces
        )
    }.flatMapLatest { state ->
        val usersIds = state.spaces.flatMap { it.memberIds }.distinct()

        usersRepository.getUsers(usersIds)
            .map { users -> state.copy(spaceUsers = users.associateBy { it.uid }) }
    }


    override suspend fun updateThemePreferences(themePreferences: ThemePreferences) {
        preferencesRepository.updateThemePreferences(themePreferences)
    }
}