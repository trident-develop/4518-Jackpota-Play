package com.gamehivecorp.taptita.gray4
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.gamehivecorp.taptita.gray4.ADB
import com.gamehivecorp.taptita.gray4.device_props.core.DevicePropertiesResult
import com.gamehivecorp.taptita.gray4.ref.ReferrerProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.Locale

object OpaqueRecursiveManager {

    private val SEQUENCE = intArrayOf(0x101, 0x202, 0x303, 0x404, 0x505, 0x606,
        0x707, 0x808, 0x909, 0xA0A, 0xB0B, 0xC0C, 0xD0D, 0xE0E)

    private const val INITIAL_SALT = 0xAF
    private const val ZERO_ID = "00000000-0000-0000-0000-000000000000"

    private val buckets = MutableList<ByteArray?>(SEQUENCE.size) { null }

    fun stream(context: Context): Flow<Pair<Int, String>> = flow {
        coroutineScope {
            val appContext = context.applicationContext

            val deferreds = SEQUENCE.indices.map { index ->
                async(Dispatchers.IO) {
                    getRawData(
                        appContext,
                        SEQUENCE[index]
                    )
                }
            }

            var salt = INITIAL_SALT
            for (index in SEQUENCE.indices) {
                val rawData = deferreds[index].await()
                val encrypted = xorProcess(rawData, salt)
                buckets[index] = encrypted
                emit(index to rawData)
                salt = rawData.hashCode() and 0xFF
            }
        }
    }.flowOn(Dispatchers.IO)


    @SuppressLint("AdvertisingIdPolicy")
    private suspend fun getRawData(context: Context, key: Int): String {
        val devProps = DevicePropertiesResult.create(context)
        return try {
            when (key) {
                0x101 -> {
                    ReferrerProvider(context).fetch()
                }

                0x202 -> {
                    try {
                        val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
                        if (!info.isLimitAdTrackingEnabled) info.id
                            ?: ZERO_ID else ZERO_ID
                    } catch (e: Exception) {
                        ZERO_ID
                    }
                }

                0x303 -> {
                    "${Build.BRAND.replaceFirstChar { it.titlecase(Locale.getDefault()) }} ${Build.MODEL}"
//                    "" // TODO Change
                }

                0x404 -> {
                    context
                        .packageManager
                        .getPackageInfo(context.packageName, 0)
                        .firstInstallTime.
                        toString()
                }

                0x505 -> {
                    runCatching {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val packageManager = context.packageManager
                            val sourceInfo =
                                packageManager.getInstallSourceInfo(context.packageName)
                            sourceInfo.packageSource
                        } else null
                    }.getOrNull().toString()
                }

                0x606 -> {
                    try {Firebase.analytics.appInstanceId.await()} catch (e: Exception) { "error" }
                }

                0x707 -> {
                    devProps.getX4()
                }

                0x808 -> {
                    devProps.getX5()
                }

                0x909 -> {
                    devProps.getX8()
                }

                0xA0A -> {
                    devProps.getX9()
                }

                0xB0B -> {
                    devProps.getX10()
                }

                0xC0C -> {
                    devProps.getS28()
                }

                0xD0D -> {
                    devProps.getS30()
                }

                0xE0E -> {
                    try {
                        Settings.Global.getString(context.contentResolver, ADB) ?: "1"
                    } catch (e: Exception) {
                        "1"
                    }
                }

                else -> ""
            }
        } catch (e: Exception) {
            "e_$key"
        }
    }

    private fun xorProcess(data: String, salt: Int): ByteArray {
        return data.map { (it.code xor salt).toByte() }.toByteArray()
    }


    fun drain(): List<String> {
        val results = mutableListOf<String>()
        var salt = INITIAL_SALT

        for (i in SEQUENCE.indices) {
            val encrypted = buckets[i] ?: break

            val decrypted = encrypted
                .map { (it.toInt() xor salt).toChar() }
                .joinToString("")

            results.add(decrypted)

            salt = decrypted.hashCode() and 0xFF
        }

        return results
    }
}