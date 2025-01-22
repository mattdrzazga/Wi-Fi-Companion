package com.mattdrzazga.wificompanion

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class BindAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        log("Device Admin Enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        log("Device Admin Disabled")
    }
}