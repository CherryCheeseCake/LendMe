package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jdocter.lendme.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

public class SelectLocationActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        setUpMapIfNeeded();

    }

    protected void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.lendMap));
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }
        }
    }

    // The Map is verified. It is now safe to manipulate the map.
    private void loadMap(final GoogleMap googleMap) {
        if (googleMap != null) {
            try {

                ParseGeoPoint userHomeLoc = ((User) ParseUser.getCurrentUser().fetch()).getLocation();
                LatLng listingPosition = new LatLng(userHomeLoc.getLatitude(), userHomeLoc.getLongitude());
                newMarker(listingPosition,googleMap);
                
            } catch (ParseException e) {
                Log.e("SelectLocationActivity", "Failed to load user home location");
            }

            // Attach marker click listener to the map here
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    LatLng newPos = marker.getPosition();
                    newMarker(newPos,googleMap);
                    return true;
                }
            });

        }
    }

    private void newMarker(LatLng pos,GoogleMap googleMap) {
        // Set the color of the marker to green
        BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

        googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title("Some title here")
                .snippet("Some description here")
                .icon(defaultMarker));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }
}
