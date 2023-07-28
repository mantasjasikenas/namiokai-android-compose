package com.github.mantasjasikenas.namiokai.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.github.mantasjasikenas.namiokai.model.theme.ThemePreferences


@Composable
fun NamiokaiTheme(
    themePreferences: ThemePreferences,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(
        themePreferences = themePreferences
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(
                window,
                view
            ).isAppearanceLightStatusBars = themePreferences.themeType.isDark()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}






