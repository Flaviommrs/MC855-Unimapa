package com.unimapa.unimapa.servercomunication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by flavio.matheus on 05/05/19.
 */
class Publish {

    @SerializedName("map")
    @Expose
    private var map : String = ""
    @SerializedName("description")
    @Expose
    private var description : String = ""
    @SerializedName("user")
    @Expose
    private var user : String = ""

    fun getMap() : String {
        return map;
    }

    fun setMap(map: String) {
        this.map = map;
    }

    fun getDescription() : String {
        return description;
    }

    fun setDescription(description : String) {
        this.description = description;
    }

    fun getUser() : String {
        return user;
    }

    fun setUser(user : String) {
        this.user = user;
    }

}