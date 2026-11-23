package com.mackey.app.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * High-performance Bluetooth HID Device Manager.
 * Registers the Android phone as an authentic USB/Bluetooth HID Keyboard for macOS.
 */
class HidDeviceManager(private val context: Context) {

    companion object {
        private const val TAG = "HidDeviceManager"
        private const val DEVICE_NAME = "MacKey Keyboard"
        private const val DEVICE_DESCRIPTION = "Wireless Mac Keyboard"
        private const val DEVICE_PROVIDER = "MacKey"
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private var hidDevice: BluetoothHidDevice? = null
    private var isAppRegistered = false
    private val executor = Executors.newSingleThreadExecutor()
    private val hidDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _capsLockActive = MutableStateFlow(false)
    val capsLockActive: StateFlow<Boolean> = _capsLockActive.asStateFlow()

    private var currentHostDevice: BluetoothDevice? = null
    private var lastHostDevice: BluetoothDevice? = null
    private var isExplicitDisconnect = false

    // Lock to prevent concurrent sendReport collisions over the Bluetooth interrupt pipe
    private val reportLock = Any()

    private val profileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.d(TAG, "BluetoothProfile.HID_DEVICE connected.")
                hidDevice = proxy as BluetoothHidDevice
                registerHidApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.d(TAG, "BluetoothProfile.HID_DEVICE disconnected.")
                hidDevice = null
                isAppRegistered = false
                _connectionState.value = ConnectionState.Disconnected
            }
        }
    }

    private val hidCallback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            super.onAppStatusChanged(pluggedDevice, registered)
            isAppRegistered = registered
            Log.d(TAG, "onAppStatusChanged: registered = $registered, device = ${pluggedDevice?.name}")
            if (!registered) {
                _connectionState.value = ConnectionState.Error("HID App registration failed or unregistered.")
            } else if (pluggedDevice != null) {
                currentHostDevice = pluggedDevice
                _connectionState.value = ConnectionState.Connected(pluggedDevice)
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            super.onConnectionStateChanged(device, state)
            Log.d(TAG, "onConnectionStateChanged: device = ${device?.name}, state = $state")
            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    currentHostDevice = device
                    if (device != null) {
                        lastHostDevice = device
                        isExplicitDisconnect = false
                        _connectionState.value = ConnectionState.Connected(device)
                    }
                }
                BluetoothProfile.STATE_CONNECTING -> {
                    _connectionState.value = ConnectionState.Connecting(device)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    if (currentHostDevice == device) {
                        currentHostDevice = null
                    }
                    _connectionState.value = ConnectionState.Disconnected
                    releaseAllKeys()

                    // Auto-reconnect if connection was dropped unexpectedly and Bluetooth is on
                    if (!isExplicitDisconnect && bluetoothAdapter?.isEnabled == true && lastHostDevice != null) {
                        val hostToReconnect = lastHostDevice
                        scope.launch {
                            delay(2000L)
                            if (currentHostDevice == null && hostToReconnect != null && !isExplicitDisconnect) {
                                Log.d(TAG, "Attempting auto-reconnect to ${hostToReconnect.name}")
                                connect(hostToReconnect)
                            }
                        }
                    }
                }
                BluetoothProfile.STATE_DISCONNECTING -> {
                    _connectionState.value = ConnectionState.Disconnecting
                }
            }
        }

        override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
            super.onGetReport(device, type, id, bufferSize)
            // Respond with empty report if queried by macOS host
            if (device != null && hidDevice != null) {
                val emptyData = ByteArray(8)
                hidDevice?.replyReport(device, type, id, emptyData)
            }
        }

        override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
            super.onSetReport(device, type, id, data)
            // macOS sends LED state (Num Lock: 0x01, Caps Lock: 0x02, Scroll Lock: 0x04)
            if (data != null && data.isNotEmpty()) {
                val ledState = data[0].toInt()
                val isCaps = (ledState and 0x02) != 0
                _capsLockActive.value = isCaps
                Log.d(TAG, "Host sent LED update: CapsLock = $isCaps")
            }
        }
    }

    /**
     * Initializes the Bluetooth HID Proxy. Call during App startup.
     */
    fun initialize() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            _connectionState.value = ConnectionState.NotSupported
            return
        }

        if (!adapter.isEnabled) {
            _connectionState.value = ConnectionState.BluetoothDisabled
            return
        }

        val missing = checkMissingPermissions()
        if (missing.isNotEmpty()) {
            _connectionState.value = ConnectionState.PermissionDenied(missing)
            return
        }

        try {
            val success = adapter.getProfileProxy(context, profileListener, BluetoothProfile.HID_DEVICE)
            if (!success) {
                Log.e(TAG, "getProfileProxy returned false. HID Device profile not supported.")
                _connectionState.value = ConnectionState.NotSupported
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get HID proxy", e)
            _connectionState.value = ConnectionState.Error(e.message ?: "Failed to initialize HID")
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerHidApp() {
        val device = hidDevice ?: return
        if (isAppRegistered) return

        val sdpSettings = BluetoothHidDeviceAppSdpSettings(
            DEVICE_NAME,
            DEVICE_DESCRIPTION,
            DEVICE_PROVIDER,
            BluetoothHidDevice.SUBCLASS1_KEYBOARD,
            HidConstants.HID_REPORT_DESCRIPTOR
        )

        val qosSettings = BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
            800,
            9,
            0,
            10000,
            -1
        )

        try {
            val registered = device.registerApp(
                sdpSettings,
                null,
                qosSettings,
                executor,
                hidCallback
            )
            Log.d(TAG, "registerApp returned: $registered")
            if (!registered) {
                _connectionState.value = ConnectionState.Error("Failed to register HID Keyboard profile with Bluetooth stack.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during registerApp", e)
            _connectionState.value = ConnectionState.Error(e.localizedMessage ?: "Registration error")
        }
    }

    /**
     * Checks if all required Bluetooth permissions are granted.
     */
    fun checkMissingPermissions(): List<String> {
        val missing = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                missing.add(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                missing.add(android.Manifest.permission.BLUETOOTH_ADVERTISE)
            }
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                missing.add(android.Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                missing.add(android.Manifest.permission.BLUETOOTH)
            }
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                missing.add(android.Manifest.permission.BLUETOOTH_ADMIN)
            }
        }
        return missing
    }

    /**
     * Connect to a specific paired Mac device.
     */
    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        val hid = hidDevice ?: return
        currentHostDevice = device
        _connectionState.value = ConnectionState.Connecting(device)
        try {
            val result = hid.connect(device)
            Log.d(TAG, "connect() returned: $result to ${device.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to device", e)
            _connectionState.value = ConnectionState.Error("Connection request failed: ${e.message}")
        }
    }

    /**
     * Disconnects the currently connected Mac.
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        isExplicitDisconnect = true
        val hid = hidDevice
        val host = currentHostDevice
        if (hid != null && host != null) {
            _connectionState.value = ConnectionState.Disconnecting
            releaseAllKeys()
            try {
                hid.disconnect(host)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to disconnect", e)
            }
        }
        currentHostDevice = null
        _connectionState.value = ConnectionState.Disconnected
    }

    /**
     * Retrieves currently bonded / paired devices.
     */
    @SuppressLint("MissingPermission")
    fun getBondedDevices(): List<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }

    /**
     * Sends an 8-byte standard keyboard report to the connected Mac.
     * Offloaded asynchronously to a dedicated single-threaded dispatcher to prevent
     * UI thread blocking, eliminating touch lag and skipped frames.
     */
    @SuppressLint("MissingPermission")
    fun sendKeyboardReport(modifier: Byte, keycodes: ByteArray) {
        val host = currentHostDevice ?: return
        val hid = hidDevice ?: return

        val payload = ByteArray(8)
        payload[0] = modifier
        payload[1] = 0x00 // Reserved padding
        for (i in 0 until minOf(keycodes.size, 6)) {
            payload[2 + i] = keycodes[i]
        }

        scope.launch(hidDispatcher) {
            synchronized(reportLock) {
                try {
                    hid.sendReport(host, HidConstants.REPORT_ID_KEYBOARD.toInt(), payload)
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending keyboard report", e)
                }
            }
        }
    }

    /**
     * Presses a key down with the specified modifier bitmask.
     */
    fun sendKeyDown(modifier: Byte, keycode: Byte) {
        sendKeyboardReport(modifier, byteArrayOf(keycode))
    }

    /**
     * Releases pressed keys. Can optionally preserve active modifier.
     */
    fun sendKeyUp(modifier: Byte = 0x00) {
        sendKeyboardReport(modifier, ByteArray(0))
    }

    /**
     * Sends an authentic Mac keystroke sequence:
     * For modifier combos (e.g. ⌘C, ⌘Space):
     * 1. Assert modifier down (Command)
     * 2. Press key while modifier is held (Command + C)
     * 3. Release key while modifier is still held
     * 4. Release modifier
     *
     * This exact 4-step sequence ensures macOS window server, event tap,
     * and global shortcuts (Spotlight, App Switcher, Copy/Paste) register 100% reliably.
     */
    fun sendKeyStroke(modifier: Byte, keycode: Byte, holdMs: Long = 35L, onComplete: (() -> Unit)? = null) {
        scope.launch {
            if (modifier != HidConstants.MODIFIER_NONE) {
                // Step 1: Modifier Down
                sendKeyboardReport(modifier, ByteArray(0))
                delay(15L)
                // Step 2: Key Down with Modifier
                sendKeyboardReport(modifier, byteArrayOf(keycode))
                delay(holdMs)
                // Step 3: Key Up with Modifier held
                sendKeyboardReport(modifier, ByteArray(0))
                delay(15L)
                // Step 4: Modifier Up
                sendKeyboardReport(HidConstants.MODIFIER_NONE, ByteArray(0))
            } else {
                sendKeyboardReport(HidConstants.MODIFIER_NONE, byteArrayOf(keycode))
                delay(holdMs)
                sendKeyboardReport(HidConstants.MODIFIER_NONE, ByteArray(0))
            }
            onComplete?.invoke()
        }
    }

    /**
     * Sends a 2-byte Consumer Control report (Media keys, Volume, Brightness).
     */
    @SuppressLint("MissingPermission")
    fun sendConsumerKey(consumerMask: Short, holdMs: Long = 30L) {
        val host = currentHostDevice ?: return
        val hid = hidDevice ?: return

        scope.launch {
            val pressPayload = byteArrayOf(
                (consumerMask.toInt() and 0xFF).toByte(),
                ((consumerMask.toInt() shr 8) and 0xFF).toByte()
            )
            val releasePayload = byteArrayOf(0x00, 0x00)

            synchronized(reportLock) {
                try {
                    hid.sendReport(host, HidConstants.REPORT_ID_CONSUMER.toInt(), pressPayload)
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending consumer report", e)
                }
            }

            delay(holdMs)

            synchronized(reportLock) {
                try {
                    hid.sendReport(host, HidConstants.REPORT_ID_CONSUMER.toInt(), releasePayload)
                } catch (e: Exception) {
                    Log.e(TAG, "Error releasing consumer report", e)
                }
            }
        }
    }

    /**
     * Release all keys and reset keyboard buffer on the Mac.
     * Prevents stuck keys if connection drops or screen unmounts.
     */
    @SuppressLint("MissingPermission")
    fun releaseAllKeys() {
        val host = currentHostDevice ?: return
        val hid = hidDevice ?: return

        synchronized(reportLock) {
            try {
                // Clear Keyboard Report
                hid.sendReport(host, HidConstants.REPORT_ID_KEYBOARD.toInt(), ByteArray(8))
                // Clear Consumer Report
                hid.sendReport(host, HidConstants.REPORT_ID_CONSUMER.toInt(), ByteArray(2))
            } catch (e: Exception) {
                Log.w(TAG, "Could not send release report: ${e.message}")
            }
        }
    }

    /**
     * Clean up and release proxy.
     */
    fun destroy() {
        releaseAllKeys()
        try {
            hidDevice?.let { hid ->
                isAppRegistered = false
                hid.unregisterApp()
                bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hid)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying HidDeviceManager", e)
        }
        hidDevice = null
    }
}

