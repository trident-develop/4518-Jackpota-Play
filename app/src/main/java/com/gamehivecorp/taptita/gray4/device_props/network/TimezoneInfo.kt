package com.gamehivecorp.taptita.gray4.device_props.network

import com.gamehivecorp.taptita.gray4.device_props.core.Info
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import java.util.TimeZone


class TimezoneInfo : Info {
    override suspend fun collect(vararg args: Any?): String {
        return try {
            val displayName1 = ZoneId.systemDefault()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            val displayName2 = TimeZone.getDefault().displayName
            "TZ[$displayName1,$displayName2]"
        } catch (e: Throwable) {
            "TZ[undefined]"
        }
    }
}
