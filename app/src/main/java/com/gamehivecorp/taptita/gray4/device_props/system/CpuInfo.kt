package com.gamehivecorp.taptita.gray4.device_props.system

import android.os.Build
import com.gamehivecorp.taptita.gray4.device_props.core.Info
import java.security.MessageDigest

class CpuInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val cpuInfo = StringBuilder()
            cpuInfo.append(Build.HARDWARE ?: "")
            cpuInfo.append(Build.BOARD ?: "")
            cpuInfo.append(Build.SUPPORTED_ABIS?.joinToString(",") ?: "")
            
            val hash = MessageDigest.getInstance("SHA-256")
                .digest(cpuInfo.toString().toByteArray())
                .joinToString("") { "%02x".format(it) }
                .take(16)
            
            "CPU[$hash]"
        } catch (e: Throwable) {
            "CPU[undefined]"
        }
    }
}
