package com.mackey.app.bluetooth

/**
 * Standard USB HID specifications for Keyboard and Consumer Control
 * as recognized natively by macOS.
 */
object HidConstants {

    const val REPORT_ID_KEYBOARD: Byte = 1
    const val REPORT_ID_CONSUMER: Byte = 2

    // Modifier bitmasks (Byte 0 in Keyboard Input Report)
    const val MODIFIER_NONE: Byte = 0x00
    const val MODIFIER_LEFT_CTRL: Byte = 0x01
    const val MODIFIER_LEFT_SHIFT: Byte = 0x02
    const val MODIFIER_LEFT_ALT: Byte = 0x04    // Mac Option (⌥)
    const val MODIFIER_LEFT_GUI: Byte = 0x08    // Mac Command (⌘)
    const val MODIFIER_RIGHT_CTRL: Byte = 0x10
    const val MODIFIER_RIGHT_SHIFT: Byte = 0x20
    const val MODIFIER_RIGHT_ALT: Byte = 0x40   // Mac Right Option (⌥)
    const val MODIFIER_RIGHT_GUI: Byte = 0x80.toByte() // Mac Right Command (⌘)

    // HID Keyboard / Keypad Usage Page (0x07) Keycodes
    const val KEY_NONE: Byte = 0x00

    // Letters
    const val KEY_A: Byte = 0x04
    const val KEY_B: Byte = 0x05
    const val KEY_C: Byte = 0x06
    const val KEY_D: Byte = 0x07
    const val KEY_E: Byte = 0x08
    const val KEY_F: Byte = 0x09
    const val KEY_G: Byte = 0x0A
    const val KEY_H: Byte = 0x0B
    const val KEY_I: Byte = 0x0C
    const val KEY_J: Byte = 0x0D
    const val KEY_K: Byte = 0x0E
    const val KEY_L: Byte = 0x0F
    const val KEY_M: Byte = 0x10
    const val KEY_N: Byte = 0x11
    const val KEY_O: Byte = 0x12
    const val KEY_P: Byte = 0x13
    const val KEY_Q: Byte = 0x14
    const val KEY_R: Byte = 0x15
    const val KEY_S: Byte = 0x16
    const val KEY_T: Byte = 0x17
    const val KEY_U: Byte = 0x18
    const val KEY_V: Byte = 0x19
    const val KEY_W: Byte = 0x1A
    const val KEY_X: Byte = 0x1B
    const val KEY_Y: Byte = 0x1C
    const val KEY_Z: Byte = 0x1D

    // Numbers 1-9, 0
    const val KEY_1: Byte = 0x1E
    const val KEY_2: Byte = 0x1F
    const val KEY_3: Byte = 0x20
    const val KEY_4: Byte = 0x21
    const val KEY_5: Byte = 0x22
    const val KEY_6: Byte = 0x23
    const val KEY_7: Byte = 0x24
    const val KEY_8: Byte = 0x25
    const val KEY_9: Byte = 0x26
    const val KEY_0: Byte = 0x27

    // Control & Editing
    const val KEY_RETURN: Byte = 0x28       // Enter / Return
    const val KEY_ESCAPE: Byte = 0x29       // Esc
    const val KEY_BACKSPACE: Byte = 0x2A    // Delete on Mac keyboard
    const val KEY_TAB: Byte = 0x2B          // Tab
    const val KEY_SPACE: Byte = 0x2C        // Spacebar
    const val KEY_MINUS: Byte = 0x2D        // - and _
    const val KEY_EQUAL: Byte = 0x2E        // = and +
    const val KEY_LEFTBRACE: Byte = 0x2F    // [ and {
    const val KEY_RIGHTBRACE: Byte = 0x30   // ] and }
    const val KEY_BACKSLASH: Byte = 0x31    // \ and |
    const val KEY_SEMICOLON: Byte = 0x33    // ; and :
    const val KEY_APOSTROPHE: Byte = 0x34   // ' and "
    const val KEY_GRAVE: Byte = 0x35        // ` and ~
    const val KEY_COMMA: Byte = 0x36        // , and <
    const val KEY_DOT: Byte = 0x37          // . and >
    const val KEY_SLASH: Byte = 0x38        // / and ?
    const val KEY_CAPSLOCK: Byte = 0x39     // Caps Lock

