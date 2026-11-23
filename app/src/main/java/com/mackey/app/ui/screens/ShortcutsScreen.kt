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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.bluetooth.HidConstants
import com.mackey.app.model.ShortcutItem
import com.mackey.app.ui.components.ConnectionBadge
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.ui.theme.MacRed
import com.mackey.app.viewmodel.KeyboardViewModel
import com.mackey.app.viewmodel.SettingsViewModel

@Composable
fun ShortcutsScreen(
    keyboardViewModel: KeyboardViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToConnection: () -> Unit
) {
    val connectionState by keyboardViewModel.connectionState.collectAsState()
    val shortcuts by settingsViewModel.shortcuts.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MacBlueAccent,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Custom Shortcut")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ConnectionBadge(
                connectionState = connectionState,
                onClick = onNavigateToConnection
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mac Shortcuts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Configure and test 1-tap macOS shortcut triggers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { settingsViewModel.resetShortcutsToDefaults() },
                    modifier = Modifier.height(30.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Reset Defaults", fontSize = 11.sp)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(shortcuts, key = { it.id }) { shortcut ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = shortcut.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = shortcut.displayCombo,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MacBlueAccent
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Test on Mac Button
                                Button(
                                    onClick = { keyboardViewModel.triggerShortcut(shortcut) },
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MacBlueAccent)
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Test",
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text("Test", fontSize = 11.sp)
                                }

                                // Delete Button (for custom shortcuts)
                                if (!shortcut.isPreset) {
                                    IconButton(
                                        onClick = { settingsViewModel.removeShortcut(shortcut.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MacRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddShortcutDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, modMask, keycode, display ->
                settingsViewModel.addCustomShortcut(name, modMask, keycode, display)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddShortcutDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, modMask: Byte, keycode: Byte, display: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var keyChar by remember { mutableStateOf("") }
    var useCmd by remember { mutableStateOf(true) }
    var useOpt by remember { mutableStateOf(false) }
    var useCtrl by remember { mutableStateOf(false) }
    var useShift by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Mac Shortcut", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Shortcut Name (e.g. Screenshot)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Select Mac Modifiers:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = useCmd, onClick = { useCmd = !useCmd }, label = { Text("⌘ Cmd") })
                    FilterChip(selected = useOpt, onClick = { useOpt = !useOpt }, label = { Text("⌥ Opt") })
                    FilterChip(selected = useCtrl, onClick = { useCtrl = !useCtrl }, label = { Text("⌃ Ctrl") })
                    FilterChip(selected = useShift, onClick = { useShift = !useShift }, label = { Text("⇧ Shift") })
                }

                OutlinedTextField(
                    value = keyChar,
                    onValueChange = { if (it.length <= 1) keyChar = it.uppercase() },
                    label = { Text("Key (e.g. 4, P, T)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && keyChar.isNotEmpty()) {
                        var mask: Byte = 0
                        var comboStr = ""
                        if (useCtrl) { mask = (mask.toInt() or HidConstants.MODIFIER_LEFT_CTRL.toInt()).toByte(); comboStr += "⌃" }
                        if (useOpt) { mask = (mask.toInt() or HidConstants.MODIFIER_LEFT_ALT.toInt()).toByte(); comboStr += "⌥" }
                        if (useShift) { mask = (mask.toInt() or HidConstants.MODIFIER_LEFT_SHIFT.toInt()).toByte(); comboStr += "⇧" }
                        if (useCmd) { mask = (mask.toInt() or HidConstants.MODIFIER_LEFT_GUI.toInt()).toByte(); comboStr += "⌘" }

                        val ch = keyChar[0]
                        val keycode: Byte = when (ch) {
                            in 'A'..'Z' -> (HidConstants.KEY_A + (ch - 'A')).toByte()
                            in '1'..'9' -> (HidConstants.KEY_1 + (ch - '1')).toByte()
                            '0' -> HidConstants.KEY_0
                            ' ' -> HidConstants.KEY_SPACE
                            else -> HidConstants.KEY_A
                        }

                        comboStr += ch
                        onAdd(name, mask, keycode, comboStr)
                    }
                },
                enabled = name.isNotBlank() && keyChar.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

