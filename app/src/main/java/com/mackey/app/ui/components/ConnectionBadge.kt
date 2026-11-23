package com.mackey.app.ui.components

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.bluetooth.ConnectionState
import com.mackey.app.ui.theme.MacAmber
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.ui.theme.MacGreen
import com.mackey.app.ui.theme.MacRed

@Composable
fun ConnectionBadge(
    connectionState: ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusText, dotColor) = when (connectionState) {
        is ConnectionState.Connected -> {
            val name = getSafeDeviceName(connectionState.device)
            "Connected: $name" to MacGreen
        }
        is ConnectionState.Connecting -> "Connecting to Mac…" to MacBlueAccent
        is ConnectionState.Disconnecting -> "Disconnecting…" to MacAmber
        is ConnectionState.Disconnected -> "Disconnected (Tap to Connect)" to MacAmber
        is ConnectionState.BluetoothDisabled -> "Bluetooth Disabled" to MacRed
        is ConnectionState.NotSupported -> "HID Not Supported" to MacRed
        is ConnectionState.PermissionDenied -> "Permissions Required" to MacRed
        is ConnectionState.Error -> "Connection Issue" to MacRed
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = if (connectionState is ConnectionState.Connected) "Disconnect" else "Pair / Connect",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MacBlueAccent
            )
        }
    }
}

private fun getSafeDeviceName(device: BluetoothDevice): String {
    return try {
        device.name?.ifEmpty { "MacBook" } ?: "MacBook"
    } catch (_: SecurityException) {
        "MacBook"
    }
}

