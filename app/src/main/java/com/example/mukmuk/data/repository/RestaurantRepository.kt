package com.example.mukmuk.data.repository

import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.data.repository.MenuRepository

object RestaurantRepository {
    private val restaurantMap: Map<String, List<Restaurant>> = mapOf(
        "김치찌개" to listOf(
            Restaurant("시골집 김치찌개", 4.5f, "350m", 328, Category.KOREAN, 37.4989, 127.0285, "8,000~10,000원", "10:30-21:00"),
            Restaurant("어머니 손맛", 4.3f, "500m", 215, Category.KOREAN, 37.4970, 127.0290, "7,000~9,000원", "11:00-20:30"),
            Restaurant("종로 김치찌개", 4.7f, "800m", 512, Category.KOREAN, 37.4960, 127.0310, "8,000~12,000원", "10:00-22:00"),
        ),
        "돈까스" to listOf(
            Restaurant("카츠하우스", 4.6f, "200m", 445, Category.JAPANESE, 37.4985, 127.0270, "12,000~18,000원", "11:00-21:00"),
            Restaurant("경양식 돈까스", 4.2f, "650m", 178, Category.JAPANESE, 37.4965, 127.0260, "9,000~13,000원", "11:30-20:30"),
            Restaurant("사보텐", 4.4f, "1.2km", 367, Category.JAPANESE, 37.4950, 127.0250, "14,000~22,000원", "11:00-22:00"),
        ),
        "짜장면" to listOf(
            Restaurant("북경반점", 4.3f, "300m", 278, Category.CHINESE, 37.4983, 127.0288, "7,000~12,000원", "10:30-21:00"),
            Restaurant("홍콩반점", 4.5f, "500m", 389, Category.CHINESE, 37.4972, 127.0295, "6,000~10,000원", "10:00-22:00"),
            Restaurant("만리장성", 4.4f, "700m", 245, Category.CHINESE, 37.4962, 127.0300, "7,000~11,000원", "10:30-21:30"),
        ),
        "파스타" to listOf(
            Restaurant("파스타 팩토리", 4.4f, "350m", 298, Category.WESTERN, 37.4986, 127.0274, "13,000~19,000원", "11:00-21:30"),
            Restaurant("올리브 가든", 4.5f, "600m", 332, Category.WESTERN, 37.4974, 127.0268, "14,000~22,000원", "11:00-22:00"),
            Restaurant("이탈리안 키친", 4.3f, "800m", 187, Category.WESTERN, 37.4963, 127.0258, "12,000~18,000원", "11:30-21:00"),
        ),
        "초밥" to listOf(
            Restaurant("스시히로", 4.7f, "450m", 523, Category.JAPANESE, 37.4978, 127.0268, "15,000~30,000원", "11:30-22:00"),
            Restaurant("쿠루쿠루 스시", 4.3f, "300m", 412, Category.JAPANESE, 37.4984, 127.0277, "12,000~20,000원", "11:00-21:30"),
            Restaurant("오마카세 스시", 4.8f, "1.0km", 198, Category.JAPANESE, 37.4955, 127.0245, "30,000~60,000원", "12:00-22:00"),
        ),
        "삼겹살" to listOf(
            Restaurant("삼겹살 명가", 4.6f, "400m", 467, Category.KOREAN, 37.4975, 127.0292, "14,000~18,000원", "16:00-01:00"),
            Restaurant("숯불가든", 4.5f, "550m", 345, Category.KOREAN, 37.4969, 127.0298, "15,000~20,000원", "16:00-00:00"),
            Restaurant("고기굽는 집", 4.4f, "700m", 223, Category.KOREAN, 37.4960, 127.0305, "13,000~17,000원", "17:00-01:00"),
        ),
        "햄버거" to listOf(
            Restaurant("버거 킹스", 4.2f, "200m", 445, Category.WESTERN, 37.4987, 127.0279, "8,000~14,000원", "10:00-23:00"),
            Restaurant("수제버거 공방", 4.6f, "450m", 312, Category.WESTERN, 37.4976, 127.0271, "12,000~18,000원", "11:00-22:00"),
            Restaurant("바이트 버거", 4.4f, "650m", 198, Category.WESTERN, 37.4966, 127.0263, "10,000~16,000원", "10:30-22:00"),
        ),
        "쌀국수" to listOf(
            Restaurant("쌀국수 하노이", 4.5f, "500m", 267, Category.SOUTHEAST_ASIAN, 37.4973, 127.0269, "9,000~13,000원", "11:00-21:00"),
            Restaurant("포 사이공", 4.4f, "350m", 234, Category.SOUTHEAST_ASIAN, 37.4981, 127.0273, "9,000~12,000원", "11:00-21:30"),
            Restaurant("베트남 식당", 4.3f, "700m", 178, Category.SOUTHEAST_ASIAN, 37.4964, 127.0257, "8,000~12,000원", "10:30-21:00"),
        ),
        "피자" to listOf(
            Restaurant("피자 나폴리", 4.6f, "400m", 356, Category.WESTERN, 37.4980, 127.0265, "15,000~25,000원", "11:30-22:00"),
            Restaurant("화덕피자 장인", 4.5f, "550m", 289, Category.WESTERN, 37.4971, 127.0260, "16,000~28,000원", "11:00-22:00"),
            Restaurant("피자 마르게리타", 4.3f, "800m", 178, Category.WESTERN, 37.4958, 127.0252, "13,000~20,000원", "11:00-21:30"),
        ),
        "비빔밥" to listOf(
            Restaurant("비빔밥 전문점", 4.3f, "600m", 201, Category.KOREAN, 37.4968, 127.0275, "8,000~11,000원", "10:00-20:30"),
            Restaurant("한식당 미소", 4.5f, "250m", 384, Category.KOREAN, 37.4988, 127.0283, "9,000~14,000원", "10:30-21:30"),
            Restaurant("전주비빔밥", 4.6f, "500m", 312, Category.KOREAN, 37.4974, 127.0286, "9,000~13,000원", "10:00-21:00"),
        ),
        "라멘" to listOf(
            Restaurant("라멘 이치란", 4.4f, "550m", 312, Category.JAPANESE, 37.4970, 127.0262, "10,000~14,000원", "11:00-21:30"),
            Restaurant("멘야 하나비", 4.6f, "400m", 278, Category.JAPANESE, 37.4979, 127.0267, "11,000~15,000원", "11:00-22:00"),
            Restaurant("라멘 가게", 4.3f, "750m", 189, Category.JAPANESE, 37.4961, 127.0255, "9,000~13,000원", "11:30-21:00"),
        ),
        "떡볶이" to listOf(
            Restaurant("떡볶이 천국", 4.3f, "150m", 512, Category.SNACK, 37.4990, 127.0278, "4,000~7,000원", "10:00-22:00"),
            Restaurant("신전떡볶이", 4.1f, "300m", 445, Category.SNACK, 37.4984, 127.0282, "4,000~8,000원", "10:00-23:00"),
            Restaurant("엽기떡볶이", 4.4f, "500m", 367, Category.SNACK, 37.4973, 127.0275, "5,000~9,000원", "11:00-22:00"),
        ),
    )

