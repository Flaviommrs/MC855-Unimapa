package com.unimapa.unimapa

/**
 * Created by flavio.matheus on 23/04/19.
 */

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle;
import android.os.StrictMode
import android.support.annotation.NonNull
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.unimapa.unimapa.dataBase.MapaDataBase
import com.unimapa.unimapa.domain.Mapa
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener, PublicationDialogFragment.PublicationDialogListener{

    private var mapView: MapView? = null
    private var routeCoordinates: MutableList<Point>? = null

    private var mNavigationView: NavigationView? = null
    private var toolbar: Toolbar? = null
    private val serverConnection: ServerConnection? = null
    private var alertDialogBuilder: AlertDialog.Builder? = null

    private val RC_SIGN_IN = 100

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude:Double? = 0.0
    private var longitude:Double? = 0.0

    private var mapas = java.util.ArrayList<Mapa>()
    private var selectedMap:Int = 0

    private var postLat: Double = 0.0
    private var postLong: Double = 0.0

    private var listOfHeatmapColors: Array<Expression>? = null
    private var listOfHeatmapRadiusStops: Array<Expression>? = null
    private var listOfHeatmapIntensityStops: Array<Float>? = null
    private val index: Int = 0
    private val SOURCE_ID = "geojson-source"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER"

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
                mapas = MDB.getData()
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

            mapboxMap.setMinZoomPreference(4.toDouble())
            mapboxMap.setMaxZoomPreference(20.toDouble())

            mapboxMap.addOnMapLongClickListener{ point ->
                showPublicationDialog()
                postLat = point.latitude
                postLong = point.longitude
                true
            }

            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                runOnUiThread {
                    val geojsonUrl = URL("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/maps/1/posts")

                    val source = GeoJsonSource(SOURCE_ID, geojsonUrl)

                    style.addSource(source)

                    addHeatMapLayer2(style)

                    addMarkerLayer(style)

                    style.addLayer(CircleLayer("urban-areas-fill", SOURCE_ID))//TODO:circlelayer
                }

            }
        })
    }

    private fun addMarkerLayer(style: Style){
        // Add the marker image to map
        style.addImage("my-marker-image", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.ic_user_round))


        style.addLayer(SymbolLayer("marker-layer", SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage("my-marker-image"),
                        iconAllowOverlap(true),
                        iconOffset(arrayOf(0f, -9f))
                )
            )

    }

    private fun addHeatMapLayer2(style: Style){

        // Each point range gets a different fill color.
        val layers = arrayOf(intArrayOf(150, Color.parseColor("#E55E5E")), intArrayOf(20, Color.parseColor("#F9886C")), intArrayOf(0, Color.parseColor("#FBB03B")))

        var unclustered = CircleLayer("unclustered-points", SOURCE_ID)

        unclustered.setProperties(circleColor(Color.parseColor("#FBB03B")),
                circleRadius(20f),
                circleBlur(1f))

        unclustered.setFilter(Expression.neq(get("cluster"), literal(true)))
        style.addLayerBelow(unclustered, "building")

        val i = 0
        for(i in 0 until layers.size){
            val circles = CircleLayer("cluster-$i", SOURCE_ID)
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(70f),
                    circleBlur(1f)
            )
            var pointCount = toNumber(get("point_count"))

            circles.setFilter(
                    if (i === 0)
                        Expression.gte(pointCount, literal(layers[i][0]))
                    else
                        Expression.all(
                                Expression.gte(pointCount, literal(layers[i][0])),
                                Expression.lt(pointCount, literal(layers[i - 1][0]))
                        )
            )
            style.addLayerBelow(circles, "building");
        }

    }

    private fun addHeatMapLayer(style: Style) {
        initHeatmapColors()
        initHeatmapRadiusStops()
        initHeatmapIntensityStops()

        addHeatmapLayer(style);
    }

    fun showPublicationDialog(){
        val dialog = PublicationDialogFragment()
        dialog.show(supportFragmentManager, "publication")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, description: String, selectedMap: String) {
        val user = FirebaseAuth.getInstance().currentUser

        user!!.getIdToken(true).addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                val token : String = it.result!!.token!!

                ServerConnection.sendJson("/posts", "{\n" +
                        "\"message\" : \"$description\",\n" +
                        "\"point_x\" : $postLong,\n" +
                        "\"point_y\" : $postLat\n" +
                        "}", token)

                println("long $postLong, lat $postLat, map $selectedMap")
                dialog.dismiss()
            }
        })
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

            if(i == selectedMap)//mapas[i].getId()
                item.setIcon(R.drawable.ic_selected)

            menu.addSubMenu("submenu")
            i++
        }

        item = menu.addSubMenu("Configurações").add(R.id.main_pages, 4000, 0, "Selecionar mapas")
        item.setIcon(R.drawable.ic_menu_add)
        item = menu.add(R.id.main_pages, 5000, 0, "Logout")
        item.setIcon(R.drawable.ic_cancel_black_24dp)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == 5000) {
            signOutUser()
        } else if (id == 4000) {
            openListMapas()
            return true
        } else {//se selecionar um mapas
            //alert("selecionou um mapa " + item.itemId)
            item.setIcon(R.drawable.ic_selected)
            selectedMap = item.itemId//mapas[item.itemId].getId()!!
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
                    if(it.isSuccessful){
                        val token : String = it.result!!.token!!

                        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                ServerConnection.sendJson("/sign-up", "", token)
                                Log.w("[Sign Up]", "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }

                            // Get new Instance ID token
                            val notification = task.result?.token
                            ServerConnection.sendJson("/sign-up", "{ \"notification_token\": \"$notification\" }", token)
                        })
                    }
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




    private fun addHeatmapLayer(@NonNull loadedMapStyle: Style) {
        // Create the heatmap layer
        val layer = HeatmapLayer(HEATMAP_LAYER_ID, SOURCE_ID)

        // Heatmap layer disappears at whatever zoom level is set as the maximum
        layer.setMaxZoom(18F)

        layer.setProperties(
                // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
                // Begin color ramp at 0-stop with a 0-transparency color to create a blur-like effect.
                heatmapColor(this!!.listOfHeatmapColors!![index]),

                // Increase the heatmap color weight weight by zoom level
                // heatmap-intensity is a multiplier on top of heatmap-weight
                heatmapIntensity(this!!.listOfHeatmapIntensityStops!![index]),

                // Adjust the heatmap radius by zoom level
                heatmapRadius(this!!.listOfHeatmapRadiusStops!![index]
                ),

                heatmapOpacity(1f)
        )

        // Add the heatmap layer to the map and above the "water-label" layer
        loadedMapStyle.addLayerAbove(layer, "waterway-label")
    }

    private fun initHeatmapColors() {
        listOfHeatmapColors = arrayOf<Expression>(
                // 0
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.25), rgba(224, 176, 63, 0.5),
                        literal(0.5), rgb(247, 252, 84),
                        literal(0.75), rgb(186, 59, 30),
                        literal(0.9), rgb(255, 0, 0)
                ),
                // 1
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(255, 255, 255, 0.4),
                        literal(0.25), rgba(4, 179, 183, 1.0),
                        literal(0.5), rgba(204, 211, 61, 1.0),
                        literal(0.75), rgba(252, 167, 55, 1.0),
                        literal(1), rgba(255, 78, 70, 1.0)
                ),
                // 2
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(12, 182, 253, 0.0),
                        literal(0.25), rgba(87, 17, 229, 0.5),
                        literal(0.5), rgba(255, 0, 0, 1.0),
                        literal(0.75), rgba(229, 134, 15, 0.5),
                        literal(1), rgba(230, 255, 55, 0.6)
                ),
                // 3
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(135, 255, 135, 0.2),
                        literal(0.5), rgba(255, 99, 0, 0.5),
                        literal(1), rgba(47, 21, 197, 0.2)
                ),
                // 4
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(4, 0, 0, 0.2),
                        literal(0.25), rgba(229, 12, 1, 1.0),
                        literal(0.30), rgba(244, 114, 1, 1.0),
                        literal(0.40), rgba(255, 205, 12, 1.0),
                        literal(0.50), rgba(255, 229, 121, 1.0),
                        literal(1), rgba(255, 253, 244, 1.0)
                ),
                // 5
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.05), rgba(0, 0, 0, 0.05),
                        literal(0.4), rgba(254, 142, 2, 0.7),
                        literal(0.5), rgba(255, 165, 5, 0.8),
                        literal(0.8), rgba(255, 187, 4, 0.9),
                        literal(0.95), rgba(255, 228, 173, 0.8),
                        literal(1), rgba(255, 253, 244, .8)
                ),
                //6
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.3), rgba(82, 72, 151, 0.4),
                        literal(0.4), rgba(138, 202, 160, 1.0),
                        literal(0.5), rgba(246, 139, 76, 0.9),
                        literal(0.9), rgba(252, 246, 182, 0.8),
                        literal(1), rgba(255, 255, 255, 0.8)
                ),

                //7
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 8
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 9
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 10
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.01),
                        literal(0.1), rgba(0, 2, 114, .1),
                        literal(0.2), rgba(0, 6, 219, .15),
                        literal(0.3), rgba(0, 74, 255, .2),
                        literal(0.4), rgba(0, 202, 255, .25),
                        literal(0.5), rgba(73, 255, 154, .3),
                        literal(0.6), rgba(171, 255, 59, .35),
                        literal(0.7), rgba(255, 197, 3, .4),
                        literal(0.8), rgba(255, 82, 1, 0.7),
                        literal(0.9), rgba(196, 0, 1, 0.8),
                        literal(0.95), rgba(121, 0, 0, 0.8)
                ),
                // 11
                interpolate(
                        linear(), heatmapDensity(),
                        literal(0.01), rgba(0, 0, 0, 0.25),
                        literal(0.25), rgba(229, 12, 1, .7),
                        literal(0.30), rgba(244, 114, 1, .7),
                        literal(0.40), rgba(255, 205, 12, .7),
                        literal(0.50), rgba(255, 229, 121, .8),
                        literal(1), rgba(255, 253, 244, .8)
                ))
    }

    private fun initHeatmapRadiusStops() {
        listOfHeatmapRadiusStops = arrayOf<Expression>(
                // 0
                interpolate(
                        linear(), zoom(),
                        literal(6), literal(50),
                        literal(20), literal(100)
                ),
                // 1
                interpolate(
                        linear(), zoom(),
                        literal(12), literal(70),
                        literal(20), literal(100)
                ),
                // 2
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 3
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 4
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(5), literal(50)
                ),
                // 5
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(7),
                        literal(15), literal(200)
                ),
                // 6
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(70)
                ),
                // 7
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 8
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 9
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 10
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ),
                // 11
                interpolate(
                        linear(), zoom(),
                        literal(1), literal(10),
                        literal(8), literal(200)
                ))
    }

    private fun initHeatmapIntensityStops() {
        listOfHeatmapIntensityStops = arrayOf(
                // 0
                0.6f,
                // 1
                0.3f,
                // 2
                1f,
                // 3
                1f,
                // 4
                1f,
                // 5
                1f,
                // 6
                1.5f,
                // 7
                0.8f,
                // 8
                0.25f,
                // 9
                0.8f,
                // 10
                0.25f,
                // 11
                0.5f)
    }


}
