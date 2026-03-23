package com.gamehivecorp.taptita.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.ui.components.CasinoFont
import com.gamehivecorp.taptita.ui.theme.GoldYellow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class FireworkParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float
)

private data class Firework(
    val originX: Float,
    val originY: Float,
    val particles: List<FireworkParticle>,
    val startTime: Float
)

@Composable
fun LevelUpDialog(
    newLevel: Int,
    totalCoins: Long,
    onDismiss: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
    }

    val fireworks = remember {
        val colors = listOf(
            Color(0xFFFFD700), Color(0xFFFF6B6B), Color(0xFF69B4FF),
            Color(0xFF50C878), Color(0xFFFF69B4), Color(0xFFFFFFFF),
            Color(0xFFFFA500), Color(0xFFBA55D3)
        )
        (0 until 8).map { i ->
            val cx = 0.15f + Random.nextFloat() * 0.7f
            val cy = 0.1f + Random.nextFloat() * 0.5f
            val color = colors[i % colors.size]
            val particleCount = 25 + Random.nextInt(15)
            val particles = (0 until particleCount).map {
                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                val speed = 1.5f + Random.nextFloat() * 3f
                FireworkParticle(
                    x = 0f, y = 0f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    color = color.copy(alpha = 0.7f + Random.nextFloat() * 0.3f),
                    size = 2f + Random.nextFloat() * 3f
                )
            }
            Firework(cx, cy, particles, startTime = i * 0.1f + Random.nextFloat() * 0.15f)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            // Firework canvas behind everything
            Canvas(modifier = Modifier.fillMaxSize()) {
                val t = progress.value
                val w = size.width
                val h = size.height

                for (fw in fireworks) {
                    val localT = ((t - fw.startTime) / 0.7f).coerceIn(0f, 1f)
                    if (localT <= 0f) continue

                    val originX = fw.originX * w
                    val originY = fw.originY * h

                    // Rising phase (0..0.3) then explosion (0.3..1)
                    if (localT < 0.3f) {
                        // Rising trail
                        val riseT = localT / 0.3f
                        val trailY = originY + (1f - riseT) * h * 0.3f
                        val trailAlpha = (1f - riseT) * 0.8f
                        drawCircle(
                            color = Color.White.copy(alpha = trailAlpha),
                            radius = 3f,
                            center = Offset(originX, trailY)
                        )
                    } else {
                        // Explosion
                        val explodeT = (localT - 0.3f) / 0.7f
                        val gravity = 2f
                        for (p in fw.particles) {
                            val px = originX + p.vx * explodeT * 60f
                            val py = originY + p.vy * explodeT * 60f + gravity * explodeT * explodeT * 40f
                            val alpha = ((1f - explodeT) * 0.9f).coerceIn(0f, 1f)
                            val sparkleSize = p.size * (1f - explodeT * 0.5f)
                            if (alpha > 0.01f) {
                                drawCircle(
                                    color = p.color.copy(alpha = alpha),
                                    radius = sparkleSize,
                                    center = Offset(px, py)
                                )
                            }
                        }
                    }
                }
            }

            // Level up content
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.levelup_bg),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-8).dp)
                ) {
                    Text(
                        text = "Coins: ${formatCoins(totalCoins)}",
                        color = GoldYellow,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CasinoFont
                    )
                }
            }
        }
    }
}

fun formatCoins(coins: Long): String {
    return when {
        coins >= 1_000_000 -> String.format("%.1fM", coins / 1_000_000.0)
        coins >= 1_000 -> String.format("%.1fK", coins / 1_000.0)
        else -> coins.toString()
    }
}
