package com.mackey.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mackey.app.bluetooth.HidConstants
import com.mackey.app.model.KeyItem
import com.mackey.app.model.KeyType
import com.mackey.app.ui.components.ConnectionBadge
import com.mackey.app.ui.components.KeyButton
import com.mackey.app.ui.components.ModifierKeyButton
import com.mackey.app.ui.components.ShortcutBar
import com.mackey.app.ui.theme.MacBlueAccent
import com.mackey.app.ui.theme.MacGreen
import com.mackey.app.viewmodel.KeyboardViewModel
import com.mackey.app.viewmodel.SettingsViewModel

@Composable
fun KeyboardScreen(
    keyboardViewModel: KeyboardViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToConnection: () -> Unit
) {
    val connectionState by keyboardViewModel.connectionState.collectAsState()
    val latchedMods by keyboardViewModel.latchedModifiers.collectAsState()
    val lockedMods by keyboardViewModel.lockedModifiers.collectAsState()
    val capsFromHost by keyboardViewModel.capsLockFromHost.collectAsState()
    val fnActive by keyboardViewModel.fnActive.collectAsState()

    val isDark by settingsViewModel.isDarkMode.collectAsState()
    val keyScale by settingsViewModel.keySizeScale.collectAsState()
    val shortcuts by settingsViewModel.shortcuts.collectAsState()

    val baseKeyHeight = (42 * keyScale).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Connection & Control Bar
        Column {
            ConnectionBadge(
                connectionState = connectionState,
                onClick = onNavigateToConnection
            )

            // Function Bar / Status Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Caps Lock LED status from host
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (capsFromHost) MacGreen else Color.Gray)
                    )
                    Text(
                        text = if (capsFromHost) "CAPS ON" else "Caps Off",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Clear Modifiers Button
                if (latchedMods != HidConstants.MODIFIER_NONE || lockedMods != HidConstants.MODIFIER_NONE) {
                    OutlinedButton(
                        onClick = { keyboardViewModel.clearAllModifiers() },
                        modifier = Modifier.height(26.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MacBlueAccent)
                    ) {
                        Text("Reset ⌘/⌥", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Quick Mac Shortcut Toolbar
            ShortcutBar(
                shortcuts = shortcuts,
                onShortcutClicked = { keyboardViewModel.triggerShortcut(it) },
                isDark = isDark
            )
        }

        // Full Interactive Mac Keyboard
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // Optional Fn Media Row (Shows when Fn is toggled)
            if (fnActive) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(KeyItem("B-", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_BRIGHTNESS_DOWN) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("B+", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_BRIGHTNESS_UP) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("⏮", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_PREV_TRACK) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("⏯", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_PLAY_PAUSE) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("⏭", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_NEXT_TRACK) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("🔇", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_MUTE) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("🔉", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_VOLUME_DOWN) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                    KeyButton(KeyItem("🔊", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_VOLUME_UP) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark)
                }
            }

            // Row 1: Number Row (`~` to Delete)
            Row(modifier = Modifier.fillMaxWidth()) {
                KeyButton(KeyItem("`", "~", HidConstants.KEY_GRAVE, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("1", "!", HidConstants.KEY_1, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("2", "@", HidConstants.KEY_2, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("3", "#", HidConstants.KEY_3, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("4", "$", HidConstants.KEY_4, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("5", "%", HidConstants.KEY_5, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("6", "^", HidConstants.KEY_6, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("7", "&", HidConstants.KEY_7, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("8", "*", HidConstants.KEY_8, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("9", "(", HidConstants.KEY_9, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("0", ")", HidConstants.KEY_0, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("-", "_", HidConstants.KEY_MINUS, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("=", "+", HidConstants.KEY_EQUAL, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("⌫", null, HidConstants.KEY_BACKSPACE, weight = 1.4f, type = KeyType.ACTION), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.4f), baseKeyHeight, isDark)
            }

            // Row 2: QWERTY Row
            Row(modifier = Modifier.fillMaxWidth()) {
                KeyButton(KeyItem("Tab", null, HidConstants.KEY_TAB, weight = 1.3f, type = KeyType.SPECIAL), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.3f), baseKeyHeight, isDark)
                KeyButton(KeyItem("Q", null, HidConstants.KEY_Q), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("W", null, HidConstants.KEY_W), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("E", null, HidConstants.KEY_E), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("R", null, HidConstants.KEY_R), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("T", null, HidConstants.KEY_T), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("Y", null, HidConstants.KEY_Y), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("U", null, HidConstants.KEY_U), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("I", null, HidConstants.KEY_I), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("O", null, HidConstants.KEY_O), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("P", null, HidConstants.KEY_P), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("[", "{", HidConstants.KEY_LEFTBRACE), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("]", "}", HidConstants.KEY_RIGHTBRACE), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("\\", "|", HidConstants.KEY_BACKSLASH), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
            }

            // Row 3: Home Row
            Row(modifier = Modifier.fillMaxWidth()) {
                KeyButton(KeyItem("Esc", null, HidConstants.KEY_ESCAPE, weight = 1.3f, type = KeyType.SPECIAL), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.3f), baseKeyHeight, isDark)
                KeyButton(KeyItem("A", null, HidConstants.KEY_A), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("S", null, HidConstants.KEY_S), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("D", null, HidConstants.KEY_D), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("F", null, HidConstants.KEY_F), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("G", null, HidConstants.KEY_G), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("H", null, HidConstants.KEY_H), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("J", null, HidConstants.KEY_J), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("K", null, HidConstants.KEY_K), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("L", null, HidConstants.KEY_L), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem(";", ":", HidConstants.KEY_SEMICOLON), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("'", "\"", HidConstants.KEY_APOSTROPHE), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("return", null, HidConstants.KEY_RETURN, weight = 1.7f, type = KeyType.ACTION), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.7f), baseKeyHeight, isDark)
            }

            // Row 4: Shift Row
            Row(modifier = Modifier.fillMaxWidth()) {
                val isShiftLatched = (latchedMods.toInt() and HidConstants.MODIFIER_LEFT_SHIFT.toInt()) != 0
                val isShiftLocked = (lockedMods.toInt() and HidConstants.MODIFIER_LEFT_SHIFT.toInt()) != 0

                ModifierKeyButton(
                    label = "shift",
                    symbol = "⇧",
                    isLatched = isShiftLatched,
                    isLocked = isShiftLocked,
                    onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_LEFT_SHIFT) },
                    modifier = Modifier.weight(1.6f),
                    height = baseKeyHeight,
                    isDark = isDark
                )

                KeyButton(KeyItem("Z", null, HidConstants.KEY_Z), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("X", null, HidConstants.KEY_X), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("C", null, HidConstants.KEY_C), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("V", null, HidConstants.KEY_V), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("B", null, HidConstants.KEY_B), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("N", null, HidConstants.KEY_N), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("M", null, HidConstants.KEY_M), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem(",", "<", HidConstants.KEY_COMMA), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem(".", ">", HidConstants.KEY_DOT), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)
                KeyButton(KeyItem("/", "?", HidConstants.KEY_SLASH), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark)

                ModifierKeyButton(
                    label = "shift",
                    symbol = "⇧",
                    isLatched = isShiftLatched,
                    isLocked = isShiftLocked,
                    onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_RIGHT_SHIFT) },
                    modifier = Modifier.weight(1.6f),
                    height = baseKeyHeight,
                    isDark = isDark
                )
            }

            // Row 5: Mac Modifiers & Arrows
            Row(modifier = Modifier.fillMaxWidth()) {
                // Fn Key
                ModifierKeyButton(
                    label = "fn",
                    symbol = "fn",
                    isLatched = fnActive,
                    isLocked = false,
                    onToggle = { keyboardViewModel.toggleFn() },
                    modifier = Modifier.weight(1.1f),
                    height = baseKeyHeight,
                    isDark = isDark
                )

                // Control ⌃
                val isCtrlLatched = (latchedMods.toInt() and HidConstants.MODIFIER_LEFT_CTRL.toInt()) != 0
                val isCtrlLocked = (lockedMods.toInt() and HidConstants.MODIFIER_LEFT_CTRL.toInt()) != 0
                ModifierKeyButton(
                    label = "control",
                    symbol = "⌃",
                    isLatched = isCtrlLatched,
                    isLocked = isCtrlLocked,
                    onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_LEFT_CTRL) },
                    modifier = Modifier.weight(1.1f),
                    height = baseKeyHeight,
                    isDark = isDark
                )

                // Option ⌥
                val isOptLatched = (latchedMods.toInt() and HidConstants.MODIFIER_LEFT_ALT.toInt()) != 0
                val isOptLocked = (lockedMods.toInt() and HidConstants.MODIFIER_LEFT_ALT.toInt()) != 0
                ModifierKeyButton(
                    label = "option",
                    symbol = "⌥",
                    isLatched = isOptLatched,
                    isLocked = isOptLocked,
                    onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_LEFT_ALT) },
                    modifier = Modifier.weight(1.2f),
                    height = baseKeyHeight,
                    isDark = isDark
                )

                // Command ⌘
                val isCmdLatched = (latchedMods.toInt() and HidConstants.MODIFIER_LEFT_GUI.toInt()) != 0
                val isCmdLocked = (lockedMods.toInt() and HidConstants.MODIFIER_LEFT_GUI.toInt()) != 0
                ModifierKeyButton(
                    label = "command",
                    symbol = "⌘",
                    isLatched = isCmdLatched,
                    isLocked = isCmdLocked,
                    onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_LEFT_GUI) },
                    modifier = Modifier.weight(1.4f),
                    height = baseKeyHeight,
                    isDark = isDark
                )

                // Spacebar
                KeyButton(
                    KeyItem("space", null, HidConstants.KEY_SPACE, weight = 3.6f, type = KeyType.SPACE),
                    { keyboardViewModel.onKeyDown(it) },
                    { keyboardViewModel.onKeyUp(it) },
                    Modifier.weight(3.6f),
                    baseKeyHeight,
                    isDark
                )

                // Right Command ⌘
                ModifierKeyButton(
                    label = "command",
                    symbol = "⌘",
                    isLatched = isCmdLatched,
                    isLocked = isCmdLocked,
                    onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_RIGHT_GUI) },
                    modifier = Modifier.weight(1.4f),
                    height = baseKeyHeight,
                    isDark = isDark
                )

                // Left Arrow
                KeyButton(
                    KeyItem("◀", null, HidConstants.KEY_LEFT_ARROW, weight = 1f, type = KeyType.ARROW),
                    { keyboardViewModel.onKeyDown(it) },
                    { keyboardViewModel.onKeyUp(it) },
                    Modifier.weight(1f),
                    baseKeyHeight,
                    isDark
                )

                // Up / Down Split Block
                Column(modifier = Modifier.weight(1f)) {
                    KeyButton(
                        KeyItem("▲", null, HidConstants.KEY_UP_ARROW, type = KeyType.ARROW),
                        { keyboardViewModel.onKeyDown(it) },
                        { keyboardViewModel.onKeyUp(it) },
                        Modifier.fillMaxWidth(),
                        baseKeyHeight / 2,
                        isDark
                    )
                    KeyButton(
                        KeyItem("▼", null, HidConstants.KEY_DOWN_ARROW, type = KeyType.ARROW),
                        { keyboardViewModel.onKeyDown(it) },
                        { keyboardViewModel.onKeyUp(it) },
                        Modifier.fillMaxWidth(),
                        baseKeyHeight / 2,
                        isDark
                    )
                }

                // Right Arrow
                KeyButton(
                    KeyItem("▶", null, HidConstants.KEY_RIGHT_ARROW, weight = 1f, type = KeyType.ARROW),
                    { keyboardViewModel.onKeyDown(it) },
                    { keyboardViewModel.onKeyUp(it) },
                    Modifier.weight(1f),
                    baseKeyHeight,
                    isDark
                )
            }
        }
    }
}

