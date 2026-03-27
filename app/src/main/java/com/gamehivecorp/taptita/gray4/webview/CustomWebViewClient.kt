package com.gamehivecorp.taptita.gray4.webview

import android.content.Intent
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.gamehivecorp.taptita.gray4.BASE_URL_STRICT
import com.gamehivecorp.taptita.gray4.LINK_STORAGE_KEY
import com.gamehivecorp.taptita.gray4.datastore.StorageImpl
import com.gamehivecorp.taptita.gray4.push.PushRegistrationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CustomWebViewClient(
    private val activity: ComponentActivity,
    private val onStubRequired: () -> Unit = {}
) : WebViewClient() {
    private val storage by lazy { StorageImpl.getInstance(activity.applicationContext) }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val uri = request?.url ?: return false
        if (uri.scheme == "about") return false

        val intent = when (uri.scheme) {
            "intent" -> runCatching {
                Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
            }.onFailure {
                //Log.e("WebView", "Bad intent uri: $uri", it)
            }.getOrNull()

            "mailto" -> Intent(Intent.ACTION_SENDTO, uri)
            "tel" -> Intent(Intent.ACTION_DIAL, uri)
            "http", "https", "blob", "data" -> null
            else -> Intent(Intent.ACTION_VIEW, uri)
        }

        intent?.let {
            try {
                activity.startActivity(it)
                return true
            } catch (_: Throwable) {
            }

            // Try to resolve package name from intent
            val packageName = intent.`package`
                ?: intent.component?.packageName

            if (packageName == null) {
                Toast.makeText(
                    activity,
                    "No application found!",
                    Toast.LENGTH_LONG
                ).show()
                return true
            }

            // Try to open Google Play via market:// intent
            try {
                val googlePlayIntent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$packageName".toUri()
                )
                googlePlayIntent.setPackage("com.android.vending")
                activity.startActivity(googlePlayIntent)
                return true
            } catch (_: Throwable) {
            }

            // Try to open any market via market:// intent
            try {
                val marketIntent = Intent(
                    Intent.ACTION_VIEW,
                    "market://details?id=$packageName".toUri()
                )
                activity.startActivity(marketIntent)
                return true
            } catch (_: Throwable) {
            }

            // Last resort: try to open Google Play in browser
            try {
                val playStoreIntent = Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$packageName".toUri()
                )
                activity.startActivity(playStoreIntent)
            } catch (_: Throwable) {
                // All attempts to open Google Play failed
                Toast.makeText(activity, "No application found!", Toast.LENGTH_LONG).show()
            }
            return true
        }
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        CoroutineScope(Dispatchers.IO).launch {
            CookieManager.getInstance().flush()
        }

        if (url != null) {
            Log.d("TAGG", "OnPageFinished")
            if (url.startsWith("https://$BASE_URL_STRICT")) {
                Log.d("TAGG", "STUB")
                openStub()
            } else {
                Log.d("TAGG", "Try to save $url")
                saveUrlIfNeeded(url)
                (view as? CustomWebView)?.showWebView()
            }
        }
    }

    private val storageMutex = Mutex()

    private fun saveUrlIfNeeded(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            storageMutex.withLock {
                val savedUrl = storage.getString(LINK_STORAGE_KEY)
                if (savedUrl == null) {
                    Log.d("TAGG", "SAVED URL $url")
                    storage.putString(LINK_STORAGE_KEY, url)
                    val push = PushRegistrationManager(this@CustomWebViewClient.activity)
                    push.registerDevice()
                }
            }
        }
    }

    private fun openStub() {
        onStubRequired.invoke()
    }
}