package com.example.mukmuk.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mukmuk.navigation.Screen

@Composable
fun RouletteScreen() {
    PlaceholderContent(screen = Screen.Roulette, description = "음식 룰렛을 돌려보세요!")
}

@Composable
fun HistoryScreen() {
    PlaceholderContent(screen = Screen.History, description = "추천 히스토리가 여기에 표시됩니다.")
}

@Composable
fun RestaurantScreen() {
    PlaceholderContent(screen = Screen.Restaurant, description = "맛집 정보가 여기에 표시됩니다.")
}

@Composable
fun SettingsScreen() {
    PlaceholderContent(screen = Screen.Settings, description = "앱 설정을 관리합니다.")
}

@Composable
private fun PlaceholderContent(screen: Screen, description: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = screen.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
