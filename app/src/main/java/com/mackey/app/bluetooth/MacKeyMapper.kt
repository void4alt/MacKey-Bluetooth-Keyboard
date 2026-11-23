package com.mackey.app.bluetooth

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * Maps standard characters, numbers, and symbols to USB HID Keycodes
 * and modifier bitmasks recognized by macOS ANSI layout.
 *
 * Also provides high-reliability sequential streaming for Text Input Mode.
 */
object MacKeyMapper {

    data class KeyStroke(
        val modifier: Byte,
        val keycode: Byte
    )

    /**
     * Resolves a character to its corresponding HID modifier and keycode.
     */
    fun charToKeyStroke(char: Char): KeyStroke? {
        val shift = HidConstants.MODIFIER_LEFT_SHIFT
        val none = HidConstants.MODIFIER_NONE

        return when (char) {
            in 'a'..'z' -> KeyStroke(none, (HidConstants.KEY_A + (char - 'a')).toByte())
            in 'A'..'Z' -> KeyStroke(shift, (HidConstants.KEY_A + (char - 'A')).toByte())

            '1' -> KeyStroke(none, HidConstants.KEY_1)
            '2' -> KeyStroke(none, HidConstants.KEY_2)
            '3' -> KeyStroke(none, HidConstants.KEY_3)
            '4' -> KeyStroke(none, HidConstants.KEY_4)
            '5' -> KeyStroke(none, HidConstants.KEY_5)
            '6' -> KeyStroke(none, HidConstants.KEY_6)
            '7' -> KeyStroke(none, HidConstants.KEY_7)
            '8' -> KeyStroke(none, HidConstants.KEY_8)
            '9' -> KeyStroke(none, HidConstants.KEY_9)
            '0' -> KeyStroke(none, HidConstants.KEY_0)

            '!' -> KeyStroke(shift, HidConstants.KEY_1)
            '@' -> KeyStroke(shift, HidConstants.KEY_2)
            '#' -> KeyStroke(shift, HidConstants.KEY_3)
            '$' -> KeyStroke(shift, HidConstants.KEY_4)
            '%' -> KeyStroke(shift, HidConstants.KEY_5)
            '^' -> KeyStroke(shift, HidConstants.KEY_6)
            '&' -> KeyStroke(shift, HidConstants.KEY_7)
            '*' -> KeyStroke(shift, HidConstants.KEY_8)
            '(' -> KeyStroke(shift, HidConstants.KEY_9)
            ')' -> KeyStroke(shift, HidConstants.KEY_0)

            ' ' -> KeyStroke(none, HidConstants.KEY_SPACE)
            '\n' -> KeyStroke(none, HidConstants.KEY_RETURN)
            '\t' -> KeyStroke(none, HidConstants.KEY_TAB)

            '-' -> KeyStroke(none, HidConstants.KEY_MINUS)
            '_' -> KeyStroke(shift, HidConstants.KEY_MINUS)
            '=' -> KeyStroke(none, HidConstants.KEY_EQUAL)
            '+' -> KeyStroke(shift, HidConstants.KEY_EQUAL)
            '[' -> KeyStroke(none, HidConstants.KEY_LEFTBRACE)
            '{' -> KeyStroke(shift, HidConstants.KEY_LEFTBRACE)
            ']' -> KeyStroke(none, HidConstants.KEY_RIGHTBRACE)
            '}' -> KeyStroke(shift, HidConstants.KEY_RIGHTBRACE)
            '\\' -> KeyStroke(none, HidConstants.KEY_BACKSLASH)
            '|' -> KeyStroke(shift, HidConstants.KEY_BACKSLASH)
            ';' -> KeyStroke(none, HidConstants.KEY_SEMICOLON)
            ':' -> KeyStroke(shift, HidConstants.KEY_SEMICOLON)
            '\'' -> KeyStroke(none, HidConstants.KEY_APOSTROPHE)
            '"' -> KeyStroke(shift, HidConstants.KEY_APOSTROPHE)
            '`' -> KeyStroke(none, HidConstants.KEY_GRAVE)
            '~' -> KeyStroke(shift, HidConstants.KEY_GRAVE)
            ',' -> KeyStroke(none, HidConstants.KEY_COMMA)
            '<' -> KeyStroke(shift, HidConstants.KEY_COMMA)
            '.' -> KeyStroke(none, HidConstants.KEY_DOT)
            '>' -> KeyStroke(shift, HidConstants.KEY_DOT)
            '/' -> KeyStroke(none, HidConstants.KEY_SLASH)
            '?' -> KeyStroke(shift, HidConstants.KEY_SLASH)

            else -> null // Unmapped character
        }
    }

    /**
     * Streams text safely over Bluetooth HID interrupt pipe to the Mac.
     * Prevents stuck keys, drops, and character collisions.
     *
     * Privacy: Text is processed strictly in memory and discarded immediately.
     */
    suspend fun streamTextToMac(
        text: CharSequence,
        hidManager: HidDeviceManager,
        onProgress: (sent: Int, total: Int) -> Unit = { _, _ -> }
    ): Boolean {
        val total = text.length
        if (total == 0) return true

        for (i in 0 until total) {
            if (!coroutineContext.isActive) return false

            val char = text[i]
            // Skip carriage returns, only process newlines
            if (char == '\r') continue

            val stroke = charToKeyStroke(char)
            if (stroke != null) {
                // Key Down
                hidManager.sendKeyDown(stroke.modifier, stroke.keycode)
                delay(18L) // macOS debounce registration window

                // Key Up
                hidManager.sendKeyUp(0x00)
                delay(18L) // Interval between consecutive keystrokes
            }

            onProgress(i + 1, total)
        }

        // Final safety check: ensure all buffers clear
        hidManager.releaseAllKeys()
        return true
    }
}

