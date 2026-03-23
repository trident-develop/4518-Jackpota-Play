package com.gamehivecorp.taptita.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.data.GameTheme
import com.gamehivecorp.taptita.ui.components.CasinoFont
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.components.SquareButton
import com.gamehivecorp.taptita.ui.components.pressableWithCooldown
import com.gamehivecorp.taptita.ui.theme.BarPurple
import com.gamehivecorp.taptita.ui.theme.BarPurpleDark
import com.gamehivecorp.taptita.ui.theme.DarkBackground
import com.gamehivecorp.taptita.ui.theme.GoldYellow
import com.gamehivecorp.taptita.viewmodel.MenuUiState
import com.gamehivecorp.taptita.viewmodel.MenuViewModel
import kotlinx.coroutines.delay
import java.util.Locale

private data class FlyingCoin(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val delayMs: Int = 0
)

@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onPlayGame: (GameTheme) -> Unit,
    onLeaderboard: () -> Unit,
    onPrivacy: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    MenuScreenContent(
        state = state,
        onPlayGame = onPlayGame,
        onLeaderboard = onLeaderboard,
        onPrivacy = onPrivacy,
        onClaimGiftBox = { viewModel.claimGiftBox() },
        onClaimDailyBonus = { viewModel.claimDailyBonus() },
        getDailyBonusAmount = { viewModel.getDailyBonusAmount(it) }
    )
}

@Composable
fun MenuScreenContent(
    state: MenuUiState,
    onPlayGame: (GameTheme) -> Unit,
    onLeaderboard: () -> Unit,
    onPrivacy: () -> Unit,
    onClaimGiftBox: () -> Unit,
    onClaimDailyBonus: () -> Unit,
    getDailyBonusAmount: (Int) -> Long
) {
    var giftBoxVisible by remember(state.giftBoxVisible) { mutableStateOf(state.giftBoxVisible) }
    var cooldownRemaining by remember(state.giftBoxCooldownEnd) { mutableLongStateOf(0L) }

    var flyingCoins by remember { mutableStateOf(listOf<FlyingCoin>()) }
    var coinIdCounter by remember { mutableStateOf(0) }

    fun spawnCoinFly(sourceX: Float, sourceY: Float) {
        val count = 15
        val newCoins = (0 until count).map { i ->
            FlyingCoin(
                id = coinIdCounter + i,
                startX = sourceX + (-40..40).random(),
                startY = sourceY + (-40..40).random(),
                delayMs = i * 40
            )
        }
        coinIdCounter += count
        flyingCoins = flyingCoins + newCoins
    }

    var giftBoxX by remember { mutableStateOf(0f) }
    var giftBoxY by remember { mutableStateOf(0f) }

    var dailyBonusX by remember { mutableStateOf(0f) }
    var dailyBonusY by remember { mutableStateOf(0f) }

    LaunchedEffect(state.giftBoxCooldownEnd, state.giftBoxVisible) {
        if (!state.giftBoxVisible && state.giftBoxCooldownEnd > 0) {
            while (true) {
                val remaining = state.giftBoxCooldownEnd - System.currentTimeMillis()
                if (remaining <= 0) {
                    giftBoxVisible = true
                    cooldownRemaining = 0L
                    break
                }
                cooldownRemaining = remaining
                delay(1000)
            }
        } else {
            cooldownRemaining = 0L
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground, Color(0xFF2D0052), DarkBackground)
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.seven_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
        ) {
            TopBar(
                coins = state.coins,
                level = state.level,
                levelProgress = state.levelProgress,
                onLeaderboard = onLeaderboard
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(GameTheme.entries.toList()) { theme ->
                        GameCard(
                            theme = theme,
                            playerLevel = state.level,
                            onPlay = { onPlayGame(theme) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(BarPurpleDark, BarPurple, BarPurpleDark))
                    )
                    .padding(horizontal = 40.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuButton(
                    text = "PRIVACY",
                    fontSize = 16.sp,
                    onClick = onPrivacy,
                    modifier = Modifier
                        .width(100.dp).height(46.dp)
                )

                Box(
                    modifier = Modifier
                        .onGloballyPositioned {
                            val pos = it.localToRoot(Offset.Zero)
                            giftBoxX = pos.x + it.size.width / 2f
                            giftBoxY = pos.y + it.size.height / 2f
                        }
                ) {
                    if (giftBoxVisible) {
                        val infiniteTransition = rememberInfiniteTransition(label = "gift")
                        val bounce by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "bounce"
                        )

                        Image(
                            painter = painterResource(id = R.drawable.box),
                            contentDescription = "Gift Box",
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer {
                                    scaleX = bounce
                                    scaleY = bounce
                                }
                                .pressableWithCooldown(cooldownMillis = 500L) {
                                    spawnCoinFly(giftBoxX, giftBoxY)
                                    onClaimGiftBox()
                                    giftBoxVisible = false
                                }
                        )
                    } else if (cooldownRemaining > 0) {
                        val minutes = (cooldownRemaining / 60000).toInt()
                        val seconds = ((cooldownRemaining % 60000) / 1000).toInt()

                        Text(
                            text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                            color = GoldYellow,
                            fontSize = 14.sp,
                            fontFamily = CasinoFont,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(8.dp)
                        )
                    }
                }

                DailyBonusPanel(
                    currentDay = state.dailyBonusDay,
                    canClaim = state.canClaimDailyBonus,
                    getDailyBonusAmount = getDailyBonusAmount,
                    onClaim = {
                        spawnCoinFly(dailyBonusX, dailyBonusY)
                        onClaimDailyBonus()
                    },
                    modifier = Modifier
                        .onGloballyPositioned {
                            val pos = it.localToRoot(Offset.Zero)
                            dailyBonusX = pos.x + it.size.width / 2f
                            dailyBonusY = pos.y + it.size.height / 2f
                        }
                )
            }
        }

        val density = LocalDensity.current
        for (coin in flyingCoins) {
            key(coin.id) {
                FlyingCoinAnimation(
                    startXPx = coin.startX,
                    startYPx = coin.startY,
                    delayMs = coin.delayMs,
                    density = density,
                    onFinished = {
                        flyingCoins = flyingCoins.filter { it.id != coin.id }
                    }
                )
            }
        }
    }
}

