package com.github.mantasjasikenas.core.domain.model

import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences

data class UserData(
    val user: User,
    val themePreferences: ThemePreferences,
)