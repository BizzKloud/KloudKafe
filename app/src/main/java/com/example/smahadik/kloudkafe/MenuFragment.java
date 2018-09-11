package com.example.smahadik.kloudkafe;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Timer;

public class MenuFragment extends Fragment {

    View view;

    public static LinearLayout dotsLayout;
    public static ImageView [] dots;
    ViewPager whatsNewViewPager;
    public static WhatsNewSliderAdapter whatsNewSliderAdapter;
    public static RecyclerViewAdapterVendorList recyclerViewAdapterVendorList;
    RecyclerView recyclerviewVendorList;

    public static int position = 0;
    int whatsNewTimer;

    Handler handler;
    public static Boolean cancelled = false;
    Boolean runnableRunning = true;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.menu_fragment, container, false);


        // WhatsNew List
        whatsNewViewPager = view.findViewById(R.id.whatsNewViewPager);
        dotsLayout = view.findViewById(R.id.dotsLayout);
        dots = new ImageView[Home.whatsNewArr.size()];
        whatsNewSliderAdapter = new WhatsNewSliderAdapter(getContext());
        whatsNewViewPager.setAdapter(whatsNewSliderAdapter);
        whatsNewViewPager.addOnPageChangeListener(viewListener);
        setDotsLayout(position);


        if(handler == null) {
            Log.i("Handler" , "Created NEW");
            handler = new Handler();
            handler.postDelayed(runnable, 0);
        }

        // Vendor List
        recyclerviewVendorList = view.findViewById(R.id.recyclerviewVendorList);
        recyclerViewAdapterVendorList = new RecyclerViewAdapterVendorList(getContext());
        recyclerviewVendorList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerviewVendorList.setAdapter(recyclerViewAdapterVendorList);


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        Log.i("MENU" , " RESUMED TRUE");
        if(!Home.whatsNewArr.isEmpty()) {
            if (!runnableRunning) {
                position = 0;
                whatsNewViewPager.setCurrentItem(position);
                setDotsLayout(0);
                whatsNewTimer = Integer.parseInt(Home.whatsNewArr.get(position).get("time").toString()) * 1000;
                handler.postDelayed(runnable, 0);
            }

        }

    }



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            whatsNewViewPager.setCurrentItem(position);
            whatsNewTimer = Integer.parseInt(Home.whatsNewArr.get(position).get("time").toString()) * 1000;

            position++;
            if (position == Home.whatsNewArr.size()) {
                position = 0;
                Log.i("MENU Position CHANGED" , String.valueOf(position) );

            }
            if(!cancelled) {
                Log.i("Runnable WhatsNew", "NOt Cancelled");
                runnableRunning = true;
                handler.postDelayed(this, whatsNewTimer);
            }else {
                runnableRunning = false;
                Log.i("Runnable WhatsNew", "Cancelled");
            }
        }

    };




    // ViewPager Listener
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position) {
            MenuFragment.position = position;
            setDotsLayout(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };



    // Setting Up DotsLayout
    public void setDotsLayout(int pos) {

        dotsLayout.removeAllViews();
        for(int i=0; i<Home.whatsNewArr.size(); i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageResource(R.drawable.smallcirclegrey);
            dotsLayout.addView(dots[i]);
        }

        dots[pos].setImageResource(R.drawable.smallcirclewhite);
    }
    public void setDotsLayout(int pos, Context context) {

        dotsLayout.removeAllViews();
//        Log.i("WhatsNew Array" , (String.valueOf(Home.whatsNewArr.size())) );
        for(int i=0; i<Home.whatsNewArr.size(); i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageResource(R.drawable.smallcirclegrey);
            dotsLayout.addView(dots[i]);
        }
        dots[pos].setImageResource(R.drawable.smallcirclewhite);
    }



//    @Override
//    public Context getContext() {
//        return super.getContext();
//    }
}
