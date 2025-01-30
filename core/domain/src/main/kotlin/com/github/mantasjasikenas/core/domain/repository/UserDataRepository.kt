package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.UserData
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Stream of [SharedState]
     */
    val sharedState: Flow<SharedState>

    /**
     * Update [ThemePreferences]
     */
    suspend fun updateThemePreferences(themePreferences: ThemePreferences)


}