package com.mattdrzazga.wificompanion

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mattdrzazga.wificompanion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var administrator: Administrator
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        administrator = Administrator(this)

        if (!administrator.isDeviceAdmin()) {
            log("This app is not a device admin. To access network connections run the following command to set your device as a device admin:\n${Adb.ADB_SET_ADMIN}")
        }

        setupUi()
        processIntent(intent)
    }

    private fun setupUi() {
        binding.finishButton.setOnClickListener { finish() }
        binding.removeAdminButton.setOnClickListener {
            removeDeviceAdmin()
            it.isEnabled = false
        }
        binding.startServiceButton.setOnClickListener {
            if (hasPostNotificationPermission()) {
                KeepAdbWifiOnService.start(this)
            } else {
                requestPostNotificationPermission()
            }
        }
        binding.stopServiceButton.setOnClickListener {
            KeepAdbWifiOnService.stop(this)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.removeAdminButton.isEnabled = administrator.isDeviceAdmin()
    }

    private fun processIntent(intent: Intent) {
        // Clear device admin if needed
        val shouldClearDeviceAdmin = intent.extras?.containsKey(CLEAR_DEVICE_ADMIN) ?: false
        if (shouldClearDeviceAdmin) {
            if (removeDeviceAdmin()) {
                log("Removed device admin")
                finish()
                return
            }
        }

        val ssid: String? = intent.getStringExtra(ARG_SSID)
        val password: String? = intent.getStringExtra(ARG_PASSWORD)
        val security: String? = intent.getStringExtra(ARG_SECURITY)

        if (ssid != null && security == null && password == null) {
            log("Trying to connect to unprotected network $ssid")
//            val wifiManager = WifiManager(this)
//            wifiManager.connectToWifi(ssid, password)
//            finish()
        } else if (ssid != null && security != null && password != null) {
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

    private fun removeDeviceAdmin(): Boolean {
        if (administrator.isDeviceAdmin()) {
            administrator.removeDeviceOwnerApp()
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == POST_NOTIFICATION_PERMISSION_REQUEST_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            KeepAdbWifiOnService.start(this)
        }
        log("Granted permissions: [$requestCode] ${permissions.filterIndexed { index, _ -> grantResults[index] == PackageManager.PERMISSION_GRANTED }}")
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
    }
}