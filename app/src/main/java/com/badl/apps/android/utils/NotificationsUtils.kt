package com.badl.apps.android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.badl.apps.android.R

object NotificationsUtils {

    private val mDefaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    // send notification with pending intent
     fun sendNotification(notificationManager: NotificationManager,
        title: String, message: String, context: Context,
        pendingIntent: PendingIntent,
        channelName: String ) {

        createNotificationChannel(channelName, notificationManager)

        val builder = NotificationCompat.Builder(context, channelName)
        builder.setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.main_app_icon)
            .setSound(mDefaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle())
        notificationManager.notify((System.currentTimeMillis() / 1000).toInt(), builder.build())
    }

     fun sendNotificationBigPicture(notificationManager: NotificationManager,
        title: String, message: String, context: Context,
        pendingIntent: PendingIntent, bigImage: Bitmap,
        channelName: String
    ) {

        createNotificationChannel(channelName, notificationManager)
        val builder = NotificationCompat.Builder(context, channelName)
        builder.setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.main_app_icon)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bigImage)
                    .setBigContentTitle(title)
                    .setSummaryText(message)
            )

        notificationManager.notify((System.currentTimeMillis() / 1000).toInt(), builder.build())
    }

    // send notification without pending intent
     fun sendNotification(notificationManager: NotificationManager,
        title: String, message: String,
        context: Context, channelName: String
    ) {

        createNotificationChannel(channelName, notificationManager)

        val builder = NotificationCompat.Builder(context, channelName)
        builder.setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.main_app_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(mDefaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle())
        notificationManager.notify((System.currentTimeMillis() / 1000).toInt(), builder.build())
    }

    // create notification channel if the build version is marshmallow or higher
    private fun createNotificationChannel(
        channelName: String,
        notificationManager: NotificationManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelName,
                channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern =
                longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}