package com.example.mukmuk.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.ui.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    restaurantViewModel: RestaurantViewModel,
    onBack: () -> Unit,
    onAddToRecord: (Restaurant) -> Unit
) {
    val context = LocalContext.current
    val restaurants = restaurantViewModel.filteredRestaurants
    val centerLat = restaurantViewModel.currentLatitude
    val centerLng = restaurantViewModel.currentLongitude

    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Build restaurant JSON array for JS
    val restaurantJson = restaurants.mapIndexed { index, r ->
        """{"name":"${r.name.replace("\"", "\\\"")}","lat":${r.latitude},"lng":${r.longitude},"distance":"${r.distance}","category":"${r.category?.name ?: ""}","address":"${r.address.replace("\"", "\\\"")}","phone":"${r.phone}","placeUrl":"${r.placeUrl.replace("\"", "\\\"")}","index":$index}"""
    }.joinToString(",")

    val htmlContent = """
    <!DOCTYPE html>
    <html>
    <head>
      <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
      <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
      <style>
        * { margin: 0; padding: 0; }
        #map { width: 100vw; height: 100vh; }
        .custom-marker {
          background: #FF6B35;
          border: 2px solid #fff;
          border-radius: 50% 50% 50% 0;
          width: 30px;
          height: 30px;
          transform: rotate(-45deg);
          box-shadow: 0 2px 5px rgba(0,0,0,0.3);
        }
        .my-location {
          background: #4285F4;
          border: 3px solid #fff;
          border-radius: 50%;
          width: 16px;
          height: 16px;
          box-shadow: 0 0 0 4px rgba(66,133,244,0.3);
        }
      </style>
    </head>
    <body>
      <div id="map"></div>
      <script>
        var map = L.map('map', {zoomControl: false}).setView([$centerLat, $centerLng], 15);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '&copy; OpenStreetMap'
        }).addTo(map);

        // My location marker
        var myIcon = L.divIcon({className: 'my-location', iconSize: [16, 16], iconAnchor: [8, 8]});
        L.marker([$centerLat, $centerLng], {icon: myIcon}).addTo(map).bindTooltip('내 위치');

        // Restaurant markers
        var restaurants = [$restaurantJson];
        var markerIcon = L.divIcon({className: 'custom-marker', iconSize: [30, 30], iconAnchor: [15, 30]});

        restaurants.forEach(function(r) {
          if (r.lat !== 0 && r.lng !== 0) {
            var marker = L.marker([r.lat, r.lng], {icon: markerIcon}).addTo(map);
            marker.bindTooltip(r.name, {direction: 'top', offset: [0, -30]});
            marker.on('click', function() {
              Android.onMarkerClick(r.index);
            });
          }
        });

        // Fit bounds if there are restaurants
        if (restaurants.length > 0) {
          var bounds = L.latLngBounds(restaurants.filter(function(r) { return r.lat !== 0; }).map(function(r) { return [r.lat, r.lng]; }));
          bounds.extend([$centerLat, $centerLng]);
          map.fitBounds(bounds, {padding: [50, 50]});
        }
      </script>
    </body>
    </html>
    """.trimIndent()

    Box(modifier = Modifier.fillMaxSize()) {
        // WebView
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    addJavascriptInterface(
                        object {
                            @JavascriptInterface
                            fun onMarkerClick(index: Int) {
                                Handler(Looper.getMainLooper()).post {
                                    if (index in restaurants.indices) {
                                        selectedRestaurant = restaurants[index]
                                        showBottomSheet = true
                                    }
                                }
                            }
                        },
                        "Android"
                    )
                    loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                }
            }
        )

        // Back button overlay
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shadowElevation = 4.dp
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
        }

        // Restaurant count badge
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "${restaurants.size}개 맛집",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Bottom Sheet
    if (showBottomSheet && selectedRestaurant != null) {
        val restaurant = selectedRestaurant!!
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedRestaurant = null
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Restaurant name + distance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = restaurant.distance,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category badge
                if (restaurant.category != null) {
                    val categoryLabel = when (restaurant.category.name) {
                        "KOREAN" -> "🇰🇷 한식"
                        "JAPANESE" -> "🇯🇵 일식"
                        "CHINESE" -> "🇨🇳 중식"
                        "WESTERN" -> "🍕 양식"
                        "SNACK" -> "🍢 분식"
                        "SOUTHEAST_ASIAN" -> "🍜 동남아"
                        else -> restaurant.category.name
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = categoryLabel,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Address
                if (restaurant.address.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍 ", fontSize = 16.sp)
                        Text(
                            text = restaurant.address,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Phone
                if (restaurant.phone.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📞 ", fontSize = 16.sp)
                        Text(
                            text = restaurant.phone,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Kakao Map button
                    if (restaurant.placeUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.placeUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📍 카카오맵에서 보기")
                        }
                    }

                    // Add to record button
                    Button(
                        onClick = {
                            onAddToRecord(restaurant)
                            showBottomSheet = false
                            selectedRestaurant = null
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📋 기록에 추가")
                    }
                }
            }
        }
    }
}
