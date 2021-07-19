package com.example.dogbook.main.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dogbook.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

import permissions.dispatcher.NeedsPermission;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";

    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Location currentLocation;
    private LocationListener locationListener;
    private GoogleMap map;

    public MapFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Load the Google Map
        setUpMap();
    }

    private void setUpMap() {
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    loadedMap(googleMap);
                }
            });
        }
    }

    private void loadedMap(GoogleMap googleMap) {
        map = googleMap;
        //Once the map is loaded, set up the current location
        getPermissionsToCurrentLocation();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getPermissionsToCurrentLocation() {
        //Checks permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        getCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_DENIED) {
                //all permissions have been granted
                getCurrentLocation();
                return;
            }
            //If permissions are not granted, current location will not be available
            Toast.makeText(getContext(), "Permissions to get current location denied", Toast.LENGTH_SHORT).show();
        }
    }

    //Permission already granted in getPermissionsToCurrentLocation()
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                currentLocation = location;
                displayLocation(); //Map camera focuses on changed location
            }
        };

        //Set up of blue icon current location indicator
        map.setMyLocationEnabled(true);
        //Start listener for changes
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 4, locationListener);

    }

    //Show current location on the view (move the camera)
    private void displayLocation() {
        if(currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
            return;

        }
        Toast.makeText(getContext(), "Could not get current location", Toast.LENGTH_SHORT).show();
    }
}