package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

public class WhatsNewSliderAdapter extends PagerAdapter{

    Context context;
    LayoutInflater layoutInflater;
    StorageReference whatsNewStorageRef;
    FloatingActionButton addtocartWhatsNew;

    public WhatsNewSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Home.whatsNewArr.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

//        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.whatsnew_image_slide, container, false);

        ImageView whatsNewImage = view.findViewById(R.id.whatsNewImage);
        final FloatingActionButton addtocartWhatsNew = view.findViewById(R.id.addtocartWhatsNew);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);

        whatsNewStorageRef = Home.storageRef.child(Home.whatsNewArr.get(position).get("pic").toString());
        Glide.with(whatsNewImage.getContext())
                .using(new FirebaseImageLoader())
                .load(whatsNewStorageRef)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(whatsNewImage);

        addtocartWhatsNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtocartWhatsNew.setImageResource(R.drawable.addedtocart_icon);
            }
        });

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((ConstraintLayout) object);
    }



}
