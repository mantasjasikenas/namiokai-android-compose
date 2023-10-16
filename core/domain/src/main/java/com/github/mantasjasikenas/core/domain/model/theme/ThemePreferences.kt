package com.github.mantasjasikenas.core.domain.model.theme

import androidx.compose.ui.graphics.Color

data class ThemePreferences(
    val themeType: ThemeType = ThemeType.AUTOMATIC,
    val theme: Theme = Theme.DEFAULT,
    val accentColor: Color = Color.Unspecified
)