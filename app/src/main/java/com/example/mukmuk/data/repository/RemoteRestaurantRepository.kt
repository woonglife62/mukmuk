package com.example.mukmuk.data.repository

import com.example.mukmuk.BuildConfig
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.remote.KakaoLocalApi
import com.example.mukmuk.data.remote.dto.KakaoPlace
import java.util.concurrent.ConcurrentHashMap

class RemoteRestaurantRepository(
    private val kakaoApi: KakaoLocalApi
) {
    private data class CacheEntry(val results: List<Restaurant>, val timestamp: Long)
    private val cache = ConcurrentHashMap<String, CacheEntry>()
    private val cacheTtlMs = 5 * 60 * 1000L // 5 minutes

    private fun cacheKey(menuName: String, lat: Double, lng: Double, radius: Int = 2000): String {
        val latRounded = "%.3f".format(lat)
        val lngRounded = "%.3f".format(lng)
        return "$menuName|$latRounded|$lngRounded|$radius"
    }

    suspend fun searchNearby(menuName: String, lat: Double, lng: Double, radius: Int = 2000): List<Restaurant> {
        val key = cacheKey(menuName, lat, lng, radius)
        val cached = cache[key]
        if (cached != null && System.currentTimeMillis() - cached.timestamp < cacheTtlMs) {
            return cached.results
        }

        val apiKey = BuildConfig.KAKAO_REST_API_KEY
        if (apiKey.isBlank()) {
            return RestaurantRepository.getRestaurants(menuName)
        }

        return try {
            val response = kakaoApi.searchByKeyword(
                auth = "KakaoAK $apiKey",
                query = menuName,
                longitude = lng.toString(),
                latitude = lat.toString(),
                radius = radius
            )
            val results = if (response.documents.isEmpty()) {
                RestaurantRepository.getRestaurants(menuName)
            } else {
                response.documents.map { it.toRestaurant() }
            }
            cache[key] = CacheEntry(results, System.currentTimeMillis())
            results
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

        val category = parseCategoryName(category_name)

        return Restaurant(
            name = place_name,
            rating = 0f,
            distance = distanceStr,
            reviews = 0,
            category = category,
            latitude = y.toDoubleOrNull() ?: 0.0,
            longitude = x.toDoubleOrNull() ?: 0.0,
            address = road_address_name.ifBlank { address_name },
            phone = phone,
            placeUrl = place_url
        )
    }

    private fun parseCategoryName(categoryName: String): Category? {
        return when {
            categoryName.contains("한식") -> Category.KOREAN
            categoryName.contains("일식") -> Category.JAPANESE
            categoryName.contains("중식") -> Category.CHINESE
            categoryName.contains("양식") -> Category.WESTERN
            categoryName.contains("분식") -> Category.SNACK
            categoryName.contains("동남아") -> Category.SOUTHEAST_ASIAN
            else -> null
        }
    }
}
