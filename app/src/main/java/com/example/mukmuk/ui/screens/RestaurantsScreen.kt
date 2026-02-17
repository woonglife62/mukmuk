package com.example.mukmuk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.ui.RestaurantViewModel
import com.example.mukmuk.ui.components.StarRating
import com.example.mukmuk.ui.theme.CardBackground
import com.example.mukmuk.ui.theme.CardBorder
import com.example.mukmuk.ui.theme.ChipBorder
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.DarkSurface
import com.example.mukmuk.ui.theme.DarkSurfaceVariant
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextHint
import com.example.mukmuk.ui.theme.TextPrimary
import com.example.mukmuk.ui.theme.TextSecondary
import com.example.mukmuk.ui.theme.TextTertiary

@Composable
fun RestaurantsScreen(
    viewModel: RestaurantViewModel,
    onRestaurantClick: (String) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val restaurants = viewModel.filteredRestaurants

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkSurfaceVariant)
                )
            )
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "근처 맛집",
            color = GoldAccent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Text(
            text = "서울 강남구 기준",
            color = TextTertiary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, ChipBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (viewModel.searchQuery.isEmpty()) {
                Text(text = "맛집 검색...", color = TextHint, fontSize = 14.sp)
            }
            BasicTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                cursorBrush = SolidColor(GoldAccent),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category chips + favorites filter
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryFilterChip(
                label = "전체",
                emoji = "🍽️",
                selected = viewModel.selectedCategory == null && !viewModel.showFavoritesOnly,
                onClick = {
                    viewModel.updateSelectedCategory(null)
                    if (viewModel.showFavoritesOnly) viewModel.toggleFavoritesOnly()
                }
            )
            CategoryFilterChip(
                label = "즐겨찾기",
                emoji = "♥",
                selected = viewModel.showFavoritesOnly,
                onClick = { viewModel.toggleFavoritesOnly() }
            )
            Category.entries.forEach { category ->
                val emoji = when (category) {
                    Category.KOREAN -> "🇰🇷"
                    Category.JAPANESE -> "🇯🇵"
                    Category.CHINESE -> "🇨🇳"
                    Category.WESTERN -> "🍕"
                    Category.SNACK -> "🍢"
                    Category.SOUTHEAST_ASIAN -> "🍜"
                }
                CategoryFilterChip(
                    label = category.displayName,
                    emoji = emoji,
                    selected = viewModel.selectedCategory == category,
                    onClick = {
                        viewModel.updateSelectedCategory(
                            if (viewModel.selectedCategory == category) null else category
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${restaurants.size}개의 맛집",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(restaurants) { restaurant ->
                val isFavorite = favorites.any { it.restaurantName == restaurant.name }
                RestaurantDetailCard(
                    restaurant = restaurant,
                    isFavorite = isFavorite,
                    onFavoriteClick = { viewModel.toggleFavorite(restaurant.name) },
                    onCardClick = { onRestaurantClick(restaurant.name) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) GoldAccent.copy(alpha = 0.2f) else CardBackground,
        modifier = Modifier
            .border(
                1.dp,
                if (selected) GoldAccent else ChipBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (selected) GoldAccent else TextSecondary,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun RestaurantDetailCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = restaurant.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StarRating(rating = restaurant.rating)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "리뷰 ${restaurant.reviews}개",
                            color = TextHint,
                            fontSize = 11.sp
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = restaurant.distance,
                            color = GoldAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (restaurant.category != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldAccent.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = restaurant.category.displayName,
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    // Favorite heart button
                    Surface(
                        shape = CircleShape,
                        color = if (isFavorite) GoldAccent.copy(alpha = 0.15f) else Color.Transparent,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = onFavoriteClick)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isFavorite) "♥" else "♡",
                                color = if (isFavorite) GoldAccent else TextSecondary,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            if (restaurant.priceRange.isNotEmpty() || restaurant.hours.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (restaurant.priceRange.isNotEmpty()) {
                        Text(
                            text = "💰 ${restaurant.priceRange}",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                    if (restaurant.hours.isNotEmpty()) {
                        Text(
                            text = "🕐 ${restaurant.hours}",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
