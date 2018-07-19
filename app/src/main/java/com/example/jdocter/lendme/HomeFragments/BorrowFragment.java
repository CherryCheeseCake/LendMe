package com.example.jdocter.lendme.HomeFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.R;

public class BorrowFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_borrow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);

            // Get the ViewPager and set it's PagerAdapter so that it can display items
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPager.setAdapter(new SampleFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                    getContext()));

            // Give the TabLayout the ViewPager
            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);

    }
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                BorrowFragment.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    */
}