    // Function Keys
    const val KEY_F1: Byte = 0x3A
    const val KEY_F2: Byte = 0x3B
    const val KEY_F3: Byte = 0x3C
    const val KEY_F4: Byte = 0x3D
    const val KEY_F5: Byte = 0x3E
    const val KEY_F6: Byte = 0x3F
    const val KEY_F7: Byte = 0x40
    const val KEY_F8: Byte = 0x41
    const val KEY_F9: Byte = 0x42
    const val KEY_F10: Byte = 0x43
    const val KEY_F11: Byte = 0x44
    const val KEY_F12: Byte = 0x45

    // Navigation & Arrow Keys
    const val KEY_PRINT_SCREEN: Byte = 0x46
    const val KEY_SCROLL_LOCK: Byte = 0x47
    const val KEY_PAUSE: Byte = 0x48
    const val KEY_INSERT: Byte = 0x49
    const val KEY_HOME: Byte = 0x4A
    const val KEY_PAGEUP: Byte = 0x4B
    const val KEY_DELETE_FORWARD: Byte = 0x4C // Forward Delete (Mac fn+backspace)
    const val KEY_END: Byte = 0x4D
    const val KEY_PAGEDOWN: Byte = 0x4E
    const val KEY_RIGHT_ARROW: Byte = 0x4F
    const val KEY_LEFT_ARROW: Byte = 0x50
    const val KEY_DOWN_ARROW: Byte = 0x51
    const val KEY_UP_ARROW: Byte = 0x52

    // Consumer Control bitmasks (Report ID 2)
    const val CONSUMER_PLAY_PAUSE: Short = 0x0001
    const val CONSUMER_NEXT_TRACK: Short = 0x0002
    const val CONSUMER_PREV_TRACK: Short = 0x0004
    const val CONSUMER_STOP: Short = 0x0008
    const val CONSUMER_VOLUME_UP: Short = 0x0010
    const val CONSUMER_VOLUME_DOWN: Short = 0x0020
    const val CONSUMER_MUTE: Short = 0x0040
    const val CONSUMER_BRIGHTNESS_UP: Short = 0x0080
    const val CONSUMER_BRIGHTNESS_DOWN: Short = 0x0100

