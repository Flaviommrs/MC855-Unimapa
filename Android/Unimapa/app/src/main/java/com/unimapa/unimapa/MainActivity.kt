package com.unimapa.unimapa

/**
 * Created by flavio.matheus on 23/04/19.
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle;
import android.os.Debug
import android.os.StrictMode
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapbox.android.gestures.AndroidGesturesManager

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.unimapa.unimapa.dataBase.MapaDataBase
import com.unimapa.unimapa.domain.Mapa
import com.unimapa.unimapa.servercomunication.ApiUtils
import com.unimapa.unimapa.servercomunication.datamodels.SignUp
import java.io.IOException
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener, PublicationDialogFragment.PublicationDialogListener{

    private var mapView: MapView? = null
    private var routeCoordinates: MutableList<Point>? = null

    private var mNavigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private val jsonReader: JsonReader? = null
    private var alertDialogBuilder: AlertDialog.Builder? = null

    private val RC_SIGN_IN = 100

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude:Double? = 0.0
    private var longitude:Double? = 0.0

    private var mapas = java.util.ArrayList<Mapa>()
    private var selectedMap:Int = 0

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInFlow()
        mapSetupFlow(savedInstanceState)

        getMapas()

        createMenu()

        ///LOCATION///////////////////////////////////////////////////////////////////////////////////////////////////
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        obtieneLocalizacion()
        //////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    private fun getMapas(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        try {
            var MDB: MapaDataBase? = MapaDataBase(this)
            if (MDB != null) {
                mapas = MDB.getData()//JsonReader.getMapas("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun signInFlow(){
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .setTheme(R.style.Theme)
                        .build(),
                RC_SIGN_IN)
    }

    private fun mapSetupFlow(savedInstanceState: Bundle?){
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_main)

        mapView = this.findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(OnMapReadyCallback { mapboxMap ->

            val position = CameraPosition.Builder()
            position.target(LatLng(-22.8184,-47.0647))
            position.zoom(15.toDouble())

            mapboxMap.cameraPosition = position.build()

            mapboxMap.setMinZoomPreference(13.toDouble())
            mapboxMap.setMaxZoomPreference(20.toDouble())

            mapboxMap.addOnMapLongClickListener{ point ->
                showPublicationDialog()
                true
            }

            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                runOnUiThread {
                    val geojsonUrl = URL("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps/1/posts")

                    val source = GeoJsonSource("geojson-source", geojsonUrl)



                    style.addSource(source)

                    style.addLayer(CircleLayer("circlelayer", "geojson-source"))
                }

            }
        })
    }

    fun showPublicationDialog(){
        val dialog = PublicationDialogFragment()
        dialog.show(supportFragmentManager, "publication")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    private fun createMenu(){
        alertDialogBuilder = AlertDialog.Builder(this)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                //this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
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

    @SuppressLint("ResourceType")
    private fun addItensInMenu() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        var menu = navigationView.menu
        var item:MenuItem
        var i = 0

        menu.clear()

        //coloca os ambientes no menu
        while (i < mapas.size) {
            item = menu.add(R.id.main_pages, i, 0, mapas[i].getName())

            if(i == selectedMap)
                item.setIcon(R.drawable.ic_selected)

            menu.addSubMenu("submenu")
            i++
        }

        item = menu.addSubMenu("Configurações").add(R.id.main_pages, 4000, 0, "Adicionar Mapas")
        item.setIcon(R.drawable.ic_menu_add)
        item = menu.add(R.id.main_pages, 5000, 0, "Logout")
        item.setIcon(R.drawable.ic_cancel_black_24dp)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        /*if (id == R.id.nav_home) {
            alert("HOME")
        } else */
        if (id == 5000) {
            signOutUser()
        } else if (id == 4000) {
            openListMapas()
            return true
        } else {//se selecionar um mapas
            //alert("selecionou um mapa " + item.itemId)
            item.setIcon(R.drawable.ic_selected)
            selectedMap = item.itemId
            addItensInMenu()
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOutUser(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    signInFlow()
                }
    }


    private fun openListMapas() {
        //alert("Abrindo novo mapa")
        intent = Intent(this, MapList::class.java)
        startActivity(intent)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        setResult(Activity.RESULT_OK)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in

                val user = FirebaseAuth.getInstance().currentUser

                user!!.getIdToken(true).addOnCompleteListener(OnCompleteListener {
//                    if(it.isSuccessful){
//                        val token : String = it.result!!.token!!
//
//                        val service = ApiUtils.getApiService()
//
//                        var tokenPayload = SignUp()
//                        tokenPayload.setToken(token)
//
//                        service.sendSignUpToken(tokenPayload)
//                    }
                })

                setUserInformations(user!!)

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    fun sendToken(user : FirebaseUser)
    {
        user!!.getIdToken(true).addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                val token : String = it.result!!.token!!

                val service = ApiUtils.getApiService()

                var tokenPayload = SignUp()
                tokenPayload.setToken(token)

                service.sendSignUpToken(tokenPayload)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion(){
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    latitude =  location?.latitude
                    longitude = location?.longitude

                    alert("lon: " + longitude + " lat: " + latitude)
                }
    }

    private fun setUserInformations(user: FirebaseUser) {
        val txtInfoName = mNavigationView!!.getHeaderView(0).findViewById<TextView>(R.id.txtInfoName)
        val txtInfoEmail = mNavigationView!!.getHeaderView(0).findViewById<TextView>(R.id.txtInfoEmail)

        txtInfoName.text = user.displayName
        txtInfoEmail.text = user.email
        try {
            //TODO:GradientDrawable backgroundColor = (GradientDrawable) mNavigationView.getHeaderView(0).getBackground();
            //backgroundColor.setColor(Color.parseColor(environment.getColor()));
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // Add the mapView's own lifecycle methods to the activity's lifecycle methods
    public override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
        getMapas()
        addItensInMenu()
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