@Composable
private fun FlyingCoinAnimation(
    startXPx: Float,
    startYPx: Float,
    delayMs: Int = 0,
    density: androidx.compose.ui.unit.Density,
    onFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
        onFinished()
    }

    val t = progress.value
    // Target: top-left coin area (roughly 50dp, 25dp from top)
    val targetXPx: Float
    val targetYPx: Float
    with(density) {
        targetXPx = 50.dp.toPx()
        targetYPx = 25.dp.toPx()
    }

    val currentX = startXPx + (targetXPx - startXPx) * t
    val currentY = startYPx + (targetYPx - startYPx) * t - 80f * kotlin.math.sin(t * Math.PI.toFloat())

    val sizeDp = with(density) { 20.dp }
    val offsetX = with(density) { currentX.toInt().toDp() }
    val offsetY = with(density) { currentY.toInt().toDp() }

    Image(
        painter = painterResource(id = R.drawable.coin),
        contentDescription = null,
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(sizeDp)
            .graphicsLayer {
                alpha = 1f - t * 0.3f
                scaleX = 1f - t * 0.4f
                scaleY = 1f - t * 0.4f
            }
    )
}

@Composable
private fun DailyBonusPanel(
    currentDay: Int,
    canClaim: Boolean,
    getDailyBonusAmount: (Int) -> Long,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, GoldYellow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "DAILY BONUS",
                color = GoldYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (day in 1..5) {
                    val isCollected = day <= currentDay
                    val isCurrent = day == currentDay + 1
                    val amount = getDailyBonusAmount(day)

                    Row (
                       verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .width(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    isCollected -> BarPurple.copy(alpha = 0.7f)
                                    isCurrent -> GoldYellow.copy(alpha = 0.2f)
                                    else -> Color.White.copy(alpha = 0.08f)
                                }
                            )
                            .then(
                                if (isCurrent) Modifier.border(1.dp, GoldYellow, RoundedCornerShape(6.dp))
                                else Modifier
                            )
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = "D$day",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 9.sp,
                            fontFamily = CasinoFont
                        )
                        Text(
                            text = "${amount / 1000}K",
                            color = GoldYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CasinoFont
                        )
                        if (isCollected) {
                            Text(
                                text = "✓",
                                color = Color.Green,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        if (canClaim) {
            MenuButton(
                text = "COLLECT",
                fontSize = 14.sp,
                modifier = Modifier.width(80.dp).height(36.dp),
                onClick = onClaim,
            )
        } else {
            Text(
                text = "COLLECTED ✓",
                color = Color.Green.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = CasinoFont
            )
        }
    }

}

@Composable
private fun TopBar(
    coins: Long,
    level: Int,
    levelProgress: Float,
    onLeaderboard: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(listOf(BarPurpleDark, BarPurple, BarPurpleDark))
            )
            .padding(horizontal = 40.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Coins
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.coin),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatCoins(coins),
                color = GoldYellow,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )
        }

        SquareButton(
            btnRes = R.drawable.leaders_button,
            btnMaxWidth = 0.07f,
            btnClickable = onLeaderboard,
            modifier = Modifier.padding(start = 80.dp)
        )

        // Level progress
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.star),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Lv.$level",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                LinearProgressIndicator(
                    progress = { (levelProgress / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = GoldYellow,
                    trackColor = Color.Transparent
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${levelProgress.toInt()}%",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontFamily = CasinoFont
            )
        }
    }
}

@Composable
private fun GameCard(
    theme: GameTheme,
    playerLevel: Int,
    onPlay: () -> Unit
) {
    val isUnlocked = playerLevel >= theme.unlockLevel

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(200.dp)
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(
                2.dp,
                if (isUnlocked) GoldYellow.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box {
            Image(
                painter = painterResource(id = theme.previewRes),
                contentDescription = theme.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (!isUnlocked) alpha = 0.4f
                    }
            )
            // Theme name overlay

            Image(
                painter = painterResource(id = theme.cardLogo),
                contentDescription = theme.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.9f)
                    .graphicsLayer {
                        if (!isUnlocked) alpha = 0.4f
                    }
            )

            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔒",
                        fontSize = 50.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (isUnlocked) {
                MenuButton(
                    text = "PLAY",
                    fontSize = 20.sp,
                    buttonRes = theme.buttonRes,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(160.dp).height(60.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = onPlay
                )
            } else {
                Text(
                    text = "UNLOCK AT LEVEL ${theme.unlockLevel}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    fontFamily = CasinoFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                )
            }
        }
    }
}