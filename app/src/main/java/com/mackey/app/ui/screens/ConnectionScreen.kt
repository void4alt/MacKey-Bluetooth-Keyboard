package com.mackey.app.ui.screens

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.bluetooth.ConnectionState
import com.mackey.app.ui.theme.MacAmber
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.ui.theme.MacGreen
import com.mackey.app.ui.theme.MacRed
import com.mackey.app.viewmodel.KeyboardViewModel

@Composable
fun ConnectionScreen(
    keyboardViewModel: KeyboardViewModel
) {
    val context = LocalContext.current
    val connectionState by keyboardViewModel.connectionState.collectAsState()
    val bondedDevices = keyboardViewModel.hidManager.getBondedDevices()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Connection Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val (statusLabel, statusColor) = when (connectionState) {
                            is ConnectionState.Connected -> "Connected" to MacGreen
                            is ConnectionState.Connecting -> "Connecting…" to MacBlueAccent
                            is ConnectionState.Disconnecting -> "Disconnecting…" to MacAmber
                            is ConnectionState.Disconnected -> "Disconnected" to MacAmber
                            is ConnectionState.BluetoothDisabled -> "Bluetooth Off" to MacRed
                            is ConnectionState.NotSupported -> "HID Unsupported" to MacRed
                            is ConnectionState.PermissionDenied -> "Missing Permissions" to MacRed
                            is ConnectionState.Error -> "Error" to MacRed
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Text(
                                text = statusLabel,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                        }
                    }

                    if (connectionState is ConnectionState.Connected) {
                        val dev = (connectionState as ConnectionState.Connected).device
                        val devName = try { dev.name ?: "MacBook" } catch (_: SecurityException) { "MacBook" }
                        val devAddr = dev.address

                        Text(
                            text = "Connected Host: $devName ($devAddr)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { keyboardViewModel.hidManager.disconnect() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MacRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Disconnect from Mac", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Connect directly to your MacBook using real Bluetooth HID keyboard profile. No internet, Wi-Fi, or software on Mac required.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Make Discoverable Button
                            Button(
                                onClick = {
                                    val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                                    }
                                    if (context is Activity) {
                                        context.startActivity(discoverableIntent)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MacBlueAccent)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                Text("Make Discoverable", fontSize = 12.sp)
                            }

                            // Re-initialize button
                            OutlinedButton(
                                onClick = { keyboardViewModel.hidManager.initialize() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                Text("Restart HID", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Paired Mac Devices Section
        item {
            Text(
                text = "Paired Devices",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (bondedDevices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No paired Bluetooth devices found.\nMake this phone discoverable and pair it from your Mac's Bluetooth settings.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        } else {
            items(bondedDevices) { device ->
                val name = try { device.name ?: "Unknown Device" } catch (_: SecurityException) { "Unknown Device" }
                val address = device.address

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = address, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = { keyboardViewModel.hidManager.connect(device) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MacBlueAccent)
                        ) {
                            Text("Connect", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Step-by-Step macOS Pairing Guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How to Pair with macOS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MacBlueAccent
                    )

                    val steps = listOf(
                        "1. On your Mac, open System Settings > Bluetooth.",
                        "2. Tap 'Make Discoverable' above on this phone.",
                        "3. On your Mac, find 'MacKey Keyboard' in Nearby Devices.",
                        "4. Click 'Connect'. A 6-digit confirmation code will appear.",
                        "5. Confirm the code on both your Mac and phone.",
                        "6. macOS will register the phone as an authentic Bluetooth keyboard.",
                        "7. If the macOS 'Keyboard Setup Assistant' pops up, select 'ANSI (Standard US)' layout."
                    )

                    steps.forEach { step ->
                        Text(
                            text = step,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Troubleshooting Guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Troubleshooting",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• Connection Failed: On your Mac, click the (i) icon next to the phone in Bluetooth settings and click 'Forget This Device'. Then unpair on the phone and pair again.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• Keystrokes Not Appearing: Ensure your phone has Bluetooth turned on and the status above displays 'Connected'. Tap the 'Clear ⌘/⌥' button if modifier keys were held down.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• HID Unsupported: If your phone shows 'HID Unsupported', the manufacturer of your phone model disabled peripheral Bluetooth HID role in their Android kernel build.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

