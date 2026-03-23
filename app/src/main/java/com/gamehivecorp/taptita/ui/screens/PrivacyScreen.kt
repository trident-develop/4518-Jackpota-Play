package com.gamehivecorp.taptita.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.ui.components.CasinoFont
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.theme.BarPurple
import com.gamehivecorp.taptita.ui.theme.BarPurpleDark
import com.gamehivecorp.taptita.ui.theme.DarkBackground
import com.gamehivecorp.taptita.ui.theme.GoldYellow

@Composable
fun PrivacyScreen(onBack: () -> Unit) {

    var loadWeb by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBackground, Color(0xFF2D0052), DarkBackground)
                )
            )
    ) {
        Image(
            painter = painterResource(R.drawable.seven_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(BarPurpleDark, BarPurple, BarPurpleDark))
                    )
                    .padding(horizontal = 40.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuButton(
                    text = "BACK",
                    fontSize = 16.sp,
                    onClick = onBack,
                    modifier = Modifier.width(100.dp).height(46.dp)
                )

                Text(
                    text = "PRIVACY POLICY",
                    color = GoldYellow,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CasinoFont
                )

                Spacer(modifier = Modifier.width(100.dp))
            }

            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        val webView = WebView(context).apply {
                            settings.setSupportZoom(true)
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    loadWeb = false
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    return false
                                }
                            }
                            loadUrl("https://telegra.ph/Privacy-Policy-for-Jackpota-Play-03-20")
                        }
                        addView(
                            webView, FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}

@Composable
private fun PrivacySection(title: String, content: String) {
    Text(
        text = title,
        color = GoldYellow,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = CasinoFont
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = content,
        color = Color.White.copy(alpha = 0.8f),
        fontSize = 14.sp,
        fontFamily = CasinoFont,
        lineHeight = 20.sp
    )
    Spacer(modifier = Modifier.height(16.dp))
}