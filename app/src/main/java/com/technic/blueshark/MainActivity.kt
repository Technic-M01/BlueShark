package com.technic.blueshark

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.technic.blueshark.utils.RequestCodes.ENABLE_BLUETOOTH_REQUEST_CODE
import com.technic.blueshark.utils.RequestCodes.RUNTIME_PERMISSION_REQUEST_CODE
import com.technic.blueshark.utils.hasRequiredRuntimePermissions
import com.technic.blueshark.utils.requestRelevantRuntimePermissions

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

/*    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()

/*        if(!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }*/
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp() // short circuit expression
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
//                    promptEnableBluetooth()
                }
            }
        }
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
/*    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
//            resultLauncher.launch(enableBtIntent)
        }
    }*/

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        /*if (result.resultCode == Activity.RESULT_OK) {
            //there are no request codes
            val data: Intent? = result.data
            //ToDo: do some operation
        }*/

        var resCode = result.resultCode

        when (resCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resCode != Activity.RESULT_OK) {
//                    promptEnableBluetooth()
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