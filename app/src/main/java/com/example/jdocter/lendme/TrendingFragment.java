package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    ArrayList<Post> posts;
    private SwipeRefreshLayout swipeRefreshLayout;
    TrendingAdapter postAdapter;
    RecyclerView rvPost;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_trending, container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPost = view.findViewById(R.id.rvTrending);
        rvPost.setHasFixedSize(true);
        posts = new ArrayList<>();
        postAdapter = new TrendingAdapter(posts);

        staggeredGridLayoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        rvPost.setLayoutManager(staggeredGridLayoutManager);
        rvPost.setAdapter(postAdapter);


        ParseUser user= ParseUser.getCurrentUser();
        loadTopPosts(user);

    }


    private void loadTopPosts(ParseUser user){
        final Post.Query postQuery = new Post.Query();
        postQuery.dec().withUser().byUser(user);

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        posts.add(objects.get(i));
                        postAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}