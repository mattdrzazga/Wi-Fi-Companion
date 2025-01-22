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
        val administrator = Administrator(this)
        val shouldClearDeviceAdmin = intent.extras?.containsKey(CLEAR_DEVICE_ADMIN) ?: false
        if (shouldClearDeviceAdmin && administrator.isDeviceAdmin()) {
            administrator.removeDeviceOwnerApp()
            finish()
            return
        }
    }

    companion object {

        const val CLEAR_DEVICE_ADMIN = "clear_device_admin"
    }
}