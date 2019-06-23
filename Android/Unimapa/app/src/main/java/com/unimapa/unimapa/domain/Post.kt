package com.unimapa.unimapa.domain

class Post(title: String?,
           message: String?,
           lat:Float?,
           lon:Float?) {

    private var title:String? = ""

    private var message:String? = ""

    private var lat:Float? = 0f

    private var lon:Float? = 0f

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
}