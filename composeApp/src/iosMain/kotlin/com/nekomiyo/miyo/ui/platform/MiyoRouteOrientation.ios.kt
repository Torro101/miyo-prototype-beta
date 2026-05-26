package com.nekomiyo.miyo.ui.platform

import androidx.compose.runtime.Composable
import com.nekomiyo.miyo.ui.state.MiyoRoute

@Composable
actual fun MiyoRouteOrientation(route: MiyoRoute) {
    // iOS orientation is controlled by the host app target. The editor shell is still landscape-first.
}
