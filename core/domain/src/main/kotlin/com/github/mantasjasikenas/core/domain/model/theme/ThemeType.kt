package com.github.mantasjasikenas.core.domain.model.theme

import androidx.annotation.StringRes
import com.github.mantasjasikenas.core.domain.R

enum class ThemeType(
    @StringRes val titleResourceId: Int
) {
    DARK(R.string.theme_type_dark),
    LIGHT(R.string.theme_type_light),
    AUTOMATIC(R.string.theme_type_automatic);
}