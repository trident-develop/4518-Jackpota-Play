package com.gamehivecorp.taptita.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.gamehivecorp.taptita.R
import com.gamehivecorp.taptita.ui.components.CasinoFont

val CasinoFontFamily = FontFamily(Font(R.font.font))

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = CasinoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CasinoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CasinoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = CasinoFont,
        textAlign = TextAlign.Center,
        shadow = Shadow(
            color = Color(0x80000000),
            offset = Offset(3f, 3f),
            blurRadius = 4f
        )
    )
)
