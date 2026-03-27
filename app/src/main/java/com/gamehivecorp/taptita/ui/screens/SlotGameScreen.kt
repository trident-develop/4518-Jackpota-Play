package com.gamehivecorp.taptita.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.data.GameTheme
import com.gamehivecorp.taptita.data.WinLine
import com.gamehivecorp.taptita.sound.SoundManager
import com.gamehivecorp.taptita.ui.components.CasinoFont
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.components.SquareButton
import com.gamehivecorp.taptita.ui.theme.GoldYellow
import com.gamehivecorp.taptita.ui.theme.WinHighlight
import com.gamehivecorp.taptita.viewmodel.SlotUiState
import com.gamehivecorp.taptita.viewmodel.SlotViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin

private data class SlotFlyingCoin(
    val id: Int,
    val startX: Float,
    val startY: Float,
    val delayMs: Int = 0
)

@Composable
fun SlotGameScreen(
    theme: GameTheme,
    soundManager: SoundManager,
    onBack: () -> Unit,
    slotViewModel: SlotViewModel = viewModel()
) {
    val state by slotViewModel.uiState.collectAsState()
    var showInfo by remember { mutableStateOf(false) }
    var musicEnabled by remember { mutableStateOf(soundManager.isMusicPlaying) }

    LaunchedEffect(theme) {
        slotViewModel.initialize(theme)
    }

    LaunchedEffect(state.spinTrigger) {
        if (state.spinTrigger > 0) {
            soundManager.playSpinSound()
        }
    }

    LaunchedEffect(state.totalWin) {
        if (state.totalWin > 0) {
            soundManager.playWinSound()
        }
    }

    LaunchedEffect(state.showLevelUp) {
        if (state.showLevelUp) {
            soundManager.playLevelUpSound()
        }
    }

    SlotGameScreenContent(
        theme = theme,
        state = state,
        showInfo = showInfo,
        musicEnabled = musicEnabled,
        onBack = onBack,
        onMusic = {
            soundManager.toggleMusic()
            musicEnabled = soundManager.isMusicPlaying
        },
        onInfo = { showInfo = true },
        onDismissInfo = { showInfo = false },
        onSpin = { slotViewModel.spin() },
        onIncreaseBet = { slotViewModel.increaseBet() },
        onDecreaseBet = { slotViewModel.decreaseBet() },
        onAutoSpin = { slotViewModel.toggleAutoSpin() },
        onSpeed = { slotViewModel.cycleSpeed() },
        onDismissWin = { slotViewModel.dismissWinScreen() },
        onDismissLevelUp = { slotViewModel.dismissLevelUp() }
    )
}

