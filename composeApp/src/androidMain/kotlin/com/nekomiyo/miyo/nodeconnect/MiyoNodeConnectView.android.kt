package com.nekomiyo.miyo.nodeconnect

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebViewClient
import java.io.ByteArrayInputStream
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

private const val NodeConnectAssetUrl = "file:///android_asset/node-connect/index.html"

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun MiyoNodeConnectView(
    bridgeHost: MiyoNodeBridgeHost,
    hostSnapshotJson: String,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = false
                settings.domStorageEnabled = false
                settings.databaseEnabled = false
                settings.javaScriptCanOpenWindowsAutomatically = false
                settings.setSupportMultipleWindows(false)

                addJavascriptInterface(AndroidNodeBridge(bridgeHost), "MiyoNodeBridge")
                webViewClient = LocalOnlyWebViewClient(
                    onPageReady = { webView -> webView.sendHostSnapshot(hostSnapshotJson) }
                )
                loadUrl(NodeConnectAssetUrl)
            }
        },
        update = { webView ->
            webView.sendHostSnapshot(hostSnapshotJson)
        },
    )
}

private class AndroidNodeBridge(
    private val bridgeHost: MiyoNodeBridgeHost,
) {
    @JavascriptInterface
    fun postMessage(messageJson: String) {
        bridgeHost.onNodeBridgeMessage(messageJson)
    }
}

private class LocalOnlyWebViewClient(
    private val onPageReady: (WebView) -> Unit,
) : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (view != null) {
            onPageReady(view)
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?,
    ): WebResourceResponse? {
        val url = request?.url
        val allowed = url?.scheme == "file" && url.path?.contains("/android_asset/node-connect/") == true
        return if (allowed) {
            null
        } else {
            WebResourceResponse(
                "text/plain",
                "utf-8",
                ByteArrayInputStream("Blocked non-local Node Connect request".toByteArray()),
            )
        }
    }
}

private fun WebView.sendHostSnapshot(hostSnapshotJson: String) {
    evaluateJavascript(
        "window.MiyoNodeConnect && window.MiyoNodeConnect.receiveHostMessage($hostSnapshotJson);",
        null,
    )
}
