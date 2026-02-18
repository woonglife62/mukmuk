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
import com.example.mukmuk.ui.RestaurantUiState
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

    // Try API results first, then fallback to local
    val apiResults = (viewModel.apiSearchState as? RestaurantUiState.Success)?.restaurants
    val restaurant = apiResults?.find { it.name == restaurantName }
        ?: RestaurantRepository.allRestaurants.find { it.name == restaurantName }

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
                    Text(text = "\u2190", color = TextPrimary, fontSize = 18.sp)
                }
            }

            Text(
                text = "\uB9DB\uC9D1 \uC0C1\uC138",
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
                        text = if (isFavorite) "\u2665" else "\u2661",
                        color = if (isFavorite) GoldAccent else TextSecondary,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (restaurant == null) {
            Text(
                text = "\uB9DB\uC9D1 \uC815\uBCF4\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4.",
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

                if (restaurant.rating > 0f) {
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
                            text = "\uB9AC\uBDF0 ${restaurant.reviews}\uAC1C",
                            color = TextHint,
                            fontSize = 12.sp
                        )
                    }
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
                    text = "\uC0C1\uC138 \uC815\uBCF4",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "\uD83D\uDCCD \uAC70\uB9AC", value = restaurant.distance)

                if (restaurant.address.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "\uD83C\uDFE0 \uC8FC\uC18C", value = restaurant.address)
                }

                if (restaurant.phone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "\uD83D\uDCDE \uC804\uD654", value = restaurant.phone)
                }

                if (restaurant.priceRange.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "\uD83D\uDCB0 \uAC00\uACA9\uB300", value = restaurant.priceRange)
                }

                if (restaurant.hours.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "\uD83D\uDD50 \uC601\uC5C5\uC2DC\uAC04", value = restaurant.hours)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Phone call button
        if (restaurant.phone.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GoldAccent.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${restaurant.phone}"))
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\uD83D\uDCDE", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\uC804\uD654\uAC78\uAE30",
                        color = GoldAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Kakao Map link button
        if (restaurant.placeUrl.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GoldAccent.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.placeUrl))
                        context.startActivity(intent)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\uD83D\uDDFA\uFE0F", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\uCE74\uCE74\uC624\uB9F5\uC5D0\uC11C \uBCF4\uAE30",
                        color = GoldAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Map button (geo intent fallback)
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
                Text(text = "\uD83D\uDDFA\uFE0F", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "\uC9C0\uB3C4\uC5D0\uC11C \uBCF4\uAE30",
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
