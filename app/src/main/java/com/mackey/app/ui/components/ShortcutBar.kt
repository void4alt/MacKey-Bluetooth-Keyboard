package com.mackey.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.model.ShortcutItem
import com.mackey.app.ui.theme.MacBlueAccent

/**
 * Dedicated Mac Shortcut Toolbar.
 * Provides rapid one-tap access to common and custom macOS commands.
 */
@Composable
fun ShortcutBar(
    shortcuts: List<ShortcutItem>,
    onShortcutClicked: (ShortcutItem) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentPadding = PaddingValues(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(shortcuts, key = { it.id }) { item ->
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .shadow(1.dp, RoundedCornerShape(6.dp))
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onShortcutClicked(item) }
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.displayCombo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MacBlueAccent
                    )
                    Text(
                        text = item.name,
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

