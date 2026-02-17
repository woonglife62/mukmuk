package com.example.mukmuk.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.data.repository.RestaurantRepository
import com.example.mukmuk.ui.RestaurantViewModel
import com.example.mukmuk.ui.components.StarRating
import com.example.mukmuk.ui.theme.CardBackground
import com.example.mukmuk.ui.theme.CardBorder
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.DarkSurface
import com.example.mukmuk.ui.theme.DarkSurfaceVariant
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextHint
import com.example.mukmuk.ui.theme.TextPrimary
import com.example.mukmuk.ui.theme.TextSecondary
import com.example.mukmuk.ui.theme.TextTertiary

@Composable
fun RestaurantDetailScreen(
    restaurantName: String,
    viewModel: RestaurantViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = favorites.any { it.restaurantName == restaurantName }

    val restaurant = RestaurantRepository.allRestaurants
        .find { it.name == restaurantName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkSurfaceVariant)
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top bar with back and favorite buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = CardBackground,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable { onBack() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "←", color = TextPrimary, fontSize = 18.sp)
                }
            }

            Text(
                text = "맛집 상세",
                color = GoldAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = CircleShape,
                color = if (isFavorite) GoldAccent.copy(alpha = 0.2f) else CardBackground,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, if (isFavorite) GoldAccent else CardBorder, CircleShape)
                    .clickable { viewModel.toggleFavorite(restaurantName) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFavorite) "♥" else "♡",
                        color = if (isFavorite) GoldAccent else TextSecondary,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (restaurant == null) {
            Text(
                text = "맛집 정보를 찾을 수 없습니다.",
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            return@Column
        }

        // Main info card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = restaurant.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarRating(rating = restaurant.rating)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${restaurant.rating}",
                        color = GoldAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "리뷰 ${restaurant.reviews}개",
                        color = TextHint,
                        fontSize = 12.sp
                    )
                }

                if (restaurant.category != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldAccent.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = restaurant.category.displayName,
                            color = GoldAccent,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Details card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "상세 정보",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "📍 거리", value = restaurant.distance)

                if (restaurant.priceRange.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "💰 가격대", value = restaurant.priceRange)
                }

                if (restaurant.hours.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "🕐 영업시간", value = restaurant.hours)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Map button
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GoldAccent.copy(alpha = 0.15f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable {
                    val uri = Uri.parse(
                        "geo:${restaurant.latitude},${restaurant.longitude}?q=${Uri.encode(restaurant.name)}"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🗺️", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "지도에서 보기",
                    color = GoldAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
        Text(text = value, color = TextTertiary, fontSize = 13.sp)
    }
}
