package com.nekomiyo.miyo.ui.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nekomiyo.miyo.ui.theme.MiyoColors
import com.nekomiyo.miyo.ui.theme.MiyoRadius
import com.nekomiyo.miyo.ui.theme.MiyoSpacing
import com.nekomiyo.miyo.ui.theme.MiyoStroke

@Composable
fun MiyoPanel(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    containerColor: Color = MiyoColors.Surface,
    contentPadding: PaddingValues = PaddingValues(MiyoSpacing.md),
    content: @Composable () -> Unit
) {
    val outline = if (selected) MiyoColors.Petal else MiyoColors.Outline
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MiyoRadius.lg))
            .background(containerColor)
            .border(
                width = if (selected) MiyoStroke.selected else MiyoStroke.hairline,
                color = outline,
                shape = RoundedCornerShape(MiyoRadius.lg)
            )
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
fun MiyoLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Icon(
        imageVector = MiyoIcons.LogoMark,
        contentDescription = "Miyo",
        modifier = modifier.size(size),
        tint = Color.Unspecified
    )
}

@Composable
fun MiyoIconLabel(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MiyoColors.TextSecondary,
    textColor: Color = MiyoColors.TextSecondary,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = iconTint
        )
        Spacer(Modifier.width(MiyoSpacing.sm))
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
        if (trailing != null) {
            trailing()
        }
    }
}

@Composable
fun MiyoPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = MiyoColors.InkSoft,
    contentColor: Color = MiyoColors.TextSecondary
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(MiyoRadius.pill))
            .background(containerColor)
            .border(MiyoStroke.hairline, MiyoColors.Outline, RoundedCornerShape(MiyoRadius.pill))
            .padding(horizontal = MiyoSpacing.sm, vertical = MiyoSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = contentColor
            )
            Spacer(Modifier.width(MiyoSpacing.xs))
        }
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
