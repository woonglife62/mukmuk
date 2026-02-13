package com.example.mukmuk

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mukmuk.navigation.Screen
import com.example.mukmuk.ui.components.BottomNavBar
import com.example.mukmuk.ui.screens.HistoryScreen
import com.example.mukmuk.ui.screens.RestaurantScreen
import com.example.mukmuk.ui.screens.RouletteScreen
import com.example.mukmuk.ui.screens.SettingsScreen

@Composable
fun MukmukApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Roulette.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Roulette.route) {
                RouletteScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Restaurant.route) {
                RestaurantScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
