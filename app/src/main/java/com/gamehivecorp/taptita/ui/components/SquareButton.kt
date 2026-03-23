package com.gamehivecorp.taptita.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun SquareButton(
    modifier: Modifier = Modifier,
    btnRes: Int,
    btnMaxWidth: Float = 0.18f,
    cooldownMillis: Long = 1000L,
    btnEnabled: Boolean = true,
    btnClickable: () -> Unit
) {
    Image(
        painter = painterResource(id = btnRes),
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        alpha = if (btnEnabled) 1f else 0.5f,
        modifier = modifier
            .fillMaxWidth(btnMaxWidth)
            .pressableWithCooldown(
                cooldownMillis = cooldownMillis,
                enabled = btnEnabled,
                onClick = btnClickable
            )
    )
}
