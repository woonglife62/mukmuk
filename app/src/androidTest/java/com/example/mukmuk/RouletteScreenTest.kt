package com.example.mukmuk

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.ui.components.CategoryChips
import com.example.mukmuk.ui.components.MenuGrid
import com.example.mukmuk.data.repository.MenuRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouletteScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun categoryChips_displayAllCategories() {
        composeTestRule.setContent {
            CategoryChips(
                categories = Category.entries.toList(),
                selectedCategories = emptySet(),
                onToggle = {},
                onClearAll = {}
            )
        }

        composeTestRule.onNodeWithText("한식").assertIsDisplayed()
        composeTestRule.onNodeWithText("일식").assertIsDisplayed()
        composeTestRule.onNodeWithText("중식").assertIsDisplayed()
        composeTestRule.onNodeWithText("양식").assertIsDisplayed()
        composeTestRule.onNodeWithText("분식").assertIsDisplayed()
        composeTestRule.onNodeWithText("동남아").assertIsDisplayed()
    }

    @Test
    fun menuGrid_displaysMenuItems() {
        val menus = MenuRepository.allMenus.take(4)
        composeTestRule.setContent {
            MenuGrid(menus = menus)
        }

        menus.forEach { menu ->
            composeTestRule.onNodeWithText(menu.name).assertIsDisplayed()
        }
    }

    @Test
    fun resultScreen_displaysSelectedMenu() {
        val menu = MenuRepository.allMenus.first()
        val restaurants = com.example.mukmuk.data.repository.RestaurantRepository.getRestaurants(menu.name)

        composeTestRule.setContent {
            com.example.mukmuk.ui.components.ResultScreen(
                menu = menu,
                restaurants = restaurants,
                onRetry = {},
                onConfirm = {}
            )
        }

        composeTestRule.onNodeWithText(menu.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(menu.emoji).assertIsDisplayed()
        composeTestRule.onNodeWithText("🔄 다시 돌리기").assertIsDisplayed()
        composeTestRule.onNodeWithText("✅ 이걸로 결정!").assertIsDisplayed()
        composeTestRule.onNodeWithText("📤 공유하기").assertIsDisplayed()
    }

    @Test
    fun resultScreen_displaysRestaurants() {
        val menu = MenuRepository.allMenus.first()
        val restaurants = com.example.mukmuk.data.repository.RestaurantRepository.getRestaurants(menu.name)

        composeTestRule.setContent {
            com.example.mukmuk.ui.components.ResultScreen(
                menu = menu,
                restaurants = restaurants,
                onRetry = {},
                onConfirm = {}
            )
        }

        restaurants.forEach { restaurant ->
            composeTestRule.onNodeWithText(restaurant.name).assertIsDisplayed()
        }
    }

    @Test
    fun rouletteWheel_displaysAccessibilityDescription() {
        composeTestRule.setContent {
            com.example.mukmuk.ui.components.RouletteWheel(
                menus = MenuRepository.allMenus,
                rotation = 0f,
                isSpinning = false,
                onSpin = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("음식 룰렛 휠. 중앙 GO 버튼을 탭하여 회전")
            .assertIsDisplayed()
    }
}
