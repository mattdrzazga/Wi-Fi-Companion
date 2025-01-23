package com.mattdrzazga.wificompanion

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

class WifiManager(context: Context) {

    private val wifiManager: WifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager


    @Suppress("DEPRECATION") // Device owner exempt
    var isWifiEnabled: Boolean
        get() = wifiManager.isWifiEnabled
        set(value) {
            wifiManager.isWifiEnabled = value
        }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION") // Device owner exempt
    fun getWifiConfigurationFor(ssid: String): WifiConfiguration? {
        log(wifiManager.configuredNetworks.toString())
        return wifiManager.configuredNetworks.firstOrNull { it.SSID == ssid }
    }

    @SuppressLint("MissingPermission")
    fun getScanResults(): List<ScanResult> {
        return wifiManager.scanResults
    }
}