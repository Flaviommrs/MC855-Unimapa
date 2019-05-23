package com.unimapa.unimapa.services

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.iid.FirebaseInstanceId
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.unimapa.unimapa.R


/**
 * Created by flavio.matheus on 26/04/19.
 */
class InstanceIdService : FirebaseMessagingService() {
    override fun onNewToken(refreshedToken: String) {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("[OnTokenRefresh]", "Refreshed token: " + refreshedToken!!)

        var preferences = applicationContext.getSharedPreferences("Unimapa", Context.MODE_PRIVATE)

        var editor = preferences.edit()

        editor.putString(R.string.notification_token_preferences.toString(), refreshedToken)
        editor.apply()
    }
}