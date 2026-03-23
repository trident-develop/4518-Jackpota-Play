package com.gamehivecorp.taptita

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.gamehivecorp.taptita.data.PreferencesManager
import com.gamehivecorp.taptita.navigation.AppNavGraph
import com.gamehivecorp.taptita.sound.SoundManager
import com.gamehivecorp.taptita.ui.theme.JackpotaPlayTheme

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: SoundManager
    private lateinit var prefs: PreferencesManager
    private val windowController by lazy {
        WindowInsetsControllerCompat(window, window.decorView)
    }
    private var multiTouchDetected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = SoundManager(this)
        prefs = PreferencesManager(this)

        if (prefs.musicEnabled) {
            soundManager.startMusic()
        }

        setContent {
            JackpotaPlayTheme(dynamicColor = false) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    soundManager = soundManager
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        soundManager.stopMusic()
    }

    override fun onResume() {
        super.onResume()
        windowController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowController.hide(WindowInsetsCompat.Type.systemBars())
        if (prefs.musicEnabled) {
            soundManager.startMusic()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1) {
            if (!multiTouchDetected) {
                multiTouchDetected = true
                val cancelEvent = MotionEvent.obtain(ev)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()
            }
            return true
        }
        if (multiTouchDetected) {
            if (ev.actionMasked == MotionEvent.ACTION_UP ||
                ev.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                multiTouchDetected = false
            }
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}
