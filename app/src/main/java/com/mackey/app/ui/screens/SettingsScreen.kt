package com.mackey.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val isDark by settingsViewModel.isDarkMode.collectAsState()
    val hapticEnabled by settingsViewModel.hapticEnabled.collectAsState()
    val keyScale by settingsViewModel.keySizeScale.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Appearance
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Appearance",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dark Mode", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Apple-styled dark graphite layout", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Switch(
                            checked = isDark,
                            onCheckedChange = { settingsViewModel.setDarkMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = MacBlueAccent)
                        )
                    }
                }
            }
        }

        // Haptic Feedback
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Haptic Feedback",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Vibration on Keystroke", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Tactile sensation when pressing keys", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Switch(
                            checked = hapticEnabled,
                            onCheckedChange = { settingsViewModel.setHapticEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = MacBlueAccent)
                        )
                    }

                    if (hapticEnabled) {
                        Text("Intensity:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = true, onClick = { settingsViewModel.setHapticStrength(1) }, label = { Text("Light") })
                            FilterChip(selected = false, onClick = { settingsViewModel.setHapticStrength(2) }, label = { Text("Medium") })
                            FilterChip(selected = false, onClick = { settingsViewModel.setHapticStrength(3) }, label = { Text("Crisp") })
                        }
                    }
                }
            }
        }

        // Keyboard Key Size
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
                        text = "Key Height & Scale",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Adjust key button height for your screen size: ${(keyScale * 100).toInt()}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Slider(
                        value = keyScale,
                        onValueChange = { settingsViewModel.setKeySizeScale(it) },
                        valueRange = 0.85f..1.25f,
                        steps = 7
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Compact (85%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Default (100%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Large (125%)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // About & License
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
                    Text("About MacKey", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0", fontSize = 12.sp, color = MacBlueAccent)
                    Text("100% Free & Open-Source wireless Bluetooth HID keyboard designed exclusively for macOS.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("• No cloud, subscriptions, or paid APIs\n• No internet or Wi-Fi required\n• Direct hardware Bluetooth communication\n• Strict zero-storage privacy policy", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

