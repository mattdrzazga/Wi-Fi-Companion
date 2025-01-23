package com.mattdrzazga.wificompanion

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat

const val PERMISSION_REQUEST_CODE = 10001

fun Context.hasNetworkPermission(): Boolean {
    val fineLocationGranted = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
    val accessWifiStateGranted = ActivityCompat.checkSelfPermission(this, ACCESS_WIFI_STATE) == PERMISSION_GRANTED
    return fineLocationGranted && accessWifiStateGranted
}

fun Activity.requestNetworkPermission() {
    this.requestPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE), PERMISSION_REQUEST_CODE)
}