package com.unimapa.unimapa.domain

class Mapa {

    private var id:Int?

    private var name:String? = ""

    private var posts:Int? = 0

    private var selected:Boolean? = false

    private var tipo:String? = ""

    constructor(id:Int?, name: String?, posts:Int?, selected:Boolean?, tipo:String?) {
        this.id  = id
        this.name = name
        this.posts = posts
        this.selected = selected
        this.tipo = tipo
    }


    fun getId(): Int? {
        return id
    }

    fun setI(id: Int?){
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?){
        this.name = name
    }

    fun getPosts(): Int? {
        return posts
    }

    fun setPosts(username: Int?){
        this.posts = posts
    }

    fun getSelected(): Boolean? {
        return selected
    }

    fun setSelected(selected: Boolean?){
        this.selected = selected
    }

    fun getTipo(): String? {
        return tipo
    }

    fun setTipo(tipo: String?){
        this.tipo = tipo
    }


    override fun toString(): String {
        return "Mapa(id=$id, name=$name, posts=$posts, selected=$selected)"
    }

    companion object {
        @JvmField
        var TABLE_NAME: String = "mapa"

        @JvmField
        var ID: String = "id"

        @JvmField
        var NAME: String = "name"

        @JvmField
        var POSTS: String = "posts"

        @JvmField
        var TIPO: String = "tipo"
    }



}