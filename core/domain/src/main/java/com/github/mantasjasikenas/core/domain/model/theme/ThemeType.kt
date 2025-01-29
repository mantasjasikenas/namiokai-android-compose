package com.github.mantasjasikenas.core.domain.model.theme

import androidx.annotation.StringRes
import com.github.mantasjasikenas.core.domain.R

enum class ThemeType(
    name: String,
    @StringRes val titleResourceId: Int
) {
    DARK("Dark", R.string.theme_type_dark),
    LIGHT("Light", R.string.theme_type_light),
    AUTOMATIC("Automatic", R.string.theme_type_automatic);
}