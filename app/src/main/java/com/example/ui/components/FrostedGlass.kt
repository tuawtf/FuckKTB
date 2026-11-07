package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

/**
 * CaffeineHealth benzeri cam (frosted glass) efekti konteyneri.
 * Blur kapalıysa (hazeState == null) veya Android sürümü API 31 altında ise
 * sistem çökmeden otomatik olarak düz primaryContainer arka planına düşer.
 */
@Composable
fun Modifier.frostedContainer(
    hazeState: HazeState?,
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    tintAlpha: Float = 0.72f,
    blurRadius: Dp = 24.dp,
    noiseFactor: Float = 0.08f
): Modifier {
    val canBlur = hazeState != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    return this
        .clip(shape)
        .then(
            if (canBlur) {
                Modifier.hazeEffect(state = hazeState!!) {
                    this.blurRadius = blurRadius
                    this.noiseFactor = noiseFactor
                    this.tints = listOf(HazeTint(backgroundColor.copy(alpha = tintAlpha)))
                }
            } else {
                Modifier.background(backgroundColor, shape)
            }
        )
}
