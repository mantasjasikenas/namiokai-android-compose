package com.github.mantasjasikenas.namiokai.model.theme

// other names: ColorSchemeType, ThemeColorsType, ThemeColors, ThemeType
enum class ThemeType(val title: String) {
    DARK("Dark"),
    LIGHT("Light"),
    AUTOMATIC("Automatic");


    companion object {
        fun valueOfOrDefault(
            value: String,
            defaultValue: ThemeType = AUTOMATIC
        ) = values().find { it.title == value || it.name == value } ?: defaultValue
    }

    fun isDark() = this == DARK
}