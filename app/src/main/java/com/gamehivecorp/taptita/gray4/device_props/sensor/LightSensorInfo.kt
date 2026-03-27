package com.gamehivecorp.taptita.gray4.device_props.sensor

import com.gamehivecorp.taptita.gray4.device_props.core.DeviceMotionResult
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class LightSensorInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val deviceMotionResult = args[0] as DeviceMotionResult
            val score = deviceMotionResult.lightScore ?: "undefined"
            "LIGHT[$score]"
        } catch (e: Throwable) {
            "LIGHT[undefined]"
        }
    }
}
