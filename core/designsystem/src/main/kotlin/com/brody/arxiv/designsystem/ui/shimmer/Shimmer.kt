package com.brody.arxiv.designsystem.ui.shimmer

import androidx.annotation.FloatRange
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.brody.arxiv.designsystem.dimens.Dimens
import com.google.accompanist.placeholder.PlaceholderDefaults
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import kotlin.math.max

fun Modifier.shimmerEffect(showShimmer: Boolean = true, targetValue: Float = 1000f): Modifier =
    composed {
        val brush: Brush = if (showShimmer) {
            val color = Color(0xFFDADADA)
            val shimmerColors = listOf(
                color.copy(alpha = 0.6f),
                color.copy(alpha = 0.2f),
                color.copy(alpha = 0.6f),
            )

            val transition = rememberInfiniteTransition(label = "")
            val translateAnimation = transition.animateFloat(
                initialValue = 0f, targetValue = targetValue, animationSpec = infiniteRepeatable(
                    animation = tween(800), repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            Brush.linearGradient(
                colors = shimmerColors,
                start = Offset.Zero,
                end = Offset(x = translateAnimation.value, y = translateAnimation.value)
            )
        } else {
            Brush.linearGradient(
                colors = listOf(Color.Transparent, Color.Transparent),
                start = Offset.Zero,
                end = Offset.Zero
            )
        }
        background(
            brush = brush
        )
    }