package com.gamehivecorp.taptita.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gamehivecorp.taptita.data.GameTheme
import com.gamehivecorp.taptita.data.getRewardRes
import com.gamehivecorp.taptita.data.getRewardTier
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.theme.GoldYellow
import com.gamehivecorp.taptita.ui.theme.Typography

@Composable
fun WinDialog(
    theme: GameTheme,
    totalWin: Long,
    bet: Long,
    onDismiss: () -> Unit
) {
    val tier = getRewardTier(totalWin, bet)
    val rewardBgRes = theme.getRewardRes(tier)

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.5f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "winScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "winGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coinGlow"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            // Just "WIN" text on top
            Text(
                text = "GOOD JOB!",
                color = GoldYellow,
                fontSize = 56.sp,
                style = Typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reward background ONLY behind coin amount
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = rewardBgRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth(0.42f)
                        .aspectRatio(2.5f)
                )

                Text(
                    text = formatCoins(totalWin),
                    color = GoldYellow.copy(alpha = glowAlpha),
                    style = Typography.titleMedium,
                    modifier = Modifier.padding(top = 28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Collect button below
            MenuButton(
                text = "COLLECT",
                fontSize = 22.sp,
                buttonRes = theme.buttonRes,
                onClick = onDismiss
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = theme.winText),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}
