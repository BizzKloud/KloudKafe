package com.example.smahadik.kloudkafe;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AdvertisementFragment extends Fragment {


    NonSwipeableViewPager advertisementViewPager;
    AdvertisementSliderAdapter advertisementSliderAdapter;

    int position = 0;
    int advTimer;

    Handler handler;
    View view;
    public static Boolean cancelled = false;
    Boolean runnableRunning = true;


    int currentPage;


//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Log.i("CREATED " ,  "ATT ON CREATE IN ADVERTISEMENT");
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.advertisement_fragment, container, false);
        advertisementViewPager = view.findViewById(R.id.advertisementViewPager);

        advertisementSliderAdapter = new AdvertisementSliderAdapter(getContext());
        advertisementViewPager.setAdapter(advertisementSliderAdapter);

//        advertisementViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//            }
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                // not needed
//            }
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    int pageCount = Home.advertisementArr.size();
//
//                    if (currentPage == 0){
//                        advertisementViewPager.setCurrentItem(pageCount-2,true);
//                    } else if (currentPage == pageCount-1){
//                        advertisementViewPager.setCurrentItem(0,true);
//                    }
//                }
//            }
//        });


        if(handler == null) {
            Log.i("Handler" , "Created NEW");
            handler = new Handler();
            handler.postDelayed(runnable, 0);
        }


        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

        Log.i("ADVERTISEMNT" , " RESUMED TRUE");

        if (!runnableRunning) {
            position = 0;
            advertisementViewPager.setCurrentItem(position);
            Log.i("RUNNABLE" , " STARTED AGAIN ");
            handler.postDelayed(runnable, 0);
        }
    }



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Log.i("Runnable", "STarted");
            Log.i("Position ADV" , String.valueOf(position));

            advertisementViewPager.setCurrentItem(position);
            advTimer = Integer.parseInt(Home.advertisementArr.get(position).get("timer").toString()) * 1000;

            position++;

            if (position == Home.advertisementArr.size()) {
                position = 0;
            }

            if(!cancelled) {
                Log.i("Runnable ADV", "NOT cancelled");
                runnableRunning = true;
                handler.postDelayed(this,  advTimer);
            }else {
                runnableRunning = false;
                Log.i("Runnable ADV", "Cancelled");
            }
        }

    };









}
