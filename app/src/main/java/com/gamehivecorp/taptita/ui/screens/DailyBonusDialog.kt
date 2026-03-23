package com.gamehivecorp.taptita.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gamehivecorp.taptita.ui.components.CasinoFont
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.theme.BarPurple
import com.gamehivecorp.taptita.ui.theme.DarkBackground
import com.gamehivecorp.taptita.ui.theme.GoldYellow

@Composable
fun DailyBonusDialog(
    currentDay: Int,
    canClaim: Boolean,
    getDailyBonusAmount: (Int) -> Long,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(DarkBackground)
                .border(2.dp, GoldYellow, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DAILY BONUS",
                color = GoldYellow,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (day in 1..5) {
                    DayBox(
                        day = day,
                        isCollected = day <= currentDay,
                        isCurrent = day == currentDay + 1,
                        amount = getDailyBonusAmount(day)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (canClaim) {
                MenuButton(
                    text = "CLAIM",
                    fontSize = 20.sp,
                    onClick = {
                        onClaim()
                        onDismiss()
                    }
                )
            } else {
                Text(
                    text = "Come back tomorrow!",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontFamily = CasinoFont
                )
            }
        }
    }
}

@Composable
private fun DayBox(day: Int, isCollected: Boolean, isCurrent: Boolean, amount: Long) {
    val bgColor = when {
        isCollected -> BarPurple
        isCurrent -> GoldYellow.copy(alpha = 0.3f)
        else -> Color.White.copy(alpha = 0.1f)
    }
    val borderColor = if (isCurrent) GoldYellow else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(55.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(if (isCurrent) 2.dp else 0.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Text(
            text = "Day $day",
            color = Color.White,
            fontSize = 10.sp,
            fontFamily = CasinoFont
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${amount / 1000}K",
            color = GoldYellow,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = CasinoFont,
            textAlign = TextAlign.Center
        )
        if (isCollected) {
            Text(text = "✓", color = Color.Green, fontSize = 12.sp)
        }
    }
}
