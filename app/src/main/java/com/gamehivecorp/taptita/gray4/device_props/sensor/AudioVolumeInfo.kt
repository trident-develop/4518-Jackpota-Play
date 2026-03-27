package com.gamehivecorp.taptita.gray4.device_props.sensor

import android.content.Context
import android.media.AudioManager
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class AudioVolumeInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val context = args[0] as Context
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            if (maxVolume > 0) {
                "VOL[${(currentVolume * 100) / maxVolume}]"
            } else {
                "VOL[0]"
            }
        } catch (e: Throwable) {
            "VOL[undefined]"
        }
    }
}
