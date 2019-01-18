package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RecyclerViewAdapterYmalList extends RecyclerView.Adapter<RecyclerViewAdapterYmalList.MyViewHolderYmalCart> {

    Context context;
    LayoutInflater layoutInflater;
    View view;
    StorageReference ymalStorageRef;
    Home home = new Home();
    CartFragment cartFragment = new CartFragment();

    public RecyclerViewAdapterYmalList (Context context) { this.context = context;  }

    @NonNull
    @Override
    public MyViewHolderYmalCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.ymal_cardview, parent, false);
        return new MyViewHolderYmalCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderYmalCart holder, int position) {

        final HashMap foodItem = home.findFoodItem(Home.ymalArr.get(position));
        if(foodItem != null) {

            ymalStorageRef = Home.storageRef.child(foodItem.get("pic").toString());
            Glide.with(holder.imageViewYmal.getContext())
                    .using(new FirebaseImageLoader())
                    .load(ymalStorageRef)
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            holder.progressBarYMAL.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.imageViewYmal);

            holder.textViewYmalFoodItemName.setText(foodItem.get("name").toString());
            holder.ymalRatingBar.setRating(Integer.parseInt(foodItem.get("rating").toString()));

            if(home.checkFoodIteminCart(foodItem)) {
                holder.radioButtonYmal.setChecked(true);
            }else {
                holder.radioButtonYmal.setChecked(false);
            }

            holder.ymalLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add or Remove  to/from Cart
                    if(home.checkFoodIteminCart(foodItem)) {
//                        Log.i("Removing ymal" , foodItem.toString());
                        holder.radioButtonYmal.setChecked(false);
                        home.removefromcart(foodItem);
                        cartFragment.notifyForUpdates();
                    }else {
                        HashMap foodItemFound =  home.findFoodItem(foodItem);
                        if(foodItemFound != null) {
//                            Log.i("Adding ymal" , foodItem.toString());
                            holder.radioButtonYmal.setChecked(true);
                            home.addtocart(foodItem);
                            cartFragment.notifyForUpdates();
                        }
                    }
                }
            });

            holder.radioButtonYmal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Called in Click from......" , "Radio Button");
                    holder.ymalLinearLayout.callOnClick();
                }
            });

        }



    }

    @Override
    public int getItemCount() {
        return Home.ymalArr.size();
    }

    public class MyViewHolderYmalCart extends RecyclerView.ViewHolder {

        ImageView imageViewYmal;
        RatingBar ymalRatingBar;
        TextView textViewYmalFoodItemName;
        RadioButton radioButtonYmal;
        ProgressBar progressBarYMAL;
        LinearLayout ymalLinearLayout;

        public MyViewHolderYmalCart(View itemView) {
            super(itemView);

            imageViewYmal = view.findViewById(R.id.imageViewYmal);
            ymalRatingBar = view.findViewById(R.id.ymalRatingBar);
            textViewYmalFoodItemName = view.findViewById(R.id.textViewYmalFoodItemName);
            progressBarYMAL = view.findViewById(R.id.progressBarYMAL);
            radioButtonYmal = view.findViewById(R.id.radioButtonYmal);
            ymalLinearLayout = view.findViewById(R.id.ymalLinearLayout);
        }
    }
}
