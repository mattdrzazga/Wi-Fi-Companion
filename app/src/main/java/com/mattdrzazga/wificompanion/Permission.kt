package com.mattdrzazga.wificompanion

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat

const val LOCATION_PERMISSION_REQUEST_CODE = 10000
const val POST_NOTIFICATION_PERMISSION_REQUEST_CODE = 10001

fun Context.hasNetworkPermission(): Boolean {
    val fineLocationGranted = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    val accessWifiStateGranted = ActivityCompat.checkSelfPermission(this, ACCESS_WIFI_STATE) == PERMISSION_GRANTED
    return fineLocationGranted && accessWifiStateGranted
}

fun Activity.requestNetworkPermission() =
    requestPermissions(
        arrayOf(ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE),
        LOCATION_PERMISSION_REQUEST_CODE
    )

fun Context.hasPostNotificationPermission(): Boolean {
    return ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED
}

fun Activity.requestPostNotificationPermission() {
    this.requestPermissions(arrayOf(POST_NOTIFICATIONS), POST_NOTIFICATION_PERMISSION_REQUEST_CODE)
}