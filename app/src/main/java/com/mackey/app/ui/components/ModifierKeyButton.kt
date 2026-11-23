package com.mackey.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.ui.theme.MacDarkKey
import com.mackey.app.ui.theme.MacDarkKeyBorder
import com.mackey.app.ui.theme.MacLightKey
import com.mackey.app.ui.theme.MacLightKeyBorder

/**
 * Modifier key component for Mac:
 * Supports Command (⌘), Option (⌥), Control (⌃), Shift (⇧), Fn
 *
 * Visual indicator with RGB Aura:
 * - Inactive: standard key background with subtle RGB perimeter
 * - Latched (single tap): Glowing neon border + active pulse (applies to next keystroke)
 * - Locked (double tap): Solid radiant RGB fill with white text & glow aura
 */
@Composable
fun ModifierKeyButton(
    label: String,
    symbol: String,
    isLatched: Boolean,
    isLocked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    isDark: Boolean = true,
    rgbEnabled: Boolean = true,
    rgbGlowColor: Color = Color(0xFF00F5FF)
) {
    val scale by animateFloatAsState(targetValue = if (isLocked) 0.94f else 1.0f, label = "mod_scale")

    val glowElevation by animateDpAsState(
        targetValue = if ((isLocked || isLatched) && rgbEnabled) 8.dp else 2.dp,
        label = "mod_glow_elevation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isLocked -> rgbGlowColor
            isLatched -> rgbGlowColor.copy(alpha = 0.28f)
            rgbEnabled -> if (isDark) Color(0xFF1E1E26) else Color(0xFFF2F2F7)
            else -> if (isDark) MacDarkKey else MacLightKey
        },
        label = "mod_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isLocked -> rgbGlowColor
            isLatched -> rgbGlowColor
            rgbEnabled -> rgbGlowColor.copy(alpha = 0.5f)
            else -> if (isDark) MacDarkKeyBorder else MacLightKeyBorder
        },
        label = "mod_border"
    )

    val textColor = when {
        isLocked -> Color.White
        isLatched -> if (rgbEnabled) rgbGlowColor else MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .scale(scale)
            .shadow(
                elevation = glowElevation,
                shape = RoundedCornerShape(8.dp),
                ambientColor = if (rgbEnabled) rgbGlowColor else Color.Black,
                spotColor = if (rgbEnabled) rgbGlowColor else Color.Black
            )
            .clip(RoundedCornerShape(8.dp))
            .border(if (isLatched || isLocked) 1.8.dp else 0.8.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .pointerInput(Unit) {
                // Instant zero-latency tap trigger
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    onToggle()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(vertical = 4.dp)
        ) {
            Text(
                text = symbol,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                lineHeight = 15.sp
            )
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLocked) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 10.sp
            )
        }

        // Dot indicator for latched / locked state
        if (isLatched || isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(if (isLocked) Color.White else rgbGlowColor)
            )
        }
    }
}
