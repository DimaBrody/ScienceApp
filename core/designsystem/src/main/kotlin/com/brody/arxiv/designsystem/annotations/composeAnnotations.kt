package com.brody.arxiv.designsystem.annotations

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Preview(name = "landscape", device = "spec:shape=Normal,width=640,height=360,unit=dp,dpi=480")
@Preview(name = "foldable", device = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480")
@Preview(name = "tablet", device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480")
annotation class DevicePreviews

@Preview(
    name = "DefaultPreview",
    widthDp = 320,
    heightDp = 240,
    showBackground = true,
    backgroundColor = 0xFFFFFBFF,
)
annotation class DefaultPreview


@Preview(
    name = "DefaultPreview",
    widthDp = 320,
    heightDp = 240,
    showBackground = true,
    backgroundColor = 0xFFFFF4F3,
)
annotation class SurfacePreview