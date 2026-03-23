package com.gamehivecorp.taptita.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamehivecorp.taptita.R
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingScreen(onLoadingComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    BackHandler(enabled = true) {}
    LaunchedEffect(Unit) {
        startAnimation = true
        // Animate progress bar from 0 to 1 over 2 seconds
        val steps = 40
        for (i in 1..steps) {
            delay(2000L / steps)
            loadingProgress = i.toFloat() / steps
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val casinoFont = FontFamily(Font(R.font.font))

    // Floating particles angle
    val particleAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particles"
    )

    // Coin orbit rotation
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    // Coin self-spin (3D flip effect via scaleX)
    val coinFlip by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flip"
    )

    // Title glow pulse
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Title scale pulse
    val titlePulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titlePulse"
    )

    // Appear animations
    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(800),
        label = "titleAlpha"
    )
    val titleScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.2f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "titleScale"
    )
    val coinAppear by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(500, delayMillis = 200),
        label = "coinAppear"
    )
    val bottomAppear by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600, delayMillis = 500),
        label = "bottomAppear"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D001A),
                        Color(0xFF1A0033),
                        Color(0xFF2D0052),
                        Color(0xFF4A0E78),
                        Color(0xFF2D0052),
                        Color(0xFF0D001A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.royal_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Animated sparkle particles background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val particleCount = 30
            for (i in 0 until particleCount) {
                val seed = i * 137.5f // golden angle spread
                val baseX = (seed * 7.3f) % w
                val baseY = (seed * 13.7f) % h
                val angle = (particleAngle + seed) * PI.toFloat() / 180f
                val drift = sin(angle) * 20f
                val px = baseX + drift
                val py = (baseY + particleAngle * (0.5f + (i % 5) * 0.1f)) % h
                val alpha = (0.15f + 0.35f * sin(angle * 2f)).coerceIn(0f, 0.5f)
                val radius = 1.5f + (i % 4) * 0.8f
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = alpha),
                    radius = radius,
                    center = Offset(px, py)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Orbiting coins around center
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer { alpha = coinAppear }
            ) {
                // Glow behind coins
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .blur(40.dp)
                )

                // Main center coin
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .graphicsLayer {
                            scaleX = coinFlip
                            scaleY = titlePulse
                        }
                )

                // 3 orbiting smaller coins
                for (i in 0 until 3) {
                    val angleOffset = i * 120f
                    val rad = (orbitAngle + angleOffset) * PI.toFloat() / 180f
                    val orbitRadius = 70f
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .offset(
                                x = (cos(rad) * orbitRadius).dp,
                                y = (sin(rad) * orbitRadius * 0.5f).dp // elliptical orbit
                            )
                            .graphicsLayer {
                                // Depth effect: scale and alpha based on position
                                val depth = sin(rad)
                                scaleX = 0.7f + depth * 0.3f
                                scaleY = 0.7f + depth * 0.3f
                                alpha = 0.5f + depth * 0.5f
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title with glow shadow and shimmer
            Box(contentAlignment = Alignment.Center) {
                // Glow layer behind text
                Text(
                    text = "JACKPOTA\nPLAY",
                    color = Color(0xFFFFD700).copy(alpha = glowAlpha * 0.4f),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = casinoFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .blur(16.dp)
                        .graphicsLayer {
                            alpha = titleAlpha
                            scaleX = titleScale * titlePulse
                            scaleY = titleScale * titlePulse
                        }
                )
                // Main text
                Text(
                    text = "JACKPOTA\nPLAY",
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFFFD700).copy(alpha = 0.8f),
                            offset = Offset(0f, 0f),
                            blurRadius = 20f
                        )
                    ),
                    color = Color(0xFFFFD700),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = casinoFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        alpha = titleAlpha
                        scaleX = titleScale * titlePulse
                        scaleY = titleScale * titlePulse
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom: progress bar + loading text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp, vertical = 32.dp)
                    .graphicsLayer { alpha = bottomAppear }
            ) {
                // Custom progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(loadingProgress)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFB8860B),
                                        Color(0xFFFFD700),
                                        Color(0xFFFFF8DC)
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Animated dots
                    val dotCount = ((loadingProgress * 12).toInt() % 3) + 1
                    val dots = ".".repeat(dotCount)
                    Text(
                        text = "Loading$dots",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = casinoFont
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${(loadingProgress * 99).toInt()}%",
                        color = Color(0xFFFFD700).copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        fontFamily = casinoFont
                    )
                }
            }
        }
    }
}