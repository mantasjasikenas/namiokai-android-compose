package com.github.mantasjasikenas.core.domain.model.theme

import androidx.annotation.StringRes
import com.github.mantasjasikenas.core.domain.R

enum class Theme(
    @StringRes val titleResId: Int,
) {
    DEFAULT(R.string.theme_default),
    DYNAMIC(R.string.theme_dynamic),
    CUSTOM(R.string.theme_custom),
    AMOLED(R.string.theme_amoled);

    companion object {
        val darkColorThemes = listOf(
            DEFAULT,
            DYNAMIC,
            CUSTOM,
            AMOLED,
        )

        val lightColorThemes = listOf(
            DEFAULT,
            DYNAMIC,
            CUSTOM,
        )
    }
}