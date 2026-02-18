package com.example.mukmuk

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mukmuk.di.MukmukViewModelFactory
import com.example.mukmuk.navigation.Screen
import com.example.mukmuk.ui.HistoryViewModel
import com.example.mukmuk.ui.RestaurantViewModel
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.components.BottomNavBar
import com.example.mukmuk.ui.screens.HistoryScreen
import com.example.mukmuk.ui.screens.RestaurantDetailScreen
import com.example.mukmuk.ui.screens.RestaurantsScreen
import com.example.mukmuk.ui.screens.RouletteScreen
import com.example.mukmuk.ui.screens.SettingsScreen

@Composable
fun MukmukApp() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MukmukApplication
    val factory = MukmukViewModelFactory(app.container)

    val viewModel: RouletteViewModel = viewModel(factory = factory)
    val historyViewModel: HistoryViewModel = viewModel(factory = factory)
    val restaurantViewModel: RestaurantViewModel = viewModel(factory = factory)

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Roulette.route
            ) {
                composable(Screen.Roulette.route) {
                    RouletteScreen(viewModel = viewModel)
                }
                composable(Screen.Restaurants.route) {
                    RestaurantsScreen(
                        viewModel = restaurantViewModel,
                        onRestaurantClick = { name ->
                            navController.navigate(Screen.RestaurantDetail.createRoute(name))
                        }
                    )
                }
                composable(Screen.History.route) {
                    HistoryScreen(viewModel = historyViewModel)
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(viewModel = viewModel)
                }
                composable(Screen.RestaurantDetail.route) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    RestaurantDetailScreen(
                        restaurantName = name,
                        viewModel = restaurantViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