@Composable
fun SlotGameScreenContent(
    theme: GameTheme,
    state: SlotUiState,
    showInfo: Boolean,
    musicEnabled: Boolean = true,
    onBack: () -> Unit,
    onMusic: () -> Unit,
    onInfo: () -> Unit,
    onDismissInfo: () -> Unit,
    onSpin: () -> Unit,
    onIncreaseBet: () -> Unit,
    onDecreaseBet: () -> Unit,
    onAutoSpin: () -> Unit,
    onSpeed: () -> Unit,
    onDismissWin: () -> Unit,
    onDismissLevelUp: () -> Unit
) {
    var flyingCoins by remember { mutableStateOf(listOf<SlotFlyingCoin>()) }
    var coinIdCounter by remember { mutableStateOf(0) }
    var betAreaX by remember { mutableStateOf(0f) }
    var betAreaY by remember { mutableStateOf(0f) }
    var coinsTargetX by remember { mutableStateOf(0f) }
    var coinsTargetY by remember { mutableStateOf(0f) }

    var flyingStars by remember { mutableStateOf(listOf<SlotFlyingCoin>()) }
    var starIdCounter by remember { mutableStateOf(0) }
    var spinBtnX by remember { mutableStateOf(0f) }
    var spinBtnY by remember { mutableStateOf(0f) }
    var starTargetX by remember { mutableStateOf(0f) }
    var starTargetY by remember { mutableStateOf(0f) }

    fun spawnWinCoins() {
        if (betAreaX <= 0f) return
        val count = 15
        val newCoins = (0 until count).map { i ->
            SlotFlyingCoin(
                id = coinIdCounter + i,
                startX = betAreaX + (-40..40).random(),
                startY = betAreaY + (-20..20).random(),
                delayMs = i * 40
            )
        }
        coinIdCounter += count
        flyingCoins = flyingCoins + newCoins
    }

    fun spawnSpinStars() {
        if (spinBtnX <= 0f) return
        val count = 10
        val newStars = (0 until count).map { i ->
            SlotFlyingCoin(
                id = starIdCounter + i,
                startX = spinBtnX + (-25..25).random(),
                startY = spinBtnY + (-15..15).random(),
                delayMs = i * 30
            )
        }
        starIdCounter += count
        flyingStars = flyingStars + newStars
    }

    LaunchedEffect(state.spinTrigger) {
        if (state.spinTrigger > 0) {
            spawnSpinStars()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = theme.bgRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            GameTopBar(
                theme = theme,
                coins = state.coins,
                level = state.level,
                levelProgress = state.levelProgress,
                musicEnabled = musicEnabled,
                onBack = onBack,
                onMusic = onMusic,
                onInfo = onInfo,
                onCoinsPositioned = { x, y ->
                    coinsTargetX = x
                    coinsTargetY = y
                },
                onStarPositioned = { x, y ->
                    starTargetX = x
                    starTargetY = y
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SlotMachine(
                    theme = theme,
                    reelStrips = state.reelStrips,
                    finalGrid = state.grid,
                    displayGrid = state.displayGrid,
                    spinTrigger = state.spinTrigger,
                    spinning = state.spinning,
                    speedLevel = state.speedLevel,
                    winLines = state.winLines
                )
            }

            GameBottomBar(
                bet = state.bet,
                maxBet = state.maxBet,
                spinning = state.spinning,
                lastWin = state.lastWin,
                speedLevel = state.speedLevel,
                theme = theme,
                onSpin = onSpin,
                onIncreaseBet = onIncreaseBet,
                onDecreaseBet = onDecreaseBet,
                onSpeed = onSpeed,
                onBetPositioned = { x, y ->
                    betAreaX = x
                    betAreaY = y
                },
                onSpinPositioned = { x, y ->
                    spinBtnX = x
                    spinBtnY = y
                }
            )
        }

//        Image(
//            painter = painterResource(id = theme.slotLogo),
//            contentDescription = null,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .fillMaxSize(0.4f)
//                .scale(1f)
//        )

        val density = LocalDensity.current

        for (coin in flyingCoins) {
            key(coin.id) {
                SlotParticleFly(
                    drawableRes = R.drawable.coin,
                    startXPx = coin.startX,
                    startYPx = coin.startY,
                    targetXPx = coinsTargetX,
                    targetYPx = coinsTargetY,
                    delayMs = coin.delayMs,
                    density = density,
                    onFinished = {
                        flyingCoins = flyingCoins.filter { it.id != coin.id }
                    }
                )
            }
        }

        for (star in flyingStars) {
            key(star.id) {
                SlotParticleFly(
                    drawableRes = R.drawable.star,
                    startXPx = star.startX,
                    startYPx = star.startY,
                    targetXPx = starTargetX,
                    targetYPx = starTargetY,
                    delayMs = star.delayMs,
                    density = density,
                    onFinished = {
                        flyingStars = flyingStars.filter { it.id != star.id }
                    }
                )
            }
        }
    }

    if (state.showWinScreen && state.totalWin > 0 && !state.spinning) {
        WinDialog(
            theme = theme,
            totalWin = state.totalWin,
            bet = state.bet,
            onDismiss = {
                spawnWinCoins()
                onDismissWin()
            }
        )
    }

    if (state.showLevelUp) {
        LevelUpDialog(
            newLevel = state.level,
            totalCoins = state.coins,
            onDismiss = onDismissLevelUp
        )
    }

    if (showInfo) {
        InfoDialog(
            theme = theme,
            onDismiss = onDismissInfo
        )
    }
}

// ─── Slot Machine with real vertical reel scrolling ─────────────────────────

@Composable
private fun SlotMachine(
    theme: GameTheme,
    reelStrips: List<List<Int>>,
    finalGrid: List<List<Int>>,
    displayGrid: List<List<Int>>,
    spinTrigger: Int,
    spinning: Boolean,
    speedLevel: Int,
    winLines: List<WinLine>
) {
    val winPositions = remember(winLines) {
        winLines.flatMap { it.positions }.toSet()
    }
    val cellPositions = remember { mutableMapOf<Pair<Int, Int>, Pair<Float, Float>>() }
    val cellSizes = remember { mutableMapOf<Pair<Int, Int>, IntSize>() }

    val infiniteTransition = rememberInfiniteTransition(label = "winGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight(0.95f)
            .fillMaxWidth(0.75f)
    ) {
        if (theme.displayName == "Treasure"){
            Image(
                painter = painterResource(id = theme.slotLogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.5f)
                    .offset(y = (-50).dp)
                    .scale(1f)
            )
        }

        Image(
            painter = painterResource(id = theme.slotRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
                .scale(1f)
        )

        if (theme.displayName == "Pirate"){
            Image(
                painter = painterResource(id = theme.slotLogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.35f)
                    .offset(y = (-30).dp)
                    .scale(1f)
            )
        }

        if (theme.displayName == "Aztec"){
            Image(
                painter = painterResource(id = theme.slotLogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.35f)
                    .offset(y = (-30).dp)
                    .scale(1f)
            )
        }

        if (theme.displayName == "Lion"){
            Image(
                painter = painterResource(id = theme.slotLogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.38f)
                    .offset(y = (-30).dp)
                    .scale(1f)
            )
        }

        if (theme.displayName == "Royal"){
            Image(
                painter = painterResource(id = theme.slotLogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.38f)
                    .offset(y = (-40).dp)
                    .scale(1f)
            )
        }

        if (theme.displayName == "Seven"){
            Image(
                painter = painterResource(id = theme.slotLogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize(0.38f)
                    .offset(y = (-24).dp)
                    .scale(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (theme.columns == 5) 24.dp else 36.dp)
                .padding(bottom = 16.dp, top = 60.dp)
                .drawBehind {
                    if (winLines.isNotEmpty() && !spinning) {
                        for (winLine in winLines) {
                            val positions = winLine.positions
                            for (i in 0 until positions.size - 1) {
                                val start = cellPositions[positions[i]]
                                val end = cellPositions[positions[i + 1]]
                                val startSize = cellSizes[positions[i]]
                                val endSize = cellSizes[positions[i + 1]]
                                if (start != null && end != null && startSize != null && endSize != null) {
                                    drawLine(
                                        color = WinHighlight.copy(alpha = glowAlpha),
                                        start = Offset(
                                            start.first + startSize.width / 2f,
                                            start.second + startSize.height / 2f
                                        ),
                                        end = Offset(
                                            end.first + endSize.width / 2f,
                                            end.second + endSize.height / 2f
                                        ),
                                        strokeWidth = 6f,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until theme.columns) {
                    ReelColumn(
                        col = col,
                        theme = theme,
                        reelStrip = reelStrips.getOrNull(col) ?: emptyList(),
                        finalSymbols = finalGrid.getOrNull(col) ?: emptyList(),
                        displaySymbols = displayGrid.getOrNull(col) ?: emptyList(),
                        spinTrigger = spinTrigger,
                        spinning = spinning,
                        speedLevel = speedLevel,
                        winPositions = winPositions,
                        glowAlpha = glowAlpha,
                        cellPositions = cellPositions,
                        cellSizes = cellSizes,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun ReelColumn(
    col: Int,
    theme: GameTheme,
    reelStrip: List<Int>,
    finalSymbols: List<Int>,
    displaySymbols: List<Int>,
    spinTrigger: Int,
    spinning: Boolean,
    speedLevel: Int,
    winPositions: Set<Pair<Int, Int>>,
    glowAlpha: Float,
    cellPositions: MutableMap<Pair<Int, Int>, Pair<Float, Float>>,
    cellSizes: MutableMap<Pair<Int, Int>, IntSize>,
    modifier: Modifier = Modifier
) {
    val rows = theme.rows
    val scrollAnim = remember { Animatable(0f) }

    LaunchedEffect(spinTrigger) {
        if (spinTrigger == 0 || reelStrip.isEmpty()) return@LaunchedEffect

        scrollAnim.snapTo(1f)

        val baseDuration = when (speedLevel) {
            1 -> 2000
            2 -> 1200
            3 -> 700
            else -> 2000
        }
        delay(col * 300L)

        scrollAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = baseDuration + col * 200,
                easing = LinearEasing
            )
        )
        // Bounce at landing
        scrollAnim.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
        )
    }

    val isAnimating = spinTrigger > 0 && reelStrip.size > rows
    val stripSize = if (isAnimating) reelStrip.size else rows
    val scrollableCount = (stripSize - rows).coerceAtLeast(0)

    // Continuous float position in the strip
    val floatPos = scrollAnim.value * scrollableCount
    val startIndex = floatPos.toInt().coerceIn(0, scrollableCount)
    val fraction = floatPos - startIndex // 0..1 sub-cell fractional offset

    val isStopped = !spinning && scrollAnim.value <= 0.01f

    // Outer box clips to exactly the visible area — only `rows` cells visible
    var columnHeightPx by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned { columnHeightPx = it.size.height.toFloat() }
    ) {
        if (columnHeightPx <= 0f) return@Box
        val cellHeightPx = columnHeightPx / rows

        // Inner column: rows+1 items, shifted up by fraction * cellHeight
        // This gives pixel-smooth scrolling; the extra item scrolls in from bottom
        val offsetPx = (fraction * cellHeightPx).toInt()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(androidx.compose.ui.platform.LocalDensity.current) {
                    (cellHeightPx * (rows + 1)).toInt().toDp()
                })
                .offset { IntOffset(0, -offsetPx) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val itemCount = (rows + 1).coerceAtMost(stripSize - startIndex)
            for (i in 0 until itemCount) {
                val stripIndex = startIndex + i
                val symbolIndex = if (isAnimating && stripIndex < reelStrip.size) {
                    reelStrip[stripIndex]
                } else {
                    displaySymbols.getOrElse(i) { 0 }
                }

                // Map back to actual row for win detection (only valid when stopped)
                val row = i
                val isWinCell = isStopped && row < rows && Pair(col, row) in winPositions

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(androidx.compose.ui.platform.LocalDensity.current) {
                            cellHeightPx.toInt().toDp()
                        })
                        .padding(2.dp)
                        .then(
                            if (isWinCell) Modifier.border(
                                2.dp, WinHighlight.copy(alpha = glowAlpha), RoundedCornerShape(4.dp)
                            ) else Modifier
                        )
                        .onGloballyPositioned { coordinates ->
                            if (isStopped && i < rows) {
                                val pos = coordinates.positionInParent()
                                cellPositions[Pair(col, i)] = Pair(pos.x, pos.y)
                                cellSizes[Pair(col, i)] = coordinates.size
                            }
                        }
                ) {
                    val resId = theme.elementResIds.getOrElse(symbolIndex) { theme.elementResIds[0] }
                    val isCurrentlySpinning = spinning && scrollAnim.value > 0.05f

                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize(0.85f)
                            .clip(RoundedCornerShape(4.dp))
                            .graphicsLayer {
                                if (isCurrentlySpinning) {
                                    scaleY = 1.1f
                                    alpha = 0.7f
                                }
                            }
                    )
                }
            }
        }
    }
}

// ─── Top Bar ────────────────────────────────────────────────────────────────

@Composable
private fun GameTopBar(
    theme: GameTheme,
    coins: Long,
    level: Int,
    levelProgress: Float,
    musicEnabled: Boolean = true,
    onBack: () -> Unit,
    onMusic: () -> Unit,
    onInfo: () -> Unit,
    onCoinsPositioned: (Float, Float) -> Unit = { _, _ -> },
    onStarPositioned: (Float, Float) -> Unit = { _, _ -> }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(theme.barColorDark.copy(alpha = 0.9f), theme.barColor.copy(alpha = 0.9f), theme.barColorDark.copy(alpha = 0.9f))
                )
            )
            .padding(horizontal = 40.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MenuButton(
            text = "BACK", fontSize = 14.sp, buttonRes = theme.buttonRes,
            modifier = Modifier.width(100.dp).height(46.dp),
            onClick = onBack
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.onGloballyPositioned {
                val pos = it.localToRoot(Offset.Zero)
                onCoinsPositioned(pos.x + it.size.width / 2f, pos.y + it.size.height / 2f)
            }
        ) {
            Image(
                painter = painterResource(id = com.gamehivecorp.taptita.R.drawable.coin),
                contentDescription = null, modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatCoins(coins), color = GoldYellow,
                fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = CasinoFont
            )
        }

        Spacer(modifier = Modifier.width(60.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = com.gamehivecorp.taptita.R.drawable.star),
                contentDescription = null,
                modifier = Modifier
                    .size(22.dp)
                    .onGloballyPositioned {
                        val pos = it.localToRoot(Offset.Zero)
                        onStarPositioned(pos.x + it.size.width / 2f, pos.y + it.size.height / 2f)
                    }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Lv.$level", color = Color.White, fontSize = 14.sp, fontFamily = CasinoFont)
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier.width(80.dp).height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                LinearProgressIndicator(
                    progress = { (levelProgress / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = GoldYellow, trackColor = Color.Transparent
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${levelProgress.toInt()}%",
                color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontFamily = CasinoFont
            )
        }

        Row {
            Box(modifier = Modifier.graphicsLayer { alpha = if (musicEnabled) 1f else 0.4f }) {
                SquareButton(
                    btnRes = R.drawable.sound_button,
                    btnMaxWidth = 0.09f, cooldownMillis = 0L, btnClickable = onMusic
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            SquareButton(
                btnRes = R.drawable.info_button,
                btnMaxWidth = 0.09f, btnClickable = onInfo
            )
        }
    }
}

// ─── Bottom Bar ─────────────────────────────────────────────────────────────

@Composable
private fun GameBottomBar(
    bet: Long, maxBet: Long, spinning: Boolean, lastWin: Long,
    speedLevel: Int, theme: GameTheme,
    onSpin: () -> Unit, onIncreaseBet: () -> Unit, onDecreaseBet: () -> Unit,
    onSpeed: () -> Unit,
    onBetPositioned: (Float, Float) -> Unit = { _, _ -> },
    onSpinPositioned: (Float, Float) -> Unit = { _, _ -> }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    listOf(theme.barColorDark.copy(alpha = 0.9f), theme.barColor.copy(alpha = 0.9f), theme.barColorDark.copy(alpha = 0.9f))
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SquareButton(
                btnRes = R.drawable.speed_button,
                btnMaxWidth = 0.06f, cooldownMillis = 300L, btnClickable = onSpeed
            )
            Text(text = "${speedLevel}x", color = Color.White, fontSize = 11.sp, fontFamily = CasinoFont)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LAST WIN",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp, fontFamily = CasinoFont
            )
            Text(
                text = if (lastWin > 0) "%,d".format(lastWin) else "—",
                color = if (lastWin > 0) GoldYellow else Color.White.copy(alpha = 0.4f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            MenuButton(
                text = "-", fontSize = 24.sp,
                buttonRes = theme.buttonRes,
                enabled = !spinning && bet > 1_000L,
                modifier = Modifier.width(50.dp).height(40.dp),
                onClick = onDecreaseBet
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.onGloballyPositioned {
                    val pos = it.localToRoot(Offset.Zero)
                    onBetPositioned(pos.x + it.size.width / 2f, pos.y + it.size.height / 2f)
                }
            ) {
                Text(text = "PLAY", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, fontFamily = CasinoFont)
                AnimatedBetText(bet = bet)
            }
            Spacer(modifier = Modifier.width(8.dp))
            MenuButton(
                text = "+", fontSize = 24.sp,
                buttonRes = theme.buttonRes,
                enabled = !spinning && bet < maxBet,
                modifier = Modifier.width(50.dp).height(40.dp),
                onClick = onIncreaseBet
            )
        }

        Box(
            modifier = Modifier.onGloballyPositioned {
                val pos = it.localToRoot(Offset.Zero)
                onSpinPositioned(pos.x + it.size.width / 2f, pos.y + it.size.height / 2f)
            }
        ) {
            SquareButton(
                btnRes = theme.spinButtonRes,
                btnMaxWidth = 0.12f, cooldownMillis = 500L,
                btnEnabled = !spinning, btnClickable = onSpin
            )
        }
    }
}

// ─── Animated Bet Counter ────────────────────────────────────────────────────

@Composable
private fun AnimatedBetText(bet: Long) {
    var displayBet by remember { mutableStateOf(bet) }

    LaunchedEffect(bet) {
        val start = displayBet
        val diff = bet - start
        if (diff == 0L) return@LaunchedEffect
        val steps = 8
        for (i in 1..steps) {
            displayBet = start + diff * i / steps
            delay(25L)
        }
        displayBet = bet
    }

    Text(
        text = "%,d".format(displayBet),
        color = GoldYellow,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = CasinoFont
    )
}

// ─── Flying Particle Animation ──────────────────────────────────────────────

@Composable
private fun SlotParticleFly(
    drawableRes: Int,
    startXPx: Float,
    startYPx: Float,
    targetXPx: Float,
    targetYPx: Float,
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
    if (t <= 0f) return

    val currentX = startXPx + (targetXPx - startXPx) * t
    val currentY = startYPx + (targetYPx - startYPx) * t - 80f * sin(t * Math.PI.toFloat())

    val offsetX = with(density) { currentX.toInt().toDp() }
    val offsetY = with(density) { currentY.toInt().toDp() }

    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(18.dp)
            .graphicsLayer {
                alpha = 1f - t * 0.3f
                scaleX = 1f - t * 0.4f
                scaleY = 1f - t * 0.4f
            }
    )
}