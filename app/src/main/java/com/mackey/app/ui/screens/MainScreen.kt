package com.mackey.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shortcut
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.viewmodel.KeyboardViewModel
import com.mackey.app.viewmodel.SettingsViewModel

enum class NavigationTab(val label: String) {
    KEYBOARD("Keyboard"),
    TEXT_MODE("Text"),
    SHORTCUTS("Shortcuts"),
    CONNECTION("Connect"),
    SETTINGS("Settings")
}

@Composable
fun MainScreen(
    keyboardViewModel: KeyboardViewModel,
    settingsViewModel: SettingsViewModel
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(NavigationTab.KEYBOARD.ordinal) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            when (tab) {
                                NavigationTab.KEYBOARD -> Icon(Icons.Default.Keyboard, contentDescription = tab.label)
                                NavigationTab.TEXT_MODE -> Icon(Icons.Default.EditNote, contentDescription = tab.label)
                                NavigationTab.SHORTCUTS -> Icon(Icons.Default.Shortcut, contentDescription = tab.label)
                                NavigationTab.CONNECTION -> Icon(Icons.Default.Bluetooth, contentDescription = tab.label)
                                NavigationTab.SETTINGS -> Icon(Icons.Default.Settings, contentDescription = tab.label)
                            }
                        },
                        label = { Text(tab.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MacBlueAccent,
                            selectedTextColor = MacBlueAccent,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (NavigationTab.entries[selectedTab]) {
                NavigationTab.KEYBOARD -> KeyboardScreen(
                    keyboardViewModel = keyboardViewModel,
                    settingsViewModel = settingsViewModel,
                    onNavigateToConnection = { selectedTab = NavigationTab.CONNECTION.ordinal }
                )
                NavigationTab.TEXT_MODE -> TextInputScreen(
                    keyboardViewModel = keyboardViewModel,
                    onNavigateToConnection = { selectedTab = NavigationTab.CONNECTION.ordinal }
                )
                NavigationTab.SHORTCUTS -> ShortcutsScreen(
                    keyboardViewModel = keyboardViewModel,
                    settingsViewModel = settingsViewModel,
                    onNavigateToConnection = { selectedTab = NavigationTab.CONNECTION.ordinal }
                )
                NavigationTab.CONNECTION -> ConnectionScreen(
                    keyboardViewModel = keyboardViewModel
                )
                NavigationTab.SETTINGS -> SettingsScreen(
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

