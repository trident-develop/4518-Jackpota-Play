package com.gamehivecorp.taptita.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.gamehivecorp.taptita.viewmodel.LeaderboardEntry
import com.gamehivecorp.taptita.viewmodel.MenuViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun LeaderboardScreen(
    viewModel: MenuViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.coins) {
        viewModel.updateLeaderboard(state.coins)
    }

    LeaderboardScreenContent(
        coins = state.coins,
        leaderboard = state.leaderboard,
        onBack = onBack
    )
}

@Composable
fun LeaderboardScreenContent(
    coins: Long,
    leaderboard: List<LeaderboardEntry>,
    onBack: () -> Unit
) {
    val isInPreview = LocalInspectionMode.current

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

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(BarPurpleDark, BarPurple, BarPurpleDark)
                        )
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
                    text = "LEADERBOARD",
                    color = GoldYellow,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CasinoFont
                )

                Spacer(modifier = Modifier.width(100.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "#",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = CasinoFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(30.dp)
                )
                Text(
                    text = "PLAYER",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = CasinoFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "COINS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CasinoFont
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp)
            ) {
                itemsIndexed(leaderboard) { index, entry ->
                    val isUser = entry.name == "YOU"

                    val rankColor = when (index) {
                        0 -> GoldYellow
                        1 -> Color(0xFFC0C0C0)
                        2 -> Color(0xFFCD7F32)
                        else -> Color.White.copy(alpha = 0.7f)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isUser) GoldYellow.copy(alpha = 0.25f)
                                else BarPurpleDark.copy(alpha = 0.45f)
                            )
                            .then(
                                if (isUser) {
                                    Modifier.border(
                                        1.dp,
                                        GoldYellow.copy(alpha = 0.4f),
                                        RoundedCornerShape(8.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = rankColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CasinoFont,
                            modifier = Modifier.width(30.dp)
                        )

                        Text(
                            text = entry.name,
                            color = if (isUser) GoldYellow else Color.White,
                            fontSize = 16.sp,
                            fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = CasinoFont,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = formatCoins(entry.score),
                            color = if (isUser) GoldYellow else Color.White.copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            fontFamily = CasinoFont
                        )
                    }
                }
            }
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}