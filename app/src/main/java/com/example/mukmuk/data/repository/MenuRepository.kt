package com.example.mukmuk.data.repository

import androidx.compose.ui.graphics.Color
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.Menu

object MenuRepository {
    val allMenus: List<Menu> = listOf(
        // KOREAN (한식) - 10 items
        Menu("김치찌개", "\uD83C\uDF72", Category.KOREAN, Color(0xFFE74C3C)),
        Menu("삼겹살", "\uD83E\uDD53", Category.KOREAN, Color(0xFFD35400)),
        Menu("비빔밥", "\uD83C\uDF5A", Category.KOREAN, Color(0xFF2ECC71)),
        Menu("된장찌개", "\uD83C\uDF72", Category.KOREAN, Color(0xFF795548)),
        Menu("불고기", "\uD83E\uDD69", Category.KOREAN, Color(0xFFBF360C)),
        Menu("갈비탕", "\uD83C\uDF72", Category.KOREAN, Color(0xFF6D4C41)),
        Menu("순두부찌개", "\uD83C\uDF72", Category.KOREAN, Color(0xFFFF7043)),
        Menu("냉면", "\uD83C\uDF5C", Category.KOREAN, Color(0xFF5C6BC0)),
        Menu("제육볶음", "\uD83C\uDF36\uFE0F", Category.KOREAN, Color(0xFFC62828)),
        Menu("설렁탕", "\uD83C\uDF72", Category.KOREAN, Color(0xFFEFEBE9)),

        // JAPANESE (일식) - 10 items
        Menu("돈까스", "\uD83E\uDD69", Category.JAPANESE, Color(0xFFF39C12)),
        Menu("초밥", "\uD83C\uDF63", Category.JAPANESE, Color(0xFFE91E63)),
        Menu("라멘", "\uD83C\uDF5C", Category.JAPANESE, Color(0xFFF1C40F)),
        Menu("우동", "\uD83C\uDF5C", Category.JAPANESE, Color(0xFFFF8F00)),
        Menu("오므라이스", "\uD83C\uDF73", Category.JAPANESE, Color(0xFFFFA726)),
        Menu("규동", "\uD83C\uDF5A", Category.JAPANESE, Color(0xFF8D6E63)),
        Menu("타코야키", "\uD83C\uDF61", Category.JAPANESE, Color(0xFFBF360C)),
        Menu("텐동", "\uD83C\uDF64", Category.JAPANESE, Color(0xFFF57F17)),
        Menu("소바", "\uD83C\uDF5C", Category.JAPANESE, Color(0xFF558B2F)),
        Menu("나베", "\uD83C\uDF72", Category.JAPANESE, Color(0xFF00838F)),

        // CHINESE (중식) - 8 items
        Menu("짜장면", "\uD83C\uDF5C", Category.CHINESE, Color(0xFF2C3E50)),
        Menu("짬뽕", "\uD83C\uDF5C", Category.CHINESE, Color(0xFFB71C1C)),
        Menu("탕수육", "\uD83C\uDF96\uFE0F", Category.CHINESE, Color(0xFFE65100)),
        Menu("마파두부", "\uD83C\uDF36\uFE0F", Category.CHINESE, Color(0xFFC62828)),
        Menu("양장피", "\uD83E\uDD57", Category.CHINESE, Color(0xFF33691E)),
        Menu("북경오리", "\uD83E\uDD86", Category.CHINESE, Color(0xFF4E342E)),
        Menu("딤섬", "\uD83E\uDD5F", Category.CHINESE, Color(0xFFF48FB1)),
        Menu("마라탕", "\uD83C\uDF36\uFE0F", Category.CHINESE, Color(0xFF880E4F)),

        // WESTERN (양식) - 9 items
        Menu("파스타", "\uD83C\uDF5D", Category.WESTERN, Color(0xFF27AE60)),
        Menu("햄버거", "\uD83C\uDF54", Category.WESTERN, Color(0xFF8E44AD)),
        Menu("피자", "\uD83C\uDF55", Category.WESTERN, Color(0xFFC0392B)),
        Menu("스테이크", "\uD83E\uDD69", Category.WESTERN, Color(0xFF4A148C)),
        Menu("리조또", "\uD83C\uDF5A", Category.WESTERN, Color(0xFF1565C0)),
        Menu("샐러드", "\uD83E\uDD57", Category.WESTERN, Color(0xFF2E7D32)),
        Menu("수프", "\uD83C\uDF72", Category.WESTERN, Color(0xFF0277BD)),
        Menu("뇨키", "\uD83C\uDF5D", Category.WESTERN, Color(0xFF6A1B9A)),
        Menu("그라탱", "\uD83E\uDDC0", Category.WESTERN, Color(0xFFF9A825)),

        // SNACK (분식) - 8 items
        Menu("떡볶이", "\uD83C\uDF36\uFE0F", Category.SNACK, Color(0xFFFF5722)),
        Menu("순대", "\uD83C\uDF2D", Category.SNACK, Color(0xFF6D4C41)),
        Menu("김밥", "\uD83C\uDF5B", Category.SNACK, Color(0xFF388E3C)),
        Menu("라면", "\uD83C\uDF5C", Category.SNACK, Color(0xFFD84315)),
        Menu("튀김", "\uD83C\uDF64", Category.SNACK, Color(0xFFF57C00)),
        Menu("핫도그", "\uD83C\uDF2D", Category.SNACK, Color(0xFFE53935)),
        Menu("붕어빵", "\uD83D\uDC1F", Category.SNACK, Color(0xFFFF8F00)),
        Menu("쌀떡꼬치", "\uD83C\uDF61", Category.SNACK, Color(0xFFAD1457)),

        // SOUTHEAST_ASIAN (동남아) - 8 items
        Menu("쌀국수", "\uD83C\uDF5C", Category.SOUTHEAST_ASIAN, Color(0xFF1ABC9C)),
        Menu("팟타이", "\uD83C\uDF5C", Category.SOUTHEAST_ASIAN, Color(0xFFFF6F00)),
        Menu("나시고렝", "\uD83C\uDF5A", Category.SOUTHEAST_ASIAN, Color(0xFF827717)),
        Menu("반미", "\uD83E\uDD56", Category.SOUTHEAST_ASIAN, Color(0xFF558B2F)),
        Menu("톰얌꿍", "\uD83C\uDF72", Category.SOUTHEAST_ASIAN, Color(0xFFE65100)),
        Menu("분짜", "\uD83C\uDF5C", Category.SOUTHEAST_ASIAN, Color(0xFF00695C)),
        Menu("에그타르트", "\uD83E\uDD5A", Category.SOUTHEAST_ASIAN, Color(0xFFF9A825)),
        Menu("사테", "\uD83C\uDF7A", Category.SOUTHEAST_ASIAN, Color(0xFF6D4C41)),
    )

    fun getFilteredMenus(selectedCategories: Set<Category>): List<Menu> {
        return if (selectedCategories.isEmpty()) allMenus
        else allMenus.filter { it.category in selectedCategories }
    }
}
