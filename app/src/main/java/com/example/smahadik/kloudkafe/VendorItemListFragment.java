package com.example.smahadik.kloudkafe;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
    public static ViewPager foodItemListViewPager;
    public static TabLayout categoryTabs;
    Toolbar toolbar;
    ImageButton backButtontoMenu;
    ImageButton changeVendorButton;
    TextView textViewvendorNameTitle;
    public static DrawerLayout drawerLayout;
    public static CategorySliderAdapter categorySliderAdapter;
    Home home = new Home();
    int venpos;
    int lastvenpos;


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
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.vendor_item_list_fragment, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        backButtontoMenu = toolbar.findViewById(R.id.backButtontoMenu);
        changeVendorButton = view.findViewById(R.id.changeVendorButton);
        textViewvendorNameTitle = toolbar.findViewById(R.id.textViewvendorNameTitle);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Log.i("Called again" , "TRUE");

        textViewvendorNameTitle.setText(Home.vendorArr.get(Home.vendorPosition).get("name").toString());

        backButtontoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("BACK BUTTON PRESSED" , "ON MAIN MENU");
                home.stopImageSwitcher(true , false);
                Home.vendorPosition = -1;
                Home.lastVendorPosition = -1;
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
//                drawerLayout.openDrawer(Gravity.END);
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction ft = fragmentManager.beginTransaction();
//                ft.replace(R.id.cartDrawerFrameLayout, new CartFragment());
//                ft.commit();
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                Home.progressDialog.setMessage("Loading Your Cart");
                Home.progressDialog.show();
                venpos = Home.vendorPosition;
                lastvenpos = Home.lastVendorPosition;
                Log.i("Called Opened", String.valueOf(lastvenpos) );
                Home.catPosition = foodItemListViewPager.getCurrentItem();
//                Home.fragmentManager = getFragmentManager();
//                Home.ft = Home.fragmentManager.beginTransaction();
//                if(Home.flagCartFirst) {
//                    Log.i("Flag CART FIRST " ,  "Flag CART FIRST = TRUE");
//                    Home.ft.replace(R.id.cartDrawerFrameLayout, new CartFragment(), CartFragment.class.getName()+"1");
//                    Home.ft.addToBackStack(null);
//                    Log.i("BACKSTACK ENTRY LIST", String.valueOf(getFragmentManager().getBackStackEntryCount()));
//                    Home.flagCartFirst = false;
//                } else {
//                    Log.i("Flag CART SECOND " ,  "Flag CART SECOND = TRUE");
//                    Home.ft.replace(R.id.cartDrawerFrameLayout, Home.fragmentManager.findFragmentByTag(CartFragment.class.getName()+"1"));
//                }
//                Home.ft.commit();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.cartDrawerFrameLayout, new CartFragment());
                ft.commit();
                Home.progressDialog.dismiss();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Home.progressDialog.setMessage("Loading Menu");
                Home.progressDialog.show();
                Home.vendorPosition = lastvenpos;
                Home.lastVendorPosition = lastvenpos;
                setupViewPager();
                foodItemListViewPager.setCurrentItem(Home.catPosition);

                Log.i("Called Closed", String.valueOf(lastvenpos) );
                Log.i("Called Closed cat", String.valueOf(Home.catPosition) );
                Home.progressDialog.dismiss();
                super.onDrawerClosed(drawerView);
            }
        });


        foodItemListViewPager = (ViewPager) view.findViewById(R.id.foodItemListViewPager);
//        foodItemListViewPager.addOnPageChangeListener(viewListener);
        setupViewPager();
        return view;
    }



    private void setupViewPager() {
        categorySliderAdapter = new CategorySliderAdapter(getContext());
        foodItemListViewPager.setAdapter(categorySliderAdapter);
        categoryTabs = (TabLayout) view.findViewById(R.id.categoryTabs);
        categoryTabs.setupWithViewPager(foodItemListViewPager);
    }







}
