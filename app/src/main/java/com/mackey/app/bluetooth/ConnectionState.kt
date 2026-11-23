package com.mackey.app.bluetooth

import android.bluetooth.BluetoothDevice

/**
 * Represents the current status of Bluetooth connection with the Mac host.
 */
sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data class Connecting(val device: BluetoothDevice? = null) : ConnectionState()
    data class Connected(val device: BluetoothDevice) : ConnectionState()
    data object Disconnecting : ConnectionState()
    data object BluetoothDisabled : ConnectionState()
    data object NotSupported : ConnectionState()
    data class PermissionDenied(val missingPermissions: List<String>) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

