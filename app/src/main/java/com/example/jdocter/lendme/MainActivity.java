package com.example.jdocter.lendme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.jdocter.lendme.MainFragments.HomeFragment;
import com.example.jdocter.lendme.MainFragments.LogoutFragment;
import com.example.jdocter.lendme.MainFragments.PaymentFragment;
import com.example.jdocter.lendme.MainFragments.ProfileFragment;
import com.example.jdocter.lendme.MainFragments.TransactionFragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements TrendingFragment.Callback{

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10000 * 1000;  /* 10000 secs - assumes don't need constant updates */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private ActionBarDrawerToggle drawerToggle;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private boolean initialHomeIntent = false;
    LatLng userLatLng;
    private ActionBar actionBar;
    private FrameLayout frameBig;
    private FrameLayout frameSmall;

    //push notification

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        frameBig = findViewById(R.id.flContent);
        frameSmall = findViewById(R.id.flContentShort);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.whiteopaque));
        actionBar.setIcon(R.drawable.logodark);
//        actionBar.setIcon(R.drawable.lend_me_logo);


        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // user location
        startLocationUpdates();
        initiatHomeFragment();



        drawerToggle = setupDrawerToggle();
        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.hamburgerBlack));
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentAction));
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.search_bar);
//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // perform query here
//                // Fetch the data remotely
//                //favoritesFragment.fetchItemsByCloseness(query);
//                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
//                // see https://code.google.com/p/android/issues/detail?id=24599
//                searchView.clearFocus();
//
//                return true;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//        return true;
//    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_logout:
                fragmentClass = LogoutFragment.class;
                break;
            case R.id.nav_payment:
                fragmentClass = PaymentFragment.class;
                break;
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_transaction:
                fragmentClass = TransactionFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentClass == HomeFragment.class) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.opaqueborder));
            actionBar.setIcon(R.drawable.logodark);
            drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.hamburgerBlack));
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            frameSmall.removeAllViews();

        } else {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
            fragmentManager.beginTransaction().replace(R.id.flContentShort, fragment).commit();
            frameBig.removeAllViews();
        }


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // ignore red squiggles
                LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                // do work here
                                onLocationChanged(locationResult.getLastLocation());
                                initiatHomeFragment();
                            }
                        },
                        Looper.myLooper());
            }

        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    Log.e("MainActivity", "Locaiton Permissions failed");
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

                // default location = user's home location
                ParseGeoPoint userHomeLoc = null;
                try {
                    userHomeLoc = ParseUser.getCurrentUser().fetch().getParseGeoPoint("location");
                    userLatLng = new LatLng(userHomeLoc.getLatitude(),userHomeLoc.getLongitude());

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                initiatHomeFragment();
            }

        });


    }

    public void initiatHomeFragment() {
        // initiate home fragment
        if (ParseUser.getCurrentUser() != null && initialHomeIntent == false) {
            // toolbar.setBackgroundColor(Color.TRANSPARENT);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();
            initialHomeIntent = true;
        }
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size()>0){
            String addy=addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getCountryName();

            Toast.makeText(this,addy, Toast.LENGTH_SHORT).show();
        }
/*
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();*/
        Log.e("MainActivity",msg);

        // You can now create a LatLng Object for use with maps
        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e("MainActivity","Testing");
    }


    @Override
    public ParseGeoPoint getLiveLoc() {
        //TODO - caused error when location is null.
        ParseGeoPoint userGeoPoint = null;
        if (userLatLng == null) {
            try {
                userGeoPoint = ParseUser.getCurrentUser().fetch().getParseGeoPoint("location");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            userGeoPoint = new ParseGeoPoint(userLatLng.latitude,userLatLng.longitude);
        }
        return userGeoPoint;
    }


}