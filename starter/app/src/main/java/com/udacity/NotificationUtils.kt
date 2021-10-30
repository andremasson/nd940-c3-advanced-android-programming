package com.udacity

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.icons8_flat_download)
            .setContentTitle("Download")
            .setContentText(messageBody)
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}