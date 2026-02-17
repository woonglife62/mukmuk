package com.example.mukmuk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class MenuEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val category: String,  // Category.name
    val color: Long,       // Compose Color.value.toLong()
    val isPreset: Boolean = false
)
