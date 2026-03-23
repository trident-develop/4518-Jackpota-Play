package com.gamehivecorp.taptita.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.pressableWithCooldown(
    cooldownMillis: Long = 1000L,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "pressScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime >= cooldownMillis) {
                        lastClickTime = now
                        onClick()
                    }
                }
            )
        }
}
