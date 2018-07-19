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

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.example.jdocter.lendme.LendAdapter;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class LendFragment extends Fragment {

    List<Post> lends;
    LendAdapter lendAdapter;
    RecyclerView rvLends;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lends = new ArrayList<>();
        rvLends = view.findViewById(R.id.rvLends);
        lendAdapter = new LendAdapter(lends);
        rvLends.setLayoutManager(new GridLayoutManager(getActivity(),3));

        // TODO load posts


        rvLends.setAdapter(lendAdapter);
        RecyclerViewHeader recyclerHeader = (RecyclerViewHeader) view.findViewById(R.id.header);
        recyclerHeader.attachTo(rvLends);


    }

    private void loadTopPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.dec().withUser().getTop();

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        lends.add(objects.get(i));
                        lendAdapter.notifyItemInserted(lends.size() - 1);
                    }
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }
}
