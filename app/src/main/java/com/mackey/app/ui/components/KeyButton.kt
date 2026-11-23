package com.mackey.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import com.mackey.app.ui.theme.MacDarkKeyPressed
import com.mackey.app.ui.theme.MacLightKey
import com.mackey.app.ui.theme.MacLightKeyBorder
import com.mackey.app.ui.theme.MacLightKeyPressed

@Composable
fun KeyButton(
    key: KeyItem,
    onDown: (KeyItem) -> Unit,
    onUp: (KeyItem) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    isDark: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1.0f, label = "press_scale")

    val baseKeyColor = if (isDark) {
        if (key.type == KeyType.ACTION || key.type == KeyType.SPECIAL) Color(0xFF28282E) else MacDarkKey
    } else {
        if (key.type == KeyType.ACTION || key.type == KeyType.SPECIAL) Color(0xFFE5E5EA) else MacLightKey
    }

    val pressedColor = if (isDark) MacDarkKeyPressed else MacLightKeyPressed
    val borderColor = if (isDark) MacDarkKeyBorder else MacLightKeyBorder

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .scale(scale)
            .shadow(if (isPressed) 1.dp else 2.dp, RoundedCornerShape(7.dp))
            .clip(RoundedCornerShape(7.dp))
            .border(0.5.dp, borderColor, RoundedCornerShape(7.dp))
            .background(if (isPressed) pressedColor else baseKeyColor)
            .pointerInput(key) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    onDown(key)
                    waitForUpOrCancellation()
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 10.sp
                )
                Text(
                    text = key.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
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
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

