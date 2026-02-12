package com.example.mukmuk.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.mukmuk.data.model.Menu
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.GoldAccent

@Composable
fun RouletteWheel(
    menus: List<Menu>,
    rotation: Float,
    isSpinning: Boolean,
    onSpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Pointer triangle at top
        Canvas(modifier = Modifier.size(24.dp, 28.dp)) {
            val path = Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path, GoldAccent)
        }

        // Wheel
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .semantics { contentDescription = "\uC74C\uC2DD \uB8F0\uB81B \uD718. \uC911\uC559 GO \uBC84\uD2BC\uC744 \uD0ED\uD558\uC5EC \uD68C\uC804" }
                .pointerInput(isSpinning) {
                    if (!isSpinning) {
                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val distance = kotlin.math.sqrt(
                                ((offset.x - center.x) * (offset.x - center.x) +
                                 (offset.y - center.y) * (offset.y - center.y)).toDouble()
                            ).toFloat()
                            // 32.dp center circle - use approximate 40dp (generous touch target)
                            val centerRadius = 40.dp.toPx()
                            if (distance <= centerRadius) {
                                onSpin()
                            }
                        }
                    }
                }
        ) {
            if (menus.isEmpty()) return@Canvas
            val canvasSize = size.minDimension
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = canvasSize / 2f - 10.dp.toPx()
            val arcAngle = 360f / menus.size

            rotate(rotation, pivot = center) {
                menus.forEachIndexed { index, menu ->
                    val startAngle = index * arcAngle - 90f
                    // Draw slice
                    drawArc(
                        color = menu.color.copy(alpha = 0.87f),
                        startAngle = startAngle,
                        sweepAngle = arcAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                    // Draw slice border
                    drawArc(
                        color = Color.White.copy(alpha = 0.3f),
                        startAngle = startAngle,
                        sweepAngle = arcAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                    )
                    // Draw text
                    drawMenuText(
                        menu = menu,
                        center = center,
                        radius = radius,
                        startAngle = startAngle,
                        arcAngle = arcAngle
                    )
                }
            }

            // Center circle
            drawCircle(
                color = DarkBackground,
                radius = 32.dp.toPx(),
                center = center
            )
            drawCircle(
                color = GoldAccent,
                radius = 32.dp.toPx(),
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
            )
            // "GO" text
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#FFB800")
                    textSize = 14.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                drawText("GO", center.x, center.y + 5.dp.toPx(), paint)
            }
        }
    }
}

private fun DrawScope.drawMenuText(
    menu: Menu,
    center: Offset,
    radius: Float,
    startAngle: Float,
    arcAngle: Float
) {
    val midAngle = Math.toRadians((startAngle + arcAngle / 2f).toDouble())
    val textRadius = radius * 0.55f
    val textX = center.x + textRadius * kotlin.math.cos(midAngle).toFloat()
    val textY = center.y + textRadius * kotlin.math.sin(midAngle).toFloat()

    drawContext.canvas.nativeCanvas.apply {
        // Emoji
        val emojiPaint = android.graphics.Paint().apply {
            textSize = 16.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        drawText(menu.emoji, textX, textY - 2.dp.toPx(), emojiPaint)

        // Name
        val namePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 11.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
            setShadowLayer(4f, 0f, 2f, android.graphics.Color.argb(128, 0, 0, 0))
        }
        drawText(menu.name, textX, textY + 14.dp.toPx(), namePaint)
    }
}
