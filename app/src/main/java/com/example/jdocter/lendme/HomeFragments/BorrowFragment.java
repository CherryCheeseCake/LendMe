package com.example.jdocter.lendme.HomeFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jdocter.lendme.FavoritesFragment;
import com.example.jdocter.lendme.R;
import com.example.jdocter.lendme.TrendingFragment;

import java.util.ArrayList;
import java.util.List;

public class BorrowFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_borrow,container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
        return view;
        //return inflater.inflate(R.layout.fragment_borrow, container, false);

    }

    private void setupViewPager(ViewPager viewPager){
        Adapter adapter= new Adapter(getChildFragmentManager());
        adapter.addFragment(new FavoritesFragment(), "{Favorites Fragment");
        adapter.addFragment(new TrendingFragment(), "Trending Fragment");
        viewPager.setAdapter(adapter);
    }
static class Adapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager){
            super(manager);
        }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*//setContentView(R.layout.activity_main);


            // Get the ViewPager and set it's PagerAdapter so that it can display items
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            viewPager.setAdapter(new FavoritesAdapter(getSupportFragmentManager, getContext()));

            viewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager(),
                    getContext()));

            // Give the TabLayout the ViewPager
            TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);


            int[] imageResId={
                    R.drawable.borrow_tending,
                    R.drawable.borrow_favorite
            };

            for (int i=0; i<imageResId.length; i++){
                tabLayout.getTabAt(i).setIcon(imageResId[i]);
            }*/

    }


}
