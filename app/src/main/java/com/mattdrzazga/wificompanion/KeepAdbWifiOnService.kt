package com.mattdrzazga.wificompanion

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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
            keepAdbOverWifiOn(AdbOverWifiSetting(contentResolver))
            log(tag = TAG, message = "Stopping service due to insufficient permissions.")
            stopSelf()
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

    companion object {

        private const val TAG = "KeepAdbWifiOnService"
        private const val FOREGROUND_SERVICE_NOTIFICATION_ID = 30000

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