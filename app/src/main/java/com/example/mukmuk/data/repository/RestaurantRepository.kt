package com.example.mukmuk.data.repository

import com.example.mukmuk.data.model.Restaurant

object RestaurantRepository {
    private val restaurantMap: Map<String, List<Restaurant>> = mapOf(
        "김치찌개" to listOf(
            Restaurant("시골집 김치찌개", 4.5f, "350m", 328),
            Restaurant("어머니 손맛", 4.3f, "500m", 215),
            Restaurant("종로 김치찌개", 4.7f, "800m", 512),
        ),
        "돈까스" to listOf(
            Restaurant("카츠하우스", 4.6f, "200m", 445),
            Restaurant("경양식 돈까스", 4.2f, "650m", 178),
            Restaurant("사보텐", 4.4f, "1.2km", 367),
        ),
    )

    private val defaultRestaurants = listOf(
        Restaurant("맛있는 식당", 4.4f, "300m", 256),
        Restaurant("인기 맛집", 4.6f, "550m", 412),
        Restaurant("동네 단골집", 4.3f, "750m", 189),
    )

    fun getRestaurants(menuName: String): List<Restaurant> {
        return restaurantMap[menuName] ?: defaultRestaurants
    }
}
