package com.example.smahadik.kloudkafe;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
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
    public static Dialog imageEnlargeDialog;



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
                    Home.progressDialog.setMessage("Removing Food Item from your Cart");
                    holder.addtocartMenu.setImageResource(R.drawable.addtocart_blueicon);
                    home.removefromcart(foodItemHashMap);
                }else {
                    Home.progressDialog.setMessage("Adding Food Item to your Cart");
                    holder.addtocartMenu.setImageResource(R.drawable.addedtocart_orangeicon);
                    home.addtocart(foodItemHashMap);
                }

                Home.progressDialog.show();
                VendorItemListFragment.drawerLayout.openDrawer(Gravity.END);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) { }
                        @Override
                        public void onFinish() {
                           Home.progressDialog.dismiss();
                    }
                }.start();
            }
        });

        holder.imageViewFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.home.setAdvCounter();
                showEnlargeImage(foodItemHashMap);
            }
        });

    }


    private void showEnlargeImage(HashMap foodItemHashMap) {
        imageEnlargeDialog = new Dialog(context);
        imageEnlargeDialog.setContentView(R.layout.image_enlarge_cardview);
        imageEnlargeDialog.setCanceledOnTouchOutside(false);

        ImageView imageViewEnlargeImage = imageEnlargeDialog.findViewById(R.id.imageViewEnlargeImage);
        TextView textViewEnlargeImage = imageEnlargeDialog.findViewById(R.id.textViewEnlargeImage);
        ImageButton closeEnlargeImageButton = imageEnlargeDialog.findViewById(R.id.closeEnlargeImageButton);
        final ProgressBar progressBarEnlargeImage = imageEnlargeDialog.findViewById(R.id.progressBarEnlargeImage);

        fooditemStorageRef = Home.storageRef.child(foodItemHashMap.get("pic").toString());
        Glide.with(imageViewEnlargeImage.getContext())
                .using(new FirebaseImageLoader())
                .load(fooditemStorageRef)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) { return false; }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBarEnlargeImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageViewEnlargeImage);
        textViewEnlargeImage.setText(foodItemHashMap.get("name").toString());

        closeEnlargeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageEnlargeDialog.dismiss();
            }
        });

        imageEnlargeDialog.show();
    }

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
