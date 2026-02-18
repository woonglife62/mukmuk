package com.example.mukmuk.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.mukmuk.data.repository.RestaurantRepository
import com.example.mukmuk.ui.RestaurantUiState
import com.example.mukmuk.ui.RestaurantViewModel
import com.example.mukmuk.ui.components.StarRating
import com.example.mukmuk.ui.theme.mukmukColors

@Composable
fun RestaurantDetailScreen(
    restaurantName: String,
    viewModel: RestaurantViewModel,
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()
    // Decode name in case it came URL-encoded from navigation
    val decodedName = try { Uri.decode(restaurantName) } catch (_: Exception) { restaurantName }
    val isFavorite = favorites.any { it.restaurantName == decodedName }

    // Try API results first, then temporary (roulette) restaurants, then local fallback
    val apiResults = (viewModel.apiSearchState as? RestaurantUiState.Success)?.restaurants
    val restaurant = apiResults?.find { it.name == decodedName }
        ?: viewModel.temporaryRestaurants.find { it.name == decodedName }
        ?: RestaurantRepository.allRestaurants.find { it.name == decodedName }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(colorScheme.background, colorScheme.surface, colorScheme.surfaceVariant)
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
                color = extColors.cardBackground,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, extColors.cardBorder, CircleShape)
                    .clickable { onBack() }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\u2190", color = colorScheme.onSurface, fontSize = 18.sp)
                }
            }

            Text(
                text = "\uB9DB\uC9D1 \uC0C1\uC138",
                color = colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = CircleShape,
                color = if (isFavorite) colorScheme.primary.copy(alpha = 0.2f) else extColors.cardBackground,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, if (isFavorite) colorScheme.primary else extColors.cardBorder, CircleShape)
                    .clickable { viewModel.toggleFavorite(decodedName) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFavorite) "\u2665" else "\u2661",
                        color = if (isFavorite) colorScheme.primary else extColors.textSecondary,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (restaurant == null) {
            Text(
                text = "\uB9DB\uC9D1 \uC815\uBCF4\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4.",
                color = extColors.textSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            return@Column
        }

        // Main info card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = extColors.cardBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = restaurant.name,
                    color = colorScheme.onSurface,
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
                            color = colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "\uB9AC\uBDF0 ${restaurant.reviews}\uAC1C",
                            color = extColors.textHint,
                            fontSize = 12.sp
                        )
                    }
                }

                if (restaurant.category != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = restaurant.category.displayName,
                            color = colorScheme.primary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Embedded map - show for all restaurants with non-default coordinates
        val isHardcodedLocation = kotlin.math.abs(restaurant.latitude - 37.4979) < 0.0001 &&
                                  kotlin.math.abs(restaurant.longitude - 127.0276) < 0.0001
        if (!isHardcodedLocation) {
            val lifecycleOwner = LocalLifecycleOwner.current
            val webViewState = remember { mutableStateOf<WebView?>(null) }
            var isMapLoading by remember { mutableStateOf(true) }

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> webViewState.value?.onPause()
                        Lifecycle.Event.ON_RESUME -> webViewState.value?.onResume()
                        Lifecycle.Event.ON_DESTROY -> {
                            webViewState.value?.stopLoading()
                            webViewState.value?.destroy()
                        }
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    webViewState.value?.stopLoading()
                    webViewState.value?.destroy()
                }
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = extColors.cardBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "\uD83D\uDDFA\uFE0F \uC704\uCE58",
                        color = colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val lat = restaurant.latitude
                    val lng = restaurant.longitude
                    val mapHtml = buildString {
                        append("<!DOCTYPE html><html><head>")
                        append("<meta name='viewport' content='width=device-width,initial-scale=1.0'>")
                        append("<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>")
                        append("<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>")
                        append("<style>html,body,#map{height:100%;margin:0;padding:0;}</style>")
                        append("</head><body><div id='map'></div><script>")
                        append("var map=L.map('map').setView([${lat},${lng}],16);")
                        append("L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',")
                        append("{attribution:'&copy; OSM'}).addTo(map);")
                        append("L.marker([${lat},${lng}]).addTo(map);")
                        append("</script></body></html>")
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                            val url = request?.url?.toString() ?: return true
                                            val allowedHosts = listOf("openstreetmap.org", "www.openstreetmap.org", "tile.openstreetmap.org", "unpkg.com")
                                            return !allowedHosts.any { url.contains(it) }
                                        }
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            isMapLoading = false
                                        }
                                    }
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    loadDataWithBaseURL(null, mapHtml, "text/html", "UTF-8", null)
                                    webViewState.value = this
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        if (isMapLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorScheme.surface.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        } else if (restaurant.address.isNotEmpty()) {
            // For hardcoded/fallback locations, show address text section
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = extColors.cardBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "\uD83D\uDDFA\uFE0F \uC704\uCE58",
                        color = colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = restaurant.address,
                        color = extColors.textSecondary,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Details card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = extColors.cardBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "\uC0C1\uC138 \uC815\uBCF4",
                    color = colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (restaurant.distance.isNotEmpty() && restaurant.distance != "0m") {
                    DetailRow(label = "\uD83D\uDCCD \uAC70\uB9AC", value = restaurant.distance)
                    Spacer(modifier = Modifier.height(8.dp))
                }

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
                color = colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                        color = colorScheme.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Map button: prefer Kakao Map if placeUrl exists, fallback to geo intent for real coords
        if (restaurant.placeUrl.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                        color = colorScheme.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        } else if (!isHardcodedLocation) {
            // Geo intent fallback only when no placeUrl and coordinates are real
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                        color = colorScheme.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = extColors.textSecondary, fontSize = 13.sp)
        Text(text = value, color = extColors.textTertiary, fontSize = 13.sp)
    }
}
