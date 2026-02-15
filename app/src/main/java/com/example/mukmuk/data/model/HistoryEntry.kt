package com.example.mukmuk.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val menuName: String,
    val emoji: String,
    val category: String,
    val color: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toMenu(): Menu = Menu(
        name = menuName,
        emoji = emoji,
        category = Category.valueOf(category),
        color = Color(color.toULong())
    )

    companion object {
        fun fromMenu(menu: Menu): HistoryEntry = HistoryEntry(
            menuName = menu.name,
            emoji = menu.emoji,
            category = menu.category.name,
            color = menu.color.value.toLong()
        )
    }
}
