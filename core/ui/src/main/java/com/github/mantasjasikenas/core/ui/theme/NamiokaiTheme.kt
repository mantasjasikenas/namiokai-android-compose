package com.github.mantasjasikenas.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.model.theme.ThemeType


@Composable
fun NamiokaiTheme(
    themePreferences: ThemePreferences,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(
        themePreferences = themePreferences
    )
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(
                window,
                view
            ).isAppearanceLightStatusBars =
                themePreferences.themeType == ThemeType.LIGHT || (themePreferences.themeType == ThemeType.AUTOMATIC && !isSystemInDarkTheme)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}






