package com.ghost.canyouseeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MenuActivity extends Activity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest;
    private static Location mCurrentLocation;
    private boolean doubleBackToExitPressedOnce;
    private Marker currentMark;
    private boolean isFirst = true;
    private int updateFailure = 0;
    private Handler gpsChecker = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView roomNameText = (TextView) this.findViewById(R.id.roomName);
        roomNameText.setText(HostActivity.roomJoined);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    ///##########################################################/
    //###########################################################
    //###########################################################

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // The Android Wear app is not installed
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        if(servicesConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        }
    }

    private boolean servicesConnected() {
        return mLocationClient.isConnected();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (!gpsIsActive()) {
            updateFailure += 1;
            if (updateFailure >= 3) {
                killPlayer();
            }
        }
        mCurrentLocation = location;
        if (isFirstInstance()) {
            LatLng currLoc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 18));
            currentMark = mMap.addMarker(new MarkerOptions()
                    .title(LoginActivity.userName)
                    .position(currLoc)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            currentMark.showInfoWindow();
            isFirst = false;
        } else {
            LatLng currLoc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            currentMark.setPosition(currLoc);
            currentMark.showInfoWindow();
        }
    }

    private boolean gpsIsActive() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isActive) {
           return false;
        }
        return true;
    }

    private void killPlayer() {
        //Kill player & remove user from game.
        Toast.makeText(this, "You have been kicked from the game.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isFirstInstance() {
        return isFirst;
    }
    //###########################################################
    //###########################################################
    //###########################################################

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
        map.setBuildingsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private Runnable gpsCheck = new Runnable() {
        @Override
        public void run() {
            if (!gpsIsActive()) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                Toast.makeText(MenuActivity.this, "You need to turn on location services or you will be kicked!", Toast.LENGTH_SHORT).show();
            } else {
                updateFailure = 0;
            }
            gpsChecker.postDelayed(this, 5000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        updateFailure += 1;
        gpsChecker.removeCallbacks(gpsCheck);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (updateFailure >= 3){
            killPlayer();
        } else {
            gpsChecker.postDelayed(gpsCheck, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
/*
    @Override
    public void onBackPressed() {
        //MOVE TO ROOM PAGE
        //UPDATE ROOM PAGE
        moveTaskToBack(true);
    }*/

}
