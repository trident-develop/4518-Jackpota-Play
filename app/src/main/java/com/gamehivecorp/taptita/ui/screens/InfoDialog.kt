package com.gamehivecorp.taptita.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gamehivecorp.taptita.data.GameTheme
import com.gamehivecorp.taptita.ui.components.CasinoFont
import com.gamehivecorp.taptita.ui.components.MenuButton
import com.gamehivecorp.taptita.ui.theme.DarkBackground
import com.gamehivecorp.taptita.ui.theme.GoldYellow

@Composable
fun InfoDialog(
    theme: GameTheme,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(DarkBackground)
                .border(2.dp, GoldYellow, RoundedCornerShape(16.dp))
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${theme.displayName} - PAYTABLE",
                color = GoldYellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Win lines info
            Text(
                text = "WIN LINES",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )

            Spacer(modifier = Modifier.height(8.dp))

            val lineNames = if (theme.columns == 5) {
                listOf("Top Row", "Middle Row", "Bottom Row", "V Shape", "^ Shape")
            } else {
                listOf("Top Row", "Middle Row", "Bottom Row", "Diagonal \\", "Diagonal /")
            }
            lineNames.forEach { name ->
                Text(
                    text = name,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontFamily = CasinoFont
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SYMBOL VALUES",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CasinoFont
            )

            Spacer(modifier = Modifier.height(8.dp))

            val totalSymbols = theme.elementResIds.size
            theme.elementResIds.forEachIndexed { index, resId ->
                val multiplier = when {
                    index >= totalSymbols - 2 -> "10x"
                    index >= totalSymbols - 4 -> "5x"
                    index >= totalSymbols - 6 -> "3x"
                    else -> "1.5x"
                }
                val tier = when {
                    index >= totalSymbols - 2 -> "ROYAL"
                    index >= totalSymbols - 4 -> "GRAND"
                    index >= totalSymbols - 6 -> "MAJOR"
                    else -> "MINI"
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = tier,
                        color = when (tier) {
                            "ROYAL" -> GoldYellow
                            "GRAND" -> Color(0xFFFF6B6B)
                            "MAJOR" -> Color(0xFF69B4FF)
                            else -> Color.White.copy(alpha = 0.7f)
                        },
                        fontSize = 14.sp,
                        fontFamily = CasinoFont,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(
                        text = multiplier,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = CasinoFont
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Match 3${if (theme.columns == 5) "-5" else ""} symbols to win!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontFamily = CasinoFont
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "CLOSE",
                buttonRes = theme.buttonRes,
                fontSize = 18.sp,
                onClick = onDismiss
            )
        }
    }
}
