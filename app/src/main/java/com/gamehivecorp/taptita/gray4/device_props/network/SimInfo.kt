package com.gamehivecorp.taptita.gray4.device_props.network

import android.content.Context
import android.telephony.TelephonyManager
import com.gamehivecorp.taptita.gray4.device_props.core.Info

class SimInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        val undefined = "undefined"
        return try {
            val context = args[0] as Context
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkOperator = tm.networkOperatorName?.takeIf { it.isNotBlank() } ?: undefined
            val simOperator = tm.simOperatorName?.takeIf { it.isNotBlank() } ?: undefined
            "SIM[$networkOperator,$simOperator]"
        } catch (e: Throwable) {
            "SIM[$undefined]"
        }
    }
}
