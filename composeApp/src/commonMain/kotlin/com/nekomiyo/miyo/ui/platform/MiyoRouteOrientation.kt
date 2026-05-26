package com.nekomiyo.miyo.ui.platform

import androidx.compose.runtime.Composable
import com.nekomiyo.miyo.ui.state.MiyoRoute

@Composable
expect fun MiyoRouteOrientation(route: MiyoRoute)
