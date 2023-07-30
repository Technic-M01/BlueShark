package com.technic.blueshark.utils.bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.technic.blueshark.utils.RequestCodes
import java.util.UUID


@SuppressLint("MissingPermission")
class BluetoothManager(private val activity: Activity) {

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
    }

    //This filter will only show items in the scan
    // that match the provided advertised UUID.
    val filter = ScanFilter.Builder().setServiceUuid(
        ParcelUuid.fromString(MOCK_UUID.toString())
    ).build()



    inner class PermissionHandler(private val resultLauncher: ActivityResultLauncher<Intent>) {

        @SuppressLint("MissingPermission")
        private fun promptEnableBluetooth() {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                resultLauncher.launch(enableBtIntent)
            }
        }



    }


    companion object {
        val MOCK_UUID = UUID.fromString("")
    }

}