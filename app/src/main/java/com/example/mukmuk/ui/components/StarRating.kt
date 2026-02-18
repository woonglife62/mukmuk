package com.example.mukmuk.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextTertiary
import kotlin.math.cos
import kotlin.math.sin

private fun DrawScope.drawStar(
    centerX: Float,
    centerY: Float,
    outerRadius: Float,
    color: Color,
    filled: Boolean
) {
    val innerRadius = outerRadius * 0.4f
    val path = Path()
    for (i in 0 until 10) {
        val angle = Math.PI / 5 * i - Math.PI / 2
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = centerX + (radius * cos(angle)).toFloat()
        val y = centerY + (radius * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    if (filled) {
        drawPath(path, color)
    } else {
        drawPath(path, color.copy(alpha = 0.3f))
    }
}

@Composable
fun StarRating(rating: Float, modifier: Modifier = Modifier) {
    val full = rating.toInt()
    val hasHalf = (rating % 1f) >= 0.5f
    val empty = 5 - full - if (hasHalf) 1 else 0
    val starSize = 14.dp
    val starSpacing = 2.dp
    val totalWidth = starSize * 5 + starSpacing * 4

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics {
            contentDescription = "\uBCC4\uC810 ${rating}\uC810, 5\uC810 \uB9CC\uC810"
        }
    ) {
        Canvas(
            modifier = Modifier
                .width(totalWidth)
                .size(starSize)
        ) {
            val starPx = starSize.toPx()
            val spacingPx = starSpacing.toPx()
            val radius = starPx * 0.45f

            for (i in 0 until 5) {
                val cx = i * (starPx + spacingPx) + starPx / 2
                val cy = starPx / 2

                when {
                    i < full -> {
                        // Full star
                        drawStar(cx, cy, radius, GoldAccent, filled = true)
                    }
                    i == full && hasHalf -> {
                        // Half star: draw empty outline first, then clip left half and fill
                        drawStar(cx, cy, radius, GoldAccent, filled = false)
                        clipRect(
                            left = cx - radius,
                            top = cy - radius,
                            right = cx,
                            bottom = cy + radius
                        ) {
                            drawStar(cx, cy, radius, GoldAccent, filled = true)
                        }
                    }
                    else -> {
                        // Empty star
                        drawStar(cx, cy, radius, GoldAccent, filled = false)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format(java.util.Locale.US, "%.1f", rating),
            color = TextTertiary,
            fontSize = 12.sp
        )
    }
}
