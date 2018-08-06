package com.example.jdocter.lendme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.DetailView.ImageEnlargeActivity;
import com.example.jdocter.lendme.DetailView.ItemCalendarActivity;
import com.example.jdocter.lendme.model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class DetailPostActivity extends AppCompatActivity {


    TextView tvfullName;
    TextView tvUsername;
    ImageView ivProfileImage;
    ImageView ivItemImage;
    TextView tvDescription;
    TextView tvTitleItem;
    TextView tvPrice;
    Button btRequest;
    ImageButton ibLikes;

    boolean isImageFitToScreen;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private final int REQUEST_CODE = 20;
    private Double userLatitude=0.0;
    private Double userLongitude=0.0;
    private static String ownerIdKey = "ownerId";
    private static String objectIdKey = "objectId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        tvfullName = (TextView) findViewById(R.id.tvname);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivItemImage = (ImageView) findViewById(R.id.ivItemImage);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvTitleItem = (TextView) findViewById(R.id.tvTitleItem);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        btRequest = (Button) findViewById(R.id.btRequest);
        ibLikes = (ImageButton) findViewById(R.id.ibLikes);
        isImageFitToScreen = false;
        final String objectId = getIntent().getStringExtra("objectId");


        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailPostActivity.this, ItemCalendarActivity.class);
                i.putExtra("objectId", objectId);
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                Post post = null;
                try {
                    post = query.get(objectId);
                    if (ParseUser.getCurrentUser() == post.getUser()) {
                        i.putExtra("transaction", true);
                    } else {
                        i.putExtra("transaction", false);
                    }
                } catch (ParseException e) {
                    Log.e("DetailPostActivity","Failed to retrieve user post");
                }

                startActivity(i);
            }

        });

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        try {
            Post post = query.get(objectId);
            if (post.getString(ownerIdKey) == ParseUser.getCurrentUser().getString(objectIdKey)) {
                btRequest.setText("Reserve my item");
            }

            try {
                String username = post.getUser().fetchIfNeeded().getUsername();
                tvUsername.setText(username);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            final String ItemUrl = post.getImage().getUrl();
            Glide.with(DetailPostActivity.this)
                    .load(ItemUrl)
                    .apply(new RequestOptions().override(470, 340).centerCrop())
                    .into(ivItemImage);
            try {
                tvfullName.setText(post.getUser().fetchIfNeeded().getString("fullName"));
            } catch (ParseException e2) {
                e2.printStackTrace();
            }
            try {
                String profileUrl = post.getUser().fetchIfNeeded().getParseFile("profileImage").getUrl();
                Glide.with(DetailPostActivity.this)
                        .load(profileUrl)
                        .apply(new RequestOptions().transform(new CircleTransform(DetailPostActivity.this)))
                        .into(ivProfileImage);
            } catch (ParseException e3) {
                e3.printStackTrace();
            }
            tvDescription.setText(post.getDescription());
            tvTitleItem.setText(post.getItem());




            tvPrice.setText("$" + Double.toString(post.getPrice()));
            //tvPrice.setText("$"+Integer.toString(post.getPrice()));
            final Post mPost = post;
            if (mPost.hasLiked()) {
                ibLikes.setImageResource(R.drawable.ufi_heart_active);
            } else {
                ibLikes.setImageResource(R.drawable.ufi_heart);
            }

            ibLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    if (mPost.hasLiked()) {
                        ibLikes.setImageResource(R.drawable.ufi_heart);
                        mPost.unlikePost(user);
                        mPost.saveInBackground();
                    } else {
                        ibLikes.setImageResource(R.drawable.ufi_heart_active);
                        mPost.likePost(user);
                        mPost.saveInBackground();

                    }
                }
            });

            final ParseGeoPoint parseGeoPoint = post.getParseGeoPoint("location");
            setUpMapIfNeeded(parseGeoPoint);

            ivItemImage.setClickable(true);
            ivItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(DetailPostActivity.this, ImageEnlargeActivity.class);
                    i.putExtra("ImageUrl", ItemUrl);
                    startActivity(i);


                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void setUpMapIfNeeded(final ParseGeoPoint parseGeoPoint) {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map=googleMap;
                        loadMap(map);
                        // Set the color of the marker to green
                        Double v =parseGeoPoint.getLatitude();
                        Double v1 =parseGeoPoint.getLongitude();
                        LatLng place = new LatLng(v,v1);


                        ParseGeoPoint userGeo;
                        userLatitude=39.9;
                        userLongitude=-116.0;
                        LatLng userLiveLocation = new LatLng(userLatitude,userLongitude);

                        map.getUiSettings().setZoomControlsEnabled(true);

                        map.addMarker(new MarkerOptions().position(place)
                                .title("Marker in ItemLocation"));
                        map.setMinZoomPreference(10.0f);
                        map.setMaxZoomPreference(25.0f);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(place)      // Sets the center of the map to Mountain View
                                .zoom(17)                   // Sets the zoom
                                //.bearing(90)                // Sets the orientation of the camera to east
                                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        //map.moveCamera(CameraUpdateFactory.newLatLng(place));
                        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                        // Add your locations to bounds using builder.include, maybe in a loop
//                        builder.include(place);
//                        builder.include(userLiveLocation);
//                        LatLngBounds bounds = builder.build();
//                        //Then construct a cameraUpdate
//                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 20);
//                        //Then move the camera
//                        //TODO FIX THIS
//                        map.animateCamera(cameraUpdate);

                    }
                });
            }
        }
    }


    // The Map is verified. It is now safe to manipulate the map.
    protected void loadMap(GoogleMap googleMap) {
        if (googleMap != null) {
            // Attach marker click listener to the map here
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                public boolean onMarkerClick(Marker marker) {
                    // Handle marker click here
                    return true;
                }
            });
        }
    }



}
