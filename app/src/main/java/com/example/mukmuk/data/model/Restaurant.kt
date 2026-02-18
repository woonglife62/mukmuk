package com.example.mukmuk.data.model

data class Restaurant(
    val name: String,
    val rating: Float,
    val distance: String,
    val reviews: Int,
    val category: Category? = null,
    val latitude: Double = 37.4979,
    val longitude: Double = 127.0276,
    val priceRange: String = "",
    val hours: String = "",
    val address: String = "",
    val phone: String = "",
    val placeUrl: String = ""
)
