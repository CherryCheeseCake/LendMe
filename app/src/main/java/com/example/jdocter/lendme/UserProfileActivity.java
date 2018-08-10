package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = "UserProfileActivity";
    private List<Post> posts;
    private LendAdapter lendAdapter;
    private RecyclerView rvLends;
    private TextView tvfullName;
    private TextView tvUsername;
    private ImageView ivProfileImage;
    private final static String userId = "userId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // declarations
        posts = new ArrayList<>();
        rvLends = findViewById(R.id.rvLends);
        lendAdapter = new LendAdapter(posts);
        rvLends.setLayoutManager(new GridLayoutManager(this,3));
        tvfullName = findViewById(R.id.tvname);
        tvUsername = findViewById(R.id.tvUsername);
        ivProfileImage = findViewById(R.id.ivProfileImage);

        final String objectUserId = getIntent().getStringExtra(userId);

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        User user = null;
        try {
            user = query.get(objectUserId);

            // populate profile views
            try {
                tvfullName.setText(user.fetch().getString("fullName"));
            } catch (ParseException e) { e.printStackTrace();
            }
            String profileUrl = null;
            try {
                profileUrl = user.fetch().getParseFile("profileImage").getUrl();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvUsername.setText("@" + user.getUsername());

            Glide.with(this)
                    .load(profileUrl)
                    .apply(new RequestOptions().transform(new CircleTransform(this)))
                    .into(ivProfileImage);

            // set adapter and recycler view header
            rvLends.setAdapter(lendAdapter);
            RecyclerViewHeader recyclerHeader = (RecyclerViewHeader) findViewById(R.id.header);
            recyclerHeader.attachTo(rvLends);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // load user's items
        loadUserPosts(user);

    }

    private void loadUserPosts(ParseUser user) {
        final Post.Query postQuery = new Post.Query();
        postQuery.dec().withUser().byUser(user);

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        posts.add(objects.get(i));
                        lendAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
