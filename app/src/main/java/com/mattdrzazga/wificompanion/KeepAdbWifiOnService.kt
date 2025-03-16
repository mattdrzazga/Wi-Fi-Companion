package com.mattdrzazga.wificompanion

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KeepAdbWifiOnService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        log(tag = TAG, message = "onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()

        log(tag = TAG, message = "onCreate")
        scope.launch {
            keepAdbOverWifiOn()
        }
    }

    private fun startForeground() {
        val notificationCreator = NotificationCreator(this)
        notificationCreator.createNotificationChannel()
        val notification = notificationCreator.createForegroundServiceNotification()
        startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        log(tag = TAG, message = "onDestroy")
    }

    private suspend fun keepAdbOverWifiOn() = withContext(Dispatchers.IO) {
        var attempt = 0
        while (true) {
            ensureActive()

            val isOn = Settings.Global.getInt(contentResolver, ADB_WIFI_ENABLED, 0) == 1
            if (isOn) {
                attempt = 0
                continue
            }

            if (attempt > MAX_RETRIES) {
                log(tag = TAG, message = "Stopping service due to inactivity.")
                stopSelf()
                break
            }

            try {
                log(
                    tag = TAG,
                    message = "Adb over wifi, turned off, attempting to turn it back on."
                )
                Settings.Global.putInt(contentResolver, ADB_WIFI_ENABLED, 1)
                attempt = 0
            } catch (e: SecurityException) {
                ++attempt
                log(
                    tag = TAG,
                    message = "java.lang.SecurityException: Permission denial, must have one of: [android.permission.WRITE_SECURE_SETTINGS]"
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

    companion object {

        private const val TAG = "KeepAdbWifiOnService"
        private const val FOREGROUND_SERVICE_NOTIFICATION_ID = 30000

        /** This value is annotated with @hide in the Android source code.
         * @see: [android.provider.Settings.Global.ADB_WIFI_ENABLED]
         * */
        @Suppress("KDocUnresolvedReference")
        private const val ADB_WIFI_ENABLED = "adb_wifi_enabled"

        private const val MAX_RETRIES = 15

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, KeepAdbWifiOnService::class.java)
            ContextCompat.startForegroundService(context, starter)
        }

        @JvmStatic
        fun stop(context: Context): Boolean {
            val intent = Intent(context, KeepAdbWifiOnService::class.java)
            return context.stopService(intent)
        }
    }
}