package com.example.mukmuk.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.components.CategoryChips
import com.example.mukmuk.ui.components.MenuGrid
import com.example.mukmuk.ui.components.ResultScreen
import com.example.mukmuk.ui.components.RouletteWheel
import com.example.mukmuk.ui.theme.DarkBackground
import com.example.mukmuk.ui.theme.DarkSurface
import com.example.mukmuk.ui.theme.DarkSurfaceVariant
import com.example.mukmuk.ui.theme.GoldAccent
import com.example.mukmuk.ui.theme.TextTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RouletteScreen(
    viewModel: RouletteViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val animatable = remember { Animatable(viewModel.rotation) }
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(viewModel.showConfirmSnackbar) {
        if (viewModel.showConfirmSnackbar) {
            snackbarHostState.showSnackbar("\uB9DB\uC788\uAC8C \uB4DC\uC138\uC694! \uD83C\uDF7D\uFE0F")
            viewModel.dismissSnackbar()
        }
    }

    val spinWheel = {
        if (!viewModel.isSpinning && viewModel.filteredMenus.isNotEmpty()) {
            viewModel.updateSpinning(true)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            scope.launch {
                val totalRotation = 1440f + (Math.random() * 1440f).toFloat()
                val target = viewModel.rotation + totalRotation
                animatable.snapTo(viewModel.rotation)
                animatable.animateTo(
                    targetValue = target,
                    animationSpec = tween(
                        durationMillis = 4000,
                        easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
                    )
                ) {
                    viewModel.updateRotation(value)
                }
                viewModel.onSpinComplete(target)
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(300)
                viewModel.showResultScreen()
            }
        }
        Unit
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(DarkBackground, DarkSurface, DarkSurfaceVariant)
                    )
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        // Header
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "\uC624\uB298 \uBFD0 \uBA39\uC9C0? \uD83E\uDD14",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent,
            textAlign = TextAlign.Center
        )
        Text(
            text = "\uACE0\uBBFC\uC740 \uADF8\uB9CC, \uB8F0\uB81B\uC5D0 \uB9E1\uACA8!",
            color = TextTertiary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category chips
        CategoryChips(
            categories = viewModel.categories,
            selectedCategories = viewModel.selectedCategories,
            onToggle = { viewModel.toggleCategory(it) },
            onClearAll = { viewModel.clearAllCategories() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!viewModel.showResult) {
            if (viewModel.filteredMenus.isEmpty()) {
                // Empty state
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "\uD83D\uDE45",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\uC120\uD0DD\uD55C \uCE74\uD14C\uACE0\uB9AC\uC5D0 \uBA54\uB274\uAC00 \uC5C6\uC5B4\uC694",
                    color = TextTertiary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.clearAllCategories() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = DarkBackground
                    )
                ) {
                    Text(
                        text = "\uC804\uCCB4 \uBCF4\uAE30",
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(80.dp))
            } else {
                // Roulette wheel
                RouletteWheel(
                    menus = viewModel.filteredMenus,
                    rotation = viewModel.rotation,
                    isSpinning = viewModel.isSpinning,
                    onSpin = { spinWheel() }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Spin button
                Button(
                    onClick = { spinWheel() },
                    enabled = !viewModel.isSpinning && viewModel.filteredMenus.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        contentColor = DarkBackground,
                        disabledContainerColor = GoldAccent.copy(alpha = 0.3f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(horizontal = 48.dp)
                ) {
                    Text(
                        text = if (viewModel.isSpinning) "\uD83C\uDFB0 \uB3CC\uB9AC\uB294 \uC911..." else "\uD83C\uDFAF \uB3CC\uB824!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Menu grid
                MenuGrid(menus = viewModel.filteredMenus)

                Spacer(modifier = Modifier.height(80.dp)) // space for bottom nav
            }
        } else {
            // Result screen
            viewModel.selectedMenu?.let { menu ->
                ResultScreen(
                    menu = menu,
                    restaurants = viewModel.restaurants,
                    onRetry = { viewModel.resetToWheel() },
                    onConfirm = { viewModel.confirmSelection() }
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
