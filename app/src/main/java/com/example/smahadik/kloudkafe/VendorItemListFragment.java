package com.example.smahadik.kloudkafe;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class VendorItemListFragment extends Fragment{


    View view;
    ViewPager foodItemListViewPager;
    TabLayout categoryTabs;
    Toolbar toolbar;
    ImageButton backButtontoMenu;
    Button changeVendorButton;
    TextView textViewvendorNameTitle;
    DrawerLayout changeVendorDrawerLayout;
    Home home = new Home();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        if (activity != null) {
//            activity.getSupportActionBar().show();
//            activity.getSupportActionBar().setTitle(Home.vendorArr.get(Home.vendorPosition).get("name").toString());
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.vendor_item_list_fragment, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        backButtontoMenu = toolbar.findViewById(R.id.backButtontoMenu);
        changeVendorButton = view.findViewById(R.id.changeVendorButton);
        textViewvendorNameTitle = toolbar.findViewById(R.id.textViewvendorNameTitle);
        changeVendorDrawerLayout = view.findViewById(R.id.changeVendorDrawerLayout);
        changeVendorDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        textViewvendorNameTitle.setText(Home.vendorArr.get(Home.vendorPosition).get("name").toString());

        backButtontoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BACK BUTTON PRESSED" , "ON MAIN MENU");
                home.stopImageSwitcher(true , false);
                Home.ft = Home.fragmentManager.beginTransaction();
                Fragment fragment  =  new MenuFragment();
                Fragment frag =  getFragmentManager().findFragmentByTag(fragment.getClass().getName());
                Home.ft.replace(R.id.rootLayout, frag);
                Home.ft.commit();
            }
        });

        changeVendorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVendorDrawerLayout.openDrawer(Gravity.END);
            }
        });

        foodItemListViewPager = (ViewPager) view.findViewById(R.id.foodItemListViewPager);
        setupViewPager();
        foodItemListViewPager.addOnPageChangeListener(viewListener);

        categoryTabs = (TabLayout) view.findViewById(R.id.categoryTabs);
        categoryTabs.setupWithViewPager(foodItemListViewPager);

        return view;
    }

    // ViewPager Listener
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
//            Home.catPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



    private void setupViewPager() {
        CategorySliderAdapter categorySliderAdapter = new CategorySliderAdapter(getContext());
        foodItemListViewPager.setAdapter(categorySliderAdapter);
    }







}
