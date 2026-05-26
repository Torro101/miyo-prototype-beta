package com.nekomiyo.miyo.ui.design

import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.nekomiyo.miyo.ui.theme.MiyoColors

@Suppress("UNUSED_PARAMETER")
fun Modifier.miyoPatternBackground(
    baseColor: Color = MiyoColors.Ink,
    lineColor: Color = MiyoColors.Pattern,
    accentColor: Color = MiyoColors.Petal.copy(alpha = 0.18f)
): Modifier = background(baseColor)
