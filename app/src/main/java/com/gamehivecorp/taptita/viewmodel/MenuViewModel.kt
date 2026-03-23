package com.gamehivecorp.taptita.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.gamehivecorp.taptita.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LeaderboardEntry(val name: String, var score: Long)

data class MenuUiState(
    val coins: Long = 50_000L,
    val level: Int = 1,
    val levelProgress: Float = 0f,
    val dailyBonusDay: Int = 0,
    val canClaimDailyBonus: Boolean = true,
    val giftBoxVisible: Boolean = true,
    val giftBoxCooldownEnd: Long = 0L,
    val leaderboard: List<LeaderboardEntry> = emptyList()
)

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private val playerNames = listOf(
        "LuckyAce", "GoldRush", "JackpotKing", "SpinMaster", "CoinHunter",
        "DiamondDave", "SlotQueen", "BigWinner", "FortuneX", "MegaSpin",
        "RoyalFlush", "StarPlayer", "CashKing", "BonusBoss", "WildCard",
        "TreasureHunt", "NeonLights", "HighRoller", "GoldenEagle", "PlatinumPlay"
    )

    init {
        loadState()
    }

    private fun loadState() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastBonus = prefs.lastDailyBonusDate
        val canClaim = lastBonus != today
        val giftCooldownEnd = prefs.lastGiftBoxTime + 30 * 60 * 1000L
        val now = System.currentTimeMillis()

        val leaderboard = loadOrCreateLeaderboard()

        _uiState.value = MenuUiState(
            coins = prefs.coins,
            level = prefs.level,
            levelProgress = prefs.levelProgress,
            dailyBonusDay = prefs.dailyBonusDay,
            canClaimDailyBonus = canClaim,
            giftBoxVisible = now >= giftCooldownEnd || prefs.lastGiftBoxTime == 0L,
            giftBoxCooldownEnd = if (prefs.lastGiftBoxTime == 0L) 0L else giftCooldownEnd,
            leaderboard = leaderboard
        )
    }

    fun refreshState() {
        loadState()
    }

    private fun loadOrCreateLeaderboard(): List<LeaderboardEntry> {
        val saved = prefs.leaderboardScores
        if (saved.isNotEmpty()) {
            return saved.split(";").mapNotNull { entry ->
                val parts = entry.split(",")
                if (parts.size == 2) LeaderboardEntry(parts[0], parts[1].toLongOrNull() ?: 0L)
                else null
            }
        }
        val board = playerNames.map { name ->
            LeaderboardEntry(name, (5_000L..500_000L).random())
        }.sortedByDescending { it.score }
        saveLeaderboard(board)
        return board
    }

    private fun saveLeaderboard(board: List<LeaderboardEntry>) {
        prefs.leaderboardScores = board.joinToString(";") { "${it.name},${it.score}" }
    }

    fun claimDailyBonus() {
        val state = _uiState.value
        if (!state.canClaimDailyBonus) return

        val newDay = if (state.dailyBonusDay >= 5) 1 else state.dailyBonusDay + 1
        val bonusAmount = newDay * 5_000L
        val newCoins = state.coins + bonusAmount

        prefs.coins = newCoins
        prefs.dailyBonusDay = newDay
        prefs.lastDailyBonusDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        _uiState.value = state.copy(
            coins = newCoins,
            dailyBonusDay = newDay,
            canClaimDailyBonus = false
        )
    }

    fun claimGiftBox() {
        val state = _uiState.value
        if (!state.giftBoxVisible) return

        val giftAmount = (1_000L..10_000L).random()
        val newCoins = state.coins + giftAmount
        val now = System.currentTimeMillis()

        prefs.coins = newCoins
        prefs.lastGiftBoxTime = now

        _uiState.value = state.copy(
            coins = newCoins,
            giftBoxVisible = false,
            giftBoxCooldownEnd = now + 30 * 60 * 1000L
        )
    }

    fun updateLeaderboard(userCoins: Long): List<LeaderboardEntry> {
        val board = _uiState.value.leaderboard.toMutableList()
        val existingIndex = board.indexOfFirst { it.name == "YOU" }
        if (existingIndex >= 0) {
            board[existingIndex] = board[existingIndex].copy(score = userCoins)
        } else {
            val insertIndex = board.indexOfFirst { userCoins > it.score }
            if (insertIndex >= 0) {
                board.add(insertIndex, LeaderboardEntry("YOU", userCoins))
                if (board.size > 20) board.removeAt(board.lastIndex)
            }
        }
        val sorted = board.sortedByDescending { it.score }
        saveLeaderboard(sorted)
        _uiState.value = _uiState.value.copy(leaderboard = sorted)
        return sorted
    }

    fun getDailyBonusAmount(day: Int): Long = day * 5_000L
}
