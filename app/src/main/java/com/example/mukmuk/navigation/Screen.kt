package com.example.mukmuk.navigation

sealed class Screen(
    val route: String,
    val icon: String,
    val title: String
) {
    data object Roulette : Screen("roulette", "\uD83C\uDFAF", "\uB8F0\uB81B")
    data object Restaurants : Screen("restaurants", "\uD83D\uDCCD", "\uB9DB\uC9D1")
    data object History : Screen("history", "\uD83D\uDCCB", "\uAE30\uB85D")
    data object Settings : Screen("settings", "\u2699\uFE0F", "\uC124\uC815")

    companion object {
        val bottomNavItems = listOf(Roulette, Restaurants, History, Settings)
    }
}
