package com.mackey.app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mackey.app.ui.screens.MainScreen
import com.mackey.app.ui.theme.MacKeyTheme
import com.mackey.app.viewmodel.KeyboardViewModel
import com.mackey.app.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val keyboardViewModel: KeyboardViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions updated, re-initialize Bluetooth HID proxy
        keyboardViewModel.hidManager.initialize()
    }

    private val bluetoothEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        keyboardViewModel.hidManager.initialize()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestBluetoothPermissions()

        setContent {
            val isDark by settingsViewModel.isDarkMode.collectAsState()

            MacKeyTheme(darkTheme = isDark) {
                MainScreen(
                    keyboardViewModel = keyboardViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        permissionLauncher.launch(permissions.toTypedArray())

        val adapter = keyboardViewModel.hidManager.bluetoothAdapter
        if (adapter != null && !adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothEnableLauncher.launch(enableBtIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        keyboardViewModel.hidManager.initialize()
    }

    override fun onPause() {
        super.onPause()
        // Release any held keys when app goes into background
        keyboardViewModel.hidManager.releaseAllKeys()
    }
}

