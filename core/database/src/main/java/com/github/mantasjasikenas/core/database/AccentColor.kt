package com.github.mantasjasikenas.core.database

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "accent_colors")
data class AccentColor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: Int,
    val pinned: Boolean = false,
    val date: Long,
) {
    @Ignore
    fun toColor() = Color(this.color)

    @Ignore
    fun toHex() = String.format("#%06X", 0xFFFFFF and color)
}