package com.gamehivecorp.taptita.sound

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.data.PreferencesManager

class SoundManager(private val context: Context) {

    private val prefs = PreferencesManager(context)
    private var mediaPlayer: MediaPlayer? = null
    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(3).build()
    private var spinSoundId: Int = 0
    private var levelCompleteSoundId: Int = 0
    var isMusicPlaying = false
        private set

    init {
        spinSoundId = soundPool.load(context, R.raw.slot_rounded, 1)
        levelCompleteSoundId = soundPool.load(context, R.raw.level_complete, 1)
    }

    fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.game_music).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        }
        mediaPlayer?.start()
        isMusicPlaying = true
    }

    fun stopMusic() {
        mediaPlayer?.pause()
        isMusicPlaying = false
    }

    fun toggleMusic(): Boolean {
        if (isMusicPlaying) {
            stopMusic()
        } else {
            startMusic()
        }
        prefs.musicEnabled = isMusicPlaying
        return isMusicPlaying
    }

    fun playSpinSound() {
        soundPool.play(spinSoundId, 0.8f, 0.8f, 1, 0, 1f)
    }

    fun playWinSound() {
        soundPool.play(levelCompleteSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playLevelUpSound() {
        soundPool.play(levelCompleteSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool.release()
    }
}
