package com.nekomiyo.miyo.ui.design

import androidx.compose.ui.graphics.Color
import com.nekomiyo.miyo.core.model.MiyoColorToken
import com.nekomiyo.miyo.ui.theme.MiyoColors

fun MiyoColorToken.toUiColor(): Color = when (this) {
    MiyoColorToken.Petal -> MiyoColors.Petal
    MiyoColorToken.Lagoon -> MiyoColors.Lagoon
    MiyoColorToken.Mint -> MiyoColors.Mint
    MiyoColorToken.Honey -> MiyoColors.Honey
    MiyoColorToken.Coral -> MiyoColors.Coral
    MiyoColorToken.Wisteria -> MiyoColors.Wisteria
    MiyoColorToken.Surface -> MiyoColors.SurfaceRaised
    MiyoColorToken.White -> Color.White
}
