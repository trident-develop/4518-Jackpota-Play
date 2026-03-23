package com.gamehivecorp.taptita.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("jackpota_prefs", Context.MODE_PRIVATE)

    var coins: Long
        get() = prefs.getLong("coins", 50_000L)
        set(value) = prefs.edit { putLong("coins", value) }

    var level: Int
        get() = prefs.getInt("level", 1)
        set(value) = prefs.edit { putInt("level", value) }

    var levelProgress: Float
        get() = prefs.getFloat("level_progress", 0f)
        set(value) = prefs.edit { putFloat("level_progress", value) }

    var musicEnabled: Boolean
        get() = prefs.getBoolean("music_enabled", true)
        set(value) = prefs.edit { putBoolean("music_enabled", value) }

    var dailyBonusDay: Int
        get() = prefs.getInt("daily_bonus_day", 0)
        set(value) = prefs.edit { putInt("daily_bonus_day", value) }

    var lastDailyBonusDate: String
        get() = prefs.getString("last_daily_bonus_date", "") ?: ""
        set(value) = prefs.edit { putString("last_daily_bonus_date", value) }

    var lastGiftBoxTime: Long
        get() = prefs.getLong("last_gift_box_time", 0L)
        set(value) = prefs.edit { putLong("last_gift_box_time", value) }

    var leaderboardScores: String
        get() = prefs.getString("leaderboard_scores", "") ?: ""
        set(value) = prefs.edit { putString("leaderboard_scores", value) }
}
