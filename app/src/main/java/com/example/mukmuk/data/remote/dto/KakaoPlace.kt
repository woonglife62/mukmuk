package com.example.mukmuk.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class KakaoPlace(
    val place_name: String,
    val distance: String,
    val phone: String,
    val address_name: String,
    val road_address_name: String,
    val x: String,
    val y: String,
    val place_url: String,
    val category_name: String
)