    /**
     * Complete USB/Bluetooth HID Report Descriptor compatible with macOS.
     * Report ID 1: Standard 8-byte Keyboard
     * Report ID 2: 2-byte Consumer Control
     */
    val HID_REPORT_DESCRIPTOR = byteArrayOf(
        // ==========================================
        // REPORT ID 1: KEYBOARD
        // ==========================================
        0x05.toByte(), 0x01.toByte(), // Usage Page (Generic Desktop)
        0x09.toByte(), 0x06.toByte(), // Usage (Keyboard)
        0xA1.toByte(), 0x01.toByte(), // Collection (Application)
        0x85.toByte(), REPORT_ID_KEYBOARD, // Report ID (1)

        // 8 Modifier bits (Left/Right Ctrl, Shift, Option, Command)
        0x05.toByte(), 0x07.toByte(), //   Usage Page (Key Codes)
        0x19.toByte(), 0xE0.toByte(), //   Usage Minimum (Left Control: 224)
        0x29.toByte(), 0xE7.toByte(), //   Usage Maximum (Right GUI: 231)
        0x15.toByte(), 0x00.toByte(), //   Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(), //   Logical Maximum (1)
        0x75.toByte(), 0x01.toByte(), //   Report Size (1 bit)
        0x95.toByte(), 0x08.toByte(), //   Report Count (8 bits)
        0x81.toByte(), 0x02.toByte(), //   Input (Data, Variable, Absolute) - Modifier byte

        // Reserved / padding byte
        0x75.toByte(), 0x08.toByte(), //   Report Size (8 bits)
        0x95.toByte(), 0x01.toByte(), //   Report Count (1)
        0x81.toByte(), 0x01.toByte(), //   Input (Constant) - Reserved

        // LED indicators (Caps Lock, Num Lock, Scroll Lock)
        0x05.toByte(), 0x08.toByte(), //   Usage Page (LEDs)
        0x19.toByte(), 0x01.toByte(), //   Usage Minimum (1: Num Lock)
        0x29.toByte(), 0x05.toByte(), //   Usage Maximum (5: Kana)
        0x15.toByte(), 0x00.toByte(), //   Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(), //   Logical Maximum (1)
        0x75.toByte(), 0x01.toByte(), //   Report Size (1 bit)
        0x95.toByte(), 0x05.toByte(), //   Report Count (5 bits)
        0x91.toByte(), 0x02.toByte(), //   Output (Data, Variable, Absolute)
        0x75.toByte(), 0x03.toByte(), //   Report Size (3 bits padding)
        0x95.toByte(), 0x01.toByte(), //   Report Count (1)
        0x91.toByte(), 0x01.toByte(), //   Output (Constant)

        // Key array: Up to 6 simultaneous key presses
        0x05.toByte(), 0x07.toByte(), //   Usage Page (Key Codes)
        0x19.toByte(), 0x00.toByte(), //   Usage Minimum (0)
        0x29.toByte(), 0x65.toByte(), //   Usage Maximum (101: Up to F12 / Arrows)
        0x15.toByte(), 0x00.toByte(), //   Logical Minimum (0)
        0x25.toByte(), 0x65.toByte(), //   Logical Maximum (101)
        0x75.toByte(), 0x08.toByte(), //   Report Size (8 bits / 1 byte per key)
        0x95.toByte(), 0x06.toByte(), //   Report Count (6 key slots)
        0x81.toByte(), 0x00.toByte(), //   Input (Data, Array) - Key array
        0xC0.toByte(),                // End Collection

        // ==========================================
        // REPORT ID 2: CONSUMER CONTROL (Media / Brightness)
        // ==========================================
        0x05.toByte(), 0x0C.toByte(), // Usage Page (Consumer)
        0x09.toByte(), 0x01.toByte(), // Usage (Consumer Control)
        0xA1.toByte(), 0x01.toByte(), // Collection (Application)
        0x85.toByte(), REPORT_ID_CONSUMER, // Report ID (2)
        0x15.toByte(), 0x00.toByte(), //   Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(), //   Logical Maximum (1)
        0x75.toByte(), 0x01.toByte(), //   Report Size (1 bit)
        0x95.toByte(), 0x10.toByte(), //   Report Count (16 bits)
        0x09.toByte(), 0xCD.toByte(), //   Play/Pause
        0x09.toByte(), 0xB5.toByte(), //   Scan Next Track
        0x09.toByte(), 0xB6.toByte(), //   Scan Previous Track
        0x09.toByte(), 0xB7.toByte(), //   Stop
        0x09.toByte(), 0xE9.toByte(), //   Volume Increment
        0x09.toByte(), 0xEA.toByte(), //   Volume Decrement
        0x09.toByte(), 0xE2.toByte(), //   Mute
        0x09.toByte(), 0x6F.toByte(), //   Brightness Increment
        0x09.toByte(), 0x70.toByte(), //   Brightness Decrement
        0x09.toByte(), 0x00.toByte(), //   Padding bit 9
        0x09.toByte(), 0x00.toByte(), //   Padding bit 10
        0x09.toByte(), 0x00.toByte(), //   Padding bit 11
        0x09.toByte(), 0x00.toByte(), //   Padding bit 12
        0x09.toByte(), 0x00.toByte(), //   Padding bit 13
        0x09.toByte(), 0x00.toByte(), //   Padding bit 14
        0x09.toByte(), 0x00.toByte(), //   Padding bit 15
        0x81.toByte(), 0x02.toByte(), //   Input (Data, Variable, Absolute)
        0xC0.toByte()                 // End Collection
    )
}

