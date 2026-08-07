package com.mentorhomeloans.core.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.mentorhomeloans.core.ui.theme.ShimmerBaseDark
import com.mentorhomeloans.core.ui.theme.ShimmerBaseLight
import com.mentorhomeloans.core.ui.theme.ShimmerHighlightDark
import com.mentorhomeloans.core.ui.theme.ShimmerHighlightLight

/**
 * Shimmer effect modifier to provide skeleton loading visualizations on Compose items.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200)
        ),
        label = "shimmer_offset"
    )

    val isDark = isSystemInDarkTheme()
    val base = if (isDark) ShimmerBaseDark else ShimmerBaseLight
    val highlight = if (isDark) ShimmerHighlightDark else ShimmerHighlightLight

    background(
        brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}
