package com.example.mukmuk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visit_records")
data class VisitRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val restaurantName: String,
    val category: String = "",
    val myRating: Float = 0f,
    val visited: Boolean = false,
    val visitDate: Long? = null,
    val address: String = "",
    val phone: String = "",
    val placeUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
