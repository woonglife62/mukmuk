package com.example.mukmuk

import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.repository.RestaurantRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantRepositoryTest {

    @Test
    fun getRestaurants_knownMenu_returnsSpecific() {
        val result = RestaurantRepository.getRestaurants("김치찌개")
        assertEquals(3, result.size)
        assertTrue(result.any { it.name == "시골집 김치찌개" })
    }

    @Test
    fun getRestaurants_unknownMenu_returnsDefaults() {
        val result = RestaurantRepository.getRestaurants("없는메뉴")
        assertEquals(3, result.size)
        assertTrue(result.any { it.name == "맛있는 식당" })
    }

    @Test
    fun getRestaurantsByCategory_null_returnsAll() {
        val result = RestaurantRepository.getRestaurantsByCategory(null)
        assertEquals(RestaurantRepository.allRestaurants.size, result.size)
    }

    @Test
    fun getRestaurantsByCategory_korean_filtersCorrectly() {
        val result = RestaurantRepository.getRestaurantsByCategory(Category.KOREAN)
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.category == Category.KOREAN })
    }

    @Test
    fun searchRestaurants_blank_returnsAll() {
        val result = RestaurantRepository.searchRestaurants("")
        assertEquals(RestaurantRepository.allRestaurants.size, result.size)
    }

    @Test
    fun searchRestaurants_matchingQuery_returnsFiltered() {
        val result = RestaurantRepository.searchRestaurants("김치")
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.name.contains("김치") })
    }

    @Test
    fun searchRestaurants_noMatch_returnsEmpty() {
        val result = RestaurantRepository.searchRestaurants("존재하지않는식당")
        assertTrue(result.isEmpty())
    }

    @Test
    fun allRestaurants_haveValidCoordinates() {
        RestaurantRepository.allRestaurants.forEach { restaurant ->
            assertTrue(restaurant.latitude in 33.0..43.0) // Korean peninsula range
            assertTrue(restaurant.longitude in 124.0..132.0)
        }
    }

    @Test
    fun allRestaurants_haveValidRatings() {
        RestaurantRepository.allRestaurants.forEach { restaurant ->
            assertTrue(restaurant.rating in 0f..5f)
            assertTrue(restaurant.reviews > 0)
        }
    }
}
