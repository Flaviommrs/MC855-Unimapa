package com.unimapa.unimapa.domain

class Post {

    private var title:String? = ""

    private var message:String? = ""

    private var lat:Float? = 0f

    private var lon:Float? = 0f

    constructor(title: String?, message: String?, lat: Float?, lon: Float?) {
        this.title = title
        this.message = message
        this.lat = lat
        this.lon = lon
    }


    fun getTitle(): String?{
        return title
    }

    fun setTitle(title: String?){
        this.title = title
    }

    fun getMessage(): String?{
        return message

    }

    fun setMessage(message: String?){
        this.message = message
    }

    fun getLon() : Float? {
        return lon
    }

    fun setLon(longitude: Float?){
        this.lon = longitude
    }

    fun setLat(latitude: Float?){
        this.lat = latitude
    }

    fun getlat() : Float?{
        return this.lat
    }

    override fun toString(): String {
        return "Post(title=$title, message=$message, lat=$lat, lon=$lon)"
    }


}