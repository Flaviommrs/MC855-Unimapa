package com.unimapa.unimapa.servercomunication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by flavio.matheus on 11/05/19.
 */
class NotificationToken {
    @SerializedName("token")
    @Expose
    private var token: String? = null

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }
}