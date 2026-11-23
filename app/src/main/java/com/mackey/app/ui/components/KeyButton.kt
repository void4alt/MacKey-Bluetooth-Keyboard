package com.mackey.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.model.KeyItem
import com.mackey.app.model.KeyType
import com.mackey.app.ui.theme.MacDarkKey
import com.mackey.app.ui.theme.MacDarkKeyBorder
import com.mackey.app.ui.theme.MacLightKey
import com.mackey.app.ui.theme.MacLightKeyBorder

@Composable
fun KeyButton(
    key: KeyItem,
    onDown: (KeyItem) -> Unit,
    onUp: (KeyItem) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    isDark: Boolean = true,
    rgbEnabled: Boolean = true,
    activeGlowColor: Color = Color(0xFF00F5FF)
) {
    var isPressed by remember { mutableStateOf(false) }

    // Instant touch feedback with scale depression
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing),
        label = "key_scale"
    )

    // Reactive RGB glow animations
    val glowElevation by animateDpAsState(
        targetValue = if (isPressed && rgbEnabled) 10.dp else 2.dp,
        animationSpec = tween(durationMillis = 80),
        label = "glow_elevation"
    )

    val keyBorderColor by animateColorAsState(
        targetValue = when {
            isPressed && rgbEnabled -> activeGlowColor
            isPressed -> Color.White
            rgbEnabled -> activeGlowColor.copy(alpha = 0.40f) // Subtle RGB underglow
            isDark -> MacDarkKeyBorder
            else -> MacLightKeyBorder
        },
        animationSpec = tween(durationMillis = if (isPressed) 40 else 220),
        label = "key_border"
    )

    val keyBorderWidth by animateDpAsState(
        targetValue = if (isPressed && rgbEnabled) 1.8.dp else 0.8.dp,
        label = "border_width"
    )

    val baseKeyColor = if (isDark) {
        if (key.type == KeyType.ACTION || key.type == KeyType.SPECIAL) Color(0xFF202026) else MacDarkKey
    } else {
        if (key.type == KeyType.ACTION || key.type == KeyType.SPECIAL) Color(0xFFE5E5EA) else MacLightKey
    }

    val backgroundModifier = if (isPressed && rgbEnabled) {
        Modifier.background(
            Brush.radialGradient(
                colors = listOf(
                    activeGlowColor.copy(alpha = 0.55f),
                    activeGlowColor.copy(alpha = 0.22f),
                    baseKeyColor
                )
            )
        )
    } else if (isPressed) {
        Modifier.background(if (isDark) Color(0xFF383842) else Color(0xFFD1D1D6))
    } else {
        Modifier.background(baseKeyColor)
    }

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .scale(scale)
            .shadow(
                elevation = glowElevation,
                shape = RoundedCornerShape(8.dp),
                ambientColor = if (rgbEnabled) activeGlowColor else Color.Black,
                spotColor = if (rgbEnabled) activeGlowColor else Color.Black
            )
            .clip(RoundedCornerShape(8.dp))
            .border(keyBorderWidth, keyBorderColor, RoundedCornerShape(8.dp))
            .then(backgroundModifier)
            .pointerInput(key) {
                // High-performance pointer tracking: captures exact down & up without cancellation
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    isPressed = true
                    onDown(key)

                    val pointerId = down.id
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.find { it.id == pointerId }
                        if (change == null || !change.pressed) {
                            break
                        }
                        change.consume()
                    }
                    isPressed = false
                    onUp(key)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (key.subLabel != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(top = 2.dp, bottom = 2.dp)
            ) {
                Text(
                    text = key.subLabel,
                    fontSize = 9.sp,
                    color = if (isPressed && rgbEnabled) activeGlowColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 10.sp
                )
                Text(
                    text = key.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPressed) Color.White else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 14.sp
                )
            }
        } else {
            Text(
                text = key.label,
                fontSize = when {
                    key.label.length > 4 -> 10.sp
                    key.label.length > 2 -> 11.sp
                    else -> 14.sp
                },
                fontWeight = FontWeight.Bold,
                color = if (isPressed) Color.White else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
