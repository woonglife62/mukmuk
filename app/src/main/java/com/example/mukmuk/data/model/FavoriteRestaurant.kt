package com.example.mukmuk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurants")
data class FavoriteRestaurant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val restaurantName: String,
    val timestamp: Long = System.currentTimeMillis()
)
