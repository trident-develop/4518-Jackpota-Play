package com.gamehivecorp.taptita.gray4.ref

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

class ReferrerProvider(private val context: Context) {

    suspend fun fetch(): String = suspendCancellableCoroutine { continuation ->
        val client = InstallReferrerClient.newBuilder(context).build()
        val hasResumed = AtomicBoolean(false)

        continuation.invokeOnCancellation {
            if (client.isReady) client.endConnection()
        }

        client.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                try {
                    if (!hasResumed.compareAndSet(false, true)) return

                    val result = if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        client.installReferrer.installReferrer ?: "null"
                    } else {
                        "null"
                    }
                    // TODO Change
//                    continuation.resume("cmpgn=sub1_TEST-Deeplink_sub3_sub4_sub5_sub6")
                    continuation.resume(result)
                } catch (e: Exception) {
                    if (hasResumed.get()) return 
                    continuation.resume("null")
                } finally {
                    client.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                if (hasResumed.compareAndSet(false, true)) {
                    continuation.resume("null")
                }
            }
        })
    }
}