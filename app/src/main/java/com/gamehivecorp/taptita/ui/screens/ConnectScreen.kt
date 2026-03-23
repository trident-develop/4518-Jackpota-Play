package com.gamehivecorp.taptita.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.navigation.Routes
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.theme.CasinoFontFamily

@Composable
fun ConnectScreen(navController: NavController) {

    var showButton by remember { mutableStateOf(true) }
    var showConnecting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    BackHandler(enabled = true) {}

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.royal_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .aspectRatio(0.6f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LOST CONNECTION",
                        fontSize = 30.sp,
                        fontFamily = CasinoFontFamily,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Image(
                        painter = painterResource(R.drawable.lost_network),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Check your\ninternet connection",
                        fontSize = 16.sp,
                        fontFamily = CasinoFontFamily,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    when {
                        showConnecting -> {
                            AnimatedVisibility(
                                visible = showConnecting,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "Connecting...",
                                    fontSize = 22.sp,
                                    fontFamily = CasinoFontFamily,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        showButton -> {
                            MenuButton(
                                text = "Try Again",
                                enabled = showButton,
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .aspectRatio(3.5f)
                            ) {
                                showButton = false
                                showConnecting = true

                                if (context.isFlowersConnected()) {
                                    navController.navigate(Routes.LOADING) {
                                        popUpTo(Routes.CONNECT) { inclusive = true }
                                    }
                                } else {
                                    showButton = true
                                    showConnecting = false
                                }
                            }
                        }

                        else -> {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ServiceCast")
fun Context.isFlowersConnected(): Boolean {
    val ballConnectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeBallNetwork = ballConnectivityManager.activeNetwork
    val ballCapabilities = ballConnectivityManager.getNetworkCapabilities(activeBallNetwork)

    return ballCapabilities?.run {
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    } == true
}