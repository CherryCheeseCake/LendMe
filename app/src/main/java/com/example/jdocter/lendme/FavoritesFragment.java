package com.example.jdocter.lendme;

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
import com.example.jdocter.lendme.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment{

    private ArrayList<Post> posts;
    private SwipeRefreshLayout swipeRefreshLayout;
    FavoritesAdapter postAdapter;
    RecyclerView rvPost;
    private String launchCamera = "launchcamera";

    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    final User parseUser = (User) ParseUser.getCurrentUser();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view= inflater.inflate(R.layout.fragment_favorites, container,false);
        setHasOptionsMenu(true);
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPost = view.findViewById(R.id.rvFavorites);
        rvPost.setHasFixedSize(true);
        posts = new ArrayList<>();
        postAdapter = new FavoritesAdapter(posts);
        swipeRefreshLayout= view.findViewById(R.id.swipeContainer);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTopPosts();
            }
        });

        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rvPost.setLayoutManager(staggeredGridLayoutManager);
        rvPost.setAdapter(postAdapter);

        ParseUser user= ParseUser.getCurrentUser();
        loadTopPosts();

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
                loadTopPosts();
                return true;
            }

        });
    }



    public void fetchItemsByCloseness(String keyword) throws ParseException {

            final User.QueryFavorites query = new User.QueryFavorites(parseUser.getFavoritePostsQuery());
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



    private void loadTopPosts(){

        final User.QueryFavorites postQuery = new User.QueryFavorites(parseUser.getFavoritePostsQuery());
        postQuery.dec().getTop();

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
//                        postAdapter.notifyItemInserted();
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

