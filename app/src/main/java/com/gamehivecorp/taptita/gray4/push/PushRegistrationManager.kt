package com.gamehivecorp.taptita.gray4.push

import android.util.Log
import com.gamehivecorp.taptita.viewmodel.ApiService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.gamehivecorp.taptita.gray4.POSTBACK_API_URL
import com.gamehivecorp.taptita.gray4.POSTBACK_FCM_TOKEN_KEY
import com.gamehivecorp.taptita.gray4.POSTBACK_TRACKING_ID_KEY
import com.gamehivecorp.taptita.gray4.PUSH_NOTIFICATION_API_FCM_TOKEN_KEY
import com.gamehivecorp.taptita.gray4.PUSH_NOTIFICATION_API_GADID_KEY
import com.gamehivecorp.taptita.gray4.PUSH_NOTIFICATION_API_URL
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import java.util.Locale

class PushRegistrationManager(private val context: android.content.Context) {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://$PUSH_NOTIFICATION_API_URL")
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun registerDevice() {
        Log.d("TAGG", "Push Register starts...")
        try {
            val gadid = try { FirebaseAnalytics.getInstance(context).appInstanceId.await() } catch (e: Exception) { "error" }
            val rawFcmToken = try { FirebaseMessaging.getInstance().token.await() } catch (e: Exception) { "error" }

            val params = mapOf(
                PUSH_NOTIFICATION_API_GADID_KEY to gadid,
                PUSH_NOTIFICATION_API_FCM_TOKEN_KEY to rawFcmToken
            )

            val fullUrl = "https://$PUSH_NOTIFICATION_API_URL"
            val lang = Locale.getDefault().toLanguageTag()

            val response = apiService.getRedditPost(fullUrl, params, lang)

            if (response.isSuccessful) {
                Log.d("TAGG", "Push Register Success")
            } else {
                Log.d("TAGG", "Push Register Failed: ${response.code()}")
            }

        } catch (e: Exception) {
            Log.d("TAGG", "Push Error: ${e.message}")
        }
    }

    suspend fun sendPostback(trackingId: String) {
        Log.d("TAGG", "Postback starts for ID: $trackingId")
        try {
            val rawFcmToken = try { FirebaseMessaging.getInstance().token.await() } catch (e: Exception) { "error" }

            val params = mapOf(
                POSTBACK_TRACKING_ID_KEY to trackingId,
                POSTBACK_FCM_TOKEN_KEY to rawFcmToken
            )

            val fullUrl = "https://$POSTBACK_API_URL"
            val response = apiService.getUserInfo(fullUrl, params)

            if (response.isSuccessful) {
                Log.d("TAGG", "Postback Success")
            }
        } catch (e: Exception) {
            Log.d("TAGG", "Postback Error: ${e.message}")
        }
    }
}