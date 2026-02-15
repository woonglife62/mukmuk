package com.example.mukmuk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.DarkSurface
import com.example.mukmuk.ui.theme.DarkSurfaceVariant
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextTertiary

@Composable
fun RestaurantsScreen() {
    PlaceholderContent(icon = "\uD83D\uDCCD", title = "\uB9DB\uC9D1", subtitle = "\uACE7 \uB9CC\uB098\uC694!")
}

@Composable
private fun PlaceholderContent(icon: String, title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkSurfaceVariant)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 48.sp)
            Text(text = title, color = GoldAccent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextTertiary, fontSize = 14.sp)
        }
    }
}
