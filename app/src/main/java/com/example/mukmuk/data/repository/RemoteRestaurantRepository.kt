package com.example.mukmuk.data.repository

import com.example.mukmuk.BuildConfig
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.remote.KakaoLocalApi
import com.example.mukmuk.data.remote.dto.KakaoPlace

class RemoteRestaurantRepository(
    private val kakaoApi: KakaoLocalApi
) {
    suspend fun searchNearby(menuName: String, lat: Double, lng: Double): List<Restaurant> {
        val apiKey = BuildConfig.KAKAO_REST_API_KEY
        if (apiKey.isBlank()) {
            return RestaurantRepository.getRestaurants(menuName)
        }

        return try {
            val response = kakaoApi.searchByKeyword(
                auth = "KakaoAK $apiKey",
                query = menuName,
                longitude = lng.toString(),
                latitude = lat.toString()
            )
            if (response.documents.isEmpty()) {
                RestaurantRepository.getRestaurants(menuName)
            } else {
                response.documents.map { it.toRestaurant() }
            }
        } catch (_: Exception) {
            RestaurantRepository.getRestaurants(menuName)
        }
    }

    private fun KakaoPlace.toRestaurant(): Restaurant {
        val distanceMeters = distance.toIntOrNull() ?: 0
        val distanceStr = if (distanceMeters >= 1000) {
            String.format("%.1fkm", distanceMeters / 1000.0)
        } else {
            "${distanceMeters}m"
        }

        return Restaurant(
            name = place_name,
            rating = 0f,
            distance = distanceStr,
            reviews = 0,
            latitude = y.toDoubleOrNull() ?: 0.0,
            longitude = x.toDoubleOrNull() ?: 0.0,
            address = road_address_name.ifBlank { address_name },
            phone = phone,
            placeUrl = place_url
        )
    }
}
