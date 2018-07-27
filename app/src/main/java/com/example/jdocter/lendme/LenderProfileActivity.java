package com.example.jdocter.lendme;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.example.jdocter.lendme.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LenderProfileActivity extends AppCompatActivity {

    RecyclerView rvLend;
    LenderProfileAdapter profileAdapter;
    List<Post> posts;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_lender);

        posts=new ArrayList<>();

        //initialize variables
        rvLend=(RecyclerView) findViewById(R.id.rvLend);
        rvLend.setHasFixedSize(true);
        profileAdapter=new LenderProfileAdapter(posts);

        //get user
        ParseUser user=ParseUser.getCurrentUser();

        //set adapter
        rvLend.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        rvLend.setAdapter(profileAdapter);

        //load posts
        loadUserPosts(user);
    }

    private void loadUserPosts(ParseUser user){
        final Post.Query postQuery = new Post.Query();
        postQuery.dec().withUser().byUser(user);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e==null){
                    for (int i=0;i<objects.size();i++){
                        posts.add(objects.get(i));
                        profileAdapter.notifyItemInserted(posts.size()-1);
//                        profileAdapter.notifyDataSetChanged();
                    }
                }else {
                    Log.d("item", "Error: "+ e.getMessage());
                }
            }
        });
    }
}