    private val defaultRestaurants = listOf(
        Restaurant("맛있는 식당", 4.4f, "300m", 256, priceRange = "8,000~15,000원", hours = "10:30-21:30"),
        Restaurant("인기 맛집", 4.6f, "550m", 412, priceRange = "10,000~18,000원", hours = "11:00-22:00"),
        Restaurant("동네 단골집", 4.3f, "750m", 189, priceRange = "7,000~12,000원", hours = "10:00-21:00"),
    )

    val allRestaurants: List<Restaurant> = listOf(
        Restaurant("한식당 미소", 4.5f, "250m", 384, Category.KOREAN, 37.4988, 127.0283, "9,000~14,000원", "10:30-21:30"),
        Restaurant("시골집 김치찌개", 4.5f, "350m", 328, Category.KOREAN, 37.4989, 127.0285, "8,000~10,000원", "10:30-21:00"),
        Restaurant("삼겹살 명가", 4.6f, "400m", 467, Category.KOREAN, 37.4975, 127.0292, "14,000~18,000원", "16:00-01:00"),
        Restaurant("비빔밥 전문점", 4.3f, "600m", 201, Category.KOREAN, 37.4968, 127.0275, "8,000~11,000원", "10:00-20:30"),
        Restaurant("카츠하우스", 4.6f, "200m", 445, Category.JAPANESE, 37.4985, 127.0270, "12,000~18,000원", "11:00-21:00"),
        Restaurant("스시히로", 4.7f, "450m", 523, Category.JAPANESE, 37.4978, 127.0268, "15,000~30,000원", "11:30-22:00"),
        Restaurant("라멘 이치란", 4.4f, "550m", 312, Category.JAPANESE, 37.4970, 127.0262, "10,000~14,000원", "11:00-21:30"),
        Restaurant("북경반점", 4.3f, "300m", 278, Category.CHINESE, 37.4983, 127.0288, "7,000~12,000원", "10:30-21:00"),
        Restaurant("홍콩반점", 4.5f, "500m", 389, Category.CHINESE, 37.4972, 127.0295, "6,000~10,000원", "10:00-22:00"),
        Restaurant("파스타 팩토리", 4.4f, "350m", 298, Category.WESTERN, 37.4986, 127.0274, "13,000~19,000원", "11:00-21:30"),
        Restaurant("피자 나폴리", 4.6f, "400m", 356, Category.WESTERN, 37.4980, 127.0265, "15,000~25,000원", "11:30-22:00"),
        Restaurant("버거 킹스", 4.2f, "200m", 445, Category.WESTERN, 37.4987, 127.0279, "8,000~14,000원", "10:00-23:00"),
        Restaurant("떡볶이 천국", 4.3f, "150m", 512, Category.SNACK, 37.4990, 127.0278, "4,000~7,000원", "10:00-22:00"),
        Restaurant("김밥천국", 4.1f, "250m", 678, Category.SNACK, 37.4988, 127.0282, "3,000~6,000원", "06:00-22:00"),
        Restaurant("쌀국수 하노이", 4.5f, "500m", 267, Category.SOUTHEAST_ASIAN, 37.4973, 127.0269, "9,000~13,000원", "11:00-21:00"),
        Restaurant("팟타이 방콕", 4.4f, "600m", 198, Category.SOUTHEAST_ASIAN, 37.4968, 127.0260, "10,000~15,000원", "11:30-21:30"),
    )

    fun getRestaurants(menuName: String): List<Restaurant> {
        restaurantMap[menuName]?.let { return it }

        // Find the menu's category and return matching restaurants from allRestaurants
        val menuCategory = MenuRepository.allMenus.find { it.name == menuName }?.category
        if (menuCategory != null) {
            val categoryResults = allRestaurants.filter { it.category == menuCategory }
            if (categoryResults.isNotEmpty()) return categoryResults
        }

        return defaultRestaurants
    }

    fun getRestaurantsByCategory(category: Category?): List<Restaurant> {
        return if (category == null) allRestaurants
        else allRestaurants.filter { it.category == category }
    }

    fun searchRestaurants(query: String): List<Restaurant> {
        if (query.isBlank()) return allRestaurants
        return allRestaurants.filter { it.name.contains(query, ignoreCase = true) }
    }
}
