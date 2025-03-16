package com.mattdrzazga.wificompanion

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationCreator(private val context: Context) {

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    fun createNotificationChannel() {
        val notificationChannelCompat = NotificationChannelCompat.Builder(
            DEFAULT_NOTIFICATION_CHANNEL_ID,
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .setName(context.getString(R.string.default_channel_name))
            .build()

        notificationManager.createNotificationChannel(notificationChannelCompat)
    }

    fun createForegroundServiceNotification(): Notification {
        val notification = NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.service_notification_title))
            .setContentText(context.getString(R.string.service_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        return notification
    }
}

private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default_channel_id"