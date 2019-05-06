package com.unimapa.unimapa.servercomunication.datamodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by flavio.matheus on 05/05/19.
 */
class Publish {

    @SerializedName("mapName")
    @Expose
    private var mapName : String = ""
    @SerializedName("pinDescription")
    @Expose
    private var pinDescription : String = ""

    private fun Publish(mapName: String, description: String){
        this.mapName = mapName
        this.pinDescription = description
    }

    fun getMapName() : String {
        return mapName
    }

    fun getPinDescription() : String {
        return pinDescription
    }
}