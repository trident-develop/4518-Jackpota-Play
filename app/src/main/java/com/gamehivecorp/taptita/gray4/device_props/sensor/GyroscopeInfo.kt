package com.gamehivecorp.taptita.gray4.device_props.sensor

import com.gamehivecorp.taptita.gray4.device_props.core.DeviceMotionResult
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class GyroscopeInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val deviceMotionResult = args[0] as DeviceMotionResult
            val score = deviceMotionResult.gyroScore ?: "undefined"
            "GYRO[$score]"
        } catch (e: Throwable) {
            "GYRO[undefined]"
        }
    }
}
