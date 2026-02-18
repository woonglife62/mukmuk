package com.example.mukmuk.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.data.model.Restaurant
import com.example.mukmuk.ui.theme.mukmukColors

@Composable
fun ResultScreen(
    menu: Menu,
    restaurants: List<Restaurant>,
    isLoading: Boolean = false,
    onRetry: () -> Unit,
    onConfirm: () -> Unit,
    onRestaurantClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    val context = LocalContext.current
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
    ) {
        Column(modifier = modifier.padding(horizontal = 20.dp)) {
            // Selected menu card
            Surface(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "\uC120\uD0DD\uB41C \uBA54\uB274: ${menu.name}, ${menu.category.displayName}"
                    }
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    menu.color.copy(alpha = 0.8f),
                                    menu.color.copy(alpha = 0.53f)
                                )
                            )
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = menu.emoji, fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = menu.name,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = menu.category.displayName,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nearby restaurants header
            val hasRealApiResults = restaurants.isNotEmpty() &&
                    restaurants.any { it.placeUrl.isNotEmpty() }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hasRealApiResults) "\uD83D\uDCCD \uADFC\uCC98 \uB9DB\uC9D1" else "\uD83C\uDF7D\uFE0F \uCD94\uCC9C \uB9DB\uC9D1",
                    color = colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (hasRealApiResults) "\uD604\uC7AC \uC704\uCE58 \uAE30\uC900" else "\uCD94\uCC9C \uBAA9\uB85D",
                    color = extColors.textHint,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\uB9DB\uC9D1 \uAC80\uC0C9 \uC911...",
                            color = extColors.textTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (restaurants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "\uD83C\uDF7D\uFE0F", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\uC8FC\uBCC0\uC5D0 \uAD00\uB828 \uB9DB\uC9D1\uC744 \uCC3E\uC9C0 \uBABB\uD588\uC5B4\uC694",
                            color = extColors.textTertiary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Restaurant list
                restaurants.forEach { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant.name) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, extColors.cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.onSurface,
                        containerColor = extColors.cardBackground
                    )
                ) {
                    Text(
                        text = "\uD83D\uDD04 \uB2E4\uC2DC \uB3CC\uB9AC\uAE30",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.background
                    )
                ) {
                    Text(
                        text = "\u2705 \uC774\uAC78\uB85C \uACB0\uC815!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }

            // Share button
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    val shareText = "\uC624\uB298\uC758 \uBA54\uB274\uB294 ${menu.emoji} ${menu.name}! \uD83C\uDFAF #\uBA39\uBA39 #mukmuk"
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "\uACF5\uC720\uD558\uAE30"))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, extColors.cardBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.onSurface,
                    containerColor = extColors.cardBackground
                )
            ) {
                Text(
                    text = "\uD83D\uDCE4 \uACF5\uC720\uD558\uAE30",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit = {}) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = extColors.cardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, extColors.cardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = restaurant.name,
                    color = colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (restaurant.rating > 0f) {
                    StarRating(rating = restaurant.rating)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\uB9AC\uBDF0 ${restaurant.reviews}\uAC1C",
                        color = extColors.textHint,
                        fontSize = 11.sp
                    )
                }
                if (restaurant.address.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\uD83D\uDCCD ${restaurant.address}",
                        color = extColors.textTertiary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
                if (restaurant.phone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "\uD83D\uDCDE ${restaurant.phone}",
                        color = extColors.textTertiary,
                        fontSize = 11.sp
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (restaurant.distance != "0m" && restaurant.distance.isNotEmpty()) {
                    Text(
                        text = restaurant.distance,
                        color = colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable {
                        if (restaurant.placeUrl.isNotEmpty()) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.placeUrl)))
                        } else {
                            val geoUri = Uri.parse(
                                "geo:${restaurant.latitude},${restaurant.longitude}?q=${Uri.encode(restaurant.name)}"
                            )
                            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                val webUri = Uri.parse(
                                    "https://maps.google.com/?q=${Uri.encode(restaurant.name)}&ll=${restaurant.latitude},${restaurant.longitude}"
                                )
                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        }
                    }
                ) {
                    Text(
                        text = "\uC9C0\uB3C4 \uBCF4\uAE30",
                        color = colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
