package com.unimapa.unimapa

/**
 * Created by flavio.matheus on 23/04/19.
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle;
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast


import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import java.io.IOException
import java.net.URL

import java.util.ArrayList;
import java.util.List;

class MainActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener{

    private var mapView: MapView? = null
    private var routeCoordinates: MutableList<Point>? = null

    private var mNavigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private val jsonReader: JsonReader? = null
    private var alertDialogBuilder: AlertDialog.Builder? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token))

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(OnMapReadyCallback { mapboxMap ->
            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                runOnUiThread {
                    val geojsonUrl = URL("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps/1/posts")

                    val source = GeoJsonSource("geojson-source", geojsonUrl)

                    style.addSource(source)

                    style.addLayer(CircleLayer("circlelayer", "geojson-source"))

                }

            }
        })

        Toast.makeText(this, "Bem vindo ao Unimap", 4000).show()

        try {
            //val json = JsonReader.readJsonArray("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/users/Username 1/posts").toString()
            //Toast.makeText(this, json, 10000).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        alertDialogBuilder = AlertDialog.Builder(this)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        //TODO:setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        ////////////////////////////////////////////////////////
        mNavigationView = findViewById(R.id.nav_view)
        if (mNavigationView != null) {
            mNavigationView?.setNavigationItemSelectedListener(this)
        }
        ////////////////////////////////////////////////////////

        addItensInMenu()
    }

    private fun setUserInformations() {
        val txtInfoName = mNavigationView!!.getHeaderView(0).findViewById<TextView>(R.id.txtInfoName)
        val txtInfoEmail = mNavigationView!!.getHeaderView(0).findViewById<TextView>(R.id.txtInfoEmail)

        try {
            //TODO:GradientDrawable backgroundColor = (GradientDrawable) mNavigationView.getHeaderView(0).getBackground();
            //backgroundColor.setColor(Color.parseColor(environment.getColor()));
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        txtInfoEmail.setText("email@email.com")
        txtInfoName.setText("Fulano da Silva")

    }


    private fun addItensInMenu() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val menu = navigationView.menu

        var i = 0

        //coloca os ambientes no menu
        while (i < 6) {
            val item = menu.add(R.id.main_pages, i, 0, "Nome do mapa")


            item.setIcon(R.drawable.ic_selected)

            menu.addSubMenu("submenu")
            i++
            i++
        }

        //menu.addSubMenu("").add("");
        //menu.addSubMenu("").add("");
        val item = menu.addSubMenu("Opção").add(R.id.main_pages, 9000, 0, "Excluir Ambientes")
        item.setIcon(R.drawable.ic_home)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_home) {
            alert("Selecione um ambiente")
        } else if (id == R.id.nav_logout) {
            openLogin()
            finish()
        } else if (id == R.id.addEnvironment) {
            openEnvironment()
            return true
        } else if (id == 9000) {
            confirmationDeleteEnvironment(this)
            return true
        } else {//se selecionar um ambiente
            alert("selecionou um ambiente")
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun confirmationDeleteEnvironment(context: Context) {
        // set title
        alertDialogBuilder?.setTitle("Mensagem")

        // set dialog message
        alertDialogBuilder?.setMessage("Deseja excuir os ambientes?")?.setPositiveButton("Sim") { dialog, id ->
            openLogin()
            finish()
        }?.setNegativeButton("Não") { dialog, id ->
            //colocar alguma coisa para fazer
        }

        // create alert dialog
        val alertDialog = alertDialogBuilder?.create()
        // show it
        alertDialog?.show()
    }

    private fun openEnvironment() {
        alert("Abrindo novo mapa")
    }

    private fun alert(msg: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        // set title
        alertDialogBuilder.setTitle("Mensagem")

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setPositiveButton("OK") { dialog, id ->
                    //colocar alguma coisa para fazer
                }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
    }

    private fun openLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        //inicia a activity
        startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun verifyItIsConnectedToInternet(): Boolean {
        var connected = false
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = manager.activeNetworkInfo
        try {
            if (ni != null)
                if (ni.isAvailable) {
                    connected = true
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return connected
    }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    public override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState!!)
    }
}
