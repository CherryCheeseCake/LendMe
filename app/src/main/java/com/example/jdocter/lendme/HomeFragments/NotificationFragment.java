package com.example.jdocter.lendme.HomeFragments;

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
import com.example.jdocter.lendme.SimpleNotificationAdapter;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    private List<Transaction> transactions;
    private RecyclerView rvNotification;
    private SimpleNotificationAdapter adapter;
    private SwipeController swipeController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotification = view.findViewById(R.id.rvNotification);
        transactions = new ArrayList<>();
        adapter = new SimpleNotificationAdapter(transactions);
        rvNotification.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rvNotification.setAdapter(adapter);
//        RecyclerViewHeader recyclerHeader = (RecyclerViewHeader) view.findViewById(R.id.notificationHeader);
//        recyclerHeader.attachTo(rvNotification);

        loadAllTransactions();


    }


    public void loadAllTransactions() {
        // TODO query by updated at date and put constraint: today's date <= end date

        final Transaction.Query transactionBorrowerQuery = new Transaction.Query();

//        // query: all transactions where current user is the borrower, ordered by newest updates, excluding items on hold (status code 0), and items current user cancelled
//        transactionBorrowerQuery.dec().includesStatusCode(1).excludeStatusCode(0).withUser().byBorrower(ParseUser.getCurrentUser());
//
//        transactionBorrowerQuery.findInBackground(new FindCallback<Transaction>() {
//            @Override
//            public void done(List<Transaction> objects, ParseException e) {
//                for (Transaction t : objects) {
//                    transactions.add(t);
//                    adapter.notifyItemInserted(transactions.size()-1);
//                }
//            }
//        });

        final Transaction.Query transactionLenderQuery = new Transaction.Query();
        // query: all transactions where current user is the lender, ordered by newest updates, excluding items on hold (status code 0), and items current user cancelled
        transactionLenderQuery.dec().includesStatusCode(1).excludeStatusCode(0).withUser().byLender(ParseUser.getCurrentUser());

        transactionLenderQuery.findInBackground(new FindCallback<Transaction>() {
            @Override
            public void done(List<Transaction> objects, ParseException e) {
                for (Transaction t : objects) {
                    transactions.add(t);
                    adapter.notifyItemInserted(transactions.size()-1);
                }
            }
        });
    }
}
