package com.github.mantasjasikenas.core.common.localization

import androidx.annotation.StringRes
import com.github.mantasjasikenas.core.common.R

enum class Language(
    @StringRes val titleResourceId: Int,
    val iso: String
) {
    English(
        titleResourceId = R.string.language_en,
        iso = "en"
    ),
    Lithuanian(
        titleResourceId = R.string.language_lt,
        iso = "lt"
    )
}