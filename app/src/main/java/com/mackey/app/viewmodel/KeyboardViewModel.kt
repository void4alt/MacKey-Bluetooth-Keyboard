package com.mackey.app.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mackey.app.bluetooth.ConnectionState
import com.mackey.app.bluetooth.HidConstants
import com.mackey.app.bluetooth.HidDeviceManager
import com.mackey.app.bluetooth.MacKeyMapper
import com.mackey.app.model.KeyItem
import com.mackey.app.model.KeyType
import com.mackey.app.model.ShortcutItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KeyboardViewModel(application: Application) : AndroidViewModel(application) {

    val hidManager = HidDeviceManager(application.applicationContext)
    val connectionState: StateFlow<ConnectionState> = hidManager.connectionState

    // Caps Lock state (combines host LED feedback + local toggle)
    private val _capsLockState = MutableStateFlow(false)
    val capsLockActive: StateFlow<Boolean> = _capsLockState.asStateFlow()

    // Modifier states
    // Latched: active for the next normal key press, then clears automatically
    private val _latchedModifiers = MutableStateFlow<Byte>(HidConstants.MODIFIER_NONE)
    val latchedModifiers: StateFlow<Byte> = _latchedModifiers.asStateFlow()

    // Locked: permanently held active (double-tapped) until tapped again
    private val _lockedModifiers = MutableStateFlow<Byte>(HidConstants.MODIFIER_NONE)
    val lockedModifiers: StateFlow<Byte> = _lockedModifiers.asStateFlow()

    // Fn layer toggle (shows F1-F12 and Mac Consumer keys)
    private val _fnActive = MutableStateFlow(false)
    val fnActive: StateFlow<Boolean> = _fnActive.asStateFlow()

    // Text Input Mode state
    private val _isSendingText = MutableStateFlow(false)
    val isSendingText: StateFlow<Boolean> = _isSendingText.asStateFlow()

    private val _sendProgress = MutableStateFlow(0f)
    val sendProgress: StateFlow<Float> = _sendProgress.asStateFlow()

    private var textStreamJob: Job? = null

    private val vibrator = application.getSystemService(Vibrator::class.java)

    init {
        hidManager.initialize()
        // Sync CapsLock with host LED reports from macOS
        viewModelScope.launch {
            hidManager.capsLockActive.collect { hostCaps ->
                _capsLockState.value = hostCaps
            }
        }
    }

    fun toggleCapsLock() {
        val newState = !_capsLockState.value
        _capsLockState.value = newState
        triggerHaptic(2)
        hidManager.sendKeyStroke(HidConstants.MODIFIER_NONE, HidConstants.KEY_CAPSLOCK, holdMs = 40L)
    }

    fun triggerHaptic(strength: Int = 1) {
        if (vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitude = when (strength) {
                    1 -> 35
                    2 -> 90
                    3 -> 180
                    else -> 60
                }
                vibrator.vibrate(VibrationEffect.createOneShot(12L, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(12L)
            }
        } catch (_: Exception) {}
    }

    /**
     * Handles normal key press down.
     */
    fun onKeyDown(key: KeyItem, hapticStrength: Int = 1) {
        triggerHaptic(hapticStrength)

        if (key.type == KeyType.MODIFIER) {
            handleModifierTap(key.modifierBit)
            return
        }

        val effectiveModifier = (_latchedModifiers.value.toInt() or _lockedModifiers.value.toInt()).toByte()

        if (key.keycode != HidConstants.KEY_NONE) {
            hidManager.sendKeyDown(effectiveModifier, key.keycode)
        }
    }

    /**
     * Handles key release up. Clears latched (single-press) modifiers cleanly after key release.
     */
    fun onKeyUp(key: KeyItem) {
        if (key.type == KeyType.MODIFIER) return

        val hadLatched = _latchedModifiers.value != HidConstants.MODIFIER_NONE
        val currentMod = (_latchedModifiers.value.toInt() or _lockedModifiers.value.toInt()).toByte()

        // Release the key first while keeping the modifier asserted
        hidManager.sendKeyUp(currentMod)

        if (hadLatched) {
            viewModelScope.launch {
                delay(20L)
                _latchedModifiers.value = HidConstants.MODIFIER_NONE
                val lockedOnly = _lockedModifiers.value
                hidManager.sendKeyUp(lockedOnly)
            }
        }
    }

    /**
     * Tap modifier logic:
     * - If locked, turn off.
     * - If latched, lock it (double tap).
     * - If off, latch it.
     */
    fun handleModifierTap(modifierBit: Byte) {
        val isLocked = (_lockedModifiers.value.toInt() and modifierBit.toInt()) != 0
        val isLatched = (_latchedModifiers.value.toInt() and modifierBit.toInt()) != 0

        when {
            isLocked -> {
                // Unlock
                _lockedModifiers.value = (_lockedModifiers.value.toInt() and modifierBit.toInt().inv()).toByte()
                _latchedModifiers.value = (_latchedModifiers.value.toInt() and modifierBit.toInt().inv()).toByte()
            }
            isLatched -> {
                // Lock
                _latchedModifiers.value = (_latchedModifiers.value.toInt() and modifierBit.toInt().inv()).toByte()
                _lockedModifiers.value = (_lockedModifiers.value.toInt() or modifierBit.toInt()).toByte()
            }
            else -> {
                // Latch for next stroke
                _latchedModifiers.value = (_latchedModifiers.value.toInt() or modifierBit.toInt()).toByte()
            }
        }

        // Send modifier update to host
        val currentMod = (_latchedModifiers.value.toInt() or _lockedModifiers.value.toInt()).toByte()
        hidManager.sendKeyUp(currentMod)
    }

    fun toggleFn() {
        _fnActive.value = !_fnActive.value
        triggerHaptic(1)
    }

    /**
     * Executes a Mac shortcut atomically.
     */
    fun triggerShortcut(shortcut: ShortcutItem, hapticStrength: Int = 2) {
        triggerHaptic(hapticStrength)
        hidManager.sendKeyStroke(shortcut.modifierMask, shortcut.keycode)
        _latchedModifiers.value = HidConstants.MODIFIER_NONE
    }

    /**
     * Executes a consumer key (Volume, Brightness, Play/Pause).
     */
    fun triggerConsumerKey(consumerMask: Short, hapticStrength: Int = 1) {
        triggerHaptic(hapticStrength)
        hidManager.sendConsumerKey(consumerMask)
    }

    /**
     * Streams text from dedicated Text Input Mode to the Mac.
     */
    fun sendBatchText(text: String, onFinished: (Boolean) -> Unit) {
        if (_isSendingText.value || text.isEmpty()) return

        _isSendingText.value = true
        _sendProgress.value = 0f

        textStreamJob = viewModelScope.launch {
            val success = MacKeyMapper.streamTextToMac(
                text = text,
                hidManager = hidManager,
                onProgress = { sent, total ->
                    _sendProgress.value = if (total > 0) sent.toFloat() / total else 0f
                }
            )
            _isSendingText.value = false
            onFinished(success)
        }
    }

    fun cancelTextStream() {
        textStreamJob?.cancel()
        textStreamJob = null
        _isSendingText.value = false
        hidManager.releaseAllKeys()
    }

    fun clearAllModifiers() {
        _latchedModifiers.value = HidConstants.MODIFIER_NONE
        _lockedModifiers.value = HidConstants.MODIFIER_NONE
        hidManager.releaseAllKeys()
    }

    override fun onCleared() {
        super.onCleared()
        hidManager.destroy()
    }
}

