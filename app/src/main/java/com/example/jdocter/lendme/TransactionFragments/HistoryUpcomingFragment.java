package com.example.jdocter.lendme.TransactionFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.TransactionAdapters.TransactionAdapter;
import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class HistoryUpcomingFragment extends Fragment {


    TransactionAdapter adapter;
    RecyclerView rvTransactions;
    List<Post> userPosts;
    List<Transaction> userTransactions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_upcoming, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPosts = new ArrayList<>();
        userTransactions = new ArrayList<>();
        rvTransactions = view.findViewById(R.id.rvTransaction);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        // get correct adapter depending on situation
        adapter = getAdapter();
        rvTransactions.setAdapter(adapter);

        loadAllTransactions();

    }

    public void loadAllTransactions() {
        // TODO get rid of transactions of same person

        // posts query
        final Post.Query postQuery = new Post.Query();
        postQuery.dec().withUser().byUser(ParseUser.getCurrentUser());

        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                for (Post post : objects) {
                    userPosts.add(post);
                }
                loadPostTransactions();
                adapter.notifyDataSetChanged();
            }
        });

//        // get posts related to users
//        try {
//            userPosts = postQuery.find();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        loadPostTransactions();
    }

    public void loadPostTransactions() {
        // iterate through posts and get all transactions
        for (Post post: userPosts) {
            // get correct query depending on situation
            ParseQuery query = getQuery(post);


            query.findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                    for (Object transaction:objects) {
                        userTransactions.add((Transaction) transaction);
                        adapter.notifyItemInserted(userTransactions.size() - 1);
                    }
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    List<Transaction> transactions = (ArrayList) o;
                    if (transactions.size() > 0) {
                        for (Transaction t:transactions) {
                            userTransactions.add(t);
                            adapter.notifyItemInserted(userTransactions.size() - 1);
                        }
                    }
                }
            });

//            try {
//                List<Transaction> postTransactions = query.find();
//                userTransactions.addAll(postTransactions);
//                adapter.notifyDataSetChanged();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
        }
    }

    public ParseQuery getQuery(Post post) {
        return post.getTransactionQuery();
    }

    public TransactionAdapter getAdapter() {
        return new TransactionAdapter(userTransactions);
    }

}
