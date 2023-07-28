package com.github.mantasjasikenas.namiokai.model.theme

enum class Theme(
    val title: String,
) {
    DEFAULT("Default"),
    DYNAMIC("Dynamic"),
    CUSTOM("Custom"),
    AMOLED("Amoled");

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

        fun valueOfOrDefault(
            value: String,
            defaultValue: Theme = DEFAULT
        ) = values().find { it.title == value || it.name == value } ?: defaultValue
    }
}