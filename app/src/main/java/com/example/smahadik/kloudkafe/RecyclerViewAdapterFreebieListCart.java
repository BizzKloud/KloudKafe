package com.example.smahadik.kloudkafe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.HashMap;

public class RecyclerViewAdapterFreebieListCart extends RecyclerView.Adapter<RecyclerViewAdapterFreebieListCart.MyViewHolderFreebieCart>{

    Context context;
    LayoutInflater layoutInflater;
    View view;
    StorageReference storageRefCartFreebie;


    public RecyclerViewAdapterFreebieListCart (Context context) { this.context = context; }

    @NonNull
    @Override
    public MyViewHolderFreebieCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.cart_freebie_cardview, parent, false);
        return new MyViewHolderFreebieCart(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderFreebieCart holder, int position) {

        final HashMap freebieCart = Home.freebieArr.get(position);
        storageRefCartFreebie = Home.storageRef.child(freebieCart.get("pic").toString());

        Glide.with(holder.imageViewfreebieCart.getContext())
                .using(new FirebaseImageLoader())
                .load(storageRefCartFreebie)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBarFreebieCart.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageViewfreebieCart);
        holder.textViewfreebieNameCart.setText(freebieCart.get("name").toString());
    }

    @Override
    public int getItemCount() {
        return Home.freebieArr.size();
    }

    public class MyViewHolderFreebieCart extends RecyclerView.ViewHolder{

        ProgressBar progressBarFreebieCart;
        ImageView imageViewfreebieCart;
        TextView textViewfreebieNameCart;

        public MyViewHolderFreebieCart(View itemView) {
            super(itemView);

            progressBarFreebieCart = view.findViewById(R.id.progressBarFreebieCart);
            imageViewfreebieCart = view.findViewById(R.id.imageViewfreebieCart);
            textViewfreebieNameCart = view.findViewById(R.id.textViewfreebieNameCart);

        }
    }
}
