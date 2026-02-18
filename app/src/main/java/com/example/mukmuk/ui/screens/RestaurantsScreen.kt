package com.example.mukmuk.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import com.example.mukmuk.R
import com.example.mukmuk.data.model.Category
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.ui.RestaurantUiState
import com.example.mukmuk.ui.RestaurantViewModel
import com.example.mukmuk.ui.components.LocationPermissionDialog
import com.example.mukmuk.ui.components.StarRating
import com.example.mukmuk.ui.theme.mukmukColors

@Composable
fun RestaurantsScreen(
    viewModel: RestaurantViewModel,
    onRestaurantClick: (String) -> Unit,
    onMapClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()
    val restaurants = viewModel.filteredRestaurants
    val apiState = viewModel.apiSearchState

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val favoriteAddedMsg = stringResource(R.string.restaurants_favorite_added)
    val favoriteRemovedMsg = stringResource(R.string.restaurants_favorite_removed)

    var showLocationDialog by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionGranted) {
            showLocationDialog = true
        }
    }

    if (showLocationDialog) {
        LocationPermissionDialog(
            onConfirm = {
                showLocationDialog = false
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDismiss = { showLocationDialog = false }
        )
    }

    val handleFavoriteToggle: (String) -> Unit = { name ->
        viewModel.toggleFavorite(name) { added ->
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    if (added) favoriteAddedMsg else favoriteRemovedMsg
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(colorScheme.background, colorScheme.surface, colorScheme.surfaceVariant)
                    )
                )
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "근처 맛집",
                color = colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = when (apiState) {
                    is RestaurantUiState.Success -> "현재 위치 기준"
                    is RestaurantUiState.Error -> "검색 실패 - 추천 맛집 표시 중"
                    else -> "추천 맛집"
                },
                color = when (apiState) {
                    is RestaurantUiState.Error -> extColors.error
                    else -> extColors.textTertiary
                },
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
                    .background(colorScheme.surface)
                    .border(1.dp, extColors.chipBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\uD83D\uDD0D", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (viewModel.searchQuery.isEmpty()) {
                            Text(text = "맛집 검색...", color = extColors.textHint, fontSize = 14.sp)
                        }
                        BasicTextField(
                            value = viewModel.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            textStyle = TextStyle(color = colorScheme.onSurface, fontSize = 14.sp),
                            cursorBrush = SolidColor(colorScheme.primary),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (viewModel.searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "\u2715",
                            color = extColors.textHint,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { viewModel.updateSearchQuery("") }
                        )
                    }
                }
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
                    emoji = "\uD83C\uDF7D\uFE0F",
                    selected = viewModel.selectedCategory == null && !viewModel.showFavoritesOnly,
                    onClick = {
                        viewModel.updateSelectedCategory(null)
                        if (viewModel.showFavoritesOnly) viewModel.toggleFavoritesOnly()
                    }
                )
                CategoryFilterChip(
                    label = "즐겨찾기",
                    emoji = "\u2665",
                    selected = viewModel.showFavoritesOnly,
                    onClick = { viewModel.toggleFavoritesOnly() }
                )
                Category.entries.forEach { category ->
                    val emoji = when (category) {
                        Category.KOREAN -> "\uD83C\uDDF0\uD83C\uDDF7"
                        Category.JAPANESE -> "\uD83C\uDDEF\uD83C\uDDF5"
                        Category.CHINESE -> "\uD83C\uDDE8\uD83C\uDDF3"
                        Category.WESTERN -> "\uD83C\uDF55"
                        Category.SNACK -> "\uD83C\uDF62"
                        Category.SOUTHEAST_ASIAN -> "\uD83C\uDF5C"
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

            when (apiState) {
                is RestaurantUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "맛집 검색 중...",
                                color = extColors.textTertiary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                is RestaurantUiState.Error -> {
                    Text(
                        text = apiState.message,
                        color = extColors.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${restaurants.size}개의 맛집",
                        color = extColors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RestaurantList(restaurants, favorites, handleFavoriteToggle, onRestaurantClick)
                }
                else -> {
                    if (restaurants.isEmpty()) {
                        EmptyRestaurantContent(
                            showFavoritesOnly = viewModel.showFavoritesOnly,
                            searchQuery = viewModel.searchQuery,
                            selectedCategory = viewModel.selectedCategory,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            text = "${restaurants.size}\uAC1C\uC758 \uB9DB\uC9D1",
                            color = extColors.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RestaurantList(restaurants, favorites, handleFavoriteToggle, onRestaurantClick)
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )

        // Floating map button
        if (restaurants.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 90.dp)
                    .clickable { onMapClick() },
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 6.dp
            ) {
                Text(
                    text = "\uD83D\uDDFA\uFE0F 지도로 보기",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RestaurantList(
    restaurants: List<Restaurant>,
    favorites: List<com.example.mukmuk.data.model.FavoriteRestaurant>,
    onFavoriteToggle: (String) -> Unit,
    onRestaurantClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(restaurants) { restaurant ->
            val isFavorite = favorites.any { it.restaurantName == restaurant.name }
            RestaurantDetailCard(
                restaurant = restaurant,
                isFavorite = isFavorite,
                onFavoriteClick = { onFavoriteToggle(restaurant.name) },
                onCardClick = { onRestaurantClick(restaurant.name) }
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun EmptyRestaurantContent(
    showFavoritesOnly: Boolean,
    searchQuery: String,
    selectedCategory: Category?,
    modifier: Modifier = Modifier
) {
    val extColors = MaterialTheme.mukmukColors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val (emoji, title, subtitle) = when {
                showFavoritesOnly -> Triple(
                    "\u2661",
                    stringResource(R.string.restaurants_empty_favorites),
                    stringResource(R.string.restaurants_empty_favorites_subtitle)
                )
                searchQuery.isNotBlank() -> Triple(
                    "\uD83D\uDD0D",
                    stringResource(R.string.restaurants_empty_search),
                    stringResource(R.string.restaurants_empty_search_subtitle)
                )
                selectedCategory != null -> Triple(
                    "\uD83C\uDF7D\uFE0F",
                    stringResource(R.string.restaurants_empty_category),
                    stringResource(R.string.restaurants_empty_category_subtitle)
                )
                else -> Triple(
                    "\uD83C\uDF7D\uFE0F",
                    stringResource(R.string.restaurants_empty_search),
                    stringResource(R.string.restaurants_empty_search_subtitle)
                )
            }
            Text(text = emoji, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = extColors.textTertiary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
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
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) colorScheme.primary.copy(alpha = 0.2f) else extColors.cardBackground,
        modifier = Modifier
            .border(
                1.dp,
                if (selected) colorScheme.primary else extColors.chipBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (selected) colorScheme.primary else extColors.textSecondary,
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
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = extColors.cardBackground,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
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
                        color = colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (restaurant.rating > 0f) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StarRating(rating = restaurant.rating)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "\uB9AC\uBDF0 ${restaurant.reviews}\uAC1C",
                                color = extColors.textHint,
                                fontSize = 11.sp
                            )
                        }
                    } else if (restaurant.placeUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\uD83D\uDCCD \uCE74\uCE74\uC624\uB9F5\uC5D0\uC11C \uBCF4\uAE30",
                            color = colorScheme.primary,
                            fontSize = 11.sp,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.placeUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                    if (restaurant.address.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\uD83D\uDCCD ${restaurant.address}",
                            color = extColors.textTertiary,
                            fontSize = 11.sp,
                            maxLines = 1
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
                            color = colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (restaurant.category != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = restaurant.category.displayName,
                                    color = colorScheme.primary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    // Favorite heart button
                    Surface(
                        shape = CircleShape,
                        color = if (isFavorite) colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = onFavoriteClick)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isFavorite) "\u2665" else "\u2661",
                                color = if (isFavorite) colorScheme.primary else extColors.textSecondary,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            if (restaurant.priceRange.isNotEmpty() || restaurant.hours.isNotEmpty() || restaurant.phone.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (restaurant.priceRange.isNotEmpty()) {
                        Text(
                            text = "\uD83D\uDCB0 ${restaurant.priceRange}",
                            color = extColors.textTertiary,
                            fontSize = 12.sp
                        )
                    }
                    if (restaurant.hours.isNotEmpty()) {
                        Text(
                            text = "\uD83D\uDD50 ${restaurant.hours}",
                            color = extColors.textTertiary,
                            fontSize = 12.sp
                        )
                    }
                    if (restaurant.phone.isNotEmpty()) {
                        Text(
                            text = "\uD83D\uDCDE ${restaurant.phone}",
                            color = extColors.textTertiary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
