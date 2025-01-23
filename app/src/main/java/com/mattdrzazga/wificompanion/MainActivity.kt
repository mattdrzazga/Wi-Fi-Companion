package com.mattdrzazga.wificompanion

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!Administrator(this).isDeviceAdmin()) {
            log("This app is not a device admin. Run the following command to set it as a device admin:\n$ADB_SET_ADMIN")
        }

        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        val administrator = Administrator(this)

        // Clear device admin if needed
        val shouldClearDeviceAdmin = intent.extras?.containsKey(CLEAR_DEVICE_ADMIN) ?: false
        if (shouldClearDeviceAdmin && administrator.isDeviceAdmin()) {
            administrator.removeDeviceOwnerApp()
            log("Removed device admin")
            finish()
            return
        }

        val ssid: String? = intent.getStringExtra(ARG_SSID)
        val password: String? = intent.getStringExtra(ARG_PASSWORD)
        val security: String? = intent.getStringExtra(ARG_SECURITY)

        if (ssid != null && security != null && password != null) {
            log("Trying to connect to unprotected network $ssid")
//            val wifiManager = WifiManager(this)
//            wifiManager.connectToWifi(ssid, password)
//            finish()
        } else if (ssid != null) {
            log("Trying to connect to network: $ssid")
//            val wifiManager = WifiManager(this)
//            wifiManager.connectToWifi(ssid)
//            finish()
        } else {
            log("Invalid arguments. Print help")
        }


        if (hasNetworkPermission()) {
            WifiManager(this).getWifiConfigurationFor("yourssid")
        } else {
            requestNetworkPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        log("Granted permissions: ${permissions.filterIndexed { index, _ -> grantResults[index] == PackageManager.PERMISSION_GRANTED }}")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        processIntent(intent)
    }

    companion object {

        const val CLEAR_DEVICE_ADMIN = "clear_device_admin"

        private const val ARG_SSID = "ssid"
        private const val ARG_PASSWORD = "password"
        private const val ARG_SECURITY = "security"

        private const val CMD_CLEAR_ADMIN = "adb shell am start -n com.mattdrzazga.wificompanion/.MainActivity --ez clear_device_admin true"
        private const val CMD_WIFI_CONNECT = "adb shell am start -n com.mattdrzazga.wificompanion/.MainActivity --es ssid yourssid --es password yourpassword"

        private const val ADB_CLEAR_ADMIN = "adb shell dpm remove-active-admin com.mattdrzazga.wificompanion/.BindAdminReceiver"
        private const val ADB_SET_ADMIN = "adb shell dpm set-device-owner com.mattdrzazga.wificompanion/.BindAdminReceiver"
    }
}