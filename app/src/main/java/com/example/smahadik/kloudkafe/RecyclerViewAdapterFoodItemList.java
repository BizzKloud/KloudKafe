package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RecyclerViewAdapterFoodItemList extends RecyclerView.Adapter<RecyclerViewAdapterFoodItemList.MyViewHolderFoodItem>  {

    private Context context;
    int catpos;
    LayoutInflater layoutInflater;
    View view;
    StorageReference fooditemStorageRef;
    Home home = new Home();
    CartFragment cartFragment = new CartFragment();


    public RecyclerViewAdapterFoodItemList (Context context, int catpos) {
        this.context = context;
        this.catpos = catpos;
    }

    @NonNull
    @Override
    public MyViewHolderFoodItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.fooditem_cardview, parent , false);
        return new MyViewHolderFoodItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderFoodItem holder, int position) {

        final HashMap foodItemHashMap = Home.foodItemArr.get(Home.vendorPosition).get(catpos).get(position);
        fooditemStorageRef = Home.storageRef.child(foodItemHashMap.get("pic").toString());
        Glide.with(holder.imageViewFoodItem.getContext())
                .using(new FirebaseImageLoader())
                .load(fooditemStorageRef)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageViewFoodItem);

        holder.textViewFoodItemName.setText(foodItemHashMap.get("name").toString());
        holder.textViewFoodItemSdesp.setText(foodItemHashMap.get("sdesp").toString());
        holder.ratingbarFoodItemRating.setRating(Integer.parseInt(foodItemHashMap.get("rating").toString()));
        String amount = Home.currencyFc + Home.formatter.format(Double.parseDouble(foodItemHashMap.get("amount").toString()));
        holder.textViewFoodItemAmount.setText(amount);
        if(home.checkFoodIteminCart(foodItemHashMap)) {
            holder.addtocartMenu.setImageResource(R.drawable.addedtocart_orangeicon);
        }else {
            holder.addtocartMenu.setImageResource(R.drawable.addtocart_blueicon);
        }

        holder.addtocartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add or Remove  to/from Cart
                if(home.checkFoodIteminCart(foodItemHashMap)) {
                    holder.addtocartMenu.setImageResource(R.drawable.addtocart_blueicon);
                    home.removefromcart(foodItemHashMap);
                    VendorItemListFragment.drawerLayout.openDrawer(Gravity.END);
                }else {
                    holder.addtocartMenu.setImageResource(R.drawable.addedtocart_orangeicon);
                    home.addtocart(foodItemHashMap);
                    VendorItemListFragment.drawerLayout.openDrawer(Gravity.END);
                }
            }
        });

    }

//    public boolean checkFoodIteminCart(HashMap fooditem) {
//        if(Home.cartArr.size() > 0) {
//            for(int i=0; i<Home.cartArr.size(); i++) {
//                if(Home.cartArr.get(i).get(0).get("venid").toString().equals(fooditem.get("venid").toString())) {
//                    //found Venid- vendor
//                    for(int j=0; j<Home.cartArr.get(i).size(); j++) {
//                        if(Home.cartArr.get(i).get(j).get("fdid").toString().equals(fooditem.get("fdid").toString())
//                                && Home.cartArr.get(i).get(j).get("catid").toString().equals(fooditem.get("catid").toString()) ) {
//                            // found fdid- foodItem
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }

    @Override
    public int getItemCount() {
        return Home.foodItemArr.get(Home.vendorPosition).get(catpos).size();
    }

    public static class MyViewHolderFoodItem extends RecyclerView.ViewHolder {

        ImageView imageViewFoodItem;
        TextView textViewFoodItemName;
        TextView textViewFoodItemSdesp;
        RatingBar ratingbarFoodItemRating;
        TextView textViewFoodItemAmount;
        ImageButton addtocartMenu;
        ProgressBar progressBar;

        public MyViewHolderFoodItem(View itemView) {
            super(itemView);

            imageViewFoodItem = itemView.findViewById(R.id.imageViewFoodItem);
            textViewFoodItemName = itemView.findViewById(R.id.textViewFoodItemName);
            textViewFoodItemSdesp = itemView.findViewById(R.id.textViewFoodItemSdesp);
            ratingbarFoodItemRating = itemView.findViewById(R.id.ratingbarFoodItemRating);
            textViewFoodItemAmount = itemView.findViewById(R.id.textViewFoodItemAmount);
            addtocartMenu = itemView.findViewById(R.id.addtocartMenu);
            progressBar = itemView.findViewById(R.id.progressBarFoodItem);

        }
    }


}
