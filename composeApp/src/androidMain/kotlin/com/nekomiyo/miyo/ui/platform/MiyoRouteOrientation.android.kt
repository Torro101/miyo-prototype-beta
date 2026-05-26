package com.nekomiyo.miyo.ui.platform

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.nekomiyo.miyo.ui.state.MiyoRoute

@Composable
actual fun MiyoRouteOrientation(route: MiyoRoute) {
    val context = LocalContext.current

    DisposableEffect(context, route) {
        val activity = context.findActivity()
        val previousOrientation = activity?.requestedOrientation
        activity?.requestedOrientation = when (route) {
            MiyoRoute.Hub -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            MiyoRoute.Editor -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        onDispose {
            if (previousOrientation != null) {
                activity?.requestedOrientation = previousOrientation
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
