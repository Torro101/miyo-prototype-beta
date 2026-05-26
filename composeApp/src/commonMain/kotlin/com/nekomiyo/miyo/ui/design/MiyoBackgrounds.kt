package com.nekomiyo.miyo.ui.design

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.nekomiyo.miyo.ui.theme.MiyoColors

fun Modifier.miyoPatternBackground(
    baseColor: Color = MiyoColors.Ink,
    lineColor: Color = MiyoColors.Pattern,
    accentColor: Color = MiyoColors.Petal.copy(alpha = 0.18f)
): Modifier = drawBehind {
    drawRect(baseColor)

    val grid = 32.dp.toPx()
    val dotRadius = 1.1.dp.toPx()
    var y = grid / 2f
    while (y < size.height) {
        var x = grid / 2f
        while (x < size.width) {
            drawCircle(
                color = lineColor.copy(alpha = 0.45f),
                radius = dotRadius,
                center = Offset(x, y)
            )
            x += grid
        }
        y += grid
    }

    val band = 96.dp.toPx()
    val strokeWidth = 1.dp.toPx()
    var offset = -size.height
    while (offset < size.width) {
        drawLine(
            color = lineColor.copy(alpha = 0.28f),
            start = Offset(offset, 0f),
            end = Offset(offset + size.height, size.height),
            strokeWidth = strokeWidth
        )
        offset += band
    }

    val motif = 124.dp.toPx()
    val petalSize = Size(18.dp.toPx(), 28.dp.toPx())
    var motifY = 40.dp.toPx()
    while (motifY < size.height) {
        var motifX = 24.dp.toPx()
        while (motifX < size.width) {
            drawOval(
                color = accentColor,
                topLeft = Offset(motifX, motifY),
                size = petalSize,
                style = Stroke(width = strokeWidth)
            )
            drawOval(
                color = MiyoColors.Lagoon.copy(alpha = 0.12f),
                topLeft = Offset(motifX + 12.dp.toPx(), motifY + 8.dp.toPx()),
                size = Size(petalSize.width, petalSize.height),
                style = Stroke(width = strokeWidth)
            )
            motifX += motif
        }
        motifY += motif
    }
}
