package com.example.jdocter.lendme.MainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.jdocter.lendme.HistoryUpcomingFragment;
import com.example.jdocter.lendme.R;

public class TransactionFragment extends Fragment {


    Fragment historyFragment;
    Fragment upcomingFragment;
    FragmentManager fragmentManager;
    FrameLayout flTransactions;
    TabLayout tabs;
    public static String isHistoryKey= "isHistory";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        flTransactions = view.findViewById(R.id.flTransaction);
        tabs =view.findViewById(R.id.tabs);
        fragmentManager = getActivity().getSupportFragmentManager();

        // history fragment and bundle
        historyFragment = new HistoryUpcomingFragment();
        Bundle historyBundle = new Bundle();
        historyBundle.putBoolean(isHistoryKey,true);
        historyFragment.setArguments(historyBundle);

        // upcoming fragment and bundle
        upcomingFragment = new HistoryUpcomingFragment();
        Bundle upcomingBundle = new Bundle();
        upcomingBundle.putBoolean(isHistoryKey,false);
        upcomingFragment.setArguments(upcomingBundle);


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (tab.getPosition()) {
                    case 0: fragmentTransaction.replace(R.id.flTransaction, historyFragment).commit();
                    case 1: fragmentTransaction.replace(R.id.flTransaction, upcomingFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });

        tabs.getTabAt(0).select();

    }

}
