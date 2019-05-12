package com.unimapa.unimapa.services

import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import android.util.Log
import com.unimapa.unimapa.servercomunication.ApiUtils
import com.unimapa.unimapa.servercomunication.datamodels.NotificationToken


/**
 * Created by flavio.matheus on 26/04/19.
 */
class InstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("[OnTokenRefresh]", "Refreshed token: " + refreshedToken!!)

//        var notificationToken = NotificationToken()
//        notificationToken.setToken(refreshedToken)
//
//        ApiUtils.getApiService().sendNotificationTokenCreated(notificationToken)
    }
}