package com.gamehivecorp.taptita.gray4.urlgenerator
import android.net.Uri
import androidx.core.net.toUri
import com.gamehivecorp.taptita.gray4.ADB_KEY
import com.gamehivecorp.taptita.gray4.BUILD_KEY
import com.gamehivecorp.taptita.gray4.CHRG_UP_BRIGHT_KEY
import com.gamehivecorp.taptita.gray4.CPU_KEY
import com.gamehivecorp.taptita.gray4.DEVICE_ID_KEY
import com.gamehivecorp.taptita.gray4.DEVICE_MODEL_KEY
import com.gamehivecorp.taptita.gray4.FIREBASE_INSTALL_ID
import com.gamehivecorp.taptita.gray4.FIRST_TIME_INSTALL_KEY
import com.gamehivecorp.taptita.gray4.GADID_KEY
import com.gamehivecorp.taptita.gray4.INSTALL_A11Y_KEY
import com.gamehivecorp.taptita.gray4.NETWORK_SECURITY_KEY
import com.gamehivecorp.taptita.gray4.PACKAGE_SOURCE_KEY
import com.gamehivecorp.taptita.gray4.REF_KEY
import com.gamehivecorp.taptita.gray4.SENSORS_KEY

class UrlGenerator(baseUrl: String) {
    private val uriBuilder = baseUrl.toUri().buildUpon()

    fun addQuery(key: String, value: String?): UrlGenerator {
        if (!value.isNullOrEmpty()) {
            uriBuilder.appendQueryParameter(key, value)
        }
        return this
    }

    fun addQueries(map: Map<String, String>): UrlGenerator {
        map.forEach { (key, value) ->
            addQuery(key, value)
        }
        return this
    }

    fun build(): String = uriBuilder.build().toString()
}

val listOfKeys = listOf(
    REF_KEY,
    GADID_KEY,
    DEVICE_MODEL_KEY,
    FIRST_TIME_INSTALL_KEY,
    PACKAGE_SOURCE_KEY,
    FIREBASE_INSTALL_ID,
    //----- device properties
    NETWORK_SECURITY_KEY,
    SENSORS_KEY,
    DEVICE_ID_KEY,
    CPU_KEY,
    BUILD_KEY,
    CHRG_UP_BRIGHT_KEY,
    INSTALL_A11Y_KEY,
    ADB_KEY
)