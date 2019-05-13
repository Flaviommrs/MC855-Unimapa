package com.unimapa.unimapa.servercomunication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by flavio.matheus on 10/05/19.
 */
class SignUp {
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