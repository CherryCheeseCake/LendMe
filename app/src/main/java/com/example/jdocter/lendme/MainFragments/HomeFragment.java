package com.example.jdocter.lendme.MainFragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.CreateActivity;
import com.example.jdocter.lendme.HomeFragments.BorrowFragment;
import com.example.jdocter.lendme.HomeFragments.CalenderFragment.CalenderFragment;
import com.example.jdocter.lendme.HomeFragments.LendFragment;
import com.example.jdocter.lendme.HomeFragments.MessageFragment;
import com.example.jdocter.lendme.HomeFragments.NotificationFragment;
import com.example.jdocter.lendme.MyCustomReceiver;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.model.Transaction;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;


public class HomeFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.


    // define your fragments here
    private final Fragment borrowFragment = new BorrowFragment();
    private final Fragment calenderFragment = new CalenderFragment();
    private final Fragment lendFragment = new LendFragment();
    private final Fragment messageFragment = new MessageFragment();
    private final Fragment notificationFragment = new NotificationFragment();
    private String launchCamera = "launchcamera";




    private BottomNavigationView bottomNavigationView;
    private android.support.v7.app.ActionBar actionBar;
    private FloatingActionButton create;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_home, parent, false);


    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        create = view.findViewById(R.id.create);

        final FragmentManager homeFragmentManager = getActivity().getSupportFragmentManager();

        bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(getResources().getDrawable(R.drawable.navigation_bottom_background));

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction fragmentTransaction = homeFragmentManager.beginTransaction();
                        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.opaqueborder));
                        switch (item.getItemId()) {
                            case R.id.message:
                                fragmentTransaction.replace(R.id.homeContainer, messageFragment).commit();
                                return true;

                            case R.id.borrow:
                                fragmentTransaction.replace(R.id.homeContainer, borrowFragment).commit();
                                actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.opaquegradient));
                                return true;
                            case R.id.calender:
                                fragmentTransaction.replace(R.id.homeContainer, calenderFragment).commit();
                                return true;

                            case R.id.lend:
                                fragmentTransaction.replace(R.id.homeContainer, lendFragment).commit();
                                return true;
                            case R.id.notification:
                                fragmentTransaction.replace(R.id.homeContainer, notificationFragment).commit();
                                return true;

                        }
                        return true; // TODO this supposed to be here?
                    }
                }
        );


        Bundle notificationInfo= getActivity().getIntent().getExtras();
        if (notificationInfo!=null) {
            String intentFragment = notificationInfo.getString("frgToLoad");
            switch (intentFragment) {
                case "notification":
                    final String response = notificationInfo.getString("response");
                    if (response != null) {
                        ParseQuery<Transaction> query = ParseQuery.getQuery(Transaction.class);
                        query.getInBackground(notificationInfo.getString("transactionId"), new GetCallback<Transaction>() {
                            public void done(Transaction transaction, ParseException e) {
                                if (e == null) {
                                    if (response.equals("accept")) {
                                        transaction.setStatusCode(2);
                                        transaction.saveInBackground();
                                    } else {
                                        transaction.setStatusCode(6);
                                        transaction.saveInBackground();
                                    }
                                } else {
                                    Log.e("Notification", "can't retrieve transaction from parse query");
                                }
                            }
                        });
                    }
                    bottomNavigationView.setSelectedItemId(R.id.notification);
                    break;
                case "message":
                    bottomNavigationView.setSelectedItemId(R.id.message);
                    break;
                case "calender":
                    bottomNavigationView.setSelectedItemId(R.id.calender);
                    break;
                case "lend":
                    bottomNavigationView.setSelectedItemId(R.id.lend);
                    break;
                default:
                    bottomNavigationView.setSelectedItemId(R.id.borrow);
            }
            NotificationManager manager=
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(MyCustomReceiver.NOTIFICATION_ID);
        }else{
            bottomNavigationView.setSelectedItemId(R.id.borrow);
        }

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), CreateActivity.class);
                i.putExtra(launchCamera, false);
                startActivity(i);
            }
        });

    }

}