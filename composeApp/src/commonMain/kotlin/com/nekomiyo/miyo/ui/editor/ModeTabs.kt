package com.nekomiyo.miyo.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nekomiyo.miyo.ui.design.MiyoIcons
import com.nekomiyo.miyo.ui.state.EditorMode
import com.nekomiyo.miyo.ui.theme.MiyoColors
import com.nekomiyo.miyo.ui.theme.MiyoRadius
import com.nekomiyo.miyo.ui.theme.MiyoSpacing
import com.nekomiyo.miyo.ui.theme.MiyoStroke
import androidx.compose.ui.unit.dp

@Composable
fun ModeTabs(
    selectedMode: EditorMode,
    onModeSelected: (EditorMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MiyoRadius.md))
            .background(MiyoColors.InkSoft)
            .padding(MiyoSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(MiyoSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditorMode.entries.forEach { mode ->
            val selected = mode == selectedMode
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(MiyoRadius.sm))
                    .background(if (selected) MiyoColors.Petal.copy(alpha = 0.16f) else Color.Transparent)
                    .border(
                        width = MiyoStroke.hairline,
                        color = if (selected) MiyoColors.Petal.copy(alpha = 0.46f) else Color.Transparent,
                        shape = RoundedCornerShape(MiyoRadius.sm)
                    )
                    .clickable { onModeSelected(mode) }
                    .padding(horizontal = MiyoSpacing.md, vertical = MiyoSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val color = if (selected) MiyoColors.Petal else MiyoColors.TextSecondary
                Icon(
                    imageVector = mode.icon,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp),
                    tint = color
                )
                Spacer(Modifier.width(MiyoSpacing.xs))
                Text(
                    text = mode.label,
                    color = color,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

private val EditorMode.icon: ImageVector
    get() = when (this) {
        EditorMode.Edit -> MiyoIcons.SimpleMode
        EditorMode.Preview -> MiyoIcons.Preview
    }
