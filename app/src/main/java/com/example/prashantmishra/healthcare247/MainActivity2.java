package com.example.prashantmishra.healthcare247;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMapClickListener{

    private MapView mapView;
    MapboxNavigation navigation;
    MapboxMap map;
    PermissionsManager permissionsManager;
    LocationEngine locationEngine;
    LocationLayerPlugin locationLayerPlugin;
    Location originLocation;
    Point srcLocation;
    Point destLocation;
    double latitude;
    double longitude;

    Marker destinationMarker;
    Button navigate;
    NavigationMapRoute navigationMapRoute;
    public static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("tokw", FirebaseInstanceId.getInstance().getToken());
        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1IjoibWlzaHJhcHJhc2hhbnQiLCJhIjoiY2ppOXliM3dlMHh0OTNxbzIzNjU4djJuaiJ9.e7mkH5dCfGuoUJZq28qFQg");
        navigation=new MapboxNavigation(this,"pk.eyJ1IjoibWlzaHJhcHJhc2hhbnQiLCJhIjoiY2ppOXliM3dlMHh0OTNxbzIzNjU4djJuaiJ9.e7mkH5dCfGuoUJZq28qFQg");
        setContentView(R.layout.activity_main2);
        mapView = findViewById(R.id.mapView);
        mapView.setStyleUrl(Style.TRAFFIC_DAY);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        navigate=findViewById(R.id.button);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationLauncherOptions navigationLauncherOptions=NavigationLauncherOptions.builder().origin(srcLocation).destination(destLocation).build();
                NavigationLauncher.startNavigation(MainActivity2.this,navigationLauncherOptions);
            }
        });

        final String lati=getIntent().getStringExtra("lat");
        final String longi=getIntent().getStringExtra("long");



        if(lati!=null && longi!=null){
            latitude=Double.parseDouble(lati);
            longitude=Double.parseDouble(longi);


            List<Address> addresses = null;
            Geocoder geocoder;
            geocoder = new Geocoder(MainActivity2.this);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String complete_add = addresses.get(0).getAddressLine(0);
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    srcLocation=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
                    destLocation=Point.fromLngLat(longitude,latitude);

                    getRoute(srcLocation,destLocation);

                    navigate.setEnabled(true);
                    navigate.setBackgroundResource(R.color.mapbox_blue);



                }
            }).setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setTitle("Patient Pickup").setIcon(R.drawable.common_google_signin_btn_icon_dark).setMessage("Are you ready to Pickup at "+complete_add+"?").setCancelable(false).show();
        }

        Toolbar toolbar = findViewById(R.id.toolbar121);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        android.support.design.widget.NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map=mapboxMap;
        map.addOnMapClickListener(this);

        enableLocation();
    }

    private void enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine();
            intializeLocationLayer();
        }else{
            permissionsManager=new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void initializeLocationEngine(){
        locationEngine=new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        @SuppressLint("MissingPermission") Location lastLocation=locationEngine.getLastLocation();
        if(lastLocation!=null){
            originLocation=lastLocation;
            setCameraLocation(lastLocation);
        }
        else{
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressLint("MissingPermission")
    private void intializeLocationLayer(){

        locationLayerPlugin=new LocationLayerPlugin(mapView,map,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);

    }

    private void setCameraLocation(Location location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),5.0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location!=null){
            originLocation=location;
            setCameraLocation(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            enableLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if(locationEngine!=null){
            locationEngine.requestLocationUpdates();
        }
        if(locationLayerPlugin!=null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(locationEngine!=null){
            locationEngine.removeLocationUpdates();
        }
        if(locationLayerPlugin!=null){
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationEngine!=null){
            locationEngine.deactivate();
        }
        mapView.onDestroy();
        navigation.endNavigation();
        navigation.onDestroy();
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {

        if(destinationMarker!=null){
            map.removeMarker(destinationMarker);
        }

        destinationMarker=map.addMarker(new MarkerOptions().position(point));
        destLocation=Point.fromLngLat(point.getLongitude(),point.getLatitude());
        srcLocation=Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
        getRoute(srcLocation,destLocation);

        navigate.setEnabled(true);
        navigate.setBackgroundResource(R.color.mapbox_blue);


    }

    void getRoute(Point origin,Point Destination){
        // map.addMarker(new MarkerOptions().position(new LatLng(longitude,latitude)));
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken()).origin(origin).destination(Destination).build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if(response.body()==null){
                    Toast.makeText(MainActivity2.this, "No route found, Check user or access token", Toast.LENGTH_SHORT).show();
                }
                else if(response.body().routes().size()==0){
                    Toast.makeText(MainActivity2.this, "No Route Found", Toast.LENGTH_SHORT).show();
                }

                DirectionsRoute route=response.body().routes().get(0);
                if(navigationMapRoute!=null){
                    navigationMapRoute.removeRoute();
                }else{
                    navigationMapRoute=new NavigationMapRoute(null,mapView,map);
                }

                navigationMapRoute.addRoute(route);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(this,DriverHistory.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
