package com.example.mukmuk.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextTertiary

@Composable
fun StarRating(rating: Float, modifier: Modifier = Modifier) {
    val full = rating.toInt()
    val hasHalf = (rating % 1f) >= 0.5f
    val empty = 5 - full - if (hasHalf) 1 else 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics {
            contentDescription = "\uBCC4\uC810 ${rating}\uC810, 5\uC810 \uB9CC\uC810"
        }
    ) {
        Text(
            text = "\u2605".repeat(full) + (if (hasHalf) "\u00BD" else "") + "\u2606".repeat(empty),
            color = GoldAccent,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = rating.toString(),
            color = TextTertiary,
            fontSize = 12.sp
        )
    }
}
