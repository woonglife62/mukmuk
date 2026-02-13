package com.example.mukmuk.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Roulette : Screen("roulette", "룰렛", Icons.Filled.Casino)
    data object History : Screen("history", "히스토리", Icons.Filled.History)
    data object Restaurant : Screen("restaurant", "맛집", Icons.Filled.Restaurant)
    data object Settings : Screen("settings", "설정", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Roulette, History, Restaurant, Settings)
    }
}
