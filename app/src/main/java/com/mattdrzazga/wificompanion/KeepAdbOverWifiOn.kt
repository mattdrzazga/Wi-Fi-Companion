package com.mattdrzazga.wificompanion

import android.content.ContentResolver
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class AdbOverWifiSetting(private val contentResolver: ContentResolver) {

    var isOn: Boolean
        get() = Settings.Global.getInt(contentResolver, ADB_WIFI_ENABLED, 0) == 1
        set(value) {
            Settings.Global.putInt(contentResolver, ADB_WIFI_ENABLED, if (value) 1 else 0)
        }
}

suspend fun keepAdbOverWifiOn(adbOverWifi: AdbOverWifiSetting): Unit = withContext(Dispatchers.IO) {
    var attempt = 0
    while (true) {
        ensureActive()

        if (adbOverWifi.isOn) {
            attempt = 0
            continue
        }

        if (attempt > MAX_RETRIES) {
            return@withContext
        }

        try {
            log(
                tag = TAG,
                message = "Adb over wifi, turned off, attempting to turn it back on."
            )
            adbOverWifi.isOn = true
            attempt = 0
        } catch (e: SecurityException) {
            ++attempt
            e.printStackTrace()
            log(
                tag = TAG,
                message = "java.lang.SecurityException: Permission denial, Grant 'android.permission.WRITE_SECURE_SETTINGS' permission with\n ${Adb.ADB_GRANT_WRITE_SECURE_SETTINGS}"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ++attempt
        }
        delay(delayForAttempt(attempt))
    }
}

private fun delayForAttempt(attempt: Int): Long {
    return when (attempt) {
        in 0..5 -> 500L
        in 6..11 -> 1000L
        else -> 2000L
    }
}

/** This value is annotated with @hide in the Android source code.
 * @see: [android.provider.Settings.Global.ADB_WIFI_ENABLED]
 * */
@Suppress("KDocUnresolvedReference")
private const val ADB_WIFI_ENABLED = "adb_wifi_enabled"
private const val MAX_RETRIES = 15
private const val TAG = "KeepAdbWifiOn"