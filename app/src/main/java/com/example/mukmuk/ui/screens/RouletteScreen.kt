package com.example.mukmuk.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.mukmuk.ui.RouletteViewModel
import com.example.mukmuk.ui.components.CategoryChips
import com.example.mukmuk.ui.components.LocationPermissionDialog
import com.example.mukmuk.ui.components.MenuGrid
import com.example.mukmuk.ui.components.ResultScreen
import com.example.mukmuk.ui.components.RouletteWheel
import com.example.mukmuk.ui.theme.mukmukColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RouletteScreen(
    viewModel: RouletteViewModel,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val extColors = MaterialTheme.mukmukColors
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val animatable = remember { Animatable(viewModel.rotation) }
    val snackbarHostState = remember { SnackbarHostState() }
    val hapticFeedback = LocalHapticFeedback.current
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    var showConfetti by remember { mutableStateOf(false) }

    // Location permission
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

    // ToneGenerator for tick/completion sounds
    val toneGenerator = remember {
        try {
            ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (_: Exception) {
            null
        }
    }
    DisposableEffect(Unit) {
        onDispose { toneGenerator?.release() }
    }

    // Confetti animation
    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            delay(2000)
            showConfetti = false
        }
    }

    LaunchedEffect(viewModel.showConfirmSnackbar) {
        if (viewModel.showConfirmSnackbar) {
            snackbarHostState.showSnackbar("\uB9DB\uC788\uAC8C \uB4DC\uC138\uC694! \uD83C\uDF7D\uFE0F")
            viewModel.dismissSnackbar()
        }
    }

    val spinWheel = {
        if (!locationPermissionGranted) {
            showLocationDialog = true
        }
        if (!viewModel.isSpinning && viewModel.filteredMenus.isNotEmpty()) {
            viewModel.updateSpinning(true)
            if (hapticEnabled) hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
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
                if (hapticEnabled) hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                if (soundEnabled) toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 150)
                showConfetti = true
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
                        colors = listOf(colorScheme.background, colorScheme.surface, colorScheme.surfaceVariant)
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
            color = colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "\uACE0\uBBFC\uC740 \uADF8\uB9CC, \uB8F0\uB81B\uC5D0 \uB9E1\uACA8!",
            color = extColors.textTertiary,
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
                    color = extColors.textTertiary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.clearAllCategories() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.background
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
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.background,
                        disabledContainerColor = colorScheme.primary.copy(alpha = 0.3f),
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
                    isLoading = viewModel.isLoadingRestaurants,
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

        // Confetti overlay
        if (showConfetti) {
            ConfettiOverlay()
        }
    }
}

@Composable
private fun ConfettiOverlay() {
    val confettiColors = listOf(
        Color(0xFFFFB800), Color(0xFFFF5722), Color(0xFF4CAF50),
        Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF9C27B0)
    )
    val particles = remember {
        List(40) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                size = Random.nextFloat() * 8f + 4f,
                color = confettiColors[Random.nextInt(confettiColors.size)],
                speedY = Random.nextFloat() * 0.003f + 0.001f,
                speedX = (Random.nextFloat() - 0.5f) * 0.002f
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            1f,
            animationSpec = tween(durationMillis = 2000)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val t = progress.value
        particles.forEach { p ->
            val x = (p.x + p.speedX * t * 1000f) * size.width
            val y = (p.y + p.speedY * t * 1000f) * size.height
            if (y in 0f..size.height) {
                drawCircle(
                    color = p.color.copy(alpha = (1f - t).coerceIn(0f, 1f)),
                    radius = p.size,
                    center = Offset(x % size.width, y)
                )
            }
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val speedY: Float,
    val speedX: Float
)
