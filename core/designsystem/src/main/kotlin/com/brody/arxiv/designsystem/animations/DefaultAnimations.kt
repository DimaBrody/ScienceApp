package com.brody.arxiv.designsystem.animations

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val dpDefaultSpring = spring(visibilityThreshold = Dp.VisibilityThreshold)

@Composable
fun animateDpAsStateNoLabel(
    targetValue: Dp,
    animationSpec: AnimationSpec<Dp> = dpDefaultSpring,
    finishedListener: ((Dp) -> Unit)? = null
): State<Dp> {
    return animateDpAsState(
        targetValue = targetValue,
        animationSpec, "Animated", finishedListener
    )
}