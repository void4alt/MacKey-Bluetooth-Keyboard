package com.mackey.app.model

import com.mackey.app.bluetooth.HidConstants

enum class KeyType {
    NORMAL,
    MODIFIER,
    SPECIAL,
    ACTION,
    ARROW,
    SPACE
}

data class KeyItem(
    val label: String,
    val subLabel: String? = null,
    val keycode: Byte = HidConstants.KEY_NONE,
    val modifierBit: Byte = HidConstants.MODIFIER_NONE,
    val weight: Float = 1.0f,
    val type: KeyType = KeyType.NORMAL
)

