package com.example.mukmuk

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.components.BottomNavBar
import com.example.mukmuk.ui.screens.HistoryScreen
import com.example.mukmuk.ui.screens.RestaurantsScreen
import com.example.mukmuk.ui.screens.RouletteScreen
import com.example.mukmuk.ui.screens.SettingsScreen

@Composable
fun MukmukApp() {
    val viewModel: RouletteViewModel = viewModel()
    var currentRoute by rememberSaveable { mutableStateOf("roulette") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { currentRoute = it }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith
                        fadeOut(animationSpec = tween(250))
                },
                label = "screen_transition"
            ) { route ->
                when (route) {
                    "roulette" -> RouletteScreen(viewModel = viewModel)
                    "restaurants" -> RestaurantsScreen()
                    "history" -> HistoryScreen()
                    "settings" -> SettingsScreen()
                }
            }
        }
    }
}
