package com.mattdrzazga.wificompanion

object Adb {

    private const val CMD_CLEAR_ADMIN = "adb shell am start -n com.mattdrzazga.wificompanion/.MainActivity --ez clear_device_admin true"
    private const val CMD_WIFI_CONNECT = "adb shell am start -n com.mattdrzazga.wificompanion/.MainActivity --es ssid yourssid --es password yourpassword"

    private const val ADB_CLEAR_ADMIN = "adb shell dpm remove-active-admin com.mattdrzazga.wificompanion/.BindAdminReceiver"
    const val ADB_SET_ADMIN = "adb shell dpm set-device-owner com.mattdrzazga.wificompanion/.BindAdminReceiver"

    private const val ADB_GRANT_WRITE_SECURE_SETTINGS = "adb shell pm grant com.mattdrzazga.wificompanion android.permission.WRITE_SECURE_SETTINGS"
}