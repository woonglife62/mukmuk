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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.mukmuk.ui.theme.CardBackground
import com.example.mukmuk.ui.theme.CardBorder
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextHint

@Composable
fun ResultScreen(
    menu: Menu,
    restaurants: List<Restaurant>,
    onRetry: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\uD83D\uDCCD \uADFC\uCC98 \uB9DB\uC9D1",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "\uC11C\uC6B8 \uAC15\uB0A8\uAD6C \uAE30\uC900",
                    color = TextHint,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Restaurant list
            restaurants.forEach { restaurant ->
                RestaurantCard(restaurant = restaurant)
                Spacer(modifier = Modifier.height(10.dp))
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
                    border = BorderStroke(1.dp, CardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                        containerColor = CardBackground
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
                        containerColor = GoldAccent,
                        contentColor = DarkBackground
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
                    val shareText = "오늘의 메뉴는 ${menu.emoji} ${menu.name}! 🎯 #먹먹 #mukmuk"
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "공유하기"))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CardBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = CardBackground
                )
            ) {
                Text(
                    text = "📤 공유하기",
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
private fun RestaurantCard(restaurant: Restaurant) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = restaurant.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                StarRating(rating = restaurant.rating)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "\uB9AC\uBDF0 ${restaurant.reviews}\uAC1C",
                    color = TextHint,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = restaurant.distance,
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldAccent.copy(alpha = 0.15f),
                    modifier = Modifier.clickable {
                        val geoUri = Uri.parse(
                            "geo:${restaurant.latitude},${restaurant.longitude}?q=${Uri.encode(restaurant.name)}"
                        )
                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            // Fallback to web browser
                            val webUri = Uri.parse(
                                "https://maps.google.com/?q=${Uri.encode(restaurant.name)}&ll=${restaurant.latitude},${restaurant.longitude}"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    }
                ) {
                    Text(
                        text = "\uC9C0\uB3C4 \uBCF4\uAE30",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
