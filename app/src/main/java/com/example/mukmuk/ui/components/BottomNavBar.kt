package com.example.mukmuk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.GoldAccent

data class NavItem(
    val icon: String,
    val label: String,
    val route: String
)

val navItems = listOf(
    NavItem("\uD83C\uDFAF", "\uB8F0\uB81B", "roulette"),
    NavItem("\uD83D\uDCCD", "\uB9DB\uC9D1", "restaurants"),
    NavItem("\uD83D\uDCCB", "\uAE30\uB85D", "history"),
    NavItem("\u2699\uFE0F", "\uC124\uC815", "settings"),
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, DarkBackground),
                    startY = 0f,
                    endY = 40f
                )
            )
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        navItems.forEach { item ->
            val isActive = item.route == currentRoute
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .defaultMinSize(minWidth = 56.dp, minHeight = 48.dp)
                    .semantics {
                        contentDescription = "${item.label} \uD0ED"
                        role = Role.Tab
                    }
                    .clickable { onNavigate(item.route) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.icon,
                    fontSize = 22.sp,
                    modifier = Modifier.alpha(if (isActive) 1f else 0.4f)
                )
                Text(
                    text = item.label,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) GoldAccent else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.padding(top = 2.dp)
                )
                // Active indicator
                if (isActive) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(GoldAccent, CircleShape)
                    )
                }
            }
        }
    }
}
