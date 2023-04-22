package com.technic.blueshark.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.technic.blueshark.utils.RequestCodes.RUNTIME_PERMISSION_REQUEST_CODE

internal object RequestCodes {
    const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    const val RUNTIME_PERMISSION_REQUEST_CODE = 2
}

fun Context.hasPermission(permissionType: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permissionType) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasRequiredRuntimePermissions(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

fun Activity.requestRelevantRuntimePermissions() {
    if (hasRequiredRuntimePermissions()) { return }

    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
            requestLocationPermission()
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            requestBluetoothPermissions()
        }
    }

}

private fun Activity.requestLocationPermission() {
    runOnUiThread {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission Required")
        builder.setMessage("Starting from Android M (6.0), the system requires apps to be granted\nlocation access in order to scan for BLE devices.")
        builder.setCancelable(false)

        builder.setPositiveButton("Ok") { _, _ ->
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                RUNTIME_PERMISSION_REQUEST_CODE
            )
        }

        builder.show()
    }
}

private fun Activity.requestBluetoothPermissions() {
    runOnUiThread {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bluetooth Permissions Required")
        builder.setMessage("Starting from Android 12, the system requires apps to be granted\nBluetooth access in order to scan for and connect to BLE devices.")
        builder.setCancelable(false)

        builder.setPositiveButton("Ok") { _, _ ->
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                RUNTIME_PERMISSION_REQUEST_CODE
            )
        }

        builder.show()
    }
}