package com.mackey.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.ui.theme.MacDarkKey
import com.mackey.app.ui.theme.MacDarkKeyBorder
import com.mackey.app.ui.theme.MacLightKey
import com.mackey.app.ui.theme.MacLightKeyBorder

/**
 * Modifier key component for Mac:
 * Supports Command (⌘), Option (⌥), Control (⌃), Shift (⇧), Fn
 *
 * Visual indicator:
 * - Inactive: standard key background
 * - Latched (single tap): Blue outline and small indicator dot (applies to next keystroke)
 * - Locked (double tap): Solid Apple Blue background with white text
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
    isDark: Boolean = true
) {
    val scale by animateFloatAsState(targetValue = if (isLocked) 0.96f else 1.0f, label = "mod_scale")

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isLocked -> MacBlueAccent
            isLatched -> if (isDark) Color(0xFF1C2C40) else Color(0xFFD6E9FF)
            else -> if (isDark) MacDarkKey else MacLightKey
        },
        label = "mod_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isLocked -> MacBlueAccent
            isLatched -> MacBlueAccent
            else -> if (isDark) MacDarkKeyBorder else MacLightKeyBorder
        },
        label = "mod_border"
    )

    val textColor = when {
        isLocked -> Color.White
        isLatched -> MacBlueAccent
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .padding(horizontal = 1.5.dp, vertical = 2.dp)
            .height(height)
            .scale(scale)
            .shadow(if (isLocked) 3.dp else 2.dp, RoundedCornerShape(7.dp))
            .clip(RoundedCornerShape(7.dp))
            .border(if (isLatched || isLocked) 1.5.dp else 0.5.dp, borderColor, RoundedCornerShape(7.dp))
            .background(backgroundColor)
            .clickable { onToggle() },
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
                fontWeight = FontWeight.Medium,
                color = if (isLocked) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 10.sp
            )
        }

        // Dot indicator for latched / locked state
        if (isLatched || isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 3.dp, end = 3.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isLocked) Color.White else MacBlueAccent)
            )
        }
    }
}

