package com.gamehivecorp.taptita.data

import androidx.compose.ui.graphics.Color
import com.gamehivecorp.taptita.R

enum class GameTheme(
    val displayName: String,
    val cardLogo: Int,
    val bgRes: Int,
    val buttonRes: Int,
    val slotRes: Int,
    val spinButtonRes: Int,
    val elementResIds: List<Int>,
    val columns: Int,
    val rows: Int,
    val unlockLevel: Int,
    val previewRes: Int,
    val miniRewardRes: Int,
    val majorRewardRes: Int,
    val grandRewardRes: Int,
    val royalRewardRes: Int,
    val barColor: Color,
    val barColorDark: Color,
    val winText: Int,
    val slotLogo: Int
) {
    AZTEC(
        displayName = "Aztec",
        bgRes = R.drawable.aztec_bg,
        cardLogo = R.drawable.aztec_element_6,
        slotRes = R.drawable.aztec_slot,
        slotLogo = R.drawable.aztec_logo,
        buttonRes = R.drawable.button_green,
        spinButtonRes = R.drawable.aztec_spin_button,
        elementResIds = listOf(
            R.drawable.aztec_element_1, R.drawable.aztec_element_2,
            R.drawable.aztec_element_3, R.drawable.aztec_element_4,
            R.drawable.aztec_element_5, R.drawable.aztec_element_6,
            R.drawable.aztec_element_7, R.drawable.aztec_element_8,
            R.drawable.aztec_element_9, R.drawable.aztec_element_10,
            R.drawable.aztec_element_11, R.drawable.aztec_element_12
        ),
        columns = 5,
        rows = 3,
        unlockLevel = 1,
        previewRes = R.drawable.aztec_bg,
        miniRewardRes = R.drawable.aztec_mini_reward,
        majorRewardRes = R.drawable.aztec_major_reward,
        grandRewardRes = R.drawable.aztec_grand_reward,
        royalRewardRes = R.drawable.aztec_royal_reward,
        barColor = Color(0xFF030E03),
        barColorDark = Color(0xFF0E250E),
        winText = R.drawable.aztec_free_coins_everyday
    ),
    LION(
        displayName = "Lion",
        bgRes = R.drawable.lion_bg,
        cardLogo = R.drawable.lion_element_7,
        slotRes = R.drawable.lion_slot,
        slotLogo = R.drawable.lion_logo,
        buttonRes = R.drawable.button_red,
        spinButtonRes = R.drawable.aztec_spin_button,
        elementResIds = listOf(
            R.drawable.lion_element_1, R.drawable.lion_element_2,
            R.drawable.lion_element_3, R.drawable.lion_element_4,
            R.drawable.lion_element_5, R.drawable.lion_element_6,
            R.drawable.lion_element_7, R.drawable.lion_element_8,
            R.drawable.lion_element_9, R.drawable.lion_element_10,
            R.drawable.lion_element_11, R.drawable.lion_element_12
        ),
        columns = 5,
        rows = 3,
        unlockLevel = 1,
        previewRes = R.drawable.lion_bg,
        miniRewardRes = R.drawable.lion_mini_reward,
        majorRewardRes = R.drawable.lion_major_reward,
        grandRewardRes = R.drawable.lion_grand_reward,
        royalRewardRes = R.drawable.lion_royal_reward,
        barColor = Color(0xFF2C0A01),
        barColorDark = Color(0xFF461508),
        winText = R.drawable.lion_free_coins_on_every_game
    ),
    PIRATE(
        displayName = "Pirate",
        bgRes = R.drawable.pirate_bg,
        cardLogo = R.drawable.pirate_element_11,
        slotRes = R.drawable.pirate_slot,
        slotLogo = R.drawable.pirate_logo,
        buttonRes = R.drawable.wood_button,
        spinButtonRes = R.drawable.pirate_spin_button,
        elementResIds = listOf(
            R.drawable.pirate_element_1, R.drawable.pirate_element_2,
            R.drawable.pirate_element_3, R.drawable.pirate_element_4,
            R.drawable.pirate_element_5, R.drawable.pirate_element_6,
            R.drawable.pirate_element_7, R.drawable.pirate_element_8,
            R.drawable.pirate_element_9, R.drawable.pirate_element_10,
            R.drawable.pirate_element_11, R.drawable.pirate_element_12
        ),
        columns = 5,
        rows = 3,
        unlockLevel = 5,
        previewRes = R.drawable.pirate_bg,
        miniRewardRes = R.drawable.pirate_mini_reward,
        majorRewardRes = R.drawable.pirate_major_reward,
        grandRewardRes = R.drawable.pirate_grand_reward,
        royalRewardRes = R.drawable.pirate_royal_reward,
        barColor = Color(0xFF002325),
        barColorDark = Color(0xFF02393D),
        winText = R.drawable.lion_free_coins_on_every_game
    ),
    ROYAL(
        displayName = "Royal",
        bgRes = R.drawable.royal_bg,
        cardLogo = R.drawable.royal_element_1,
        slotRes = R.drawable.royal_slot,
        slotLogo = R.drawable.royal_logo,
        buttonRes = R.drawable.button_purple,
        spinButtonRes = R.drawable.royal_spin_button,
        elementResIds = listOf(
            R.drawable.royal_element_1, R.drawable.royal_element_2,
            R.drawable.royal_element_3, R.drawable.royal_element_4,
            R.drawable.royal_element_5, R.drawable.royal_element_6,
            R.drawable.royal_element_7, R.drawable.royal_element_8
        ),
        columns = 3,
        rows = 3,
        unlockLevel = 10,
        previewRes = R.drawable.royal_bg,
        miniRewardRes = R.drawable.royal_mini_reward,
        majorRewardRes = R.drawable.royal_major_reward,
        grandRewardRes = R.drawable.royal_grand_reward,
        royalRewardRes = R.drawable.royal_royal_reward,
        barColor = Color(0xFF330A48),
        barColorDark = Color(0xFF1F0A3B),
        winText = R.drawable.royal_free_gold_coins
    ),
    SEVEN(
        displayName = "Seven",
        bgRes = R.drawable.seven_bg,
        cardLogo = R.drawable.seven_element_5,
        slotRes = R.drawable.seven_slot,
        slotLogo = R.drawable.seven_logo,
        buttonRes = R.drawable.button_yellow,
        spinButtonRes = R.drawable.treasure_spin_button,
        elementResIds = listOf(
            R.drawable.seven_element_1, R.drawable.seven_element_2,
            R.drawable.seven_element_3, R.drawable.seven_element_4,
            R.drawable.seven_element_5, R.drawable.seven_element_6,
            R.drawable.seven_element_7, R.drawable.seven_element_8,
            R.drawable.seven_element_9, R.drawable.seven_element_10
        ),
        columns = 3,
        rows = 3,
        unlockLevel = 15,
        previewRes = R.drawable.seven_bg,
        miniRewardRes = R.drawable.seven_mini_reward,
        majorRewardRes = R.drawable.seven_major_reward,
        grandRewardRes = R.drawable.seven_grand_reward,
        royalRewardRes = R.drawable.seven_royal_reward,
        barColor = Color(0xFF090C31),
        barColorDark = Color(0xFF05081A),
        winText = R.drawable.royal_free_gold_coins
    ),
    TREASURE(
        displayName = "Treasure",
        bgRes = R.drawable.treasure_bg,
        cardLogo = R.drawable.treasure_element_11,
        slotRes = R.drawable.treasure_slot,
        slotLogo = R.drawable.treasure_logo,
        buttonRes = R.drawable.button_orange,
        spinButtonRes = R.drawable.treasure_spin_button,
        elementResIds = listOf(
            R.drawable.treasure_element_1, R.drawable.treasure_element_2,
            R.drawable.treasure_element_3, R.drawable.treasure_element_4,
            R.drawable.treasure_element_5, R.drawable.treasure_element_6,
            R.drawable.treasure_element_7, R.drawable.treasure_element_8,
            R.drawable.treasure_element_9, R.drawable.treasure_element_10,
            R.drawable.treasure_element_11
        ),
        columns = 5,
        rows = 3,
        unlockLevel = 20,
        previewRes = R.drawable.treasure_bg,
        miniRewardRes = R.drawable.treasure_mini_reward,
        majorRewardRes = R.drawable.treasure_major_reward,
        grandRewardRes = R.drawable.treasure_grand_reward,
        royalRewardRes = R.drawable.treasure_royal_reward,
        barColor = Color(0xFF480486),
        barColorDark = Color(0xFF2C0B79),
        winText = R.drawable.aztec_free_coins_everyday
    );
}

