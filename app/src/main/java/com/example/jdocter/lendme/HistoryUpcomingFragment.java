package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.TransactionAdapters.HistoryAdapter;
import com.example.jdocter.lendme.TransactionAdapters.TransactionAdapter;
import com.example.jdocter.lendme.TransactionAdapters.UpcomingAdapter;
import com.example.jdocter.lendme.model.Post;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class HistoryUpcomingFragment extends Fragment {

    public Boolean isHistory;
    public static String isHistoryKey = "isHistory";
    TransactionAdapter adapter;
    RecyclerView rvTransactions;
    List<Post> userPosts;
    List<Transaction> userTransactions;

    public void newInstance(boolean history) {
        isHistory = history;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_upcoming, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isHistory = this.getArguments().getBoolean(isHistoryKey);
        userPosts = new ArrayList<>();
        userTransactions = new ArrayList<>();
        rvTransactions = view.findViewById(R.id.rvTransaction);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));
        // get correct adapter depending on situation
        adapter = isHistory ? new HistoryAdapter(userTransactions) : new UpcomingAdapter(userTransactions);
        rvTransactions.setAdapter(adapter);
        loadTransactions();

    }

    public void loadTransactions() {
        // TODO get rid of transactions of same person

        // posts query
        final Post.Query postQuery = new Post.Query();
        postQuery.dec().withUser().byUser(ParseUser.getCurrentUser());

        // get posts related to users
        try {
            userPosts = postQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // iterate through posts and get all transactions
        for (Post post: userPosts) {
            // get correct query depending on situation
            ParseQuery query = isHistory ? post.getPastTransactionQuery() : post.getFutureTransactionQuery();
            try {
                List<Transaction> postTransactions = query.find();
                userTransactions.addAll(postTransactions);
                if (postTransactions.size() >0) adapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
