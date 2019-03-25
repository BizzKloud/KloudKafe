package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CategorySliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    View view;
    RecyclerView recyclerviewFoodItemList;
    public static RecyclerViewAdapterFoodItemList recyclerViewAdapterFoodItemList;


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
        recyclerViewAdapterFoodItemList = new RecyclerViewAdapterFoodItemList(context, position);
        recyclerviewFoodItemList.setLayoutManager(new GridLayoutManager(context , 1));
        recyclerviewFoodItemList.setAdapter(recyclerViewAdapterFoodItemList);

//        Home.home.setAdvCounter();
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
