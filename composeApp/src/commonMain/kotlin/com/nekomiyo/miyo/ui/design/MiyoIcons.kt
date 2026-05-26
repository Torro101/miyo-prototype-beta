package com.nekomiyo.miyo.ui.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object MiyoIcons {
    val LogoMark: ImageVector by lazy {
        Builder(
            name = "MiyoLogoMark",
            defaultWidth = 48.dp,
            defaultHeight = 48.dp,
            viewportWidth = 48f,
            viewportHeight = 48f
        ).apply {
            path(fill = SolidColor(Color(0xFFFF6F9D))) {
                moveTo(23.2f, 5.4f)
                curveTo(13.1f, 6.3f, 6.5f, 14.8f, 8.1f, 25.4f)
                curveTo(9.5f, 34.5f, 16.4f, 41.0f, 24.0f, 42.6f)
                curveTo(22.6f, 32.6f, 22.3f, 15.5f, 23.2f, 5.4f)
                close()
            }
            path(fill = SolidColor(Color(0xFF4BC3C7))) {
                moveTo(24.8f, 5.4f)
                curveTo(34.9f, 6.3f, 41.5f, 14.8f, 39.9f, 25.4f)
                curveTo(38.5f, 34.5f, 31.6f, 41.0f, 24.0f, 42.6f)
                curveTo(25.4f, 32.6f, 25.7f, 15.5f, 24.8f, 5.4f)
                close()
            }
            path(fill = SolidColor(Color(0xFF8B7CF6))) {
                moveTo(24.0f, 10.0f)
                lineTo(31.4f, 19.0f)
                lineTo(24.0f, 36.0f)
                lineTo(16.6f, 19.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF5F1EA))) {
                moveTo(19.2f, 20.4f)
                curveTo(20.5f, 19.1f, 22.1f, 18.5f, 24.0f, 18.5f)
                curveTo(25.9f, 18.5f, 27.5f, 19.1f, 28.8f, 20.4f)
                lineTo(27.0f, 23.2f)
                curveTo(26.1f, 22.5f, 25.1f, 22.1f, 24.0f, 22.1f)
                curveTo(22.9f, 22.1f, 21.9f, 22.5f, 21.0f, 23.2f)
                close()
            }
        }.build()
    }

    val Add: ImageVector by lazy { lineIcon("Add") { moveTo(12f, 5f); lineTo(12f, 19f); moveTo(5f, 12f); lineTo(19f, 12f) } }
    val Assets: ImageVector by lazy { lineIcon("Assets") { rect(4f, 5f, 16f, 14f); moveTo(7f, 16f); lineTo(11f, 12f); lineTo(14f, 15f); lineTo(16f, 13f); lineTo(20f, 17f); moveTo(8f, 9f); lineTo(8.1f, 9f) } }
    val Back: ImageVector by lazy { lineIcon("Back") { moveTo(15f, 6f); lineTo(9f, 12f); lineTo(15f, 18f); moveTo(10f, 12f); lineTo(21f, 12f) } }
    val BackgroundAction: ImageVector by lazy { lineIcon("BackgroundAction") { rect(4f, 5f, 16f, 14f); moveTo(5f, 17f); lineTo(10f, 11f); lineTo(14f, 15f); lineTo(17f, 12f); lineTo(20f, 16f); moveTo(15f, 8f); lineTo(15.1f, 8f) } }
    val CharacterAction: ImageVector by lazy { lineIcon("CharacterAction") { circle(12f, 8f, 3f); moveTo(6f, 20f); curveTo(7.1f, 15.7f, 9.1f, 14f, 12f, 14f); curveTo(14.9f, 14f, 16.9f, 15.7f, 18f, 20f) } }
    val ChoiceAction: ImageVector by lazy { lineIcon("ChoiceAction") { moveTo(5f, 6f); lineTo(10f, 6f); lineTo(14f, 10f); lineTo(19f, 10f); moveTo(10f, 6f); lineTo(14f, 14f); lineTo(19f, 14f); moveTo(18f, 8f); lineTo(20f, 10f); lineTo(18f, 12f); moveTo(18f, 12f); lineTo(20f, 14f); lineTo(18f, 16f) } }
    val CodeMode: ImageVector by lazy { lineIcon("CodeMode") { moveTo(9f, 7f); lineTo(4f, 12f); lineTo(9f, 17f); moveTo(15f, 7f); lineTo(20f, 12f); lineTo(15f, 17f); moveTo(13f, 5f); lineTo(11f, 19f) } }
    val EmptyProject: ImageVector by lazy { lineIcon("EmptyProject") { moveTo(7f, 3f); lineTo(14f, 3f); lineTo(19f, 8f); lineTo(19f, 21f); lineTo(7f, 21f); close(); moveTo(14f, 3f); lineTo(14f, 8f); lineTo(19f, 8f); moveTo(10f, 13f); lineTo(16f, 13f); moveTo(13f, 10f); lineTo(13f, 16f) } }
    val Export: ImageVector by lazy { lineIcon("Export") { moveTo(12f, 15f); lineTo(12f, 4f); moveTo(8f, 8f); lineTo(12f, 4f); lineTo(16f, 8f); moveTo(5f, 14f); lineTo(5f, 20f); lineTo(19f, 20f); lineTo(19f, 14f) } }
    val Hub: ImageVector by lazy { lineIcon("Hub") { moveTo(4f, 11f); lineTo(12f, 4f); lineTo(20f, 11f); moveTo(6f, 10f); lineTo(6f, 20f); lineTo(18f, 20f); lineTo(18f, 10f); moveTo(10f, 20f); lineTo(10f, 14f); lineTo(14f, 14f); lineTo(14f, 20f) } }
    val Import: ImageVector by lazy { lineIcon("Import") { moveTo(12f, 4f); lineTo(12f, 15f); moveTo(8f, 11f); lineTo(12f, 15f); lineTo(16f, 11f); moveTo(5f, 14f); lineTo(5f, 20f); lineTo(19f, 20f); lineTo(19f, 14f) } }
    val Inspector: ImageVector by lazy { lineIcon("Inspector") { moveTo(5f, 7f); lineTo(19f, 7f); moveTo(8f, 7f); lineTo(8f, 7.1f); moveTo(5f, 12f); lineTo(19f, 12f); moveTo(14f, 12f); lineTo(14f, 12.1f); moveTo(5f, 17f); lineTo(19f, 17f); moveTo(11f, 17f); lineTo(11f, 17.1f) } }
    val Library: ImageVector by lazy { lineIcon("Library") { rect(5f, 5f, 14f, 15f); moveTo(8f, 3f); lineTo(16f, 3f); lineTo(19f, 5f); moveTo(8f, 10f); lineTo(16f, 10f); moveTo(8f, 14f); lineTo(14f, 14f) } }
    val NodeMode: ImageVector by lazy { lineIcon("NodeMode") { circle(6f, 7f, 2.2f); circle(18f, 7f, 2.2f); circle(12f, 17f, 2.2f); moveTo(8f, 8f); lineTo(11f, 15f); moveTo(16f, 8f); lineTo(13f, 15f); moveTo(8.2f, 7f); lineTo(15.8f, 7f) } }
    val Preview: ImageVector by lazy { lineIcon("Preview") { rect(4f, 5f, 16f, 12f); moveTo(10f, 9f); lineTo(15f, 11f); lineTo(10f, 14f); close(); moveTo(8f, 21f); lineTo(16f, 21f); moveTo(12f, 17f); lineTo(12f, 21f) } }
    val Settings: ImageVector by lazy { lineIcon("Settings") { moveTo(5f, 7f); lineTo(19f, 7f); circle(9f, 7f, 2f); moveTo(5f, 12f); lineTo(19f, 12f); circle(15f, 12f, 2f); moveTo(5f, 17f); lineTo(19f, 17f); circle(11f, 17f, 2f) } }
    val SimpleMode: ImageVector by lazy { lineIcon("SimpleMode") { moveTo(5f, 6f); lineTo(19f, 6f); moveTo(5f, 12f); lineTo(19f, 12f); moveTo(5f, 18f); lineTo(19f, 18f); circle(7f, 6f, 1.5f); circle(12f, 12f, 1.5f); circle(17f, 18f, 1.5f) } }
    val SoundAction: ImageVector by lazy { lineIcon("SoundAction") { moveTo(8f, 17f); curveTo(6.3f, 17f, 5f, 15.9f, 5f, 14.5f); curveTo(5f, 13.1f, 6.3f, 12f, 8f, 12f); curveTo(9.7f, 12f, 11f, 13.1f, 11f, 14.5f); lineTo(11f, 6f); lineTo(18f, 4.5f); lineTo(18f, 8f); lineTo(11f, 9.5f) } }
    val TextAction: ImageVector by lazy { lineIcon("TextAction") { moveTo(5f, 6f); lineTo(19f, 6f); moveTo(12f, 6f); lineTo(12f, 18f); moveTo(9f, 18f); lineTo(15f, 18f) } }
    val Timeline: ImageVector by lazy { lineIcon("Timeline") { moveTo(6f, 4f); lineTo(6f, 20f); rect(9f, 5f, 10f, 4f); rect(9f, 12f, 10f, 4f); moveTo(6f, 7f); lineTo(9f, 7f); moveTo(6f, 14f); lineTo(9f, 14f) } }
    val Warning: ImageVector by lazy { lineIcon("Warning") { moveTo(12f, 4f); lineTo(21f, 20f); lineTo(3f, 20f); close(); moveTo(12f, 10f); lineTo(12f, 14f); moveTo(12f, 17f); lineTo(12.1f, 17f) } }
    val Workshop: ImageVector by lazy { lineIcon("Workshop") { rect(4f, 8f, 16f, 11f); moveTo(7f, 8f); lineTo(7f, 5f); lineTo(11f, 3f); lineTo(15f, 5f); lineTo(15f, 8f); moveTo(8f, 13f); lineTo(16f, 13f); moveTo(12f, 10f); lineTo(12f, 17f) } }
}

private fun lineIcon(name: String, block: PathBuilder.() -> Unit): ImageVector =
    Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            block()
        }
    }.build()

private fun PathBuilder.rect(x: Float, y: Float, width: Float, height: Float) {
    moveTo(x, y)
    lineTo(x + width, y)
    lineTo(x + width, y + height)
    lineTo(x, y + height)
    close()
}

private fun PathBuilder.circle(cx: Float, cy: Float, radius: Float) {
    moveTo(cx + radius, cy)
    curveTo(cx + radius, cy + radius * 0.55f, cx + radius * 0.55f, cy + radius, cx, cy + radius)
    curveTo(cx - radius * 0.55f, cy + radius, cx - radius, cy + radius * 0.55f, cx - radius, cy)
    curveTo(cx - radius, cy - radius * 0.55f, cx - radius * 0.55f, cy - radius, cx, cy - radius)
    curveTo(cx + radius * 0.55f, cy - radius, cx + radius, cy - radius * 0.55f, cx + radius, cy)
    close()
}
