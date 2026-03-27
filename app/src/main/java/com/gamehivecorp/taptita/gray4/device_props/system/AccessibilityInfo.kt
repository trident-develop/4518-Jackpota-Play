package com.gamehivecorp.taptita.gray4.device_props.system

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.gamehivecorp.taptita.gray4.device_props.core.Info

@Suppress("DEPRECATION")
class AccessibilityInfo : Info {
    override suspend fun collect(vararg args: Any?): String = runCatching {
        val context = args[0] as Context
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val services =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val packages = services.mapNotNull { it.resolveInfo?.serviceInfo?.packageName }
            .distinct().joinToString(",").ifBlank { "na" }
        return "A11Y[${am.isEnabled},${services.size},$packages]"
    }.getOrDefault("A11Y[undefined]")
}