enum class RewardTier { MINI, MAJOR, GRAND, ROYAL }

fun GameTheme.getRewardRes(tier: RewardTier): Int = when (tier) {
    RewardTier.MINI -> miniRewardRes
    RewardTier.MAJOR -> majorRewardRes
    RewardTier.GRAND -> grandRewardRes
    RewardTier.ROYAL -> royalRewardRes
}

fun getRewardTier(totalWin: Long, bet: Long): RewardTier {
    val ratio = if (bet > 0) totalWin.toDouble() / bet else 0.0
    return when {
        ratio >= 15.0 -> RewardTier.ROYAL
        ratio >= 8.0 -> RewardTier.GRAND
        ratio >= 3.0 -> RewardTier.MAJOR
        else -> RewardTier.MINI
    }
}

data class WinLine(
    val positions: List<Pair<Int, Int>>,
    val symbolIndex: Int,
    val matchCount: Int,
    val payout: Long
)

object WinLineDefinitions {
    fun getWinLines(columns: Int, rows: Int): List<List<Pair<Int, Int>>> {
        return if (columns == 5) {
            listOf(
                // Top row
                (0 until 5).map { it to 0 },
                // Middle row
                (0 until 5).map { it to 1 },
                // Bottom row
                (0 until 5).map { it to 2 },
                // V shape
                listOf(0 to 0, 1 to 1, 2 to 2, 3 to 1, 4 to 0),
                // ^ shape
                listOf(0 to 2, 1 to 1, 2 to 0, 3 to 1, 4 to 2)
            )
        } else {
            listOf(
                // Top row
                (0 until 3).map { it to 0 },
                // Middle row
                (0 until 3).map { it to 1 },
                // Bottom row
                (0 until 3).map { it to 2 },
                // Diagonal \
                listOf(0 to 0, 1 to 1, 2 to 2),
                // Diagonal /
                listOf(0 to 2, 1 to 1, 2 to 0)
            )
        }
    }
}

object PayoutTable {
    fun getPayout(symbolIndex: Int, matchCount: Int, totalSymbols: Int, bet: Long): Long {
        val symbolMultiplier = when {
            symbolIndex >= totalSymbols - 2 -> 5.0   // Top 2 symbols
            symbolIndex >= totalSymbols - 4 -> 3.0   // Next 2
            symbolIndex >= totalSymbols - 6 -> 1.5   // Next 2
            else -> 0.8                                // Low value
        }
        val matchMultiplier = when (matchCount) {
            5 -> 5.0
            4 -> 2.5
            3 -> 1.5
            else -> 0.0
        }
        return (bet * symbolMultiplier * matchMultiplier).toLong()
    }
}
