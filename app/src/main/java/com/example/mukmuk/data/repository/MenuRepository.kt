package com.example.mukmuk.data.repository

import androidx.compose.ui.graphics.Color
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.Menu

object MenuRepository {
    val allMenus: List<Menu> = listOf(
        Menu("김치찌개", "\uD83C\uDF72", Category.KOREAN, Color(0xFFE74C3C)),
        Menu("돈까스", "\uD83E\uDD69", Category.JAPANESE, Color(0xFFF39C12)),
        Menu("짜장면", "\uD83C\uDF5C", Category.CHINESE, Color(0xFF2C3E50)),
        Menu("파스타", "\uD83C\uDF5D", Category.WESTERN, Color(0xFF27AE60)),
        Menu("초밥", "\uD83C\uDF63", Category.JAPANESE, Color(0xFFE91E63)),
        Menu("떡볶이", "\uD83C\uDF36\uFE0F", Category.SNACK, Color(0xFFFF5722)),
        Menu("삼겹살", "\uD83E\uDD53", Category.KOREAN, Color(0xFFD35400)),
        Menu("햄버거", "\uD83C\uDF54", Category.WESTERN, Color(0xFF8E44AD)),
        Menu("쌀국수", "\uD83C\uDF5C", Category.SOUTHEAST_ASIAN, Color(0xFF1ABC9C)),
        Menu("피자", "\uD83C\uDF55", Category.WESTERN, Color(0xFFC0392B)),
        Menu("비빔밥", "\uD83C\uDF5A", Category.KOREAN, Color(0xFF2ECC71)),
        Menu("라멘", "\uD83C\uDF5C", Category.JAPANESE, Color(0xFFF1C40F)),
    )

    fun getFilteredMenus(selectedCategories: Set<Category>): List<Menu> {
        return if (selectedCategories.isEmpty()) allMenus
        else allMenus.filter { it.category in selectedCategories }
    }
}
