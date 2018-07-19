package com.example.jdocter.lendme.HomeFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.jdocter.lendme.CircleTransform;
import com.example.jdocter.lendme.LendAdapter;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LendFragment extends Fragment {

    List<Post> posts;
    LendAdapter lendAdapter;
    RecyclerView rvLends;
    TextView tvfullName;
    TextView tvUsername;
    ImageView ivProfileImage;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // declarations
        posts = new ArrayList<>();
        rvLends = view.findViewById(R.id.rvLends);
        lendAdapter = new LendAdapter(posts);
        rvLends.setLayoutManager(new GridLayoutManager(getActivity(),3));
        tvfullName = view.findViewById(R.id.tvname);
        tvUsername = view.findViewById(R.id.tvUsername);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        ParseUser user = ParseUser.getCurrentUser();


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
        tvUsername.setText(user.getUsername());

        // load image using glide
        Glide.with(getActivity())
                .load(profileUrl)
                .apply(new RequestOptions().transform(new CircleTransform(getActivity())))
                .into(ivProfileImage);

        // set adapter and recycler view header
        rvLends.setAdapter(lendAdapter);
        RecyclerViewHeader recyclerHeader = (RecyclerViewHeader) view.findViewById(R.id.header);
        recyclerHeader.attachTo(rvLends);

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
