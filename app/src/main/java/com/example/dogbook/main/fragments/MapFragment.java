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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dogbook.R;
import com.example.dogbook.common.ParseApplication;
import com.example.dogbook.main.mapclusteringrender.MapClusteringRenderer;
import com.example.dogbook.main.models.Post;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;

import static android.content.Context.LOCATION_SERVICE;
import static java.lang.Math.abs;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private static final int CURRENT_LOCATION_PERMISSION_REQUEST_CODE = 64;
    private static final int MAP_FOCUS_ZOOM = 10;
    private static final float COVERED_AREA_EXTRA_PERCENTAGE = 2; //The covered area box will be 200% the length of the current visible box

    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Location currentLocation;
    private GoogleMap map;
    private List<Post> posts;
    private List<Marker> markers = new ArrayList<>();
    private FragmentManager fragmentManager;

    private ClusterManager<Post> clusterManager;
    private MapClusteringRenderer clusterRenderer;

    private LatLngBounds coveredAreaBox;

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
        fragmentManager = getParentFragmentManager();
        //Load the Google Map
        setUpMap();
    }

    private void setUpMap() {
        if (mapFragment != null) {
            return;
        }
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadedMap(googleMap);
            }
        });
    }

    private void loadedMap(GoogleMap googleMap) {
        map = googleMap;
        setUpClusterManager();
        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds visibleAreaBox = map.getProjection().getVisibleRegion().latLngBounds;
                Log.i(TAG, "New visible box: " + visibleAreaBox.toString());
                if (!isVisibleAreaCovered(visibleAreaBox)) {
                    updateCoveredAreaBox(visibleAreaBox);
                }
                clusterManager.onCameraIdle();
            }
        });

        requestPermissionsForCurrentLocation();
    }

    private void setUpClusterManager() {
        clusterManager = new ClusterManager<>(getContext(), map);
        clusterRenderer = new MapClusteringRenderer(getContext(), map, clusterManager);
        clusterRenderer.setMinClusterSize(2);
        clusterManager.setRenderer(clusterRenderer);
        clusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Post>() {
            @Override
            public void onClusterItemInfoWindowClick(Post item) {
                PostDetailsFragment fragment = new PostDetailsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.mapFragmentContainer, fragment.newInstance(item))
                        .addToBackStack(null)
                        .commit();
            }
        });
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void requestPermissionsForCurrentLocation() {
        //Checks permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, CURRENT_LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        getCurrentLocation();
    }

    //Callback from getting the results from permissions request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CURRENT_LOCATION_PERMISSION_REQUEST_CODE) {
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
        //Set up of blue icon current location indicator
        map.setMyLocationEnabled(true);
        //Get current location ONCE
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        displayLocation();
    }

    //Show current location on the view (move the camera)
    private void displayLocation() {
        if (currentLocation == null) {
            Log.e(TAG, "Current location not found");
            Toast.makeText(getContext(), "Could not get current location", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, MAP_FOCUS_ZOOM);
        map.animateCamera(cameraUpdate);
    }

    private void updateCoveredAreaBox(LatLngBounds visibleAreaBox) {
        double longitudeLength = abs(visibleAreaBox.northeast.longitude - visibleAreaBox.southwest.longitude);
        double latitudeLength = abs(visibleAreaBox.northeast.latitude - visibleAreaBox.southwest.latitude);
        //The percentage that will be added per side, will be the total percentage minus 1 (100% of the visible box), divided by two because the percentage left has to be divided between the two sides
        float extraPercentageBySide = (COVERED_AREA_EXTRA_PERCENTAGE - 1)/2;

        //Reduce percentage if the area exceeds the map horizontally, since the map has 360 longitude degrees
        if (longitudeLength * COVERED_AREA_EXTRA_PERCENTAGE >= 360) {
            extraPercentageBySide = (float) 0.25;
        }

        LatLng northeast = new LatLng(visibleAreaBox.northeast.latitude + (latitudeLength * extraPercentageBySide), visibleAreaBox.northeast.longitude + (longitudeLength * extraPercentageBySide));
        LatLng southwest = new LatLng(visibleAreaBox.southwest.latitude - (latitudeLength * extraPercentageBySide), visibleAreaBox.southwest.longitude - (longitudeLength * extraPercentageBySide));

        coveredAreaBox = new LatLngBounds(southwest, northeast);
        Log.i(TAG, "New CAB: " + coveredAreaBox.toString());

        getPostsForCoveredAreaBox();
    }

    private boolean isVisibleAreaCovered(LatLngBounds visibleAreaBox) {
        //First time, the cab will be null, so we want to update it
        if (coveredAreaBox == null) {
            return false;
        }
        if (coveredAreaBox.contains(visibleAreaBox.northeast) && coveredAreaBox.contains(visibleAreaBox.southwest)) {
            return true;
        }
        return false;
    }

    private void getPostsForCoveredAreaBox() {
        ParseQuery<Post> query = ParseApplication.getLocationPostWithinBoundsQuery(coveredAreaBox.southwest, coveredAreaBox.northeast);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts = objects;
                    clusterManager.clearItems();
                    clusterManager.addItems(objects);
                    clusterManager.cluster();
                    return;
                }
                Log.e(TAG, "Issue finding posts in Parse", e);
                Toast.makeText(getContext(), "Unable to refresh posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

}