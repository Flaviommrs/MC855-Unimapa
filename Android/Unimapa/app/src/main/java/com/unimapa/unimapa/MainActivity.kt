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
import android.os.Bundle
import android.os.StrictMode
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
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

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
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap

import com.unimapa.unimapa.dataBase.MapaDataBase
import com.unimapa.unimapa.dataBase.UserDataBase
import com.unimapa.unimapa.domain.Mapa
import com.unimapa.unimapa.domain.User
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() ,NavigationView.OnNavigationItemSelectedListener, PublicationDialogFragment.PublicationDialogListener, PermissionsListener{

    private var mapView: MapView? = null
    private lateinit var map: MapboxMap
    private lateinit var locationComponent: LocationComponent
    private var routeCoordinates: MutableList<Point>? = null
    private lateinit var permissionsManager: PermissionsManager

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
    private val SOURCE_ID = "geojson-source"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER"

    private var savedInstanceState_global: Bundle? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        signInFlow()
        savedInstanceState_global = savedInstanceState
        mapSetupFlow(savedInstanceState)

        getMapas()

        createMenu()
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

            map = mapboxMap

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

            val UDB = UserDataBase(this)
            val token = UDB.getToken()

            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                runOnUiThread {
                    val url = URL(ServerConnection.BASE_URL + "/maps/" + mapas.get(selectedMap).getId() + "/posts")

                    //System.out.println(ServerConnection(this).sendJson("/maps/1/posts","","GET"))

                    System.out.println("pegando pontos com geojson")
                    val geojsonUrl = url
                    //geojsonUrl.openConnection().setRequestProperty("Authorization", "Bearer $token")
                    val source = GeoJsonSource(SOURCE_ID, geojsonUrl)
                    System.out.println("pegou pintous")

                    style.addSource(source)

                    if(selectedMap % 2 == 0) {//TODO: mudar aq para o tipo do mapa
                        addHeatMapLayer(style)
                    }else {
                        addMarkerLayer(style)
                    }

                    setupLocation(style)

                    style.addLayer(CircleLayer("urban-areas-fill", SOURCE_ID))
                }

            }
        })
    }

    private fun addNewPoints(){
        mapView!!.getMapAsync(OnMapReadyCallback { mapboxMap ->

            map = mapboxMap

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

            val UDB = UserDataBase(this)
            val token = UDB.getToken()

            mapboxMap.setStyle(Style.OUTDOORS) { style ->

                runOnUiThread {
                    val url = URL(ServerConnection.BASE_URL + "/maps/" + mapas.get(selectedMap).getId() + "/posts")
                    
                    val geojsonUrl = url
                    geojsonUrl.openConnection().setRequestProperty("Authorization", "Bearer $token")
                    val source = GeoJsonSource(SOURCE_ID, geojsonUrl)

                    style.addSource(source)

                    if(selectedMap % 2 == 0) {//TODO: mudar aq para o tipo do mapa
                        addHeatMapLayer(style)
                    }else {
                        addMarkerLayer(style)
                    }

                    setupLocation(style)

                    style.addLayer(CircleLayer("urban-areas-fill", SOURCE_ID))
                }

            }
        })
    }



    private fun addMarkerLayer(style: Style){
        // Add the marker image to map
        style.addImage("markerImage", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.ic_marker_48))


        style.addLayer(SymbolLayer("marker-layer", SOURCE_ID)
                .withProperties(
                        PropertyFactory.iconImage("markerImage"),
                        iconAllowOverlap(true),
                        iconOffset(arrayOf(0f, -9f))
                )
            )

    }

    private fun addHeatMapLayer(style: Style){

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

    fun showPublicationDialog(){
        val dialog = PublicationDialogFragment()
        dialog.show(supportFragmentManager, "publication")
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, description: String, selectedMap: String, title: String, id:Int) {
        val user = FirebaseAuth.getInstance().currentUser

        user!!.getIdToken(true).addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                val token : String = it.result!!.token!!

                ServerConnection(this).sendJson("/maps/" + id.toString() + "/posts", "{\n" +//
                        "\"title\" : \"$title\", \n" +
                        "\"message\" : \"$description\",\n" +
                        "\"lat\" : $postLat,\n" +
                        "\"lon\" : $postLong\n" +
                        "}", "POST")

                println("long $postLong, lat $postLat, map $selectedMap")
                //TODO:mapSetupFlow(savedInstanceState_global)
                addNewPoints()
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

        item = menu.addSubMenu("Configurações").add(R.id.main_pages, 3000, 0, "Publicações")
        item.setIcon(R.drawable.ic_menu_send)
        item = menu.add(R.id.main_pages, 4000, 0, "Selecionar mapas")
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
        }else if(id == 3000){
            intent = Intent(this, PostListActivity::class.java)
            startActivity(intent)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            setResult(Activity.RESULT_OK)
            return true
        } else {//se selecionar um mapas
            //alert("selecionou um mapa " + item.itemId)
            item.setIcon(R.drawable.ic_selected)
            selectedMap = item.itemId//mapas[item.itemId].getId()!!
            addItensInMenu()
            addNewPoints()
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
                                ServerConnection(this).sendJson("/sign-up", "", "POST")
                                Log.w("[Sign Up]", "getInstanceId failed", task.exception)
                                return@OnCompleteListener
                            }

                            UserDataBase(this).insertData(User(user.displayName!!,user.email!!,user.displayName!!, token ))

                            // Get new Instance ID token
                            val notification = task.result?.token
                            ServerConnection(this).sendJson("/sign-up", "{ \"notification_token\": \"$notification\" }", "POST")
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

    private fun saveToken(token: String) {
        val DB:UserDataBase = UserDataBase(this)
        DB.updateToken(token)
    }

    @SuppressLint("MissingPermission")
    private fun setupLocation(loadedMapStyle: Style){

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

          // Create and customize the LocationComponent's options
          var customLocationComponentOptions = LocationComponentOptions.builder(this)
            .elevation(5f)
            .accuracyAlpha(.6f)

            .accuracyColor(Color.RED)
            .build()

          // Get an instance of the component
          locationComponent = map.getLocationComponent();

          var locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, loadedMapStyle)
              .locationComponentOptions(customLocationComponentOptions)
              .useDefaultLocationEngine(true)
              .build()

          // Activate with options
          locationComponent.activateLocationComponent(locationComponentActivationOptions);

          // Enable to make component visible
          locationComponent.setLocationComponentEnabled(true);

          // Set the component's render mode
          locationComponent.setRenderMode(RenderMode.COMPASS);


        } else {
          permissionsManager = PermissionsManager(this);
          permissionsManager.requestLocationPermissions(this);
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

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "explanation", Toast.LENGTH_LONG).show();
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
         map.getStyle {
              setupLocation(it)
            }
        } else {
          Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
          finish()
        }
    }
}
