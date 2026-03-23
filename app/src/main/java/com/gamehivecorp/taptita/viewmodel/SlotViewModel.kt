package com.gamehivecorp.taptita.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gamehivecorp.taptita.data.GameTheme
import com.gamehivecorp.taptita.data.PayoutTable
import com.gamehivecorp.taptita.data.PreferencesManager
import com.gamehivecorp.taptita.data.WinLine
import com.gamehivecorp.taptita.data.WinLineDefinitions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SlotUiState(
    val grid: List<List<Int>> = emptyList(),
    val displayGrid: List<List<Int>> = emptyList(),
    val spinning: Boolean = false,
    val columnStopped: List<Boolean> = emptyList(),
    val coins: Long = 50_000L,
    val bet: Long = 1_000L,
    val level: Int = 1,
    val levelProgress: Float = 0f,
    val winLines: List<WinLine> = emptyList(),
    val totalWin: Long = 0L,
    val lastWin: Long = 0L,
    val showWinScreen: Boolean = false,
    val showLevelUp: Boolean = false,
    val autoSpin: Boolean = false,
    val speedLevel: Int = 1,
    val maxBet: Long = 5_000L,
    // Reel animation data
    val reelStrips: List<List<Int>> = emptyList(), // per column: long strip of symbol indices
    val spinTrigger: Int = 0 // incremented to trigger UI animation
)

class SlotViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val _uiState = MutableStateFlow(SlotUiState())
    val uiState: StateFlow<SlotUiState> = _uiState.asStateFlow()

    private var currentTheme: GameTheme = GameTheme.AZTEC
    private var autoSpinJob: Job? = null

    companion object {
        // How many extra symbols to scroll through before landing
        const val REEL_EXTRA_SYMBOLS = 20
    }

    fun initialize(theme: GameTheme) {
        currentTheme = theme
        val maxBet = calculateMaxBet(prefs.level)
        val initialGrid = generateRandomGrid(theme)
        _uiState.value = SlotUiState(
            grid = initialGrid,
            displayGrid = initialGrid,
            columnStopped = List(theme.columns) { true },
            coins = prefs.coins,
            bet = 1_000L,
            level = prefs.level,
            levelProgress = prefs.levelProgress,
            maxBet = maxBet
        )
    }

    private fun calculateMaxBet(level: Int): Long {
        return (1_000L + (level - 1) * 1_000L).coerceAtLeast(1_000L).coerceAtMost(50_000L)
    }

    private fun generateRandomGrid(theme: GameTheme): List<List<Int>> {
        val numSymbols = theme.elementResIds.size
        val cols = theme.columns
        val rows = theme.rows

        if ((1..100).random() <= 40) {
            return generateBiasedGrid(theme, numSymbols, cols, rows)
        }
        if ((1..100).random() <= 25) {
            return generateNearWinGrid(theme, numSymbols, cols, rows)
        }
        return List(cols) { List(rows) { (0 until numSymbols).random() } }
    }

    private fun generateBiasedGrid(
        theme: GameTheme, numSymbols: Int, cols: Int, rows: Int
    ): List<List<Int>> {
        val grid = MutableList(cols) { MutableList(rows) { (0 until numSymbols).random() } }
        val winLines = WinLineDefinitions.getWinLines(cols, rows)
        val chosenLine = winLines.random()
        val symbol = if ((1..100).random() <= 70) {
            (0 until (numSymbols / 2).coerceAtLeast(1)).random()
        } else if ((1..100).random() <= 70) {
            (numSymbols / 2 until (numSymbols - 2).coerceAtLeast(numSymbols / 2 + 1)).random()
        } else {
            (numSymbols - 2 until numSymbols).random()
        }
        val matchCount = if (cols == 3) 3 else listOf(3, 3, 3, 4, 5).random()
        for (i in 0 until matchCount.coerceAtMost(chosenLine.size)) {
            val (c, r) = chosenLine[i]
            grid[c][r] = symbol
        }
        return grid
    }

    private fun generateNearWinGrid(
        theme: GameTheme, numSymbols: Int, cols: Int, rows: Int
    ): List<List<Int>> {
        val grid = MutableList(cols) { MutableList(rows) { (0 until numSymbols).random() } }
        val winLines = WinLineDefinitions.getWinLines(cols, rows)
        val chosenLine = winLines.random()
        val symbol = (0 until numSymbols).random()
        val count = if (cols == 3) 2 else listOf(2, 2, 3).random()
        for (i in 0 until count.coerceAtMost(chosenLine.size)) {
            val (c, r) = chosenLine[i]
            grid[c][r] = symbol
        }
        return grid
    }

    /**
     * Build a reel strip per column:
     * [final symbols] + [random symbols] + [current symbols]
     * Animation starts at position 1 (end = current), scrolls DOWN to 0 (start = final).
     * This way the current symbols stay visible at start, then spin away.
     */
    private fun buildReelStrips(
        finalGrid: List<List<Int>>,
        currentGrid: List<List<Int>>
    ): List<List<Int>> {
        val numSymbols = currentTheme.elementResIds.size
        return List(finalGrid.size) { col ->
            val extraCount = REEL_EXTRA_SYMBOLS + col * 5
            val randomPart = List(extraCount) { (0 until numSymbols).random() }
            val finalPart = finalGrid[col]
            val currentPart = currentGrid.getOrElse(col) {
                List(currentTheme.rows) { (0 until numSymbols).random() }
            }
            finalPart + randomPart + currentPart
        }
    }

    fun spin() {
        val state = _uiState.value
        if (state.spinning) return
        if (state.coins < state.bet) return

        val newCoins = state.coins - state.bet
        prefs.coins = newCoins

        val finalGrid = generateRandomGrid(currentTheme)
        val strips = buildReelStrips(finalGrid, state.displayGrid)

        _uiState.value = state.copy(
            coins = newCoins,
            spinning = true,
            winLines = emptyList(),
            totalWin = 0L,
            showWinScreen = false,
            grid = finalGrid,
            displayGrid = finalGrid,
            columnStopped = List(currentTheme.columns) { false },
            reelStrips = strips,
            spinTrigger = state.spinTrigger + 1
        )

        // Wait for UI animation to complete, then resolve wins
        viewModelScope.launch {
            val baseDuration = when (state.speedLevel) {
                1 -> 2500L
                2 -> 1600L
                3 -> 900L
                else -> 2500L
            }
            // Last column finishes last: base + columns * stagger
            val totalWait = baseDuration + currentTheme.columns * 300L
            delay(totalWait)

            val winLines = checkWins(finalGrid, _uiState.value.bet)
            val totalWin = winLines.sumOf { it.payout }

            val progressGain = if (totalWin > 0) 7.5f else 4.5f
            var newProgress = _uiState.value.levelProgress + progressGain
            var newLevel = _uiState.value.level
            var showLevelUp = false
            if (newProgress >= 100f) {
                newProgress -= 100f
                newLevel++
                showLevelUp = true
                prefs.level = newLevel
            }
            prefs.levelProgress = newProgress

            // If no win, coins stay as is. If win, coins are added after dismissing win screen.
            val showWin = totalWin > 0 && !showLevelUp
            val coinsNow = if (showWin) {
                _uiState.value.coins // don't add yet — will add on dismiss
            } else {
                val c = _uiState.value.coins + totalWin
                prefs.coins = c
                c
            }

            _uiState.value = _uiState.value.copy(
                displayGrid = finalGrid,
                spinning = false,
                coins = coinsNow,
                winLines = winLines,
                totalWin = totalWin,
                lastWin = if (totalWin > 0) totalWin else _uiState.value.lastWin,
                showWinScreen = showWin,
                level = newLevel,
                levelProgress = newProgress,
                showLevelUp = showLevelUp,
                maxBet = calculateMaxBet(newLevel),
                columnStopped = List(currentTheme.columns) { true }
            )

            if (_uiState.value.autoSpin && !showLevelUp && totalWin <= 0) {
                delay(500L)
                spin()
            }
        }
    }

    private fun checkWins(grid: List<List<Int>>, bet: Long): List<WinLine> {
        val winLinePositions = WinLineDefinitions.getWinLines(currentTheme.columns, currentTheme.rows)
        val wins = mutableListOf<WinLine>()
        for (linePositions in winLinePositions) {
            val symbols = linePositions.map { (col, row) -> grid[col][row] }
            val firstSymbol = symbols[0]
            if (currentTheme.columns == 3) {
                if (symbols.all { it == firstSymbol }) {
                    val payout = PayoutTable.getPayout(firstSymbol, 3, currentTheme.elementResIds.size, bet)
                    wins.add(WinLine(linePositions, firstSymbol, 3, payout))
                }
            } else {
                var matchCount = 1
                for (i in 1 until symbols.size) {
                    if (symbols[i] == firstSymbol) matchCount++ else break
                }
                if (matchCount >= 3) {
                    val payout = PayoutTable.getPayout(firstSymbol, matchCount, currentTheme.elementResIds.size, bet)
                    wins.add(WinLine(linePositions.take(matchCount), firstSymbol, matchCount, payout))
                }
            }
        }
        return wins
    }

    fun increaseBet() {
        val state = _uiState.value
        if (state.spinning) return
        _uiState.value = state.copy(bet = (state.bet + 1_000L).coerceAtMost(state.maxBet))
    }

    fun decreaseBet() {
        val state = _uiState.value
        if (state.spinning) return
        _uiState.value = state.copy(bet = (state.bet - 1_000L).coerceAtLeast(1_000L))
    }

    fun toggleAutoSpin() {
        val state = _uiState.value
        val newAuto = !state.autoSpin
        _uiState.value = state.copy(autoSpin = newAuto)
        if (newAuto && !state.spinning) spin()
        else if (!newAuto) autoSpinJob?.cancel()
    }

    fun cycleSpeed() {
        val state = _uiState.value
        _uiState.value = state.copy(speedLevel = if (state.speedLevel >= 3) 1 else state.speedLevel + 1)
    }

    fun dismissWinScreen() {
        val state = _uiState.value
        val newCoins = state.coins + state.totalWin
        prefs.coins = newCoins
        _uiState.value = state.copy(showWinScreen = false, coins = newCoins)
        if (_uiState.value.autoSpin && !_uiState.value.showLevelUp) {
            viewModelScope.launch { delay(300); spin() }
        }
    }

    fun dismissLevelUp() {
        _uiState.value = _uiState.value.copy(showLevelUp = false)
        if (_uiState.value.autoSpin) {
            viewModelScope.launch { delay(300); spin() }
        }
    }
}
