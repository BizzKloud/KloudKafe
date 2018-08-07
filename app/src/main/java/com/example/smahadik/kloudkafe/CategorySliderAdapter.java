package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import static java.security.AccessController.getContext;

public class CategorySliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    View view;
    RecyclerView recyclerviewFoodItemList;


    public CategorySliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Home.categoryArr.get(Home.vendorPosition).size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.category_item_list_fragment, container, false);

        recyclerviewFoodItemList = view.findViewById(R.id.recyclerviewFoodItemList);
        RecyclerViewAdapterFoodItemList recyclerViewAdapterFoodItemList = new RecyclerViewAdapterFoodItemList(context, position);
        recyclerviewFoodItemList.setLayoutManager(new GridLayoutManager(context , 1));
        recyclerviewFoodItemList.setAdapter(recyclerViewAdapterFoodItemList);

        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Home.categoryArr.get(Home.vendorPosition).get(position).get("name").toString();
    }





}
