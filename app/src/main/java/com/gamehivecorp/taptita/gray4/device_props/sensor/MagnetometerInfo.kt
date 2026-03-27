package com.gamehivecorp.taptita.gray4.device_props.sensor

import com.gamehivecorp.taptita.gray4.device_props.core.Info
import com.gamehivecorp.taptita.gray4.device_props.core.DeviceMotionResult

class MagnetometerInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val deviceMotionResult = args[0] as DeviceMotionResult
            val score = deviceMotionResult.magScore ?: "undefined"
            "MAGN[$score]"
        } catch (e: Throwable) {
            "MAGN[undefined]"
        }
    }
}
