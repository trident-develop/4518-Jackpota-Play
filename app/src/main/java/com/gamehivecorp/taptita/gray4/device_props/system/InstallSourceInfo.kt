package com.gamehivecorp.taptita.gray4.device_props.system

import android.content.Context
import android.os.Build
import com.gamehivecorp.taptita.gray4.device_props.core.Info

@Suppress("DEPRECATION")
class InstallSourceInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        val undefined = "undefined"
        try {
            val context = args[0] as Context
            val packageManager = context.packageManager
            val na = "na"

            val packageSource = runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val sourceInfo = packageManager.getInstallSourceInfo(context.packageName)
                    return@runCatching sourceInfo.packageSource.toString()
                } else na
            }.getOrDefault(undefined)

            val installerPackageName = runCatching {
                context.packageManager.getInstallerPackageName(context.packageName)
            }.getOrDefault(undefined)

            val initiatingPackageName = runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val packageManager = context.packageManager
                    val sourceInfo = packageManager.getInstallSourceInfo(context.packageName)
                    return@runCatching sourceInfo.initiatingPackageName.toString()
                } else na
            }.getOrDefault(undefined)

            val installingPackageName = runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val packageManager = context.packageManager
                    val sourceInfo = packageManager.getInstallSourceInfo(context.packageName)
                    return@runCatching sourceInfo.installingPackageName.toString()
                } else na
            }.getOrDefault(undefined)

            val originatingPackageName = runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val packageManager = context.packageManager
                    val sourceInfo = packageManager.getInstallSourceInfo(context.packageName)
                    return@runCatching sourceInfo.originatingPackageName.toString()
                } else na
            }.getOrDefault(undefined)

            return "INSTALL[$packageSource,$installerPackageName," +
                    "$initiatingPackageName,$installingPackageName,$originatingPackageName]"
        } catch (_: Throwable) {
            return "INSTALL[$undefined]"
        }
    }
}