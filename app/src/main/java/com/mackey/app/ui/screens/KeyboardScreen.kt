package com.mackey.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
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
    val isCapsActive by keyboardViewModel.capsLockActive.collectAsState()
    val fnActive by keyboardViewModel.fnActive.collectAsState()

    val isDark by settingsViewModel.isDarkMode.collectAsState()
    val keyScale by settingsViewModel.keySizeScale.collectAsState()
    val shortcuts by settingsViewModel.shortcuts.collectAsState()
    val rgbEnabled by settingsViewModel.rgbEnabled.collectAsState()

    val baseKeyHeight = (42 * keyScale).dp

    // Dynamic RGB Rainbow Chroma Animation
    val infiniteTransition = rememberInfiniteTransition(label = "rgb_wave")
    val animatedHue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "chroma_hue"
    )

    fun getChromaColor(offsetDeg: Float): Color {
        if (!rgbEnabled) return MacBlueAccent
        val h = (animatedHue + offsetDeg) % 360f
        return Color.hsl(hue = h, saturation = 0.95f, lightness = 0.58f)
    }

    val row1Glow = getChromaColor(0f)    // Cyan / Electric Blue
    val row2Glow = getChromaColor(70f)   // Purple / Magenta
    val row3Glow = getChromaColor(140f)  // Hot Pink / Crimson
    val row4Glow = getChromaColor(210f)  // Neon Amber / Gold
    val row5Glow = getChromaColor(280f)  // Emerald / Mint

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF101014) else MaterialTheme.colorScheme.background)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Connection & Control Bar
        Column {
            ConnectionBadge(
                connectionState = connectionState,
                onClick = onNavigateToConnection
            )

            // Function Bar / Status Strip with RGB Toggle
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
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(3.5.dp))
                            .background(if (isCapsActive) MacGreen else Color.Gray)
                    )
                    Text(
                        text = if (isCapsActive) "CAPS ON" else "Caps Off",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // RGB Mode Pill Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (rgbEnabled) row1Glow.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(0.8.dp, if (rgbEnabled) row1Glow else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { settingsViewModel.setRgbEnabled(!rgbEnabled) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (rgbEnabled) "🌈 RGB ON" else "RGB OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (rgbEnabled) row1Glow else MaterialTheme.colorScheme.onSurfaceVariant
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
            }

            // Quick Mac Shortcut Toolbar with RGB Glow
            ShortcutBar(
                shortcuts = shortcuts,
                onShortcutClicked = { keyboardViewModel.triggerShortcut(it) },
                isDark = isDark,
                rgbEnabled = rgbEnabled,
                accentGlow = row2Glow
            )
        }

        // Full Interactive Mac Keyboard wrapped in glowing chassis
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (rgbEnabled) 1.2.dp else 0.5.dp,
                    brush = if (rgbEnabled) {
                        Brush.horizontalGradient(
                            listOf(row1Glow, row2Glow, row3Glow, row4Glow, row5Glow, row1Glow)
                        )
                    } else {
                        Brush.linearGradient(listOf(Color(0xFF282830), Color(0xFF282830)))
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .background(if (isDark) Color(0xFF131318) else MaterialTheme.colorScheme.surface)
                .padding(3.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // Optional Fn Media Row (Shows when Fn is toggled)
                if (fnActive) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        KeyButton(KeyItem("B-", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_BRIGHTNESS_DOWN) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("B+", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_BRIGHTNESS_UP) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("⏮", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_PREV_TRACK) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("⏯", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_PLAY_PAUSE) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("⏭", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_NEXT_TRACK) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("🔇", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_MUTE) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("🔉", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_VOLUME_DOWN) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                        KeyButton(KeyItem("🔊", null, HidConstants.KEY_NONE, weight = 1f), { keyboardViewModel.triggerConsumerKey(HidConstants.CONSUMER_VOLUME_UP) }, {}, Modifier.weight(1f), baseKeyHeight * 0.85f, isDark, rgbEnabled, row1Glow)
                    }
                }

                // Row 1: Number Row (`Esc`, `~` to Delete)
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(KeyItem("esc", null, HidConstants.KEY_ESCAPE, weight = 1.1f, type = KeyType.SPECIAL), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("`", "~", HidConstants.KEY_GRAVE, weight = 0.9f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(0.9f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("1", "!", HidConstants.KEY_1, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("2", "@", HidConstants.KEY_2, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("3", "#", HidConstants.KEY_3, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("4", "$", HidConstants.KEY_4, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("5", "%", HidConstants.KEY_5, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("6", "^", HidConstants.KEY_6, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("7", "&", HidConstants.KEY_7, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("8", "*", HidConstants.KEY_8, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("9", "(", HidConstants.KEY_9, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("0", ")", HidConstants.KEY_0, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("-", "_", HidConstants.KEY_MINUS, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("=", "+", HidConstants.KEY_EQUAL, weight = 1f), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                    KeyButton(KeyItem("⌫", null, HidConstants.KEY_BACKSPACE, weight = 1.3f, type = KeyType.ACTION), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.3f), baseKeyHeight, isDark, rgbEnabled, row1Glow)
                }

                // Row 2: QWERTY Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(KeyItem("Tab", null, HidConstants.KEY_TAB, weight = 1.3f, type = KeyType.SPECIAL), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.3f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("Q", null, HidConstants.KEY_Q), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("W", null, HidConstants.KEY_W), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("E", null, HidConstants.KEY_E), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("R", null, HidConstants.KEY_R), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("T", null, HidConstants.KEY_T), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("Y", null, HidConstants.KEY_Y), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("U", null, HidConstants.KEY_U), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("I", null, HidConstants.KEY_I), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("O", null, HidConstants.KEY_O), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("P", null, HidConstants.KEY_P), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("[", "{", HidConstants.KEY_LEFTBRACE), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("]", "}", HidConstants.KEY_RIGHTBRACE), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                    KeyButton(KeyItem("\\", "|", HidConstants.KEY_BACKSLASH), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row2Glow)
                }

                // Row 3: Home Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    KeyButton(
                        key = KeyItem(
                            label = if (isCapsActive) "CAPS" else "caps",
                            subLabel = if (isCapsActive) "●" else null,
                            keycode = HidConstants.KEY_CAPSLOCK,
                            weight = 1.3f,
                            type = KeyType.SPECIAL
                        ),
                        onDown = { keyboardViewModel.toggleCapsLock() },
                        onUp = {},
                        modifier = Modifier.weight(1.3f),
                        height = baseKeyHeight,
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        activeGlowColor = row3Glow
                    )
                    KeyButton(KeyItem("A", null, HidConstants.KEY_A), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("S", null, HidConstants.KEY_S), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("D", null, HidConstants.KEY_D), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("F", null, HidConstants.KEY_F), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("G", null, HidConstants.KEY_G), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("H", null, HidConstants.KEY_H), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("J", null, HidConstants.KEY_J), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("K", null, HidConstants.KEY_K), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("L", null, HidConstants.KEY_L), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem(";", ":", HidConstants.KEY_SEMICOLON), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("'", "\"", HidConstants.KEY_APOSTROPHE), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
                    KeyButton(KeyItem("return", null, HidConstants.KEY_RETURN, weight = 1.7f, type = KeyType.ACTION), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1.7f), baseKeyHeight, isDark, rgbEnabled, row3Glow)
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
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = row4Glow
                    )

                    KeyButton(KeyItem("Z", null, HidConstants.KEY_Z), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("X", null, HidConstants.KEY_X), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("C", null, HidConstants.KEY_C), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("V", null, HidConstants.KEY_V), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("B", null, HidConstants.KEY_B), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("N", null, HidConstants.KEY_N), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("M", null, HidConstants.KEY_M), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem(",", "<", HidConstants.KEY_COMMA), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem(".", ">", HidConstants.KEY_DOT), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)
                    KeyButton(KeyItem("/", "?", HidConstants.KEY_SLASH), { keyboardViewModel.onKeyDown(it) }, { keyboardViewModel.onKeyUp(it) }, Modifier.weight(1f), baseKeyHeight, isDark, rgbEnabled, row4Glow)

                    ModifierKeyButton(
                        label = "shift",
                        symbol = "⇧",
                        isLatched = isShiftLatched,
                        isLocked = isShiftLocked,
                        onToggle = { keyboardViewModel.handleModifierTap(HidConstants.MODIFIER_RIGHT_SHIFT) },
                        modifier = Modifier.weight(1.6f),
                        height = baseKeyHeight,
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = row4Glow
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
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = row5Glow
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
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = getChromaColor(300f)
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
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = getChromaColor(320f)
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
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = getChromaColor(345f)
                    )

                    // Spacebar with Glowing RGB Center Laser
                    KeyButton(
                        KeyItem("space", null, HidConstants.KEY_SPACE, weight = 3.6f, type = KeyType.SPACE),
                        { keyboardViewModel.onKeyDown(it) },
                        { keyboardViewModel.onKeyUp(it) },
                        Modifier.weight(3.6f),
                        baseKeyHeight,
                        isDark,
                        rgbEnabled,
                        row5Glow
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
                        isDark = isDark,
                        rgbEnabled = rgbEnabled,
                        rgbGlowColor = getChromaColor(345f)
                    )

                    // Left Arrow
                    KeyButton(
                        KeyItem("◀", null, HidConstants.KEY_LEFT_ARROW, weight = 1f, type = KeyType.ARROW),
                        { keyboardViewModel.onKeyDown(it) },
                        { keyboardViewModel.onKeyUp(it) },
                        Modifier.weight(1f),
                        baseKeyHeight,
                        isDark,
                        rgbEnabled,
                        row1Glow
                    )

                    // Up / Down Split Block
                    Column(modifier = Modifier.weight(1f)) {
                        KeyButton(
                            KeyItem("▲", null, HidConstants.KEY_UP_ARROW, type = KeyType.ARROW),
                            { keyboardViewModel.onKeyDown(it) },
                            { keyboardViewModel.onKeyUp(it) },
                            Modifier.fillMaxWidth(),
                            baseKeyHeight / 2,
                            isDark,
                            rgbEnabled,
                            row1Glow
                        )
                        KeyButton(
                            KeyItem("▼", null, HidConstants.KEY_DOWN_ARROW, type = KeyType.ARROW),
                            { keyboardViewModel.onKeyDown(it) },
                            { keyboardViewModel.onKeyUp(it) },
                            Modifier.fillMaxWidth(),
                            baseKeyHeight / 2,
                            isDark,
                            rgbEnabled,
                            row1Glow
                        )
                    }

                    // Right Arrow
                    KeyButton(
                        KeyItem("▶", null, HidConstants.KEY_RIGHT_ARROW, weight = 1f, type = KeyType.ARROW),
                        { keyboardViewModel.onKeyDown(it) },
                        { keyboardViewModel.onKeyUp(it) },
                        Modifier.weight(1f),
                        baseKeyHeight,
                        isDark,
                        rgbEnabled,
                        row1Glow
                    )
                }
            }
        }
    }
}
