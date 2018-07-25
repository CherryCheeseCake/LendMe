package com.example.jdocter.lendme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.model.Post;
import com.parse.ParseException;
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
        isImageFitToScreen=false;
        final String objectId = getIntent().getStringExtra("objectId");


        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailPostActivity.this, ItemCalendarActivity.class);
                i.putExtra("objectId",objectId);
                startActivity(i);

            }

        });

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        try {
            Post post =query.get(objectId);
            try {
                String username = post.getUser().fetchIfNeeded().getUsername();
                tvUsername.setText(username);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }

            String ItemUrl = post.getImage().getUrl();
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
            tvPrice.setText("$"+Double.toString(post.getPrice()));
            //tvPrice.setText("$"+Integer.toString(post.getPrice()));
            final Post mPost = post;
            if (mPost.hasLiked()) {
                ibLikes.setImageResource(R.drawable.ufi_heart_active);
            }else {
                ibLikes.setImageResource(R.drawable.ufi_heart);
            }

            ibLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = ParseUser.getCurrentUser();
                    if (mPost.hasLiked()) {
                        ibLikes.setImageResource(R.drawable.ufi_heart);
                        mPost.unlikePost();
                        mPost.saveInBackground();
                    } else {
                        ibLikes.setImageResource(R.drawable.ufi_heart_active);
                        mPost.likePost();
                        mPost.saveInBackground();

                    }
                }
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
