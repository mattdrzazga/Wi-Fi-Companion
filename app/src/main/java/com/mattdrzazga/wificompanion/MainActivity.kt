package com.mattdrzazga.wificompanion

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
        // Clear device admin if needed
        val administrator = Administrator(this)
        val shouldClearDeviceAdmin = intent.extras?.containsKey(CLEAR_DEVICE_ADMIN) ?: false
        if (shouldClearDeviceAdmin && administrator.isDeviceAdmin()) {
            administrator.removeDeviceOwnerApp()
            log("Removed device admin")
            finish()
            return
        }
    }

    companion object {

        const val CLEAR_DEVICE_ADMIN = "clear_device_admin"

        private const val CMD_CLEAR_ADMIN = "adb shell am start -n com.mattdrzazga.wificompanion/.MainActivity --ez clear_device_admin true"

        private const val ADB_CLEAR_ADMIN = "adb shell dpm remove-active-admin com.mattdrzazga.wificompanion/.BindAdminReceiver"
        private const val ADB_SET_ADMIN = "adb shell dpm set-device-owner com.mattdrzazga.wificompanion/.BindAdminReceiver"
    }
}