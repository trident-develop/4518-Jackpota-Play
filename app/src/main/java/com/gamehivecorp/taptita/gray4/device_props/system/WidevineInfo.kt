package com.gamehivecorp.taptita.gray4.device_props.system

import android.media.MediaDrm
import com.gamehivecorp.taptita.gray4.device_props.core.Info
import java.util.UUID

class WidevineInfo : Info {

    companion object {
        private val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)

        fun getDeviceId(): String {
            var drm: MediaDrm? = null
            return try {
                if (!MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)) "unsupported"
                else {
                    drm = MediaDrm(WIDEVINE_UUID)
                    drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                        .joinToString("") { "%02x".format(it) }
                        .ifBlank { "empty" }
                }
            } catch (_: Throwable) {
                "error"
            } finally {
                runCatching { drm?.close() }
            }
        }
    }

    override suspend fun collect(vararg args: Any?): String {
        var drm: MediaDrm? = null
        return try {
            if (!MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)) "WID[unsupported]"
            else {
                drm = MediaDrm(WIDEVINE_UUID)
                val idBytes = drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                val hexId = idBytes.joinToString("") { "%02x".format(it) }.ifBlank { "empty" }

                val securityLevel = try { drm.getPropertyString("securityLevel") }
                catch (_: Throwable) { "unknown" }

                "WID[$securityLevel,$hexId]"
            }
        } catch (_: Throwable) {
            "WID[undefined]"
        } finally {
            runCatching { drm?.close() }
        }
    }
}
