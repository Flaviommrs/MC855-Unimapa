package com.unimapa.unimapa.domain

class User {



    private var id:String? = ""

    private var username:String? = ""

    private var name:String? = ""

    private var token:String? = ""

    constructor(id:String?, username: String, name: String?) {
        this.id  = id
        this.username = username
        this.name = name
    }


    fun getId(): String? {
        return id
    }

    fun setId(id: String?){
        this.id = id
    }

    fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String?){
        this.username = username
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?){
        this.name = name
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?){
        this.token = token
    }



    companion object {
        @JvmField
        var TABLE_NAME: String = "user"

        @JvmField
        var ID: String = "id"

        @JvmField
        var USERNAME: String  = "username"

        @JvmField
        var NAME: String  = "names"

        @JvmField
        var TOKEN: String  = "token"
    }

}