package com.unimapa.unimapa

/**
 * Created by flavio.matheus on 23/04/19.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.unimapa.unimapa.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import java.net.URL

import java.util.ArrayList;
import java.util.List;



class MainActivity : AppCompatActivity() {

    private var mapView: MapView? = null
    private var routeCoordinates: MutableList<Point>? = null

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
