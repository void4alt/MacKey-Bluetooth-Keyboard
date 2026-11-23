package com.mackey.app.model

import com.mackey.app.bluetooth.HidConstants
import java.util.UUID

data class ShortcutItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val modifierMask: Byte,
    val keycode: Byte,
    val displayCombo: String,
    val isPreset: Boolean = true
) {
    companion object {
        val DEFAULT_SHORTCUTS = listOf(
            ShortcutItem(
                name = "Copy",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_C,
                displayCombo = "⌘C"
            ),
            ShortcutItem(
                name = "Paste",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_V,
                displayCombo = "⌘V"
            ),
            ShortcutItem(
                name = "Cut",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_X,
                displayCombo = "⌘X"
            ),
            ShortcutItem(
                name = "Undo",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_Z,
                displayCombo = "⌘Z"
            ),
            ShortcutItem(
                name = "Select All",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_A,
                displayCombo = "⌘A"
            ),
            ShortcutItem(
                name = "Save",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_S,
                displayCombo = "⌘S"
            ),
            ShortcutItem(
                name = "Find",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_F,
                displayCombo = "⌘F"
            ),
            ShortcutItem(
                name = "Close Window",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_W,
                displayCombo = "⌘W"
            ),
            ShortcutItem(
                name = "Switch Apps",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_TAB,
                displayCombo = "⌘Tab"
            ),
            ShortcutItem(
                name = "Spotlight",
                modifierMask = HidConstants.MODIFIER_LEFT_GUI,
                keycode = HidConstants.KEY_SPACE,
                displayCombo = "⌘Space"
            )
        )
    }
}

