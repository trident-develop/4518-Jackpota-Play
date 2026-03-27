package com.gamehivecorp.taptita.gray4.device_props.system

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class BatteryInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val context = args[0] as Context
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
            if (!isCharging) {
                return "CHRG[false]"
            }
            val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
            val isAc = (plugged and BatteryManager.BATTERY_PLUGGED_AC) != 0
            val isUsb = (plugged and BatteryManager.BATTERY_PLUGGED_USB) != 0
            val isWireless = (plugged and BatteryManager.BATTERY_PLUGGED_WIRELESS) != 0
            val isDock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                (plugged and BatteryManager.BATTERY_PLUGGED_DOCK) != 0
            } else false

            val source = when {
                isAc -> "ac"
                isUsb -> "usb"
                isWireless -> "wireless"
                isDock -> "dock"
                else -> "unknown"
            }

            "CHRG[$source]"
        } catch (_: Throwable) {
            "CHRG[undefined]"
        }
    }
}
