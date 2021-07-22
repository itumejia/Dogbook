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
import com.example.dogbook.main.models.Post;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment";
    private static final int CURRENT_LOCATION_PERMISSION_REQUEST_CODE = 64;
    private static final int MIN_DISTANCE_CHANGED = 4;
    private static final int MIN_TIME_CHANGED = 0;
    private static final int MAP_FOCUS_ZOOM = 10;

    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Location currentLocation;
    private LocationListener locationListener;
    private GoogleMap map;
    private List<Post> posts;
    private List<Marker> markers = new ArrayList<>();
    private FragmentManager fragmentManager;

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
        //Once the map is loaded, set up the current location
        requestPermissionsForCurrentLocation();
        getLocationPosts();
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //TODO: Navigate to post details view
                Post post = (Post) marker.getTag();
                PostDetailsFragment fragment = new PostDetailsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.mapFragmentContainer, fragment.newInstance(post))
                        .addToBackStack(null)
                        .commit();
                Log.i(TAG, "Post selected, caption: " + post.getDescription());
            }
        });
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_CHANGED, MIN_DISTANCE_CHANGED, locationListener);

    }

    //Show current location on the view (move the camera)
    private void displayLocation() {
        if (currentLocation == null) {
            Toast.makeText(getContext(), "Could not get current location", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, MAP_FOCUS_ZOOM);
        map.animateCamera(cameraUpdate);
    }

    private void getLocationPosts() {
        ParseQuery<Post> query = ParseApplication.getLocationPostQuery();
        //TODO: Filter posts to only get the ones near the current location (fixed distance limit)
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    posts = objects;
                    addMarkers();
                    return;
                }
                Log.e(TAG, "Issue finding posts in Parse", e);
                Toast.makeText(getContext(), "Unable to refresh posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarkers() {
        for (Post post : posts) {
            ParseGeoPoint location = post.getLocation();
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(position)
                    .title(post.getAuthor().getUsername())
                    .snippet(post.getDescription())
            );
            marker.setTag(post);
            markers.add(marker);
        }

    }

}