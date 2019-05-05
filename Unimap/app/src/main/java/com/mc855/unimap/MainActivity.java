package com.mc855.unimap;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.support.annotation.NonNull;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mc855.unimap.com.mc855.unimap.database.EnvironmentDataBase;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private MapView mapView;
    private ProgressBar progressBarLoadingPage;
    private NavigationView mNavigationView;
    private Toolbar toolbar;
    private JsonReader jsonReader;
    private AlertDialog.Builder alertDialogBuilder;
    private boolean alredyRefreshed = false;
    private boolean blanckPage = true;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Mapbox.getInstance(this, "pk.eyJ1IjoiZ3VudGVybWluZ2F0byIsImEiOiJjanVlbnJ2aDAwNHJmNDNwODd0ZG53NnU1In0.RpQSPeyHV7exvw8i7Alziw");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        style.addImage("marker-icon-id",
                                BitmapFactory.decodeResource(
                                        MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default));

                        GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", Feature.fromGeometry(
                                Point.fromLngLat(-47.069919, -22.817113)));
                        style.addSource(geoJsonSource);

                        SymbolLayer symbolLayer = new SymbolLayer("layer-id", "source-id");
                        symbolLayer.withProperties(
                                PropertyFactory.iconImage("marker-icon-id")
                        );
                        style.addLayer(symbolLayer);

                    }
                });
            }
        });

        Toast.makeText(this, "Bem vindo ao Unimap", 4000).show();

        try {
            String json = JsonReader.readJsonArray("https://ac820fm2ig.execute-api.us-east-1.amazonaws.com/dev/users/Username 1/posts").toString();
            Toast.makeText(this, json, 10000).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        alertDialogBuilder = new AlertDialog.Builder(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ////////////////////////////////////////////////////////
        mNavigationView = findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        ////////////////////////////////////////////////////////

        addItensInMenu();
    }

    private void setUserInformations(){
        if(alredyRefreshed) {
            alredyRefreshed = true;
            final TextView txtInfoName = mNavigationView.getHeaderView(0).findViewById(R.id.txtInfoName);
            final TextView txtInfoEmail = mNavigationView.getHeaderView(0).findViewById(R.id.txtInfoEmail);

            try {
                //TODO:GradientDrawable backgroundColor = (GradientDrawable) mNavigationView.getHeaderView(0).getBackground();
                //backgroundColor.setColor(Color.parseColor(environment.getColor()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            txtInfoEmail.setText("email@email.com");
            txtInfoName.setText("Fulano da Silva");
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        // set title
        alertDialogBuilder.setTitle("Exit");

        // set dialog message
        alertDialogBuilder
                .setMessage("Deseja Sair?")
                .setCancelable(false)
                .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finish();
                    }
                })
                .setNegativeButton("Não",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

        //////////////////////////////////////////////////////////////////////////////////////////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void addItensInMenu (){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        int i = 0;

        //coloca os ambientes no menu
        for(; i < 6; i++){
            MenuItem item = menu.add(R.id.main_pages, i, 0, "Nome do mapa");


            item.setIcon(R.drawable.ic_selected);

            menu.addSubMenu("submenu");
            i++;
        }

        //menu.addSubMenu("").add("");
        //menu.addSubMenu("").add("");
        MenuItem item = menu.addSubMenu("Opção").add(R.id.main_pages, 9000, 0, "Excluir Ambientes");
        item.setIcon(R.drawable.ic_home);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        EnvironmentDataBase EDB = new EnvironmentDataBase(MainActivity.this);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            alert("Selecione um ambiente");
        } else if (id == R.id.nav_logout) {
            openLogin();
            finish();
        }else if (id == R.id.addEnvironment) {
            openEnvironment();
            return true;
        }else if (id == 9000) {
            confirmationDeleteEnvironment(this);
            return true;
        }else {//se selecionar um ambiente
            alert("selecionou um ambiente");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }

    private void alert(String msg){
        // set title
        alertDialogBuilder.setTitle("Mensagem");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //colocar alguma coisa para fazer
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void openEnvironment(){
        alert("Adicionando novo mapa");
    }


    private void confirmationDeleteEnvironment(final Context context){
        // set title
        alertDialogBuilder.setTitle("Mensagem");

        // set dialog message
        alertDialogBuilder
                .setMessage("Deseja excuir os ambientes?")
                .setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        openLogin();
                        finish();
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        //colocar alguma coisa para fazer
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent;

        /////////////////////////////////////////////
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            pendingIntent = TaskStackBuilder.create(this)
                    // add all of DetailsActivity's parents to the stack,
                    // followed by DetailsActivity itself
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);
        }



        //inicia a activity
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(Activity.RESULT_OK);
        //finish();
    }

    private boolean verifyItIsConnectedToInternet(){
        boolean connected = false;
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo ni = manager.getActiveNetworkInfo();
        try {
            if(ni != null)
                if (ni.isAvailable()) {
                    connected = true;
                }
        }catch (Exception e){e.printStackTrace();}

        return  connected;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}