package com.gamehivecorp.taptita.gray4.device_props.system

import android.os.SystemClock
import com.gamehivecorp.taptita.gray4.device_props.core.Info
import java.util.concurrent.TimeUnit

class UptimeInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val uptimeMin = TimeUnit.MILLISECONDS.toMinutes(SystemClock.uptimeMillis())
            val elapsedMin = TimeUnit.MILLISECONDS.toMinutes(SystemClock.elapsedRealtime())
            "UP[$uptimeMin;$elapsedMin]"
        } catch (_: Throwable) {
            "UP[error]"
        }
    }
}