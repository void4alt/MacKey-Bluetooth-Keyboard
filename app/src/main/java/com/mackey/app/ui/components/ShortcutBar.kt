package com.mackey.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.model.ShortcutItem

/**
 * Dedicated Mac Shortcut Toolbar with glowing RGB pill cards.
 * Provides rapid zero-latency one-tap access to common and custom macOS commands.
 */
@Composable
fun ShortcutBar(
    shortcuts: List<ShortcutItem>,
    onShortcutClicked: (ShortcutItem) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    rgbEnabled: Boolean = true,
    accentGlow: Color = Color(0xFF00F5FF)
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        contentPadding = PaddingValues(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(shortcuts, key = { it.id }) { item ->
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .shadow(
                        elevation = if (rgbEnabled) 4.dp else 1.dp,
                        shape = RoundedCornerShape(8.dp),
                        ambientColor = if (rgbEnabled) accentGlow else Color.Black,
                        spotColor = if (rgbEnabled) accentGlow else Color.Black
                    )
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        0.8.dp,
                        if (rgbEnabled) accentGlow.copy(alpha = 0.6f) else Color(0xFF3E3E48),
                        RoundedCornerShape(8.dp)
                    )
                    .background(if (isDark) Color(0xFF1E1E26) else MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(item.id) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            down.consume()
                            onShortcutClicked(item)
                        }
                    }
                    .padding(horizontal = 11.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.displayCombo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (rgbEnabled) accentGlow else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
