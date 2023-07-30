package com.technic.blueshark

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.technic.blueshark.utils.RequestCodes.ENABLE_BLUETOOTH_REQUEST_CODE
import com.technic.blueshark.utils.RequestCodes.RUNTIME_PERMISSION_REQUEST_CODE
import com.technic.blueshark.utils.hasRequiredRuntimePermissions
import com.technic.blueshark.utils.requestRelevantRuntimePermissions

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    //Could use higher power scan settings
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            with (result.device) {
                Log.i("ScanCallback", "Found BLE device\n\tName: ${name ?: "Unnamed"}\n\tAddress: $address")
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan() {
        if (!hasRequiredRuntimePermissions()) {
            requestRelevantRuntimePermissions()
        } else {
            bleScanner.startScan(null, scanSettings, scanCallback)
        }
    }


    private fun verifyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
            Log.d("TestPermissions", "requestBluetooth: GRANTED")
        } else {
            //deny
            Log.d("TestPermissions", "requestBluetooth: DENIED")
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.d("TestPermissions", "${it.key} = ${it.value}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("Navigation", "destination: ${destination.label}")
        }

        //ToDo: figure out why this is broken
//        setupActionBarWithNavController(navController)

        verifyPermission()
    }

    override fun onResume() {
        super.onResume()

        if(!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp() // short circuit expression
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RUNTIME_PERMISSION_REQUEST_CODE -> {
                val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any() {
                    it.second == PackageManager.PERMISSION_DENIED
                            && !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
                }

                val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                when {

                    containsPermanentDenial -> {
                        //ToDo: handle permanent denial (e.g., show AlertDialog with justification)
                        // Note: the user will need to navigate to App Settings and manually grant
                        // permissions that were permanently denied
                    }
                    containsDenial -> {
                        requestRelevantRuntimePermissions()
                    }
                    allGranted && hasRequiredRuntimePermissions() -> {
                        //ToDo: startBleScan (maybe trigger an observable)
                        startBleScan()
                    }
                    else -> {
                        //Unexpected scenario encountered when handling permissions
                        recreate()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBtIntent)
        }
    }

    var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        /*if (result.resultCode == Activity.RESULT_OK) {
            //there are no request codes
            val data: Intent? = result.data
            //ToDo: do some operation
        }*/

        var resCode = result.resultCode

        when (resCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
            Activity.RESULT_OK -> {
                //ToDo: Do some operation?
            }
        }

    }

    companion object {

    }
}