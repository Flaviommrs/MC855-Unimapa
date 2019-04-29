package com.unimapa.unimapa

import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import android.util.Log


/**
 * Created by flavio.matheus on 26/04/19.
 */
class UnimapaFirebaseInstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("[UnimapaFirebaseInstanceIdService]", "Refreshed token: " + refreshedToken!!)


    }
}