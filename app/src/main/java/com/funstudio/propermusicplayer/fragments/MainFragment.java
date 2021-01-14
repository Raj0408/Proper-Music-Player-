package com.funstudio.propermusicplayer.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.funstudio.propermusicplayer.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment  {
    ImageView searchButton,menuIcon;
    TabLayout tabLayout;
    MyPagerAdapter adapter;
    ViewPager viewPager;



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      adapter = new MyPagerAdapter(getChildFragmentManager());
        searchButton = view.findViewById(R.id.search_icon);
        menuIcon = view.findViewById(R.id.menu_iconfrag);

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00B0FF"));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        addfrags();

    }

    public void addfrags()
    {
        adapter.addFragment(new SongsFragment(), "Songs");
       adapter.addFragment(new AlbumsFragment(),"Albums");
        adapter.notifyDataSetChanged();

    }

    class MyPagerAdapter extends FragmentPagerAdapter
    {

        private List<Fragment> fragments = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    public Fragment getFragmentByPosition(int position) {
        return (adapter != null) ? adapter.getItem(position) : null;
    }
}