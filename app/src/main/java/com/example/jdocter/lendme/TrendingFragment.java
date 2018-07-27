package com.example.jdocter.lendme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    ArrayList<Post> posts;
    private SwipeRefreshLayout swipeRefreshLayout;
    TrendingAdapter postAdapter;
    RecyclerView rvPost;
    private Callback callback;
    private String launchCamera = "launchcamera";

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    /**
     * interface to get the user's live location which is accessed in MainActivity
     */
    public interface Callback {
        ParseGeoPoint getLiveLoc();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Callback) {
            this.callback = (Callback) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_trending, container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity();
        rvPost = view.findViewById(R.id.rvTrending);
        rvPost.setHasFixedSize(true);
        posts = new ArrayList<>();
        postAdapter = new TrendingAdapter(posts);
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        final ParseUser user= ParseUser.getCurrentUser();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTopPosts(user);
            }
        });

        staggeredGridLayoutManager=new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        rvPost.setLayoutManager(staggeredGridLayoutManager);
        rvPost.setAdapter(postAdapter);



        loadTopPosts(user);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem createItem = menu.findItem(R.id.add_button);
        createItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent i = new Intent(getContext(),CreateActivity.class);
                i.putExtra(launchCamera,false);
                startActivity(i);
                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                try {
                    fetchItemsByCloseness(query);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                searchView.clearFocus();

                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                posts.clear();
                loadTopPosts(ParseUser.getCurrentUser());
                return true;
            }

        });

    }


    public void fetchItemsByCloseness(String keyword) throws ParseException {

        final Post.Query query = new Post.Query();
        query.byItem(keyword);
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    posts.clear();
                    for (Post item:itemList){
                        posts.add(item);
                    }
                    postAdapter.notifyDataSetChanged();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void loadTopPosts(ParseUser user){
        ParseGeoPoint userGeoPoint = callback.getLiveLoc();

        final Post.Query postQuery = new Post.Query();
        postQuery.notByUser(user).withUser().byProximity(userGeoPoint).dec();

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {

                    postAdapter.mPosts.clear();
                    posts.clear();
                    posts.addAll(objects);
                    postAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    /*
                    for (int i = 0; i < objects.size(); i++) {
                        posts.add(objects.get(i));
//                        postAdapter.notifyItemInserted(posts.size() - 1);
                    }
                    postAdapter.notifyDataSetChanged();
                    */
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

}