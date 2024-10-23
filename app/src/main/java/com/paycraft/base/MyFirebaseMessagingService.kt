package com.paycraft.base

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.paycraft.R
import com.paycraft.home.HomeActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val E_PAGE = "page"
        const val E_ID = "id"
    }

    val TAG = "FCM"
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "From: ${message.from}")
//        if (message.data.isEmpty()) {
//            Log.d(TAG, "Data is empty!")
//            return
//        }
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(message)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Token $token")
        SessionManager.instance().setFcmToken(token)
    }

    private fun sendNotification(message: RemoteMessage) {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            HomeActivity.intent(applicationContext,
                message.data[E_PAGE],
                message.data[E_ID]),
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat
                .Builder(this, channelId)
                .setPriority(PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message.notification?.body ?: "An update from Paycraft")
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}