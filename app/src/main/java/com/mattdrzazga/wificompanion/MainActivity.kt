package com.mattdrzazga.wificompanion

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mattdrzazga.wificompanion.Ops.ClearDeviceAdmin
import com.mattdrzazga.wificompanion.Ops.ForgetNetwork
import com.mattdrzazga.wificompanion.Ops.JoinNetwork
import com.mattdrzazga.wificompanion.Ops.NoOp
import com.mattdrzazga.wificompanion.Ops.StartAdbWifiKeeper
import com.mattdrzazga.wificompanion.Ops.StopAdbWifiKeeper
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
            checkPermissionAndStartAdbWifiService()
        }
        binding.stopServiceButton.setOnClickListener {
            stopAdbWifiService()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.removeAdminButton.isEnabled = administrator.isDeviceAdmin()
    }

    private fun processIntent(intent: Intent) {
        when (val op = Ops.fromIntent(intent.extras)) {
            ClearDeviceAdmin -> {
                // Clear device admin if needed
                if (removeDeviceAdmin()) {
                    log("Removed device admin")
                    finish()
                    return
                }
            }

            is ForgetNetwork -> {
                if (op.isValid) {
                    log("Forgetting network: '${op.ssid}'")
                    WifiManager(this).forgetNetwork(op.ssid)
                } else {
                    logw("Could not forget network, reason: ${op.error}")
                }
            }

            is JoinNetwork -> {
                log("Connecting to network: ${op.ssid}")
            }

            NoOp -> {
                log("Invalid arguments. Print help")
            }

            StartAdbWifiKeeper -> {
                log("Starting service")
                checkPermissionAndStartAdbWifiService()
            }

            StopAdbWifiKeeper -> {
                log("Stopping service")
                stopAdbWifiService()
            }
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

    private fun stopAdbWifiService() {
        KeepAdbWifiOnService.stop(this)
    }

    private fun checkPermissionAndStartAdbWifiService() {
        if (hasPostNotificationPermission()) {
            KeepAdbWifiOnService.start(this)
        } else {
            requestPostNotificationPermission()
        }
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
        const val START_KEEP_ADB_WIFI_ON = "start_adb_wifi_keeper"
        const val STOP_KEEP_ADB_WIFI_ON = "stop_adb_wifi_keeper"
        const val FORGET_NETWORK = "forget_network"
        const val JOIN_NETWORK = "join_network"

        private const val ARG_SSID = "ssid"
        private const val ARG_PASSWORD = "password"
        private const val ARG_SECURITY = "security"
    }
}