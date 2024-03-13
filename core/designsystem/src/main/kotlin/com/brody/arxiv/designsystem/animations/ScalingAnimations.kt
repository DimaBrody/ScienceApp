package com.brody.arxiv.designsystem.animations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut


fun scaleIntoContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.INWARDS,
    initialScale: Float = if (direction == ScaleTransitionDirection.OUTWARDS) 1f else 0.99f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(110, delayMillis = 90), initialScale = initialScale
    ) + fadeIn(animationSpec = tween(110, delayMillis = 90))
}

fun scaleOutOfContainer(
    direction: ScaleTransitionDirection = ScaleTransitionDirection.OUTWARDS,
    targetScale: Float = if (direction == ScaleTransitionDirection.INWARDS) 1f else 0.99f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 110, delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}

enum class ScaleTransitionDirection {
    OUTWARDS, INWARDS
}