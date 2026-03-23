package com.gamehivecorp.taptita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamehivecorp.taptita.R

val CasinoFont = FontFamily(Font(R.font.font))

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    text: String,
    buttonRes: Int = R.drawable.button_purple,
    enabled: Boolean = true,
    fontSize: TextUnit = 24.sp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(200.dp)
            .height(60.dp)
            .pressableWithCooldown(
                cooldownMillis = 500L,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(id = buttonRes),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = if (enabled) 1f else 0.5f,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = CasinoFont,
            textAlign = TextAlign.Center
        )
    }
}
