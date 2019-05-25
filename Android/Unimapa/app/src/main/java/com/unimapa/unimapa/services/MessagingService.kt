package com.unimapa.unimapa.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log


class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("[MessagingService]", "From: " + remoteMessage!!.from)

        remoteMessage.notification?.let {
            Log.d("[MessagingService]", "Message Notification Body: ${it.body}")
        }
    }
}
