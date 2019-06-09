package com.unimapa.unimapa.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import com.unimapa.unimapa.R


class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("[MessagingService]", "From: " + remoteMessage!!.from)

        remoteMessage.notification?.let {
            Log.d("[MessagingService]", "Message Notification Body: ${it.body}")

            var builder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_marker)
                    .setContentTitle(it.title)
                    .setContentText(it.body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setChannelId("maps_1")
                    .setAutoCancel(true)

            val id = 1

            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(id, builder.build())
            }
        }
    }

}
