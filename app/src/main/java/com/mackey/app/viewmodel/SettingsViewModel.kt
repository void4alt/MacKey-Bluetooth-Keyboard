package com.mackey.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.mackey.app.bluetooth.HidConstants
import com.mackey.app.model.ShortcutItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mackey_settings", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean("haptic_enabled", true))
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _hapticStrength = MutableStateFlow(prefs.getInt("haptic_strength", 2))
    val hapticStrength: StateFlow<Int> = _hapticStrength.asStateFlow()

    private val _rgbEnabled = MutableStateFlow(prefs.getBoolean("rgb_enabled", true))
    val rgbEnabled: StateFlow<Boolean> = _rgbEnabled.asStateFlow()

    private val _keySizeScale = MutableStateFlow(prefs.getFloat("key_size_scale", 1.0f))
    val keySizeScale: StateFlow<Float> = _keySizeScale.asStateFlow()

    private val _shortcuts = MutableStateFlow(ShortcutItem.DEFAULT_SHORTCUTS)
    val shortcuts: StateFlow<List<ShortcutItem>> = _shortcuts.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun setHapticEnabled(enabled: Boolean) {
        _hapticEnabled.value = enabled
        prefs.edit().putBoolean("haptic_enabled", enabled).apply()
    }

    fun setHapticStrength(strength: Int) {
        _hapticStrength.value = strength
        prefs.edit().putInt("haptic_strength", strength).apply()
    }

    fun setRgbEnabled(enabled: Boolean) {
        _rgbEnabled.value = enabled
        prefs.edit().putBoolean("rgb_enabled", enabled).apply()
    }

    fun setKeySizeScale(scale: Float) {
        _keySizeScale.value = scale
        prefs.edit().putFloat("key_size_scale", scale).apply()
    }

    fun addCustomShortcut(name: String, modifierMask: Byte, keycode: Byte, displayCombo: String) {
        val newItem = ShortcutItem(
            name = name,
            modifierMask = modifierMask,
            keycode = keycode,
            displayCombo = displayCombo,
            isPreset = false
        )
        val updated = _shortcuts.value.toMutableList().apply { add(newItem) }
        _shortcuts.value = updated
    }

    fun removeShortcut(id: String) {
        val updated = _shortcuts.value.filterNot { it.id == id }
        _shortcuts.value = updated
    }

    fun reorderShortcuts(fromIndex: Int, toIndex: Int) {
        val list = _shortcuts.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _shortcuts.value = list
        }
    }

    fun resetShortcutsToDefaults() {
        _shortcuts.value = ShortcutItem.DEFAULT_SHORTCUTS
    }
}

