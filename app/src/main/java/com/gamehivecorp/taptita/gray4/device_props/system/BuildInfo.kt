package com.gamehivecorp.taptita.gray4.device_props.system

import android.os.Build
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class BuildInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val buildMap = linkedMapOf(
                "HARDWARE" to safe(Build.HARDWARE),
                "PRODUCT" to safe(Build.PRODUCT),
                "BOOTLOADER" to safe(Build.BOOTLOADER),
                "DISPLAY" to safe(Build.DISPLAY),
                "ID" to safe(Build.ID),
                "BOARD" to safe(Build.BOARD),
                "ABIS" to safeAbis(),
                "USER" to safe(Build.USER),
                "HOST" to safe(Build.HOST),
                "TYPE" to safe(Build.TYPE),
                "TAGS" to safe(Build.TAGS)
            )

            val payload = buildMap.entries
                .joinToString(",") { (key, value) -> "$key:$value" }

            "BUILD[$payload]"
        } catch (e: Throwable) {
            "BUILD[error:${e.javaClass.simpleName}]"
        }
    }

    private fun safe(value: String?): String {
        return value ?: "unknown"
    }

    private fun safeAbis(): String {
        return try {
            Build.SUPPORTED_ABIS?.joinToString("|") ?: "unknown"
        } catch (e: Throwable) {
            "unknown"
        }
    }
}
