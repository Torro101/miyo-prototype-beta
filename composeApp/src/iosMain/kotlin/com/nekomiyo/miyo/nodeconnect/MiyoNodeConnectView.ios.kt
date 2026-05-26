package com.nekomiyo.miyo.nodeconnect

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * iOS placeholder for the first foundation pass.
 *
 * The bundled local assets live under iosMain/resources/node-connect. The next iOS pass should
 * replace this Box with a WKWebView backed by those files, register a script handler named
 * `miyoNodeBridge`, and allow only file/about navigation.
 */
@Composable
actual fun MiyoNodeConnectView(
    bridgeHost: MiyoNodeBridgeHost,
    hostSnapshotJson: String,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Miyo Node Connect iOS WebView placeholder")
    }
}
