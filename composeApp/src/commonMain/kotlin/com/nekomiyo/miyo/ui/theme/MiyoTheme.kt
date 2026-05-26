package com.nekomiyo.miyo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

object MiyoColors {
    val Ink = Color(0xFF0C0F13)
    val InkSoft = Color(0xFF141922)
    val Surface = Color(0xFF1B2029)
    val SurfaceRaised = Color(0xFF252B35)
    val SurfaceHigh = Color(0xFF303744)
    val Petal = Color(0xFFFF6F9D)
    val Wisteria = Color(0xFF8B7CF6)
    val Lagoon = Color(0xFF4BC3C7)
    val Mint = Color(0xFF8FE3B0)
    val Honey = Color(0xFFF0C96A)
    val Coral = Color(0xFFFF8C6A)
    val Danger = Color(0xFFFF5F70)
    val TextPrimary = Color(0xFFF5F1EA)
    val TextSecondary = Color(0xFFC9C5BF)
    val TextMuted = Color(0xFF8C9299)
    val Outline = Color(0xFF3B4350)
    val Pattern = Color(0xFF242B35)
}

object MiyoSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object MiyoRadius {
    val xs = 3.dp
    val sm = 4.dp
    val md = 6.dp
    val lg = 8.dp
    val pill = 999.dp
}

object MiyoStroke {
    val hairline = 1.dp
    val selected = 2.dp
}

private val MiyoDarkColorScheme: ColorScheme = darkColorScheme(
    primary = MiyoColors.Petal,
    onPrimary = MiyoColors.Ink,
    secondary = MiyoColors.Lagoon,
    onSecondary = MiyoColors.Ink,
    tertiary = MiyoColors.Honey,
    onTertiary = MiyoColors.Ink,
    background = MiyoColors.Ink,
    onBackground = MiyoColors.TextPrimary,
    surface = MiyoColors.Surface,
    onSurface = MiyoColors.TextPrimary,
    surfaceVariant = MiyoColors.SurfaceRaised,
    onSurfaceVariant = MiyoColors.TextSecondary,
    outline = MiyoColors.Outline
)

private val MiyoTypography = Typography().run {
    copy(
        displaySmall = displaySmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
        headlineSmall = headlineSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
        labelLarge = labelLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
        labelMedium = labelMedium.copy(letterSpacing = 0.sp),
        bodyMedium = bodyMedium.copy(letterSpacing = 0.sp),
        bodySmall = bodySmall.copy(letterSpacing = 0.sp)
    )
}

@Composable
fun MiyoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MiyoDarkColorScheme else MiyoDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MiyoTypography,
        content = content
    )
}
