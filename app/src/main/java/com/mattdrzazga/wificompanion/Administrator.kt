package com.mattdrzazga.wificompanion

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import androidx.core.content.getSystemService

class Administrator(private val context: Context) {

    private val devicePolicyManager: DevicePolicyManager =
        requireNotNull(context.getSystemService<DevicePolicyManager>())


    fun isDeviceAdmin(): Boolean {
        val isAdminActive = devicePolicyManager.isAdminActive(ComponentName(context, BindAdminReceiver::class.java))
        val isDeviceOwnerApp = devicePolicyManager.isDeviceOwnerApp(context.packageName)
        return isAdminActive && isDeviceOwnerApp
    }

    fun removeDeviceOwnerApp() {
        devicePolicyManager.clearDeviceOwnerApp(context.packageName)
    }
}