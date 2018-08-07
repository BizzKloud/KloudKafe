package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

public class AdvertisementSliderAdapter extends PagerAdapter{

    Context context;
    LayoutInflater layoutInflater;
    StorageReference advStorageRef;


    public AdvertisementSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Home.advertisementArr.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.advertisement_image_slide, container, false);
        ImageView advImage = view.findViewById(R.id.advImage);
        advStorageRef = Home.storageRef.child(Home.advertisementArr.get(position).get("pic").toString());
        Glide.with(advImage.getContext())
                .using(new FirebaseImageLoader())
                .load(advStorageRef)
                .into(advImage);

        container.addView(view);

        return view;

    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((ConstraintLayout) object);
    }
}
