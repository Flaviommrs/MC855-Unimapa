package com.unimapa.unimapa

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ListView
import android.widget.Toast
import com.unimapa.unimapa.dataBase.MapaDataBase

import com.unimapa.unimapa.domain.Mapa
import com.unimapa.unimapa.domain.CustomAdapter

import java.io.IOException

class MapList : AppCompatActivity() {

    private var mapas = java.util.ArrayList<Mapa>()
    private var SelectedMapas = java.util.ArrayList<Mapa>()

    private var lv: ListView? = null
    private var customAdapter: CustomAdapter? = null
    private var toolbar: Toolbar? = null
    private var MDB: MapaDataBase? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_map_list)

        MDB= MapaDataBase(this)

        this.getLocalMapas()
        this.getMapasFromServer()
        this.SelectMapas()

        if(mapas.size > 0) {
            this.lv = findViewById<ListView>(R.id.lv)

            this.customAdapter = CustomAdapter(this, this.mapas)
            lv!!.adapter = this.customAdapter
        }

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar!!.setTitle("Seleção de Mapas")
    }

    private fun SelectMapas(){
        var mapas2 = java.util.ArrayList<Mapa>()

        for(mapa in mapas){
            if(findMapaById(mapa.getId()!!) != null){
                mapas2.add(Mapa(mapa.getId(), mapa.getName(), mapa.getPosts(), true, mapa.getTipo()))
            }else{
                mapas2.add(Mapa(mapa.getId(), mapa.getName(), mapa.getPosts(), false, mapa.getTipo()))
            }
        }

        this.mapas = mapas2
    }

    @SuppressLint("WrongConstant")
    private fun getMapasFromServer(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        try {
            this.mapas.add(Mapa(-1,"",0,false, ""))
            this.mapas.addAll(ServerConnection.getMapas("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps"))
        } catch (e: IOException) {
            Toast.makeText(this,"Não conseguiu carregar mapas",60000)
            e.printStackTrace()
            getMapasFromServer()
        }
    }

    private fun getLocalMapas(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        try {
            SelectedMapas = MDB!!.getData()//ServerConnection.getMapas("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun findMapaById(id: Int): Mapa? {
        for (mapa in SelectedMapas) {
            if (mapa.getId() == id) {
                return mapa
            }
        }

        return null
    }

}
